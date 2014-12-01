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
public class ExpressionVariable extends Expression {
    String name;
    public ExpressionVariable(String varname){
        name=varname;
    }
    @Override
    public Object evaluate(Context c){
        return c.get(name);
    }
    public String toString(){
        return "~var "+name+"~";
    }
}
