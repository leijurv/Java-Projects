package gridworld;
import java.util.*;


public class UnboundedGrid<E> extends AbstractGrid<E>{
	private Map<Location,Actor> occupantMap;
	public UnboundedGrid(){
		occupantMap=new HashMap<Location,Actor>();
				}
	@Override
	public int getNumRows() {
		
		return -1;
	}

	@Override
	public int getNumCols() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public boolean isValid(Location loc) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object put(Location loc, Object obj) {
	Actor a=(Actor) obj;
		occupantMap.put(loc,a);
		return obj;
	}

	@Override
	public Object remove(Location loc) {
		Actor a=(Actor)occupantMap.get(loc);
		occupantMap.remove(loc);
		return a;
	}

	@Override
	public Object get(Location loc) {
		Actor a=(Actor) occupantMap.get(loc);
		return a;
	}

	@Override
	public ArrayList<Location> getOccupiedLocations() {
		// TODO Auto-generated method stub
		Set<Location> l=occupantMap.keySet();
		ArrayList<Location> lll=new ArrayList<Location>();
		for (Location ll : l){
			lll.add(ll);
		}
		return lll;
	}
	public void print(){
		ArrayList<Location> l=getOccupiedLocations();
		ArrayList<Actor> a=new ArrayList<Actor>();
		for (Location b : l){
			a.add((Actor) get(b));
		}
		ListIterator ab=a.listIterator();
		
		for (Actor q=(Actor) ab.next(); ab.hasNext(); q=(Actor)ab.next()){
			System.out.println(q);
		}
	}
}
