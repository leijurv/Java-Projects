/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package runtime.and.garbagecollection;

/**
 *
 * @author kevinmcclelland
 */
public class RuntimeAndGarbageCollection {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        long a=System.currentTimeMillis();
        System.out.println(a+" milliseconds to start the run.");
        System.out.println();
        
        
        
        System.out.println("Runtime!");
              Runtime rt=Runtime.getRuntime();
        System.out.println("Free memory in jvm(Java Virtual Machine) before Garbage Collection = "+rt.freeMemory());
               rt.gc();
        System.out.println("Free memory in jvm(Java Virtual Machine) before Garbage Collection = "+rt.freeMemory());      
              
               
             
             
        System.out.println();
        long b=System.currentTimeMillis();
        System.out.println(b-a+" milliseconds to run build.");
    }
}
