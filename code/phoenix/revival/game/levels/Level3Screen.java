public class Level3Screen extends LevelScreen {
	
	final boolean spawnedWavesArray[];
	float spawnTimesArray[] = {5, 10, 20, 30, 40, 50, 60, 70, 80, 90, 95};
	final SpawnAction spawnActionsArray[];
	int spawnAsteroidTimesArray[];
	final boolean spawnedAsteroidsArray[]; 
	
	//spawn timers specific to this level:
	float spawnTimerSingleEnemyDropDownBegin = 2;
	float spawnTimerSingleEnemyDropDownCD = 5f;
	float spawnTimerSingleEnemyDropDownEnd = 93;
	float spawnTimerSingleEnemyDropDown = spawnTimerSingleEnemyDropDownCD + 1;
	
	float spawnTimerSurroundAllDirectionsFormationBegin = 70;
	float spawnTimerSurroundAllDirectionsFormationCD = 7f;
	float spawnTimerSurroundAllDirectionsFormationEnd = 89;
	float spawnTimerSurroundAllDirectionsFormation = spawnTimerSurroundAllDirectionsFormationCD + 1;
	
	MinionView asteroidView;
	
	public Level3Screen(OuyaGame game) {
		super(game);
		bonusGold = 20;
		planetNumber = 2;
		minionView = new MinionRedCoreType1View(game);
		asteroidView = new MinionAsteroidView(game);
		spawnedWavesArray = new boolean[spawnTimesArray.length];
		spawnActionsArray = new SpawnAction[spawnTimesArray.length];
		spawnAsteroidTimesArray = new int[10];
		spawnedAsteroidsArray = new boolean[spawnAsteroidTimesArray.length];
		for(int i=0; i<spawnAsteroidTimesArray.length; i++){
			spawnAsteroidTimesArray[i] = Utils.getRandomInt(90);
		}
		//then sort it
		for(int i=0; i<spawnAsteroidTimesArray.length; i++){
			for(int j=0; j<spawnAsteroidTimesArray.length-1; j++){
				if(spawnAsteroidTimesArray[j]>spawnAsteroidTimesArray[j+1]){
					spawnAsteroidTimesArray[j] ^= spawnAsteroidTimesArray[j+1];
					spawnAsteroidTimesArray[j+1]^=spawnAsteroidTimesArray[j];
					spawnAsteroidTimesArray[j] ^= spawnAsteroidTimesArray[j+1];
				}
			}
		}
		//end sorting
		
		//adding how to spawn
		spawnActionsArray[0] = new SpawnActionSingleEnemyDownwardsRandomPosition();
		spawnActionsArray[1] = new SpawnActionLeftWallFormation();
		spawnActionsArray[2] = new SpawnActionRightWallFormation();
		spawnActionsArray[3] = new SpawnActionTopWallFormation();
		spawnActionsArray[4] = new SpawnActionBottomWallFormation();
		spawnActionsArray[5] = new SpawnActionLeftAndRightWallFormation();
		spawnActionsArray[6] = new SpawnActionTopAndBottomWallFormation();
		spawnActionsArray[7] = new SpawnActionTopAndLeftWallFormation();
		spawnActionsArray[8] = new SpawnActionTopAndRightWallFormation();
		spawnActionsArray[9] = new SpawnActionBottomAndRightWallFormation();
		spawnActionsArray[10] = new SpawnActionBottomAndLeftWallFormation();
		
		screenLevel = 3;
	}
	
	@Override
	public void updateSpawningEnemies(float deltaTime){
		//spawning asteroids
		for(int i=0; i<spawnAsteroidTimesArray.length; i++){
			if(!spawnedAsteroidsArray[i] && levelTimer > spawnAsteroidTimesArray[i]){
				float x = Utils.getRandomFloat()*1800+62, y = camera.getPosition().y + Utils.WORLD_HEIGHT/2 + Utils.getRandomFloat()*(Utils.WORLD_HEIGHT-200)+100;
				MinionAsteroid m = new MinionAsteroid(x, y, game, Level3Screen.this);
				m.setVelocity(0, MinionAsteroid.VELOCITY_Y_DOWNWARDS_SLOW);
//				m.shouldRotate = Utils.getRandomSign() == 1;
				m.shouldRotate = false;
				
				minions.add(m);
				spawnedAsteroidsArray[i] = true;
			}
		}
		
		//special spawnings
		for(int i=0; i<spawnedWavesArray.length; i++){
			if(!spawnedWavesArray[i] && levelTimer > spawnTimesArray[i]){
				spawnActionsArray[i].spawn();
				spawnedWavesArray[i] = true;
			}
		}
		
		if(levelTimer > spawnTimerSingleEnemyDropDownBegin && levelTimer < spawnTimerSingleEnemyDropDownEnd){
			if(spawnTimerSingleEnemyDropDown > spawnTimerSingleEnemyDropDownCD){
				new SpawnActionSingleEnemyDownwardsRandomPosition().spawn();
				spawnTimerSingleEnemyDropDown = 0;
			} else {
				spawnTimerSingleEnemyDropDown += deltaTime;
			}
		}
		
		if(levelTimer > spawnTimerSurroundAllDirectionsFormationBegin && levelTimer < spawnTimerSurroundAllDirectionsFormationEnd){
			if(spawnTimerSurroundAllDirectionsFormation > spawnTimerSurroundAllDirectionsFormationCD){
				new SpawnActionSurroundAllDirectionsFormation().spawn();
				spawnTimerSurroundAllDirectionsFormation = 0;
			} else {
				spawnTimerSurroundAllDirectionsFormation += deltaTime;
			}
		}
		
	}
	
	//================================= Spawning Formations ============================
	
	private abstract class SpawnAction{
		public static final float VELOCITY_SLOW = 90;
		public static final float VELOCITY_MEDIUM = 160;
		public static final float VELOCITY_FAST = 200;
		public abstract void spawn();
		public float getPositionTopBorder(){
			return camera.getPosition().y + Utils.WORLD_HEIGHT/2;
		}
		public float getPositionAboveTopBorder(){
			return camera.getPosition().y + Utils.WORLD_HEIGHT/2 + MinionRedCoreType1View.RADIUS *2;
		}
		public float getPositionBelowBottomBorder(){
			return camera.getPosition().y - Utils.WORLD_HEIGHT/2 - MinionRedCoreType1View.RADIUS *2;
		}
		public float getPositionLeftOfLeftBorder(){
			return MinionRedCoreType1View.RADIUS*-2;
		}
		public float getPositionRightOfRightBorder(){
			return MinionRedCoreType1View.RADIUS*2 + Utils.WORLD_WIDTH;
		}
		
		public float getPositionRandomXAxis(){
			return Utils.getRandomInt((int)Utils.WORLD_WIDTH);
		}
		public float getPositionEvenlyAcrossXAxis(int i, int howMany){
			return i*(Utils.WORLD_WIDTH/howMany) + MinionRedCoreType1View.RADIUS*2;
		}
		public float getPositionEvenlyAcrossYAxis(int i, int howMnay){
			return camera.getPosition().y - Utils.WORLD_HEIGHT/2 + MinionRedCoreType1View.RADIUS*2 + i*Utils.WORLD_HEIGHT/howMnay;
		}
		
		public float getPositionRandomAboveTopBorder(){
			return Utils.getRandomInt(400) + getPositionTopBorder();
		}
		
		public void spawnSingleEnemyTopDownRandomPosition(){
			MinionRedCoreType1 m = new MinionRedCoreType1(getPositionRandomXAxis(), getPositionRandomAboveTopBorder(), game, Level3Screen.this);
			m.setVelocity(0, -VELOCITY_MEDIUM);
			m.setBulletVelocity(m.velocity.x, m.velocity.y*2);
			m.setFiringTime(m.firingTimerCD * Utils.getRandomFloat());
			minions.add(m);
		}
		
		public void spawnLeftWallFormation(){
			for(int i=0; i<10; i++){
				MinionRedCoreType1 m = new MinionRedCoreType1(getPositionLeftOfLeftBorder(), getPositionEvenlyAcrossYAxis(i,10), game, Level3Screen.this);
				m.setVelocity(VELOCITY_SLOW, 0);
				m.setBulletVelocity(m.velocity.x*2, m.velocity.y);
				m.setFiringTime(m.firingTimerCD);
				minions.add(m);
			}
		}
		
		public void spawnRightWallFormation(){
			for(int i=0; i<10; i++){
				MinionRedCoreType1 m = new MinionRedCoreType1(getPositionRightOfRightBorder(), getPositionEvenlyAcrossYAxis(i,10), game, Level3Screen.this);
				m.setVelocity(-VELOCITY_SLOW, 0);
				m.setBulletVelocity(m.velocity.x*2, m.velocity.y);
				m.setFiringTime(m.firingTimerCD);
				minions.add(m);
			}
		}
		
		public void spawnTopWallFormation(){
			for(int i=0; i<10; i++){
				MinionRedCoreType1 m = new MinionRedCoreType1(getPositionEvenlyAcrossXAxis(i, 10), getPositionAboveTopBorder(), game, Level3Screen.this);
				m.setVelocity(0, -VELOCITY_SLOW);
				m.setBulletVelocity(m.velocity.x, m.velocity.y*2);
				m.setFiringTime(m.firingTimerCD);
				minions.add(m);
			}
		}
		
		public void spawnBottomWallFormation(){
			for(int i=0; i<10; i++){
				MinionRedCoreType1 m = new MinionRedCoreType1(getPositionEvenlyAcrossXAxis(i, 10), getPositionBelowBottomBorder(), game, Level3Screen.this);
				m.setVelocity(0, VELOCITY_SLOW);
				m.setBulletVelocity(m.velocity.x, m.velocity.y*2);
				m.setFiringTime(m.firingTimerCD);
				minions.add(m);
			}	
		}
	}
	
	protected class SpawnActionSingleEnemyDownwardsRandomPosition extends SpawnAction{
		@Override
		public void spawn() {
			spawnSingleEnemyTopDownRandomPosition();
		}
	}
	
	protected class SpawnActionBottomAndRightWallFormation extends SpawnAction{
		@Override
		public void spawn(){
			spawnRightWallFormation();
			spawnBottomWallFormation();			
		}
	}
	protected class SpawnActionTopAndRightWallFormation extends SpawnAction{
		@Override
		public void spawn(){
			spawnTopWallFormation();
			spawnRightWallFormation();
		}
	}
	protected class SpawnActionBottomAndLeftWallFormation extends SpawnAction{
		@Override
		public void spawn(){
			spawnBottomWallFormation();	
			spawnLeftWallFormation();
		}
	}
	protected class SpawnActionTopAndLeftWallFormation extends SpawnAction{
		@Override
		public void spawn(){
			spawnTopWallFormation();
			spawnLeftWallFormation();
		}
	}
	protected class SpawnActionLeftAndRightWallFormation extends SpawnAction{
		@Override
		public void spawn(){
			spawnRightWallFormation();
			spawnLeftWallFormation();
		}
	}
	protected class SpawnActionTopAndBottomWallFormation extends SpawnAction{
		@Override
		public void spawn(){
			spawnTopWallFormation();
			spawnBottomWallFormation();
		}
	}
	
	protected class SpawnActionTopWallFormation extends SpawnAction{
		@Override
		public void spawn(){
			spawnTopWallFormation();
		}
	}
	protected class SpawnActionBottomWallFormation extends SpawnAction{
		@Override
		public void spawn(){
			spawnBottomWallFormation();
		}
	}
	protected class SpawnActionLeftWallFormation extends SpawnAction{
		@Override
		public void spawn(){
			spawnLeftWallFormation();
		}
	}
	protected class SpawnActionRightWallFormation extends SpawnAction{
		@Override
		public void spawn(){
			spawnRightWallFormation();
		}
	}
	
	protected class SpawnActionSurroundAllDirectionsFormation extends SpawnAction{
		@Override
		public void spawn() {
			spawnTopWallFormation();
			spawnLeftWallFormation();
			spawnRightWallFormation();
			spawnBottomWallFormation();
		}
	}
	
	//================================= Drawing ============================
	
	@Override
	public void updateBoss(float deltaTime){
		super.updateBoss(deltaTime);
		
		if(boss!=null){
			if(!boss.isDead){
				if(boss.isEnraged){
					((Level1Boss)boss).shouldSwitchToFinalAttack = true;
				} else {
					if( !((Level1Boss)boss).attack1Active && !((Level1Boss)boss).attack2Active ){ //ready to attack again? or give them a min
						int rand = Utils.getRandomInt(2);
						if(rand==0){
							((Level1Boss)boss).attack1Active = true;
						} else {
							((Level1Boss)boss).attack2Active = true;
						}
					}
				}
			}
		}
	}
	
	@Override
	protected void spawnBoss(){
		boss = new DeadBoss(camera.getPosition().x, camera.getPosition().y, (OuyaGame)game, this);
		boss.isDead = true;
	}
	
	@Override
	public void present(float deltaTime) {
		super.present1(deltaTime);
		for(Minion m : minions){
			if(m instanceof MinionRedCoreType1){
				m.present(deltaTime, minionView, effectsStatusView, minionBatcher);
			} else if(m instanceof MinionAsteroid){
				m.present(deltaTime, asteroidView, effectsStatusView, minionBatcher);
			}
		}
		super.present2(deltaTime);
	}
}
