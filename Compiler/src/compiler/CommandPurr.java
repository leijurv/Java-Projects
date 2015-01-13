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
public class CommandPurr extends Command{
    private final Expression condition;
    private final ArrayList<Command> contents;
    public CommandPurr(ArrayList<Command> Contents,Expression cond){
        condition=cond;
        contents=Contents;
    }
    @Override
    public boolean execute(Context c){
        Context local=c.subContext();
        while (isTrue(condition.evaluate(c))){
            for (Command com:contents){
                if (com.execute(local)){
                    c.Pounce(local.getPounce());
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public String toString(){
        return "purr"+condition+"{"+contents+"}";
    }
}
