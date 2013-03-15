/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package derivative;

/**
 *
 * @author leijurv
 */
public class Divide extends Function{
    Function top;
    Function bottom;
    public Divide(Function Top, Function Bottom){
        top=Top;
        bottom=Bottom;
    }
    public Function derivitive(){
        return new Divide(new Subtract(new Multiply(bottom,top.derivitive()),new Multiply(bottom.derivitive(),top)),new ToThePower(bottom,new Constant(2)));
    }
    public String toString(){
        return "("+top+")/("+bottom+")";
    }
    public Function simplify(){
        top=top.simplify();
        bottom=bottom.simplify();
        if (top instanceof Constant){
            int tval=((Constant)top).val;
            if (tval==0){
                return new Constant(0);
            }
        }
        if (bottom instanceof Constant){
            int bval=((Constant)bottom).val;
            if (bval==1){
                return top;
            }
        }
        return this;
    }
    public boolean equal(Function f){
        if (f instanceof Divide){
            Divide d=(Divide)f;
            return d.top.equal(top) && d.bottom.equal(bottom);
        }
        return false;
    }
    public double eval(double d){
        return top.eval(d)/bottom.eval(d);
    }
}
