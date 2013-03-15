/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

/**
 *
 * @author leijurv
 */
public class Dog extends EntityLiving{
    String name;
    public Dog(World world, double cutness, double helth){
        super(world,new Vertex(0,0,0),0.5,1,2);
        health=helth;
    }
    public Dog(World world, String nme, double helth){
        super(world,new Vertex(0,0,0),0.5,1,2);
        health=helth;
        name=nme;
    }
    public void kill(EntityLiving thing){
        if (thing instanceof Cat){
            System.out.println("Cats are invincible. " + ((Cat)thing).name + " can't be killed.");
            return;
        }
        if (thing instanceof Dog){
            ((Dog)thing).die(this);
        }
    }
    public void die(EntityLiving killer){
        if (name.equals("Momo")){
            System.out.println("Momo is invincible.");
            return;
        }
        doDamage(health+1);
        
        if (killer instanceof Cat){
            System.out.println(((Cat)killer).name + " killed " + name + "!");
            return;
        }
        if (killer instanceof Dog){
            System.out.println(((Dog)killer).name + " killed " + name + "!");
            return;
        }
    }
}
