/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package derp;
/**
 *
 * @author leif
 */
public class Derp {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.util.HashMap<String,Integer> NameOfVariable=new java.util.HashMap<String,Integer>();
        NameOfVariable.put("Jack", 5);
        NameOfVariable.put("Leif", -5);
        Jack<Boolean> derp=new Jack<Boolean>();
        System.out.println("Jack has a derp level of "+NameOfVariable.get("Jack"));
        System.out.println("Leif has a derp level of "+NameOfVariable.get("Leif"));
        System.out.println("Jack has a derp level of 5");
        System.out.println("Leif has a derp level of -10");
        java.util.HashMap<String,String> Languages=new java.util.HashMap<String,String>();
        Languages.put("Jacks Face","JackoLanguage");
        Languages.put("Books", "English");
        Languages.put("Your face, life, cat I win", "Your face, life, cat I wino language");
        System.out.println(Languages.get("Mow"));
        
        
    }
    public static class Jack<E>{
        
    }
    
}
