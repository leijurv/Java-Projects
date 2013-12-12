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
    Function pow;
    public ToThePower(Function Base, Function Pow){
        base=Base;
        pow=Pow;
    }
    public Function derivitive(){
        
        return new Multiply(new ToThePower(base,new Subtract(pow,new Constant(1))),new Add(new Multiply(pow,base.derivitive()),new Multiply(base,new Multiply(new Ln(base),pow.derivitive()))));
    }
    public String toString(){
        if (base instanceof X || base instanceof Constant){
            if (pow instanceof X || pow instanceof Constant){
                return base+"^"+pow;
            }
            return base+"^("+pow+")";
        }
        if (pow instanceof X || pow instanceof Constant){
            return "("+base+")^"+pow;
        }
        return "("+base+")^("+pow+")";
    }
    public Function simplify(){
        base=base.simplify();
        pow=pow.simplify();
        if (pow instanceof Constant){
        if (((Constant)pow).val==0){
            return new Constant(1);
        }
        if (((Constant)pow).val==1){
            return base.simplify();
        }
        if (((Constant)pow).val<0){
            return (new Divide(new Constant(1),new ToThePower(base,new Constant(-((Constant)pow).val)))).simplify();
        }
        }
        if (base instanceof X){
            return this;
        }
        
        if (base instanceof ToThePower){
            return new ToThePower(((ToThePower)base).base,new Multiply((ToThePower)base,pow)).simplify();
        }
        if (base instanceof Multiply){
            Multiply M=(Multiply)base;
            return new Multiply(new ToThePower(M.a,pow),new ToThePower(M.b,pow)).simplify();
        }
        if (base instanceof Constant){
            if (pow instanceof Constant){
                return new Constant((int)Math.pow(((Constant)base).val,((Constant)pow).val));
            }
        }
        //if (Expand){
        //return (new Multiply(base,new ToThePower(base,new Constant(pow.val-1)))).simplify();
        //}
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
