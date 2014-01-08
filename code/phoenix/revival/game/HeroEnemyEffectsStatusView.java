public class HeroEnemyEffectsStatusView {
	
	Texture textureFrosted;
    TextureRegion regionFrosted;
    
	public HeroEnemyEffectsStatusView(OuyaGame game) {
		textureFrosted = new Texture(game, "status_effects/frosted.png");
	    regionFrosted = new TextureRegion(textureFrosted, 0, 0, 256, 256);
	}
	
	public Texture getTextureFrosted(){return textureFrosted;}
	public TextureRegion getTextureRegionFrosted(){return regionFrosted;}
}
