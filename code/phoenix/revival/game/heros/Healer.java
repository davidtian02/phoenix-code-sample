public class Healer extends Hero {
	HealingBubble healingBubble;
	HolyBall holyBall;
	TimeBubble timeBubble;
	
	//view stuff
	SpriteBatcher healingBubbleBatcher;
	HealingBubbleView healingBubbleView;
	SpriteBatcher holyBallBatcher;
	HolyBallView holyBallView;
	SpriteBatcher timeBubbleBatcher;
	TimeBubbleView timeBubbleView;
	
	//sounds
	Sound healingBubbleSound;
	Sound holyBallSound;
	Sound timeBubbleSound;
	Sound instaGuardSound;
	
	public Healer(float x, float y, float radius, OuyaGame game, OuyaScreen screen, int playerNumber){
		super(x, y, radius, game, screen, playerNumber);
		view = new HealerView(game, playerNumber);
		
		//base stats
		baseHp = 320;
		baseMp = 260;
		baseHpRegen = 1.2f;
		baseMpRegen = 2.5f;
		baseAttackDmg = 18f;
		baseAttackSpeed = 4.1f;
		baseMovementSpeed = 12f;
		
		//healing bubble
		spell1MpCost = 80;
		spell1CdTimer = spell1Cd = 15f;
		spell1DurationTimer = spell1Duration = 3f;
		//holy ball		
		spell2MpCost = 80;
		spell2CdTimer = spell2Cd = 15f;
		//instant heal
		spell3MpCost = 80;
		spell3CdTimer = spell3Cd = 15f;
		//time slow		
		spell4MpCost = 240;
		spell4CdTimer = spell4Cd = 30f;
		spell4DurationTimer = spell4Duration = 10f; 

		//view
		healingBubbleBatcher = new SpriteBatcher(game.getGLGraphics(), 1);
		healingBubbleView = new HealingBubbleView(game);
		holyBallBatcher = new SpriteBatcher(game.getGLGraphics(), 1);
		holyBallView = new HolyBallView(game);
		timeBubbleBatcher = new SpriteBatcher(game.getGLGraphics(), 1);
		timeBubbleView = new TimeBubbleView(game);

		//sounds
		healingBubbleSound = game.getAudio().newSound("sound/healer/healing_bubble.ogg");
		holyBallSound = game.getAudio().newSound("sound/healer/holy_bolt.ogg");
		instaGuardSound = game.getAudio().newSound("sound/healer/insta_guard.ogg");
		timeBubbleSound = game.getAudio().newSound("sound/healer/time_bubble.ogg");
	}

	@Override
	protected void updateBuffsAndAuras(float deltaTime){
		if(timeBubble != null && Utils.isWithinBoundsCompletely((Circle)this.bounds, (Circle)timeBubble.bounds)){
			movementSpeed = TimeBubble.MOVEMENT_SPEED_BOOST * getMovementSpeedByBase();
			attackSpeed = TimeBubble.ATTACK_SPEED_BOOST * getAttackSpeedByBase();
		} else {
			movementSpeed = getMovementSpeedByBase();
			attackSpeed = getAttackSpeedByBase();
		}
		super.updateBuffsAndAuras(deltaTime);
	}
	
	@Override
	protected void updateSpellEffectsAndTimer(float deltaTime) {
		if(spell1DurationTimer < spell1Duration){
			spell1DurationTimer += deltaTime;
			healingBubble.update(deltaTime, getPosition().x, getPosition().y);
		} else {
			healingBubble = null;
		}
		
		if(holyBall != null){
			if(Utils.isOutOfBoundsCompletely( (Circle)holyBall.bounds, screen.camera.getBounds()) ){
				holyBall = null;
			} else {
				holyBall.update(deltaTime);
			}
		}
		
		if(spell4DurationTimer < spell4Duration){
			spell4DurationTimer += deltaTime;
			timeBubble.update(deltaTime);
		} else {
			timeBubble = null;
		}
		super.updateSpellEffectsAndTimer(deltaTime);
	}

	@Override
	protected void checkSpellsWithEnemiesAndAllies(float deltaTime){
		//on enemies:
		Iterator<List<Minion>> iter = screen.enemies.iterator();
		List<Minion> minionsList;
		Iterator<Minion> iterM;
		Minion m;
		while(iter.hasNext()){
			 minionsList = iter.next();
			 iterM = minionsList.iterator();
			 while(iterM.hasNext()){
				 m = iterM.next();
				 if(holyBall != null){
					 if(holyBall.isColliding(m)){
						 if(m.takeDamageAndCheckDies(HolyBall.damage)){
							 screen.explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, view.game));
							 gainExp(m.dropExperience);
							 iterM.remove();
							 continue;
						 }
					 }
				 }
				 
				 if(timeBubble != null){
					 if(timeBubble.isColliding(m)){
						 m.velocity.x *= TimeBubble.SLOW_RATE;
						 m.velocity.y *= TimeBubble.SLOW_RATE;
						 if(m.takeDamageAndCheckDies(TimeBubble.damage)){
							 screen.explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, view.game));
							 gainExp(m.dropExperience);
							 iterM.remove();
							 continue;
						 }
					 }
				 }
			 }
		}
		
		
		Iterator<MinionBullet> iterMB = screen.minionBullets.iterator();
		MinionBullet mb;
		while(iterMB.hasNext()){
			mb = iterMB.next();
			if(holyBall != null){
				if(holyBall.isColliding(mb)){
					iterMB.remove();
					continue;
				}
			}
			
			if(timeBubble!=null){
				if(timeBubble.isColliding(mb)){
					mb.velocity.x *= TimeBubble.SLOW_RATE_BULLET;
					mb.velocity.y *= TimeBubble.SLOW_RATE_BULLET;
					if(mb.velocity.dist(new Vector2()) < 140f){
						iterMB.remove();
						continue;
					}
				}
			}
		}
		
		//on allies:
		for(Hero h : screen.herosList){
			if(healingBubble != null){
				if(healingBubble.isColliding(h)){
					currentHP = currentHP + HealingBubble.HP_HEAL_RATE > maxHp ? maxHp : currentHP + HealingBubble.HP_HEAL_RATE;
					h.currentHP = h.currentHP + HealingBubble.HP_HEAL_RATE > h.maxHp ? h.maxHp : h.currentHP + HealingBubble.HP_HEAL_RATE;
				}
			}
			
			if(holyBall != null){
				if(holyBall.isColliding(h)){
					h.currentHP = h.currentHP + HolyBall.HP_HEAL_RATE > h.maxHp ? h.maxHp : h.currentHP + HolyBall.HP_HEAL_RATE;
				}
			}
			
			if(timeBubble != null){
				//do anything for allies as well?
			}
		}
	}

	@Override
	protected void checkSpellsWithBoss(float deltaTime) {
		super.checkSpellsWithBoss(deltaTime);
		
		for(Minion m : screen.boss.getDamageableParts()){
			if(holyBall != null){
				if(holyBall.isColliding(m)){
					 screen.boss.takeDamageAndCheckDies(HolyBall.bossDamage);
				}
			}
			
			if(timeBubble != null){
				 if(timeBubble.isColliding(m)){
					 screen.boss.takeDamageAndCheckDies(TimeBubble.damage);
				 }
			 }
		}
	}
	
	
	@Override
	protected void updatePositionWithParallaxBackground(float deltaTime){
		super.updatePositionWithParallaxBackground(deltaTime);
		if(healingBubble!=null){
			healingBubble.setPosition(healingBubble.getPosition().x, healingBubble.getPosition().y + screen.PARALLAX_SPEED*deltaTime);
		}
		
		if(holyBall!=null){
			holyBall.setPosition(holyBall.getPosition().x, holyBall.getPosition().y + screen.PARALLAX_SPEED*deltaTime);
		}
		
		if(timeBubble!=null){
			timeBubble.setPosition(timeBubble.getPosition().x, timeBubble.getPosition().y + screen.PARALLAX_SPEED*deltaTime);
		}
	}
	
	@Override //healing bubble
	protected void castSpell1() {
		//play sound here too
		healingBubble = new HealingBubble(getPosition().x, getPosition().y, HealingBubbleView.RADIUS*2, game);
		healingBubbleSound.play(GameData.soundVolume);
	}

	@Override //holy ball
	protected void castSpell2() {
		//play sound here
		holyBall = new HolyBall(getPosition().x, getPosition().y, HolyBallView.RADIUS, angle, game);
		holyBallSound.play(GameData.soundVolume);
	}

	@Override //insta guard
	protected void castSpell3() {
		for(Hero h : screen.herosList){
			h.instaGuard = new InstaGuard(h.getPosition().x, h.getPosition().y, InstaGuard.RADIUS, game);
		}
		instaGuardSound.play(GameData.soundVolume);
	}

	@Override //time bubble
	protected void castSpell4() {
		timeBubble = new TimeBubble(getPosition().x, getPosition().y, TimeBubbleView.RADIUS*(TimeBubbleView.VIEW_MAGNIFIER_FACTOR/2), game);
		timeBubbleSound.play(GameData.soundVolume/2);
	}
	
	@Override
	public void present(float deltaTime) {
	    if(healingBubble != null){
	    	healingBubble.present(deltaTime);
	    }
	    
	    if(holyBall != null){
	    	holyBall.present(deltaTime);
	    }
		
	    if(timeBubble != null){
	    	timeBubble.present(deltaTime);
	    }
	    
		super.present(deltaTime);
	}

	// =========================  Spells  ============================
	
	private class HealingBubble extends DynamicGameObject{
		static final float HP_HEAL_RATE = 1f;
		float animationTime;
		public HealingBubble(float x, float y, float radius, OuyaGame game) {
			super(x, y, radius);
			healingBubbleView = new HealingBubbleView(game);
			healingBubbleBatcher = new SpriteBatcher(game.getGLGraphics(), 1);
			animationTime = 0;
		}
		protected void update(float deltaTime, float x, float y){
			animationTime += deltaTime;
			setPosition(x,y);
		}
		protected void present(float deltaTime){
			healingBubbleBatcher.beginBatch(healingBubbleView.texture);
			healingBubbleBatcher.drawSprite(getPosition().x, getPosition().y, HealingBubbleView.RADIUS*2 * HealingBubbleView.VIEW_MAGNIFIER_FACTOR, HealingBubbleView.RADIUS*2 * HealingBubbleView.VIEW_MAGNIFIER_FACTOR, healingBubbleView.animation.getKeyFrame(animationTime, Animation.ANIMATION_LOOPING));
			healingBubbleBatcher.endBatch();
		}
	}
	
	private class HealingBubbleView extends HeroSpellView{
		static final float RADIUS = 64;
		static final float VIEW_MAGNIFIER_FACTOR = 2;
		public HealingBubbleView(OuyaGame game){
			super(game, "heros/healer/healer_spell_1_healing_bubble.png", 23, 4, 128, 128, 0.1f);
		}
	}
	
	private class HolyBall extends DynamicGameObject{
		static final float HP_HEAL_RATE = 2f;
		static final float bossDamage = 4f;
		static final float damage = 10f;
		float animationTime;
		public HolyBall(float x, float y, float radius, float angle, OuyaGame game) {
			super(x, y, radius);
			holyBallView = new HolyBallView(game);
			holyBallBatcher = new SpriteBatcher(game.getGLGraphics(), 1);
			animationTime = 0;
			float radians = angle * Vector2.TO_RADIANS;
			velocity.set(-FloatMath.sin(radians) * 10, FloatMath.cos(radians) * 10);
		}

		public void update(float deltaTime) {
			setPosition(getPosition().x + velocity.x*deltaTime*Utils.FPS, getPosition().y + velocity.y*deltaTime*Utils.FPS);
			animationTime += deltaTime;
		}
		
		public void present(float deltaTime){
			holyBallBatcher.beginBatch(holyBallView.texture);
			holyBallBatcher.drawSprite(getPosition().x, getPosition().y, HolyBallView.RADIUS*2, HolyBallView.RADIUS*2, holyBallView.animation.getKeyFrame(animationTime, Animation.ANIMATION_LOOPING));
			holyBallBatcher.endBatch();
		}
	}
	private class HolyBallView extends HeroSpellView{
		static final float RADIUS = 128;
		public HolyBallView(OuyaGame game) {
			super(game, "heros/healer/healer_spell_2_holy_ball.png", 6, 4, 256, 256, 0.1f);
		}
	}
	
	private class TimeBubble extends DynamicGameObject{
		static final float SLOW_RATE_BULLET = 0.95f;
		static final float MOVEMENT_SPEED_BOOST = 2f;
		static final float ATTACK_SPEED_BOOST = 2f;
		static final float SLOW_RATE = 0.98f;
		static final float damage = 1.5f;
		float animationTime;
		public TimeBubble(float x, float y, float radius, OuyaGame game) {
			super(x, y, radius);
			animationTime = 0;
		}
		
		public void update(float deltaTime){
			animationTime += deltaTime;
		}
		
		public void present(float deltaTime){
			timeBubbleBatcher.beginBatch(timeBubbleView.texture);
			timeBubbleBatcher.drawSprite(getPosition().x, getPosition().y, TimeBubbleView.RADIUS*2 * TimeBubbleView.VIEW_MAGNIFIER_FACTOR, TimeBubbleView.RADIUS*2 * TimeBubbleView.VIEW_MAGNIFIER_FACTOR, timeBubbleView.animation.getKeyFrame(animationTime, Animation.ANIMATION_LOOPING));
			timeBubbleBatcher.endBatch();
		}
	}
	
	private class TimeBubbleView extends HeroSpellView{
		static final float RADIUS = 256;
		static final int VIEW_MAGNIFIER_FACTOR = 4;
		public TimeBubbleView(OuyaGame game){
			super(game, "heros/healer/healer_spell_4_time_bubble.png", 8, 4, ((int)RADIUS)*2, ((int)RADIUS)*2, 0.1f);
		}
	}
	
	// =========================  View  ============================
	
	public class HealerView extends HeroView {
		public HealerView(OuyaGame game, int playerNumber) {
			super(game, playerNumber);
			textureShip = new Texture(game, "heros/healer/healer.png");
			regionShip = new TextureRegion(textureShip, 0, 0, HeroView.RADIUS*2, HeroView.RADIUS*2);
		}
	}

}
