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
public class CommandBlink extends Command{
    private final Expression condition;
    private final ArrayList<Command> ifTrue;
    private final ArrayList<Command> ifFalse;
    public CommandBlink(Expression Condition,ArrayList<Command> IfTrue){
        this(Condition,IfTrue,new ArrayList<Command>());
    }
    public CommandBlink(Expression Condition,ArrayList<Command> IfTrue,ArrayList<Command> IfFalse){
        condition=Condition;
        ifTrue=IfTrue;
        ifFalse=IfFalse;
    }
    @Override
    public boolean execute(Context c){
        Object exp=condition.evaluate(c);
        Context local=c.subContext();
        Boolean b=isTrue(exp);
        if (b){
            for (Command com:ifTrue){
                if (com.execute(local)){
                    c.Pounce(local.getPounce());
                    return true;
                }
            }
        }else{
            for (Command com:ifFalse){
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
        return "blink"+condition+"{"+ifTrue+"}"+(ifFalse.isEmpty() ? "" : "else{"+ifFalse+"}");
    }
}
