/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
/**
 *
 * @author leijurv
 */
public class CommandPurr extends Command {
    private final Expression condition;
    private final ArrayList<Command> contents;
    public CommandPurr(ArrayList<Command> contents,Expression condition){
        this.contents=contents;
        this.condition=condition;
    }
    protected CommandPurr(DataInputStream in) throws IOException{
        condition=Expression.readExpression(in);
        contents=readmultiple(in);
    }
    @Override
    public boolean execute(Context c){
        Context local=c.subContext();
        while (isTrue(condition.evaluate(c))){
            for (Command com : contents){
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
    @Override
    public byte getCommandID(){
        return 3;
    }
    @Override
    protected void doWrite(DataOutputStream out) throws IOException{
        condition.writeExpression(out);
        writemultiple(out,contents);
    }
}
