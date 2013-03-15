package model;
import java.awt.*;

public class GuiSquare extends Component{

int row,col;
int x;
int y;
int size;

public GuiSquare(int row,int col,int size){

	this.row=row;this.col=col;this.size=size;
	y=row*size;	x=col*size;
	setBounds(x,y,size,size);
	setBackground((row+col)%2==0?Color.white:Color.black);
}

public void paint(Graphics g){
	g.setColor(getBackground());		
	g.fillRect(0,0, size-1, size-1);
	g.setColor(Color.gray);
	g.drawString(""+((row*8)+col), size/2, size/2);
}
}