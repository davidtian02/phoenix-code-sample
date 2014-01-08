public class Coin extends DynamicGameObject{
	
	public Coin(float x, float y, float radius) {
		super(x, y, CoinView.RADIUS);
	}

	public void updateParallaxSpeed(float deltaTime, float speed){
		setPosition(getPosition().x, getPosition().y+deltaTime*speed);
	}
	
	public void present(float deltaTime, SpriteBatcher coinBatcher, CoinView coinView) {
		coinBatcher.beginBatch(coinView.texture);
		coinBatcher.drawSprite(getPosition().x, getPosition().y, CoinView.RADIUS*2, CoinView.RADIUS*2, coinView.region);
		coinBatcher.endBatch();
	}
	
	public static class CoinView{
		public static final int RADIUS = 32; 
		public Texture texture;
		public TextureRegion region;
		public CoinView(OuyaGame game){
			texture = new Texture(game, "coin.png");
			region = new TextureRegion(texture, 0, 0, RADIUS*2, RADIUS*2);
		}
	}

}
