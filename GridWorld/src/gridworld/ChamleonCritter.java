package gridworld;
import java.util.ArrayList;


public class ChamleonCritter extends Critter{
	public void processActors(ArrayList<Actor>actors){
		Grid<Actor> g=super.getGrid();
		if (g==null){
			return;
		}
		int r=actors.size();
		int n=(int)(Math.random()*r);
		super.setColor(actors.get(n).getColor());
	}
	public void makeMove(Location loc){
		super.setDirection(super.getLocation().getDirectionToward(loc));
		moveTo(loc);
	}
}
