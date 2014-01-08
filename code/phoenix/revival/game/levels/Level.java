public abstract class Level extends OuyaGame {
	public Music backgroundMusic;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		backgroundMusic = getAudio().newMusic("music/background_game.ogg");
		backgroundMusic.setLooping(true);
		backgroundMusic.setVolume(0.65f);
	}
	
	@Override
	public void onPause(){
		backgroundMusic.pause();
		super.onPause();
	}
	
	@Override
	public void onResume(){
		backgroundMusic.play();
		super.onResume();
	}
}
