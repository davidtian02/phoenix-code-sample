public class LevelBackground{
	public static final int NUM_BACKGROUNDS = 8;
	public static final float WIDTH = Utils.WORLD_WIDTH;
	public static final float HEIGHT = 2048;
	Texture textureBackgrounds[];
	TextureRegion regionBackgrounds[]; 
	SpriteBatcher batcherBackground;
	float yPositions[];
	int currentBackgroundIndex;
	int previousBackgroundIndex;
	boolean continueIndefinitely;
	
	public LevelBackground(OuyaGame game){
		textureBackgrounds = new Texture[NUM_BACKGROUNDS];
		regionBackgrounds = new TextureRegion[NUM_BACKGROUNDS];
		yPositions = new float[NUM_BACKGROUNDS];
		for(int i=0; i<NUM_BACKGROUNDS; i++){
			textureBackgrounds[i] = new Texture((OuyaGame)game, "backgrounds/level1/piece_" + (i+1) + ".jpg");
			regionBackgrounds[i] = new TextureRegion(textureBackgrounds[i], 0, 0, WIDTH, HEIGHT);
			yPositions[i] = (i*HEIGHT) + HEIGHT/2;
		}

		batcherBackground = new SpriteBatcher(game.getGLGraphics(), 1);
	}
	
	public void update(float deltaTime, Camera2D camera){
		if(continueIndefinitely){
			if(camera.getPosition().y > yPositions[currentBackgroundIndex]){
				yPositions[previousBackgroundIndex] += NUM_BACKGROUNDS * HEIGHT;
				previousBackgroundIndex = currentBackgroundIndex;
				if(currentBackgroundIndex < NUM_BACKGROUNDS-1){
					currentBackgroundIndex++; 
				} else {
					currentBackgroundIndex = 0;
				}
			}
		} else {
			if(camera.getPosition().y > yPositions[currentBackgroundIndex]){
				if(currentBackgroundIndex < NUM_BACKGROUNDS-1){
					previousBackgroundIndex = currentBackgroundIndex;
					currentBackgroundIndex++;
				}
			}
		}
		
	}
	
	public void present(float deltaTime, boolean isCameraCapped){
		batcherBackground.beginBatch(textureBackgrounds[currentBackgroundIndex]);
    	batcherBackground.drawSprite(Utils.WORLD_WIDTH/2, yPositions[currentBackgroundIndex], WIDTH+1, HEIGHT, regionBackgrounds[currentBackgroundIndex]);
    	batcherBackground.endBatch();
    	
		if(!isCameraCapped){
			batcherBackground.beginBatch(textureBackgrounds[previousBackgroundIndex]);
        	batcherBackground.drawSprite(Utils.WORLD_WIDTH/2, yPositions[previousBackgroundIndex], WIDTH+1, HEIGHT, regionBackgrounds[previousBackgroundIndex]);
        	batcherBackground.endBatch();
		}
	}
	
	public void setContinuingForever(){
		continueIndefinitely = true;
	}
}