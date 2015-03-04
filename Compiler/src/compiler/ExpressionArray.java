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
public class ExpressionArray extends Expression implements Settable {
    String var;
    Expression index;
    public ExpressionArray(String var, Expression index) {
        this.var = var;
        this.index = index;
    }
    protected ExpressionArray(DataInputStream in) throws IOException {
        var = in.readUTF();
        index = readExpression(in);
    }
    @Override
    public Object evaluate(Context c) {
        Object[] dank = (Object[]) c.get(var);
        int ind = (Integer) index.evaluate(c);
        return dank[ind];
    }
    @Override
    protected void doWriteExpression(DataOutputStream out) throws IOException {
        out.writeUTF(var);
        index.writeExpression(out);
    }
    @Override
    public byte getExpressionID() {
        return 7;
    }
    @Override
    public String toString() {
        return var + "[" + index + "]";
    }
    @Override
    public void set(Context c, Object value) {
        Object[] dank = (Object[]) c.get(var);
        int ind = (Integer) index.evaluate(c);
        dank[ind] = value;
    }
}
