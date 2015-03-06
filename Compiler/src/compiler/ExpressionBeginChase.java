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
public class ExpressionBeginChase extends Expression {
    private final String chasename;
    private final ArrayList<Expression> prey;
    public ExpressionBeginChase(String chasename, ArrayList<Expression> prey) {
        this.prey = prey;
        this.chasename = chasename;
    }
    protected ExpressionBeginChase(DataInputStream in) throws IOException {
        chasename = in.readUTF();
        int numPrey = in.readInt();
        prey = new ArrayList<>(numPrey);
        for (int i = 0; i < numPrey; i++) {
            prey.add(readExpression(in));
        }
    }
    @Override
    public Object evaluate(Context c) {
        ArrayList<Object> preyVals = new ArrayList<>(prey.size());
        for (int i = 0; i < prey.size(); i++) {
            preyVals.add(prey.get(i).evaluate(c));
        }
        if (chasename.equals("meow")) {
            if (preyVals.size() > 1) {
                throw new IllegalStateException("Can't meow more than 1 thing. Use an array.");
            }
            Object o = preyVals.get(0);
            if (o instanceof Object[]) {
                o = (Arrays.asList((Object[]) o));
            }
            System.out.println("MEOWING " + o);
            return preyVals;
        }
        if (chasename.equals("len")) {
            return ((Object[]) preyVals.get(0)).length;
        }
        if (chasename.equals("array")) {
            return new Object[(Integer) preyVals.get(0)];
        }
        System.out.println("BEGINNING " + chasename + " " + " with prey " + prey + " and context " + c);
        Chase f = (Chase) (c.get(chasename));
        System.out.println("Evaluated args as: " + preyVals);
        return f.run(c, preyVals);
    }
    @Override
    public String toString() {
        return "~begin " + chasename + " " + prey + "~";
    }
    @Override
    protected void doWriteExpression(DataOutputStream out) throws IOException {
        out.writeUTF(chasename);
        out.writeInt(prey.size());
        for (Expression pre : prey) {
            pre.writeExpression(out);
        }
    }
    public byte getExpressionID() {
        return 2;
    }
}
