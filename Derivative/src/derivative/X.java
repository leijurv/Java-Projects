/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package derivative;

/**
 *
 * @author leijurv
 */
public class X extends Function{
    public Function derivitive(){
        return new Constant(1);
    }
    public String toString(){
        return "x";
    }
    public Function simplify(){
        return this;
    }
    public boolean equal(Function f){
        if (f instanceof X){
            return true;
        }
        return false;
    }
    public double eval(double d){
        return d;
    }
}
