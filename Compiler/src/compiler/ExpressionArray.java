/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
/**
 *
 * @author leijurv
 */
public class ExpressionArray extends Settable {
    private final Expression var;
    private final Expression index;
    public ExpressionArray(Expression var, Expression index) {
        this.var = var;
        this.index = index;
    }
    protected ExpressionArray(DataInputStream in) throws IOException {
        var = readExpression(in);
        index = readExpression(in);
    }
    @Override
    public Object evaluate(Context c) {
        Object[] dank = (Object[]) var.evaluate(c);
        int ind = (Integer) index.evaluate(c);
        return dank[ind];
    }
    @Override
    protected void doWriteExpression(DataOutputStream out) throws IOException {
        var.writeExpression(out);
        index.writeExpression(out);
    }
    @Override
    public byte getExpressionID() {
        return 7;
    }
    @Override
    public String toString() {
        return "%" + var + "[" + index + "]";
    }
    @Override
    public void set(Context c, Object value) {
        Object[] dank = (Object[]) var.evaluate(c);
        int ind = (Integer) index.evaluate(c);
        try {
            dank[ind] = value;
        } catch (Exception e) {
            System.out.println(Arrays.asList(dank) + "," + index + "," + ind);
            throw e;
        }
    }
}
