package gridworld;
import java.awt.*;

public class Actor {

	private int direction;
	private Location loc;
	 Grid<Actor> grid;
	private Color color;
	public Actor(){
		setColor(Color.BLUE);
		direction=Location.NORTH;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int Direction) {
		direction=Direction;
	}
	public Location getLocation() {
		return loc;
	}
	public void setLocation(Location l){
		loc=l;
	}
	public void moveTo(Location Loc) {
            ((Actor)grid.get(Loc)).removeSelfFromGrid();
			loc=Loc;
			grid.put(Loc,this);
		
		
		
	}
	public void removeSelfFromGrid(){
            if (grid!=null){
		grid.remove(loc);
            }
	}
	public void act(){
		System.out.println("MEOW");
	}
	public Grid<Actor> getGrid() {
		return grid;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public String toString(){
		return "Loc:"+loc+", Dir:"+((this instanceof Bug)?"BUG":"NOTBUG")+direction;
	}
        public void putSelfInGrid(Grid<Actor> gr, Location Loc){
            removeSelfFromGrid();
            grid=gr;
            moveTo(Loc);
        }
                
}
