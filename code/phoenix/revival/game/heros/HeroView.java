public abstract class HeroView {
	public static final float RADIUS = 64;
	
	OuyaGame game;
	
	Texture textureShip;
	TextureRegion regionShip;
	
	Texture textureShield;
	TextureRegion regionShield;
	
	TextureRegion regionDroneBullet; //TODO eventually, move this away from current sprite sheet and put with drones? 
	
	Animation animationBullets;
	Texture textureBullets;
	TextureRegion regionBullet;
	TextureRegion regionsBullets[];
	
	public HeroView(OuyaGame game, int playerNumber){
		this.game = game;
		regionsBullets = new TextureRegion[6];
		
		int positionYOffsetBulletsSprite = 0; // offset position Y on the sprite itself
		int positionXOffsetShieldsSprite = 0;
		switch(playerNumber){
		case PlayersManager.PLAYER_1_ID: positionXOffsetShieldsSprite =   0; positionYOffsetBulletsSprite = 128; break;
		case PlayersManager.PLAYER_2_ID: positionXOffsetShieldsSprite = 256; positionYOffsetBulletsSprite =   0; break;
		case PlayersManager.PLAYER_3_ID: positionXOffsetShieldsSprite = 512; positionYOffsetBulletsSprite = 256; break;
		case PlayersManager.PLAYER_4_ID: positionXOffsetShieldsSprite = 768; positionYOffsetBulletsSprite = 384; break; //TODO need green bullet on the actual sprite sheet
		}
		textureShield = new Texture(game, "heros/shields.png");
		regionShield = new TextureRegion(textureShield, positionXOffsetShieldsSprite, 0, Shield.RADIUS*4, Shield.RADIUS*4);

		textureBullets = new Texture(game, "bullets_sprite.png");
		for(int i=0; i<regionsBullets.length; i++){
			regionsBullets[i] = new TextureRegion(textureBullets, 64*i - 3 < 0 ? 0 : 64*i - 3, positionYOffsetBulletsSprite, Bullet.ANIMATION_WIDTH, Bullet.ANIMATION_HEIGHT);
		}
		regionBullet = new TextureRegion(textureBullets, 64*(regionsBullets.length-1)-3, positionYOffsetBulletsSprite, Bullet.WIDTH-1, Bullet.HEIGHT);
		animationBullets = new Animation(0.01f, regionsBullets);
		
		regionDroneBullet = new TextureRegion(textureBullets, 384, 256, 64, 64);
	}
}
