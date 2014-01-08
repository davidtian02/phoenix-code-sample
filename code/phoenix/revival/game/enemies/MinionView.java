public abstract class MinionView {
	OuyaGame game;
	
	private Texture texture;
	TextureRegion regions[];
	private Animation animation;
	
	public MinionView(OuyaGame game, String spriteSheetName, int spritesPerSheet, int width, int height, int spriteSheetWidth, float animationTimeInterval){
		this.game = game;
		texture = new Texture(game, spriteSheetName);
		regions = new TextureRegion[spritesPerSheet];
		for(int i=0; i<regions.length; i++){
			regions[i] = new TextureRegion(getTexture(), (i*width) % spriteSheetWidth, (i*height)/spriteSheetWidth * height, width, height);
		}
		animation = new Animation(animationTimeInterval, regions);
	}
	
	//REFACTOR this and the above constructor can be combined, if the spriteSheetHeight parameter is like... 0
	public MinionView(OuyaGame game, String spriteSheetName, int spritesPerSheet, int width, int height, int spriteSheetWidth, int spriteSheetHeight, float animationTimeInterval){
		this.game = game;
		texture = new Texture(game, spriteSheetName);
		regions = new TextureRegion[spritesPerSheet];
		for(int i=0; i<regions.length; i++){
			regions[i] = new TextureRegion(getTexture(), (i*width) % spriteSheetWidth, (i*height)/spriteSheetHeight * height, width, height);
		}
		animation = new Animation(animationTimeInterval, regions);
	}

	public Texture getTexture() { return texture;}
	public Animation getAnimation() { return animation;}
	
	public static class MinionBulletView {
		public static final float RADIUS = 16;
		Texture textureBullets;
		TextureRegion regionsBullets[];
		Animation animationBullets;
		public MinionBulletView(OuyaGame game){
			regionsBullets = new TextureRegion[8];
			
			textureBullets = new Texture(game, "bullets_sprite.png");
			for(int i=0; i<regionsBullets.length; i++){
				regionsBullets[i] = new TextureRegion(textureBullets, 64*i, 960, 64, 64);
			}
			animationBullets = new Animation(0.05f, regionsBullets);
		}
	}
	
	public abstract float getWidth();
	public abstract float getHeight();
}

