import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;

public class TrainingScreen extends OuyaScreen {
	public static final int MAX_NUMBER_ENEMIES = 1000;
	public static final int MAX_NUMBER_BULLETS = 1000;
	private static final int END_TIME_LIMIT = 90;
	LevelBackgroundTerrain backgroundView;
	boolean didHeroDie = false;
	boolean hasToldUserGameOver;
	GameOverView gameOverView;
	GamePausedView gamePausedView;
	private float spawnTimer;
	private final float spawnTimerCD = 2f;
	
	ShipExplosionView shipExplosionView;
	SpriteBatcher shipExplosionBatcher;
	
	MinionView minionView1;
	MinionView minionView2;
	MinionView minionView3;
	HeroEnemyEffectsStatusView heroEnemyEffectsStatusView;
	
	public TrainingScreen(OuyaGame game) {
		super(game);
		PARALLAX_SPEED = 0;
		backgroundView = new LevelBackgroundTerrain((OuyaGame)game);
		isCameraCapped = true;
		screenLevel = 5;
		
		Hero h = Hero.generateHeroFromName(game.getIntent().getStringExtra("player1"), Utils.WORLD_WIDTH/2, 200, HeroView.RADIUS, game, this, 0);
		h.initializeByLevel(10);
		h.isInTrainingMode = true;
		herosList.add(h);
		
		heroEnemyStatusView = new HeroEnemyStatusView((OuyaGame)game);
		heroEnemyEffectsStatusView = new HeroEnemyEffectsStatusView((OuyaGame)game);
		batcherStatus = new SpriteBatcher(glGraphics, herosList.size() * HeroEnemyStatusView.NUMBER_OF_SPRITES);
		minionBulletView = new MinionBulletView(game);
		minionBulletBatcher = new SpriteBatcher(game.getGLGraphics(), MAX_NUMBER_BULLETS);
		effectsStatusView = new HeroEnemyEffectsStatusView(game);
		minionBatcher = new SpriteBatcher(game.getGLGraphics(), MAX_NUMBER_ENEMIES);
		
		minionView1 = new MinionKamikaze.MinionKamikazeView(game);
		minionView2 = new MinionRedCoreType1.MinionRedCoreType1View(game);
		minionView3 = new MinionRedCoreType2.MinionRedCoreType2View(game);
		
		shipExplosionView = new ShipExplosionView(game);
		shipExplosionBatcher = new SpriteBatcher(game.getGLGraphics(), MAX_NUMBER_ENEMIES);
		gameOverView = new GameOverView((OuyaGame)game);
		gamePausedView = new GamePausedView((OuyaGame)game);
		hasToldUserGameOver = false;
	}

	@Override
	public void update(float deltaTime) {
		if(((OuyaGame)game).isPaused){
			for(Hero h : herosList){
				if(h.hasUserPressedExitUponPause){
					((OuyaGame)game).finish();
				}
			}
			return;
		}
		
		backgroundView.update(deltaTime);
		
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
		
		updateSpawning(deltaTime);
		
		Iterator<ShipExplosion> iterSE = explosions.iterator();
		ShipExplosion se;
		while(iterSE.hasNext()){
			se = iterSE.next();
			se.update(deltaTime);
			if(se.isDoneExploding){
				iterSE.remove();
			}
		}
		
		for(Minion m : minions){
			m.update(deltaTime);
		}
		
		for(Hero h : herosList){
			h.update(deltaTime);
		}
		
		updateBulletsOffScreen(deltaTime);
	}

	private void updateSpawning(float deltaTime){
		if(spawnTimer >= spawnTimerCD){
			spawnRandom();
			spawnTimer = 0;
		} else {
			spawnTimer += deltaTime;
		}
		
		if(spawnTimer >= END_TIME_LIMIT){
			didHeroDie = true;
		}
	}
	private void spawnRandom(){
		int r = Utils.getRandomInt(3);
		Minion m = null;
		float x, y;
		x = Utils.getRandomInt((int)Utils.WORLD_WIDTH);
		y = Utils.getRandomInt((int)Utils.WORLD_HEIGHT);
		switch(r){
		case 0:
			m = new MinionKamikaze(x, y, (OuyaGame)game, this);
			break;
		case 1:
			m = new MinionRedCoreType1(x, y, (OuyaGame)game, this);
			m.setBulletVelocity((Utils.getRandomFloat()*10f - 5) * Utils.FPS, (Utils.getRandomFloat()*10 - 5) * Utils.FPS);
			break;
		case 2:
			m = new MinionRedCoreType2(x, y, (OuyaGame)game, this);
			break;
		}
		minions.add(m);
	}
	private void updateBulletsOffScreen(float deltaTime){
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
		}
	}
	
	@Override 
	public void present(float deltaTime) {
		GL10 gl = glGraphics.getGL();
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        camera.setViewportAndMatrices();

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        
        backgroundView.present(deltaTime);
        
        for(ShipExplosion se : explosions){
			se.present(deltaTime, shipExplosionBatcher, shipExplosionView);
		}
        
        for(MinionBullet mb : minionBullets){
			mb.present(deltaTime, minionBulletBatcher, minionBulletView);
		}
        
        for(Minion m : minions){
        	if(m instanceof MinionKamikaze){
        		m.present(deltaTime, minionView1, heroEnemyEffectsStatusView, minionBatcher);
        	} else if(m instanceof MinionRedCoreType1){
        		m.present(deltaTime, minionView2, heroEnemyEffectsStatusView, minionBatcher);
        	} else if(m instanceof MinionRedCoreType2){
        		m.present(deltaTime, minionView3, heroEnemyEffectsStatusView, minionBatcher);
        	} 
        }
        
        for(Hero h : herosList){
			h.present(deltaTime);
		}
        
        drawHeroStatuses();
        
        if(((OuyaGame)game).isPaused){
			gamePausedView.draw();
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
	}
	
	protected void endGame(boolean victory){
		((OuyaGame)game).finish();
	}
	
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void dispose() {}
}
