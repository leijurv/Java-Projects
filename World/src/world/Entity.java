/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

/**
 *
 * @author leijurv
 */
public class Entity {
    Vertex position;
    double xSize=0;
    double ySize=0;
    double zSize=0;
    public Entity(World world, double x1, double y1, double z1, double x2, double y2, double z2){
        this(world,new Vertex(x1,y1,z1),x2,y2,z2);
        
    }
    public Entity(World world, Vertex positon, double x2, double y2, double z2){
        position=positon;
        xSize=x2;
        ySize=y2;
        zSize=z2;
    }
    public Entity(World world, Vertex positon){
        position=positon;
    }
    public Entity(World world, double x1, double y1, double z1){
        this(world,new Vertex(x1,y1,z1));
    }
    public double distance(Vertex p){
        return position.distance(p);
    }
    public double distance(double x1, double y1, double z1){
        return distance(new Vertex(x1,y1,z1));
    }
}
