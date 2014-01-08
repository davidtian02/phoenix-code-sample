public abstract class Hero extends DynamicGameObject implements OnControllerActionListener{
	public static final int MAX_NUM_BULLETS = 100;
	public static final int MAX_LEVEL = 20;
	private float rotationBufferX[];
	private float rotationBufferY[];
	private float baseMultiplier = 1.1f;
	public boolean isInTrainingMode;
	
	static int level; //this determines how strong a hero is.
	
	//base stats
	float baseHp;
	float baseMp;
	float baseHpRegen;
	float baseMpRegen;
	float baseAttackDmg;
	float baseAttackSpeed;
	float baseMovementSpeed;
	
	//basic stats
	float maxHp, maxMana;
	float currentHP, currentMana;
	static float currentExp, maxExp;
	float shootingTimer;
	
	//spells
	boolean isSpell1Unlocked, isSpell2Unlocked, isSpell3Unlocked, isSpell4Unlocked;
	boolean shouldCastSpell1, shouldCastSpell2, shouldCastSpell3, shouldCastSpell4;
	float spell1MpCost, spell2MpCost, spell3MpCost, spell4MpCost;
	float spell1Cd, spell2Cd, spell3Cd, spell4Cd;
	float spell1CdTimer, spell2CdTimer, spell3CdTimer, spell4CdTimer;
	float spell1Duration, spell2Duration, spell3Duration, spell4Duration;
	float spell1DurationTimer, spell2DurationTimer, spell3DurationTimer, spell4DurationTimer;
	//special stats
	float attackDmg, attackSpeed, movementSpeed;
	float criticalHitChance;
	float specialShield;
	float hpRegen, manaRegen;
	//basic states
	float lsx, lsy, rsx, rsy;
	float angle;
	boolean isShootingPressed;  
	//buffs
	boolean isStealth;
	//debuffs
	boolean isStunned, isFrosted, isSlowed, isHeld, isDisabled;
	//items
	boolean shouldUseHpPotion, shouldUseManaPotion;

	//ally spell buffs:
	InstaGuard instaGuard;
	InstaGuardView instaGuardView;
	SpriteBatcher instaGuardBatcher;
	
	//basic properties
	HeroView view;
	SpriteBatcher batcherShip;
	SpriteBatcher batcherBullets;
	SpriteBatcher batcherShield;
	Shield shield;
	List<Bullet> bullets;
	
	//game stuff
	OuyaGame game;
	OuyaScreen screen;
	boolean isHeroDead;
	public boolean exitAfterGameOver;
	public boolean hasUserPressedExitUponPause;
	
	public Hero(float x, float y, float radius, OuyaGame game, OuyaScreen screen, int playerNumber){
		super(x, y, radius);
		this.game = game;
		this.screen = screen;
		game.setOnControllerActionListener(playerNumber, this);
		shootingTimer = attackSpeed;
		rotationBufferX = new float[10];
		rotationBufferY = new float[10];
		
		batcherShip = new SpriteBatcher(game.getGLGraphics(), 1);
		batcherBullets = new SpriteBatcher(game.getGLGraphics(), MAX_NUM_BULLETS);
		batcherShield = new SpriteBatcher(game.getGLGraphics(), 1);
		shield = new Shield(x, y, Shield.RADIUS);
		bullets = new LinkedList<Bullet>();
		
		//preloading graphics
		instaGuardView = new InstaGuardView(game);
		instaGuardBatcher = new SpriteBatcher(game.getGLGraphics(), 1);
		
		exitAfterGameOver = true;
		
		//stats stuff
		isSpell1Unlocked = true;
		isSpell2Unlocked = true;
		isSpell3Unlocked = true;
		isSpell4Unlocked = true;
	}
	
	public void update(float deltaTime) {
		if(!isInTrainingMode){
			if(currentHP <= 5){
				if(sharedItems.remove(Items.PHOENIX_REVIVAL)){
					MusicAndSound.playSoundItemUsed();
					initializeByLevel(level);
				} else {
					isHeroDead = true;
					return;
				}
			}
		}
		updateStatsBeforeSpells(deltaTime);
		updateBuffsAndAuras(deltaTime);
		updateMovementAndRotation(deltaTime);
		checkShipCollidesWithEnemies(deltaTime);
		updateShootingAndBullets(deltaTime);
		checkBulletCollidesWithEnemies(deltaTime);
		checkCastSpells(deltaTime);
		updateSpellEffectsAndTimer(deltaTime);
		checkSpellsWithEnemiesAndAllies(deltaTime);
		if(screen.boss!=null){
			checkShipCollidesWithBoss(deltaTime);
			checkBulletCollidesWithBoss(deltaTime);
			checkSpellsWithBoss(deltaTime);
		}
		checkItemsUsed(deltaTime);
		updateHpManaAndSpellCdTimer(deltaTime);
		updatePositionWithParallaxBackground(deltaTime);
	}
	
	private void updateStatsBeforeSpells(float deltaTime){
		Vector2 input = new Vector2(lsx, lsy);
		if(input.len() > 0.4f){
			accel.set((2f*lsx/3)*(1+(movementSpeed/100f)), (2f*lsy/3)*(1+(movementSpeed/100f)));
		} else {
			accel.set(0, 0);
		}
		velocity.add(accel);
		velocity.x = velocity.x * 0.93f;
		velocity.y = velocity.y * 0.93f;
		float maxVelocity = movementSpeed;
		if(velocity.x > maxVelocity){
			velocity.x = maxVelocity;
		} else if(velocity.x < -maxVelocity){
			velocity.x = -maxVelocity;
		}
		
		if(velocity.y > maxVelocity){
			velocity.y = maxVelocity;
		} else if(velocity.y < -maxVelocity){
			velocity.y = -maxVelocity;
		}
	}
	
	protected void updateBuffsAndAuras(float deltaTime){
		if(instaGuard != null){
			instaGuard.update(deltaTime, getPosition().x, getPosition().y);
			if(instaGuard.isDone){
				instaGuard = null;
			}
		}
	}
	
	protected void updateMovementAndRotation(float deltaTime){
		//only move there if it won't put her off the bounds
		setPosition(getPosition().add(velocity));
		float padding = 10f;
		if(getPosition().x + ((Circle)bounds).radius >= screen.camera.getBounds().lowerLeft.x + screen.camera.getBounds().width - padding){
			setPosition( screen.camera.getBounds().lowerLeft.x + screen.camera.getBounds().width - ((Circle)bounds).radius - padding, getPosition().y );
		}
		if(getPosition().x - ((Circle)bounds).radius <= screen.camera.getBounds().lowerLeft.x + padding){
			setPosition( screen.camera.getBounds().lowerLeft.x + ((Circle)bounds).radius + padding, getPosition().y );
		}
		if(getPosition().y + ((Circle)bounds).radius >= screen.camera.getBounds().lowerLeft.y + screen.camera.getBounds().height - padding){
			setPosition( getPosition().x, screen.camera.getBounds().lowerLeft.y + screen.camera.getBounds().height - ((Circle)bounds).radius - padding);
		}
		if(getPosition().y - ((Circle)bounds).radius <= screen.camera.getBounds().lowerLeft.y + padding){
			setPosition( getPosition().x, screen.camera.getBounds().lowerLeft.y + ((Circle)bounds).radius + padding);
		}
		
		//just calibrating data properly, average out to 10
		Vector2 newInput = new Vector2(rsx, rsy);
		if(newInput.len() > 0.5f){
			for(int i=0; i<rotationBufferX.length-1; i++){
				rotationBufferX[i] = rotationBufferX[i+1];
				rotationBufferY[i] = rotationBufferY[i+1];
			}
			rotationBufferX[rotationBufferX.length-1] = rsx;
			rotationBufferY[rotationBufferY.length-1] = rsy;
			
			float tempX=0, tempY=0;
			for(int i=0; i<rotationBufferX.length; i++){
				tempX += rotationBufferX[i];
				tempY += rotationBufferY[i];
			}
			tempX /= rotationBufferX.length;
			tempY /= rotationBufferY.length;
			angle = new Vector2(-tempY, -tempX).angle();
			shield.angle = angle;
			isShootingPressed = true;
		} else {
			isShootingPressed = false;
		}
		
		shield.setPosition(getPosition());
	}
	
	protected void checkShipCollidesWithEnemies(float deltaTime){
		takeDamageAndCheckDies(helperCollisionOnEnemiesReturnsDamage(this, Float.MAX_VALUE));
	}
	
	protected void updateShootingAndBullets(float deltaTime){
		if(isShootingPressed){
			if(shootingTimer >= (1/attackSpeed)) {
				bullets.add(new Bullet(getPosition().x, getPosition().y, Bullet.WIDTH, Bullet.HEIGHT));
				shootingTimer = 0;
			} else {
				shootingTimer += deltaTime;
			}
		}
		//checks that bullets are still on the screen and not off screen
		Iterator<Bullet> iter = bullets.iterator();
		Bullet b;
		while(iter.hasNext()){
			b = iter.next();
			if(Utils.isOutOfBoundsCompletely((Rectangle)b.bounds, screen.camera.getBounds())){
				iter.remove();
			} else {
				b.update(deltaTime);
			}
		}
	}
	
	protected void checkBulletCollidesWithEnemies(float deltaTime){
		Iterator<Bullet> iter = bullets.iterator();
		Bullet b;
		while(iter.hasNext()){
			b = iter.next();
			if(helperCollisionOnEnemiesReturnsDamage(b, attackDmg) != 0){
				iter.remove();
			}
		}
	}
	
	//this just checks to see if any of the spells should be casted.
	protected void checkCastSpells(float deltaTime){
		if(isDisabled) return;
		
		if(isSpell1Unlocked){
			if(shouldCastSpell1){
				shouldCastSpell1 = false;
				if(currentMana >= spell1MpCost && spell1CdTimer >= spell1Cd){
					currentMana -= spell1MpCost;
					spell1CdTimer = 0;
					spell1DurationTimer = 0;
					castSpell1();
				}
			}
		} else {
			shouldCastSpell1 = false;
		}
		
		if(isSpell2Unlocked){
			if(shouldCastSpell2){
				shouldCastSpell2 = false;
				if(currentMana >= spell2MpCost && spell2CdTimer >= spell2Cd){
					currentMana -= spell2MpCost;
					spell2CdTimer = 0;
					spell2DurationTimer = 0;
					castSpell2();
				}
			}
		} else {
			shouldCastSpell2 = false;
		}
		
		if(isSpell3Unlocked){
			if(shouldCastSpell3){
				shouldCastSpell3 = false;
				if(currentMana >= spell3MpCost && spell3CdTimer >= spell3Cd){
					currentMana -= spell3MpCost;
					spell3CdTimer = 0;
					spell3DurationTimer = 0;
					castSpell3();
				}
			}
		} else {
			shouldCastSpell3 = false;
		}
		
		if(isSpell4Unlocked){
			if(shouldCastSpell4){
				shouldCastSpell4 = false;
				if(currentMana >= spell4MpCost && spell4CdTimer >= spell4Cd){
					currentMana -= spell4MpCost;
					spell4CdTimer = 0;
					spell4DurationTimer = 0;
					castSpell4();
				}
			}
		} else {
			shouldCastSpell4 = false;
		}
	}
	protected void updateSpellEffectsAndTimer(float deltaTime){
		spell1CdTimer = spell1CdTimer >= spell1Cd ? spell1Cd : spell1CdTimer + deltaTime;
		spell2CdTimer = spell2CdTimer >= spell2Cd ? spell2Cd : spell2CdTimer + deltaTime;
		spell3CdTimer = spell3CdTimer >= spell3Cd ? spell3Cd : spell3CdTimer + deltaTime;
		spell4CdTimer = spell4CdTimer >= spell4Cd ? spell4Cd : spell4CdTimer + deltaTime;
	}
	
	protected abstract void checkSpellsWithEnemiesAndAllies(float deltaTime);
	
	// ========================= Boss ===============================
	protected void checkShipCollidesWithBoss(float deltaTime){
		if(screen.boss.isColliding(this, 0)){ //this ship doesn't do any collision damage to bosses
			takeDamageAndCheckDies(screen.boss.collisionDamage);
		}
	}
	private void checkBulletCollidesWithBoss(float deltaTime){
		Iterator<Bullet> iter = bullets.iterator();
		Bullet b;
		while(iter.hasNext()){
			b = iter.next();
			if(screen.boss.isColliding(b, attackDmg)){
				iter.remove();
			}
		}
	}
	protected void checkSpellsWithBoss(float deltaTime){
		if(screen.boss.isDead){
			return;
		}
	}
	// ========================= end Boss ===============================
	
	protected void checkItemsUsed(float deltaTime){
		if(shouldUseHpPotion){
			shouldUseHpPotion = false;
			useHpPotion();
		}
		
		if(shouldUseManaPotion){
			shouldUseManaPotion = false;
			useManaPotion();
		}
	}
	
	protected void updateHpManaAndSpellCdTimer(float deltaTime){
		float regen = deltaTime * hpRegen;
		currentHP = currentHP + regen >= maxHp ? maxHp : currentHP + regen;
		regen = deltaTime * manaRegen;
		currentMana = currentMana + regen >= maxMana ? maxMana : currentMana + regen;
	}
	
	protected void updatePositionWithParallaxBackground(float deltaTime){
		if(!screen.isCameraCapped){
			setPosition(getPosition().x, getPosition().y + screen.PARALLAX_SPEED*deltaTime);
			shield.setPosition(getPosition().x, getPosition().y);
			for(Bullet b:bullets){
				b.setPosition(b.getPosition().x, b.getPosition().y + screen.PARALLAX_SPEED*deltaTime);
			}
		}
		
		//spells
		if(instaGuard != null){
			instaGuard.updatePositionWithParallaxBackground(deltaTime, screen.PARALLAX_SPEED);
		}
	}
	
	protected abstract void castSpell1();
	protected abstract void castSpell2();
	protected abstract void castSpell3();
	protected abstract void castSpell4();
	
	protected void useHpPotion(){
		for(Hero h : screen.herosList){
			h.currentHP = h.currentHP + Items.REPAIR_KIT_HP > h.maxHp ? h.maxHp : h.currentHP + Items.REPAIR_KIT_HP;
		}
		sharedItems.remove(Items.REPAIR_KIT);
		MusicAndSound.playSoundItemUsed();
	}
	
	protected void useManaPotion(){
		for(Hero h : screen.herosList){
			h.currentMana = h.currentMana + Items.ENERGY_CELL_MP > h.maxMana ? h.maxMana : h.currentMana + Items.ENERGY_CELL_MP;
		}
		sharedItems.remove(Items.ENERGY_CELL);
		MusicAndSound.playSoundItemUsed();
	}
		
	//helper on collisions by either ship or bullet, returns damage
	protected float helperCollisionOnEnemiesReturnsDamage(DynamicGameObject object, float damage){
		float collisionDamage = 0;
		Iterator<List<Minion>> iter = screen.enemies.iterator();
		List<Minion> minionsList;
		Iterator<Minion> iterM;
		Minion m;
		while(iter.hasNext()){
			 minionsList = iter.next();
			 iterM = minionsList.iterator();
			 while(iterM.hasNext()){
				 m = iterM.next();
				 if(m.isColliding(object)){
					 collisionDamage = m.collisionDamage;
					 if(damage < Float.MAX_VALUE - 1){ //so not the hero bumping into enemy
						 if(m.takeDamageAndCheckDies(damage)){
							 screen.explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, view.game));
							 gainExp(m.dropExperience);
							 iterM.remove();
						 }
					 } else {
						 //when hero bumps enemy
						 if(m.shouldDieUponShipCollision){
							 if(m.takeDamageAndCheckDies(damage)){
								 screen.explosions.add(new ShipExplosion(m.getPosition().x, m.getPosition().y, ShipExplosionView.RADIUS, view.game));
								 gainExp(m.dropExperience);
								 iterM.remove();
							 }
						 } else {
							 
						 }
					 }
				 }
			 }
		}
		return collisionDamage;
	}
	
	public boolean takeDamageAndCheckDies(float damage){
		boolean isDead = false;
		if(damage>0){ //actually got hit
			MusicAndSound.playSoundHeroGettingHit();
		}
		if(instaGuard != null){
			damage = instaGuard.takeDamage(damage);
		}
		currentHP = currentHP - damage;
		if(currentHP <= 0){
			currentHP = 0;
			isDead = true;
		}
		return isDead;
	}
	
	public void onExitPressedFromMenu(){
		if(((OuyaGame)game).isPaused){
			hasUserPressedExitUponPause = true;
		}
	}
	
	public void present(float deltaTime){
	    //bullets
	    if(bullets.size() > 0){
	    	batcherBullets.beginBatch(view.textureBullets);
	    	for(Bullet b : bullets){
	    		if(b.isAnimating){
	    			batcherBullets.drawSprite(b.getPosition().x, b.getPosition().y, Bullet.ANIMATION_WIDTH, Bullet.ANIMATION_HEIGHT, b.angle, view.animationBullets.getKeyFrame(b.animationTime, Animation.ANIMATION_NONLOOPING));
	    		} else {
	    			batcherBullets.drawSprite(b.getPosition().x, b.getPosition().y, Bullet.WIDTH, Bullet.HEIGHT, b.angle, view.regionBullet);
	    		}
	    	}
	    	batcherBullets.endBatch();
	    }
	    
	    if(instaGuard != null){
	    	instaGuard.present(deltaTime, instaGuardBatcher, instaGuardView);
	    }
	    
	    //shield
		batcherShield.beginBatch(view.textureShield);
		batcherShield.drawSprite(shield.getPosition().x, shield.getPosition().y, Shield.RADIUS*4, Shield.RADIUS*4, shield.angle, view.regionShield);
	    batcherShield.endBatch();
	    
	    //ship
  		batcherShip.beginBatch(view.textureShip);
  		batcherShip.drawSprite(getPosition().x, getPosition().y, HeroView.RADIUS*2, HeroView.RADIUS*2, angle, view.regionShip);
  	    batcherShip.endBatch();
	}
	
	// ========================= OnControllerActionListener implementation ===============================	
	
	@Override
	public void onLeftStickMoved(float x, float y) {
		lsx = x;
		lsy = y;
	}

	@Override
	public void onRightStickMoved(float x, float y) {
		rsx = x;
		rsy = y;
	}

	@Override
	public void onSpellsCasted(int button) {
		switch(button){
			case OuyaController.BUTTON_O: shouldCastSpell1 = true; break;
			case OuyaController.BUTTON_U: shouldCastSpell2 = true; break;
			case OuyaController.BUTTON_Y: shouldCastSpell3 = true; break;
			case OuyaController.BUTTON_A: shouldCastSpell4 = true; break;
		}
	}

	@Override
	public void onShootPressed() {
		isShootingPressed = true;
	}
	@Override
	public void onShootReleased() {
		isShootingPressed = false;
	}
	
	@Override
	public void onUseHpPotionPressed(){
		if(sharedItems.remove(Items.REPAIR_KIT)){
			shouldUseHpPotion = true;
		}
	}
	@Override
	public void onUseManaPotionPressed(){
		if(sharedItems.remove(Items.ENERGY_CELL)){
			shouldUseManaPotion = true;
		}
	}
	
	// ========================= Shield ===============================
	
	public static class Shield extends DynamicGameObject {
		public static final float RADIUS = 64;
		public float angle;
		public Shield(float x, float y, float radius) {
			super(x, y, radius);
		}
	}
	
	// ========================= Bullet ===============================	
	
	public class Bullet extends DynamicGameObject {
		public static final float WIDTH = 64;
		public static final float HEIGHT = 64;
		static final int ANIMATION_WIDTH = 64;
		static final int ANIMATION_HEIGHT = 128;
		float animationTime;
		float angle;
		boolean isAnimating;
		public Bullet(float x, float y, float width, float height) {
			super(x, y, width, height);
			this.angle = Hero.this.angle;
			float radians = angle * Vector2.TO_RADIANS;
			velocity.set(-FloatMath.sin(radians) * 3600, FloatMath.cos(radians) * 3600);
			animationTime = 0f;
			isAnimating = true;
			MusicAndSound.playSoundHeroBullet();
		}
		public void update(float deltaTime){
			animationTime = animationTime > 0.1f ? 0.1f : animationTime + deltaTime;
			if(animationTime > 0.1f){
				setPosition(getPosition().x + velocity.x*deltaTime, getPosition().y + velocity.y*deltaTime);
				isAnimating = false;
			}
		}
	}

	// ========================= status info ===============================
	public float getExpPercentage() {return currentExp/maxExp;}
	public float getHPPercentage() {return currentHP/maxHp;}
	public float getManaPercentage() {return currentMana/maxMana;}
	public float getSpell1CDPercentage() {return isSpell1Unlocked ? spell1CdTimer/spell1Cd : 0;}
	public float getSpell2CDPercentage() {return isSpell2Unlocked ? spell2CdTimer/spell2Cd : 0;}
	public float getSpell3CDPercentage() {return isSpell3Unlocked ? spell3CdTimer/spell3Cd : 0;}
	public float getSpell4CDPercentage() {return isSpell4Unlocked ? spell4CdTimer/spell4Cd : 0;}
	
	public boolean isHeroDead(){
		return isHeroDead;
	}

	public void initializeByLevel(int level) {
		Hero.level = level > MAX_LEVEL ? MAX_LEVEL : level;
		setUnlockedSpellsByLevel();
		setStatsByLevel();
	}
	
	public void setExperience(int exp) {
		currentExp = exp;
	}

	public int getLevel() {
		return level;
	}
	
	public int getCurrentExp(){
		return (int) currentExp;
	}
	
	public void gainExp(float dropExperience){
		currentExp += dropExperience;
		while(dropExperience > 0){
			float remainingExp = maxExp - currentExp;
			if(remainingExp > dropExperience){ //doesn't level up
				currentExp += dropExperience;
				dropExperience = 0;
			} else { //levels up
				currentExp += remainingExp;
				dropExperience -= remainingExp;
				levelUp();
			}
		}
		if(level >= MAX_LEVEL){
			currentExp = maxExp;
			level = MAX_LEVEL;
		}
	}
	
	private void levelUp(){
		currentExp = 0;
		MusicAndSound.playSoundLevelUp();
		for(Hero h : screen.herosList){
			h.initializeByLevel(level + 1);
		}
	}
	
	private void setUnlockedSpellsByLevel(){
		isSpell1Unlocked = true;
		isSpell2Unlocked = true;
		isSpell3Unlocked = true;
		isSpell4Unlocked = true;
		switch(level){
		case 1:
			isSpell2Unlocked = false;
		case 2:			
		case 3: 
			isSpell3Unlocked = false;
		case 4:
		case 5: 
		case 6:
			isSpell4Unlocked = false;
		}
	}
	
	protected void setStatsByLevel(){
		float baseMultiplier = 1.1f;
		this.maxHp = baseHp*(float)(Math.pow(baseMultiplier, level));
		this.currentHP = maxHp;
		this.maxMana = baseMp*(float)(Math.pow(baseMultiplier, level));
		this.currentMana = maxMana;
		maxExp = 100 * (float)Math.pow(2, level-1);
		this.hpRegen = baseHpRegen*(float)(Math.pow(baseMultiplier, level));
		this.manaRegen = baseMpRegen*(float)(Math.pow(baseMultiplier, level));
		this.attackDmg = baseAttackDmg*(float)(Math.pow(baseMultiplier, level));
		this.attackSpeed = getAttackSpeedByBase();
		this.movementSpeed = getMovementSpeedByBase();
		this.spell1CdTimer = spell1Cd + 1;
		this.spell2CdTimer = spell2Cd + 1;
		this.spell3CdTimer = spell3Cd + 1;
		this.spell4CdTimer = spell4Cd + 1;
	}
	protected float getMovementSpeedByBase(){
		return baseMovementSpeed*(float)(Math.pow(baseMultiplier, level));
	}
	protected float getAttackSpeedByBase(){
		return baseAttackSpeed*(float)(Math.pow(baseMultiplier, level));
	}
	protected float getAttackDamageByBase(){
		return baseAttackDmg*(float)(Math.pow(baseMultiplier, level));
	}
	
	// ========================= static methods ===============================	
	public static Hero generateHeroFromName(String name, float x, float y, float radius, OuyaGame ouyaGame, OuyaScreen ouyaScreen, int playerNumber) {
		Hero hero = null;
		if(name.equals("tank")){
			hero = new Tank(x, y, radius, ouyaGame, ouyaScreen, playerNumber);
		} else if(name.equals("healer")){
			hero = new Healer(x, y, radius, ouyaGame, ouyaScreen, playerNumber);
		} else if(name.equals("carry")){
			hero = new Carry(x, y, radius, ouyaGame, ouyaScreen, playerNumber);
		} else if(name.equals("caster")){
			hero = new Caster(x, y, radius, ouyaGame, ouyaScreen, playerNumber);
		} else if(name.equals("steelash")){
			hero = new Steelash(x, y, radius, ouyaGame, ouyaScreen, playerNumber);
		} 
		
		return hero;
	}

	protected static List<String> sharedItems = new ArrayList<String>();
	public static void setSharedItems(List<String> heroItems) {
		sharedItems = heroItems;
	}
	public static List<String> getSharedItems(){
		return sharedItems;
	}
}