public class Level10Boss extends Boss {

	View view;
	SpriteBatcher batcher;
	List<Minion> nonDamageableParts;
	float animationTime;
	
	public Level10Boss(float x, float y, OuyaGame game, OuyaScreen screen) {
		super(x, y, game, screen);
		view = new View(game, "bosses/bug/part1.png", 8, 256, 256, 1024, 0.1f);
		batcher = new SpriteBatcher(game.getGLGraphics(), 1);
		
		nonDamageableParts = new ArrayList<Minion>();
		
		currentHP = maxHP = 500;
	}

	@Override
	public boolean isColliding(DynamicGameObject other, float damage) {
		return false;
	}

	@Override
	public List<? extends Minion> getNonDamageableParts() {
		return nonDamageableParts;
	}

	@Override
	public void update(float deltaTime) {
		animationTime += deltaTime;
	}

	@Override
	protected void justDied(float deltaTime) {

	}

	@Override
	protected void updateDieing(float deltaTime) {

	}

	@Override
	public void present(float deltaTime) {
		if(!isDead){
			batcher.beginBatch(view.getTexture());
			batcher.drawSprite(getPosition().x, getPosition().y, 256*2, 256*2, view.getAnimation().getKeyFrame(animationTime, Animation.ANIMATION_LOOPING));
			batcher.endBatch();
		}
	}

	
	private class View extends MinionView{
		
		public View(OuyaGame game, String spriteSheetName, int spritesPerSheet,	int width, int height, int spriteSheetWidth, float animationTimeInterval) {
			super(game, spriteSheetName, spritesPerSheet, width, height, spriteSheetWidth, animationTimeInterval);
		}

		@Override
		public float getWidth() {
			return 256;
		}

		@Override
		public float getHeight() {
			return 256;
		}
		
	}
}
