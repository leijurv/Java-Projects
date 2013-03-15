/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rubiks.cube;
import  java.util.ArrayList;
/**
 *
 * @author leif
 */
public class Node {
    static final int max_moves=21;
    CubePosition position;
    byte moves;
    boolean visited;
    Node prev;
    public Node(CubePosition pos){
        this();
        position=pos;
    }
    public Node(){
        visited=false;
        prev=null;
        moves=-1;
    }
    public ArrayList<CubePosition> neighbors(){
        if (moves>=max_moves){
            return new ArrayList<CubePosition>();
        }
        ArrayList<CubePosition> N=new ArrayList<CubePosition>();
        for (int axis=0; axis<3; axis++){
            for (int direction=0; direction<2; direction++){
                for (int slice=0; slice<2; slice++){
                    CubePosition New=position.clone();
                    if (slice==0){
                        New.rotate(axis,(direction*2)-1,0,false);
                    }else{
                        New.rotate(axis,(direction*2)-1,2,false);
                    }
                    N.add(New);
                }
            }
        }
        if (prev!=null){
            N.remove(prev.position);
        }
        return N;
    }
}
