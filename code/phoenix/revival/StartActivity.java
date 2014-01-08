import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class StartActivity extends Activity {
	String[] promoKeys = {};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		MusicAndSound.initializeMusic(this);
		initGame();
		initView();
	}
	
	private void initGame(){
		PlayersManager.initialize();
		GameData.loadData(this);
	}
	
	private void initView(){
		Button buttonCoop = (Button) findViewById(R.id.start_activity_coop_mode);
		Button buttonTraining = (Button) this.findViewById(R.id.start_activity_training_mode);
		Button buttonPromoCode = (Button) findViewById(R.id.start_activity_promo_code);
		
		buttonCoop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				StartActivity.this.startActivity(new Intent(StartActivity.this, GameFileSelectionScreen.class));
			}
		});
		
		buttonTraining.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(StartActivity.this, HeroSelectionScreen.class);
				intent.putExtra("mode", "training");
				StartActivity.this.startActivity(intent);
			}
		});
		buttonPromoCode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertPromptPromoCode = new AlertDialog.Builder(StartActivity.this);
				final EditText editTextInput = new EditText(StartActivity.this);
				alertPromptPromoCode.setView(editTextInput);
				alertPromptPromoCode.setTitle("Please Enter Promo Code");
				alertPromptPromoCode.setNegativeButton("Cancel", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				alertPromptPromoCode.setPositiveButton("Okay", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(validatesPromoCode( editTextInput.getText().toString().trim()) ){
							showResonseSuccessful(true);
						} else {
							showResonseSuccessful(false);
						}
					}
				});
				alertPromptPromoCode.show();
			}
		});
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	
	private boolean validatesPromoCode(String input){
		unlockSteelash();
		String KEY1 = "delete...";
		String KEY2 = "not shown on sample..."; 
		if(KEY1.equals(input) || KEY2.equals(input)){
			unlockSteelash();
			return true;
		}
		return false;
	}
	private void unlockSteelash(){
		GameData.setSteelashUnlocked(this, true);
	}
	private void showResonseSuccessful(boolean isSuccessful){
		AlertDialog.Builder alertPromoCodeSuccess = new AlertDialog.Builder(this);
		if(isSuccessful){
			alertPromoCodeSuccess.setTitle("Congrats!").setMessage("The selectable hero Steelash is now available to you for free. Enjoy! Please LIKE our game on the OUYA store!").setPositiveButton("Okay", null).show();
		} else {
			alertPromoCodeSuccess.setTitle("Incorrect Promo Code!").setMessage("Please double check your promo key, and email support@syraca.com if it's not working.").setPositiveButton("Okay", null).show();
		}
	}
}
