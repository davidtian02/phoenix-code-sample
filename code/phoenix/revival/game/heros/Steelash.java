public class Steelash extends Hero {

	PiercingRounds piercingRounds;
	Disable disable;
	WarpDrive warpDrive;
	LaserBarrage laserBarrage;
	
	PiercingRoundsView piercingRoundsView;
	SpriteBatcher piercingRoundsBatcher;
	DisableView disableView;
	SpriteBatcher disableBatcher;
	WarpDriveView warpDriveView;
	SpriteBatcher warpDriveBatcher;
	LaserBarrageView laserBarrageView;
	SpriteBatcher laserBarrageBatcher;
	
	Sound piercingRoundsSound;
	Sound disableSound;
	Sound warpDriveSound;
	Sound laserBarrageSound;
	
	public Steelash(float x, float y, float radius, OuyaGame game, OuyaScreen screen, int playerNumber) {
		super(x, y, radius, game, screen, playerNumber);
		view = new SteelashView(game, playerNumber);
		
		//base stats
		baseHp = 390;
		baseMp = 270;
		baseHpRegen = 2f;
		baseMpRegen = 2f;
		baseAttackDmg = 35f;
		baseAttackSpeed = 6f;
		baseMovementSpeed = 24f;

		//Piercing Rounds
		spell1MpCost = 80;
		spell1CdTimer = spell1Cd = 15f;
		//Disable
		spell2MpCost = 80;
		spell2CdTimer = spell2Cd = 15f;
		spell2DurationTimer = spell2Duration = 0.28f;
		//Warp Drive
		spell3MpCost = 80;
		spell3CdTimer = spell3Cd = 15f;
		spell3DurationTimer = spell3Duration = 5f;
		//Laser Barrage
		spell4MpCost = 240;
		spell4CdTimer = spell4Cd = 30f;
		spell4DurationTimer = spell4Duration = 12f;
		
		//view stuff
		piercingRoundsView = new PiercingRoundsView(game);
		piercingRoundsBatcher = new SpriteBatcher(game.getGLGraphics(), PiercingRounds.MAX_NUMBER_ROUNDS);
		disableView = new DisableView(game);
		disableBatcher = new SpriteBatcher(game.getGLGraphics(), 1);
		warpDriveView = new WarpDriveView(game);
		warpDriveBatcher = new SpriteBatcher(game.getGLGraphics(), WarpDrive.MAX_NUMBER_SHADOWS);
		laserBarrageView = new LaserBarrageView(game);
		laserBarrageBatcher = new SpriteBatcher(game.getGLGraphics(), LaserBarrage.MAX_NUMBER_LASERS);
		
		//sound stuff
		piercingRoundsSound = game.getAudio().newSound("sound/steelash/piercing_rounds.ogg");
		disableSound = game.getAudio().newSound("sound/steelash/disable.ogg");
		warpDriveSound = game.getAudio().newSound("sound/steelash/warp_drive.ogg");
		laserBarrageSound = game.getAudio().newSound("sound/steelash/laser_barrage.ogg");
	}

	@Override
	protected void updateSpellEffectsAndTimer(float deltaTime) {
		
		if(piercingRounds != null){
			if(piercingRounds.isAllOutOfScreen()){
				piercingRounds = null; 
			} else {
				piercingRounds.update(deltaTime);
			}
		}
		
		if(spell2DurationTimer < spell2Duration){
			spell2DurationTimer += deltaTime;
			disable.update(deltaTime);
		} else {
			disable = null;
		}
		
		if(spell3DurationTimer < spell3Duration){
			spell3DurationTimer += deltaTime;
			warpDrive.update(deltaTime, getPosition().x, getPosition().y);
		} else {
			warpDrive = null;
			attackSpeed = getAttackSpeedByBase();
			movementSpeed = getMovementSpeedByBase();
			attackDmg = getAttackDamageByBase();
		}
		
		if(spell4DurationTimer < spell4Duration){
			spell4DurationTimer += deltaTime;
			laserBarrage.update(deltaTime, getPosition().x, getPosition().y);
			laserBarrage.isAllOutOfScreen(); // clears it...
		} else {
			if(laserBarrage != null){
				if(laserBarrage.isAllOutOfScreen()){
					laserBarrage = null;
				} else {
					laserBarrage.update(deltaTime, getPosition().x, getPosition().y);
				}
			}
		}
		
		
		super.updateSpellEffectsAndTimer(deltaTime);
	}
	
	@Override
	protected void checkSpellsWithEnemiesAndAllies(float deltaTime) {
		Iterator<List<Minion>> iter = screen.enemies.iterator();
		List<Minion> minionsList;
		Iterator<Minion> iterM;
		Minion m;
		while(iter.hasNext()){
			minionsList = iter.next();
			iterM = minionsList.iterator();
			while(iterM.hasNext()){
				m = iterM.next();
				//checking against spells and what not
				if(piercingRounds != null){
					if(piercingRounds.isColliding(m)){
						if(m.takeDamageAndCheckDies(piercingRounds.damage)){
							screen.explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, view.game));
							gainExp(m.dropExperience);
							iterM.remove();
							continue;
						}	
					}
				}
				
				if(disable != null){
					if(screen.isWithinBounds(m)){
						m.isDisabled = true;
						if(m.takeDamageAndCheckDies(disable.damage )){
							screen.explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, view.game));
							gainExp(m.dropExperience);
							iterM.remove();
							continue;
						}
					}
				}
				
				if(warpDrive != null){
					if(warpDrive.isColliding(m)){
						if(m.takeDamageAndCheckDies(warpDrive.damage)){
							screen.explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, view.game));
							gainExp(m.dropExperience);
							iterM.remove();
							continue;
						}	
					}
				}
				
				if(laserBarrage != null){
					if(laserBarrage.isColliding(m)){
						if(m.takeDamageAndCheckDies(laserBarrage.damage)){
							screen.explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, view.game));
							gainExp(m.dropExperience);
							iterM.remove();
							continue;
						}						 
					}
				}
			}
		}
		
		//checking against bullets:
		Iterator<MinionBullet> iterMB = screen.minionBullets.iterator();
		MinionBullet mb;
		while(iterMB.hasNext()){
			mb = iterMB.next();
			
			if(piercingRounds != null){
				if(piercingRounds.isColliding(mb)){
					iterMB.remove();
					continue;	
				}
			}
			
			if(disable != null){
				if(screen.isWithinBounds(mb)){
					iterMB.remove();
					continue;
				}
			}
			
			if(warpDrive != null){
				if(warpDrive.isColliding(mb)){
					iterMB.remove();
					continue;
				}
			}
			
			if(laserBarrage!=null){
				if(laserBarrage.isColliding(mb)){
					iterMB.remove();
					continue;
				}
			}
		}
	}

	@Override
	protected void checkSpellsWithBoss(float deltaTime) {
		super.checkSpellsWithBoss(deltaTime);
		
		for(Minion m : screen.boss.getDamageableParts()){
			if(piercingRounds != null){
				if(piercingRounds.isColliding(m)){
					 screen.boss.takeDamageAndCheckDies(piercingRounds.bossDamage );
				}
			}
			
			if(disable != null){
				if(disable.isColliding(m)){
					screen.boss.takeDamageAndCheckDies(disable.damage);
				}
			}
			
			if(warpDrive != null){
				if(warpDrive.isColliding(m)){
					screen.boss.takeDamageAndCheckDies(warpDrive.bossDamage);
				}
			}
			
			if(laserBarrage != null){
				if(laserBarrage.isColliding(m)){
					screen.boss.takeDamageAndCheckDies(laserBarrage.bossDamage );
				}
			}
		}
		
		if(screen.boss.getNonDamageableParts() != null){
			for(Minion m : screen.boss.getNonDamageableParts()){
				if(laserBarrage != null){
					if(laserBarrage.isColliding(m)){
						MusicAndSound.playSoundEnemyGettingHitNotDamaged();
					}
				}
			}
		}
	}
	
	@Override
	protected void updatePositionWithParallaxBackground(float deltaTime){
		super.updatePositionWithParallaxBackground(deltaTime);
		
		if(piercingRounds != null){
			piercingRounds.updatePositionWithParallaxBackground(deltaTime);
		}
		
		if(disable != null){
			disable.updatePositionWithParallaxBackground(deltaTime);
		}
		
		if(warpDrive!=null){
			warpDrive.updatePositionWithParallaxBackground(deltaTime);
		}
		
		if(laserBarrage != null){
			laserBarrage.updatePositionWithParallaxBackground(deltaTime);
		}
	}
	
	@Override
	protected void checkBulletCollidesWithEnemies(float deltaTime){
		Iterator<Bullet> iter = bullets.iterator();
		Bullet b;
		float attackDamage;
		while(iter.hasNext()){
			b = iter.next();
			//critical hit
			attackDamage = Utils.getRandomFloat() < 0.2f ? attackDmg*2: attackDmg;
			if(super.helperCollisionOnEnemiesReturnsDamage(b, attackDamage) != 0){
				iter.remove();
			}
		}
	}
	
	@Override
	protected void castSpell1() {
		piercingRounds = new PiercingRounds(getPosition().x, getPosition().y, game);
		piercingRoundsSound.play(0.8f);
	}

	@Override
	protected void castSpell2() {
		disable = new Disable(getPosition().x, getPosition().y, game);
		disableSound.play(1f);
	}

	@Override
	protected void castSpell3() {
		warpDrive = new WarpDrive(getPosition().x, getPosition().y, game);
		movementSpeed *= 2;
		attackDmg *= 2;
		attackSpeed *= 2;
		warpDriveSound.play(0.8f);
	}

	@Override
	protected void castSpell4() {
		laserBarrage = new LaserBarrage(getPosition().x, getPosition().y, game);
	}

	@Override
	public boolean takeDamageAndCheckDies(float damage){
		boolean isDead = false;
		if(instaGuard != null){
			damage = instaGuard.takeDamage(damage);
		}
		if(warpDrive != null){
			if(Utils.getRandomFloat() < 0.90f){
				damage = 0;
			}
		}
		if(laserBarrage != null){
			damage = 0;
		}
		if(damage>0){ //actually got hit
			MusicAndSound.playSoundHeroGettingHit();
		}
		currentHP = currentHP - damage;
		if(currentHP <= 0){
			currentHP = 0;
			isDead = true;
		}
		return isDead;
	}
	
	@Override
	public void present(float deltaTime){
		if(piercingRounds != null){
			piercingRounds.present(deltaTime);
		}
		if(disable != null){
			disable.present(deltaTime);
		}
		if(warpDrive != null){
			warpDrive.present(deltaTime);
		}
		if(laserBarrage != null){
			laserBarrage.present(deltaTime);
		}
		super.present(deltaTime);
	}
	
	// =========================  Spells  ============================
	
	private class PiercingRounds{
		public static final int MAX_NUMBER_ROUNDS = 10;
		public float bossDamage = 5f;
		public float damage = attackDmg;
		List<Round> rounds;
		float animationTime;
		public PiercingRounds(float x, float y, OuyaGame game) {
			rounds = new LinkedList<Round>();
			float direction;
			for(int i=0; i<MAX_NUMBER_ROUNDS; i++){
				direction = (Steelash.this.angle+((90/MAX_NUMBER_ROUNDS)*i)-45) * Vector2.TO_RADIANS;
				Round m = new Round(x, y, direction, game);
				m.velocity.set(-FloatMath.sin(direction)*20, FloatMath.cos(direction)*20);
				rounds.add(m);
			}
		}
		public boolean isColliding(DynamicGameObject object) {
			boolean result = false;
			for(Round r : rounds){
				if(r.isColliding(object)){
					result = true;
					break;
				}
			}
			return result;
		}
		public boolean isAllOutOfScreen() {
			Iterator<Round> iter = rounds.iterator();
			Round r;
			while(iter.hasNext()){
				r = iter.next();
				if(Utils.isOutOfBoundsCompletely((Rectangle)r.bounds, screen.camera.getBounds())){
					iter.remove();
				}
			}
			return rounds.size() == 0;
		}
		public void update(float deltaTime){
			animationTime += deltaTime;
			for(Round r : rounds){
				r.update(deltaTime);
			}
		}
		public void updatePositionWithParallaxBackground(float deltaTime) {
			for(Round r : rounds){
				r.setPosition(r.getPosition().x, r.getPosition().y + screen.PARALLAX_SPEED*deltaTime);
			}
		}
		public void present(float deltaTime){
			if(rounds.size() > 0){
				piercingRoundsBatcher.beginBatch(piercingRoundsView.texture);
				for(Round r : rounds){
					piercingRoundsBatcher.drawSprite(r.getPosition().x, r.getPosition().y, PiercingRoundsView.WIDTH, PiercingRoundsView.HEIGHT, r.angle, piercingRoundsView.animation.getKeyFrame(animationTime, Animation.ANIMATION_NONLOOPING));
				}
				piercingRoundsBatcher.endBatch();
			}
		}
		private class Round extends DynamicGameObject{
			float angle;
			public Round(float x, float y, float angle, OuyaGame game) {
				super(x, y, PiercingRoundsView.WIDTH, PiercingRoundsView.HEIGHT);
				this.angle = angle*Vector2.TO_DEGREES;
			}
			
			public void update(float deltaTime){
				setPosition(getPosition().x + velocity.x * deltaTime*Utils.FPS, getPosition().y + velocity.y * deltaTime*Utils.FPS);
			}
		}
	}
	private class PiercingRoundsView extends HeroSpellView{
		public static final float WIDTH = 64f;
		public static final float HEIGHT = 128f;
		public PiercingRoundsView(OuyaGame game) {
			super(game, "heros/steelash/piercing_rounds.png", 1, 1, (int)WIDTH, (int)HEIGHT, 0.1f);
		}
	}
	
	private class Disable extends DynamicGameObject{
		public float damage = 8f;
		float animationTime;
		public Disable(float x, float y, OuyaGame game) {
			super(screen.camera.getPosition().x, screen.camera.getPosition().y, Utils.WORLD_WIDTH-50, Utils.WORLD_HEIGHT-50);
		}
		public void update(float deltaTime){
			animationTime += deltaTime;
			setPosition(screen.camera.getPosition().x, screen.camera.getPosition().y);
		}
		public void updatePositionWithParallaxBackground(float deltaTime) {
			setPosition(getPosition().x, getPosition().y + screen.PARALLAX_SPEED*deltaTime);
		}
		public void present(float deltaTime){
			disableBatcher.beginBatch(disableView.texture);
			disableBatcher.drawSprite(getPosition().x, getPosition().y, Utils.WORLD_WIDTH, Utils.WORLD_HEIGHT, disableView.animation.getKeyFrame(animationTime, Animation.ANIMATION_NONLOOPING));
			disableBatcher.endBatch();
		}
	}
	private class DisableView extends HeroSpellView{
		public DisableView(OuyaGame game) {
			super(game, "heros/steelash/disable.png", 8, 4, 256, 256, 0.035f);
		}
	}
	
	private class WarpDrive{
		public static final int MAX_NUMBER_SHADOWS = 10;
		public float bossDamage = 2f;
		public float damage = 5f;
		List<Shadow> shadows;
		OuyaGame game;
		float timerChangeShadow = 1;
		final float timerChangeShadowCD = 0.033f;
		public WarpDrive(float x, float y, OuyaGame game) {
			shadows = new LinkedList<Shadow>();
			this.game = game;
		}
		public void update(float deltaTime, float x, float y){
			if(timerChangeShadow >= timerChangeShadowCD){
				if(shadows.size()<MAX_NUMBER_SHADOWS){
					shadows.add(new Shadow(x, y, Steelash.this.angle, game));
				}
				for(int i=shadows.size()-1; i>0; i--){
					shadows.get(i).setPosition(shadows.get(i-1).getPosition());
					shadows.get(i).angle = shadows.get(i-1).angle;
				}
				shadows.get(0).setPosition(x,y); //should always be safe...
				shadows.get(0).angle = angle;
				timerChangeShadow = 0;
			} else {
				timerChangeShadow += deltaTime;
			}
		}
		public void updatePositionWithParallaxBackground(float deltaTime) {
			for(Shadow s : shadows){s.setPosition(s.getPosition().x, s.getPosition().y + screen.PARALLAX_SPEED*deltaTime);}
		}
		public boolean isColliding(DynamicGameObject other){
			boolean result = false;
			for(Shadow s : shadows){
				if(s.isColliding(other)){
					result = true;
				}
			}
			return result;
		}
		public void present(float deltaTime){
			if(shadows.size()>0){
				warpDriveBatcher.beginBatch(warpDriveView.texture);
				for(Shadow s : shadows){
					warpDriveBatcher.drawSprite(s.getPosition().x, s.getPosition().y, WarpDriveView.RADIUS*2, WarpDriveView.RADIUS*2, s.angle, warpDriveView.animation.getKeyFrame(s.animationTime, Animation.ANIMATION_NONLOOPING));
				}
				warpDriveBatcher.endBatch();
			}
		}
		private class Shadow extends DynamicGameObject{
			float animationTime;
			float angle;
			public Shadow(float x, float y, float angle, OuyaGame game) {
				super(x, y, WarpDriveView.RADIUS);
				this.angle = angle;
			}
		}
	}
	private class WarpDriveView extends HeroSpellView{
		static final float RADIUS = 64;
		public WarpDriveView(OuyaGame game) {
			super(game, "heros/steelash/warp_drive.png", 1, 1, (int)RADIUS*2, (int)RADIUS*2, 0.1f);
		}
	}
	
	private class LaserBarrage{
		public static final int MAX_NUMBER_LASERS = 500;
		public float damage = Steelash.this.attackDmg*3;
		public float bossDamage = damage/40f;
		List<Laser> lasers;
		OuyaGame game;
		float timerShootLaser = 1;
		final float timerShootLaserCD = 0.015f;
		
		public LaserBarrage(float x, float y, OuyaGame game) {
			lasers = new LinkedList<Laser>();
			this.game = game;
		}
		public void update(float deltaTime, float x, float y){
			if(spell4DurationTimer < spell4Duration){
				if(timerShootLaser >= timerShootLaserCD){
					if(lasers.size() < MAX_NUMBER_LASERS){
						laserBarrageSound.play(1f);
						lasers.add(new Laser(x, y, game));
						lasers.add(new Laser(x, y, game));
						lasers.add(new Laser(x, y, game));
						lasers.add(new Laser(x, y, game));
						lasers.add(new Laser(x, y, game));
					}
					timerShootLaser = 0;
				} else {
					timerShootLaser += deltaTime;
				}
			} else {

			}
		}
		public void updatePositionWithParallaxBackground(float deltaTime) {
			for(Laser l : lasers){
				l.setPosition(l.getPosition().x, l.getPosition().y + screen.PARALLAX_SPEED*deltaTime);
			}
		}
		public boolean isAllOutOfScreen() {
			Iterator<Laser> iter = lasers.iterator();
			Laser l;
			while(iter.hasNext()){
				l = iter.next();
				if(Utils.isOutOfBoundsCompletely((Rectangle)l.bounds, screen.camera.getBounds())){
					iter.remove();
				}
			}
			return lasers.size() == 0;
		}
		public boolean isColliding(DynamicGameObject other){
			boolean result = false;
			for(Laser l : lasers){
				if(l.isColliding(other)){
					result = true;
					break;
				}
			}
			return result;
		}
		public void present(float deltaTime){
			if(lasers.size() > 0){
				laserBarrageBatcher.beginBatch(laserBarrageView.texture);
				for(Laser l : lasers){
					l.update(deltaTime);
					laserBarrageBatcher.drawSprite(l.getPosition().x, l.getPosition().y, LaserBarrageView.WIDTH, LaserBarrageView.HEIGHT, l.angle, laserBarrageView.animation.getKeyFrame(l.animationTime, Animation.ANIMATION_NONLOOPING));
				}
				laserBarrageBatcher.endBatch();
			}
		}
		private class Laser extends DynamicGameObject{
			float animationTime;
			float angle;
			public Laser(float x, float y, OuyaGame game) {
				super(x, y, LaserBarrageView.WIDTH, LaserBarrageView.HEIGHT);
				float radians = (Utils.getRandomFloat() * 360f) * Vector2.TO_RADIANS;
				velocity.set(new Vector2(-FloatMath.sin(radians)*40f*Utils.FPS, FloatMath.cos(radians)*40f*Utils.FPS));
				this.angle = radians * Vector2.TO_DEGREES;
			}

			public void update(float deltaTime){
				animationTime += deltaTime;
				setPosition(getPosition().x + velocity.x * deltaTime, getPosition().y + velocity.y * deltaTime);
			}
		}
	}
	private class LaserBarrageView extends HeroSpellView{
		static final float WIDTH = 64;
		static final float HEIGHT = 128;
		public LaserBarrageView(OuyaGame game) {
			super(game, "heros/steelash/laser_barrage.png", 1, 1, (int)WIDTH, (int)HEIGHT, 0.1f);
		}
	}
	
	// =========================  View  ============================
	
	public class SteelashView extends HeroView {
		public SteelashView(OuyaGame game, int playerNumber) {
			super(game, playerNumber);
			textureShip = new Texture(game, "heros/steelash/steelash.png");
			regionShip = new TextureRegion(textureShip, 0, 0, HeroView.RADIUS*2, HeroView.RADIUS*2);
		}
	}
}
