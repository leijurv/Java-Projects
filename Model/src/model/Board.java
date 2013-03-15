package model;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JFrame;
import model.Neuron;
/**
* sets up the graphic representation of the chess board, and draws on the neurons as required
 * @param x
 * @param y
 * @param squareSize
 */
public class Board extends JFrame {
ArrayList<Line> lines=new ArrayList<Line>();
int squareSize;

public Board(int x, int y,int squareSize){

	//initialize dimensions etc
	super("there.");
	setDefaultCloseOperation(EXIT_ON_CLOSE);

	setBounds(x,y,squareSize*Control.size+8,squareSize*Control.size+30);
	this.setLayout(null);
	this.setVisible(true);
	this.squareSize=squareSize;

	//draw the squares
	drawSquares();

	repaint();
}

private void drawSquares(){
	for(int i=0;i<Control.size;i++)
		for (int j=0;j<Control.size;j++){
			GuiSquare s=new GuiSquare(i,j,squareSize);
			this.add(s,0);
		}
}

/**
 * represent the neuron as a line on the board
 * @param n
 */
public void drawLine(Neuron n){
	Line l=new Line(n.s1.col+n.s1.row*Control.size,n.s2.col+n.s2.row*Control.size,squareSize);
	if(n.isActive()){
		lines.add(l);
		add (l,0);
	}
}

/**
 * removes all of the lines (neurons) from the board
 */
public void clear(){
	for(Object o:lines)
		remove((Line)o);

	lines.clear();
}
}