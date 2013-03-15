package model;

import java.util.Random;

public class Neuron {


boolean oldActive=true,active=true; //ie part of the solution
int state=0,previousState=0;

public Square s1,s2;

public Neuron(Square s1,Square s2){
	this.s1=s1;this.s2=s2;


	//status of the neuron is initialised randomly
	oldActive=active= (new Random()).nextInt(2)==1?true:false;

}


public int activeNeighbours(){
    int s=0; //changed
    for(Object o:s1.neurons)
            if(((Neuron)o).isActive())s++;
    for(Object o:s2.neurons)
            if(((Neuron)o).isActive())s++;
    return s;
}

public boolean hasChanged(){
    return  oldActive != active||previousState != state;
}



public boolean isActive(){return active;}

public void updateState(){
    previousState=state;
    state+=4-activeNeighbours(); //changed
}

public void updateOutput(){
	oldActive=active;
	if (state>3) active=true;
	else if(state<0)active=false;

}


}