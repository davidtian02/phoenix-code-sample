public class LevelResultsScreen extends Activity {
	AlertDialog.Builder insufficientFundsAlert;
	private int fileNumber;
	private String levelResults;
	private TextView tvResult;
	private TextView tvResultDescription;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_level_results_screen);
		fileNumber = getIntent().getIntExtra("fileNumber", -1);
		
		boolean beatLevel = getIntent().getBooleanExtra("resultWon", false);
		int coinsCollected = getIntent().getIntExtra("coinsCollected", 0);
		int enemiesKilled = getIntent().getIntExtra("enemiesKilled", 0);
		int levels = GameData.getHeroLevelsFromFile(fileNumber).get(0);
		
		final Intent intent;

		Button bContinue = (Button) findViewById(R.id.level_results_continue);
		tvResult = (TextView) findViewById(R.id.level_results);
		final TextView tvResult2 = (TextView) findViewById(R.id.level_results2);
		levelResults = "Results:\n\nCoins + Bonus: " + coinsCollected + 
				"\nEnemies Killed: " + enemiesKilled + 
				"\nHero Levels: " + levels;
		tvResult.setText(levelResults);
		updateItemsList();
		
		//items
		final Button bRepairKit = (Button) findViewById(R.id.level_results_repair_kit);
		final Button bEnergyCell = (Button) findViewById(R.id.level_results_energy_cell);
		final Button bPhoenixRevival = (Button) findViewById(R.id.level_results_phoenix_revival);
		
		if(Utils.didPlayerJustBeatEntireGame){
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Congrats!")
			.setMessage("Great job finishing the game. If you've enjoyed the game, PLEASE PLEASE PLEASE support us by liking our game! Thank you!!!")
			.setCancelable(true)
			.setPositiveButton("Okay", null)
			.show();
			Utils.didPlayerJustBeatEntireGame = false;
			tvResult2.setText("\nCongrats on finishing game! \nComments? Feedback? Want a free purchase? \nThen please email: support@syraca.com with the title \"I beat Phoenix\"");
			bRepairKit.setVisibility(View.INVISIBLE);
			bEnergyCell.setVisibility(View.INVISIBLE);
			bPhoenixRevival.setVisibility(View.INVISIBLE);
		} else { 
			insufficientFundsAlert = new AlertDialog.Builder(this);
			insufficientFundsAlert.setTitle("Insufficient Funds").setMessage("Not enough coins! collect more coins to purchase this item.").setPositiveButton("Okay", null);
			
			tvResultDescription = (TextView) findViewById(R.id.level_results_item_description);
			
			bRepairKit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(tryToBuyItem(Items.REPAIR_KIT, Items.REPAIR_KIT_COST)){
						bRepairKit.setVisibility(View.INVISIBLE);
						tvResultDescription.setText("");
					}
				}
			});
			bRepairKit.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(hasFocus){
						tvResultDescription.setText(Items.REPAIR_KIT + ": " + Items.REPAIR_KIT_COST + " coins. This will instantly recover 400 Hit points." );
					}
				}
			});
			if(GameData.getHeroItemsFromFile(fileNumber).contains(Items.REPAIR_KIT)){
				bRepairKit.setVisibility(View.INVISIBLE);
			}

			bEnergyCell.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(tryToBuyItem(Items.ENERGY_CELL, Items.ENERGY_CELL_COST)){
						bEnergyCell.setVisibility(View.INVISIBLE);
						tvResultDescription.setText("");
					}
				}
			});
			bEnergyCell.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(hasFocus){
						tvResultDescription.setText(Items.ENERGY_CELL + ": " + Items.ENERGY_CELL_COST + " coins. This will instantly recover 500 energy points (for special abilities)" );
					}
				}
			});
			if(GameData.getHeroItemsFromFile(fileNumber).contains(Items.ENERGY_CELL)){
				bEnergyCell.setVisibility(View.INVISIBLE);
			}
			
			bPhoenixRevival.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(tryToBuyItem(Items.PHOENIX_REVIVAL, Items.PHOENIX_REVIVAL_COST)){
						bPhoenixRevival.setVisibility(View.INVISIBLE);
						tvResultDescription.setText("");
					}
				}
			});
			bPhoenixRevival.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(hasFocus){
						tvResultDescription.setText(Items.PHOENIX_REVIVAL + ": " + Items.PHOENIX_REVIVAL_COST + " coins. This will instantly revive you from death");
					}
				}
			});
			if(GameData.getHeroItemsFromFile(fileNumber).contains(Items.PHOENIX_REVIVAL)){
				bPhoenixRevival.setVisibility(View.INVISIBLE);
			}
		}
		
		if(beatLevel){
			bContinue.setText("Continue");
			intent = new Intent(this, LevelSelectionScreen.class);
			GameData.unlockNextPlanet(this, fileNumber, getIntent().getIntExtra("planetNumber", -1) + 1);
		} else {
			bContinue.setText("Play Again");
			intent = new Intent(this, ControllerConfigurationActivity.class);
			intent.putExtra("mode", "coop");
			intent.putExtra("planetNumber", getIntent().getIntExtra("planetNumber", -1));
		}
		intent.putExtra("fileNumber", fileNumber);
		
		//give gold and exp to heros
		
		bContinue.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LevelResultsScreen.this.startActivity(intent);
				LevelResultsScreen.this.finish();
			}
		});
	}
	
	private boolean tryToBuyItem(String name, int cost){
		boolean purchaseSuccessful = false;
		int numCoins = GameData.getNumberOfCoinsInFile(fileNumber);
		if(numCoins >= cost){
			if(!GameData.getHeroItemsFromFile(fileNumber).contains(name)){
				GameData.addHeroItemInFile(LevelResultsScreen.this, fileNumber, name);
				GameData.setNumberOfCoinsInFile(LevelResultsScreen.this, fileNumber, numCoins - cost);
				purchaseSuccessful = true;
			}
		} else {
			insufficientFundsAlert.show();
		}
		if(purchaseSuccessful){
			updateItemsList();
		}
		return purchaseSuccessful;
	}
	
	private void updateItemsList(){
		StringBuilder appendix = new StringBuilder("\nTotal Coins: " + GameData.getNumberOfCoinsInFile(fileNumber) + "\n\nCurrent Items: \n");
		List<String> items = GameData.getHeroItemsFromFile(fileNumber);
		for(String item : items){
			appendix.append(item).append("\n");
		}
		tvResult.setText(levelResults + appendix);
	}
	
	@Override
	protected void onResume(){
		MusicAndSound.playMusicResults();
		super.onResume();
	}
	
	@Override
	protected void onPause(){
		MusicAndSound.pauseMusicResults();
		super.onPause();
	}
}
