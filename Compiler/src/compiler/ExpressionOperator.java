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
public class ExpressionOperator extends Expression {
    private final Operator operator;
    private final Expression before;
    private final Expression after;
    protected ExpressionOperator(DataInputStream in) throws IOException {
        operator = Operator.values()[in.readInt()];
        before = readExpression(in);
        after = readExpression(in);
    }
    @Override
    protected void doWriteExpression(DataOutputStream out) throws IOException {
        out.writeInt(operator.ordinal());
        before.writeExpression(out);
        after.writeExpression(out);
    }
    @Override
    public byte getExpressionID() {
        return 5;
    }
    public static enum Operator {
        ADD("+"), SUBTRACT("-"), MULTIPLY("*"), DIVIDE("/"), MOD("%"), TOTHEPOWER("^"), GREATER(">"), LESSER("<"), EQUAL("=="), AND("&&"), OR("||"), NOTEQUAL("!="), GREATEROREQUAL(">="), LESSEROREQUAL("<=");
        private final String opcode;
        private Operator(String opcode) {
            this.opcode = opcode;
        }
        public Object run(Object a,Object b) {
            switch (this) {
                case ADD:
                    if (a instanceof Double) {
                        if (b instanceof Double) {
                            return (Double) a + (Double) b;
                        }
                        if (b instanceof Integer) {
                            return (Integer) b + (Double) a;
                        }
                    }
                    if (b instanceof Double && a instanceof Integer) {
                        return (Double) b + (Integer) a;
                    }
                    if (a instanceof Integer && b instanceof Integer) {
                        return (Integer) a + (Integer) b;
                    }
                    break;
                case SUBTRACT:
                    return (Integer) a - (Integer) b;
                case MULTIPLY:
                    return (Integer) a * (Integer) b;
                case DIVIDE:
                    return (Integer) a / (Integer) b;
                case MOD:
                    return (Integer) a % (Integer) b;
                case TOTHEPOWER:
                    double result = Math.pow(toDouble(a),toDouble(b));
                    if (Math.floor(result) == result) {//If it's a round number, return an int just because
                        return (int) result;
                    }
                    //System.out.println(A + "^" + B + "=" + result);
                    return result;
                case GREATER:
                    return compare(a,b) == 1;
                case LESSER:
                    return compare(a,b) == -1;
                case EQUAL:
                    return b.equals(a);
                case AND:
                    return (Boolean) b && (Boolean) a;
                case OR:
                    return (Boolean) b || (Boolean) a;
                case NOTEQUAL:
                    return !a.equals(b);
                case GREATEROREQUAL:
                    return compare(a,b) != -1;
                case LESSEROREQUAL:
                    return compare(a,b) != 1;
                default:
                    //This is null??
                    return null;
            }
            throw new IllegalStateException("Unable to preform operation " + this + " on '" + a + "' and '" + b + "'");
        }
        private static int compare(Object a,Object b) {
            try {
                return ((Comparable) a).compareTo((Comparable) b);
            } catch (ClassCastException E) {
                if ((a instanceof Double || a instanceof Integer) && (b instanceof Integer || b instanceof Double)) {
                    return compare(toDouble(a),toDouble(b));
                }
                throw new RuntimeException("#LOLNO you can't compare " + a + " and " + b + ", silly.");
            }
        }
        private static double toDouble(Object a) {
            return (a instanceof Integer) ? (double) (((Integer) a)) : (Double) a;
        }
        public boolean matches(String opcode) {
            return this.opcode.equals(opcode);
        }
        public static Operator get(String operation) {
            for (Operator o : Operator.values()) {
                if (o.matches(operation)) {
                    return o;
                }
            }
            return null;
        }
    }
    public ExpressionOperator(Expression before,String operator,Expression after) {
        this.operator = Operator.get(operator);
        this.before = before;
        this.after = after;
        if (this.operator == null) {
            throw new UnsupportedOperationException("Unknown operator " + operator + " trying to be applied to " + before + " and " + after);
        }
    }
    @Override
    public Object evaluate(Context c) {
        Object beforeEvaluated = before.evaluate(c);
        Object afterEvaluated = after.evaluate(c);
        return operator.run(beforeEvaluated,afterEvaluated);
    }
    @Override
    public String toString() {
        return "(" + before + operator + after + ")";
    }
}
