/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

/**
 *
 * @author leijurv
 */
public class Cat extends EntityLiving{
    double cuteness=0;
    String name="";
    public Cat(World world, double cutness, double helth, String nam){
        super(world,new Vertex(0,0,0),0.5,1,2);
        health=helth;
        cuteness=cutness;
        name=nam;
    }
    public void kill(EntityLiving thing){
        if (thing instanceof Dog){
            ((Dog)thing).die(this);
        }
    }
    
}
