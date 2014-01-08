public class Level2 extends Level {

	@Override
	public Screen getStartScreen() {
		return new Level2Screen(this);
	}
	
}
