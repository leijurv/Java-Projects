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
public class ExpressionSetVariable extends Expression {
    private final Settable set;
    private final Expression value;
    public ExpressionSetVariable(String varname, Expression val) {
        ExpressionVariable v = new ExpressionVariable(varname);
        set = v;
        this.value = val;
    }
    public ExpressionSetVariable(Settable set, Expression value) {
        this.set = set;
        this.value = value;
    }
    protected ExpressionSetVariable(DataInputStream in) throws IOException {
        set = (Settable) readExpression(in);
        value = readExpression(in);
    }
    @Override
    public Object evaluate(Context c) {
        Object o = value.evaluate(c);
        set.set(c, o);
        return o;
    }
    @Override
    public String toString() {
        return "~set~ " + set + "=" + value;
    }
    @Override
    protected void doWriteExpression(DataOutputStream out) throws IOException {
        set.writeExpression(out);
        value.writeExpression(out);
    }
    @Override
    public byte getExpressionID() {
        return 6;
    }
}
