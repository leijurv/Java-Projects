package model;

import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JOptionPane;

public class Control {
Board b; //the graphic board

ArrayList<Neuron> neurons=new ArrayList<Neuron>();   //all 168 neurons
static final int size=Integer.parseInt(JOptionPane.showInputDialog("How many on a side?"));
Square[][] squares=new Square[size][size];

int[][] moves={
		{-2,-2, 2, 2,-1,-1, 1, 1},
		{ 1,-1, 1,-1, 2,-2, 2,-2}};

public Control(Board b){
	this.b=b;

	//create 64 squares
	for(int row=0;row<size;row++)
		for (int col=0;col<size;col++)
			squares[row][col]=new Square(row,col);

	//create neurons
	for(int row=0;row<size;row++)
		for(int col=0;col<size;col++)
			findMoves(squares[row][col]);

	dl();//draw the initial active neurons on the graphic

	//try this many enumerations of the board before giving up
	int counter=1000000;

	//the main updating loop
	while(counter>0){
		for(Object o:neurons)((Neuron)o).updateState();//update all the states
		for(Object o:neurons)((Neuron)o).updateOutput();//then all the outputs
		counter--;
if (counter%100000==0){
    dl();
    System.out.println(counter);
}
		//if(isStable())break;
	}

	dl();	//draw the neurons when the solution is found/attempt abandoned
}

/**
 * draws the lines (active neurons) on the graphic display
 */
private void dl(){
	b.clear();
	for(Object o:neurons)
		b.drawLine((Neuron)o);
	b.repaint();


}


/**
 * Identify all of the squares legal to move to from this one - link with a neuron,
 * then add the neuron to the collection
 * 
 * @param s
 */
private void findMoves(Square s){

	for (int i=0;i<moves[0].length;i++){
		int newRow=s.row+moves[0][i];
		int newCol=s.col+moves[1][i];

		if(isInBounds(newRow,newCol)){

			Neuron n=s.link(squares[newRow][newCol]);
			if (n!=null)neurons.add(n);			
		}			
	}

}

/**
 * tests whether the identified square is contained within the (8*8) board
 * @param row
 * @param col
 * @return
 */
private boolean isInBounds(int row,int col){
	if (row>=0 && row<size && col>=0 && col<size)return true;
	return false;
}


/**
 * returns true if no neuron changes its state/output
 * @return
 */
private boolean isStable(){

	for (Object o:neurons)
		if(((Neuron)o).hasChanged())
			return false;
	return true;

}



public static void main(String[]s){
	Board b=new Board(50,50,60);
	new Control(b);
}

}