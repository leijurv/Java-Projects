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
        if (top.equal(bottom)){
            return new Constant(1);
        }
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
        if (bottom instanceof Cos){
            return new Multiply(top,new Sec(((Cos)bottom).of)).simplify();
        }
        if (bottom instanceof Sin){
            return new Multiply(top,new Csc(((Sin)bottom).of)).simplify();
        }
        if (bottom instanceof ToThePower){
            ToThePower tt=(ToThePower)bottom;
            if (tt.base instanceof Cos){
                return new Multiply(top,new ToThePower(new Sec(((Cos)tt.base).of),tt.pow)).simplify();
            }
        }
        if (exp){
            if (top instanceof Add){
                Add a=(Add)top;
                return new Add(new Divide(a.a,bottom),new Divide(a.b,bottom)).simplify();
            }
            if (top instanceof Subtract){
                Subtract a=(Subtract)top;
                return new Subtract(new Divide(a.a,bottom),new Divide(a.b,bottom)).simplify();
            }
        }
        if (bottom instanceof ToThePower){
            ToThePower b=(ToThePower)bottom;
            if (top instanceof Multiply && (((Multiply)top).b instanceof ToThePower || ((Multiply)top).a instanceof ToThePower)){
                Multiply t=(Multiply)top;
                if (t.a instanceof ToThePower){
                    ToThePower tb=(ToThePower)(t.a);
                    if (tb.base.equal(b.base)){
                        return new Multiply(t.b,new ToThePower(tb.base,new Subtract(tb.pow,b.pow))).simplify();
                    }
                }
                if (t.b instanceof ToThePower){
                    ToThePower tb=(ToThePower)(t.b);
                    if (tb.base.equal(b.base)){
                        return new Multiply(t.a,new ToThePower(tb.base,new Subtract(tb.pow,b.pow))).simplify();
                    }
                }
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
