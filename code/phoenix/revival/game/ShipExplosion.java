public class ShipExplosion extends GameObject {
	
	public float explosionTime;
	public boolean isDoneExploding;
	private static final float EXPLOSION_LENGTH_TIME = 1f;
	
	public ShipExplosion(float x, float y, float radius, OuyaGame game) {
		super(x, y, radius);
		explosionTime = 0;
		isDoneExploding = false;
		MusicAndSound.playSoundShipExplosion();
	}

	public void update(float deltaTime){
		explosionTime += deltaTime;
		if(explosionTime > EXPLOSION_LENGTH_TIME){
			isDoneExploding = true;
		}
	}
	
	public void present(float deltaTime, SpriteBatcher shipExplosionBatcher, ShipExplosionView shipExplosionView) {
		shipExplosionBatcher.beginBatch(shipExplosionView.texture);
		shipExplosionBatcher.drawSprite(getPosition().x, getPosition().y, ShipExplosionView.RADIUS*2, ShipExplosionView.RADIUS*2, shipExplosionView.animation.getKeyFrame(explosionTime, Animation.ANIMATION_LOOPING));
		shipExplosionBatcher.endBatch();
	}
	
	public static class ShipExplosionView{
		public static final float RADIUS = 128;
		public Texture texture;
		TextureRegion regions[];
		public Animation animation;
		public ShipExplosionView(OuyaGame game){
			texture = new Texture(game, "ship_explosion.png");
			regions = new TextureRegion[6];
			for(int i=0; i<regions.length; i++){
				regions[i] = new TextureRegion(texture, (i%4) * RADIUS*2, (i/4) * RADIUS*2, RADIUS*2, RADIUS*2);
			}
			animation = new Animation(EXPLOSION_LENGTH_TIME/6, regions);
		}
	}
}
