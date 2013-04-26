package gridworld;
import java.awt.*;
public class Bug extends Actor{
	public Bug(){
		super();
		setColor(Color.RED);
		
	}
	public Bug(Color c){
		super();
		setColor(c);
	}
	public void move(){
		Grid<Actor> g=super.getGrid();
		if (g==null){
			return;
		}
		Location l=super.getLocation();
		Location n=l.getAdjacentLocation(super.getDirection());
                if (!g.isValid(n)){
			removeSelfFromGrid();
			return;
		}
		moveTo(n);
		Flower f=new Flower(super.getColor());
		f.putSelfInGrid(g,l);
	}
	public void act(){
		Grid<Actor> g=super.getGrid();
		Location l=super.getLocation();
                
		Location n=l.getAdjacentLocation(super.getDirection());
                
		if (canMove()){
			move();
		}else{
    turn();
}
	}
	public boolean canMove(){
		Grid<Actor> g=super.getGrid();
		if (g==null){
			return false;
		}
		Location l=super.getLocation();
		Location n=l.getAdjacentLocation(super.getDirection());
		if (!g.isValid(n)){
			return false;
		}
		if (g.get(n) instanceof Flower){
			return true;
		}
		return g.get(n)==null ;
	}
	public void turn(){
		super.setDirection(super.getDirection()+45);
	}
}
