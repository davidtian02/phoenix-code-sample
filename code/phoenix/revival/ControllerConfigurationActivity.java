import tv.ouya.console.api.OuyaController;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

public class ControllerConfigurationActivity extends Activity {
	boolean isSinglePlayer;
	boolean isAllPlayersConfigured;
	int deviceIds[];
	boolean devicesConfigured[];
	TextView tv1;
	TextView tv2;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		OuyaController.init(this);
		
		if(getIntent().getStringExtra("mode").equals("coop")){
			Utils.isSinglePlayer = GameData.getHerosFromFile(getIntent().getIntExtra("fileNumber", -1)).size() == 1;
		} else if(getIntent().getStringExtra("mode").equals("training")){
			Utils.isSinglePlayer = true;
		}
		
		
		isSinglePlayer = Utils.isSinglePlayer; 
		// note: only 2 players for now.
		if(!isSinglePlayer){
			deviceIds = new int[Utils.MAX_NUM_PLAYERS];
			devicesConfigured = new boolean[Utils.MAX_NUM_PLAYERS];
			for(int i=0; i<Utils.MAX_NUM_PLAYERS; i++) {
				devicesConfigured[i] = false;
			}
		}
		
		this.setContentView(R.layout.activity_controller_configuration);
		
		if(!Utils.isSinglePlayer){
			TextView tv = (TextView) findViewById(R.id.controller_configuration_tv_prompt);
			tv.setText(tv.getText() + "Player 2 please press U");
		}
		
		tv1 = (TextView) this.findViewById(R.id.controller_configuration_tv1);
		tv2 = (TextView) this.findViewById(R.id.controller_configuration_tv2);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		
		if(keyCode == OuyaController.BUTTON_L1){ // to cancel
			this.finish();
			return true;
		}
		
		int singlePlayerDeviceId = -1;
		if(!isAllPlayersConfigured){
			if(isSinglePlayer){
				if(keyCode == OuyaController.BUTTON_O){
					singlePlayerDeviceId = event.getDeviceId();
					isAllPlayersConfigured = true;
				}
			} else { // multiple controllers
				switch(keyCode){
				case Utils.PLAYER_1_CONFIGURATION_KEY:
					if(devicesConfigured[0]){
						tv1.setText("Player 1 configured! deviceID: " + deviceIds[0]);
						return true;
					}
					if(devicesConfigured[1] && deviceIds[1] == event.getDeviceId()){ // TODO this would have to change if more than 2 controllers 
						tv2.setText("Player 2 already configured, Player 1 please hit O button");
						return true;
					}
					deviceIds[0] = event.getDeviceId();
					devicesConfigured[0] = true;
					tv1.setText("Player 1 configured! deviceID: " + deviceIds[0]);
					break;
				case Utils.PLAYER_2_CONFIGURATION_KEY:
					if(devicesConfigured[1]){ // don't configure it again
						tv2.setText("Player 2 configured! deviceID: " + deviceIds[1]);
						return true;
					}
					if(devicesConfigured[0] && deviceIds[0] == event.getDeviceId()){ //TODO this would have to change if more than 2 controllers
						tv1.setText("Player 1 already configured, Player 2 please hit U button");
						return true;
					}
					deviceIds[1] = event.getDeviceId();
					devicesConfigured[1] = true;
					tv2.setText("Player 2 configured! deviceID: " + deviceIds[1]);
					break;
					//note: only 2 players here.
				}
				boolean tempInjector = devicesConfigured[0];
				for(int i=0; i<devicesConfigured.length; i++){
					tempInjector = tempInjector && devicesConfigured[i];
				}
				isAllPlayersConfigured = tempInjector;
			}
		}

		if(isAllPlayersConfigured){
			if(isSinglePlayer){
				PlayersManager.addPlayer(singlePlayerDeviceId);
			} else {
				for(int id : deviceIds){
					PlayersManager.addPlayer(id);
				}
			}

			Intent intent = null;
			if(getIntent().getStringExtra("mode").equals("coop")){
				int planetNumber = getIntent().getIntExtra("planetNumber", -1);
				if(planetNumber == 0){
					intent = new Intent(this, Level1.class);
				} else if(planetNumber == 1){
					intent = new Intent(this, Level2.class);
				} else if(planetNumber == 2){
					intent = new Intent(this, Level3.class);
				} else if(planetNumber == 3){
					intent = new Intent(this, Level4.class);
				} else if(planetNumber == 4){
					intent = new Intent(this, Level5.class);
				} else if(planetNumber == 5){
					intent = new Intent(this, Level6.class);
				} else if(planetNumber == 6){
					intent = new Intent(this, Level7.class);
				} else {
					System.out.println("uh oh - not in this universe...");
				}
				intent.putExtra("fileNumber", getIntent().getIntExtra("fileNumber", -1));
			} else if(getIntent().getStringExtra("mode").equals("training")){
				intent = new Intent(this, TrainingMode.class);
				intent.putExtra("player1", getIntent().getStringExtra("player1"));
			}

			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			finish();
		}
		return true;
	}

	@Override
	public boolean onGenericMotionEvent(MotionEvent event){
		return OuyaController.onGenericMotionEvent(event) || super.onGenericMotionEvent(event);
	}

	@Override
	protected void onPause(){
		MusicAndSound.pauseMusicUi();
		super.onPause();
	}
}
