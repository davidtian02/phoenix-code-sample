public class Level1Boss extends Boss {
	static final int NUM_PIECES = 10;
	MinionView viewHead;
	MinionView viewBody;
	SpriteBatcher batcher;
	MinionRedCoreType1 head;
	List<MinionKamikaze> body;
	
	public boolean attack1Active, attack2Active, attack3Active;
	enum ATTACK_1_STATE {DASHING_IN, SPINNING, DASHING_OUT, GRACE_PERIOD}
	ATTACK_1_STATE attack1State;
	enum ATTACK_2_STATE { DASHING_IN, PAUSE_BEFORE_DISPERSING, DISPERSING, GRACE_PERIOD}
	ATTACK_2_STATE attack2State;
	enum ATTACK_3_STATE { DASHING_THROUGH, GRACE_PERIOD}
	ATTACK_3_STATE attack3State;
	float pauseTimer;
	float angle;
	float length;
	float dashVelocity = 10f * Utils.FPS;
	float DASH_VELOCITY_CAP = 25*Utils.FPS;
	float bufferPositionOrVelocityX[];
	float bufferPositionOrVelocityY[];
	Rectangle cameraBounds;
	
	public Level1Boss(float x, float y, OuyaGame game, OuyaScreen screen) {
		super(x, y, game, screen);
		viewHead = new MinionRedCoreType1View(game);
		viewBody = new MinionKamikaze.MinionKamikazeView(game);
		batcher = new SpriteBatcher(game.getGLGraphics(), NUM_PIECES);
		head = new MinionRedCoreType1(x, y, game, screen);
		head.setPosition(-Utils.WORLD_WIDTH, y);
		body = new LinkedList<MinionKamikaze>();
		for(int i=1; i<NUM_PIECES; i++){
			body.add(new MinionKamikaze(head.getPosition().x+i*MinionKamikazeView.RADIUS*2, y, game, screen));
		}
		damageableParts.add(head);
		
		collisionDamage = 5f; //this is per second
		dropExperience = 100;
		if(Utils.isSinglePlayer){
			currentHP = maxHP = 20000f;
		} else {
			currentHP = maxHP = 40000f;
		}
		
		length = 10*MinionRedCoreType1View.RADIUS*2;
		attack1State = ATTACK_1_STATE.DASHING_IN;
		attack2State = ATTACK_2_STATE.DASHING_IN;
		attack3State = ATTACK_3_STATE.GRACE_PERIOD;
		bufferPositionOrVelocityX = new float[NUM_PIECES];
		bufferPositionOrVelocityY = new float[NUM_PIECES];
		cameraBounds = new Rectangle(midScreenX, midScreenY, Utils.WORLD_WIDTH, Utils.WORLD_HEIGHT);
	}
	
	@Override
	public void update(float deltaTime){
		if(isDead && ! hasShownDeadAnimation){
			justDied(deltaTime);
		}
		
		if(!isDead){
			head.updateAnimationOnly(deltaTime);
			for(Minion m : body){
				m.updateAnimationOnly(deltaTime);
			}
			
			if(attack1Active){ // move to center and rotate slowly
				useAttack1(deltaTime);
			}
			
			if(attack2Active){ // move into left side & disperse parts randomly
				useAttack2(deltaTime);
			}
			
			if(shouldSwitchToFinalAttack && !attack1Active && !attack2Active){
				attack3Active = true;
			}
			
			if(attack3Active){ // dashing around on screen, vertically and horizontally 
				useAttack3(deltaTime);
			}
			
			
		} else {
			updateDieing(deltaTime);
		}
		
		if(getHpPercentage() < BOSS_ENRAGE_HP_PERCENTAGE){
			isEnraged = true;
		}
	}
	
	@Override
	protected void justDied(float deltaTime){
		explosions.add(new ShipExplosion(head.getPosition().x, head.getPosition().y, ShipExplosionView.RADIUS, game));
		for(Minion m : body){
			explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, game));
		}
		hasShownDeadAnimation = true;
		
		head = null;
		body = null;
	}
	
	@Override
	protected void updateDieing(float deltaTime){
		Iterator<ShipExplosion> iter = explosions.iterator();
		ShipExplosion se;
		while(iter.hasNext()){
			se = iter.next();
			if(se.isDoneExploding){
				iter.remove();
			} else {
				se.update(deltaTime);
			}
		}
	}
	
	private void useAttack1(float deltaTime){
		switch(attack1State){
		case DASHING_IN:
			head.setPosition(head.getPosition().x + deltaTime*dashVelocity, head.getPosition().y); 
			for(Minion m : body){ m.setPosition(m.getPosition().x + deltaTime*dashVelocity, m.getPosition().y); }
			angle = 0;
			if(head.getPosition().x >= midScreenX){
				attack1State = ATTACK_1_STATE.SPINNING;
			}
			break;
		case SPINNING:
			head.setPosition(midScreenX, midScreenY);
			for(int i=0; i<body.size(); i++){ 
				//FIXME these aren't using the Utils.FPS * deltaTime, cus it wiggles for some reason
				body.get(i).setPosition( midScreenX + FloatMath.cos(angle*Vector2.TO_RADIANS)*(i+1)*MinionKamikazeView.RADIUS*2, midScreenY + FloatMath.sin(angle*Vector2.TO_RADIANS)*(i+1)*MinionKamikazeView.RADIUS*2);
			}
			angle += 1.5f *deltaTime*Utils.FPS;
			if(angle >= 1080){//3 spins
				attack1State = ATTACK_1_STATE.DASHING_OUT;
				angle = 0;
			}
			break;
		case DASHING_OUT:
			head.setPosition(head.getPosition().x - dashVelocity*deltaTime, head.getPosition().y); //NOTE: assuming parallax stops scroling at this point
			for(Minion m : body){ m.setPosition(m.getPosition().x - dashVelocity *deltaTime, m.getPosition().y); }
			if(body.get(body.size()-1).getPosition().x + MinionKamikazeView.RADIUS*2 < 0){
				pauseTimer = 0;
				attack1State = ATTACK_1_STATE.GRACE_PERIOD;
			}
			break;
		case GRACE_PERIOD:
			if(pauseTimer >= 2f){
				attack1State = ATTACK_1_STATE.DASHING_IN;
				attack1Active = false;
				pauseTimer = 0;
			} else {
				pauseTimer += deltaTime;
			}
			break;
		}
	}
	
	private void useAttack2(float deltaTime){
		switch(attack2State){
		case DASHING_IN:
			head.setPosition(head.getPosition().x + deltaTime*dashVelocity, head.getPosition().y); 
			for(Minion m : body){ m.setPosition(m.getPosition().x + deltaTime*dashVelocity, m.getPosition().y); }
			if(head.getPosition().x >= (Utils.WORLD_WIDTH-length)/2 + MinionRedCoreType1View.RADIUS){
				attack2State = ATTACK_2_STATE.PAUSE_BEFORE_DISPERSING;
				pauseTimer = 0;
				int sign;
				for(int i=0; i<bufferPositionOrVelocityX.length;i++){
					sign = (Utils.getRandomInt(2) == 1) ? -1 : 1;
					bufferPositionOrVelocityX[i] = sign*(Utils.getRandomFloat()*dashVelocity + dashVelocity/2);
					sign = (Utils.getRandomInt(2) == 1) ? -1 : 1;
					bufferPositionOrVelocityY[i] = sign*(Utils.getRandomFloat()*dashVelocity + dashVelocity/2);
				}
			}
			break;
		case PAUSE_BEFORE_DISPERSING:
			if(pauseTimer >= 2f){
				attack2State = ATTACK_2_STATE.DISPERSING;
				pauseTimer = 0;
			} else {
				pauseTimer += deltaTime;
			}
			break;
		case DISPERSING:
			head.setPosition(head.getPosition().x + bufferPositionOrVelocityX[0]*deltaTime, head.getPosition().y + bufferPositionOrVelocityY[0]*deltaTime);
			for(int i=0; i<bufferPositionOrVelocityX.length-1; i++){
				body.get(i).setPosition(body.get(i).getPosition().x + bufferPositionOrVelocityX[i+1]*deltaTime, body.get(i).getPosition().y + bufferPositionOrVelocityY[i+1]*deltaTime);
			}
			boolean isReadyToMoveOn = true;
			if(!Utils.isOutOfBoundsCompletely((Circle)head.bounds, cameraBounds)){
				isReadyToMoveOn = false;
			} else {
				for(Minion m:body){
					if(!Utils.isOutOfBoundsCompletely((Circle)m.bounds, cameraBounds)){
						isReadyToMoveOn = false;
						break;
					}
				}
			}
			if(isReadyToMoveOn){
				attack2State = ATTACK_2_STATE.GRACE_PERIOD;
				head.setPosition(-Utils.WORLD_WIDTH, midScreenY);
				for(int i=0; i<body.size(); i++){
					body.get(i).setPosition(head.getPosition().x+(i+1)*MinionRedCoreType1View.RADIUS*2, midScreenY);
				}
			}
			break;
		case GRACE_PERIOD:
			if(pauseTimer >= 2f){
				attack2State = ATTACK_2_STATE.DASHING_IN;
				attack2Active = false;
				pauseTimer = 0;
			} else {
				pauseTimer += deltaTime;
			}
			break;
		}
	}
	
	private void useAttack3(float deltaTime){
		switch(attack3State){
		case DASHING_THROUGH:
			head.setPosition(head.getPosition().x + deltaTime*dashVelocity, head.getPosition().y); 
			for(Minion m : body){ m.setPosition(m.getPosition().x + deltaTime*dashVelocity, m.getPosition().y); }
			boolean isReadyToMoveOn = false;
			
			if(dashVelocity < 0){
				if(body.get(body.size()-1).getPosition().x < -MinionKamikazeView.RADIUS*2){
					isReadyToMoveOn = true;
				}
			} else {
				if(body.get(body.size()-1).getPosition().x > Utils.WORLD_WIDTH+MinionKamikazeView.RADIUS*2){
					isReadyToMoveOn = true;
				}
			}
			if(isReadyToMoveOn){
				attack3State = ATTACK_3_STATE.GRACE_PERIOD;
				pauseTimer = 0;						
			}
			break;
		case GRACE_PERIOD:
			if(pauseTimer >= 1f){
				attack3State = ATTACK_3_STATE.DASHING_THROUGH;
				pauseTimer = 0;
				int sign = Utils.getRandomInt(2) == 1 ? -1 : 1;
				float randomYValue = (midScreenY-Utils.WORLD_HEIGHT/2) + Utils.getRandomFloat() * (Utils.WORLD_HEIGHT-MinionRedCoreType1View.RADIUS*2) + MinionRedCoreType1View.RADIUS;
				dashVelocity = sign*Math.abs(dashVelocity*1.1f);
				if(sign == -1){ // go from right to left
					if(dashVelocity < -DASH_VELOCITY_CAP){
						dashVelocity = -DASH_VELOCITY_CAP;
					}
					head.setPosition(Utils.WORLD_WIDTH + MinionRedCoreType1View.RADIUS*2, randomYValue);
					for(int i=0; i<body.size(); i++){
						body.get(i).setPosition(head.getPosition().x+(i+1)*MinionRedCoreType1View.RADIUS*2, randomYValue);
					}
				} else {
					if(dashVelocity > DASH_VELOCITY_CAP){
						dashVelocity = DASH_VELOCITY_CAP;
					}
					head.setPosition( -MinionRedCoreType1View.RADIUS*2, randomYValue);
					for(int i=0; i<body.size(); i++){
						body.get(i).setPosition(head.getPosition().x-(i+1)*MinionRedCoreType1View.RADIUS*2, randomYValue);
					}
				}
			} else {
				pauseTimer += deltaTime;
			}
			break;
		}
	}
	
	@Override
	public void present(float deltaTime){
		if(!isDead){
			batcher.beginBatch(viewHead.getTexture());
			batcher.drawSprite(head.getPosition().x, head.getPosition().y, MinionRedCoreType1View.RADIUS*2, MinionRedCoreType1View.RADIUS*2, viewHead.getAnimation().getKeyFrame(head.animationTime, Animation.ANIMATION_LOOPING));
			batcher.endBatch();
			
			batcher.beginBatch(viewBody.getTexture());
			for(Minion m : body){
				batcher.drawSprite(m.getPosition().x, m.getPosition().y, MinionKamikazeView.RADIUS*2, MinionKamikazeView.RADIUS*2, viewBody.getAnimation().getKeyFrame(m.animationTime, Animation.ANIMATION_LOOPING));
			}
			batcher.endBatch();
		} else {
			if(!explosions.isEmpty()){
				batcher.beginBatch(explosionView.texture);
				for(ShipExplosion se : explosions){
					batcher.drawSprite(se.getPosition().x, se.getPosition().y, ShipExplosionView.RADIUS*2, ShipExplosionView.RADIUS*2, explosionView.animation.getKeyFrame(se.explosionTime, Animation.ANIMATION_LOOPING));
				}
				batcher.endBatch();
			}
		}
	}

	@Override
	public boolean isColliding(DynamicGameObject other, float damage) {
		if(head==null) return false;
		if(body==null) return false; 
				
		if(head.isColliding(other)){
			takeDamageAndCheckDies(damage);
			return true;
		}
		for(Minion m : body){
			if(m.isColliding(other)){
				MusicAndSound.playSoundEnemyGettingHitNotDamaged();
				return true;
			}
		}
		return false;
	}
	
	public List<? extends Minion> getNonDamageableParts(){
		return body;
	}
}
