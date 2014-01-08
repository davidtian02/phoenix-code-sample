import java.util.ArrayList;
import java.util.List;

@Deprecated
public class MinionGenerator {
	int difficulty;
	List<Minion> minions;
	
	public MinionGenerator(){
		difficulty = 1;
		minions = new ArrayList<Minion>();
	}
	
	public List<Minion> getMinions(){
		return minions;
	}
	
	public void generateMoreMinions(){
		
	}
}
