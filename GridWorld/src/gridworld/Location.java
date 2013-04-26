package gridworld;
public class Location{
	private int Row;
	private int Col;  
	public static final int NORTH=0;
	public static final int EAST=90;
	public static final int SOUTH=180;
	public static final int WEST=270;
	public static final int NORTHEAST=45;
	public static final int SOUTHEAST=135;
	public static final int SOUTHWEST=225;
	public static final int NORTHWEST=315;
	public static final int LEFT=-90;
	public static final int RIGHT=90;
	public static final int HALF_LEFT=-45;
	public static final int HALF_RIGHT=45;
	public static final int FULL_CIRCLE=360;
	public static final int HALF_CIRCLE=180;
	public static final int AHEAD=0;
	public Location(int r, int c){
		Row=r;
		Col=c;
	}
	public int getCol() {
		return Col;
	}
	public int getRow(){
		return Row;
	}
	public boolean equals(Object other){
		if(other instanceof Location){
			Location l=(Location)other;
			if (l.Row!=Row){
				return false;
			}
			if (l.Col!=Col){
				return false;
			}
			return true;
		}
		return false;
	}
	public int compareTo(Object other){
		if( other instanceof Location){
			Location l=(Location)other;
			if (l.Row>Row){
				return -1;
			}
			if (l.Row<Row){
				return 1;
			}
			if (l.Col>Col){
				return -1;
			}
			if (l.Col<Col){
				return 1;
			}
			return 0;
		}
		return (Integer) null;
	}
	public Location getAdjacentLocation(int direction){
		System.out.println(direction);
		switch(direction){
		case 0:
			return new Location(Row-1,Col);//NOTE: Might not be valid location
		case 90:
			return new Location(Row,Col+1);
		case 180:
			return new Location(Row+1,Col);
		case 270:
			return new Location(Row,Col-1);
		case 45:
			return new Location(Row-1,Col+1);
		case 135:
			return new Location(Row+1,Col+1);
		case 225:
			return new Location(Row+1,Col-1);
		case 315:
			return new Location(Row-1,Col-1);
		default:
			return null;
		}
	}
	public int getDirectionToward(Location target){
		int Rowdif=Row-target.Row;
		int Coldif=Col-target.Col;
		Coldif=0-Coldif;
		Rowdif=0-Rowdif;
		double d=Math.atan((double)Coldif/(double)Rowdif);
		
		if (Rowdif<0){
			d+=Math.PI;
		}
		double n=180*d/Math.PI;
		n=180-n;
		
		
		
		if(n<0){
			n+=360;
		}
		n=n/45;
		
		return (int)(45*Math.round(n));
	}
	public String toString(){
		return "("+Row+","+Col+")";
	}
	public int hashCode(){
		return Row*5+Col;
	}
}
