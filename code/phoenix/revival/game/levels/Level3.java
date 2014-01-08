public class Level3 extends Level {

	@Override
	public Screen getStartScreen() {
		return new Level3Screen(this);
	}
	
}
