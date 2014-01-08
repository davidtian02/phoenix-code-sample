public class LevelBackgroundTerrain {
	Texture texture;
	TextureRegion region;
	SpriteBatcher batcher;
	
	Texture textureClouds;
	TextureRegion regionClouds;
	
	public LevelBackgroundTerrain(OuyaGame game){
		texture = new Texture(game, "backgrounds/endless/metal_gates_back.png");
		region = new TextureRegion(texture, 0, 0, Utils.WORLD_WIDTH-10, Utils.WORLD_HEIGHT-10);
		batcher = new SpriteBatcher(game.getGLGraphics(), 1);
		
		textureClouds = new Texture(game, "backgrounds/endless/metal_gates_middle.png");
		regionClouds = new TextureRegion(texture, 0, 0, Utils.WORLD_WIDTH-10, Utils.WORLD_HEIGHT-10);
	}
	
	public void update(float deltaTime){
		
	}
	
	public void present(float deltaTime){
		batcher.beginBatch(texture);
    	batcher.drawSprite(Utils.WORLD_WIDTH/2, Utils.WORLD_HEIGHT/2, Utils.WORLD_WIDTH, Utils.WORLD_HEIGHT, region);
    	batcher.endBatch();
    	
    	batcher.beginBatch(textureClouds);
    	batcher.drawSprite(Utils.WORLD_WIDTH/2, Utils.WORLD_HEIGHT/2, Utils.WORLD_WIDTH, Utils.WORLD_HEIGHT, regionClouds);
    	batcher.endBatch();
	}
}
