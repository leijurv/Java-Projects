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
    @Override
    public boolean execute(Context c) {
        evaluate(c);
        return false;
    }
    public abstract Object evaluate(Context c);
    private static final String[] let = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","y","z"};
    private static boolean let(Object o) {//Does o consist of letters?
        if (o instanceof String) {
            String r = (String) o;
            if (r.length() > 1) {
                for (int i = 0; i < r.length(); i++) {
                    if (!let(r.substring(i,i + 1))) {
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
            return ((((String) o).replace(">","").length() == 0) || (((String) o).replace("<","").length() == 0) || ((String) o).replace("!","").length() == 0) || (((String) o).replace("=","").length() == 0) || (((String) o).replace("|","").length() == 0) || (((String) o).replace("&","").length() == 0);
        }
        return false;
    }
    public static ArrayList<Object> lex(String s) {
        Object[] objects = new Object[s.length()];
        for (int i = 0; i < objects.length; i++) {
            objects[i] = s.substring(i,i + 1);
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
                i = Math.max(i - 2,0);
            }
            if (objects[i] != null && objects[i + 1] != null && let(objects[i]) && let(objects[i + 1])) {
                objects[i + 1] = (String) objects[i] + (String) objects[i + 1];
                objects[i] = null;
                numItemsRemoved++;
                i = Math.max(i - 2,0);
            }
            if (objects[i] != null && objects[i + 1] != null && num(objects[i]) && num(objects[i + 1])) {
                objects[i + 1] = (String) objects[i] + (String) objects[i + 1];
                objects[i] = null;
                numItemsRemoved++;
                i = Math.max(i - 2,0);
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
        System.arraycopy(objects,0,N,0,N.length);
        return new ArrayList<>(Arrays.asList(N));
        //Grouping together sequences of letters and numbers. e.g. 4,5,+,j,r,*,x becomes 45,+,jr,*,x
        //Note: x does not group it says by itself
    }
    private static Expression Do(Object[] o,int i) {//Evaluate the operator at i in o,
        //replace a*b with the result, then evaluate that recursively
        Object[] N = new Object[3];
        System.arraycopy(o,i - 1,N,0,3);
        Object[] result = new Object[o.length - 2];
        System.arraycopy(o,0,result,0,i - 1);
        System.arraycopy(o,i + 2,result,i,o.length - (i + 2));
        result[i - 1] = parse(N);
        return parse(result);
    }
    private static final HashMap<String,Object> constants;
    static{
        constants = new HashMap<>();
        constants.put("true",true);
        constants.put("false",false);
        constants.put("55","DANK SWAMP KUSH");
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
                return new ExpressionGetVariable(s);
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
            String varname = (String) o[0];
            Expression set = parse(Cool);
            if (constants.get(varname) != null) {
                throw new UnsupportedOperationException("Unable to set constant " + varname + " to " + set);
            }
            return new ExpressionSetVariable(varname,set);
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
                System.arraycopy(o,0,leftover,0,i);
                System.arraycopy(o,j,leftover,i + 1,o.length - j);
                leftover[i] = parse(parenContents);
                return parse(leftover);
            }
        }
        if (o.length == 3) {//Assume that the middle one is an operator
            Expression First = parse(new Object[] {(Object) (new Object[] {o[0]})});
            String s = (String) o[1];
            Expression Last = parse(new Object[] {(Object) (new Object[] {o[2]})});
            return new ExpressionOperator(First,s,Last);
        }
        //Functions
        for (int i = 0; i < o.length - 1; i++) {
            if (o[i] instanceof String) {
                String s = (String) o[i];
                if (let(s) && o[i + 1] instanceof Expression) {
                    Object[] Cool = new Object[o.length - 1];
                    System.arraycopy(o,0,Cool,0,i);
                    System.arraycopy(o,i + 1,Cool,i,o.length - (i + 1));
                    Expression f = parse(new Object[] {o[i + 1]});
                    ArrayList<Expression> args = new ArrayList<>();
                    args.add(f);
                    Cool[i] = new ExpressionBeginChase(s,args);
                    return parse(Cool);
                }
            }
        }
        //Operators, using order of operations. Between * and /, 5*6/7 would be (5*6)/7, going left to right
        ArrayList<List<String>> orderOfOperations = new ArrayList<>();
        orderOfOperations.add(Arrays.asList(new String[] {"%"}));
        orderOfOperations.add(Arrays.asList(new String[] {"^"}));
        orderOfOperations.add(Arrays.asList(new String[] {"*","/"}));
        orderOfOperations.add(Arrays.asList(new String[] {"+","-"}));
        orderOfOperations.add(Arrays.asList(new String[] {">","<","==","!=","<=",">="}));
        orderOfOperations.add(Arrays.asList(new String[] {"||","&&"}));
        for (List<String> ops : orderOfOperations) {
            for (int i = 0; i < o.length; i++) {
                if (o[i] instanceof String && ops.contains((String) o[i])) {
                    return Do(o,i);
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
        Expression result = readExpressionWithID(in,expressionID);
        if (result == null) {
            return null;//Or throw exception, haven't decided yet
        }
        if (result.getExpressionID() != expressionID) {
            throw new IOException("Failed to read properly");
        }
        System.out.println("Parsed " + result);
        return result;
    }
    private static Expression readExpressionWithID(DataInputStream in,byte expressionID) throws IOException {
        switch (expressionID) {
            case 1:
                return new Chase(in);
            case 2:
                return new ExpressionBeginChase(in);
            case 3:
                return new ExpressionConstant(in);
            case 4:
                return new ExpressionGetVariable(in);
            case 5:
                return new ExpressionOperator(in);
            case 6:
                return new ExpressionSetVariable(in);
            default:
                return null;
        }
    }
}
