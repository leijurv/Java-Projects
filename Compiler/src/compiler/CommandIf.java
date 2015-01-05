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
public class CommandIf extends Command{
    Expression condition;
    ArrayList<Object> ifTrue;
    ArrayList<Object> ifFalse;
    public CommandIf(Expression Condition,ArrayList<Object> IfTrue){
        this(Condition,IfTrue,new ArrayList<>());
    }
    public CommandIf(Expression Condition,ArrayList<Object> IfTrue,ArrayList<Object> IfFalse){
        condition=Condition;
        ifTrue=IfTrue;
        ifFalse=IfFalse;
    }
    @Override
    public boolean execute(Context c){
        Object exp=condition.evaluate(c);
        Context local=c.subContext();
        Boolean b=(Boolean) exp;
        if (b){
            for (Object o:ifTrue){
                Command com=(Command) o;
                if (com.execute(local)){
                    c.Pounce(local.getPounce());
                    return true;
                }
            }
        }else{
            for (Object o:ifFalse){
                Command com=(Command) o;
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
        return "if"+condition+"{"+ifTrue+"}"+(ifFalse.isEmpty() ? "" : "else{"+ifFalse+"}");
    }
}
