package gridworld;
import java.awt.Color;


public class Rock extends Actor{
	public void act(){
		//Does nothing
	}
	public Rock(){
		super();
		setColor(Color.BLACK);
		
	}
	public Rock(Color c){
		super();
		setColor(c);
	}
}
