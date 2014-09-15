package mdpAlgorithm;

//import java.util.Stack;

public class ExplorationMgr implements Runnable {
	MapGrid map;
	Robot rob;
	
	public ExplorationMgr(MapGrid map, Robot rob) {
		this.rob = rob;
		this.map = map;
	}

	@Override
	public void run() {
		int count = 0;
		boolean completed = false;
		boolean enteredGoal = false;
		
		Exploration explore = new Exploration();
		explore.simulatorExplore(map, rob);

		do {
			explore.simTest(map, rob);
			count++;
		} while(count<20);

		do {
			if(rob.getX() == 12 && rob.getY() == 17) enteredGoal = true;
			if(rob.getX() == 0 && rob.getY() == 0 && enteredGoal) completed = true;
			explore.simulatorExplore2(map, rob);
		} while(!completed);

//		
//		Stack<Robot> pathTravelled = new Stack<Robot>();
//		pathTravelled.push(rob);
	}
	
}
