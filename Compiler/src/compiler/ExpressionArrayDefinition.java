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
import java.util.Arrays;
/**
 *
 * @author leijurv
 */
public class ExpressionArrayDefinition extends Settable {
    private final Expression[] expressions;
    public ExpressionArrayDefinition(Expression[] expressions) {
        this.expressions = expressions;
    }
    protected ExpressionArrayDefinition(DataInputStream in) throws IOException {
        expressions = new Expression[in.readInt()];
        for (int i = 0; i < expressions.length; i++) {
            expressions[i] = readExpression(in);
        }
    }
    @Override
    public Object evaluate(Context c) {
        Object[] result = new Object[expressions.length];
        for (int i = 0; i < expressions.length; i++) {
            result[i] = expressions[i].evaluate(c);
        }
        return result;
    }
    @Override
    protected void doWriteExpression(DataOutputStream out) throws IOException {
        out.writeInt(expressions.length);
        for (Expression e : expressions) {
            e.writeExpression(out);
        }
    }
    @Override
    public byte getExpressionID() {
        return 8;
    }
    @Override
    public String toString() {
        return "#" + Arrays.asList(expressions).toString();
    }
    public ArrayList<Expression> contents() {
        return new ArrayList<>(Arrays.asList(expressions));
    }
    @Override
    public void set(Context c, Object value) {
        if (!(value instanceof Object[])) {
            throw new IllegalStateException("Cannot set array to scalar");
        }
        Object[] array = (Object[]) value;
        if (array.length != expressions.length) {
            throw new IllegalStateException("Unbalanced array length");
        }
        for (int i = 0; i < expressions.length; i++) {
            Settable s = (Settable) expressions[i];
            s.set(c, array[i]);
        }
    }
}
