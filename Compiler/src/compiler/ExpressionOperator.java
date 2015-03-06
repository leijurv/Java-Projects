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
        ADD("+"), SUBTRACT("-"), MULTIPLY("*"), DIVIDE("/"), MOD("%"), TOTHEPOWER("^"), GREATER(">"), LESSER("<"), GREATEROREQUAL(">="), LESSEROREQUAL("<="), EQUAL("=="), AND("&&"), OR("||"), NOTEQUAL("!=");
        private final String opcode;
        private Operator(String opcode) {
            this.opcode = opcode;
        }
        public Object run(Object a, Object b) {
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
                    return a.toString() + b.toString();
                case SUBTRACT:
                    if (a instanceof Double) {
                        if (b instanceof Double) {
                            return (Double) a - (Double) b;
                        }
                        if (b instanceof Integer) {
                            return (Double) a - (Integer) b;
                        }
                    }
                    if (b instanceof Double && a instanceof Integer) {
                        return (Integer) a - (Double) b;
                    }
                    if (a instanceof Integer && b instanceof Integer) {
                        return (Integer) a - (Integer) b;
                    }
                    break;
                case MULTIPLY:
                    if (a instanceof Double) {
                        if (b instanceof Double) {
                            return (Double) a * (Double) b;
                        }
                        if (b instanceof Integer) {
                            return (Double) a * (Integer) b;
                        }
                    }
                    if (b instanceof Double && a instanceof Integer) {
                        return (Integer) a * (Double) b;
                    }
                    if (a instanceof Integer && b instanceof Integer) {
                        return (Integer) a * (Integer) b;
                    }
                    break;
                case DIVIDE:
                    if (a instanceof Double) {
                        if (b instanceof Double) {
                            return (Double) a / (Double) b;
                        }
                        if (b instanceof Integer) {
                            return (Double) a / (Integer) b;
                        }
                    }
                    if (b instanceof Double && a instanceof Integer) {
                        return (Integer) a / (Double) b;
                    }
                    if (a instanceof Integer && b instanceof Integer) {
                        return (Integer) a / (Integer) b;
                    }
                    break;
                case MOD:
                    return (Integer) a % (Integer) b;
                case TOTHEPOWER:
                    double result = Math.pow(toDouble(a), toDouble(b));
                    if (Math.floor(result) == result) {//If it's a round number, return an int just because
                        return (int) result;
                    }
                    return result;
                case GREATER:
                    return compare(a, b) == 1;
                case LESSER:
                    return compare(a, b) == -1;
                case GREATEROREQUAL:
                    return compare(a, b) != -1;
                case LESSEROREQUAL:
                    return compare(a, b) != 1;
                case EQUAL:
                    return compare(a, b) == 0;
                case AND:
                    return isTrue(b) && isTrue(a);
                case OR:
                    return isTrue(b) || isTrue(a);
                case NOTEQUAL:
                    if (a == null || b == null) {
                        return (a == null) != (b == null);
                    }
                    return compare(a, b) != 0;
                default:
                    throw new IllegalStateException("Amount of dank swamp kush too low. Aquire more from Shrek then continue.");
            }
            throw new IllegalStateException("Unable to preform operation " + this + " on '" + a + "' and '" + b + "'");
        }
        private static int compare(Object a, Object b) {
            if (a == null || b == null) {
                if (a == null && b == null) {
                    return 0;
                }
                throw new NullPointerException("Unable to compare a null value and " + b + ", silly.");
            }
            if (!(a instanceof Comparable && b instanceof Comparable)) {//If they aren't comparable,
                if (a.equals(b)) {
                    return 0;
                }
            }
            try {
                return ((Comparable) a).compareTo((Comparable) b);
            } catch (Exception E) {
                if ((a instanceof Double || a instanceof Integer) && (b instanceof Integer || b instanceof Double)) {
                    return compare(toDouble(a), toDouble(b));
                }
            }
            throw new RuntimeException("#LOLNO you can't compare " + a + " and " + b + ", silly.");
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
    public ExpressionOperator(Expression before, String operator, Expression after) {
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
        if (beforeEvaluated instanceof Object[]) {
            return apply(operator, afterEvaluated, (Object[]) beforeEvaluated, false);
        }
        if (afterEvaluated instanceof Object[]) {
            return apply(operator, beforeEvaluated, (Object[]) afterEvaluated, true);
        }
        return operator.run(beforeEvaluated, afterEvaluated);
    }
    private static Object[] apply(Operator operator, Object a, Object[] b, boolean aBefore) {
        Object[] res = new Object[b.length];
        for (int i = 0; i < b.length; i++) {
            if (aBefore) {
                res[i] = operator.run(a, b[i]);
            } else {
                res[i] = operator.run(b[i], a);
            }
        }
        return res;
    }
    @Override
    public String toString() {
        return "(" + before + operator + after + ")";
    }
}
