package main;

public class RunTicker extends Thread{
	static int delay = 0;
	static boolean running=false;
	static void setDelay(int d){
		delay = d;
		
	}
    @Override
	public void run(){
		running=true;
		while(running){
                    if (delay>=10){
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
                    }
		Main.calcNext();
		Main.M.painting=true;
		
		Main.M.repaint();
		while(Main.M.ip()){}
		}
	}
}