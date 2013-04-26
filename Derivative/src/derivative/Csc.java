/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package derivative;

/**
 *
 * @author leijurv
 */
public class Csc extends Function{
Function of;
static boolean simplifyToSin=false;
public Csc(Function Of){
    of=Of;
}
    @Override
    public Function derivitive() {
        return new Subtract(new Constant(0),new Multiply(new Multiply(new Cot(of),new Csc(of)),of.derivitive()));
    }

    @Override
    public String toString() {
        return "Csc["+of+"]";
    }

    @Override
    public Function simplify() {
        if (simplifyToSin){
            return new Divide(new Constant(1),new Sin(of)).simplify();
        }
        return new Csc(of.simplify());
    }

    @Override
    public boolean equal(Function f) {
        if (f instanceof Csc){
            Csc c=(Csc)f;
            if (c.of.equals(of)){
                return true;
            }
        }
        return false;
    }

    @Override
    public double eval(double d) {
        return 1/Math.sin(of.eval(d));
    }
    
}
