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
public class ExpressionGetVariable extends Expression{
    private final String name;
    public ExpressionGetVariable(String varname){
        name=varname;
    }
    @Override
    public Object evaluate(Context c){
        return c.get(name);
    }
    @Override
    public String toString(){
        return "~var "+name+"~";
    }
}
