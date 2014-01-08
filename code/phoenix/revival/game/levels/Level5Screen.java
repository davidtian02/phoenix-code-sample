public class Level5Screen extends LevelScreen {
	public final float spawnPaddingY = 275f;
	public final float spawnPaddingX = 450f;
	
	final boolean spawnedWavesArray[];
	float spawnTimesArray[] = {2, 5, 8, 14, 20, 25, 30, 35, 40, 45, 50, 55, 60, 70, 80, 90, 100};
	final SpawnFormation spawnFormationArray[];
	
	//spawn timers specific to this level:
	
	MinionRedCoreType2View minionRedCoreType2View;
	
	public Level5Screen(OuyaGame game) {
		super(game);
		bonusGold = 100;
		planetNumber = 4;
		minionRedCoreType2View = new MinionRedCoreType2View(game);
		spawnedWavesArray = new boolean[spawnTimesArray.length];
		spawnFormationArray = new SpawnFormation[spawnTimesArray.length];
		
		//adding how to spawn
		spawnBossAtTime = 100;
		
		spawnFormationArray[0] = new SpawnFormationTopMid();
		spawnFormationArray[1] = new SpawnFormationBotMid();
		spawnFormationArray[2] = new SpawnFormationLeftAndRight();
		spawnFormationArray[3] = new SpawnFormationTopAndBottom();
		spawnFormationArray[4] = new SpawnFormationTopWall();
		spawnFormationArray[5] = new SpawnFormationBottomWall();
		spawnFormationArray[6] = new SpawnFormationLeftWall();
		spawnFormationArray[7] = new SpawnFormationRightWall();
		spawnFormationArray[8] = new SpawnFormationLeftWall();
		spawnFormationArray[9] = new SpawnFormationRightWall();
		spawnFormationArray[10] = new SpawnFormationAllCorners();
		spawnFormationArray[11] = new SpawnFormationAllCorners();
		spawnFormationArray[12] = new SpawnFormationLeftAndRightWall();
		spawnFormationArray[13] = new SpawnFormationTopAndBottomWall();
		spawnFormationArray[14] = new SpawnFormationFullSurround();
		spawnFormationArray[15] = new SpawnFormationFullSurround();
		spawnFormationArray[16] = new SpawnFormationFullSurround();
		
		screenLevel = 5;
	}
	
	@Override
	public void updateSpawningEnemies(float deltaTime){
		//special spawnings
		for(int i=0; i<spawnedWavesArray.length; i++){
			if(!spawnedWavesArray[i] && levelTimer > spawnTimesArray[i]){
				spawnFormationArray[i].spawn();
				spawnedWavesArray[i] = true;
			}
		}
	}
	
	protected abstract class SpawnFormation{
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
		public abstract void spawn();
		public float getScreenBase(){
			return camera.getPosition().y - Utils.WORLD_HEIGHT/2;
		}
	}
	
	protected class SpawnFormationTopMid extends SpawnFormation{
		@Override
		public void spawn() {
			MinionRedCoreType2 m = new MinionRedCoreType2(getPositionXMidVerticle(), getScreenBase() + getPositionTop() + spawnPaddingY, game, Level5Screen.this);
			m.setVelocity(0, -VELOCITY_DASH_IN);
			m.setStopAtY(getPositionTop());
			minions.add(m);
		}
	}
	protected class SpawnFormationBotMid extends SpawnFormation{
		@Override
		public void spawn() {
			MinionRedCoreType2 m = new MinionRedCoreType2(getPositionXMidVerticle(), getScreenBase() + getPositionBot() - spawnPaddingY, game, Level5Screen.this);
			m.setVelocity(0, VELOCITY_DASH_IN);
			m.setStopAtY(getPositionBot());
			minions.add(m);
		}
	}
	protected class SpawnFormationLeftAndRight extends SpawnFormation{
		@Override
		public void spawn() {
			MinionRedCoreType2 m = new MinionRedCoreType2(getPositionLeft() - spawnPaddingX, getScreenBase() + getPositionMidHorizontal(), game, Level5Screen.this);
			m.setVelocity(VELOCITY_DASH_IN, 0);
			m.setStopAtX(getPositionLeft());
			minions.add(m);
			
			MinionRedCoreType2 m1 = new MinionRedCoreType2(getPositionRight() + spawnPaddingX, getScreenBase() + getPositionMidHorizontal(), game, Level5Screen.this);
			m1.setVelocity(-VELOCITY_DASH_IN, 0);
			m1.setStopAtX(getPositionRight());
			minions.add(m1);
		}
	}
	protected class SpawnFormationTopAndBottom extends SpawnFormation{
		@Override
		public void spawn() {
			MinionRedCoreType2 m = new MinionRedCoreType2(getPositionXMidVerticle(), getScreenBase() + getPositionTop() + spawnPaddingY, game, Level5Screen.this);
			m.setVelocity(0, -VELOCITY_DASH_IN);
			m.setStopAtY(getPositionTop());
			minions.add(m);
			
			MinionRedCoreType2 m1 = new MinionRedCoreType2( getPositionXMidVerticle(), getScreenBase() + getPositionBot() - spawnPaddingY, game, Level5Screen.this);
			m1.setVelocity(0, VELOCITY_DASH_IN);
			m1.setStopAtY(getPositionBot());
			minions.add(m1);
		}
	}
	protected class SpawnFormationTopWall extends SpawnFormation{
		@Override
		public void spawn() {
			MinionRedCoreType2 m = new MinionRedCoreType2(getPositionLeft(), getScreenBase() + getPositionTop() + spawnPaddingY, game, Level5Screen.this);
			m.setVelocity(0, -VELOCITY_DASH_IN);
			m.setStopAtY(getPositionTop());
			minions.add(m);
			
			MinionRedCoreType2 m1 = new MinionRedCoreType2(getPositionXMidVerticleLeft(), getScreenBase() + getPositionTop() + spawnPaddingY, game, Level5Screen.this);
			m1.setVelocity(0, -VELOCITY_DASH_IN);
			m1.setStopAtY(getPositionTop());
			minions.add(m1);
			
			MinionRedCoreType2 m2 = new MinionRedCoreType2(getPositionXMidVerticleRight(), getScreenBase() + getPositionTop() + spawnPaddingY, game, Level5Screen.this);
			m2.setVelocity(0, -VELOCITY_DASH_IN);
			m2.setStopAtY(getPositionTop());
			minions.add(m2);
			
			MinionRedCoreType2 m3 = new MinionRedCoreType2(getPositionRight(), getScreenBase() + getPositionTop() + spawnPaddingY, game, Level5Screen.this);
			m3.setVelocity(0, -VELOCITY_DASH_IN);
			m3.setStopAtY(getPositionTop());
			minions.add(m3);
		}
	}
	protected class SpawnFormationBottomWall extends SpawnFormation{
		@Override
		public void spawn() {
			MinionRedCoreType2 m = new MinionRedCoreType2(getPositionLeft(), getScreenBase() + getPositionBot() - spawnPaddingY, game, Level5Screen.this);
			m.setVelocity(0, VELOCITY_DASH_IN);
			m.setStopAtY(getPositionBot());
			minions.add(m);
			
			MinionRedCoreType2 m1 = new MinionRedCoreType2(getPositionXMidVerticleLeft(), getScreenBase() + getPositionBot() - spawnPaddingY, game, Level5Screen.this);
			m1.setVelocity(0, VELOCITY_DASH_IN);
			m1.setStopAtY(getPositionBot());
			minions.add(m1);
			
			MinionRedCoreType2 m2 = new MinionRedCoreType2(getPositionXMidVerticleRight(), getScreenBase() + getPositionBot() - spawnPaddingY, game, Level5Screen.this);
			m2.setVelocity(0, VELOCITY_DASH_IN);
			m2.setStopAtY(getPositionBot());
			minions.add(m2);
			
			MinionRedCoreType2 m3 = new MinionRedCoreType2(getPositionRight(), getScreenBase() + getPositionBot() - spawnPaddingY, game, Level5Screen.this);
			m3.setVelocity(0, VELOCITY_DASH_IN);
			m3.setStopAtY(getPositionBot());
			minions.add(m3);
		}
	}
	protected class SpawnFormationLeftWall extends SpawnFormation{
		@Override
		public void spawn() {
			System.out.println("YOU CAN'T MISS ME");
			MinionRedCoreType2 m = new MinionRedCoreType2(getPositionLeft() - spawnPaddingX, getScreenBase() + getPositionTop(), game, Level5Screen.this);
			m.setVelocity(VELOCITY_DASH_IN, 0);
			m.setStopAtX(getPositionLeft());
			minions.add(m);
			
			MinionRedCoreType2 m1 = new MinionRedCoreType2(getPositionLeft() - spawnPaddingX, getScreenBase() + getPositionMidHorizontal(), game, Level5Screen.this);
			m1.setVelocity(VELOCITY_DASH_IN, 0);
			m1.setStopAtX(getPositionLeft());
			minions.add(m1);
			
			MinionRedCoreType2 m2 = new MinionRedCoreType2(getPositionLeft() - spawnPaddingX, getScreenBase() + getPositionBot(), game, Level5Screen.this);
			m2.setVelocity(VELOCITY_DASH_IN, 0);
			m2.setStopAtX(getPositionLeft());
			minions.add(m2);
		}
	}
	protected class SpawnFormationRightWall extends SpawnFormation{
		@Override
		public void spawn() {
			MinionRedCoreType2 m = new MinionRedCoreType2(getPositionRight() + spawnPaddingX, getScreenBase() + getPositionTop(), game, Level5Screen.this);
			m.setVelocity(-VELOCITY_DASH_IN, 0);
			m.setStopAtX(getPositionRight());
			minions.add(m);
			
			MinionRedCoreType2 m1 = new MinionRedCoreType2(getPositionRight() + spawnPaddingX, getScreenBase() + getPositionMidHorizontal(), game, Level5Screen.this);
			m1.setVelocity(-VELOCITY_DASH_IN, 0);
			m1.setStopAtX(getPositionRight());
			minions.add(m1);
			
			MinionRedCoreType2 m2 = new MinionRedCoreType2(getPositionRight() + spawnPaddingX, getScreenBase() + getPositionBot(), game, Level5Screen.this);
			m2.setVelocity(-VELOCITY_DASH_IN, 0);
			m2.setStopAtX(getPositionRight());
			minions.add(m2);
		}
	}
	protected class SpawnFormationTopAndBottomWall extends SpawnFormation{
		@Override
		public void spawn() {
			new SpawnFormationTopWall().spawn();
			new SpawnFormationBottomWall().spawn();
		}
	}
	protected class SpawnFormationLeftAndRightWall extends SpawnFormation{
		@Override
		public void spawn() {
			new SpawnFormationLeftWall().spawn();
			new SpawnFormationRightWall().spawn();
		}
	}
	protected class SpawnFormationAllCorners extends SpawnFormation{
		@Override
		public void spawn() {
			MinionRedCoreType2 m = new MinionRedCoreType2(getPositionLeft(), getScreenBase() + getPositionTop() + spawnPaddingY, game, Level5Screen.this);
			m.setVelocity(0, -VELOCITY_DASH_IN);
			m.setStopAtY(getPositionTop());
			minions.add(m);
			
			MinionRedCoreType2 m1 = new MinionRedCoreType2(getPositionRight(), getScreenBase() + getPositionTop() + spawnPaddingY, game, Level5Screen.this);
			m1.setVelocity(0, -VELOCITY_DASH_IN);
			m1.setStopAtY(getPositionTop());
			minions.add(m1);
			
			MinionRedCoreType2 m2 = new MinionRedCoreType2(getPositionLeft(), getScreenBase() + getPositionBot() - spawnPaddingY, game, Level5Screen.this);
			m2.setVelocity(0, VELOCITY_DASH_IN);
			m2.setStopAtY(getPositionBot());
			minions.add(m2);
			
			MinionRedCoreType2 m3 = new MinionRedCoreType2(getPositionRight(), getScreenBase() + getPositionBot() - spawnPaddingY, game, Level5Screen.this);
			m3.setVelocity(0, VELOCITY_DASH_IN);
			m3.setStopAtY(getPositionBot());
			minions.add(m3);
		}
	}
	protected class SpawnFormationFullSurround extends SpawnFormation{
		@Override
		public void spawn() {
			new SpawnFormationTopAndBottomWall().spawn();
			new SpawnFormationLeftAndRight().spawn();
		}
	}
	
	
	//================================= Drawing ============================
	
	@Override
	public void updateBoss(float deltaTime){
		super.updateBoss(deltaTime);
		
		if(boss!=null){
			if(!boss.isDead){
				if(boss.isEnraged){
					((Level2Boss)boss).shouldSwitchToFinalAttack = true;
				} else {
					if( !((Level2Boss)boss).attack1Active && !((Level2Boss)boss).attack2Active ){ //ready to attack again? or give them a min
						int rand = Utils.getRandomInt(2);
						if(rand==0){
							((Level2Boss)boss).attack1Active = true;
						} else {
							((Level2Boss)boss).attack2Active = true;
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
			m.present(deltaTime, minionRedCoreType2View, effectsStatusView, minionBatcher);
		}
		super.present2(deltaTime);
	}
}
