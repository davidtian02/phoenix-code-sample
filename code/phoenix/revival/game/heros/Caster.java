public class Caster extends Hero {

	Emp emp;
	GravityBeam gravityBeam;
	FrostScreen frostScreen;
	GrandFireballs grandFireballs;
	
	//view stuff
	EmpView empView;
	SpriteBatcher empBatcher;
	GravityBeamCenterView gravityBeamCenterView;
	GravityBeamEndView gravityBeamEndView;
	SpriteBatcher gravityBeamBatcher;
	FrostScreenView frostScreenView;
	SpriteBatcher frostScreenBatcher;
	GrandFireballView grandFireballView;
	SpriteBatcher grandFireballsBatcher;
	
	//sound stuff
	Sound empSound;
	Sound gravityBeamSound;
	Sound frostScreenSound;
	Sound grandFireballsSound;
	
	float spell4InvincibleDuration = 2.1f; 
	float spell4InvincibleDurationCD = 2f;
	
	public Caster(float x, float y, float radius, OuyaGame game, OuyaScreen screen, int playerNumber) {
		super(x, y, radius, game, screen, playerNumber);
		view = new CasterView(game, playerNumber);
		
		//base stats
		baseHp = 290;
		baseMp = 370;
		baseHpRegen = 1.2f;
		baseMpRegen = 3.0f;
		baseAttackDmg = 15f;
		baseAttackSpeed = 4.5f;
		baseMovementSpeed = 13f;
		
		//emp
		spell1MpCost = 80;
		spell1CdTimer = spell1Cd = 8f;
		spell1DurationTimer = spell1Duration = 0.25f;

		spell2MpCost = 80;
		spell2CdTimer = spell2Cd = 8f;
		spell2DurationTimer = spell2Duration = 3f;

		spell3MpCost = 80;
		spell3CdTimer = spell3Cd = 8f;
		spell3DurationTimer = spell3Duration = 0.25f;

		spell4MpCost = 240;
		spell4CdTimer = spell4Cd = 15f;
		
		empView = new EmpView(game);
		empBatcher = new SpriteBatcher(game.getGLGraphics(), 1);
		gravityBeamCenterView = new GravityBeamCenterView(game);
		gravityBeamEndView = new GravityBeamEndView(game);
		gravityBeamBatcher = new SpriteBatcher(game.getGLGraphics(), GravityBeam.beamCountMax );
		frostScreenView = new FrostScreenView(game);
		frostScreenBatcher = new SpriteBatcher(game.getGLGraphics(), 1);
		grandFireballView = new GrandFireballView(game);
		grandFireballsBatcher = new SpriteBatcher(game.getGLGraphics(), GrandFireballs.fireballCount);
		
		//sounds
		empSound = game.getAudio().newSound("sound/caster/emp.ogg");
		gravityBeam = game.getAudio().newSound("sound/caster/gravity_beam.ogg");
		frostScreenSound = game.getAudio().newSound("sound/caster/frost_screen.ogg");
		grandFireballsSound = game.getAudio().newSound("sound/caster/grand_fireballs.ogg");
	}

	@Override
	protected void updateSpellEffectsAndTimer(float deltaTime) {
		if(spell1DurationTimer < spell1Duration){
			spell1DurationTimer += deltaTime;
			emp.update(deltaTime);
		} else {
			emp = null;
		}
		
		if(spell2DurationTimer < spell2Duration){
			spell2DurationTimer += deltaTime;
			gravityBeam.update(deltaTime);
		} else {
			gravityBeam = null;
		}
		
		if(spell3DurationTimer < spell3Duration){
			spell3DurationTimer += deltaTime;
			frostScreen.update(deltaTime);
		} else {
			frostScreen = null;
		}
		
		if(grandFireballs != null){
			if(spell4InvincibleDuration <= spell4InvincibleDurationCD){
				spell4InvincibleDuration += deltaTime;
			}
			if(grandFireballs.isAllOutOfScreen()){
				grandFireballs = null;
			} else {
				grandFireballs.update(deltaTime);
			}
		} else {
			
		}
		
		super.updateSpellEffectsAndTimer(deltaTime);
	}
	
	@Override
	public boolean takeDamageAndCheckDies(float damage){
		//note: this is assuming straight up damage, and no armor
		boolean isDead = false;
		if(damage>0){ //actually got hit
			MusicAndSound.playSoundHeroGettingHit();
		}
		if(instaGuard != null){
			damage = instaGuard.takeDamage(damage);
		}
		
		if(spell4InvincibleDuration < spell4InvincibleDurationCD){
			damage = 0f;
		}
		currentHP = currentHP - damage;
		if(currentHP <= 0){
			// game over at this point
			currentHP = 0;
			isDead = true;
		}
		return isDead;
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
				if(emp != null){
					if(m.isColliding(emp.toDynamicBounds())){
						if(m.takeDamageAndCheckDies(emp.damage)){
							screen.explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, view.game));
							gainExp(m.dropExperience);
							iterM.remove();
							continue;
						}						 
					}
				}
				
				m.isHeld = false;
				if(gravityBeam != null){
					boolean isWithinBounds = screen.isWithinBounds(m);
					if(isWithinBounds){
						if(gravityBeam.alreadyContains(m)){
							m.isHeld = true;
							if(m.takeDamageAndCheckDies(gravityBeam.damage)){
								screen.explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, view.game));
								gainExp(m.dropExperience);
								gravityBeam.removeMinion(m);
								iterM.remove();
								continue;
							}
						} else {
							if(gravityBeam.addMinion(m)){
								m.isHeld = true;
							} else {
								m.isHeld = false;
							}
						}
					} else {
						gravityBeam.removeMinion(m);
					}
				}
				
				if(frostScreen != null){
					if(m.isColliding(frostScreen.toDynamicBounds())){
						m.isFrosted = true;
						if(m.takeDamageAndCheckDies(frostScreen.damage )){
							screen.explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, view.game));
							gainExp(m.dropExperience);
							iterM.remove();
							continue;
						}
					}
				}
				 
				if(grandFireballs != null){
					if(grandFireballs.isColliding(m)){
						if(m.takeDamageAndCheckDies(grandFireballs.damage)){
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
			if(emp != null){
				if(mb.isColliding(emp.toDynamicBounds())){
					iterMB.remove();
					continue;
				}
			}
			
			if(grandFireballs != null){
				if(grandFireballs.isColliding(mb)){
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
			if(emp != null){
				if(emp.isColliding(m)){
					screen.boss.takeDamageAndCheckDies(emp.damage);		 
				}
			}
			
			if(gravityBeam != null){
				if(screen.isWithinBounds(m)){
					if(!gravityBeam.alreadyContains(m)){
						gravityBeam.addMinion(m);
					} else {
						screen.boss.takeDamageAndCheckDies(gravityBeam.bossDamage);
					}
				} else {
					gravityBeam.removeMinion(m);
				}
			}
			
			if(frostScreen != null){
				if(frostScreen.isColliding(m)){
					screen.boss.takeDamageAndCheckDies(frostScreen.damage);		 
				}
			}
			
			if(grandFireballs != null){
				if(grandFireballs.isColliding(m)){
					screen.boss.takeDamageAndCheckDies(grandFireballs.bossDamage);		 
				}
			}
		}
	}
	
	@Override
	protected void updatePositionWithParallaxBackground(float deltaTime){
		super.updatePositionWithParallaxBackground(deltaTime);
		if(emp != null){
			emp.updatePositionWithParallaxBackground(deltaTime);
		}
		if(gravityBeam != null){
			gravityBeam.updatePositionWithParallaxBackground(deltaTime);
		}
		if(frostScreen != null){
			frostScreen.updatePositionWithParallaxBackground(deltaTime);
		}
		if(grandFireballs != null){
			grandFireballs.updatePositionWithParallaxBackground(deltaTime);
		}
	}
	
	@Override
	protected void castSpell1() {
		emp = new Emp(getPosition().x, getPosition().y, EmpView.RADIUS, game);
		empSound.play(1f);
	}

	@Override
	protected void castSpell2() {
		gravityBeam = new GravityBeam(getPosition().x, getPosition().y, game);
		gravityBeamSound.play(1f);
	}

	@Override
	protected void castSpell3() {
		frostScreen = new FrostScreen(getPosition().x, getPosition().y, game);
		frostScreenSound.play(1f);
	}

	@Override
	protected void castSpell4() {
		spell4InvincibleDuration = 0f;
		grandFireballs = new GrandFireballs(getPosition().x, getPosition().y, game);
		grandFireballsSound.play(1f);
	}
	
	@Override
	public void present(float deltaTime){
		if(emp != null){
			emp.present(deltaTime);
		}
		if(gravityBeam != null){
			gravityBeam.present(deltaTime);
		}
		if(frostScreen != null){
			frostScreen.present(deltaTime);
		}
		if(grandFireballs != null){
			grandFireballs.present(deltaTime);
		}
		super.present(deltaTime);
	}
	
	// =========================  Spells  ============================
	
	private class Emp extends DynamicGameObject{
		public float damage = 2f; //per second? TODO
		float animationTime;
		public Emp(float x, float y, float radius, OuyaGame game) {
			super(x, y, EmpView.RADIUS); //collision range has to be slightly bigger
		}
		public void update(float deltaTime){
			animationTime += deltaTime;
		}
		public void updatePositionWithParallaxBackground(float deltaTime) {
			setPosition(getPosition().x, getPosition().y + screen.PARALLAX_SPEED*deltaTime);
		}
		public DynamicGameObject toDynamicBounds() {
			return new EmpShellBounds(getPosition().x, getPosition().y, EmpView.RADIUS * EmpView.VIEW_MAGNIFIER_FACTOR *animationTime*15);
		}
		public void present(float deltaTime){
			empBatcher.beginBatch(empView.texture);
			empBatcher.drawSprite(getPosition().x, getPosition().y, EmpView.RADIUS *2 * EmpView.VIEW_MAGNIFIER_FACTOR *animationTime*15, EmpView.RADIUS *2 * EmpView.VIEW_MAGNIFIER_FACTOR *animationTime*15, empView.animation.getKeyFrame(animationTime, Animation.ANIMATION_NONLOOPING));
			empBatcher.endBatch();
		}
		private class EmpShellBounds extends DynamicGameObject{
			public EmpShellBounds(float x, float y, float radius) {
				super(x, y, radius);
			}
		}
	}
	private class EmpView extends HeroSpellView{
		public static final float VIEW_MAGNIFIER_FACTOR = 2f;
		public static final float RADIUS = 128;
		public EmpView(OuyaGame game) {
			super(game, "heros/caster/caster_spell_1_emp.png", 7, 4, 2*(int)RADIUS, 2*(int)RADIUS, 0.035f);
		}
	}
	
	private class GravityBeam extends DynamicGameObject{
		public static final int beamCountMax = 15;
		float animationTime;
		public float bossDamage = 3;
		public float damage = 2.5f;
		List<Minion> captives;
		public GravityBeam(float x, float y, OuyaGame game){
			super(x,y,0);
			captives = new LinkedList<Minion>();
		}
		
		public void removeMinion(Minion m) {
			captives.remove(m);
		}
		public boolean alreadyContains(Minion m) {
			return captives.contains(m);
		}
		public boolean addMinion(Minion m) {
			if(captives.size() < beamCountMax){
				captives.add(m);
				return true;
			}
			return false;
		}
		public void update(float deltaTime) {
			animationTime += deltaTime;
		}
		public void present(float deltaTime){
			Iterator<Minion> iterM = captives.iterator();
			Minion m;
			while(iterM.hasNext()){
				m = iterM.next();
				if(m.currentHp <= 0){
					iterM.remove();
					continue;
				}
				float angle = m.getPosition().cpy().sub(Caster.this.getPosition()).angle();
				float reverseAngle = Caster.this.getPosition().cpy().sub(m.getPosition()).angle();
				
				//first draw on top of self
				gravityBeamBatcher.beginBatch(gravityBeamEndView.texture);
				gravityBeamBatcher.drawSprite(Caster.this.getPosition().x, Caster.this.getPosition().y, GravityBeamEndView.RADIUS, GravityBeamEndView.RADIUS, angle, gravityBeamEndView.animation.getKeyFrame(animationTime, Animation.ANIMATION_LOOPING));
				gravityBeamBatcher.endBatch();
				
				//then draw in mid
				gravityBeamBatcher.beginBatch(gravityBeamCenterView.texture);
				gravityBeamBatcher.drawSprite((Caster.this.getPosition().x+m.getPosition().x)/2, (Caster.this.getPosition().y+m.getPosition().y)/2, Math.abs(m.getPosition().dist(Caster.this.getPosition())), GravityBeamEndView.RADIUS, angle, gravityBeamCenterView.animation.getKeyFrame(animationTime, Animation.ANIMATION_LOOPING));
				gravityBeamBatcher.endBatch();
				
				//then draw on top of others
				gravityBeamBatcher.beginBatch(gravityBeamEndView.texture);
				gravityBeamBatcher.drawSprite(m.getPosition().x, m.getPosition().y, GravityBeamEndView.RADIUS, GravityBeamEndView.RADIUS, reverseAngle, gravityBeamEndView.animation.getKeyFrame(animationTime, Animation.ANIMATION_LOOPING));
				gravityBeamBatcher.endBatch();
			}
		}
		public void updatePositionWithParallaxBackground(float deltaTime) {
			setPosition(getPosition().x, getPosition().y + deltaTime*screen.PARALLAX_SPEED);
		}
	}
	private class GravityBeamCenterView extends HeroSpellView{
		public static final float RADIUS = 128f;
		public GravityBeamCenterView(OuyaGame game) {
			super(game, "heros/caster/caster_spell_2_gravity_beam_center.png", 4, 4, 2*(int)RADIUS, 2*(int)RADIUS, 0.1f);
		}
	}
	private class GravityBeamEndView extends HeroSpellView{
		public static final float RADIUS = 128f;
		public GravityBeamEndView(OuyaGame game) {
			super(game, "heros/caster/caster_spell_2_gravity_beam_end.png", 4, 4, 2*(int)RADIUS, 2*(int)RADIUS, 0.1f);
		}
	}
	
	private class FrostScreen extends DynamicGameObject{
		public float damage = 7f;
		float animationTime;
		public FrostScreen(float x, float y, OuyaGame game) {
			super(x, y, FrostScreenView.RADIUS);
		}
		public void update(float deltaTime){
			animationTime += deltaTime;
		}
		public void updatePositionWithParallaxBackground(float deltaTime) {
			setPosition(getPosition().x, getPosition().y + screen.PARALLAX_SPEED*deltaTime);
		}
		public DynamicGameObject toDynamicBounds() {
			return new FrostScreenShellBounds(getPosition().x, getPosition().y, FrostScreenView.RADIUS * FrostScreenView.VIEW_MAGNIFIER_FACTOR);
		}
		public void present(float deltaTime){
			frostScreenBatcher.beginBatch(frostScreenView.texture);
			frostScreenBatcher.drawSprite(getPosition().x, getPosition().y, FrostScreenView.RADIUS *2 * FrostScreenView.VIEW_MAGNIFIER_FACTOR, FrostScreenView.RADIUS *2 * FrostScreenView.VIEW_MAGNIFIER_FACTOR, frostScreenView.animation.getKeyFrame(animationTime, Animation.ANIMATION_NONLOOPING));
			frostScreenBatcher.endBatch();
		}
		private class FrostScreenShellBounds extends DynamicGameObject{
			public FrostScreenShellBounds(float x, float y, float radius) {
				super(x, y, radius);
			}
		}
	}
	private class FrostScreenView extends HeroSpellView{
		public static final float VIEW_MAGNIFIER_FACTOR = 4f;
		public static final float RADIUS = 256;
		public FrostScreenView(OuyaGame game) {
			super(game, "heros/caster/caster_spell_3_frost_screen.png", 8, 4, 2*(int)RADIUS, 2*(int)RADIUS, 0.035f);
		}
	}
	
	private class GrandFireballs{
		public float bossDamage=2.5f;
		public float damage = 10f;
		public static final int fireballCount = 10;
		List<GrandFireball> fireballs;
		float animationTime;
		public GrandFireballs(float x, float y, OuyaGame game) {
			fireballs = new LinkedList<GrandFireball>(); 
			for(int i=0; i<fireballCount; i++){
				fireballs.add(new GrandFireball(x, y, (i)*(360/fireballCount) + Caster.this.angle, game));
			}
		}
		public boolean isColliding(DynamicGameObject m) {
			boolean result = false;
			for(GrandFireball f : fireballs){
				if(f.isColliding(m)){
					result = true;
				}
			}
			return result;
		}
		public boolean isAllOutOfScreen() {
			Iterator<GrandFireball> iter = fireballs.iterator();
			GrandFireball f;
			while(iter.hasNext()){
				f = iter.next();
				if(Utils.isOutOfBoundsCompletely((Circle)f.bounds, screen.camera.getBounds())){
					iter.remove();
				}
			}
			return fireballs.size() == 0;
		}
		public void update(float deltaTime){
			animationTime += deltaTime;
			for(GrandFireball f : fireballs){
				f.update(deltaTime);
			}
		}
		public void updatePositionWithParallaxBackground(float deltaTime) {
			for(GrandFireball f : fireballs){
				f.setPosition(f.getPosition().x, f.getPosition().y + screen.PARALLAX_SPEED*deltaTime);
			}
		}
		public void present(float deltaTime){
			grandFireballsBatcher.beginBatch(grandFireballView.texture);
			for(GrandFireball f : fireballs){
				grandFireballsBatcher.drawSprite(f.getPosition().x, f.getPosition().y, GrandFireballView.RADIUS *2 * GrandFireballView.VIEW_MAGNIFIER_FACTOR, GrandFireballView.RADIUS *2 * GrandFireballView.VIEW_MAGNIFIER_FACTOR, f.angle, grandFireballView.animation.getKeyFrame(animationTime, Animation.ANIMATION_LOOPING));
			}
			grandFireballsBatcher.endBatch();
		}
		private class GrandFireball extends DynamicGameObject{
			float angle;
			public GrandFireball(float x, float y, float angle, OuyaGame game) {
				super(x, y, GrandFireballView.RADIUS);
				this.angle = angle;
				float radians = angle * Vector2.TO_RADIANS;
				velocity.set(new Vector2(-FloatMath.sin(radians)*300f, FloatMath.cos(radians)*300f));
			}
			public void update(float deltaTime){
				setPosition(getPosition().x + velocity.x * deltaTime, getPosition().y + velocity.y * deltaTime);
			}
		}
	}
	private class GrandFireballView extends HeroSpellView{
		public static final float VIEW_MAGNIFIER_FACTOR = 2f;
		public static final float RADIUS = 128;
		public GrandFireballView(OuyaGame game) {
			super(game, "heros/caster/caster_spell_4_grand_fireballs.png", 3, 4, 2*(int)RADIUS, 2*(int)RADIUS, 0.1f);
		}
	}
	// =========================  View  ============================
	
	public class CasterView extends HeroView {
		public CasterView(OuyaGame game, int playerNumber) {
			super(game, playerNumber);
			textureShip = new Texture(game, "heros/caster/caster.png");
			regionShip = new TextureRegion(textureShip, 0, 0, HeroView.RADIUS*2, HeroView.RADIUS*2);
		}
	}
}
