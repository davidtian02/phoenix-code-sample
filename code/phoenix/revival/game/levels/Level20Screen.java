public class Level20Screen extends LevelScreen {
	
	public Level20Screen(OuyaGame game) {
		super(game);
		bonusGold = 300;
		planetNumber = 6;

		spawnBossAtTime = 0;
		isCameraCapped = true;
		
		screenLevel = 7;
	}
	
	@Override
	public void updateSpawningEnemies(float deltaTime){
		
	}
	
	//================================= Drawing ============================
	
	@Override
	public void updateBoss(float deltaTime){
		super.updateBoss(deltaTime);
		
	}
	
	@Override
	protected void spawnBoss(){
		boss = new Level10Boss(camera.getPosition().x, camera.getPosition().y, (OuyaGame)game, this);
	}
	
	@Override
	public void present(float deltaTime) {
		super.present1(deltaTime);
		super.present2(deltaTime);
	}
}
