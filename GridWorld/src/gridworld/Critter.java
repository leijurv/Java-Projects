package gridworld;
import java.util.ArrayList;


public class Critter extends Actor{
	public void act(){
		processActors(getActors());
		makeMove(selectMoveLocation(getMoveLocations()));
		
	}
		public ArrayList<Actor>getActors(){
			Grid<Actor> g=super.getGrid();
			if (g==null){
				return null;
			}
			Location l=super.getLocation();
			return g.getNeighbors(l);
		}
		public void processActors(ArrayList<Actor>actors){
			Grid<Actor> g=super.getGrid();
			if (g==null){
				return;
			}
			for (Actor actor : actors){
				if (actor!=null){
					if (!(actor instanceof Rock) && !(actor instanceof Critter)){
						g.remove(actor.getLocation());
					}
					
				}
			}
		}
		public ArrayList<Location>getMoveLocations(){
			Grid<Actor> g=super.getGrid();
			if (g==null){
				return null;
			}
			Location l=super.getLocation();
			return g.getEmptyAdjacentLocations(l);
		}
		public Location selectMoveLocation(ArrayList<Location> locs){
		int n=locs.size();
		if (n==0){
			return super.getLocation();
			
		}
		int r=(int)(Math.random()*n);
		return locs.get(r);
		}
		public void makeMove(Location loc){
			moveTo(loc);
		}
}
