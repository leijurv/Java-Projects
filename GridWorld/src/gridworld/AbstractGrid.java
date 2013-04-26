package gridworld;
import java.util.ArrayList;


public abstract class AbstractGrid<E> implements Grid<E>{
	public ArrayList<E> getNeighbors(Location loc){
		ArrayList<Location> list=getOccupiedAdjacentLocations(loc);
		ArrayList<Actor> q=new ArrayList<Actor>();
		for (Location l : list){
			q.add((Actor) get(l));
		}
		ArrayList<E> q2 = ((ArrayList<E>) q);
		return 	q2;
	}
	public ArrayList<Location>getValidAdjacentLocations(Location loc){
		ArrayList<Location> l=new ArrayList<Location>();
		for (int i=0; i<8; i++){
			Location ll=loc.getAdjacentLocation(i*45);
			if (isValid(ll)){
				l.add(ll);
			}
		}
		return l;
	}
	public ArrayList<Location>getEmptyAdjacentLocations(Location loc){
		ArrayList<Location> l=getValidAdjacentLocations(loc);
		ArrayList<Location> ll=new ArrayList<Location>();
		for (Location lll : l){
			if (get(lll)==null){
				ll.add(lll);
			}
		}
		return ll;
	}
	
	public ArrayList<Location>getOccupiedAdjacentLocations(Location loc){
		ArrayList<Location> l=getValidAdjacentLocations(loc);
		ArrayList<Location> ll=new ArrayList<Location>();
		for (Location lll : l){
			if (get(lll)!=null){
				ll.add(lll);
			}
		}
		return ll;
	}
	public String toString(){
		ArrayList<Location> locs=getOccupiedLocations();
		String s="";
		for (Location l : locs){
			if (s.length()>1){
				s+=", ";
			}
			s+=l;
			s+=get(l);
		}
		return s;
		
	}
	
}
