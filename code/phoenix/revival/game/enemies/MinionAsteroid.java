public class MinionAsteroid extends Minion {
	public static final float VELOCITY_Y_DOWNWARDS_SLOW = -2f;
	
	public boolean shouldRotate;
	
	public MinionAsteroid(float x, float y, OuyaGame game, OuyaScreen screen) {
		super(x, y, MinionAsteroidView.RADIUS, game, screen);
		if(Utils.isSinglePlayer){
			baseHp = 100;
			baseCollisionDamage = 300;
		} else {
			baseHp = 200;
			baseCollisionDamage = 600;
		}
		maxHp = getHpByBaseScreenLevel(screen.screenLevel);
		currentHp = maxHp;
		shouldDieUponShipCollision = true;
		collisionDamage = getCollisionDamageByBaseScreenLevel(screen.screenLevel);
		dropExperience = 0;
		animationTime = Utils.getRandomFloat() * 10f;
	}

	@Override
	protected void updateBuffsAndAuras(float deltaTime) {

	}

	@Override
	protected void updateMovementAndRotation(float deltaTime) {
		setPosition(getPosition().x + deltaTime * velocity.x *Utils.FPS, getPosition().y + deltaTime*velocity.y*Utils.FPS);
		if(shouldRotate){
			animationTime += deltaTime;
		}
	}

	@Override
	protected void updateShooting(float deltaTime) {

	}

	@Override
	protected void checkCastSpells(float deltaTime) {

	}

	@Override
	protected void updateSpellEffectsAndTimer(float deltaTime) {

	}

	@Override
	protected void updateHpManaAndSpellCdTimer(float deltaTime) {

	}

	// =========================  View ============================	
	public static class MinionAsteroidView extends MinionView {
		public static final float RADIUS = 128f;
		public MinionAsteroidView(OuyaGame game) {
			super(game, "asteroid.png", 16, 256, 256, 1024, 0.2f);
		}
		@Override public float getWidth(){return RADIUS*4;}
		@Override public float getHeight(){return RADIUS*4;}
	}

	
}
