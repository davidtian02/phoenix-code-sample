public class MinionNeutronStar extends Minion {

	public MinionNeutronStar(float x, float y, OuyaGame game, OuyaScreen screen) {
		super(x, y, MinionNeutronStarView.RADIUS/2, game, screen);
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
	}

	@Override
	protected void updateBuffsAndAuras(float deltaTime) {

	}

	@Override
	protected void updateMovementAndRotation(float deltaTime) {
		animationTime += deltaTime;
		setPosition(getPosition().x + velocity.x * deltaTime * Utils.FPS, getPosition().y + velocity.y * deltaTime * Utils.FPS);
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
	
	public static class MinionNeutronStarView extends MinionView {
		public static final float RADIUS = 256f;
		public MinionNeutronStarView(OuyaGame game) {
			super(game, "neutron_star.png", 6, 512, 512, 2048, 0.1f);
		}
		@Override public float getWidth(){return RADIUS*4;}
		@Override public float getHeight(){return RADIUS*4;}
	}
}
