public class Tank extends Hero {

	MegaShield megaShield;
	DispatchDrones dispatchDrones;
	DroneBullets droneBullets;
	SplashWaves splashWaves;
	GalacticForce galacticForce;
	
	//view stuff
	MegaShieldView megaShieldView;
	SpriteBatcher megaShieldBatcher;
	DispatchDronesView dispatchDronesView;
	SpriteBatcher dispatchDronesBatcher;
	DroneBulletView droneBulletView;
	SpriteBatcher droneBulletBatcher;
	SplashWaveView splashWaveView;
	SpriteBatcher splashWaveBatcher;
	GalacticForceView galacticForceView;
	SpriteBatcher galacticForceBatcher;
	
	//sound stuff
	Sound megaShieldSound;
	Sound dispatchDronesDispatchSound;
	Sound dispatchDronesBulletsSound;
	Sound splashWavesSound;
	Sound galacticForceSound;
	
	public Tank(float x, float y, float radius, OuyaGame game, OuyaScreen screen, int playerNumber) {
		super(x, y, radius, game, screen, playerNumber);
		view = new TankView(game, playerNumber);
		
		//base stats
		baseHp = 430;
		baseMp = 110;
		baseHpRegen = 2.9f;
		baseMpRegen = 1.5f;
		baseAttackDmg = 22f;
		baseAttackSpeed = 4.1f;
		baseMovementSpeed = 10f;

		//mega shield
		spell1MpCost = 80;
		spell1CdTimer = spell1Cd = 15f;
		spell1DurationTimer = spell1Duration = 3f;
		//dispatch drones	
		spell2MpCost = 80;
		spell2CdTimer = spell2Cd = 15f;
		spell2DurationTimer = spell2Duration = 8f;
		//splash waves
		spell3MpCost = 80;
		spell3CdTimer = spell3Cd = 15f;
		spell3DurationTimer = spell3Duration = 5f;
		//galactic force
		spell4MpCost = 240;
		spell4CdTimer = spell4Cd = 30f;
		spell4DurationTimer = spell4Duration = 5f; 
		
		megaShieldView = new MegaShieldView(game);
		megaShieldBatcher = new SpriteBatcher(game.getGLGraphics(), 1);
		dispatchDronesView = new DispatchDronesView(game);
		dispatchDronesBatcher = new SpriteBatcher(game.getGLGraphics(), 2);
		droneBulletView = new DroneBulletView(game);
		droneBulletBatcher = new SpriteBatcher(game.getGLGraphics(), 101); //hmm... this can break eventually.
		splashWaveBatcher = new SpriteBatcher(game.getGLGraphics(), 25);
		splashWaveView = new SplashWaveView(game);
		galacticForceView = new GalacticForceView(game);
		galacticForceBatcher = new SpriteBatcher(game.getGLGraphics(), 1);
		
		//sounds
		megaShieldSound = game.getAudio().newSound("sound/tank/mega_shield.ogg");
		dispatchDronesDispatchSound = game.getAudio().newSound("sound/tank/dispatch_drones_dispatch.ogg");
		dispatchDronesBulletsSound = game.getAudio().newSound("sound/tank/dispatch_drones_firing.ogg");
		splashWavesSound = game.getAudio().newSound("sound/tank/splash_waves.ogg");
		galacticForceSound = game.getAudio().newSound("sound/tank/galactic_force.ogg");
	}

