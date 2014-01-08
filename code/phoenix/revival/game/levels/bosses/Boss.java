public abstract class Boss extends DynamicGameObject {
	static final float BOSS_ENRAGE_HP_PERCENTAGE = 0.3f;
	
	OuyaGame game;
	OuyaScreen screen;
	List<ShipExplosion> explosions;
	boolean hasShownDeadAnimation;
	ShipExplosionView explosionView;
	List<Minion> damageableParts;
	float midScreenX, midScreenY;
	
	float currentHP;
	float maxHP;
	public boolean isDead;
	public float collisionDamage;
	public boolean isEnraged;
	public float dropExperience;
	public boolean shouldSwitchToFinalAttack;

	public Boss(float x, float y, OuyaGame game, OuyaScreen screen) {
		super(x, y, 0);
		this.game = game;
		this.screen = screen;
		explosions = new LinkedList<ShipExplosion>();
		explosionView = new ShipExplosionView(game);
		
		midScreenX = x;
		midScreenY = y;
		
		damageableParts = new LinkedList<Minion>();
	}

	//this is for like... any part of boss hitting a bullet or hero
	public abstract boolean isColliding(DynamicGameObject other, float damage);
	
	//this is for spells
	public List<Minion> getDamageableParts(){
		return damageableParts;
	}
	public abstract List<? extends Minion> getNonDamageableParts();

	public void takeDamageAndCheckDies(float damage){
		currentHP -= damage;
		MusicAndSound.playSoundEnemyGettingHitDamaged();
		if(currentHP <= 0){
			isDead = true;
			currentHP = 0;
		}
	}
	
	public abstract void update(float deltaTime);
	protected abstract void justDied(float deltaTime);
	protected abstract void updateDieing(float deltaTime);
	public abstract void present(float deltaTime);
	
	public float getHpPercentage(){return currentHP/maxHP;}
}
