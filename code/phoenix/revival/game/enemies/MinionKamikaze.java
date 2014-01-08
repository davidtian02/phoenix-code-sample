public class MinionKamikaze extends Minion {
	
	public MinionKamikaze(float x, float y, OuyaGame game, OuyaScreen screen) {
		super(x, y, MinionKamikazeView.RADIUS*0.8f, game, screen);
		if(Utils.isSinglePlayer){ 
			baseHp = 40;
			baseCollisionDamage = 30;
		} else {
			baseHp = 80;
			baseCollisionDamage = 45;
		}
		maxHp = getHpByBaseScreenLevel(screen.screenLevel);
		currentHp = maxHp;
		shouldDieUponShipCollision = true;
		collisionDamage = getCollisionDamageByBaseScreenLevel(screen.screenLevel);
		dropExperience = 4;
		animationTime = Utils.getRandomFloat() * 10f;
	}

	@Override
	protected void updateBuffsAndAuras(float deltaTime) {
		
	}

	@Override
	protected void updateMovementAndRotation(float deltaTime) {
		if(!isDisabled && !isFrosted && !isHeld){
			setPosition(getPosition().x + deltaTime * velocity.x, getPosition().y + deltaTime*velocity.y);
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
	
	// =========================  other public methods ============================
	
	
	// =========================  View ============================
	
	public static class MinionKamikazeView extends MinionView {
		public static final float RADIUS = 64f;
		public MinionKamikazeView(OuyaGame game) {
			super(game, "minions/kamikaze.png", 7, 128, 128, 512, 0.1f);
		}
		@Override public float getWidth(){return RADIUS*2;}
		@Override public float getHeight(){return RADIUS*2;}
	}

}
