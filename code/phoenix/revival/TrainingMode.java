import android.os.Bundle;

public class TrainingMode extends OuyaGame {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public Screen getStartScreen() {
		return new TrainingScreen(this);
	}
}
