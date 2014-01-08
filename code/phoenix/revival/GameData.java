import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class GameData {
	public static boolean isSoundEnabled = true;
	public static float soundVolume = 0.5f;

	public static final String VERSION = "VERSION";
	public static final String GAME_FILES = "GAME_FILES";
	public static final String IS_STEELASH_UNLOCKED = "IS_STEELASH_UNLOCKED";
	
	public static final int MAX_NUM_FILES = 10;
	
	private static class GameFile{
		public static final String GAME_FILE_NAME = "GAME_FILE_NAME";
		public static final String HERO_NAMES  = "HERO_NAMES";
		public static final String HERO_LEVELS = "HERO_LEVELS";
		public static final String HERO_ITEMS = "HERO_ITEMS";
		public static final String MONEY = "MONEY";
		public static final String UNIVERSE = "UNIVERSE";
		public static final String PLANET = "PLANET";
		public static final String HERO_EXPERIENCES = "HERO_EXPERIENCES";
		public static final String IS_FIRST_TIME_PLAYING = "IS_FIRST_TIME_PLAYING";
		private JSONObject file;
		
		public GameFile(String fileName, List<String> names){
			JSONArray heroNames;
			JSONArray heroLevels;
			JSONArray heroExperiences;
			JSONArray heroItems;
			int money;
			int universe;
			int planet;
			boolean isFirstTimePlaying;
			heroNames = new JSONArray();
			heroLevels = new JSONArray();
			heroExperiences = new JSONArray();
			heroItems = new JSONArray();
			for(String s : names){
				heroNames.put(s);
				heroLevels.put(1); //default is 1
				heroExperiences.put(0);
			}
			//default is 0 for the below
			money = 0;
			universe = 0;
			planet = 0;
			isFirstTimePlaying = true;
			file = new JSONObject();
			try {
				file.put(GAME_FILE_NAME, fileName);
				file.put(HERO_NAMES, heroNames);
				file.put(HERO_LEVELS, heroLevels);
				file.put(HERO_EXPERIENCES, heroExperiences);
				file.put(HERO_ITEMS, heroItems);
				file.put(MONEY, money);
				file.put(UNIVERSE, universe);
				file.put(PLANET, planet);
				file.put(IS_FIRST_TIME_PLAYING, isFirstTimePlaying);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		public JSONObject toJSON(){
			return file;
		}
	}
	
	//======================================= data manipulation:
	private static final String GAME_DATA_FILE = "GAME_DATA_FILE";
	private static JSONObject gameData;
	
	private static void initNewMemoryFile(Context ctx){
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(VERSION, 2); 
			jObj.put(GAME_FILES, new JSONArray());
			jObj.put(IS_STEELASH_UNLOCKED, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		gameData = jObj;
		saveData(ctx, gameData.toString());
	}
	
	public static void loadData(Context ctx){
		String gameDataString = new String();
		try {
			FileInputStream fis = ctx.openFileInput(GAME_DATA_FILE);
			Scanner s = new Scanner(fis);
			while(s.hasNextLine()){
				gameDataString += s.nextLine();
			}
			
			//updating from verison 1 to version 2
			gameData = new JSONObject(gameDataString);
			if(gameData.getInt(VERSION) == 1){
				gameData.put(VERSION, 2);
				gameData.put(IS_STEELASH_UNLOCKED, false);
				saveData(ctx, gameData.toString());
				JSONArray jArr = gameData.getJSONArray(GAME_FILES);
				JSONObject jObj;
				for(int i=0; i<jArr.length(); i++){
					jObj = jArr.getJSONObject(i);
					jObj.put(GameFile.IS_FIRST_TIME_PLAYING, false);
					jObj.put(GameFile.HERO_ITEMS, new JSONArray());
					jArr.put(i, jObj);
				}
				gameData.put(GAME_FILES, jArr);
				saveData(ctx, gameData.toString());
			}
		} catch (FileNotFoundException e) {
			//first time creating file
			GameData.initNewMemoryFile(ctx);
			return;
		} catch (JSONException je){
			je.printStackTrace();
		}
	}
	
	private static void saveData(Context ctx, String data){
		FileOutputStream fos;
		try {
			fos = ctx.openFileOutput(GAME_DATA_FILE, Context.MODE_PRIVATE);
			fos.write(data.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void resetData(Context ctx) {
		// TODO
	}
	
	public static boolean createNewFile(Context ctx, String fileName, List<String> heroNames){
		JSONArray jArr;
		try {
			jArr = gameData.getJSONArray(GAME_FILES);
			if(jArr.length() < MAX_NUM_FILES){
				jArr.put(new GameFile(fileName, heroNames).toJSON());
				gameData.put(GAME_FILES, jArr);
				saveData(ctx, gameData.toString());
			} else {
				// complain to user that max number has exceeded
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static void deleteFile(Context ctx, int position){
		JSONArray files;
		ArrayList<JSONObject> temp = new ArrayList<JSONObject>();
		try{
			files = gameData.getJSONArray(GAME_FILES);
			for(int i=0; i<files.length(); i++){
				if(position != i){
					temp.add(files.getJSONObject(i));
				}
			}
			files = new JSONArray(temp);
			gameData.put(GAME_FILES, files);
			saveData(ctx, gameData.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static String[] getGameFileNames() {
		ArrayList<String> fileNames = new ArrayList<String>();
		try {
			JSONArray jArr = gameData.getJSONArray(GAME_FILES); 
			for(int i=0; i<jArr.length(); i++){
				JSONObject jObj = jArr.getJSONObject(i);
				fileNames.add(jObj.getString(GameFile.GAME_FILE_NAME));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String[] sArr = new String[fileNames.size()];
		return fileNames.toArray(sArr);
	}
	
	//============================= public accessors =================================
	
	public static List<String> getHerosFromFile(int fileNumber) {
		ArrayList<String> result = new ArrayList<String>();
		JSONObject file = getGameFile(fileNumber);
		try {
			JSONArray heroNames = file.getJSONArray(GameFile.HERO_NAMES);
			for(int i=0; i<heroNames.length(); i++){
				result.add(heroNames.getString(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<Integer> getHeroLevelsFromFile(int fileNumber) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		JSONObject file = getGameFile(fileNumber);
		try {
			JSONArray heroLevels = file.getJSONArray(GameFile.HERO_LEVELS);
			for(int i=0; i<heroLevels.length(); i++){
				result.add(heroLevels.getInt(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void setHeroLevelsInFile(Context ctx, int fileNumber, int level) {
		JSONObject file = getGameFile(fileNumber);
		try {
			JSONArray files = gameData.getJSONArray(GAME_FILES);
			JSONArray heroLevels = file.getJSONArray(GameFile.HERO_LEVELS);
			for(int i=0; i<heroLevels.length(); i++){
				heroLevels.put(i,level);
			}
			files.put(fileNumber, file);
			gameData.put(GAME_FILES, files);
			saveData(ctx, gameData.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static List<Integer> getHeroExperiencesFromFile(int fileNumber) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		JSONObject file = getGameFile(fileNumber);
		try {
			JSONArray heroLevels = file.getJSONArray(GameFile.HERO_EXPERIENCES);
			for(int i=0; i<heroLevels.length(); i++){
				result.add(heroLevels.getInt(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	public static void setHeroExperiencesInFile(Context ctx, int fileNumber, int exp) {
		JSONObject file = getGameFile(fileNumber);
		try {
			JSONArray files = gameData.getJSONArray(GAME_FILES);
			JSONArray heroExperiences = file.getJSONArray(GameFile.HERO_EXPERIENCES);
			for(int i=0; i<heroExperiences.length(); i++){
				heroExperiences.put(i,exp);
			}
			files.put(fileNumber, file);
			gameData.put(GAME_FILES, files);
			saveData(ctx, gameData.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static List<String> getHeroItemsFromFile(int fileNumber) {
		ArrayList<String> result = new ArrayList<String>();
		JSONObject file = getGameFile(fileNumber);
		try {
			JSONArray heroItems = file.getJSONArray(GameFile.HERO_ITEMS);
			for(int i=0; i<heroItems.length(); i++){
				result.add(heroItems.getString(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	public static void addHeroItemInFile(Context ctx, int fileNumber, String item) {
		JSONObject file = getGameFile(fileNumber);
		try {
			JSONArray files = gameData.getJSONArray(GAME_FILES);
			JSONArray heroItems = file.getJSONArray(GameFile.HERO_ITEMS);
			heroItems.put(item);
			files.put(fileNumber, file);
			gameData.put(GAME_FILES, files);
			saveData(ctx, gameData.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void setHeroItemsInFile(Context ctx, int fileNumber, List<String> heroItems) {
		JSONObject file = getGameFile(fileNumber);
		try {
			JSONArray files = gameData.getJSONArray(GAME_FILES);
			JSONArray items = new JSONArray();
			for(String item : heroItems){
				items.put(item);
			}
			file.put(GameFile.HERO_ITEMS, items);
			files.put(fileNumber, file);
			gameData.put(GAME_FILES, files);
			saveData(ctx, gameData.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static int getPlanetNumberFromFile(int fileNumber) {
		int result = -1;
		JSONObject file = getGameFile(fileNumber);
		try {
			result = file.getInt(GameFile.PLANET);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void unlockNextPlanet(Context ctx, int fileNumber, int nextPlanet) {
		JSONObject file;
		JSONArray files;
		try{
			files = gameData.getJSONArray(GAME_FILES);
			file = files.getJSONObject(fileNumber);
			int planetOn = file.getInt(GameFile.PLANET);
			if(planetOn > nextPlanet){
				return;
			}
			if(nextPlanet < 7){
				file.put(GameFile.PLANET, nextPlanet);
				files.put(fileNumber, file);
				gameData.put(GAME_FILES, files);
				saveData(ctx, gameData.toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static int getNumberOfCoinsInFile(int fileNumber) {
		JSONObject jObj = GameData.getGameFile(fileNumber);
		int monies = 0;
		try {
			monies = jObj.getInt(GameFile.MONEY);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return monies;
	}
	
	public static void setNumberOfCoinsInFile(Context ctx, int fileNumber, int coins){
		JSONObject file;
		JSONArray files;
		try{
			files = gameData.getJSONArray(GAME_FILES);
			file = files.getJSONObject(fileNumber);
			file.put(GameFile.MONEY, coins);
			files.put(fileNumber, file);
			gameData.put(GAME_FILES, files);
			saveData(ctx, gameData.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean getIsUserPlayingForFirstTime(int fileNumber) {
		JSONObject jObj = GameData.getGameFile(fileNumber);
		boolean isFirstTime = true;
		try {
			isFirstTime = jObj.getBoolean(GameFile.IS_FIRST_TIME_PLAYING);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return isFirstTime;
	}
	
	public static void setIsUserPlayingFirstTime(Context ctx, int fileNumber, boolean value){
		JSONObject file;
		JSONArray files;
		try{
			files = gameData.getJSONArray(GAME_FILES);
			file = files.getJSONObject(fileNumber);
			file.put(GameFile.IS_FIRST_TIME_PLAYING, value);
			files.put(fileNumber, file);
			gameData.put(GAME_FILES, files);
			saveData(ctx, gameData.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static String getGameFileShortDescription(int fileNumber) {
		String result = "";
		JSONObject file = getGameFile(fileNumber);
		try {
			JSONArray heroNames = file.getJSONArray(GameFile.HERO_NAMES);
			JSONArray heroLevels = file.getJSONArray(GameFile.HERO_LEVELS);
			for(int i=0; i<heroLevels.length(); i++){
				result += "" + heroNames.get(i) + ": lvl."+ heroLevels.get(i) + "\n";
			}
			result += "coins: " + file.get(GameFile.MONEY) + "\n";
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static JSONObject getGameFile(int fileNumber){
		JSONObject file = null;
		try {
			JSONArray jArr = gameData.getJSONArray(GAME_FILES);
			file = jArr.getJSONObject(fileNumber);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	//============================== game specific settings/data ===============================
	public static boolean isSteelashUnlocked(){
		boolean result = false;
		try {
			result = gameData.getBoolean(IS_STEELASH_UNLOCKED);
		} catch (JSONException e) {
			try {
				gameData.put(IS_STEELASH_UNLOCKED, false);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return result;
	}

	public static void setSteelashUnlocked(Context ctx, boolean value) {
		try {
			gameData.put(IS_STEELASH_UNLOCKED, value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		saveData(ctx, gameData.toString());
	}
}

/*
 * file version log:
 * 
 * v2 -> added in steelash and items 
 * 
 */
