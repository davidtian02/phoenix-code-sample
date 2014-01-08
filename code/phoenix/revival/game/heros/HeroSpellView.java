public class HeroSpellView {
	Texture texture;
	TextureRegion regions[];
	Animation animation;
	
	public HeroSpellView(OuyaGame game, String spriteSheetName, int spritesPerSheet, int spritesPerRow, int width, int height, float animationTimeInterval){
		texture = new Texture(game, spriteSheetName);
		regions = new TextureRegion[spritesPerSheet];
		for(int i=0; i<regions.length; i++){
			regions[i] = new TextureRegion(texture, (i%spritesPerRow) * width, (i/spritesPerRow) * height, width, height);
		}
		animation = new Animation(animationTimeInterval, regions);
	}
}
