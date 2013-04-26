package gridworld;
import java.util.ArrayList;
import java.util.ListIterator;


public class BoundedGrid<E> extends AbstractGrid<E>{
	private int Cols;
	private int Rows;
	private Object[][] occupantArray;
	public BoundedGrid(int rows, int cols){
		Rows=rows;
		Cols=cols;
		occupantArray=new Object[rows][cols];
	}
	public int getNumRows() {
		return 0;
	}

	@Override
	public int getNumCols() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isValid(Location loc) {
		int row=loc.getRow();
		int col=loc.getCol();
		if (col>Cols){
			return false;
		}
		if (col<0){
			return false;
		}
		if (row<0){
			return false;
		}
		if (row>Rows){
			return false;
		}
		return true;
	}

	@Override
	public Object put(Location loc, Object obj) {
		occupantArray[loc.getRow()][loc.getCol()] =obj;
		return null;
	}

	@Override
	public E remove(Location loc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object get(Location loc) {
		// TODO Auto-generated method stub
		return occupantArray[loc.getRow()][loc.getCol()];
	}

	@Override
	public ArrayList<Location> getOccupiedLocations() {
		// TODO Auto-generated method stub
		ArrayList<Location> res=new ArrayList<Location>();
		for (int i=0; i<Rows; i++){
			for (int n=0; n<Cols; n++){
				Object cur=occupantArray[i][n];
				if (cur!=null){
					res.add(new Location(i,n));
				}
			}
		}
		return res;
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
