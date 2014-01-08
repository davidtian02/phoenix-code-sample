public class Level2Boss extends Boss {
	static final int NUM_BODY_PIECES = 9;
	
	MinionView viewHead;
	MinionView viewBody;
	SpriteBatcher batcher;
	MinionRedCoreType2 head;
	List<MinionRedCoreType1> body;
	
	float angle;
	final float tempAngleOffset = 60;
	final float angleOfSeparationBetweenPieces;
	final float distanceToHeadPiece = 200f;
	
	public boolean attack1Active, attack2Active, attack3Active;
	enum ATTACK_1_STATE {DASHING_IN, SPINNING, PAUSE_TO_SHOOT, PAUSE_TO_SHOOT_GRACE_PERIOD, DASHING_OUT, GRACE_PERIOD}
	ATTACK_1_STATE attack1State;
	enum ATTACK_2_STATE { DASHING_IN, PAUSE_BEFORE_DISPERSING, DISPERSING, DASHING_OUT, GRACE_PERIOD}
	ATTACK_2_STATE attack2State;
	enum ATTACK_3_STATE { DASHING_THROUGH, GRACE_PERIOD}
	ATTACK_3_STATE attack3State;
	float dashVelocity = 6f * Utils.FPS;
	float enragedDashVelocity = 8*Utils.FPS;
	float DASH_VELOCITY_CAP = 14*Utils.FPS;
	float bossRadius;
	float pauseTimer;
	float attack1FiringTimer;
	float attack1FiringTimerCD = 0.5f;
	float attack1ShootingTime;
	float attack1ShootingTimeCD = 1f;
	float attack1FiringInterval;
	float attack1FiringIntervalCD = 0.2f;
	float bufferPositionOrVelocityX[];
	float bufferPositionOrVelocityY[];
	
	public Level2Boss(float x, float y, OuyaGame game, OuyaScreen screen) {
		super(x, y, game, screen);
		viewHead = new MinionRedCoreType2View(game);
		viewBody = new MinionRedCoreType1View(game);
		
		batcher = new SpriteBatcher(game.getGLGraphics(), NUM_BODY_PIECES+1);
		
		head = new MinionRedCoreType2(x, y, game, screen);
		head.setPosition(x, y + Utils.WORLD_HEIGHT);
		body = new LinkedList<MinionRedCoreType1>();
		angleOfSeparationBetweenPieces = 360/(NUM_BODY_PIECES-1+4);
		angle = 0;
		bossRadius = MinionRedCoreType2View.HEIGHT/2 + distanceToHeadPiece + MinionRedCoreType1View.RADIUS/2;
		float tempAngle = angle + tempAngleOffset;
		float positionX, positionY;
		for(int i=0; i<NUM_BODY_PIECES; i++){
			positionX = midScreenX + FloatMath.cos(tempAngle*Vector2.TO_RADIANS)*distanceToHeadPiece;
			positionY = midScreenY + FloatMath.sin(tempAngle*Vector2.TO_RADIANS)*distanceToHeadPiece + Utils.WORLD_HEIGHT;
			body.add(new MinionRedCoreType1(positionX, positionY, game, screen));
			tempAngle += angleOfSeparationBetweenPieces;
		}
		damageableParts.add(head);
		
		attack1State = ATTACK_1_STATE.DASHING_IN;
		attack2State = ATTACK_2_STATE.DASHING_IN;
		attack3State = ATTACK_3_STATE.GRACE_PERIOD;
		bufferPositionOrVelocityX = new float[NUM_BODY_PIECES];
		bufferPositionOrVelocityY = new float[NUM_BODY_PIECES];
		
		collisionDamage = 7f; //this is per second
		dropExperience = 300;
		if(Utils.isSinglePlayer){
			currentHP = maxHP = 50000f;
		} else {
			currentHP = maxHP = 100000f;
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

	@Override
	public void update(float deltaTime) {
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
		Utils.didPlayerJustBeatEntireGame = true;
		
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
			head.setPosition(head.getPosition().x, head.getPosition().y - deltaTime*dashVelocity); 
			for(Minion m : body){ m.setPosition(m.getPosition().x, m.getPosition().y - deltaTime*dashVelocity); }
			angle = 0;
			if(head.getPosition().y <= midScreenY){
				attack1State = ATTACK_1_STATE.SPINNING;
			}
			break;
		case SPINNING:
			head.setPosition(midScreenX, midScreenY);
			float tempAngle = angle + tempAngleOffset;
			float positionX, positionY;
			for(int i=0; i<body.size(); i++){ 
				positionX = midScreenX + FloatMath.cos(tempAngle*Vector2.TO_RADIANS)*distanceToHeadPiece;
				positionY = midScreenY + FloatMath.sin(tempAngle*Vector2.TO_RADIANS)*distanceToHeadPiece;
				body.get(i).setPosition(positionX, positionY);
				tempAngle += angleOfSeparationBetweenPieces;
			}
			if(attack1FiringTimer >= attack1FiringTimerCD){
				attack1State = ATTACK_1_STATE.PAUSE_TO_SHOOT;
				attack1FiringTimer = 0;
			} else {
				angle += 1f * deltaTime*Utils.FPS;
				attack1FiringTimer += deltaTime;
			}
			if(angle >= 360){
				attack1State = ATTACK_1_STATE.DASHING_OUT;
				angle = 0;
			}
			break;
		case PAUSE_TO_SHOOT:
			if(attack1ShootingTime > attack1ShootingTimeCD){
				attack1ShootingTime = 0;
				attack1State = ATTACK_1_STATE.PAUSE_TO_SHOOT_GRACE_PERIOD;
			} else {
				if(attack1FiringInterval > attack1FiringIntervalCD){
					attack1FiringInterval = 0;
					//shoot
					MinionBullet mbLeftSide = new MinionBullet(getPosition().x-100, getPosition().y, 50);
					MinionBullet mbRightSide = new MinionBullet(getPosition().x+100, getPosition().y, 50);
					mbLeftSide.velocity.x = 10*Utils.FPS * FloatMath.cos(angle*Vector2.TO_RADIANS);
					mbLeftSide.velocity.y = 10*Utils.FPS * FloatMath.sin(angle*Vector2.TO_RADIANS);
					mbRightSide.velocity.x = mbLeftSide.velocity.x;
					mbRightSide.velocity.y = mbLeftSide.velocity.y;
					screen.minionBullets.add(mbLeftSide);
					screen.minionBullets.add(mbRightSide);
				} else {
					attack1FiringInterval += deltaTime;
				}
				attack1ShootingTime += deltaTime;
			}
			break;
		case PAUSE_TO_SHOOT_GRACE_PERIOD:
			if(pauseTimer >= 1f){
				attack1State = ATTACK_1_STATE.SPINNING;
				pauseTimer = 0;
			} else {
				pauseTimer += deltaTime;
			}
			break;
		case DASHING_OUT:
			head.setPosition(head.getPosition().x, head.getPosition().y + dashVelocity*deltaTime);
			boolean isOutOfScreen = true;
			for(Minion m : body){ 
				m.setPosition(m.getPosition().x, m.getPosition().y + dashVelocity*deltaTime);
				if(m.getPosition().y - MinionRedCoreType1View.RADIUS*2 < midScreenY + Utils.WORLD_HEIGHT/2){
					isOutOfScreen = false;
				}
			}
			if(isOutOfScreen){
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
	
	private void resetPosition(){
		head.setPosition(midScreenX, midScreenY + Utils.WORLD_HEIGHT);
		angle = 0;
		setBodyByHead();
	}
	private void setBodyByHead(){
		float tempAngle = angle + tempAngleOffset;
		float positionX=0, positionY=0;
		for(int i=0; i<body.size(); i++){
			positionX = head.getPosition().x + FloatMath.cos(tempAngle*Vector2.TO_RADIANS)*distanceToHeadPiece;
			positionY = head.getPosition().y + FloatMath.sin(tempAngle*Vector2.TO_RADIANS)*distanceToHeadPiece;
			body.get(i).setPosition(positionX, positionY);
			tempAngle += angleOfSeparationBetweenPieces;
		}
	}
	
	private void useAttack2(float deltaTime){
		switch(attack2State){
		case DASHING_IN:
			head.setPosition(head.getPosition().x, head.getPosition().y - deltaTime*dashVelocity); 
			for(Minion m : body){ m.setPosition(
				m.getPosition().x, m.getPosition().y - deltaTime*dashVelocity); 
			}
			
			if(head.getPosition().y <= midScreenY){
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
			for(int i=0; i<bufferPositionOrVelocityX.length; i++){
				body.get(i).setPosition(body.get(i).getPosition().x + bufferPositionOrVelocityX[i]*deltaTime, body.get(i).getPosition().y + bufferPositionOrVelocityY[i]*deltaTime);
			}
			boolean isReadyToMoveOn = true;
			for(Minion m:body){
				if(!Utils.isOutOfBoundsCompletely((Circle)m.bounds, screen.camera.getBounds())){
					isReadyToMoveOn = false;
					break;
				}
			}

			if(isReadyToMoveOn){
				attack2State = ATTACK_2_STATE.DASHING_OUT;
			}
			break;
		case DASHING_OUT:
			head.setPosition(head.getPosition().x, head.getPosition().y + dashVelocity *deltaTime);
			if(head.getPosition().y - MinionRedCoreType2View.HEIGHT > midScreenY + Utils.WORLD_HEIGHT/2 ){
				resetPosition();
				attack2State = ATTACK_2_STATE.GRACE_PERIOD;
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
	
	private void prepareEnragedDashIn(){
		int sign = Utils.getRandomInt(2) == 1 ? -1 : 1;
		float randomYValue = (midScreenY-Utils.WORLD_HEIGHT/2) + Utils.getRandomFloat() * (Utils.WORLD_HEIGHT-(bossRadius*2)) + bossRadius;
		enragedDashVelocity = sign*Math.abs(enragedDashVelocity*1.1f);
		if(sign == -1){ // go from right to left
			if(enragedDashVelocity < -DASH_VELOCITY_CAP){
				enragedDashVelocity = -DASH_VELOCITY_CAP;
			}
			head.setPosition(Utils.WORLD_WIDTH + midScreenX, randomYValue);
			angle = 180;
			setBodyByHead();
		} else {
			if(enragedDashVelocity > DASH_VELOCITY_CAP){
				enragedDashVelocity = DASH_VELOCITY_CAP;
			}
			head.setPosition( -midScreenX, randomYValue);
			angle = 0;
			setBodyByHead();
		}
	}
	
	private void useAttack3(float deltaTime){
		switch(attack3State){
		case DASHING_THROUGH:
			head.setPosition(head.getPosition().x + deltaTime*enragedDashVelocity, head.getPosition().y); 
			setBodyByHead();
			boolean isReadyToMoveOn = false;
			
			if(enragedDashVelocity < 0){
				if(head.getPosition().x < -bossRadius){
					isReadyToMoveOn = true;
				}
			} else {
				if(head.getPosition().x > Utils.WORLD_WIDTH+bossRadius){
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
				prepareEnragedDashIn();
			} else {
				pauseTimer += deltaTime;
			}
			break;
		}
	}
	
	@Override
	public void present(float deltaTime) {
		if(!isDead){
			batcher.beginBatch(viewHead.getTexture());
			batcher.drawSprite(head.getPosition().x, head.getPosition().y, MinionRedCoreType2View.WIDTH, MinionRedCoreType2View.HEIGHT, viewHead.getAnimation().getKeyFrame(head.animationTime, Animation.ANIMATION_LOOPING));
			batcher.endBatch();
			
			batcher.beginBatch(viewBody.getTexture());
			for(Minion m : body){
				batcher.drawSprite(m.getPosition().x, m.getPosition().y, MinionRedCoreType1View.RADIUS*2, MinionRedCoreType1View.RADIUS*2, viewBody.getAnimation().getKeyFrame(m.animationTime, Animation.ANIMATION_LOOPING));
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
	public List<? extends Minion> getNonDamageableParts() {
		return body;
	}

}
