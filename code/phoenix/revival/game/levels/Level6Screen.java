public class Level6Screen extends LevelScreen {
	public final float spawnPaddingY = 275f;
	public final float spawnPaddingX = 450f;
	private enum MinionType { KAMIKAZE, REDCORETYPE1, REDCORETYPE2 };
	
	final boolean spawnedWavesArray[];
	float spawnTimesArray[] = {2, 5, 8, 12.5f, 23, 24, 30, 31, 40,41, 50,51, 60, 75, 95, 100};
	final SpawnAction spawnActionsArray[];
	final boolean spawnedQuasarsArray[];
	final float spawnTimesArrayQuasars[] = {7, 17, 27, 35, 55, 68, 85};
	
	//spawn timers specific to this level:
	float spawnTimerSingleEnemyRandomKamikazeBegin = 5;
	float spawnTimerSingleEnemyRandomKamikazeCD = 4f;
	float spawnTimerSingleEnemyRandomKamikazeEnd = 90;
	float spawnTimerSingleEnemyRandomKamikaze = spawnTimerSingleEnemyRandomKamikazeCD + 1;

	float spawnTimerSingleEnemyRandomRedCoreBegin = 5.5f;
	float spawnTimerSingleEnemyRandomRedCoreCD = 4f;
	float spawnTimerSingleEnemyRandomRedCoreEnd = 90;
	float spawnTimerSingleEnemyRandomRedCore = spawnTimerSingleEnemyRandomRedCoreCD + 1;
	
	float spawnTimerSingleEnemyRandomKamikazeFasterBegin = 30;
	float spawnTimerSingleEnemyRandomKamikazeFasterCD = 2f;
	float spawnTimerSingleEnemyRandomKamikazeFasterEnd = 100;
	float spawnTimerSingleEnemyRandomKamikazeFaster = spawnTimerSingleEnemyRandomKamikazeFasterCD + 1;

	float spawnTimerSingleEnemyRandomRedCoreFasterBegin = 30;
	float spawnTimerSingleEnemyRandomRedCoreFasterCD = 2f;
	float spawnTimerSingleEnemyRandomRedCoreFasterEnd = 100;
	float spawnTimerSingleEnemyRandomRedCoreFaster = spawnTimerSingleEnemyRandomRedCoreFasterCD + 1;
	
	float spawnTimerSingleEnemyRandomKamikazeFastBegin = 80;
	float spawnTimerSingleEnemyRandomKamikazeFastCD = 1f;
	float spawnTimerSingleEnemyRandomKamikazeFastEnd = 105;
	float spawnTimerSingleEnemyRandomKamikazeFast = spawnTimerSingleEnemyRandomKamikazeFastCD + 1;
	
	float spawnTimerSingleEnemyRandomRedCoreFastBegin = 80;
	float spawnTimerSingleEnemyRandomRedCoreFastCD = 1f;
	float spawnTimerSingleEnemyRandomRedCoreFastEnd = 105;
	float spawnTimerSingleEnemyRandomRedCoreFast = spawnTimerSingleEnemyRandomRedCoreFastCD + 1;
	
	MinionView quasarView;
	MinionView neutronStarView;
	MinionView minionView2;
	MinionView minionView3;
	
	public Level6Screen(OuyaGame game) {
		super(game);
		bonusGold = 200;
		planetNumber = 5;
		minionView = new MinionKamikazeView(game);
		minionView2 = new MinionRedCoreType1View(game);
		minionView3 = new MinionRedCoreType2View(game);
		quasarView = new MinionQuasarView(game);
		neutronStarView = new MinionNeutronStarView(game);
		spawnedWavesArray = new boolean[spawnTimesArray.length];
		spawnedQuasarsArray = new boolean[spawnTimesArrayQuasars.length];
		spawnActionsArray = new SpawnAction[spawnTimesArray.length];
		
		//adding how to spawn
		spawnActionsArray[0] = new SpawnActionTopMid();
		spawnActionsArray[1] = new SpawnActionBotMid();
		spawnActionsArray[2] = new SpawnActionLeftAndRight();
		spawnActionsArray[3] = new SpawnActionAllCorners();
		spawnActionsArray[4] = new SpawnActionLeftWallFormation();
		spawnActionsArray[5] = new SpawnActionRightWall();
		spawnActionsArray[6] = new SpawnActionRightWallFormation();
		spawnActionsArray[7] = new SpawnActionLeftWall();
		spawnActionsArray[8] = new SpawnActionLeftAndRightWallFormation();
		spawnActionsArray[9] = new SpawnActionTopAndBottom();
		spawnActionsArray[10] = new SpawnActionLeftAndRightWallFormation();
		spawnActionsArray[11] = new SpawnActionTopAndBottom();
		spawnActionsArray[12] = new SpawnActionLeftAndRightWall();
		spawnActionsArray[13] = new SpawnActionTopAndBottomWall();
		spawnActionsArray[14] = new SpawnActionAllCorners();
		spawnActionsArray[15] = new SpawnActionLeftAndRightWallFormation();

		spawnBossAtTime = 113f;
		
		screenLevel = 6;
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
		
		//spawning for quasars
		for(int i=0; i<spawnedQuasarsArray.length; i++){
			if(!spawnedQuasarsArray[i] && levelTimer > spawnTimesArrayQuasars[i]){
				doSpawnMinionQuasar();
				spawnedQuasarsArray[i] = true;
			}
		}
		
		if(levelTimer > spawnTimerSingleEnemyRandomKamikazeBegin && levelTimer < spawnTimerSingleEnemyRandomKamikazeEnd){
			if(spawnTimerSingleEnemyRandomKamikaze > spawnTimerSingleEnemyRandomKamikazeCD){
				new SpawnActionSingleEnemyDownwardsRandomPosition(MinionType.KAMIKAZE, SpawnAction.VELOCITY_SLOW).spawn();
				spawnTimerSingleEnemyRandomKamikaze = 0;
			} else {
				spawnTimerSingleEnemyRandomKamikaze += deltaTime;
			}
		}
		
		if(levelTimer > spawnTimerSingleEnemyRandomRedCoreBegin && levelTimer < spawnTimerSingleEnemyRandomRedCoreEnd){
			if(spawnTimerSingleEnemyRandomRedCore > spawnTimerSingleEnemyRandomRedCoreCD){
				new SpawnActionSingleEnemyDownwardsRandomPosition(MinionType.REDCORETYPE1, SpawnAction.VELOCITY_SLOW).spawn();
				spawnTimerSingleEnemyRandomRedCore = 0;
			} else {
				spawnTimerSingleEnemyRandomRedCore += deltaTime;
			}
		}
		
		if(levelTimer > spawnTimerSingleEnemyRandomKamikazeFasterBegin && levelTimer < spawnTimerSingleEnemyRandomKamikazeFasterEnd){
			if(spawnTimerSingleEnemyRandomKamikazeFaster > spawnTimerSingleEnemyRandomKamikazeFasterCD){
				new SpawnActionSingleEnemyDownwardsRandomPosition(MinionType.KAMIKAZE, SpawnAction.VELOCITY_MEDIUM).spawn();
				spawnTimerSingleEnemyRandomKamikazeFaster = 0;
			} else {
				spawnTimerSingleEnemyRandomKamikazeFaster += deltaTime;
			}
		}
		
		if(levelTimer > spawnTimerSingleEnemyRandomRedCoreFasterBegin && levelTimer < spawnTimerSingleEnemyRandomRedCoreFasterEnd){
			if(spawnTimerSingleEnemyRandomRedCoreFaster > spawnTimerSingleEnemyRandomRedCoreFasterCD){
				new SpawnActionSingleEnemyDownwardsRandomPosition(MinionType.KAMIKAZE, SpawnAction.VELOCITY_FAST).spawn();
				spawnTimerSingleEnemyRandomRedCoreFaster = 0;
			} else {
				spawnTimerSingleEnemyRandomRedCoreFaster += deltaTime;
			}
		}
		
		if(levelTimer > spawnTimerSingleEnemyRandomKamikazeFastBegin && levelTimer < spawnTimerSingleEnemyRandomKamikazeFastEnd){
			if(spawnTimerSingleEnemyRandomKamikazeFast > spawnTimerSingleEnemyRandomKamikazeFastCD){
				new SpawnActionSingleEnemyDownwardsRandomPosition(MinionType.REDCORETYPE1, SpawnAction.VELOCITY_FAST).spawn();
				spawnTimerSingleEnemyRandomKamikazeFast = 0;
			} else {
				spawnTimerSingleEnemyRandomKamikazeFast += deltaTime;
			}
		}
		
		if(levelTimer > spawnTimerSingleEnemyRandomRedCoreFastBegin && levelTimer < spawnTimerSingleEnemyRandomRedCoreFastEnd){
			if(spawnTimerSingleEnemyRandomRedCoreFast > spawnTimerSingleEnemyRandomRedCoreFastCD){
				new SpawnActionSingleEnemyDownwardsRandomPosition(MinionType.REDCORETYPE1, SpawnAction.VELOCITY_FAST).spawn();
				spawnTimerSingleEnemyRandomRedCoreFast = 0;
			} else {
				spawnTimerSingleEnemyRandomRedCoreFast += deltaTime;
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
		
		// ----------------------------- type 2 -----------------------------
		
		public static final float VELOCITY_DASH_IN = 10*Utils.FPS;
		public float getPositionTop(){
			return Utils.WORLD_HEIGHT - 100f;
		}
		public float getPositionBot(){
			return 100f; 
		}
		public float getPositionLeft(){
			return 200;
		}
		public float getPositionRight(){
			return Utils.WORLD_WIDTH - 200f;
		}
		
		//in between
		public float getPositionXMidVerticleLeft(){
			return 660;
		}
		public float getPositionXMidVerticleRight(){
			return 1260;
		}
		public float getPositionXMidVerticle(){
			return Utils.WORLD_WIDTH/2;
		}
		public float getPositionMidHorizontal(){
			return Utils.WORLD_HEIGHT/2;
		}
		public float getScreenBase(){
			return camera.getPosition().y - Utils.WORLD_HEIGHT/2;
		}
		
		// ----------------------------- type 1 -----------------------------
		
		public void spawnSingleKamikazeTopDownRandomPosition(int speed){
			MinionKamikaze m = new MinionKamikaze(getPositionRandomXAxis(), getPositionRandomAboveTopBorder(), game, Level6Screen.this);
			m.setVelocity(0, -speed);
			minions.add(m);
		}
		
		public void spawnSingleRedCoreTopDownRandomPosition(int speed){
			MinionRedCoreType1 m = new MinionRedCoreType1(getPositionRandomXAxis(), getPositionRandomAboveTopBorder(), game, Level6Screen.this);
			m.setVelocity(0, -speed);
			m.setBulletVelocity(m.velocity.x, m.velocity.y*2);
			m.setFiringTime(m.firingTimerCD * Utils.getRandomFloat());
			minions.add(m);
		}
		
		public void spawnLeftWallFormation(){
			for(int i=0; i<10; i++){
				if( (i&0x01) == 0){
					MinionKamikaze m = new MinionKamikaze(getPositionLeftOfLeftBorder(), getPositionEvenlyAcrossYAxis(i,10), game, Level6Screen.this);
					m.setVelocity(VELOCITY_SLOW, 0);
					minions.add(m);
				} else {
					MinionRedCoreType1 m = new MinionRedCoreType1(getPositionLeftOfLeftBorder(), getPositionEvenlyAcrossYAxis(i,10), game, Level6Screen.this);
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
					MinionKamikaze m = new MinionKamikaze(getPositionRightOfRightBorder(), getPositionEvenlyAcrossYAxis(i,10), game, Level6Screen.this);
					m.setVelocity(-VELOCITY_SLOW, 0);
					minions.add(m);
				} else {
					MinionRedCoreType1 m = new MinionRedCoreType1(getPositionRightOfRightBorder(), getPositionEvenlyAcrossYAxis(i,10), game, Level6Screen.this);
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
					MinionKamikaze m = new MinionKamikaze(getPositionEvenlyAcrossXAxis(i, 10), getPositionAboveTopBorder(), game, Level6Screen.this);
					m.setVelocity(0, -VELOCITY_SLOW);
					minions.add(m);
				} else {
					MinionRedCoreType1 m = new MinionRedCoreType1(getPositionEvenlyAcrossXAxis(i, 10), getPositionAboveTopBorder(), game, Level6Screen.this);
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
					MinionKamikaze m = new MinionKamikaze(getPositionEvenlyAcrossXAxis(i, 10), getPositionBelowBottomBorder(), game, Level6Screen.this);
					m.setVelocity(0, VELOCITY_SLOW);
					minions.add(m);
				} else {
					MinionRedCoreType1 m = new MinionRedCoreType1(getPositionEvenlyAcrossXAxis(i, 10), getPositionBelowBottomBorder(), game, Level6Screen.this);
					m.setVelocity(0, VELOCITY_SLOW);
					m.setBulletVelocity(m.velocity.x, m.velocity.y*2);
					m.setFiringTime(m.firingTimerCD);
					minions.add(m);
				}
			}	
		}
		
		// ----------------------------- type 2 ----------------------------- 
		
		
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
			} else if(type == MinionType.REDCORETYPE1){
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
	
		
	protected class SpawnActionTopMid extends SpawnAction{
		@Override
		public void spawn() {
			MinionRedCoreType2 m = new MinionRedCoreType2(getPositionXMidVerticle(), getScreenBase() + getPositionTop() + spawnPaddingY, game, Level6Screen.this);
			m.setVelocity(0, -VELOCITY_DASH_IN);
			m.setStopAtY(getPositionTop());
			minions.add(m);
		}
	}
	protected class SpawnActionBotMid extends SpawnAction{
		@Override
		public void spawn() {
			MinionRedCoreType2 m = new MinionRedCoreType2(getPositionXMidVerticle(), getScreenBase() + getPositionBot() - spawnPaddingY, game, Level6Screen.this);
			m.setVelocity(0, VELOCITY_DASH_IN);
			m.setStopAtY(getPositionBot());
			minions.add(m);
		}
	}
	protected class SpawnActionLeftAndRight extends SpawnAction{
		@Override
		public void spawn() {
			MinionRedCoreType2 m = new MinionRedCoreType2(getPositionLeft() - spawnPaddingX, getScreenBase() + getPositionMidHorizontal(), game, Level6Screen.this);
			m.setVelocity(VELOCITY_DASH_IN, 0);
			m.setStopAtX(getPositionLeft());
			minions.add(m);
			
			MinionRedCoreType2 m1 = new MinionRedCoreType2(getPositionRight() + spawnPaddingX, getScreenBase() + getPositionMidHorizontal(), game, Level6Screen.this);
			m1.setVelocity(-VELOCITY_DASH_IN, 0);
			m1.setStopAtX(getPositionRight());
			minions.add(m1);
		}
	}
	protected class SpawnActionTopAndBottom extends SpawnAction{
		@Override
		public void spawn() {
			MinionRedCoreType2 m = new MinionRedCoreType2(getPositionXMidVerticle(), getScreenBase() + getPositionTop() + spawnPaddingY, game, Level6Screen.this);
			m.setVelocity(0, -VELOCITY_DASH_IN);
			m.setStopAtY(getPositionTop());
			minions.add(m);
			
			MinionRedCoreType2 m1 = new MinionRedCoreType2( getPositionXMidVerticle(), getScreenBase() + getPositionBot() - spawnPaddingY, game, Level6Screen.this);
			m1.setVelocity(0, VELOCITY_DASH_IN);
			m1.setStopAtY(getPositionBot());
			minions.add(m1);
		}
	}
	protected class SpawnActionTopWall extends SpawnAction{
		@Override
		public void spawn() {
			MinionRedCoreType2 m = new MinionRedCoreType2(getPositionLeft(), getScreenBase() + getPositionTop() + spawnPaddingY, game, Level6Screen.this);
			m.setVelocity(0, -VELOCITY_DASH_IN);
			m.setStopAtY(getPositionTop());
			minions.add(m);
			
			MinionRedCoreType2 m1 = new MinionRedCoreType2(getPositionXMidVerticleLeft(), getScreenBase() + getPositionTop() + spawnPaddingY, game, Level6Screen.this);
			m1.setVelocity(0, -VELOCITY_DASH_IN);
			m1.setStopAtY(getPositionTop());
			minions.add(m1);
			
			MinionRedCoreType2 m2 = new MinionRedCoreType2(getPositionXMidVerticleRight(), getScreenBase() + getPositionTop() + spawnPaddingY, game, Level6Screen.this);
			m2.setVelocity(0, -VELOCITY_DASH_IN);
			m2.setStopAtY(getPositionTop());
			minions.add(m2);
			
			MinionRedCoreType2 m3 = new MinionRedCoreType2(getPositionRight(), getScreenBase() + getPositionTop() + spawnPaddingY, game, Level6Screen.this);
			m3.setVelocity(0, -VELOCITY_DASH_IN);
			m3.setStopAtY(getPositionTop());
			minions.add(m3);
		}
	}
	protected class SpawnActionBottomWall extends SpawnAction{
		@Override
		public void spawn() {
			MinionRedCoreType2 m = new MinionRedCoreType2(getPositionLeft(), getScreenBase() + getPositionBot() - spawnPaddingY, game, Level6Screen.this);
			m.setVelocity(0, VELOCITY_DASH_IN);
			m.setStopAtY(getPositionBot());
			minions.add(m);
			
			MinionRedCoreType2 m1 = new MinionRedCoreType2(getPositionXMidVerticleLeft(), getScreenBase() + getPositionBot() - spawnPaddingY, game, Level6Screen.this);
			m1.setVelocity(0, VELOCITY_DASH_IN);
			m1.setStopAtY(getPositionBot());
			minions.add(m1);
			
			MinionRedCoreType2 m2 = new MinionRedCoreType2(getPositionXMidVerticleRight(), getScreenBase() + getPositionBot() - spawnPaddingY, game, Level6Screen.this);
			m2.setVelocity(0, VELOCITY_DASH_IN);
			m2.setStopAtY(getPositionBot());
			minions.add(m2);
			
			MinionRedCoreType2 m3 = new MinionRedCoreType2(getPositionRight(), getScreenBase() + getPositionBot() - spawnPaddingY, game, Level6Screen.this);
			m3.setVelocity(0, VELOCITY_DASH_IN);
			m3.setStopAtY(getPositionBot());
			minions.add(m3);
		}
	}
	protected class SpawnActionLeftWall extends SpawnAction{
		@Override
		public void spawn() {
			System.out.println("YOU CAN'T MISS ME");
			MinionRedCoreType2 m = new MinionRedCoreType2(getPositionLeft() - spawnPaddingX, getScreenBase() + getPositionTop(), game, Level6Screen.this);
			m.setVelocity(VELOCITY_DASH_IN, 0);
			m.setStopAtX(getPositionLeft());
			minions.add(m);
			
			MinionRedCoreType2 m1 = new MinionRedCoreType2(getPositionLeft() - spawnPaddingX, getScreenBase() + getPositionMidHorizontal(), game, Level6Screen.this);
			m1.setVelocity(VELOCITY_DASH_IN, 0);
			m1.setStopAtX(getPositionLeft());
			minions.add(m1);
			
			MinionRedCoreType2 m2 = new MinionRedCoreType2(getPositionLeft() - spawnPaddingX, getScreenBase() + getPositionBot(), game, Level6Screen.this);
			m2.setVelocity(VELOCITY_DASH_IN, 0);
			m2.setStopAtX(getPositionLeft());
			minions.add(m2);
		}
	}
	protected class SpawnActionRightWall extends SpawnAction{
		@Override
		public void spawn() {
			MinionRedCoreType2 m = new MinionRedCoreType2(getPositionRight() + spawnPaddingX, getScreenBase() + getPositionTop(), game, Level6Screen.this);
			m.setVelocity(-VELOCITY_DASH_IN, 0);
			m.setStopAtX(getPositionRight());
			minions.add(m);
			
			MinionRedCoreType2 m1 = new MinionRedCoreType2(getPositionRight() + spawnPaddingX, getScreenBase() + getPositionMidHorizontal(), game, Level6Screen.this);
			m1.setVelocity(-VELOCITY_DASH_IN, 0);
			m1.setStopAtX(getPositionRight());
			minions.add(m1);
			
			MinionRedCoreType2 m2 = new MinionRedCoreType2(getPositionRight() + spawnPaddingX, getScreenBase() + getPositionBot(), game, Level6Screen.this);
			m2.setVelocity(-VELOCITY_DASH_IN, 0);
			m2.setStopAtX(getPositionRight());
			minions.add(m2);
		}
	}
	protected class SpawnActionTopAndBottomWall extends SpawnAction{
		@Override
		public void spawn() {
			new SpawnActionTopWall().spawn();
			new SpawnActionBottomWall().spawn();
		}
	}
	protected class SpawnActionLeftAndRightWall extends SpawnAction{
		@Override
		public void spawn() {
			new SpawnActionLeftWall().spawn();
			new SpawnActionRightWall().spawn();
		}
	}
	protected class SpawnActionAllCorners extends SpawnAction{
		@Override
		public void spawn() {
			MinionRedCoreType2 m = new MinionRedCoreType2(getPositionLeft(), getScreenBase() + getPositionTop() + spawnPaddingY, game, Level6Screen.this);
			m.setVelocity(0, -VELOCITY_DASH_IN);
			m.setStopAtY(getPositionTop());
			minions.add(m);
			
			MinionRedCoreType2 m1 = new MinionRedCoreType2(getPositionRight(), getScreenBase() + getPositionTop() + spawnPaddingY, game, Level6Screen.this);
			m1.setVelocity(0, -VELOCITY_DASH_IN);
			m1.setStopAtY(getPositionTop());
			minions.add(m1);
			
			MinionRedCoreType2 m2 = new MinionRedCoreType2(getPositionLeft(), getScreenBase() + getPositionBot() - spawnPaddingY, game, Level6Screen.this);
			m2.setVelocity(0, VELOCITY_DASH_IN);
			m2.setStopAtY(getPositionBot());
			minions.add(m2);
			
			MinionRedCoreType2 m3 = new MinionRedCoreType2(getPositionRight(), getScreenBase() + getPositionBot() - spawnPaddingY, game, Level6Screen.this);
			m3.setVelocity(0, VELOCITY_DASH_IN);
			m3.setStopAtY(getPositionBot());
			minions.add(m3);
		}
	}
	protected class SpawnActionFullSurround extends SpawnAction{
		@Override
		public void spawn() {
			new SpawnActionTopAndBottomWall().spawn();
			new SpawnActionLeftAndRight().spawn();
		}
	}
	
	
	private void doSpawnMinionQuasar(){
		MinionQuasar m = new MinionQuasar(camera.getPosition().x, camera.getPosition().y+Utils.WORLD_HEIGHT/2, game, Level6Screen.this);
		m.setVelocity(0, MinionQuasar.VELOCITY_Y_DOWNWARDS_SLOW);
		minions.add(m);
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
		isCameraCapped = true;
	}
	
	@Override
	public void present(float deltaTime) {
		super.present1(deltaTime);
		for(Minion m : minions){
			//REFACTOR could do this MUCH better
			if(m instanceof MinionKamikaze){
				m.present(deltaTime, minionView, effectsStatusView, minionBatcher);
			} else if(m instanceof MinionRedCoreType1){
				m.present(deltaTime, minionView2, effectsStatusView, minionBatcher);
			} else if(m instanceof MinionRedCoreType2){
				m.present(deltaTime, minionView3, effectsStatusView, minionBatcher);
			} else if(m instanceof MinionQuasar){
				m.present(deltaTime,  quasarView, effectsStatusView, minionBatcher);
			} else if(m instanceof MinionNeutronStar){
				m.present(deltaTime,  neutronStarView, effectsStatusView, minionBatcher);
			} else {
				System.out.println("uh oh....");
			}
		}
		super.present2(deltaTime);
	}
}
