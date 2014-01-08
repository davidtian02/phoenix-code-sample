import java.util.ArrayList;
import java.util.List;

public class PlayersManager { 
	public static final int PLAYER_1_ID = 0;
	public static final int PLAYER_2_ID = 1;
	public static final int PLAYER_3_ID = 2;
	public static final int PLAYER_4_ID = 3;
	
	private static List<Player> players;
	
	public static void initialize(){
		players = new ArrayList<Player>();
	}
	
	public static void addPlayer(int deviceID){
		players.add(new Player(deviceID));
	}

	public static List<Player> getPlayers() {
		return players;
	}

	public static int getNumberOfPlayers() {
		return players.size();
	}

	public static int queryPlayerNumberByDeviceId(int deviceId) {
		for(int i=0; i<players.size(); i++){
			if(players.get(i).getDeviceId()==deviceId){
				return i;
			}
		}
		return -1;
	}
}
