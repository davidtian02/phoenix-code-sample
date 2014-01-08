public class Carry extends Hero {

	SparkAttack sparkAttack;
	ClusterMissles clusterMissles;
	GuardianSparks guardianSparks;
	HellfireMissles hellfireMissles;
	
	//view stuff
	SparkAttackView sparkAttackView;
	SpriteBatcher sparkAttackBatcher;
	ClusterMisslesView clusterMisslesView;
	SpriteBatcher clusterMisslesBatcher;
	SpriteBatcher guardianSparksBatcher;
	SpriteBatcher hellfireMisslesBatcher;
	
	//sound stuff
	Sound sparkAttackSound;
	Sound clusterMisslesSound;
	Sound guardianSparksSound;
	Sound hellfireMisslesSound;
	
	public Carry(float x, float y, float radius, OuyaGame game, OuyaScreen screen, int playerNumber) {
		super(x, y, radius, game, screen, playerNumber);
		view = new CarryView(game, playerNumber);
		
		//base stats
		baseHp = 240;
		baseMp = 190;
		baseHpRegen = 1.5f;
		baseMpRegen = 1.5f;
		baseAttackDmg = 27f;
		baseAttackSpeed = 5f;
		baseMovementSpeed = 20f;

		//Spark Attack
		spell1MpCost = 80;
		spell1CdTimer = spell1Cd = 15f;
		//Cluster Missles
		spell2MpCost = 80;
		spell2CdTimer = spell2Cd = 15f;
		//Spark Shield
		spell3MpCost = 80;
		spell3CdTimer = spell3Cd = 15f;
		spell3DurationTimer = spell3Duration = 5f;
		//galactic force
		spell4MpCost = 240;
		spell4CdTimer = spell4Cd = 30f;
		spell4DurationTimer = spell4Duration = 10f;
		
		//view stuff
		sparkAttackView = new SparkAttackView(game);
		sparkAttackBatcher = new SpriteBatcher(game.getGLGraphics(), 1);
		clusterMisslesView = new ClusterMisslesView(game);
		clusterMisslesBatcher = new SpriteBatcher(game.getGLGraphics(), ClusterMissles.missleCount);
		guardianSparksBatcher = new SpriteBatcher(game.getGLGraphics(), 25);
		hellfireMisslesBatcher = new SpriteBatcher(game.getGLGraphics(), HellfireMissles.missleCountCap);
		
		//sound stuff
		sparkAttackSound = game.getAudio().newSound("sound/carry/spark_attack.ogg");
		clusterMisslesSound = game.getAudio().newSound("sound/carry/cluster_missles.ogg");
		guardianSparksSound = game.getAudio().newSound("sound/carry/guardian_sparks.ogg");
		hellfireMisslesSound = game.getAudio().newSound("sound/carry/cluster_missles.ogg");;
	}

	@Override
	protected void updateSpellEffectsAndTimer(float deltaTime) {
		if(sparkAttack != null){
			if(Utils.isOutOfBoundsCompletely( (Circle)sparkAttack.bounds, screen.camera.getBounds()) ){
				sparkAttack = null;
			} else {
				sparkAttack.update(deltaTime);
			}
		}
		
		if(clusterMissles != null){
			if(clusterMissles.isAllOutOfScreen()){
				clusterMissles = null; 
			} else {
				clusterMissles.update(deltaTime);
			}
		}
		
		if(spell3DurationTimer < spell3Duration){
			spell3DurationTimer += deltaTime;
			guardianSparks.update(deltaTime);
		} else {
			guardianSparks = null;
		}
		
		if(spell4DurationTimer < spell4Duration){
			spell4DurationTimer += deltaTime;
			if(hellfireMissles != null){
				hellfireMissles.update(deltaTime);
			}
		} else {
			if(hellfireMissles != null){
				if(hellfireMissles.isAllOutOfScreen()){
					hellfireMissles = null;
				}
			}
		}
		
		super.updateSpellEffectsAndTimer(deltaTime);
	}
	
	@Override
	protected void updateBuffsAndAuras(float deltaTime){
		super.updateBuffsAndAuras(deltaTime);
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
				if(sparkAttack != null){
					if(sparkAttack.isColliding(m)){
						if(m.takeDamageAndCheckDies(sparkAttack.damage)){
							screen.explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, view.game));
							gainExp(m.dropExperience);
							iterM.remove();
							continue;
						}						 
					}
				}
				
				if(clusterMissles != null){
					if(clusterMissles.isColliding(m)){
						if(m.takeDamageAndCheckDies(clusterMissles.damage)){
							screen.explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, view.game));
							gainExp(m.dropExperience);
							iterM.remove();
							continue;
						}
					}
				}

				if(guardianSparks != null){
					if(guardianSparks.isColliding(m)){
						if(m.takeDamageAndCheckDies(guardianSparks.damage)){
							screen.explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, view.game));
							gainExp(m.dropExperience);
							iterM.remove();
							continue;
						}
					}
				}

				if(hellfireMissles != null){
					if(hellfireMissles.isColliding(m)){
						if(m.takeDamageAndCheckDies(hellfireMissles.damage)){
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
			if(sparkAttack != null){
				if(sparkAttack.isColliding(mb)){
					iterMB.remove();
					continue;
				}
			}
			
			if(guardianSparks != null){
				if(guardianSparks.isColliding(mb)){
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
			if(sparkAttack != null){
				if(sparkAttack.isColliding(m)){
					 screen.boss.takeDamageAndCheckDies(sparkAttack.bossDamage);
				}
			}
			
			if(clusterMissles != null){
				if(clusterMissles.isColliding(m)){
					screen.boss.takeDamageAndCheckDies(clusterMissles.damage);
				}
			}
			
			if(guardianSparks != null){
				if(guardianSparks.isColliding(m)){
					screen.boss.takeDamageAndCheckDies(guardianSparks.bossDamage);
				}
			}
			
			if(hellfireMissles != null){
				if(hellfireMissles.isColliding(m)){
					screen.boss.takeDamageAndCheckDies(hellfireMissles.bossDamage );
				}
			}
		}
		
		if(screen.boss.getNonDamageableParts() != null){
			for(Minion m : screen.boss.getNonDamageableParts()){
				if(clusterMissles != null){
					if(clusterMissles.isColliding(m)){
						MusicAndSound.playSoundEnemyGettingHitNotDamaged();
					}
				}
				if(hellfireMissles != null){
					if(hellfireMissles.isColliding(m)){
						MusicAndSound.playSoundEnemyGettingHitNotDamaged();
					}
				}
			}
		}
	}
	
	@Override
	protected void updatePositionWithParallaxBackground(float deltaTime){
		super.updatePositionWithParallaxBackground(deltaTime);
		if(sparkAttack != null){
			sparkAttack.updatePositionWithParallaxBackground(deltaTime);
		}
		if(clusterMissles != null){
			clusterMissles.updatePositionWithParallaxBackground(deltaTime);
		}
		if(guardianSparks != null){
			guardianSparks.updatePositionWithParallaxBackground(deltaTime);
		}
		if(hellfireMissles != null){
			hellfireMissles.updatePositionWithParallaxBackground(deltaTime);
		}
	}
	
	@Override
	protected void castSpell1() {
		sparkAttack = new SparkAttack(getPosition().x, getPosition().y, game);
		sparkAttackSound.play(1f);
	}

	@Override
	protected void castSpell2() {
		clusterMissles = new ClusterMissles(getPosition().x, getPosition().y, game);
		clusterMisslesSound.play(0.5f);
	}

	@Override
	protected void castSpell3() {
		guardianSparks = new GuardianSparks(getPosition().x, getPosition().y, game);
		guardianSparksSound.play(1f);
	}

	@Override
	protected void castSpell4() {
		hellfireMissles = new HellfireMissles(getPosition().x, getPosition().y, game);
	}
	
	@Override
	public void present(float deltaTime){
		if(sparkAttack != null){
			sparkAttack.present(deltaTime);
		}
		
		if(clusterMissles != null){
			if(clusterMissles.isNotEmpty()){
				clusterMissles.present(deltaTime);
			}
		}
		
		if(guardianSparks != null){
			guardianSparks.present(deltaTime);
		}
		
		if(hellfireMissles != null){
			if(hellfireMissles.isNotEmpty()){
				hellfireMissles.present(deltaTime);
			}
		}
		
		super.present(deltaTime);
	}

	// =========================  Spells  ============================
	
	private class SparkAttack extends DynamicGameObject{
		public float bossDamage = 2f;
		public float damage = 20f;
		float animationTime;
		public SparkAttack(float x, float y, OuyaGame game) {
			super(x, y, SparkAttackView.RADIUS);
			float radians = angle * Vector2.TO_RADIANS;
			velocity.set(new Vector2(-FloatMath.sin(radians)*7.5f, FloatMath.cos(radians)*7.5f));
		}
		public void update(float deltaTime){
			animationTime += deltaTime;
			setPosition(velocity.x*deltaTime*Utils.FPS + getPosition().x, velocity.y*deltaTime*Utils.FPS + getPosition().y);
		}

		public void updatePositionWithParallaxBackground(float deltaTime) {
			setPosition(getPosition().x, getPosition().y + screen.PARALLAX_SPEED*deltaTime);
		}
		public void present(float deltaTime){
			sparkAttackBatcher.beginBatch(sparkAttackView.texture);
			sparkAttackBatcher.drawSprite(getPosition().x, getPosition().y, SparkAttackView.RADIUS*2*SparkAttackView.VIEW_MAGNIFIER_FACTOR, SparkAttackView.RADIUS*2*SparkAttackView.VIEW_MAGNIFIER_FACTOR, sparkAttackView.animation.getKeyFrame(animationTime, Animation.ANIMATION_LOOPING));
			sparkAttackBatcher.endBatch();
		}
	}
	private class SparkAttackView extends HeroSpellView{
		public static final float VIEW_MAGNIFIER_FACTOR = 2;
		public static final float RADIUS = 128;
		public SparkAttackView(OuyaGame game) {
			super(game, "heros/carry/carry_spell_1_spark_attack.png", 8, 4, 2*(int)RADIUS, 2*(int)RADIUS, 0.1f);
		}
	}
	
	private class ClusterMissles{
		public float damage = 200f;
		public static final int missleCount = 5;
		List<Missle> missles;
		float animationTime;
		public ClusterMissles(float x, float y, OuyaGame game) {
			missles = new LinkedList<Missle>();
			float direction;
			for(int i=0; i<missleCount; i++){
				direction = (Carry.this.angle+(18*i)-45) * Vector2.TO_RADIANS;
				Missle m = new Missle(x, y, direction, game);
				m.velocity.set(-FloatMath.sin(direction)*15, FloatMath.cos(direction)*15);
				missles.add(m);
			}
		}
		public boolean isNotEmpty() {
			return missles.size()>0;
		}
		public boolean isColliding(Minion m) {
			boolean result = false;
			Iterator<Missle> iterM = missles.iterator();
			Missle mis;
			while(iterM.hasNext()){
				mis = iterM.next();
				if(mis.isColliding(m)){
					iterM.remove();
					return true;
				}
			}
			return result;
		}
		public boolean isAllOutOfScreen() {
			Iterator<Missle> iter = missles.iterator();
			Missle mis;
			while(iter.hasNext()){
				mis = iter.next();
				if(Utils.isOutOfBoundsCompletely((Rectangle)mis.bounds, screen.camera.getBounds())){
					iter.remove();
				}
			}
			return missles.size() == 0;
		}
		public void update(float deltaTime){
			animationTime += deltaTime;
			for(Missle mis : missles){
				mis.update(deltaTime);
			}
		}
		public void updatePositionWithParallaxBackground(float deltaTime) {
			for(Missle mis : missles){
				mis.setPosition(mis.getPosition().x, mis.getPosition().y + screen.PARALLAX_SPEED*deltaTime);
			}
		}
		public void present(float deltaTime){
			clusterMisslesBatcher.beginBatch(clusterMisslesView.texture);
			for(Missle f : missles){
				clusterMisslesBatcher.drawSprite(f.getPosition().x, f.getPosition().y, ClusterMisslesView.WIDTH, ClusterMisslesView.HEIGHT, f.angle, clusterMisslesView.animation.getKeyFrame(animationTime, Animation.ANIMATION_LOOPING));
			}
			clusterMisslesBatcher.endBatch();
		}
		private class Missle extends DynamicGameObject{
			float angle;
			public Missle(float x, float y, float angle, OuyaGame game) {
				super(x, y, ClusterMisslesView.WIDTH, ClusterMisslesView.HEIGHT);
				this.angle = angle*Vector2.TO_DEGREES;
			}
			
			public void update(float deltaTime){
				setPosition(getPosition().x + velocity.x * deltaTime*Utils.FPS, getPosition().y + velocity.y * deltaTime*Utils.FPS);
			}
		}
	}
	private class ClusterMisslesView extends HeroSpellView{
		public static final float WIDTH = 128f;
		public static final float HEIGHT = 256f;
		public ClusterMisslesView(OuyaGame game) {
			super(game, "heros/carry/carry_spell_2_cluster_missles.png", 2, 2, (int)WIDTH, (int)HEIGHT, 0.1f);
		}
	}

	private class GuardianSparks{
		public float bossDamage = 2f;
		public float damage = 8f;
		List<Spark> sparks;
		OuyaGame game;
		public GuardianSparks(float x, float y, OuyaGame game) {
			sparks = new LinkedList<Spark>();
			this.game = game;
		}
		public void update(float deltaTime){
			if(sparks.size() == 0){
				sparks.add(new Spark(getPosition().x, getPosition().y, game));
			} else if(sparks.get(sparks.size()-1).animationTime >= 0.025f){
				sparks.add(new Spark(getPosition().x, getPosition().y, game));
			}
			Iterator<Spark> iter = sparks.iterator();
			Spark s;
			while(iter.hasNext()){
				s = iter.next();
				s.update(deltaTime);
				if(s.animationTime >= 0.5f){
					iter.remove();
				}
			}
		}
		public void updatePositionWithParallaxBackground(float deltaTime) {
			for(Spark s : sparks){s.setPosition(s.getPosition().x, s.getPosition().y + screen.PARALLAX_SPEED*deltaTime);}
		}
		public boolean isColliding(DynamicGameObject other){
			for(Spark s : sparks){
				if(s.isColliding(other)){
					return true;
				}
			}
			return false;
		}
		public void present(float deltaTime){
			guardianSparksBatcher.beginBatch(sparkAttackView.texture);
			for(Spark gs : sparks){
				guardianSparksBatcher.drawSprite(gs.getPosition().x, gs.getPosition().y, SparkAttackView.RADIUS, SparkAttackView.RADIUS, sparkAttackView.animation.getKeyFrame(gs.animationTime, Animation.ANIMATION_LOOPING));
			}
			guardianSparksBatcher.endBatch();
		}
		private class Spark extends DynamicGameObject{
			float animationTime;
			public Spark(float x, float y, OuyaGame game) {
				super(x, y, SparkAttackView.RADIUS/2);
				float direction = (Utils.getRandomFloat() * 360f) * Vector2.TO_RADIANS;
				velocity.set(new Vector2(-FloatMath.sin(direction)*7.5f, FloatMath.cos(direction)*7.5f));
			}
			public void update(float deltaTime){
				animationTime += deltaTime;
				setPosition(getPosition().x + velocity.x * deltaTime*Utils.FPS, getPosition().y + velocity.y * deltaTime*Utils.FPS);
			}
		}
	}
	
	private class HellfireMissles{
		public float bossDamage = 20;
		public static final int missleCountCap = 20;
		public float damage = 200f;
		private int missleCount;
		float directions[];
		List<Missle> missles;
		float animationTime;
		float timerFireNewMissleCD = 3/missleCountCap;
		float timerFireNewMissle = timerFireNewMissleCD + 1;
		public HellfireMissles(float x, float y, OuyaGame game) {
			missles = new LinkedList<Missle>();
			directions = new float[missleCountCap];
			for(int i=0; i<missleCountCap; i++){
				directions[i] = (Carry.this.angle+(i*360/missleCountCap)) * Vector2.TO_RADIANS;
			}
		}
		public boolean isNotEmpty() {
			return missles.size()>0;
		}
		public boolean isAllOutOfScreen() {
			Iterator<Missle> iter = missles.iterator();
			Missle f;
			while(iter.hasNext()){
				f = iter.next();
				if(Utils.isOutOfBoundsCompletely((Rectangle)f.bounds, screen.camera.getBounds())){
					iter.remove();
				}
			}
			return missles.size() == 0;
		}
		public boolean isColliding(Minion m) {
			boolean result = false;
			Iterator<Missle> iterM = missles.iterator();
			Missle mis;
			while(iterM.hasNext()){
				mis = iterM.next();
				if(mis.isColliding(m)){
					iterM.remove();
					return true;
				}
			}
			return result;
		}
		public void update(float deltaTime){
			animationTime += deltaTime;
			if(timerFireNewMissle >= timerFireNewMissleCD && missleCount < missleCountCap){
				Missle m = new Missle(getPosition().x, getPosition().y, directions[missleCount], game);
				m.velocity.set(-FloatMath.sin(directions[missleCount])*17, FloatMath.cos(directions[missleCount])*17);			
				missles.add(m);
				missleCount++;
				timerFireNewMissle = 0;
				clusterMisslesSound.play(0.5f);
			} else {
				timerFireNewMissle += deltaTime;
			}
			
			Iterator<Missle> iter = missles.iterator();
			Missle f;
			while(iter.hasNext()){
				f = iter.next();
				if(Utils.isOutOfBoundsCompletely((Rectangle)f.bounds, screen.camera.getBounds())){
					iter.remove();
				} else {
					f.update(deltaTime);
				}
			}
		}
		public void updatePositionWithParallaxBackground(float deltaTime) {
			for(Missle f : missles){
				f.setPosition(f.getPosition().x, f.getPosition().y + screen.PARALLAX_SPEED*deltaTime);
			}
		}
		public void present(float deltaTime){
			hellfireMisslesBatcher.beginBatch(clusterMisslesView.texture);
			for(Missle f : missles){
				hellfireMisslesBatcher.drawSprite(f.getPosition().x, f.getPosition().y, ClusterMisslesView.WIDTH, ClusterMisslesView.HEIGHT, f.angle, clusterMisslesView.animation.getKeyFrame(animationTime, Animation.ANIMATION_LOOPING));
			}
			hellfireMisslesBatcher.endBatch();
		}
		private class Missle extends DynamicGameObject{
			float angle;
			public Missle(float x, float y, float angle, OuyaGame game) {
				super(x, y, ClusterMisslesView.WIDTH, ClusterMisslesView.HEIGHT);
				this.angle = angle*Vector2.TO_DEGREES;
			}
			
			//FIXME oh man... you should really override the isColliding() method, cus this is a rectangle...
			
			public void update(float deltaTime){
				setPosition(getPosition().x + velocity.x * deltaTime*Utils.FPS, getPosition().y + velocity.y * deltaTime*Utils.FPS);
			}
		}
	}
	
	// =========================  View  ============================
	public class CarryView extends HeroView {
		public CarryView(OuyaGame game, int playerNumber) {
			super(game, playerNumber);
			textureShip = new Texture(game, "heros/carry/carry.png");
			regionShip = new TextureRegion(textureShip, 0, 0, HeroView.RADIUS*2, HeroView.RADIUS*2);
		}
	}
}
