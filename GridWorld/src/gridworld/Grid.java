package gridworld;
import java.util.*;


public interface Grid <E>{
	int getNumRows();
	int getNumCols();
	boolean isValid(Location loc);
	Object put(Location loc, E obj);
	Object remove(Location loc);
	Object get(Location loc);
	ArrayList<Location> getOccupiedLocations();
	ArrayList<Location> getValidAdjacentLocations(Location loc);
	ArrayList<Location> getEmptyAdjacentLocations(Location loc);
	ArrayList<Location> getOccupiedAdjacentLocations(Location loc);
	ArrayList<E> getNeighbors(Location loc);
	void print();
}
