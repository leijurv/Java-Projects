/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

/**
 *
 * @author leijurv
 */
public class Vertex {
    double x=0;
    double y=0;
    double z=0;
    public Vertex(double ecks, double why, double zee){
        x=ecks;
        y=why;
        z=zee;
    }
    public double distance(double x1, double y1, double z1){
        return Math.sqrt(((x1-x)*(x1-x))+((y1-y)*(y1-y))+((z1-z)*(z1-z)));
    }
    public double distance(Vertex p){
        return distance(p.x,p.y,p.z);
    }
}
