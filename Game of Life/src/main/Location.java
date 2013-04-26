package main;


public class Location{
        
	int Row;
	int Col;
	public Location(int row, int col){
		Row=row;
		Col=col;
                if (!Main.M.unbounded1){
                    //Row=calcRow(Row,Main.minRow,Main.maxRow);
                    //Col=calcRow(Col,Main.minCol,Main.maxCol);
                }
	}
        public static int calcRow(int row,int minRow,int maxRow){
            if (minRow!=0 && row<0){
                return calcRow(row+minRow,minRow,maxRow);
            }
            if (maxRow!=0 && row>maxRow){
                System.out.println(row+" "+maxRow);
                return calcRow(row-maxRow,minRow,maxRow);
            }
            return row;
        }
	public Location[] adjacent(){
		Location[] result={new Location(Row-1,Col),//NORTH
		new Location(Row-1,Col+1),//NORTHEAST
		new Location(Row,Col+1),//EAST
		new Location(Row+1,Col+1),//SOUTHEAST
		new Location(Row+1,Col),//SOUTH
		new Location(Row+1,Col-1),//SOUTHWEST
		new Location(Row,Col-1),//WEST
		new Location(Row-1,Col-1)};//NORTHWEST
		return result;
	}
	public String toString(){
		return "("+Row+","+Col+")";
	}
	public boolean equals(Object obk){
		Location l=(Location)obk;
		return (Row==l.Row && Col==l.Col);
	}
}