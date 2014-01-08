public class Level7Screen extends LevelScreen {
	
	public Level7Screen(OuyaGame game) {
		super(game);
		bonusGold = 300;
		planetNumber = 6;
		minionView = new MinionRedCoreType1View(game);

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
		boss = new Level2Boss(camera.getPosition().x, camera.getPosition().y, (OuyaGame)game, this);
	}
	
	@Override
	public void present(float deltaTime) {
		super.present1(deltaTime);
		for(Minion m : minions){
			m.present(deltaTime, minionView, effectsStatusView, minionBatcher);
		}
		super.present2(deltaTime);
	}
}
