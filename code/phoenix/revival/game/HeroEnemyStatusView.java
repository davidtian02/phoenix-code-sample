public class HeroEnemyStatusView {
    
	public static final int NUMBER_OF_SPRITES = 7;

	public Texture texture;
	
    public TextureRegion regionHealthBar;
    public TextureRegion regionManaBar;
    public TextureRegion regionExperienceBar;
    
    public TextureRegion regionCooldown1;
    public TextureRegion regionCooldown2;
    public TextureRegion regionCooldown3;
    public TextureRegion regionCooldown4;
    
	public HeroEnemyStatusView(OuyaGame game) {
		texture = new Texture(game, "hero_enemy_status.png");
		
		regionHealthBar = new TextureRegion(texture, 0,0, 128, 10);
		regionManaBar = new TextureRegion(texture, 0,16, 128, 10);
		regionExperienceBar = new TextureRegion(texture, 0,32, 128, 10);
		
		regionCooldown1 = new TextureRegion(texture, 0, 48, 32, 10);
		regionCooldown2 = new TextureRegion(texture, 32, 48, 32, 10);
		regionCooldown3 = new TextureRegion(texture, 64, 48, 32, 10);
		regionCooldown4 = new TextureRegion(texture, 96, 48, 32, 10);
	}
}
