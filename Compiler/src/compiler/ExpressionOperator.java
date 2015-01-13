/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

/**
 *
 * @author leijurv
 */
public class ExpressionOperator extends Expression{
    private final char operator;
    private final Expression before;
    private final Expression after;
    public ExpressionOperator(Expression Before,String Operator,Expression After){
        this(Before,Operator.equals("<=") ? '@' : (Operator.equals(">=") ? '$' : (Operator.charAt(0))),After);
    }
    public ExpressionOperator(Expression Before,char Operator,Expression After){
        before=Before;
        operator=Operator;
        after=After;
    }
    @Override
    public Object evaluate(Context c){
        Object bef=before.evaluate(c);
        Object aft=after.evaluate(c);
        switch (operator){
            case '+':
                return (Integer) bef+(Integer) aft;
            case '-':
                return (Integer) bef-(Integer) aft;
            case '*':
                return (Integer) bef*(Integer) aft;
            case '/':
                return (Integer) bef/(Integer) aft;
            case '%':
                return (Integer) bef%(Integer) aft;
            case '>':
                return (Integer) bef>(Integer) aft;
            case '<':
                return (Integer) bef<(Integer) aft;
            case '=':
                return aft.equals(bef);
            case '&':
                return (Boolean) aft&&(Boolean) bef;
            case '|':
                return (Boolean) aft||(Boolean) bef;
            case '!':
                return (Integer) bef!=(Integer) aft;
            case '@'://I know its terrible
                return (Integer) bef<=(Integer) aft;
            case '$':
                return (Integer) bef>=(Integer) aft;
        }
        throw new RuntimeException("Unable to perform operator '"+operator+"' on objects '"+bef+"' and '"+aft+"'");
    }
    @Override
    public String toString(){
        return "("+before+operator+after+")";
    }
}
