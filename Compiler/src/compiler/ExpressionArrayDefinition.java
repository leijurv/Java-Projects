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
public class ExpressionArrayDefinition extends Expression {
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
}
