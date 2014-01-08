public class Level1 extends Level {
	
	@Override
	public Screen getStartScreen() {
		return new Level1Screen(this);
	}

}
