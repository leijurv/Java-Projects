/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package knightstour;

/**
 *
 * @author leijurv
 */
public class Neuron {
    public Neuron(){
        
    }
    public void meow(){
        a.connected.add(this);
        b.connected.add(this);
    }
    int curU;
    int curV;
    int newU;
    int newV;
    boolean same;
    Position a;
    Position b;
    boolean con;
    public boolean equals(Object o){
        if (o instanceof Neuron){
            Neuron n=(Neuron)o;
            if (n.a.equals(a) && n.b.equals(b)){
                return true;
            }
            if (n.b.equals(a) && n.a.equals(b)){
                return true;
            }
        }
        return false;
    }
    public void tick(){
        newU=curU+2;
        if (!KnightsTour.kitten)
            newU+=2;
        for (Neuron n : a.connected){
            newU-=n.curV;
        }
        for (Neuron n : b.connected){
            newU-=n.curV;
        }
        if (KnightsTour.kitten)
        newU+=2*curV;//curV has been included twice
        
        newV=newU>3?1:(newU<0?0:curV);
        if (newU>25){
            newU=20;
        }
        if (newU==curU){
            //newV=1;
        }
    }
    public void dub(){
        same=(curV==newV&&curU==newU);
        curV=newV;
        curU=newU;
    }
    public String toString(){
        return a+"   "+b;
    }
}
