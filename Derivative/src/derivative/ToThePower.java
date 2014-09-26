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
public class ToThePower extends Function{
    Function base;
    Function pow;
    public ToThePower(Function Base, Function Pow){
        base=Base;
        pow=Pow;
    }
    public Function derivitive(){
        System.out.println("a");
        return multiply(new ToThePower(base,new Subtract(pow,new Constant(1))),new Add(multiply(pow,base.derivitive()),new MultiplyDivide(new Function[]{base,new Ln(base),pow.derivitive()},new Function[]{})));
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
            return (new MultiplyDivide(new Function[]{new Constant(1)},new Function[]{new ToThePower(base,new Constant(-((Constant)pow).val))})).simplify();
        }
        }
        if (base instanceof X){
            return this;
        }
        
        if (base instanceof ToThePower){
            return new ToThePower( ((ToThePower)base).base,multiply(((ToThePower)base).pow,pow)).simplify();
        }
        if (base instanceof MultiplyDivide){
            MultiplyDivide M=(MultiplyDivide)base;
            
            //return new MultiplyDivide(new ToThePower(M.a,pow),new ToThePower(M.b,pow)).simplify();
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
    public Function clone(){
        return new ToThePower(base.clone(),pow.clone());
    }
}
