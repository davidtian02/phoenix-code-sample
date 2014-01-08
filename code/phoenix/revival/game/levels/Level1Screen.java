public class Level1Screen extends LevelScreen {
	
	float surroundSpawnTimer;
	float surroundSpawnTimerCD = 7f;
	
	boolean spawnedWave0, spawnedWave1, spawnedWave2, spawnedWave3, spawnedWave4, spawnedWave5, spawnedWave6, spawnedWave7,
		spawnedWave8, spawnedWave9, spawnedWave10, spawnedWave11, spawnedWave12, spawnedWave13, spawnedWave14;
	
	MinionKamikazeView minionKamikazeView;
	
	public Level1Screen(OuyaGame game) {
		super(game);
		bonusGold = 0;
		planetNumber = 0;
		spawnBossAtTime = 97;
		minionKamikazeView = new MinionKamikazeView(game);
		screenLevel = 1;
	}
	
	@Override
	public void updateSpawningEnemies(float deltaTime){
		//1 coming downwards, randomly spawned
		if(levelTimer > 2.5f && !spawnedWave0){
			MinionKamikaze m = new MinionKamikaze(Utils.getRandomInt(1920), Utils.getRandomInt(200) + camera.getPosition().y + Utils.WORLD_HEIGHT/2, game, this);
			m.velocity.y = -50f;
			minions.add(m);
			spawnedWave0 = true;
		}
		
		//3 going downwards, randomly spawned
		if(levelTimer > 5f && !spawnedWave1){
			for(int i=0; i<3; i++){
				MinionKamikaze mk = new MinionKamikaze(Utils.getRandomInt(1920), Utils.getRandomInt(400) + camera.getPosition().y + Utils.WORLD_HEIGHT/2, game, this);
				mk.velocity.y = -90f;
				minions.add(mk);
				
			}
			spawnedWave1 = true;
		}
		
		//10 going downwards, randomly spawned
		if(levelTimer > 10f && !spawnedWave2){
			for(int i=0; i<10; i++){
				MinionKamikaze mk = new MinionKamikaze(Utils.getRandomInt(1920), Utils.getRandomInt(1000) + camera.getPosition().y + Utils.WORLD_HEIGHT/2 + MinionKamikazeView.RADIUS*2, game, this);
				mk.velocity.y = -150f;
				minions.add(mk);
			}			
			spawnedWave2 = true;
		}
		
		//10 going downwards, uniformed wall
		if(levelTimer > 15f && !spawnedWave3){
			for(int i=0; i<10; i++){
				MinionKamikaze mk = new MinionKamikaze(i*(Utils.WORLD_WIDTH/10) + MinionKamikazeView.RADIUS*2, camera.getPosition().y + Utils.WORLD_HEIGHT/2 + MinionKamikazeView.RADIUS *2, game, this);
				mk.velocity.y = -120f;
				minions.add(mk);
			}			
			spawnedWave3 = true;
		}
		
		//3 going from left to right, in wall
		if(levelTimer > 17f && !spawnedWave4){
			for(int i=0; i<3; i++){
				MinionKamikaze mk = new MinionKamikaze(MinionKamikazeView.RADIUS*-2 , camera.getPosition().y - Utils.WORLD_HEIGHT/2 + MinionKamikazeView.RADIUS *2 + i*Utils.WORLD_HEIGHT/3, game, this);
				mk.velocity.x = 60f;
				minions.add(mk);
			}
			spawnedWave4 = true;
		}
		
		//3 from bottom, in wall fashion
		if(levelTimer > 21f && !spawnedWave5){
			for(int i=0; i<3; i++){
				MinionKamikaze mk = new MinionKamikaze(i*Utils.WORLD_WIDTH/3, camera.getPosition().y - Utils.WORLD_HEIGHT/2 - MinionKamikazeView.RADIUS*2, game, this);
				mk.velocity.y = 60;
				minions.add(mk);
			}			
			spawnedWave5 = true;
		}
		
		//3 from right going left, in line formation
		if(levelTimer > 24f && !spawnedWave6){
			for(int i=0; i<3; i++){
				MinionKamikaze mk = new MinionKamikaze(MinionKamikazeView.RADIUS*2 + Utils.WORLD_WIDTH, camera.getPosition().y - Utils.WORLD_HEIGHT/2 + MinionKamikazeView.RADIUS *2 + i*Utils.WORLD_HEIGHT/3, game, this);
				mk.velocity.x = -60f;
				minions.add(mk);
			}		
			spawnedWave6 = true;
		}
		
		//10 going down, in line
		if(levelTimer > 28f && !spawnedWave7){
			for(int i=0; i<10; i++){
				MinionKamikaze mk = new MinionKamikaze(Utils.getRandomInt(1920), Utils.getRandomInt(1000) + camera.getPosition().y + Utils.WORLD_HEIGHT/2, game, this);
				mk.velocity.y = -100;
				minions.add(mk);
			}			
			spawnedWave7 = true;
		}
		
		//10 going right to left, much faster in line
		if(levelTimer > 35f && !spawnedWave8){
			for(int i=0; i<10; i++){
				MinionKamikaze mk = new MinionKamikaze(MinionKamikazeView.RADIUS*2 + Utils.WORLD_WIDTH, camera.getPosition().y - Utils.WORLD_HEIGHT/2 + MinionKamikazeView.RADIUS *2 + i*Utils.WORLD_HEIGHT/10, game, this);
				mk.velocity.x = -160f;
				minions.add(mk);
			}
			spawnedWave8 = true;
		}
		
		//10 going upwards, in line
		if(levelTimer > 40f && !spawnedWave9){
			for(int i=0; i<10; i++){
				MinionKamikaze mk = new MinionKamikaze(i*(Utils.WORLD_WIDTH/10) + MinionKamikazeView.RADIUS*2, camera.getPosition().y - Utils.WORLD_HEIGHT/2 - MinionKamikazeView.RADIUS *2, game, this);
				mk.velocity.y = 240f;
				minions.add(mk);
			}	
			spawnedWave9 = true;
		}
		
		//10 from top and left side, surrounding in
		if(levelTimer > 45f && !spawnedWave10){
			for(int i=0; i<10; i++){
				MinionKamikaze mk = new MinionKamikaze(i*(Utils.WORLD_WIDTH/10) + MinionKamikazeView.RADIUS*2, camera.getPosition().y + Utils.WORLD_HEIGHT/2 + MinionKamikazeView.RADIUS *2, game, this);
				mk.velocity.y = -200f;
				minions.add(mk);
			}
			
			for(int i=0; i<10; i++){
				MinionKamikaze mk = new MinionKamikaze(MinionKamikazeView.RADIUS*-2, camera.getPosition().y - Utils.WORLD_HEIGHT/2 + MinionKamikazeView.RADIUS *2 + i*Utils.WORLD_HEIGHT/10, game, this);
				mk.velocity.x = 200f;
				minions.add(mk);
			}
			
			spawnedWave10 = true;
		}	
		
		//10 from right and bottom, faster
		if(levelTimer > 50f && !spawnedWave11){
			for(int i=0; i<10; i++){
				MinionKamikaze mk = new MinionKamikaze(MinionKamikazeView.RADIUS*2 + Utils.WORLD_WIDTH, camera.getPosition().y - Utils.WORLD_HEIGHT/2 + MinionKamikazeView.RADIUS *2 + i*Utils.WORLD_HEIGHT/10, game, this);
				mk.velocity.x = -240f;
				minions.add(mk);
			}
			
			for(int i=0; i<10; i++){
				MinionKamikaze mk = new MinionKamikaze(i*(Utils.WORLD_WIDTH/10) + MinionKamikazeView.RADIUS*2, camera.getPosition().y - Utils.WORLD_HEIGHT/2 - MinionKamikazeView.RADIUS *2, game, this);
				mk.velocity.y = 240f;
				minions.add(mk);
			}	
			spawnedWave11 = true;
		}
		
		//10 from each side, full surround
		if(levelTimer > 55f && !spawnedWave12){
			for(int i=0; i<10; i++){
				MinionKamikaze mk = new MinionKamikaze(i*(Utils.WORLD_WIDTH/10) + MinionKamikazeView.RADIUS*2, camera.getPosition().y + Utils.WORLD_HEIGHT/2 + MinionKamikazeView.RADIUS *2, game, this);
				mk.velocity.y = -240f;
				minions.add(mk);
			}
			
			for(int i=0; i<10; i++){
				MinionKamikaze mk = new MinionKamikaze(MinionKamikazeView.RADIUS*-2, camera.getPosition().y - Utils.WORLD_HEIGHT/2 + MinionKamikazeView.RADIUS *2 + i*Utils.WORLD_HEIGHT/10, game, this);
				mk.velocity.x = 240f;
				minions.add(mk);
			}
			
			for(int i=0; i<10; i++){
				MinionKamikaze mk = new MinionKamikaze(MinionKamikazeView.RADIUS*2 + Utils.WORLD_WIDTH, camera.getPosition().y - Utils.WORLD_HEIGHT/2 + MinionKamikazeView.RADIUS *2 + i*Utils.WORLD_HEIGHT/10, game, this);
				mk.velocity.x = -240f;
				minions.add(mk);
			}
			
			for(int i=0; i<10; i++){
				MinionKamikaze mk = new MinionKamikaze(i*(Utils.WORLD_WIDTH/10) + MinionKamikazeView.RADIUS*2, camera.getPosition().y - Utils.WORLD_HEIGHT/2 - MinionKamikazeView.RADIUS *2, game, this);
				mk.velocity.y = 240f;
				minions.add(mk);
			}	
			spawnedWave12 = true;
		}
		
		//10 from each side, surround again, faster
		if(levelTimer > 60f && !spawnedWave13){
			for(int i=0; i<10; i++){
				MinionKamikaze mk = new MinionKamikaze(i*(Utils.WORLD_WIDTH/10) + MinionKamikazeView.RADIUS*2, camera.getPosition().y + Utils.WORLD_HEIGHT/2 + MinionKamikazeView.RADIUS *2, game, this);
				mk.velocity.y = -300f;
				minions.add(mk);
			}
			
			for(int i=0; i<10; i++){
				MinionKamikaze mk = new MinionKamikaze(MinionKamikazeView.RADIUS*-2, camera.getPosition().y - Utils.WORLD_HEIGHT/2 + MinionKamikazeView.RADIUS *2 + i*Utils.WORLD_HEIGHT/10, game, this);
				mk.velocity.x = 300f;
				minions.add(mk);
			}
			
			for(int i=0; i<10; i++){
				MinionKamikaze mk = new MinionKamikaze(MinionKamikazeView.RADIUS*2 + Utils.WORLD_WIDTH, camera.getPosition().y - Utils.WORLD_HEIGHT/2 + MinionKamikazeView.RADIUS *2 + i*Utils.WORLD_HEIGHT/10, game, this);
				mk.velocity.x = -300f;
				minions.add(mk);
			}
			
			for(int i=0; i<10; i++){
				MinionKamikaze mk = new MinionKamikaze(i*(Utils.WORLD_WIDTH/10) + MinionKamikazeView.RADIUS*2, camera.getPosition().y - Utils.WORLD_HEIGHT/2 - MinionKamikazeView.RADIUS *2, game, this);
				mk.velocity.y = 300f;
				minions.add(mk);
			}	
			spawnedWave13 = true;
		}
		
		//give a bit of a short break...
		
		if(levelTimer > 68f && levelTimer < 90f){
			if(surroundSpawnTimer >= surroundSpawnTimerCD){
				surroundSpawnTimer = 0;
				for(int i=0; i<10; i++){
					MinionKamikaze mk = new MinionKamikaze(i*(Utils.WORLD_WIDTH/10) + MinionKamikazeView.RADIUS*2, camera.getPosition().y + Utils.WORLD_HEIGHT/2 + MinionKamikazeView.RADIUS *2, game, this);
					mk.velocity.y = -120f;
					minions.add(mk);
				}
				
				for(int i=0; i<10; i++){
					MinionKamikaze mk = new MinionKamikaze(MinionKamikazeView.RADIUS*-2, camera.getPosition().y - Utils.WORLD_HEIGHT/2 + MinionKamikazeView.RADIUS *2 + i*Utils.WORLD_HEIGHT/10, game, this);
					mk.velocity.x = 120f;
					minions.add(mk);
				}
				
				for(int i=0; i<10; i++){
					MinionKamikaze mk = new MinionKamikaze(MinionKamikazeView.RADIUS*2 + Utils.WORLD_WIDTH, camera.getPosition().y - Utils.WORLD_HEIGHT/2 + MinionKamikazeView.RADIUS *2 + i*Utils.WORLD_HEIGHT/10, game, this);
					mk.velocity.x = -120f;
					minions.add(mk);
				}
				
				for(int i=0; i<10; i++){
					MinionKamikaze mk = new MinionKamikaze(i*(Utils.WORLD_WIDTH/10) + MinionKamikazeView.RADIUS*2, camera.getPosition().y - Utils.WORLD_HEIGHT/2 - MinionKamikazeView.RADIUS *2, game, this);
					mk.velocity.y = 120f;
					minions.add(mk);
				}
			} else {
				surroundSpawnTimer += deltaTime;
			}
		}
	}
	
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
			m.present(deltaTime, minionKamikazeView, effectsStatusView, minionBatcher);
		}
		super.present2(deltaTime);
	}

}
