public class SandboxScreen extends OuyaScreen {
	LevelBackground backgroundView;
	public float PARALLAX_SPEED = 500f;
	public static final int NUM_BACKGROUNDS = 8;
	
	public SandboxScreen(OuyaGame game) {
		super(game);
		backgroundView = new LevelBackground((OuyaGame)game);
//		backgroundView.setContinuingForever();
		
		this.herosList.add(new Caster(500, 200, HeroView.RADIUS, game, this, 0));
	}

	@Override
	public void update(float deltaTime) {
		for(Hero h: herosList){
			h.update(deltaTime);
		}
		updateParallaxBackground(deltaTime);
		backgroundView.update(deltaTime, camera);
	}

	@Override
	protected void updateParallaxBackground(float deltaTime) {
		isCameraCapped = true;
	}
	
	@Override
	public void present(float deltaTime) {
        GL10 gl = glGraphics.getGL();
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        camera.setViewportAndMatrices();

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        
		backgroundView.present(deltaTime, isCameraCapped);
		
		for(Hero h:herosList){
			h.present(deltaTime); 
		}
	}

	
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void dispose() { }
}
