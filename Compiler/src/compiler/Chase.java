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
public class Chase extends Expression{
    private final ArrayList<Command> contents;
    private final ArrayList<String> preyNames;
    public Chase(ArrayList<String> PreyNames,ArrayList<Command> commands){
        contents=commands;
        preyNames=PreyNames;
    }
    @Override
    public Object evaluate(Context c){//foo=    > chase(bar){blah} <      this is defining a chase not running it
        return this;
    }
    public Object run(Context c,ArrayList<Object> prey){
        Context local=c.subContext();
        for (int i=0; i<prey.size(); i++){
            local.defineLocal(preyNames.get(i),prey.get(i));
        }
        if (preyNames.size()>prey.size()){
            System.out.println("Received "+prey.size()+" prey, expected "+preyNames.size()+". Prey "+preyNames.subList(prey.size(),preyNames.size())+" will be null");
        }
        for (int i=0; i<contents.size(); i++){
            System.out.println("Line "+i+": "+contents.get(i)+" with context "+local);
            Command com=contents.get(i);
            if (com.execute(local)){
                return local.getPounce();
            }
        }
        return null;
    }
    @Override
    public String toString(){
        return "chase ("+preyNames+"){"+contents+"}";
    }
}
