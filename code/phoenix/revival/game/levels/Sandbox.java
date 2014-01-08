public class Sandbox extends OuyaGame {

	@Override
	public Screen getStartScreen() {
		return new SandboxScreen(this);
	}

}
