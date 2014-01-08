public class Assassin extends Hero {

	public Assassin(float x, float y, float radius, OuyaGame game, OuyaScreen screen, int playerNumber) {
		super(x, y, radius, game, screen, playerNumber);
		view = new AssassinView(game, playerNumber);
	}

	@Override
	protected void updateSpellEffectsAndTimer(float deltaTime) {
		super.updateSpellEffectsAndTimer(deltaTime);
	}
	
	@Override
	protected void checkSpellsWithEnemiesAndAllies(float deltaTime) {

	}

	@Override
	protected void checkSpellsWithBoss(float deltaTime) {
		
	}
	
	@Override
	protected void updatePositionWithParallaxBackground(float deltaTime){
		super.updatePositionWithParallaxBackground(deltaTime);
	}
	
	@Override
	protected void castSpell1() {

	}

	@Override
	protected void castSpell2() {

	}

	@Override
	protected void castSpell3() {

	}

	@Override
	protected void castSpell4() {

	}

	@Override
	public void present(float deltaTime){		
		super.present(deltaTime);
	}
	
	// =========================  Spells  ============================
	
	// =========================  View  ============================
	
	public class AssassinView extends HeroView {
		public AssassinView(OuyaGame game, int playerNumber) {
			super(game, playerNumber);
			textureShip = new Texture(game, "heros/assassin/assassin.png");
			regionShip = new TextureRegion(textureShip, 0, 0, HeroView.RADIUS*2, HeroView.RADIUS*2);
		}
	}
}
