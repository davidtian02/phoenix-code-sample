import java.util.ArrayList;
import java.util.List;

import tv.ouya.console.api.Product;
import tv.ouya.console.api.Receipt;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HeroSelectionScreen extends Activity implements OuyaProductPurchaseListener{
	public final String TANK_DESCRIPTION = "Base HP: 430 \nBase MP: 110 \nBase Damage: 22\nMovement Speed: 10\nAttack Speed: 4.1\n" +
			"Skills: \n Mega Shield (O) - 3 seconds protective coating in a small radius \n Dispatch Drones (U) - throws down a wall of defensive drones that also attack \n Splash Waves (Y) - AOE attack that lasts 5 seconds \n Galactic Force (A) - Ultimate attack that gravitates enemies inwards and does massive damage";
	public final String HEALER_DESCRIPTION = "Base HP: 320\nBase MP: 260\nBase Damage: 18\nMovement Speed: 12\nAttack Speed: 4.1\n" + 
			"Skills: \n Healing Bubble (O) - Heals self and all allies that are near \n Holy Bolt (U) - Shoots a bolt in a line that damages enemies and heals any allies \n InstaGuard (Y) - Instantly casts a shield to absorb damage \n Time Bubble (A) Ultimate attack that boosts attack speed on self, slows and damages enemies.";
	public final String CARRY_DESCRIPTION = "Base HP: 240\nBase MP: 190\nBase Damage: 27\nMovement Speed: 20\nAttack Speed: 5\n" + 
			"Skills: \n Spark Attack (O) - Shoots a bolt of electricity that deals damage \n Cluster Missles (U) - Shoots missles in an arc that deal damage \n Guardian Sparks (Y) - Shoots sparks in a small radius that deals damage and nullifies enemy fire \n Hellfire Missles (A) - Ultimate attack that fires many missles outwards in a ring";
	public final String CASTER_DESCRIPTION = "Base HP: 290\nBase MP: 370\nBase Damage: 15\nMovement Speed: 13\nAttack Speed: 4.5\n" + 
			"Skills: \n EMP (O) - Destroys all enemy bullets in AOE \n Gravity Beam (U) - Temporarily holds 15 enemies on screen and damages them over time \n Frost Ring (Y) - Instantly freezes all enemies in an AOE \n Grand Fireballs (A) Ultimate attack that shoots fireballs outwards that deal massive damage.";
	private String STEELASH_DESCRIPTION = "Base HP: 390\nBase MP: 270\nBase Damage: 35\nMovement Speed: 24\nAttack Speed: 6\n" + 
			"Skills: \n Piercing Rounds (O) - Shoots beams in arc that damages and nullfies enemy fire\n Disable (U) - Damages and disables all enemies/enemy fire on screen \n Warp Drive (Y) - 2x boost on movement speed, atk dmg and attack speed. +90% evasion (5 seconds)\n Laser Barrage (A) - Shoots beams that deal 3x atk dmg each (12 seconds). \n Bonus Passive: 20% chance to do 2x Critical Hit damage\n\nloading...";
	
	boolean hasPlayer1Chosen;
	boolean hasPlayer2Chosen;
	boolean hasPlayer3Chosen;
	boolean hasPlayer4Chosen;
	boolean chosenStartGame;
	List<String> heroNames;
	TextView tvHeroSelectionPrompt;
	TextView tvHeroDescription;
	ImageView ivHeroSpell1;
	ImageView ivHeroSpell2;
	ImageView ivHeroSpell3;
	ImageView ivHeroSpell4;
	Intent intent;
	Button bSteelash;
	String productIdentifierSteelash;
	
	private OuyaIAP ouyaIAP;
	private enum IsSteelashPurchased {YES, NO, MAYBE};
	IsSteelashPurchased isSteelashPurchased;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//IAP stuff
		ouyaIAP = new OuyaIAP(this);
		if(!GameData.isSteelashUnlocked()){
			ouyaIAP.requestProductList();
			ouyaIAP.requestReceipts();
			isSteelashPurchased = IsSteelashPurchased.MAYBE;
		} else {
			isSteelashPurchased = IsSteelashPurchased.YES;
		}
		
		setContentView(R.layout.activity_hero_selection_screen);
		heroNames = new ArrayList<String>();
		
		tvHeroSelectionPrompt = (TextView) findViewById(R.id.hero_selection_prompt);
		tvHeroSelectionPrompt.setText("Select Player 1's Hero");
		tvHeroDescription = (TextView) findViewById(R.id.hero_selection_hero_description);
		
		ivHeroSpell1 = (ImageView) findViewById(R.id.hero_selection_hero_spell_image_1);
		ivHeroSpell2 = (ImageView) findViewById(R.id.hero_selection_hero_spell_image_2);
		ivHeroSpell3 = (ImageView) findViewById(R.id.hero_selection_hero_spell_image_3);
		ivHeroSpell4 = (ImageView) findViewById(R.id.hero_selection_hero_spell_image_4);
		
		int numPlayers = -1;
		
		String mode = getIntent().getStringExtra("mode");
		if(mode.equals("coop")){
			//this woulda been from a new file
			intent = new Intent(HeroSelectionScreen.this, LevelSelectionScreen.class);
			intent.putExtra("fileNumber", GameData.getGameFileNames().length);
			intent.putExtra("mode", "coop");
			numPlayers = getIntent().getIntExtra("numberOfPlayers", -1);
		} else if(mode.equals("training")){
			intent = new Intent(HeroSelectionScreen.this, HeroExplanationScreen.class);
			intent.putExtra("mode", "training");
			numPlayers = 1;
		}
		
		switch(numPlayers){
		case 1:
			hasPlayer2Chosen = true;
			hasPlayer3Chosen = true;
			hasPlayer4Chosen = true;
			break;
		case 2:
			hasPlayer3Chosen = true;
			hasPlayer4Chosen = true;
			break;
		case 3:
			hasPlayer4Chosen = true;
		}
		
		Button bTank = (Button) findViewById(R.id.hero_selection_tank_button);
		bTank.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					tvHeroDescription.setText(TANK_DESCRIPTION);
					refreshSpellViews(R.drawable.hero_tank_mega_shield, R.drawable.hero_tank_dispatch_drones, R.drawable.hero_tank_splash_waves, R.drawable.hero_tank_galactic_force);
				}
			}
		});
		
		Button bHealer = (Button) findViewById(R.id.hero_selection_healer_button);
		bHealer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					tvHeroDescription.setText(HEALER_DESCRIPTION);
					refreshSpellViews(R.drawable.hero_healer_healing_bubble, R.drawable.hero_healer_holy_bolt, R.drawable.hero_healer_insta_guard, R.drawable.hero_healer_time_bubble);
				}
			}
		});
		
		Button bCarry = (Button) findViewById(R.id.hero_selection_carry_button);
		bCarry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					tvHeroDescription.setText(CARRY_DESCRIPTION);
					refreshSpellViews(R.drawable.hero_carry_spark_attack, R.drawable.hero_carry_cluster_missles, R.drawable.hero_carry_guardian_sparks, R.drawable.hero_carry_hellfire_missles);
				}
			}
		});
		
		Button bCaster = (Button) findViewById(R.id.hero_selection_caster_button);
		bCaster.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					tvHeroDescription.setText(CASTER_DESCRIPTION);
					refreshSpellViews(R.drawable.hero_caster_emp, R.drawable.hero_caster_gravity_beam, R.drawable.hero_caster_frost_ring, R.drawable.hero_caster_grand_fireballs);
				}
			}
		});
		
		bSteelash = (Button) findViewById(R.id.hero_selection_steelash_button);
		bSteelash.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					tvHeroDescription.setText(STEELASH_DESCRIPTION);
					refreshSpellViews(R.drawable.hero_steelash_piercing_rounds, R.drawable.hero_steelash_disable, R.drawable.hero_steelash_warp_drive, R.drawable.hero_steelash_laser_barrage);
				}
			}
		});
		
		if(isSteelashPurchased == IsSteelashPurchased.MAYBE){
			bSteelash.setEnabled(false);
			bSteelash.setText("Loading...");
		} else if (isSteelashPurchased == IsSteelashPurchased.NO){
			bSteelash.setEnabled(false);
			bSteelash.setText("Locked");
		} else {
			STEELASH_DESCRIPTION = STEELASH_DESCRIPTION.substring(0, STEELASH_DESCRIPTION.length() - 10) + "Purchased! Thank you! Enjoy :)";
			bSteelash.setText("Steelash");
			bSteelash.setEnabled(true);
		}
	}
	
	public void pickHero(View v){
		int id = v.getId();
		if(!hasPlayer1Chosen){
			hasPlayer1Chosen = chooseClass("player1", id);
			tvHeroSelectionPrompt.setText("Select Player 2's Hero");
		} else if(!hasPlayer2Chosen){
			hasPlayer2Chosen = chooseClass("player2", id);
			tvHeroSelectionPrompt.setText("Select Player 3's Hero");
		} else if(!hasPlayer3Chosen){
			hasPlayer3Chosen = chooseClass("player3", id);
			tvHeroSelectionPrompt.setText("Select Player 3's Hero");
		} else if(!hasPlayer4Chosen){
			hasPlayer4Chosen = chooseClass("player4", id);
			tvHeroSelectionPrompt.setText("All heros selected");
		} else {
			// case for extra clicks
		}

		if(hasPlayer1Chosen && hasPlayer2Chosen && hasPlayer3Chosen && hasPlayer4Chosen){
			if(!chosenStartGame){ //note: this is in case someone keeps hitting it too quickly
				chosenStartGame = true;
				startGame();
			}
		}
	}
	
	private boolean chooseClass(String player, int id){
		String heroName = null;
		if(id == R.id.hero_selection_tank_button){
			heroName = "tank";
		} else if(id == R.id.hero_selection_healer_button){
			heroName = "healer";
		} else if(id == R.id.hero_selection_carry_button){
			heroName = "carry";
		} else if(id == R.id.hero_selection_caster_button){
			heroName = "caster";
		} else if(id == R.id.hero_selection_steelash_button){
			if(isSteelashPurchased == IsSteelashPurchased.YES){
				heroName = "steelash";
			} else if(isSteelashPurchased == IsSteelashPurchased.NO) {
				ouyaIAP.requestPurchase(productIdentifierSteelash);
			}
		}
		
		if(heroName != null){
			heroNames.add(heroName);
			intent.putExtra(player, heroName);
		}
		
		return heroName != null;
	}
	
	private void refreshSpellViews(int r1, int r2, int r3, int r4){
		ivHeroSpell1.setImageResource(r1);
		ivHeroSpell1.invalidate();
		ivHeroSpell1.refreshDrawableState();
		ivHeroSpell2.setImageResource(r2);
		ivHeroSpell2.invalidate();
		ivHeroSpell2.refreshDrawableState();
		ivHeroSpell3.setImageResource(r3);
		ivHeroSpell3.invalidate();
		ivHeroSpell3.refreshDrawableState();
		ivHeroSpell4.setImageResource(r4);
		ivHeroSpell4.invalidate();
		ivHeroSpell4.refreshDrawableState();
	}
	
	private void startGame(){
		if(getIntent().getStringExtra("mode").equals("coop")){
			GameData.createNewFile(this, Utils.TEXT_LEFT_PADDING + getIntent().getStringExtra("fileName"), heroNames);
		}
		
		startActivity(intent);
		finish();
	}

	@Override
	protected void onDestroy(){
		ouyaIAP.teardown();
		super.onDestroy();
	}
	
	//============================== OuyaResponseListener ============================

	@Override
	public void onProductRequestSucceeded(List<Product> products) {
		System.out.println("on request products succeeded");
		for(Product p : products){
			System.out.println(p.getName());
		}

		productIdentifierSteelash = products.get(0).getIdentifier();
		bSteelash.setEnabled(true);
	}

	@Override
	public void onProductRequestFailed(int errorCode, String errorMessage, Bundle bundle) {
		System.out.println("on product request failed");
	}

	@Override
	public void onPurchaseRequestSucceeded(Product p) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Purchase Successful!")
		.setMessage("Thank you so much for the support! Enjoy!")
		.setCancelable(true)
		.setPositiveButton("Awesome", null);
		
		if(p.getIdentifier().equals("hero-steelash")){
			STEELASH_DESCRIPTION = STEELASH_DESCRIPTION.substring(0, STEELASH_DESCRIPTION.length() - 5) + "Purchased! Thank you! Enjoy :)";
			isSteelashPurchased = IsSteelashPurchased.YES;
			GameData.setSteelashUnlocked(HeroSelectionScreen.this, true);
			bSteelash.setText("Steelash");
			bSteelash.setEnabled(true);
			tvHeroDescription.setText(STEELASH_DESCRIPTION);
		}
		
		System.out.println("on product purchase request succeeded!");
	}

	@Override
	public void onPurchaseRequestFailed(int errorCode, String errorMessage, Bundle info) {
		System.out.println("on product purchase request with message: " + errorMessage);
	}

	@Override
	public void onPurchaseRequestFailed(Exception e) {
		System.out.println("on product purchase request failed with exception " + e);
	}

	@Override
	public void onReceiptsRequestSucceeded(List<Receipt> receipts) {
		System.out.println("on receipt requested success");
		isSteelashPurchased = IsSteelashPurchased.NO;
		for(Receipt r : receipts){
			if(r.getIdentifier().equals("hero-steelash")){
				isSteelashPurchased = IsSteelashPurchased.YES;
				bSteelash.setEnabled(true);
			}
		}
		
		if(isSteelashPurchased == IsSteelashPurchased.NO){
			System.out.println("receipt: steelash not yet purchased");
			bSteelash.setText("Locked");
			STEELASH_DESCRIPTION = STEELASH_DESCRIPTION.substring(0, STEELASH_DESCRIPTION.length() - 10) + "$1.99";
		} else if(isSteelashPurchased == IsSteelashPurchased.YES){
			System.out.println("receipt: steelash already purchased");
			GameData.setSteelashUnlocked(HeroSelectionScreen.this, true);
			bSteelash.setText("Steelash");
			STEELASH_DESCRIPTION = STEELASH_DESCRIPTION.substring(0, STEELASH_DESCRIPTION.length() - 10) + "Purchased! Thank you! Enjoy :)";
		}
	}

	@Override
	public void onReceiptsRequestFailed(int errorCode, String errorMessage,	Bundle info) {
		System.out.println("on receipts request failed\n" + errorMessage);
	}
	
}
