import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HeroExplanationScreen extends Activity {
	private Intent intent;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hero_explanation_screen);
		
		intent = new Intent(this, ControllerConfigurationActivity.class);
		String mode = getIntent().getStringExtra("mode");
		intent.putExtra("mode", mode);
		if(mode.equals("coop")){
			intent.putExtra("fileNumber", getIntent().getIntExtra("fileNumber", -1));
			intent.putExtra("planetNumber", getIntent().getIntExtra("planetNumber", -1));
		} else if(mode.equals("training")){
			intent.putExtra("player1", getIntent().getStringExtra("player1"));
		}
		
		Button b = (Button) findViewById(R.id.hero_explanation_goto_controller_configuartion_screen);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				HeroExplanationScreen.this.startActivity(intent);
				HeroExplanationScreen.this.finish();
			}
		});
	}
}
