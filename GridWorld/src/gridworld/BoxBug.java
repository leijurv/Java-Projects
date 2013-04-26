package gridworld;
public class BoxBug extends Bug{
	private int steps=0;
	private int sideLength;
	public BoxBug(int length){
		super();
		sideLength=length;
	}
	public final void act(){
            
		if (steps<sideLength){
			if (canMove()){
				move();
                                steps++;
			}else{
				turn();
				turn();
				steps=0;
			}
		}else{
			turn();
			turn();
			steps=0;
		}
	}

}
