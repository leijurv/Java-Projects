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
public class CommandSet extends Command{
    String a;
    String b;
    public CommandSet(String A, String B){
        a=A;
        b=B;
    }
    @Override
    public String[] compile(int start){
        return new String[]{a+"<-"+b};
    }
    
}
