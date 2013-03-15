/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

/**
 *
 * @author leijurv
 */
public class EntityLiving extends Entity{
    double health=0;
    boolean dead=false;
    public EntityLiving(World world, Vertex position){
        super(world,position);
    }
    public EntityLiving(World world, Vertex position, double xsize, double ysize, double zsize){
        super(world,position,xsize,ysize,zsize);
    }
    public EntityLiving(World world, Vertex position,double heal){
        this(world,position);
        health=heal;
    }
    public void doDamage(double damage){
        health =- damage;
        if (health<=0){
            dead=true;
            health=0;
        }
    }
}
