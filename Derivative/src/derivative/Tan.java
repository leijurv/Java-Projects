/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package derivative;

import static derivative.MultiplyDivide.multiply;

/**
 *
 * @author leijurv
 */
public class Tan extends Function{
    Function of;
    static boolean simplifyToSinCos=false;
    public Tan(Function f){
        of=f;
    }

    @Override
    public Function derivitive() {
        return multiply(new ToThePower(new Sec(of),new Constant(2)),of.derivitive());
    }

    @Override
    public String toString() {
        return "Tan["+of+"]";
    }

    @Override
    public Function simplify() {
        of=of.simplify();
        if (simplifyToSinCos){
            return new MultiplyDivide(new Function[]{new Sin(of)},new Function[]{new Cos(of)});
        }
        return this;
    }

    @Override
    public boolean equal(Function f) {
        if (f instanceof Tan){
            return ((Tan)f).of.equal(of);
        }
        return false;
    }

    @Override
    public double eval(double d) {
        return Math.tan(of.eval(d));
    }
    public Function clone(){
        return new Tan(of.clone());
    }
}
