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
public class ExpressionSetVariable extends Expression{
    private final String variablename;
    private final Expression value;
    public ExpressionSetVariable(String varname,Expression val){
        variablename=varname;
        value=val;
    }
    @Override
    public Object evaluate(Context c){
        Object o=value.evaluate(c);
        c.set(variablename,o);
        return o;
    }
    @Override
    public String toString(){
        return "~set~ "+variablename+"="+value;
    }
}
