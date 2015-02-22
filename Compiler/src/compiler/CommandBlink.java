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
public class CommandBlink extends Command {
    private final Expression condition;
    private final ArrayList<Command> ifTrue;
    private final ArrayList<Command> ifFalse;
    public CommandBlink(Expression condition,ArrayList<Command> ifTrue) {
        this(condition,ifTrue,new ArrayList<Command>());
    }
    public CommandBlink(Expression condition,ArrayList<Command> ifTrue,ArrayList<Command> ifFalse) {
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }
    protected CommandBlink(DataInputStream in) throws IOException {
        condition = Expression.readExpression(in);
        ifTrue = readmultiple(in);
        ifFalse = readmultiple(in);
    }
    @Override
    public boolean execute(Context c) {
        Object exp = condition.evaluate(c);
        Context local = c.subContext();
        Boolean b = isTrue(exp);
        if (b) {
            for (Command com : ifTrue) {
                if (com.execute(local)) {
                    c.Pounce(local.getPounce());
                    return true;
                }
            }
        } else {
            for (Command com : ifFalse) {
                if (com.execute(local)) {
                    c.Pounce(local.getPounce());
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public String toString() {
        return "blink" + condition + "{" + ifTrue + "}" + (ifFalse.isEmpty() ? "" : "else{" + ifFalse + "}");
    }
    @Override
    public byte getCommandID() {
        return 1;
    }
    @Override
    protected void doWrite(DataOutputStream out) throws IOException {
        condition.writeExpression(out);
        writemultiple(out,ifTrue);
        writemultiple(out,ifFalse);
    }
}
