public class MinionRedCoreType2 extends Minion {
	boolean isFiringChainBullets;
	float firingActivatedTimer;
	float firingActivatedTimerCD = 1f;
	float firingInterval;
	float firingIntervalCD = 0.2f;
	
	float angleOfFire;
	float stopPositionX = 0f;
	float stopPositionY = 0f;
	boolean shouldStopMoving;
	
	public MinionRedCoreType2(float x, float y, OuyaGame game, OuyaScreen screen) {
		super(x, y, MinionRedCoreType2View.WIDTH*0.8f, MinionRedCoreType2View.HEIGHT*0.8f, game, screen);
		if(Utils.isSinglePlayer){ 
			baseHp = 100;
			baseCollisionDamage = 1f;
			baseAttackDamage = 3;
		} else {
			baseHp = 200;
			baseCollisionDamage = 2f;
			baseAttackDamage = 7;
		}
		
		attackDmg = getAttackDamageByBaseScreenLevel(screen.screenLevel);
		firingTimerCD = 2;
		maxHp = getHpByBaseScreenLevel(screen.screenLevel);
		currentHp = maxHp;
		collisionDamage = getCollisionDamageByBaseScreenLevel(screen.screenLevel); //these don't explode
		dropExperience = 30;
		animationTime = Utils.getRandomFloat() * 10f;
		angleOfFire = 0;
	}

	@Override
	protected void updateBuffsAndAuras(float deltaTime) {
		
	}
	
	public void setStopAtX(float positionX){
		stopPositionX = positionX;
	}
	public void setStopAtY(float positionY) {
		stopPositionY = positionY;
	}
	
	@Override
	protected void updateMovementAndRotation(float deltaTime) {
		if(!isDisabled && !isFrosted && !isHeld){
			animationTime += deltaTime;
			if(shouldStopMoving){
				return;
			}
			if(stopPositionX > 1f){
				if(screen.camera.getPosition().x > stopPositionX){
					//supposed to be going right and then stopping
					if(getPosition().x > stopPositionX){
						shouldStopMoving = true;
					}
				} else {
					if(getPosition().x < stopPositionX){
						shouldStopMoving = true;
					}
				}
			}
			if(stopPositionY > 1f){
				if(screen.camera.getPosition().y > stopPositionY + screen.camera.getPosition().y-Utils.WORLD_HEIGHT/2){
					//supposed to be going up and then stopping
					if(getPosition().y > stopPositionY + screen.camera.getPosition().y-Utils.WORLD_HEIGHT/2){
						shouldStopMoving = true;
					}
				} else {
					if(getPosition().y < stopPositionY + screen.camera.getPosition().y-Utils.WORLD_HEIGHT/2){
						shouldStopMoving = true;
					}
				}
			}
			setPosition(getPosition().x + deltaTime*velocity.x, getPosition().y + deltaTime*velocity.y);
		}
	}

	@Override
	protected void updateShooting(float deltaTime) {
		if(!isDisabled && !isFrosted && !isHeld){
			if(firingTimer >= firingTimerCD){
				isFiringChainBullets = true;
				int randTarget = Utils.getRandomInt(screen.herosList.size());
				angleOfFire = screen.herosList.get(randTarget).getPosition().cpy().sub(getPosition()).angle();
				firingTimer = 0;
			} else {
				firingTimer += deltaTime;
			}
		
			if(isFiringChainBullets){
				if(firingActivatedTimer > firingActivatedTimerCD){
					isFiringChainBullets = false;
					firingActivatedTimer = 0;
				} else {
					if(firingInterval > firingIntervalCD){
						firingInterval = 0;
						fireBullet();
					} else {
						firingInterval += deltaTime;
					}
					firingActivatedTimer += deltaTime;
				}
			}
		}
	}
	
	private void fireBullet(){
		MinionBullet mbLeftSide = new MinionBullet(getPosition().x-100, getPosition().y, attackDmg);
		MinionBullet mbRightSide = new MinionBullet(getPosition().x+100, getPosition().y, attackDmg);
		mbLeftSide.velocity.x = 10f*Utils.FPS * FloatMath.cos(angleOfFire*Vector2.TO_RADIANS);
		mbLeftSide.velocity.y = 10f*Utils.FPS * FloatMath.sin(angleOfFire*Vector2.TO_RADIANS);
		mbRightSide.velocity.x = mbLeftSide.velocity.x;
		mbRightSide.velocity.y = mbLeftSide.velocity.y;
		screen.minionBullets.add(mbLeftSide);
		screen.minionBullets.add(mbRightSide);
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
	
	public static class MinionRedCoreType2View extends MinionView {
		public static final float WIDTH = 256f;
		public static final float HEIGHT = 128f;
		public MinionRedCoreType2View(OuyaGame game) {
			super(game, "minions/red_core_type_2.png", 14, 256, 128, 1024, 512, 0.1f);
		}
		@Override
		public float getWidth(){
			return WIDTH;
		}
		@Override
		public float getHeight(){
			return HEIGHT;
		}
	}

}
