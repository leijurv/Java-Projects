/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package breeding;

import java.util.Random;

/**
 *
 * @author leijurv
 */
public class Bunny {
    boolean Trait1;
    boolean Trait2;
    public Bunny(boolean a, boolean b, Random r){
        Trait1=a;
        Trait2=b;
        mutate(r);
    }
    public Bunny(Bunny a, Bunny b, Random r){
        if (r.nextBoolean()){
            Trait1=a.Trait1;
        }else{
            Trait1=a.Trait2;
        }
        if (r.nextBoolean()){
            Trait2=b.Trait1;
        }else{
            Trait2=b.Trait2;
        }
        mutatelittle(r);
    }
    public boolean SHOULDDIE(){
        return (!Trait1)&&(!Trait2);
    }
    public void mutate(Random r){
        if (r.nextInt(2000)==0){
            Trait1=true;
        }
        if (r.nextInt(2000)==0){
            Trait2=true;
        }
    }
    public void mutatelittle(Random r){
        if (r.nextInt(1000000)==0){
            Trait1=!Trait1;
        }
        if (r.nextInt(1000000)==0){
            Trait2=!Trait2;
        }
    }
}
