public class Level7 extends Level {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		backgroundMusic = getAudio().newMusic("music/background_final_boss.ogg");
		backgroundMusic.setLooping(true);
		backgroundMusic.setVolume(0.65f);
	}
	
	@Override
	public Screen getStartScreen() {
		return new Level7Screen(this);
	}
	
}
