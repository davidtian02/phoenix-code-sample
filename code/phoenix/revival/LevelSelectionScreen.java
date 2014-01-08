import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LevelSelectionScreen extends Activity {
	Intent intent;
	int fileNumber;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_level_selection_screen);

		fileNumber = getIntent().getIntExtra("fileNumber", -1);
		int planets = GameData.getPlanetNumberFromFile(fileNumber);
		
		if(GameData.getIsUserPlayingForFirstTime(fileNumber)){
			intent = new Intent(this, HeroExplanationScreen.class);
			GameData.setIsUserPlayingFirstTime(this, fileNumber, false);
		} else {
			intent = new Intent(this, ControllerConfigurationActivity.class);
		}
		intent.putExtra("mode", getIntent().getStringExtra("mode"));
		
		switch(planets){ //DO NOT add breaks below.
		case 0:
			((Button) findViewById(R.id.level_selection_universe_1_planet_2)).setVisibility(View.INVISIBLE);
		case 1:
			((Button) findViewById(R.id.level_selection_universe_1_planet_3)).setVisibility(View.INVISIBLE);
		case 2:
			((Button) findViewById(R.id.level_selection_universe_1_planet_4)).setVisibility(View.INVISIBLE);
		case 3:
			((Button) findViewById(R.id.level_selection_universe_1_planet_5)).setVisibility(View.INVISIBLE);
		case 4:
			((Button) findViewById(R.id.level_selection_universe_1_planet_6)).setVisibility(View.INVISIBLE);
		case 5:
			((Button) findViewById(R.id.level_selection_universe_1_planet_7)).setVisibility(View.INVISIBLE);
		}
		
	}
	
	public void pickPlanet(View v){
		int id = v.getId();
		if(id == R.id.level_selection_universe_1_planet_1){
			intent.putExtra("planetNumber", 0);
		} else if(id == R.id.level_selection_universe_1_planet_2){
			intent.putExtra("planetNumber", 1);
		} else if(id == R.id.level_selection_universe_1_planet_3){
			intent.putExtra("planetNumber", 2);
		} else if(id == R.id.level_selection_universe_1_planet_4){
			intent.putExtra("planetNumber", 3);
		} else if(id == R.id.level_selection_universe_1_planet_5){
			intent.putExtra("planetNumber", 4);
		} else if(id == R.id.level_selection_universe_1_planet_6){
			intent.putExtra("planetNumber", 5);
		} else if(id == R.id.level_selection_universe_1_planet_7){
			intent.putExtra("planetNumber", 6);
		}
		intent.putExtra("fileNumber", fileNumber);
		startActivity(intent);
	}
}
