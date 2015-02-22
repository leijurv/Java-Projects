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
    protected ExpressionOperator(DataInputStream in) throws IOException{
        operator=Operator.values()[in.readInt()];
        before=readExpression(in);
        after=readExpression(in);
    }
    @Override
    protected void doWriteExpression(DataOutputStream out) throws IOException{
        out.writeInt(operator.ordinal());
        before.writeExpression(out);
        after.writeExpression(out);
    }
    @Override
    public byte getExpressionID(){
        return 5;
    }
    private static enum Operator {
        ADD("+"), SUBTRACT("-"), MULTIPLY("*"), DIVIDE("/"), MOD("%"), GREATER(">"), LESSER("<"), EQUAL("=="), AND("&"), OR("||"), NOTEQUAL("!="), GREATEROREQUAL(">="), LESSEROREQUAL("<=");
        private final String opcode;
        private Operator(String opcode){
            this.opcode=opcode;
        }
        public Object run(Object a,Object b){
            switch (this){
                case ADD:
                    return (Integer) a+(Integer) b;
                case SUBTRACT:
                    return (Integer) a-(Integer) b;
                case MULTIPLY:
                    return (Integer) a*(Integer) b;
                case DIVIDE:
                    return (Integer) a/(Integer) b;
                case MOD:
                    return (Integer) a%(Integer) b;
                case GREATER:
                    return ((Comparable) a).compareTo((Comparable) b)==1;
                case LESSER:
                    return ((Comparable) a).compareTo((Comparable) b)==-1;
                case EQUAL:
                    return b.equals(a);
                case AND:
                    return (Boolean) b&&(Boolean) a;
                case OR:
                    return (Boolean) b||(Boolean) a;
                case NOTEQUAL:
                    return !a.equals(b);
                case GREATEROREQUAL:
                    return ((Comparable) a).compareTo((Comparable) b)!=-1;
                case LESSEROREQUAL:
                    return ((Comparable) a).compareTo((Comparable) b)!=1;
                default:
                    //This is null??
                    return null;
            }
        }
        public boolean matches(String opcode){
            return this.opcode.equals(opcode);
        }
        public static Operator get(String operation){
            for (Operator o : Operator.values()){
                if (o.matches(operation)){
                    return o;
                }
            }
            return null;
        }
    }
    public ExpressionOperator(Expression before,String operator,Expression after){
        this.operator=Operator.get(operator);
        this.before=before;
        this.after=after;
        if (this.operator==null){
            throw new RuntimeException("Unknown operator "+operator+" trying to be applied to "+before+" and "+after);
        }
    }
    @Override
    public Object evaluate(Context c){
        Object a=before.evaluate(c);
        Object b=after.evaluate(c);
        try{
            return operator.run(a,b);
        } catch (Exception e){
            throw new RuntimeException("Unable to perform operator '"+operator+"' on objects '"+a+"' and '"+b+"'");
        }
    }
    @Override
    public String toString(){
        return "("+before+operator+after+")";
    }
}
