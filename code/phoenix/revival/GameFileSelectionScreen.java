import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class GameFileSelectionScreen extends Activity {
	int fileNumber = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		ListView lv = new ListView(this);
		final String[] fileNames = new String[GameData.MAX_NUM_FILES];
		final String[] gameFileNames = GameData.getGameFileNames();
		for(int i=0; i<GameData.MAX_NUM_FILES; i++){
			if(i<gameFileNames.length){
				fileNames[i] = gameFileNames[i];
			} else {
				fileNames[i] = Utils.TEXT_LEFT_PADDING + "-----";
			}
		}
		if(gameFileNames.length < GameData.MAX_NUM_FILES){
			fileNames[gameFileNames.length] = Utils.TEXT_LEFT_PADDING + "Create New File";
		}
		
		//user chose new file
		final AlertDialog.Builder alertNewFile = new AlertDialog.Builder(this);
		final EditText newFileEditTextView = new EditText(GameFileSelectionScreen.this);
		alertNewFile.setView(newFileEditTextView)
		.setTitle("New File")
		.setMessage("Please enter a file name (must only use spaces, letter, and numbers")
		.setPositiveButton("Okay", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String input = newFileEditTextView.getText().toString().trim();
				input = input.replaceAll("[^a-zA-Z\\d\\s]", "").trim();
				if(input.length() > 30){
					input = input.substring(0, 30);
				}
				
				Intent intent = new Intent(GameFileSelectionScreen.this, NumberPlayersSelectionScreen.class);
				intent.putExtra("fileName", input);
				intent.putExtra("mode", "coop");
				GameFileSelectionScreen.this.startActivity(intent);
				GameFileSelectionScreen.this.finish();
			}
		})
		.setNegativeButton("Cancel", null)
		.setCancelable(true);
		
		//loading file
		final AlertDialog.Builder alertLoadFile = new AlertDialog.Builder(this);
		alertLoadFile.setTitle("Load File")
		.setPositiveButton("Load", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(GameFileSelectionScreen.this, LevelSelectionScreen.class);
				intent.putExtra("fileNumber", fileNumber);
				intent.putExtra("mode", "coop");
				GameFileSelectionScreen.this.startActivity(intent);
				GameFileSelectionScreen.this.finish();
			}
		}).setNegativeButton("Delete File", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				GameData.deleteFile(GameFileSelectionScreen.this, fileNumber);
				//REFACTOR - maybe reload this activity on the spot?
				Intent intent = new Intent(GameFileSelectionScreen.this, GameFileSelectionScreen.class);
				GameFileSelectionScreen.this.startActivity(intent);
				GameFileSelectionScreen.this.finish();
			}
		}).setCancelable(true);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, fileNames);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(position < gameFileNames.length){
					fileNumber = position;
					alertLoadFile.setMessage(GameData.getGameFileShortDescription(position));
					alertLoadFile.show();
				} else if(position == gameFileNames.length && gameFileNames.length != GameData.MAX_NUM_FILES){
					alertNewFile.show();
				}
			}
		});
		setContentView(lv);
	}
}
