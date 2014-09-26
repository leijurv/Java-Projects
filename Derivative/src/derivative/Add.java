/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package derivative;

import static derivative.MultiplyDivide.multiply;
import static derivative.Subtract.expand;

/**
 *
 * @author leijurv
 */
public class Add extends Function{
    Function a;
    Function b;
    public Add(Function A, Function B){
        a=A;
        b=B;
    }
    public Function derivitive(){
        return new Add(a.derivitive(),b.derivitive());
    }
    public String toString(){
        return ""+a+"+"+b+"";
    }
    public Function simplify(){
        a=a.simplify();
        b=b.simplify();
        if (a instanceof Constant){
            int aval=((Constant)a).val;
            if (aval==0){
                return b;
            }
            if (b instanceof Constant){
                int bval=((Constant)b).val;
                if (bval==0){
                    return a;
                }
                return (new Constant(aval+bval)).simplify();
            }
        }
        if (b instanceof Constant){
            int bval=((Constant)b).val;
            if (bval==0){
                return a;
            }
            return new Add(b,a).simplify();//Always reorder f(x)+constant to constant+f(x)
        }
        if (a instanceof Subtract){
            Subtract A=(Subtract)a;
            if (A.a instanceof Constant){
                if (((Constant)A.a).val==0){
                    return new Subtract(b,A.b).simplify();
                }
            }
        }
        if (b instanceof Subtract){
            Subtract B=(Subtract)b;
            return (new Subtract(new Add(a,B.a),B.b)).simplify();
        }
        if (a.equal(b)){
            return (multiply(new Constant(2),a)).simplify();
        }
        if (b instanceof Add){
            Add B=(Add)b;
            if (B.b.equal(a)){
                return new Add(B.a,new Add(a,B.b)).simplify();
            }
            if (B.a.equal(a)){
                return new Add(B.b,new Add(B.a,a)).simplify();
            }
        }
        if (a instanceof Add){
            Add A=(Add)a;
            if (A.a.equal(b)){
                return new Add(A.b,new Add(A.a,b)).simplify();
            }
            if (A.b.equal(b)){
                return new Add(A.a,new Add(A.b,b)).simplify();
            }
        }
        if (a instanceof MultiplyDivide){
            MultiplyDivide A=(MultiplyDivide)a;
            if (!A.top.isEmpty() && A.top.get(0).equals(new Constant(-1))){
                A.top.remove(0);
                return new Subtract(b,A).simplify();
            }
        }
        if (b instanceof MultiplyDivide){
            MultiplyDivide A=(MultiplyDivide)b;
            if (!A.top.isEmpty() && A.top.get(0).equals(new Constant(-1))){
                A.top.remove(0);
                return new Subtract(a,A).simplify();
            }
        }
        if (a instanceof MultiplyDivide && b instanceof MultiplyDivide && !expand){
                    MultiplyDivide A=(MultiplyDivide)a;
                    MultiplyDivide B=(MultiplyDivide)b;
                    //if (A.bottom.isEmpty() && B.bottom.isEmpty()){
                        for (int i=0; i<A.top.size(); i++){
                            if (B.top.contains(A.top.get(i))){
                                B.top.remove(A.top.get(i));
                                Function f=A.top.remove(i);
                                return new MultiplyDivide(new Function[]{f,this},new Function[]{}).simplify();
                            }
                        }
                    //}
                        
                }
        /*
        if (a instanceof MultiplyDivide){
            if (b instanceof MultiplyDivide){
                MultiplyDivide A=(MultiplyDivide)a;
                MultiplyDivide B=(MultiplyDivide)b;
                if (A.b.equal(B.b)){
                if (A.a instanceof Constant){
                    if (B.a instanceof Constant){
                        return new MultiplyDivide(new Add(A.a,B.a),A.b).simplify();
                        //I know it's bad, but I add things like this for special cases where this
                        //would make it simplify a certain function perfectly
                    }
                }
                }
            }
        }
        
        
        if (a instanceof Divide){
            if (b instanceof Divide){
                if (((Divide)a).bottom.equal(((Divide)b).bottom)){
                    return new Divide(new Add(((Divide)b).top,((Divide)a).top).simplify(),((Divide)a).bottom);
                }
                
            }
        }
        if (a instanceof Divide){
            if (((Divide)a).bottom instanceof X){
                if (b instanceof MultiplyDivide){
                    if (((MultiplyDivide)b).divByX()){
                        return new Divide(new Add(((Divide)a).top,new MultiplyDivide(((MultiplyDivide)b),new X())),new X());
                    }
                }
            }
        }*/
        return this;
    }
    public boolean equal(Function f){
        if (f instanceof Add){
            Add A=(Add)f;
            return (A.a.equal(a) && A.b.equal(b)) || (A.a.equal(b) && A.b.equal(a));
        }
        return false;
    }
    public double eval(double d){
        return a.eval(d)+b.eval(d);
    }
    public Function clone(){
        return new Add(a.clone(),b.clone());
    }
}
