public class DeadBoss extends Boss {

	List<Minion> minions;
	
	public DeadBoss(float x, float y, OuyaGame game, OuyaScreen screen) {
		super(x, y, game, screen);

		minions = new LinkedList<Minion>();
	}

	@Override
	public boolean isColliding(DynamicGameObject other, float damage) {
		return false;
	}

	@Override
	public void update(float deltaTime) {

	}

	@Override
	protected void justDied(float deltaTime) {

	}

	@Override
	protected void updateDieing(float deltaTime) {

	}

	@Override
	public void present(float deltaTime) {

	}

	@Override
	public List<? extends Minion> getNonDamageableParts() {
		return minions;
	}

}
