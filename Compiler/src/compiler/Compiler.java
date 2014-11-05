/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

/**
 *
 * @author leijurv
 */
public class Compiler {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        String[] a=new CommandIf("5<6",new Command[]{new CommandIf("lol",new Command[]{new CommandSet("x","6")}),new CommandSet("5","6")}).compile(0);
        for (String A : a){
            System.out.println(A);
        }
    }    
}
