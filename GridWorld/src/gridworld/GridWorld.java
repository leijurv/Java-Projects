package gridworld;
public class GridWorld {
	public static void main(String[]args){
		Grid<Actor> grid=new UnboundedGrid<Actor>();
		Actor rock=new BoxBug(4);
		rock.grid=grid;
		rock.moveTo(new Location(2,2));
                Actor rok=new Rock();
                rok.putSelfInGrid(grid,new Location(0,2));
		System.out.println(grid);
		rock.act();
		System.out.println(grid);
		rock.act();
		System.out.println(grid);
		rock.act();
		System.out.println(grid);
		rock.act();
		System.out.println(grid);
		rock.act();
		System.out.println(grid);
		rock.act();
		System.out.println(grid);
	}
}
