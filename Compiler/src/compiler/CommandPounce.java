/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
/**
 *
 * @author leijurv
 */
public class CommandPounce extends Command {
    private final Expression toReturn;
    public CommandPounce(Expression toReturn) {
        this.toReturn = toReturn;
    }
    protected CommandPounce(DataInputStream in) throws IOException {
        toReturn = Expression.readExpression(in);
    }
    @Override
    public boolean execute(Context c) {
        c.Pounce(toReturn.evaluate(c));
        return true;
    }
    @Override
    public String toString() {
        return "$pounce " + toReturn + "$";
    }
    @Override
    public byte getCommandID() {
        return 2;
    }
    @Override
    protected void doWrite(DataOutputStream out) throws IOException {
        toReturn.writeExpression(out);
    }
}
