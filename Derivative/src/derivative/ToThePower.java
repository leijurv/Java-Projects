/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package derivative;

/**
 *
 * @author leijurv
 */
public class ToThePower extends Function{
    Function base;
    Constant pow;
    static final private boolean Expand=false;
    public ToThePower(Function Base, Constant Pow){
        base=Base;
        pow=Pow;
    }
    public Function derivitive(){
        if (pow.val==0){
            return new Constant(0);
        }
        return new Multiply(new Multiply(pow,new ToThePower(base,new Constant(pow.val-1))),base.derivitive());
    }
    public String toString(){
        if (base instanceof X){
            return base+"^"+pow;
        }
        return "("+base+")^"+pow;
    }
    public Function simplify(){
        base=base.simplify();
        if (pow.val==0){
            return new Constant(1);
        }
        if (pow.val==1){
            return base.simplify();
        }
        if (pow.val<0){
            return (new Divide(new Constant(1),new ToThePower(base,new Constant(-pow.val)))).simplify();
        }
        if (base instanceof X){
            return this;
        }
        if (base instanceof ToThePower){
            return new ToThePower(((ToThePower)base).base,new Constant(((ToThePower)base).pow.val*pow.val)).simplify();
        }
        if (base instanceof Multiply){
            Multiply M=(Multiply)base;
            return new Multiply(new ToThePower(M.a,pow),new ToThePower(M.b,pow)).simplify();
        }
        if (base instanceof Constant){
            return new Constant((int)Math.pow(((Constant)base).val,pow.val));
        }
        if (Expand){
        //return (new Multiply(base,new ToThePower(base,new Constant(pow.val-1)))).simplify();
        }
        return this;
    }
    public boolean equal(Function f){
        if (f instanceof ToThePower){
            ToThePower q=(ToThePower)f;
            return q.base.equal(base) && q.pow.equal(pow);
        }
        return false;
    }
    public double eval(double d){
        return Math.pow(base.eval(d),pow.eval(d));
    }
}
