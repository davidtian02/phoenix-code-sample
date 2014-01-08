public class Level4Screen extends LevelScreen {
	
	public Level4Screen(OuyaGame game) {
		super(game);
		bonusGold = 150;
		planetNumber = 3;
		minionView = new MinionRedCoreType1View(game);

		spawnBossAtTime = 0;
		isCameraCapped = true;
		
		screenLevel = 4;
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
		boss = new Level1Boss(camera.getPosition().x, camera.getPosition().y, (OuyaGame)game, this);
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
