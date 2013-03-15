/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

/**
 *
 * @author leijurv
 */
public class World {

    /**
     * @param args the command line arguments
     */
    static Entity[] entities=new Entity[1];
    public World(){
        
    }
    public static void add(Entity entity){
        Entity[] entitynew=new Entity[entities.length+1];
        System.arraycopy(entities, 0, entitynew, 0, entities.length);
        entitynew[entities.length]=entity;
        entities=entitynew;
    }
    public static void main(String[] args) {
        World world=new World();
        add(new Cat(world,10,10,"Leif's Cat"));
        add(new Dog(world,"Horatio",10));
        ((Dog)entities[0]).kill((Cat)entities[1]);
        Dog h=(Dog)entities[0];
        ((Cat)entities[1]).kill(h);
    }
}
