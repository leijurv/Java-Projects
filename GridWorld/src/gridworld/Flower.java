package gridworld;
import java.awt.Color;

public class Flower extends Actor{
	public void act(){
		super.setColor(super.getColor().darker());
	}
	public Flower(){
		super();
		setColor(Color.PINK);
	}
	public Flower(Color c){
		super();
		setColor(c);
	}
}
