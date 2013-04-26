/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package derivative;

/**
 *
 * @author leijurv
 */
public class Ln extends Function{
    Function of;
    public Ln(Function OF){
        of=OF;
    }
    @Override
    public Function derivitive() {
        return new Divide(of.derivitive(),of);
    }

    @Override
    public String toString() {
        return "Log["+of+"]";
    }

    @Override
    public Function simplify() {
        return new Ln(of.simplify());
    }

    @Override
    public boolean equal(Function f) {
        if (f instanceof Ln){
            Ln l=(Ln)f;
            return of.equals(l.of);
        }
        return false;
    }

    @Override
    public double eval(double d) {
        return Math.log(of.eval(d));
    }
    
}
