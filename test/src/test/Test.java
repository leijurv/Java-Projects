/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author leijurv
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
	pr(ap(gh(", "),gr("!")));
    }
    public static void pr(String h){
    	String q=h;
    	System.out.println(q);
    }
    public static String gr(String h){
    	return "World"+h;
    }
    public static String ap(String q, String h){
	return q+h;
    }
    public static String gh(String q){
	return "Hello"+q;
    }
}
