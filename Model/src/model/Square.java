package model;

import java.util.ArrayList;
import java.util.Vector;

public class Square {

ArrayList<Neuron> neurons=new ArrayList<Neuron>();//neurons which connect to this square
public int col;
public int row;

public Square(int row, int col){

	this.col=col;
	this.row=row;


}


/**
 * creates a neuron which links this square with the square s,
 * then tells both squares about it,
 * also returns the neuron for inclusion in the global list.
 * 
 * @param s
 * @return neuron n, or null
 */
public Neuron link(Square s){

	for(Object o: neurons)
		//discounts the link if it has already been created
		if (((Neuron)o).s1==s ||((Neuron)o).s2==s)return null;

	Neuron n=new Neuron(this,s);
	neurons.add(n);
	s.neurons.add(n);
	return n;

}


}