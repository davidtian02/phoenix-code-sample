public abstract class LevelScreen extends OuyaScreen {
	public static final int MAX_NUMBER_ENEMIES = 1000;
	public static final int MAX_NUMBER_BULLETS = 1000;
	
	//level generic stuff
	LevelBackground backgroundView;
	boolean didHeroDie;
	GameOverView gameOverView;
	GamePausedView gamePausedView;
	boolean hasToldUserGameOver;
	
	//views and stuff
	ShipExplosionView shipExplosionView;
	SpriteBatcher shipExplosionBatcher;
	CoinView coinView;
	SpriteBatcher coinBatcher;
	
	//for results
	Intent intentNextActivity;
	int bonusGold;
	int planetNumber;
	
	//spawning stuff
	float levelTimer;
	float spawnBossAtTime = 110f;
	boolean spawnedBoss;
	
	//misc
	OuyaGame game;
	
	//hero stuff
	HeroItemsViews heroItemsViews;
	List<String> heroItems;
	
	public LevelScreen(OuyaGame game) {
		super(game);
		this.game = game;
		backgroundView = new LevelBackground((OuyaGame)game);

		int fileNumber = ((OuyaGame)game).getIntent().getIntExtra("fileNumber", -1);
		
		List<String> heroClasses = GameData.getHerosFromFile(fileNumber);
		List<Integer> heroLevels = GameData.getHeroLevelsFromFile(fileNumber);
		List<Integer> heroExperiences = GameData.getHeroExperiencesFromFile(fileNumber);
		heroItems = GameData.getHeroItemsFromFile(fileNumber);
		
		for(int i=0; i<heroClasses.size(); i++){
			Hero h = Hero.generateHeroFromName(heroClasses.get(i), Utils.WORLD_WIDTH/2, Utils.WORLD_HEIGHT/7, HeroView.RADIUS, (OuyaGame)game, this, i);
			herosList.add(h);
		}
		Hero h;
		for(int i=0; i<herosList.size(); i++){
			h = herosList.get(i);
			h.initializeByLevel(heroLevels.get(i));
			h.setExperience(heroExperiences.get(i));
		}
		Hero.setSharedItems(heroItems);
		
		heroEnemyStatusView = new HeroEnemyStatusView((OuyaGame)game);
		batcherStatus = new SpriteBatcher(glGraphics, herosList.size() * HeroEnemyStatusView.NUMBER_OF_SPRITES);
		batcherBossStatus = new SpriteBatcher(glGraphics, 1);
		
		shipExplosionView = new ShipExplosionView(game);
		shipExplosionBatcher = new SpriteBatcher(game.getGLGraphics(), MAX_NUMBER_ENEMIES);
		
		coinView = new CoinView(game);
		coinBatcher = new SpriteBatcher(game.getGLGraphics(), MAX_NUMBER_ENEMIES);
		
		minionBulletView = new MinionBulletView(game);
		minionBulletBatcher = new SpriteBatcher(game.getGLGraphics(), MAX_NUMBER_BULLETS);
		
		effectsStatusView = new HeroEnemyEffectsStatusView(game);
		minionBatcher = new SpriteBatcher(game.getGLGraphics(), MAX_NUMBER_ENEMIES);
		
		didHeroDie = false;
		gameOverView = new GameOverView((OuyaGame)game);
		gamePausedView = new GamePausedView((OuyaGame)game);
		hasToldUserGameOver = false;
		
		heroItemsViews = new HeroItemsViews((OuyaGame)game);
	}

	//================================= updating methods ========================================
	
	@Override
	public void update(float deltaTime) {
		if(((OuyaGame)game).isPaused){
			for(Hero h : herosList){
				if(h.hasUserPressedExitUponPause){
					endGame(false);
					((OuyaGame)game).finish(); //REFACTOR wait... didn't this already get called by now?
				}
			}
			return;
		}
		
		if(didHeroDie){
			if(!hasToldUserGameOver){
				hasToldUserGameOver = true;
				MusicAndSound.playSoundGameOver();
				extraGameOverTimerForIdiotsThatHoldL1 = 0;
			}
			boolean shouldEndGameSoon = false;
			for(Hero h: herosList){
				if(h.exitAfterGameOver){
					shouldEndGameSoon = true;
				}
			}
			if(shouldEndGameSoon){
				extraGameOverTimerForIdiotsThatHoldL1 += deltaTime;
				if(extraGameOverTimerForIdiotsThatHoldL1 >= extraGameOverTimerForIdiotsThatHoldL1CD){
					endGame(false);
				}
			}
			
			return;
		}
		
		//TODO check all ships, bullets, minions for moving out of bounds (in which case u would remove)
		for(Hero h : herosList){
			h.update(deltaTime);
			if(h.isHeroDead()){
				didHeroDie = true;
			}
		}
		
		Iterator<Minion> iter;
		Minion m;
		for(List<Minion> list : enemies){
			iter = list.iterator();
			while(iter.hasNext()){
				m = iter.next();
				m.update(deltaTime);
				//remove if off screen:
				if(m.bounds instanceof Circle){
					if(Utils.isOutOfBoundsCompletely((Circle)m.bounds, camera.getEnemySpawnableBounds())){ 
						iter.remove();
					}
				} else if(m.bounds instanceof Rectangle){
					if(Utils.isOutOfBoundsCompletely((Rectangle)m.bounds, camera.getEnemySpawnableBounds())){
						iter.remove();
					}
				}
				m.updatePositionWithParallaxBackground(deltaTime, PARALLAX_SPEED);
			}
		}
		
		//when quasars die, they become neutron stars
		Minion mNeutronStar;
		if(enemies.size() >= 1){
			List<Minion> list = enemies.get(0); //FIXME check this?
			if(list != null){
				for(int i=0; i<list.size(); i++){
					m = list.get(i);
					if(m instanceof MinionQuasar){
						if(((MinionQuasar)m).shouldExplodeIntoNeutroStar){
							mNeutronStar = new MinionNeutronStar(m.getPosition().x, m.getPosition().y, game, this);
							mNeutronStar.setVelocity(m.velocity.x, m.velocity.y);
							list.set(i, mNeutronStar);
						}
					}
				}
			}
		}
		Iterator<MinionBullet> iterMinionBullet = minionBullets.iterator();
		MinionBullet mb;
		while(iterMinionBullet.hasNext()){
			mb = iterMinionBullet.next();
			mb.update(deltaTime);
			//remove if off screen:
			if(Utils.isOutOfBoundsCompletely((Circle)mb.bounds, camera.getBounds())){ 
				iterMinionBullet.remove();
				continue;
			}
			for(Hero h : herosList){
				if(h.isColliding(mb)){
					h.takeDamageAndCheckDies(mb.damage);
					iterMinionBullet.remove();
					break;
				}
			}
			mb.updatePositionWithParallaxBackground(deltaTime, PARALLAX_SPEED);
		}
		
		
		if(boss != null){
			boss.update(deltaTime);
		}
			
		Iterator<ShipExplosion> iterSE = explosions.iterator();
		ShipExplosion se;
		while(iterSE.hasNext()){
			se = iterSE.next();
			se.update(deltaTime);
			if(se.isDoneExploding){
				iterSE.remove();
				enemiesKilled++; //FIXME don't do this at the end! do this right when an enemy is killed
				coins.add(new Coin(se.getPosition().x, se.getPosition().y, CoinView.RADIUS));
			}
		}
		
		Iterator<Coin> iterCoin = coins.iterator();
		Coin c;
		while(iterCoin.hasNext()){
			c = iterCoin.next();
			if(!isCameraCapped){
				c.updateParallaxSpeed(deltaTime, PARALLAX_SPEED);
			}
			for(Hero h : herosList){
				if(c.isColliding(h)){
					iterCoin.remove();
					MusicAndSound.playCoinSound();
					coinsCollected++;
					break;
				}
			}
		}
		
		levelTimer += deltaTime;
		updateSpawningEnemies(deltaTime);
		updateBoss(deltaTime);
		
		updateParallaxBackground(deltaTime);
		backgroundView.update(deltaTime, camera);
	}
	
	protected abstract void updateSpawningEnemies(float deltaTime);
	
	protected void updateBoss(float deltaTime){
		boolean allEnemiesOffScreen = true;
		Iterator<Minion> iter;
		Minion m;
		if(levelTimer > spawnBossAtTime && !spawnedBoss){
			for(List<Minion> list : enemies){
				iter = list.iterator();
				while(iter.hasNext()){
					m = iter.next();
					if(m.bounds instanceof Circle){
						if(!Utils.isOutOfBoundsCompletely((Circle)m.bounds, camera.getBounds())){
							allEnemiesOffScreen = false;
						}
						if(Utils.isOutOfBoundsCompletely((Circle)m.bounds, camera.getBounds())){
							iter.remove();
						}
					} else if(m.bounds instanceof Rectangle){
						if(!Utils.isOutOfBoundsCompletely((Rectangle)m.bounds, camera.getBounds())){
							allEnemiesOffScreen = false;
						}
						if(Utils.isOutOfBoundsCompletely((Rectangle)m.bounds, camera.getBounds())){
							iter.remove();
						}
					}
				}
			}

			if(allEnemiesOffScreen && isCameraCapped){
				spawnBoss();
				spawnedBoss = true;
			}
		}
		
		if(boss!=null){
			if(boss.isDead){
				extraGameOverTimerForIdiotsThatHoldL1 += deltaTime;
				if(extraGameOverTimerForIdiotsThatHoldL1 >= extraGameOverTimerForIdiotsThatHoldL1CD){
					endGame(true);
				}
			}
		}
	}
	
	protected abstract void spawnBoss();
	
	//================================= actual drawing methods ========================================
	
	public abstract void present(float deltaTime);
	protected void present1(float deltaTime){
		GL10 gl = glGraphics.getGL();
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        camera.setViewportAndMatrices();

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        
        backgroundView.present(deltaTime, isCameraCapped);
        
		for(ShipExplosion se : explosions){
			se.present(deltaTime, shipExplosionBatcher, shipExplosionView);
		}
        
		for(Coin c : coins){
			c.present(deltaTime, coinBatcher, coinView);
		}
		
		for(MinionBullet mb : minionBullets){
			mb.present(deltaTime, minionBulletBatcher, minionBulletView);
		}
	}
	protected void present2(float deltaTime){
		if(boss!=null){
			boss.present(deltaTime);
		}
		
		for(Hero h : herosList){
			h.present(deltaTime);
		}
		
		drawHeroStatuses();
		
		if(boss != null){
			if(!boss.isDead){
				drawBossStatuses();
			}
		}
		
		if(((OuyaGame)game).isPaused){
			gamePausedView.draw();
		}
		
		if(didHeroDie){
			gameOverView.draw();
		}
	}

	protected void drawHeroStatuses(){
		float x, y;
		float heroRadius = HeroView.RADIUS;
		for(Hero h : herosList){
			x = h.getPosition().x;
			y = h.getPosition().y;
			batcherStatus.beginBatch(heroEnemyStatusView.texture);
			batcherStatus.drawSprite(x, y+heroRadius+10, 2f*heroRadius*h.getHPPercentage(), 10, heroEnemyStatusView.regionHealthBar);
			batcherStatus.drawSprite(x, y+heroRadius, 2f*heroRadius*h.getManaPercentage(), 10, heroEnemyStatusView.regionManaBar);
			batcherStatus.drawSprite(x, y-heroRadius+5.5f, 2f*heroRadius*h.getExpPercentage(), 7, heroEnemyStatusView.regionExperienceBar);
			
			batcherStatus.drawSprite(x-heroRadius + 16, y-heroRadius, 32f*h.getSpell1CDPercentage(), 7, heroEnemyStatusView.regionCooldown1);
			batcherStatus.drawSprite(x-heroRadius + 48, y-heroRadius, 32f*h.getSpell2CDPercentage(), 7, heroEnemyStatusView.regionCooldown2);
			batcherStatus.drawSprite(x-heroRadius + 80, y-heroRadius, 32f*h.getSpell3CDPercentage(), 7, heroEnemyStatusView.regionCooldown3);
			batcherStatus.drawSprite(x-heroRadius + 112, y-heroRadius, 32f*h.getSpell4CDPercentage(), 7, heroEnemyStatusView.regionCooldown4);
			batcherStatus.endBatch();
		}
		
		//drawing items:
		if(Hero.getSharedItems().contains(Items.REPAIR_KIT)){
			batcherStatus.beginBatch(heroItemsViews.textureRepairKit);
			batcherStatus.drawSprite(camera.getPosition().x + 730, camera.getPosition().y - 470, 90, 90, heroItemsViews.regionRepairKit);
			batcherStatus.endBatch();
		}
		
		if(Hero.getSharedItems().contains(Items.ENERGY_CELL)){
			batcherStatus.beginBatch(heroItemsViews.textureEnergyCell);
			batcherStatus.drawSprite(camera.getPosition().x + 790, camera.getPosition().y - 470, 90, 90, heroItemsViews.regionEnergyCell);
			batcherStatus.endBatch();
		}
		
		if(Hero.getSharedItems().contains(Items.PHOENIX_REVIVAL)){
			batcherStatus.beginBatch(heroItemsViews.texturePhoenixRevival);
			batcherStatus.drawSprite(camera.getPosition().x + 850, camera.getPosition().y - 470, 90, 90, heroItemsViews.regionPhoenixRevival);
			batcherStatus.endBatch();
		}
    }
	
	protected void drawBossStatuses(){
		batcherBossStatus.beginBatch(heroEnemyStatusView.texture);
		batcherBossStatus.drawSprite(Utils.WORLD_WIDTH/2, camera.getPosition().y + Utils.WORLD_HEIGHT/2 - 30,  Utils.WORLD_WIDTH * boss.getHpPercentage(),  20, heroEnemyStatusView.regionHealthBar);
		batcherBossStatus.endBatch();
	}
	
	//================================= other screen methods ========================================
	
	@Override
	public void pause() {}
	@Override
	public void resume() {}
	@Override
	public void dispose() {}
	
	//============================ helper methods ================================
	
	protected void endGame(boolean victory){
		int fileNumber = ((OuyaGame)game).getIntent().getIntExtra("fileNumber", -1);
		intentNextActivity = new Intent((OuyaGame)game, LevelResultsScreen.class);
		int level = -1;
		int exp = -1;
		for(Hero h: herosList){
			if(victory){ //bonus exp and gold
				h.gainExp(boss.dropExperience/herosList.size());
			}
			level = h.getLevel();
			exp = h.getCurrentExp();
		}
		if(victory){
			coinsCollected += bonusGold;
		}
		
		GameData.setHeroExperiencesInFile(((OuyaGame)game), fileNumber, exp);
		GameData.setHeroLevelsInFile((OuyaGame)game, fileNumber, level);
		GameData.setNumberOfCoinsInFile((OuyaGame)game, fileNumber, coinsCollected + GameData.getNumberOfCoinsInFile(fileNumber));
		GameData.setHeroItemsInFile((OuyaGame)game, fileNumber, Hero.getSharedItems());
		
		intentNextActivity.putExtra("resultWon", victory);
		intentNextActivity.putExtra("coinsCollected", coinsCollected);
		intentNextActivity.putExtra("enemiesKilled", enemiesKilled);
		intentNextActivity.putExtra("planetNumber", planetNumber);
		intentNextActivity.putExtra("fileNumber", fileNumber);
		((OuyaGame)game).startActivity(intentNextActivity);
		((OuyaGame)game).finish();
	}
	
}
