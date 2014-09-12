package mdpAlgorithm;

import java.util.Timer;
import java.util.TimerTask;
public class DelayMove {
	Timer timer;
	
	public DelayMove(int seconds) {
        timer = new Timer();
        timer.schedule(new DelayTask(), seconds * 1000);
	}
	
	class DelayTask extends TimerTask {
        public void run() {
            System.out.println("Time's up!");
            timer.cancel(); //Terminate the timer thread
        }
    }
}
