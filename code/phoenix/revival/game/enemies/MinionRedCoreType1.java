public class MinionRedCoreType1 extends Minion {
	public static final int MAX_NUM_BULLETS = 20;

	public MinionRedCoreType1(float x, float y, OuyaGame game, OuyaScreen screen) {
		super(x, y, MinionRedCoreType1View.RADIUS*0.8f, game, screen);
		if(Utils.isSinglePlayer){ 
			baseHp = 70;
			baseCollisionDamage = 30;
			baseAttackDamage = 8;
		} else {
			baseHp = 120;
			baseCollisionDamage = 60;
			baseAttackDamage = 15;
		}
		attackDmg = getAttackDamageByBaseScreenLevel(screen.screenLevel);
		firingTimerCD = 3f;
		maxHp = getHpByBaseScreenLevel(screen.screenLevel);
		currentHp = maxHp;
		shouldDieUponShipCollision = true;
		collisionDamage = getCollisionDamageByBaseScreenLevel(screen.screenLevel);
		dropExperience = 10;
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
		if(!isDisabled && !isFrosted && !isHeld){
			if(firingTimer >= firingTimerCD){
				MinionBullet mb = new MinionBullet(getPosition().x, getPosition().y, attackDmg);
				mb.velocity.x = bulletVelocity.x;
				mb.velocity.y = bulletVelocity.y;
				screen.minionBullets.add(mb);
				firingTimer = 0;
			} else {
				firingTimer += deltaTime;
			}
		}
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
	
	public static class MinionRedCoreType1View extends MinionView {
		public static final float RADIUS = 64f;
		public MinionRedCoreType1View(OuyaGame game) {
			super(game, "minions/red_core_type_1.png", 14, 128, 128, 512, 0.1f);
		}
		@Override public float getWidth(){return RADIUS*2;}
		@Override public float getHeight(){return RADIUS*2;}
	}

}
