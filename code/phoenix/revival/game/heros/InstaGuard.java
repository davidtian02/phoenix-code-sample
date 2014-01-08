//note: this class just adds extra hp, that's all, but much easier to put it here
class InstaGuard extends DynamicGameObject{
	static float RADIUS = 64;
	boolean isDone;
	float hp;
	float animationTime;
	public InstaGuard(float x, float y, float radius, OuyaGame game) {
		super(x, y, radius);
		hp = 180;
		animationTime = 0;
	}
	public void update(float deltaTime, float x, float y){
		animationTime += deltaTime;
		setPosition(x, y);
	}
	public void updatePositionWithParallaxBackground(float deltaTime, float parallaxSpeed){
		setPosition(getPosition().x, getPosition().y+parallaxSpeed*deltaTime);
	}
	public void present(float deltaTime, SpriteBatcher instaGuardBatcher, InstaGuardView instaGuardView){
		instaGuardBatcher.beginBatch(instaGuardView.texture);
		instaGuardBatcher.drawSprite(getPosition().x, getPosition().y, RADIUS*2, RADIUS*2, instaGuardView.animation.getKeyFrame(animationTime, Animation.ANIMATION_LOOPING));
		instaGuardBatcher.endBatch();
	}
	public float takeDamage(float dmg){
		hp -= dmg;
		if(hp<=0){
			isDone = true;
			return -hp; //so can be subtracted
		}
		return 0;
	}
	public static class InstaGuardView extends HeroSpellView{
		public InstaGuardView(OuyaGame game) {
			super(game, "heros/healer/healer_spell_3_insta_guard.png", 8, 4, (int)RADIUS*2, (int)RADIUS*2, 0.1f);
		}
	}
}