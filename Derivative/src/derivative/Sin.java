/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package derivative;

/**
 *
 * @author leijurv
 */
public class Sin extends Function{
    Function of;
    public Sin(Function f){
        of=f;
    }

    @Override
    public Function derivitive() {
        return new Multiply(new Cos(of),of.derivitive());
    }

    @Override
    public String toString() {
        return "Sin["+of+"]";
    }

    @Override
    public Function simplify() {
        of=of.simplify();
        return this;
    }

    @Override
    public boolean equal(Function f) {
        if (f instanceof Sin){
            return ((Sin)f).of.equal(of);
        }
        return false;
    }

    @Override
    public double eval(double d) {
        return Math.sin(of.eval(d));
    }
}
