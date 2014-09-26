/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package derivative;

/**
 *
 * @author leijurv
 */
public class Sec extends Function{
    Function of;
    static boolean simplifyToCos=false;
    public Sec(Function f){
        of=f;
    }

    @Override
    public Function derivitive() {
        
        return new MultiplyDivide(new Function[]{new Sec(of),new Tan(of),of.derivitive()},new Function[]{});
    }

    @Override
    public String toString() {
        return "Sec["+of+"]";
    }

    @Override
    public Function simplify() {
        of=of.simplify();
        if (simplifyToCos){
            return new MultiplyDivide(new Function[]{new Constant(1)},new Function[]{new Cos(of)});
        }
        return this;
        //return new Divide(new Constant(1),new Cos(of));
    }

    @Override
    public boolean equal(Function f) {
        if (f instanceof Sec){
            return ((Sec)f).of.equal(of);
        }
        return false;
    }

    @Override
    public double eval(double d) {
        return 1/Math.cos(of.eval(d));
    }
    public Function clone(){
        return new Sec(of.clone());
    }
}
