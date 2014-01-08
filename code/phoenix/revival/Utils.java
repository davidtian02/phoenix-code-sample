import java.security.PublicKey;
import java.util.Random;

import tv.ouya.console.api.OuyaController;
import android.app.Activity;
import android.util.DisplayMetrics;

public class Utils {
	private static Random rand;
	static {
		rand = new Random();
	}
	private static int screenWidth = -1;
	private static int screenHeight = -1;
	//TODO this is using 32 PTM, but is this the best simulation?
	public static final float WORLD_WIDTH = 1920;
	public static final float WORLD_HEIGHT = 1080f;
	
	public static final String DEVELOPER_ID = "2cc78bd9-c8e6-48a6-be98-11ad5bd4450a"; 
	
	public static boolean isSinglePlayer = false;
	public static boolean didPlayerJustBeatEntireGame = false;
	public static PublicKey mPublicKey;
	
	public static final int MAX_NUM_PLAYERS = 2; // TODO change this later
	public static final int PLAYER_1_CONFIGURATION_KEY = OuyaController.BUTTON_O;
	public static final int PLAYER_2_CONFIGURATION_KEY = OuyaController.BUTTON_U;
	public static final int PLAYER_3_CONFIGURATION_KEY = OuyaController.BUTTON_Y;
	public static final int PLAYER_4_CONFIGURATION_KEY = OuyaController.BUTTON_A;
	public static final float FPS = 60f;
	public static final String TEXT_LEFT_PADDING = "    ";
	
	private static void initScreenSizes(Activity act){
		DisplayMetrics metrics = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
	}
	
	public static int getScreenWidth(Activity act){
		if(screenWidth==-1){
			initScreenSizes(act);
		}
		return screenWidth;
	}
	
	public static int getScreenHeight(Activity act){
		if(screenWidth==-1){
			initScreenSizes(act);
		}
		return screenHeight;
	}

	public static int getRandomInt(int n) {
		return rand.nextInt(n);
	}
	
	public static float getRandomFloat(){
		return rand.nextFloat();
	}

	public static int getRandomSign(){
		return rand.nextInt(2) == 1 ? -1 : 1;
	}
	
	public static boolean isWithinBoundsCompletely(Circle object, Rectangle bounds) {
		boolean result = false;
		if(object.center.x + object.radius <= bounds.lowerLeft.x + bounds.width){ // left of right wall
			if(object.center.x - object.radius >= bounds.lowerLeft.x){ //right of left wall
				if(object.center.y - object.radius >= bounds.lowerLeft.y){ //above bottom wall
					if(object.center.y + object.radius <= bounds.lowerLeft.y + bounds.height){
						result = true;
					}
				}
			}
		}
		return result;
	}
	
	public static boolean isWithinBoundsCompletely(Circle circle1, Circle circle2) {
		Circle smaller, bigger;
		if(circle1.radius < circle2.radius){
			smaller = circle1; bigger = circle2;
		} else {
			smaller = circle2; bigger = circle1;
		}
		
		if(bigger.radius >= 2*smaller.radius){
			Circle tempCircle = new Circle(bigger.center.x, bigger.center.y, bigger.radius-2*smaller.radius);
			if(OverlapTester.overlapCircles(tempCircle, smaller)){
				return true;
			}
		} else {
			Vector2 slope = new Vector2((bigger.center.y - smaller.center.y) , (bigger.center.x - smaller.center.x)).nor();
			Vector2 furthestPoint = new Vector2(smaller.center.x + slope.x*smaller.radius, smaller.center.y + slope.y * smaller.radius);
			if(OverlapTester.pointInCircle(bigger, furthestPoint)){
				return true;
			}
		}
		return false;
	}

	public static boolean isOutOfBoundsCompletely(Rectangle bounds, Rectangle cameraBounds) {
		return !OverlapTester.overlapRectangles(bounds, cameraBounds);
	}

	public static boolean isOutOfBoundsCompletely(Circle bounds, Rectangle cameraBounds) {
		return !OverlapTester.overlapCircleRectangle(bounds, cameraBounds);
	}
 
	public static boolean isWithinBoundsCompletely(Rectangle smaller, Rectangle bigger) {
		boolean result = false;
		if(smaller.lowerLeft.x + smaller.width <= bigger.lowerLeft.x + bigger.width){ // left of right wall
			if(smaller.lowerLeft.x >= bigger.lowerLeft.x){ //right of left wall
				if(smaller.lowerLeft.y >= bigger.lowerLeft.y){ //above bottom wall
					if(smaller.lowerLeft.y + smaller.height <= bigger.lowerLeft.y + bigger.height){
						result = true;
					}
				}
			}
		}
		return result;
	}


}
