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
import java.util.HashMap;
import java.util.List;
/**
 *
 * @author leijurv
 */
public abstract class Expression extends Command {
    static final ArrayList<List<String>> orderOfOperations = new ArrayList<>();
    static{
        orderOfOperations.add(Arrays.asList(new String[] {"%"}));
        orderOfOperations.add(Arrays.asList(new String[] {"^"}));
        orderOfOperations.add(Arrays.asList(new String[] {"*", "/"}));
        orderOfOperations.add(Arrays.asList(new String[] {"+", "-"}));
        orderOfOperations.add(Arrays.asList(new String[] {">", "<", "==", "!=", "<=", ">="}));
        orderOfOperations.add(Arrays.asList(new String[] {"||", "&&"}));
    }
    @Override
    public boolean execute(Context c) {
        evaluate(c);
        return false;
    }
    public abstract Object evaluate(Context c);
    private static final String[] let = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "y", "z"};
    private static boolean let(Object o) {//Does o consist of letters?
        if (o instanceof String) {
            String r = (String) o;
            if (r.length() > 1) {
                for (int i = 0; i < r.length(); i++) {
                    if (!let(r.substring(i, i + 1))) {
                        return false;
                    }
                }
                return true;
            }
            for (String s : let) {
                if (((String) o).equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }
    private static boolean num(Object o) {//Is o a number?
        if ((o instanceof ExpressionConstant)) {
            return true;
        }
        if (o instanceof String) {
            String s = (String) o;
            try {
                Double.parseDouble(s);//Le lazy
                return true;
            } catch (NumberFormatException e) {
                return s.startsWith(".") || s.endsWith(".");
            }
        }
        return false;
    }
    private static boolean eq(Object o) {
        if (o instanceof String) {
            return ((((String) o).replace(">", "").length() == 0) || (((String) o).replace("<", "").length() == 0) || ((String) o).replace("!", "").length() == 0) || (((String) o).replace("=", "").length() == 0) || (((String) o).replace("|", "").length() == 0) || (((String) o).replace("&", "").length() == 0);
        }
        return false;
    }
    public static ArrayList<Object> lex(String s) {
        Object[] objects = new Object[s.length()];
        for (int i = 0; i < objects.length; i++) {
            objects[i] = s.substring(i, i + 1);
        }
        int numItemsRemoved = 0;
        for (int i = 0; i < objects.length; i++) {
            if (objects[i].equals(" ")) {
                objects[i] = null;
                numItemsRemoved++;
            }
        }
        for (int i = 0; i < objects.length - 1; i++) {
            if (objects[i] != null && objects[i + 1] != null && eq(objects[i]) && eq(objects[i + 1])) {
                objects[i + 1] = (String) objects[i] + (String) objects[i + 1];
                objects[i] = null;
                numItemsRemoved++;
                i = Math.max(i - 2, 0);
            }
            if (objects[i] != null && objects[i + 1] != null && let(objects[i]) && let(objects[i + 1])) {
                objects[i + 1] = (String) objects[i] + (String) objects[i + 1];
                objects[i] = null;
                numItemsRemoved++;
                i = Math.max(i - 2, 0);
            }
            if (objects[i] != null && objects[i + 1] != null && num(objects[i]) && num(objects[i + 1])) {
                objects[i + 1] = (String) objects[i] + (String) objects[i + 1];
                objects[i] = null;
                numItemsRemoved++;
                i = Math.max(i - 2, 0);
            }
        }
        for (int i = 0; i < objects.length - 1; i++) {
            if (objects[i] == null && objects[i + 1] != null) {
                objects[i] = objects[i + 1];
                objects[i + 1] = null;
                i -= 2;
                if (i == -2) {
                    i++;
                }
            }
        }
        Object[] N = new Object[objects.length - numItemsRemoved];
        System.arraycopy(objects, 0, N, 0, N.length);
        return new ArrayList<>(Arrays.asList(N));
        //Grouping together sequences of letters and numbers. e.g. 4,5,+,j,r,*,x becomes 45,+,jr,*,x
        //Note: x does not group it says by itself
    }
    private static Expression Do(Object[] o, int i) {//Evaluate the operator at i in o,
        //replace a*b with the result, then evaluate that recursively
        Object[] N = new Object[3];
        System.arraycopy(o, i - 1, N, 0, 3);
        Object[] result = new Object[o.length - 2];
        System.arraycopy(o, 0, result, 0, i - 1);
        System.arraycopy(o, i + 2, result, i, o.length - (i + 2));
        result[i - 1] = parse(N);
        return parse(result);
    }
    private static final HashMap<String, Object> constants;
    static{
        constants = new HashMap<>();
        constants.put("true", true);
        constants.put("false", false);
        constants.put("55", new Object[] {1, 2, 3, 5, 6, 67, 7});
    }
    public static Expression parse(Object o) {
        if (o instanceof Object[]) {
            Object[] O = (Object[]) o;
            return parse(O);
        }
        if (o instanceof Expression) {
            return (Expression) o;
        }
        String s = (String) o;
        Object constant = constants.get(s);
        if (constant != null) {
            System.out.println("Replacing '" + s + "' with predefined constant " + constant);
            return new ExpressionConstant(constant);
        }
        try {
            int r = Integer.parseInt(s);//Try int first, because all ints are also valid doubles, and if there isn't a . then we want it to assume it's an int
            return new ExpressionConstant(r);//If it can be parsed as an int without throwing an exception, it's an int!
        } catch (NumberFormatException e) {
            try {
                double r = Double.parseDouble(s);
                return new ExpressionConstant(r);//If it can be parsed as a double without throwing an exception, it's a double!
            } catch (NumberFormatException E) {
                return new ExpressionVariable(s);
            }
        }
    }
    public static Expression parse(Object[] o) {//I TOTALLY didn't copy this from my derivative project
        System.out.print("Parsing expression ");
        for (Object k : o) {
            System.out.print(k + "       ");
        }
        System.out.println();
        if (o.length == 1) {
            return parse(o[0]);
        }
        if (o.length > 2 && o[1] instanceof String && ((String) o[1]).equals("=")) {//must be setting a variable if second item is "="
            Object[] Cool = new Object[o.length - 2];
            for (int i = 0; i < Cool.length; i++) {
                Cool[i] = o[i + 2];
            }
            Expression set = parse(Cool);
            if (o[0] instanceof String) {
                if (constants.get((String) o[0]) != null) {
                    throw new UnsupportedOperationException("Unable to set constant " + o[0] + " to " + set);
                }
                o[0] = new ExpressionVariable((String) o[0]);
            }
            Settable varname = (Settable) o[0];
            return new ExpressionSetVariable(varname, set);
        }
        //System.out.println(o.length);
        //Parenthesis
        for (int i = 0; i < o.length; i++) {
            if (o[i].equals("(")) {
                int n = 1;
                int j;
                for (j = i + 1; j < o.length && n > 0; j++) {
                    if (o[j].equals("(")) {
                        n++;
                    }
                    if (o[j].equals(")")) {
                        n--;
                    }
                }
                Object[] parenContents = new Object[j - i - 2];
                for (int m = 0; m < j - 1 - (i + 1); m++) {
                    parenContents[m] = o[m + i + 1];
                }
                Object[] leftover = new Object[o.length - (parenContents.length + 1)];
                System.arraycopy(o, 0, leftover, 0, i);
                System.arraycopy(o, j, leftover, i + 1, o.length - j);
                leftover[i] = parse(parenContents);
                return parse(leftover);
            }
        }
        if ((o[0] instanceof String) && ((String) o[0]).equals("[")) {//Defining an array
            int n = 1;
            int j;
            ArrayList<ArrayList<Object>> contents = new ArrayList<>();
            contents.add(new ArrayList<>());
            for (j = 1; j < o.length && n > 0; j++) {
                if (o[j].equals("[")) {
                    n++;
                }
                if (o[j].equals("]")) {
                    n--;
                }
                if ((o[j].equals(",") && n == 1) || n == 0) {
                    int k = 2;//Keep this here for Zach
                    if (n != 0) {
                        contents.add(new ArrayList<>());
                    }
                } else {
                    contents.get(contents.size() - 1).add(o[j]);
                }
            }
            Expression[] result = new Expression[contents.size()];
            for (int i = 0; i < result.length; i++) {
                ArrayList<Object> cont = contents.get(i);
                Object[] cn = cont.toArray();
                result[i] = parse(cn);
            }
            Object[] leftover = new Object[o.length - j + 1];
            for (int i = j; i < o.length; i++) {
                leftover[i - j + 1] = o[i];
            }
            leftover[0] = new ExpressionArrayDefinition(result);
            return parse(leftover);
        }
        if (o.length == 3) {//Assume that the middle one is an operator
            Expression First = parse(new Object[] {(Object) (new Object[] {o[0]})});
            String s = (String) o[1];
            Expression Last = parse(new Object[] {(Object) (new Object[] {o[2]})});
            return new ExpressionOperator(First, s, Last);
        }
        //Functions
        for (int i = 0; i < o.length - 1; i++) {
            if (o[i] instanceof String) {
                String s = (String) o[i];
                if (let(s) && o[i + 1] instanceof Expression) {
                    Object[] Cool = new Object[o.length - 1];
                    System.arraycopy(o, 0, Cool, 0, i);
                    System.arraycopy(o, i + 1, Cool, i, o.length - (i + 1));
                    Expression f = parse(new Object[] {o[i + 1]});
                    ArrayList<Expression> args = new ArrayList<>();
                    args.add(f);
                    Cool[i] = new ExpressionBeginChase(s, args);
                    return parse(Cool);
                }
            }
        }
        for (int i = 1; i < o.length - 1; i++) {
            if (o[i] instanceof String && o[i].equals("[")) {
                Expression varname = parse(new Object[] {o[i - 1]});
                int n = 1;
                int j;
                for (j = i + 1; j < o.length && n > 0; j++) {
                    if (o[j].equals("[")) {
                        n++;
                    }
                    if (o[j].equals("]")) {
                        n--;
                    }
                }
                Object[] parenContents = new Object[j - i - 2];
                for (int m = 0; m < j - 1 - (i + 1); m++) {
                    parenContents[m] = o[m + i + 1];
                }
                Object[] leftover = new Object[o.length - (parenContents.length + 2)];
                for (int k = 0; k < i - 1; k++) {
                    leftover[k] = o[k];
                }
                for (int k = j; k < o.length; k++) {
                    leftover[k - j + i] = o[k];
                }
                leftover[i - 1] = new ExpressionArray(varname, parse(parenContents));
                return parse(leftover);
            }
        }
        //Operators, using order of operations. Between * and /, 5*6/7 would be (5*6)/7, going left to right
        for (List<String> ops : orderOfOperations) {
            for (int i = 0; i < o.length; i++) {
                if (o[i] instanceof String && ops.contains((String) o[i])) {
                    return Do(o, i);
                }
            }
        }
        System.out.print("ERROR WHILE PARSING");
        for (Object k : o) {
            System.out.print(k);
        }
        throw new RuntimeException("ERROR WHILE PARSING");
    }
    @Override
    public final byte getCommandID() {
        return 0;
    }
    @Override
    protected final void doWrite(DataOutputStream out) throws IOException {
        writeExpression(out);
    }
    protected final void writeExpression(DataOutputStream out) throws IOException {
        System.out.println("Writing expression with id " + getExpressionID() + ", " + this);
        out.writeByte(getExpressionID());
        doWriteExpression(out);
    }
    protected abstract void doWriteExpression(DataOutputStream out) throws IOException;
    public abstract byte getExpressionID();
    public static Expression readExpression(DataInputStream in) throws IOException {
        byte expressionID = in.readByte();
        System.out.println("Reading expression ID" + expressionID);
        Expression result = readExpressionWithID(in, expressionID);
        if (result == null) {
            return null;//Or throw exception, haven't decided yet
        }
        if (result.getExpressionID() != expressionID) {
            throw new IOException("Failed to read properly");
        }
        System.out.println("Parsed " + result);
        return result;
    }
    private static Expression readExpressionWithID(DataInputStream in, byte expressionID) throws IOException {
        switch (expressionID) {
            case 1:
                return new Chase(in);
            case 2:
                return new ExpressionBeginChase(in);
            case 3:
                return new ExpressionConstant(in);
            case 4:
                return new ExpressionVariable(in);
            case 5:
                return new ExpressionOperator(in);
            case 6:
                return new ExpressionSetVariable(in);
            case 7:
                return new ExpressionArray(in);
            case 8:
                return new ExpressionArrayDefinition(in);
            default:
                return null;
        }
    }
}
