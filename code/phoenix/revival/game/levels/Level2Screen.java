public class Level2Screen extends LevelScreen {
	private enum MinionType { KAMIKAZE, REDCORE};
	
	final boolean spawnedWavesArray[];
	float spawnTimesArray[] = {2, 10, 20, 40, 60, 70, 90, 95, 100};
	final SpawnAction spawnActionsArray[];
	
	//spawn timers specific to this level:
	float spawnTimerSingleEnemyDropDownKamikazeBegin = 5;
	float spawnTimerSingleEnemyDropDownKamikazeCD = 1f;
	float spawnTimerSingleEnemyDropDownKamikazeEnd = 90;
	float spawnTimerSingleEnemyDropDownKamikaze = spawnTimerSingleEnemyDropDownKamikazeCD + 1;

	float spawnTimerSingleEnemyDropDownRedCoreBegin = 5.5f;
	float spawnTimerSingleEnemyDropDownRedCoreCD = 1f;
	float spawnTimerSingleEnemyDropDownRedCoreEnd = 90;
	float spawnTimerSingleEnemyDropDownRedCore = spawnTimerSingleEnemyDropDownRedCoreCD + 1;
	
	float spawnTimerSingleEnemyDropDownKamikazeFasterBegin = 30;
	float spawnTimerSingleEnemyDropDownKamikazeFasterCD = 0.5f;
	float spawnTimerSingleEnemyDropDownKamikazeFasterEnd = 100;
	float spawnTimerSingleEnemyDropDownKamikazeFaster = spawnTimerSingleEnemyDropDownKamikazeFasterCD + 1;

	float spawnTimerSingleEnemyDropDownRedCoreFasterBegin = 30;
	float spawnTimerSingleEnemyDropDownRedCoreFasterCD = 0.5f;
	float spawnTimerSingleEnemyDropDownRedCoreFasterEnd = 100;
	float spawnTimerSingleEnemyDropDownRedCoreFaster = spawnTimerSingleEnemyDropDownRedCoreFasterCD + 1;
	
	float spawnTimerSingleEnemyDropDownKamikazeFastBegin = 80;
	float spawnTimerSingleEnemyDropDownKamikazeFastCD = 0.25f;
	float spawnTimerSingleEnemyDropDownKamikazeFastEnd = 100;
	float spawnTimerSingleEnemyDropDownKamikazeFast = spawnTimerSingleEnemyDropDownKamikazeFastCD + 1;
	
	float spawnTimerSingleEnemyDropDownRedCoreFastBegin = 80;
	float spawnTimerSingleEnemyDropDownRedCoreFastCD = 0.25f;
	float spawnTimerSingleEnemyDropDownRedCoreFastEnd = 100;
	float spawnTimerSingleEnemyDropDownRedCoreFast = spawnTimerSingleEnemyDropDownRedCoreFastCD + 1;
	
	MinionView minionView2;
	
	public Level2Screen(OuyaGame game) {
		super(game);
		bonusGold = 10;
		planetNumber = 1;
		minionView = new MinionRedCoreType1View(game);
		minionView2 = new MinionKamikazeView(game);
		spawnedWavesArray = new boolean[spawnTimesArray.length];
		spawnActionsArray = new SpawnAction[spawnTimesArray.length];
		
		//adding how to spawn
		spawnActionsArray[0] = new SpawnActionSingleEnemyDownwardsRandomPosition(MinionType.REDCORE, SpawnAction.VELOCITY_SLOW);
		spawnActionsArray[1] = new SpawnActionLeftWallFormation();
		spawnActionsArray[2] = new SpawnActionRightWallFormation();
		spawnActionsArray[3] = new SpawnActionLeftAndRightWallFormation();
		spawnActionsArray[4] = new SpawnActionLeftAndRightWallFormation();
		spawnActionsArray[5] = new SpawnActionLeftAndRightWallFormation();
		spawnActionsArray[6] = new SpawnActionSurroundAllDirectionsFormation();
		spawnActionsArray[7] = new SpawnActionSurroundAllDirectionsFormation();
		spawnActionsArray[8] = new SpawnActionSurroundAllDirectionsFormation();
		
		spawnBossAtTime = 110f;
		screenLevel = 2;
	}
	
	@Override
	public void updateSpawningEnemies(float deltaTime){
		//special spawnings
		for(int i=0; i<spawnedWavesArray.length; i++){
			if(!spawnedWavesArray[i] && levelTimer > spawnTimesArray[i]){
				spawnActionsArray[i].spawn();
				spawnedWavesArray[i] = true;
			}
		}
		
		if(levelTimer > spawnTimerSingleEnemyDropDownKamikazeBegin && levelTimer < spawnTimerSingleEnemyDropDownKamikazeEnd){
			if(spawnTimerSingleEnemyDropDownKamikaze > spawnTimerSingleEnemyDropDownKamikazeCD){
				new SpawnActionSingleEnemyDownwardsRandomPosition(MinionType.KAMIKAZE, SpawnAction.VELOCITY_SLOW).spawn();
				spawnTimerSingleEnemyDropDownKamikaze = 0;
			} else {
				spawnTimerSingleEnemyDropDownKamikaze += deltaTime;
			}
		}
		
		if(levelTimer > spawnTimerSingleEnemyDropDownRedCoreBegin && levelTimer < spawnTimerSingleEnemyDropDownRedCoreEnd){
			if(spawnTimerSingleEnemyDropDownRedCore > spawnTimerSingleEnemyDropDownRedCoreCD){
				new SpawnActionSingleEnemyDownwardsRandomPosition(MinionType.REDCORE, SpawnAction.VELOCITY_SLOW).spawn();
				spawnTimerSingleEnemyDropDownRedCore = 0;
			} else {
				spawnTimerSingleEnemyDropDownRedCore += deltaTime;
			}
		}
		
		if(levelTimer > spawnTimerSingleEnemyDropDownKamikazeFasterBegin && levelTimer < spawnTimerSingleEnemyDropDownKamikazeFasterEnd){
			if(spawnTimerSingleEnemyDropDownKamikazeFaster > spawnTimerSingleEnemyDropDownKamikazeFasterCD){
				new SpawnActionSingleEnemyDownwardsRandomPosition(MinionType.KAMIKAZE, SpawnAction.VELOCITY_MEDIUM).spawn();
				spawnTimerSingleEnemyDropDownKamikazeFaster = 0;
			} else {
				spawnTimerSingleEnemyDropDownKamikazeFaster += deltaTime;
			}
		}
		
		if(levelTimer > spawnTimerSingleEnemyDropDownRedCoreFasterBegin && levelTimer < spawnTimerSingleEnemyDropDownRedCoreFasterEnd){
			if(spawnTimerSingleEnemyDropDownRedCoreFaster > spawnTimerSingleEnemyDropDownRedCoreFasterCD){
				new SpawnActionSingleEnemyDownwardsRandomPosition(MinionType.KAMIKAZE, SpawnAction.VELOCITY_FAST).spawn();
				spawnTimerSingleEnemyDropDownRedCoreFaster = 0;
			} else {
				spawnTimerSingleEnemyDropDownRedCoreFaster += deltaTime;
			}
		}
		
		if(levelTimer > spawnTimerSingleEnemyDropDownKamikazeFastBegin && levelTimer < spawnTimerSingleEnemyDropDownKamikazeFastEnd){
			if(spawnTimerSingleEnemyDropDownKamikazeFast > spawnTimerSingleEnemyDropDownKamikazeFastCD){
				new SpawnActionSingleEnemyDownwardsRandomPosition(MinionType.REDCORE, SpawnAction.VELOCITY_FAST).spawn();
				spawnTimerSingleEnemyDropDownKamikazeFast = 0;
			} else {
				spawnTimerSingleEnemyDropDownKamikazeFast += deltaTime;
			}
		}
		
		if(levelTimer > spawnTimerSingleEnemyDropDownRedCoreFastBegin && levelTimer < spawnTimerSingleEnemyDropDownRedCoreFastEnd){
			if(spawnTimerSingleEnemyDropDownRedCoreFast > spawnTimerSingleEnemyDropDownRedCoreFastCD){
				new SpawnActionSingleEnemyDownwardsRandomPosition(MinionType.REDCORE, SpawnAction.VELOCITY_FAST).spawn();
				spawnTimerSingleEnemyDropDownRedCoreFast = 0;
			} else {
				spawnTimerSingleEnemyDropDownRedCoreFast += deltaTime;
			}
		}
	}
	
	//================================= Spawning Formations ============================
	
	private abstract class SpawnAction{
		public static final int VELOCITY_SLOW = 90;
		public static final int VELOCITY_MEDIUM = 160;
		public static final int VELOCITY_FAST = 200;
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
		
		public void spawnSingleKamikazeTopDownRandomPosition(int speed){
			MinionKamikaze m = new MinionKamikaze(getPositionRandomXAxis(), getPositionRandomAboveTopBorder(), game, Level2Screen.this);
			m.setVelocity(0, -speed);
			minions.add(m);
		}
		
		public void spawnSingleRedCoreTopDownRandomPosition(int speed){
			MinionRedCoreType1 m = new MinionRedCoreType1(getPositionRandomXAxis(), getPositionRandomAboveTopBorder(), game, Level2Screen.this);
			m.setVelocity(0, -speed);
			m.setBulletVelocity(m.velocity.x, m.velocity.y*2);
			m.setFiringTime(m.firingTimerCD * Utils.getRandomFloat());
			minions.add(m);
		}
		
		public void spawnLeftWallFormation(){
			for(int i=0; i<10; i++){
				if( (i&0x01) == 0){
					MinionKamikaze m = new MinionKamikaze(getPositionLeftOfLeftBorder(), getPositionEvenlyAcrossYAxis(i,10), game, Level2Screen.this);
					m.setVelocity(VELOCITY_SLOW, 0);
					minions.add(m);
				} else {
					MinionRedCoreType1 m = new MinionRedCoreType1(getPositionLeftOfLeftBorder(), getPositionEvenlyAcrossYAxis(i,10), game, Level2Screen.this);
					m.setVelocity(VELOCITY_SLOW, 0);
					m.setBulletVelocity(m.velocity.x*2, m.velocity.y);
					m.setFiringTime(m.firingTimerCD);
					minions.add(m);
				}
				
			}
		}
		
		public void spawnRightWallFormation(){
			for(int i=0; i<10; i++){
				if((i&0x01)==0){
					MinionKamikaze m = new MinionKamikaze(getPositionRightOfRightBorder(), getPositionEvenlyAcrossYAxis(i,10), game, Level2Screen.this);
					m.setVelocity(-VELOCITY_SLOW, 0);
					minions.add(m);
				} else {
					MinionRedCoreType1 m = new MinionRedCoreType1(getPositionRightOfRightBorder(), getPositionEvenlyAcrossYAxis(i,10), game, Level2Screen.this);
					m.setVelocity(-VELOCITY_SLOW, 0);
					m.setBulletVelocity(m.velocity.x*2, m.velocity.y);
					m.setFiringTime(m.firingTimerCD);
					minions.add(m);
				}
			}
		}
		
		public void spawnTopWallFormation(){
			for(int i=0; i<10; i++){
				if((i&0x01)==0){
					MinionKamikaze m = new MinionKamikaze(getPositionEvenlyAcrossXAxis(i, 10), getPositionAboveTopBorder(), game, Level2Screen.this);
					m.setVelocity(0, -VELOCITY_SLOW);
					minions.add(m);
				} else {
					MinionRedCoreType1 m = new MinionRedCoreType1(getPositionEvenlyAcrossXAxis(i, 10), getPositionAboveTopBorder(), game, Level2Screen.this);
					m.setVelocity(0, -VELOCITY_SLOW);
					m.setBulletVelocity(m.velocity.x, m.velocity.y*2);
					m.setFiringTime(m.firingTimerCD);
					minions.add(m);
				}
			}
		}
		
		public void spawnBottomWallFormation(){
			for(int i=0; i<10; i++){
				if((i&0x01)==0){
					MinionKamikaze m = new MinionKamikaze(getPositionEvenlyAcrossXAxis(i, 10), getPositionBelowBottomBorder(), game, Level2Screen.this);
					m.setVelocity(0, VELOCITY_SLOW);
					minions.add(m);
				} else {
					MinionRedCoreType1 m = new MinionRedCoreType1(getPositionEvenlyAcrossXAxis(i, 10), getPositionBelowBottomBorder(), game, Level2Screen.this);
					m.setVelocity(0, VELOCITY_SLOW);
					m.setBulletVelocity(m.velocity.x, m.velocity.y*2);
					m.setFiringTime(m.firingTimerCD);
					minions.add(m);
				}
			}	
		}
	}
	
	protected class SpawnActionSingleEnemyDownwardsRandomPosition extends SpawnAction{
		MinionType type;
		int speed;
		public SpawnActionSingleEnemyDownwardsRandomPosition(MinionType type, int speed) {
			this.type = type;
			this.speed = speed;
		}

		@Override
		public void spawn() {
			if(type == MinionType.KAMIKAZE){
				spawnSingleKamikazeTopDownRandomPosition(speed);
			} else if(type == MinionType.REDCORE){
				spawnSingleRedCoreTopDownRandomPosition(speed);
			}
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
	
	protected class SpawnActionLeftAndRightWallFormation extends SpawnAction{
		@Override
		public void spawn(){
			spawnRightWallFormation();
			spawnLeftWallFormation();
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
			//REFACTOR could do this MUCH better
			if(m instanceof MinionRedCoreType1){
				m.present(deltaTime, minionView, effectsStatusView, minionBatcher);
			} else if(m instanceof MinionKamikaze){
				m.present(deltaTime, minionView2, effectsStatusView, minionBatcher);
			} else {
				System.out.println("uh oh....");
			}
		}
		super.present2(deltaTime);
	}
}
