/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package derivative;

/**
 *
 * @author leijurv
 */
public class Cos extends Function{
    Function of;
    public Cos(Function f){
        of=f;
    }

    @Override
    public Function derivitive() {
        return new Subtract(new Constant(0),new Multiply(new Sin(of),of.derivitive()));
    }

    @Override
    public String toString() {
        return "Cos["+of+"]";
    }

    @Override
    public Function simplify() {
        of=of.simplify();
        return this;
    }

    @Override
    public boolean equal(Function f) {
        if (f instanceof Cos){
            return ((Cos)f).of.equal(of);
        }
        return false;
    }

    @Override
    public double eval(double d) {
        return Math.sin(of.eval(d));
    }
}
