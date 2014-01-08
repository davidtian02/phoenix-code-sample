import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NumberPlayersSelectionScreen extends Activity {
	
	Button one, two, three, four;
	Intent intent;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_players_selection_screen);
		
		one = (Button) findViewById(R.id.number_players_one);
		two = (Button) findViewById(R.id.number_players_two);
		
		intent = new Intent(this, HeroSelectionScreen.class);
		intent.putExtra("mode", getIntent().getStringExtra("mode"));
		intent.putExtra("fileName", getIntent().getStringExtra("fileName"));
	}
	
	public void numberChosen(View v){
		int id = v.getId();
		if(id == R.id.number_players_one){
			intent.putExtra("numberOfPlayers", 1);
			Utils.isSinglePlayer = true;
		} else if(id == R.id.number_players_two){
			intent.putExtra("numberOfPlayers", 2);
			Utils.isSinglePlayer = false;
		} 

		startActivity(intent);
		finish();
	}
}