	@Override
	protected void updateSpellEffectsAndTimer(float deltaTime) {
		if(spell1DurationTimer < spell1Duration){
			spell1DurationTimer += deltaTime;
			megaShield.update(deltaTime, getPosition().x, getPosition().y);
		} else {
			megaShield = null;
		}
		
		if(spell2DurationTimer < spell2Duration){
			spell2DurationTimer += deltaTime;
			dispatchDrones.update(deltaTime, getPosition().x, getPosition().y);
			droneBullets.update(deltaTime);
		} else {
			dispatchDrones = null;
			droneBullets = null;
		}
		
		if(spell3DurationTimer < spell3Duration){
			spell3DurationTimer += deltaTime;
			splashWaves.update(deltaTime, getPosition().x, getPosition().y);
		} else {
			splashWaves = null;
		}
		
		if(spell4DurationTimer < spell4Duration){
			spell4DurationTimer += deltaTime;
			galacticForce.update(deltaTime, getPosition().x, getPosition().y);
		} else {
			galacticForce = null;
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
				 if(megaShield != null){
					 if(megaShield.isColliding(m)){
						 if(m.takeDamageAndCheckDies(megaShield.damage)){
							 screen.explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, view.game));
							 gainExp(m.dropExperience);
							 iterM.remove();
							 continue;
						 }
					 }
				 }
				 if(dispatchDrones != null){
					 if(dispatchDrones.isColliding(m)){
						 if(m.takeDamageAndCheckDies(dispatchDrones.damage)){
							 screen.explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, view.game));
							 gainExp(m.dropExperience);
							 iterM.remove();
							 continue;
						 }
					 }
					 
					 if(droneBullets.checkIsColliding(m)){
						 if(m.takeDamageAndCheckDies(DroneBullet.ATTACK_DAMAGE)){
							 screen.explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, view.game));
							 gainExp(m.dropExperience);
							 iterM.remove();
							 continue;
						 }
					 }					 
				 }
				 if(splashWaves != null){
					 if(splashWaves.isObjectWithinAOE(m)){
						 if(m.takeDamageAndCheckDies(splashWaves.damage)){
							 screen.explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, view.game));
							 gainExp(m.dropExperience);
							 iterM.remove();
							 continue;
						 }
					 }
				 }
				 
				 if(galacticForce != null){
					 if(galacticForce.isColliding(m)){
						 if(m.takeDamageAndCheckDies(GalacticForce.damage )){
							 screen.explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, view.game));
							 gainExp(m.dropExperience);
							 iterM.remove();
							 continue;
						 }
					 } else {
						 if(m.takeDamageAndCheckDies(GalacticForce.outOfRangeDamage)){
							 screen.explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, view.game));
							 gainExp(m.dropExperience);
							 iterM.remove();
							 continue;
						 }
					 }
					 //gravitational effect here:
					 float offsetX, offsetY;
					 offsetX = m.getPosition().x > getPosition().x ? -60f: 60f;
					 offsetY = m.getPosition().y > getPosition().y ? -60f: 60f;
					 m.setPosition(m.getPosition().x + offsetX*deltaTime, m.getPosition().y + offsetY*deltaTime);
				 }
			 }
		}
		
		Iterator<MinionBullet> iterMB = screen.minionBullets.iterator();
		MinionBullet mb;
		while(iterMB.hasNext()){
			mb = iterMB.next();
			if(megaShield != null){
				if(megaShield.isColliding(mb)){
					iterMB.remove();
					continue;
				}
			}
			
			if(dispatchDrones != null){
				if(dispatchDrones.isColliding(mb)){
					iterMB.remove();
					continue;
				}
			}
			
			if(splashWaves != null){
				 if(splashWaves.isObjectWithinAOE(mb)){
				 	 iterMB.remove();
					 continue;
				 }
			 }
			
			if(galacticForce != null){
				if(mb.isColliding(galacticForce.toBoundsForNullifyBullet())){
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
			if(megaShield != null){
				if(megaShield.isColliding(m)){
					screen.boss.takeDamageAndCheckDies(megaShield.bossDamage );		 
				}
			}
			if(dispatchDrones != null){
				if(dispatchDrones.isColliding(m)){					 
					screen.boss.takeDamageAndCheckDies(dispatchDrones.bossDamage);
				}
			}
			if(splashWaves != null){
				if(splashWaves.isObjectWithinAOE(m)){
					screen.boss.takeDamageAndCheckDies(splashWaves.bossDamage);
				}
			} 
			if(galacticForce != null){
				if(galacticForce.isColliding(m)){
					screen.boss.takeDamageAndCheckDies(GalacticForce.bossDamage);
				} else {
					screen.boss.takeDamageAndCheckDies(GalacticForce.outOfRangeBossDamage);
				}
			}
		}
	}
	
	@Override
	protected void updatePositionWithParallaxBackground(float deltaTime){
		super.updatePositionWithParallaxBackground(deltaTime);
		if(megaShield != null){
			megaShield.updatePositionWithParallaxBackground(deltaTime);
		}
		if(dispatchDrones != null){
			dispatchDrones.updatePositionWithParallaxBackground(deltaTime);
		}
		if(droneBullets != null){
			droneBullets.updatePositionWithParallaxBackground(deltaTime);
		}
		if(splashWaves != null){
			splashWaves.updatePositionWithParallaxBackground(deltaTime);
		}
		if(galacticForce != null){
			galacticForce.updatePositionWithParallaxBackground(deltaTime);
		}
	}
	
	@Override
	protected void castSpell1() {
		megaShield = new MegaShield(getPosition().x, getPosition().y, MegaShieldView.RADIUS, game);
		megaShieldSound.play(1f);
	}

	@Override
	protected void castSpell2() {
		dispatchDrones = new DispatchDrones(getPosition().x, getPosition().y, angle, game);
		droneBullets = new DroneBullets(game);
		dispatchDronesDispatchSound.play(1f);
	}

	@Override
	protected void castSpell3() {
		splashWaves = new SplashWaves(getPosition().x, getPosition().y, game);
		splashWavesSound.play(GameData.soundVolume);
	}

	@Override
	protected void castSpell4() {
		galacticForce = new GalacticForce(getPosition().x, getPosition().y, game);
		galacticForceSound.play(0.75f);
	}
	
	@Override
	public boolean takeDamageAndCheckDies(float damage){
		boolean isDead = false;
		if(instaGuard != null){
			damage = instaGuard.takeDamage(damage);
		}
		if(megaShield != null || galacticForce != null){
			damage = 0; //reduced so can survive
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
		if(megaShield != null){
			megaShield.present(deltaTime);
		}
		
		if(dispatchDrones != null){
			dispatchDrones.present(deltaTime);
			droneBullets.present(deltaTime);
		}
		
		if(splashWaves != null){
			splashWaves.present(deltaTime);
		}
		
		if(galacticForce != null){
			galacticForce.present(deltaTime);
		}
		
		super.present(deltaTime);
	}
	
	// =========================  Spells  ============================
	
	private class MegaShield extends DynamicGameObject{
		public float bossDamage = 2f;
		public float damage = 30f; //per second? TODO
		float animationTime;
		public MegaShield(float x, float y, float radius, OuyaGame game) {
			super(x, y, MegaShieldView.RADIUS*1.15f); //collision range has to be slightly bigger
		}
		public void update(float deltaTime, float x, float y){
			animationTime += deltaTime;
			setPosition(x, y);
		}
		public void updatePositionWithParallaxBackground(float deltaTime) {
			setPosition(getPosition().x, getPosition().y + screen.PARALLAX_SPEED*deltaTime);
		}
		public void present(float deltaTime){
			megaShieldBatcher.beginBatch(megaShieldView.texture);
			megaShieldBatcher.drawSprite(getPosition().x, getPosition().y, MegaShieldView.RADIUS *2 * MegaShieldView.VIEW_MAGNIFIER_FACTOR, MegaShieldView.RADIUS *2 * MegaShieldView.VIEW_MAGNIFIER_FACTOR, megaShieldView.animation.getKeyFrame(animationTime, Animation.ANIMATION_LOOPING));
			megaShieldBatcher.endBatch();
		}
	}
	private class MegaShieldView extends HeroSpellView{
		public static final float VIEW_MAGNIFIER_FACTOR = 1.5f;
		public static final float RADIUS = 64;
		public MegaShieldView(OuyaGame game) {
			super(game, "heros/tank/tank_spell_1_mega_shield.png", 10, 4, 4*(int)RADIUS, 4*(int)RADIUS, 0.1f);
		}
	}
	
	private class DispatchDrones extends DynamicGameObject{
		public float bossDamage = 1f;
		public float damage = 10f;
		float x1, x2, y1, y2; //these are the 2 images to make up all 8 drones
		float xValues[]; //these 2 arrays contain the actual points of where the drones are at
		float yValues[];
		Drone allCollidiableDrones[];
		float angle;
		
		public DispatchDrones(float x, float y, float angle, OuyaGame game) {
			super(x, y, DispatchDronesView.WIDTH, DispatchDronesView.HEIGHT); //collision range has to be slightly bigger
			this.angle = angle;
			float radians = angle * Vector2.TO_RADIANS;
			x1 = x + DispatchDronesView.WIDTH/2 * FloatMath.cos(radians);
			y1 = y + DispatchDronesView.WIDTH/2 * FloatMath.sin(radians);
			x2 = x + DispatchDronesView.WIDTH/2 * -FloatMath.cos(radians);
			y2 = y + DispatchDronesView.WIDTH/2 * -FloatMath.sin(radians);
			xValues = new float[8];
			yValues = new float[8];
			for(int i=0; i<4; i++){
				xValues[i] = x + ((i*DispatchDronesView.WIDTH/4) + DispatchDronesView.WIDTH/8)*FloatMath.cos(radians);
				xValues[i+4] = x - ((i*DispatchDronesView.WIDTH/4) + DispatchDronesView.WIDTH/8)*FloatMath.cos(radians);
				yValues[i] = y + ((i*DispatchDronesView.WIDTH/4) + DispatchDronesView.WIDTH/8)*FloatMath.sin(radians);
				yValues[i+4] = y - ((i*DispatchDronesView.WIDTH/4) + DispatchDronesView.WIDTH/8)*FloatMath.sin(radians);
			}
			allCollidiableDrones = new Drone[8];
			for(int i=0; i<allCollidiableDrones.length; i++){
				allCollidiableDrones[i] = new Drone(xValues[i], yValues[i], DispatchDronesView.HEIGHT/2);
			}
		}
		private class Drone extends DynamicGameObject{
			public Drone(float x, float y, float radius){
				super(x,y,radius);
			}
		}
		public void update(float deltaTime, float x, float y){ 
			//even need to do anything here?
		}
		public void updatePositionWithParallaxBackground(float deltaTime) {
			y1 += screen.PARALLAX_SPEED*deltaTime;
			y2 += screen.PARALLAX_SPEED*deltaTime;
			for(Drone d : allCollidiableDrones){ 
				d.setPosition(d.getPosition().x, d.getPosition().y + screen.PARALLAX_SPEED*deltaTime);
			}
			for(int i=0; i<yValues.length; i++){
				yValues[i] += screen.PARALLAX_SPEED * deltaTime;
			}
		}
		@Override
		public boolean isColliding(DynamicGameObject other){
			boolean collided = false;
			for(int i=0; i<allCollidiableDrones.length; i++){
				if(allCollidiableDrones[i].isColliding(other)){
					collided = true;
				}
			}
			return collided;
		}
		public void present(float deltaTime){
			dispatchDronesBatcher.beginBatch(dispatchDronesView.texture);
			dispatchDronesBatcher.drawSprite(x1, y1, DispatchDronesView.WIDTH, DispatchDronesView.HEIGHT, angle, dispatchDronesView.regions[0]);
			dispatchDronesBatcher.drawSprite(x2, y2, DispatchDronesView.WIDTH, DispatchDronesView.HEIGHT, angle, dispatchDronesView.regions[1]);
			dispatchDronesBatcher.endBatch();
		}
	}
	private class DispatchDronesView extends HeroSpellView{
		public static final float WIDTH = 256;
		public static final float HEIGHT = 64;
		public DispatchDronesView(OuyaGame game) {
			super(game, "heros/tank/tank_spell_2_dispatch_drones.png", 2, 1, (int)WIDTH, (int)HEIGHT, 0f);
		}
	}
	
	private class DroneBullets {
		float firingTimer;
		float firingTimerCD;
		List<DroneBullet> bullets;
		float xValues[];
		float yValues[];
		float angle;
		public DroneBullets(OuyaGame game) {
			firingTimerCD = .25f;
			firingTimer = firingTimerCD + 1f;
			angle = 0f;
			bullets = new LinkedList<DroneBullet>();
			if(dispatchDrones != null){
				xValues = dispatchDrones.xValues;
				yValues = dispatchDrones.yValues;
			}
		}
		public void update(float deltaTime){
			if(firingTimer >= firingTimerCD){ // can shoot
				//assuming dispatchDrones is not null...
				if(angle < 360){
					for(int i=0; i<xValues.length; i++){
						bullets.add(new DroneBullet(xValues[i], yValues[i], angle));
					}
				}
				angle += 36f;
				firingTimer = 0f;
				dispatchDronesBulletsSound.play(1f);
			} else {
				for(DroneBullet db : bullets){
					db.update(deltaTime);
				}
				firingTimer += deltaTime;
			}
		}
		public void updatePositionWithParallaxBackground(float deltaTime) {
			for(DroneBullet db : bullets){db.setPosition(db.getPosition().x, db.getPosition().y+screen.PARALLAX_SPEED*deltaTime);}
		}
		public boolean checkIsColliding(DynamicGameObject other){
			//check to see if hitting enemies also...
			Iterator<DroneBullet> iter = bullets.iterator();
			DroneBullet db;
			while(iter.hasNext()){
				db = iter.next();
				if( db.isColliding(other) ) {
					iter.remove();
					return true;
				}
			}
			return false;
		}
		public void present(float deltaTime){
			if(bullets.size() > 0){
				droneBulletBatcher.beginBatch(droneBulletView.texture);
				for(DroneBullet db : bullets){
					droneBulletBatcher.drawSprite(db.getPosition().x, db.getPosition().y, DroneBulletView.RADIUS*2*2, DroneBulletView.RADIUS *2*2, droneBulletView.region);
				}
				droneBulletBatcher.endBatch();
			}
		}
	}
	private class DroneBullet extends DynamicGameObject {
		static final float ATTACK_DAMAGE = 10f; //TODO shoould this be a percentage of tank's attack?
		public DroneBullet(float x, float y, float angle) {
			super(x, y, DroneBulletView.RADIUS);
			float radians = angle * Vector2.TO_RADIANS;
			velocity.set(-FloatMath.sin(radians) * 30, FloatMath.cos(radians) * 30);
		}
		public void update(float deltaTime){
			setPosition(getPosition().x + deltaTime*Utils.FPS*velocity.x, getPosition().y + deltaTime*Utils.FPS*velocity.y);
		}
	}
	private class DroneBulletView{
		static final float RADIUS = 8f;
		Texture texture;
		TextureRegion region;
		public DroneBulletView(OuyaGame game){
			texture = Tank.this.view.textureBullets;
			region = Tank.this.view.regionDroneBullet;
		}
	}		
	
	private class SplashWaves{
		public float bossDamage = 2f;
		public float damage = 8f;
		List<SplashWave> waves;
		OuyaGame game;
		SplashWaveAOE aoeRange;
		public SplashWaves(float x, float y, OuyaGame game) {
			waves = new LinkedList<SplashWave>();
			this.game = game;
			aoeRange = new SplashWaveAOE(x, y);
		}
		public void update(float deltaTime, float x, float y){
			aoeRange.update(x, y);
			if(waves.size() == 0){
				waves.add(new SplashWave(x, y, game));
			} else if(waves.get(waves.size()-1).animationTime >= 0.025f){
				waves.add(new SplashWave(x, y, game));
			}
			Iterator<SplashWave> iter = waves.iterator();
			SplashWave sw;
			while(iter.hasNext()){
				sw = iter.next();
				sw.update(deltaTime, x, y);
				if(sw.animationTime >= 0.5f){
					iter.remove();
				}
			}
		}
		public void updatePositionWithParallaxBackground(float deltaTime) {
			for(SplashWave sw : waves){sw.setPosition(sw.getPosition().x, sw.getPosition().y + screen.PARALLAX_SPEED*deltaTime);}
		}
		public boolean isObjectWithinAOE(DynamicGameObject other){
			return aoeRange.isColliding(other);
		}
		public void present(float deltaTime){
			splashWaveBatcher.beginBatch(splashWaveView.texture);
			for(SplashWave sw : waves){
				splashWaveBatcher.drawSprite(sw.getPosition().x, sw.getPosition().y, SplashWaveView.WIDTH, SplashWaveView.HEIGHT, sw.angle, splashWaveView.animation.getKeyFrame(sw.animationTime, Animation.ANIMATION_LOOPING));
			}
			splashWaveBatcher.endBatch();
		}
		private class SplashWave extends DynamicGameObject{
			float animationTime;
			float angle;
			public SplashWave(float x, float y, OuyaGame game) {
				super(x, y, SplashWaveView.WIDTH, SplashWaveView.HEIGHT);
				float radians = (Utils.getRandomFloat() * 360f) * Vector2.TO_RADIANS;
				velocity.set(new Vector2(-FloatMath.sin(radians)*450f, FloatMath.cos(radians)*450f));
				this.angle = radians * Vector2.TO_DEGREES;
			}
			public void update(float deltaTime, float x, float y){
				animationTime += deltaTime;
				setPosition(getPosition().x + velocity.x * deltaTime, getPosition().y + velocity.y * deltaTime);
			}
		}
		private class SplashWaveAOE extends DynamicGameObject{
			public SplashWaveAOE(float x, float y) {
				super(x, y, TankView.RADIUS*3.0f);
			}
			public void update(float x, float y){
				setPosition(x, y);
			}
		}
	}
	private class SplashWaveView extends HeroSpellView{
		static final float WIDTH = 256;
		static final float HEIGHT = 128;
		public SplashWaveView(OuyaGame game) {
			super(game, "heros/tank/tank_spell_3_splash_waves.png", 6, 4, (int)WIDTH/2, (int)HEIGHT/2, 0.05f);
		}
	}
	
	private class GalacticForce extends DynamicGameObject{
		static final float bossDamage = 1.5f;
		static final float outOfRangeBossDamage = 0.1f;
		
		static final float damage = 2f;
		static final float outOfRangeDamage = 0.1f;
		
		float animationTime;
		public GalacticForce(float x, float y, OuyaGame game) {
			super(x, y, GalacticForceView.RADIUS * GalacticForceView.VIEW_MAGNIFIER_FACTOR); 
		}
		public DynamicGameObject toBoundsForNullifyBullet() {
			return new BoundsForNullifyingBullet(getPosition().x, getPosition().y, TankView.RADIUS*2);
		}
		private class BoundsForNullifyingBullet extends DynamicGameObject{
			public BoundsForNullifyingBullet(float x, float y, float radius) {
				super(x, y, radius);
			}
		}
		public void update(float deltaTime, float x, float y){
			animationTime += deltaTime;
			setPosition(x,y);
		}
		public void updatePositionWithParallaxBackground(float deltaTime) {
			setPosition(getPosition().x,getPosition().y+screen.PARALLAX_SPEED*deltaTime);
		}
		public void present(float deltaTime){
			galacticForceBatcher.beginBatch(galacticForceView.texture);
			galacticForceBatcher.drawSprite(getPosition().x, getPosition().y, GalacticForceView.RADIUS*2*GalacticForceView.VIEW_MAGNIFIER_FACTOR, GalacticForceView.RADIUS*2*GalacticForceView.VIEW_MAGNIFIER_FACTOR, galacticForceView.animation.getKeyFrame(animationTime, Animation.ANIMATION_LOOPING));
			galacticForceBatcher.endBatch();
		}
	}
	private class GalacticForceView extends HeroSpellView{
		public static final float RADIUS = 128;
		public static final float VIEW_MAGNIFIER_FACTOR = 4f;
		public GalacticForceView(OuyaGame game) {
			super(game, "heros/tank/tank_spell_4_galactic_force.png", 8, 4, 2*(int)RADIUS, 2*(int)RADIUS, 0.08f);
		}
	}
	// =========================  View  ============================
	
	public class TankView extends HeroView {
		public TankView(OuyaGame game, int playerNumber) {
			super(game, playerNumber);
			textureShip = new Texture(game, "heros/tank/tank.png");
			regionShip = new TextureRegion(textureShip, 0, 0, HeroView.RADIUS*2, HeroView.RADIUS*2);
		}
	}
}
