/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;
import java.util.ArrayList;
/**
 *
 * @author leijurv
 */
public class Function extends Expression {
    ArrayList<Object> contents;
    ArrayList<String> arguments;
    public Function(ArrayList<String> args,ArrayList<Object> com){
        contents=com;
        arguments=args;
    }
    @Override
    public Object evaluate(Context c){//foo=function(bar){blah}
        return this;
    }
    public Object run(Context c,ArrayList<Object> args){
        Context local=c.subContext();
        for (int i=0; i<args.size(); i++){
            local.defineLocal(arguments.get(i),args.get(i));
        }
        for (int i=0; i<contents.size(); i++){
            System.out.println("Line "+i+": "+contents.get(i)+" with context "+local);
            Command com=(Command) (contents.get(i));
            if(com.execute(local)){
                return local.getReturn();
            }
        }
        return null;
    }
    public String toString(){
        return "function ("+arguments+"){"+contents+"}";
    }
}
