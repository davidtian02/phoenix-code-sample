public class MinionQuasar extends Minion {
	public static final float VELOCITY_Y_DOWNWARDS_SLOW = -2f;
	public boolean shouldExplodeIntoNeutroStar = false;
	private float animationTimeLimit;
	
	public MinionQuasar(float x, float y, OuyaGame game, OuyaScreen screen) {
		super(x, y, MinionQuasarView.RADIUS/2, game, screen);
		if(Utils.isSinglePlayer){
			baseHp = 99999f;
			baseCollisionDamage = 900;
		} else {
			baseHp = 99999f;
			baseCollisionDamage = 1800;
		}
		maxHp = getHpByBaseScreenLevel(screen.screenLevel);
		currentHp = maxHp;
		collisionDamage = getCollisionDamageByBaseScreenLevel(screen.screenLevel);
		dropExperience = 0;
		animationTimeLimit = Utils.getRandomInt(5) + 2f;
	}

	@Override
	protected void updateBuffsAndAuras(float deltaTime) {

	}

	@Override
	protected void updateMovementAndRotation(float deltaTime) {
		animationTime += deltaTime;
		setPosition(getPosition().x + velocity.x * deltaTime * Utils.FPS, getPosition().y + velocity.y * deltaTime * Utils.FPS);
		if(animationTime >= animationTimeLimit){
			shouldExplodeIntoNeutroStar = true;
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
	
	public static class MinionQuasarView extends MinionView {
		public static final float RADIUS = 256f;
		public MinionQuasarView(OuyaGame game) {
			super(game, "quasar.png", 13, 512, 512, 2048, 0.1f);
		}
		@Override public float getWidth(){return RADIUS*9;}
		@Override public float getHeight(){return RADIUS*6;}
	}

}
