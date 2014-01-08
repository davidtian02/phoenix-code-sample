public abstract class Minion extends DynamicGameObject {
	
	OuyaGame game;
	OuyaScreen screen;
	Vector2 bulletVelocity;
	float firingTimer;
	public float firingTimerCD;
	
	public float baseHp;
	public float baseAttackDamage;
	public float baseCollisionDamage;
	public float baseMultiplier = 1.45f;
	
	public float collisionDamage;
	public float dropExperience;
	public float animationTime;
	public float currentHp;
	public float maxHp;
	public float attackDmg;

	public boolean isDisabled;
	public boolean isFrosted;
	public boolean isHeld;
	public boolean shouldDieUponShipCollision;
	
	Sound gettingDamagedSound;
	Sound notGettingDamagedSound;

	public Minion(float x, float y, float radius, OuyaGame game, OuyaScreen screen) {
		super(x, y, radius);
		this.game = game;
		this.screen = screen;
		bulletVelocity = new Vector2();
	}
	
	public Minion(float x, float y, float width, float height, OuyaGame game, OuyaScreen screen) {
		super(x, y, width, height);
		this.game = game;
		this.screen = screen;
		bulletVelocity = new Vector2();
	}
	
	public void updateAnimationOnly(float deltaTime){
		animationTime += deltaTime;
	}
	
	public void update(float deltaTime){
		updateBuffsAndAuras(deltaTime);
		updateMovementAndRotation(deltaTime);
		updateShooting(deltaTime);
		checkCastSpells(deltaTime);
		updateSpellEffectsAndTimer(deltaTime);
		updateHpManaAndSpellCdTimer(deltaTime);
	}
	
	protected abstract void updateBuffsAndAuras(float deltaTime);
	protected abstract void updateMovementAndRotation(float deltaTime);
	protected abstract void updateShooting(float deltaTime);
	protected abstract void checkCastSpells(float deltaTime);
	protected abstract void updateSpellEffectsAndTimer(float deltaTime);
	protected abstract void updateHpManaAndSpellCdTimer(float deltaTime);
	public void updatePositionWithParallaxBackground(float deltaTime, float parallaxSpeed){
		setPosition(getPosition().x, getPosition().y + parallaxSpeed*deltaTime);
	}
	
	public void present(float deltaTime, MinionView minionView, HeroEnemyEffectsStatusView statusView, SpriteBatcher batcher){
		batcher.beginBatch(minionView.getTexture());
		batcher.drawSprite(getPosition().x, getPosition().y, minionView.getWidth(), minionView.getHeight(), minionView.getAnimation().getKeyFrame(animationTime, Animation.ANIMATION_LOOPING));
		batcher.endBatch();
		
		if(isFrosted){
			batcher.beginBatch(statusView.getTextureFrosted());
			batcher.drawSprite(getPosition().x, getPosition().y, minionView.getWidth(), minionView.getHeight(), statusView.getTextureRegionFrosted());
			batcher.endBatch();
		}
		
		batcher.beginBatch(screen.heroEnemyStatusView.texture);
		batcher.drawSprite(getPosition().x, getPosition().y + minionView.getHeight()/2,  minionView.getWidth()*(currentHp/maxHp),  10, screen.heroEnemyStatusView.regionHealthBar);
		batcher.endBatch();
	}

	public boolean takeDamageAndCheckDies(float damage){
		boolean isDead = false;
		if(damage > 0){
			MusicAndSound.playSoundEnemyGettingHitDamaged(); //TODO this could break...
		}
		currentHp -= damage;
		if(currentHp < 0){
			currentHp = 0;
			isDead = true;
		}
		return isDead;
	}

	//========================= Bullet ==================================
	
	public static class MinionBullet extends DynamicGameObject {
		public float damage;
		float animationTime;
		public MinionBullet(float x, float y, float damage) {
			super(x, y, MinionBulletView.RADIUS);
			animationTime = 0f;
			this.damage = damage;
			MusicAndSound.playSoundEnemyBullet();
		}
		public void update(float deltaTime){
			animationTime += deltaTime;
			setPosition(getPosition().x + velocity.x*deltaTime, getPosition().y + velocity.y*deltaTime);
		}
		public void updatePositionWithParallaxBackground(float deltaTime, float speed) {
			setPosition(getPosition().x, getPosition().y + speed*deltaTime);
		}
		public void present(float deltaTime, SpriteBatcher minionBulletBatcher,	MinionBulletView minionBulletView) {
			minionBulletBatcher.beginBatch(minionBulletView.textureBullets);
			minionBulletBatcher.drawSprite(getPosition().x, getPosition().y, MinionBulletView.RADIUS*4, MinionBulletView.RADIUS*4, minionBulletView.animationBullets.getKeyFrame(animationTime, Animation.ANIMATION_LOOPING));
			minionBulletBatcher.endBatch();
		}
	}

	public void setVelocity(float x, float y) {
		velocity.x = x;
		velocity.y = y;
	}
	
	public void setBulletVelocity(float x, float y){
		bulletVelocity.x = x;
		bulletVelocity.y = y;
	}
	
	public void setFiringTime(float time){
		firingTimer = time;
	}
	
	//============================= Public methods =================================
	protected int getHpByBaseScreenLevel(int lvl){
		return (int)(baseHp*(Math.pow(baseMultiplier, screen.screenLevel)));
	}
	protected int getCollisionDamageByBaseScreenLevel(int lvl){
		return (int)(baseCollisionDamage*(Math.pow(baseMultiplier, screen.screenLevel)));
	}
	protected int getAttackDamageByBaseScreenLevel(int lvl){
		return (int)(baseAttackDamage*(Math.pow(baseMultiplier, screen.screenLevel)));
	}
}