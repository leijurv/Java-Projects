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
public class Subtract extends Function{
    Function a;
    Function b;
    static boolean expand=false;
    public Subtract(Function A, Function B){
        a=A;
        b=B;
    }
    public Function derivitive(){
        return new Subtract(a.derivitive(),b.derivitive());
    }
    public String toString(){
        if (a instanceof Constant){
            return a+"-("+b+")";
        }
        if (b instanceof Constant){
            return "("+a+")-"+b;
        }
        return "("+a+")-("+b+")";
    }
    public Function simplify(){
        System.out.println("d"+this);
        a=a.simplify();
        b=b.simplify();
                System.out.println("c"+this);
                //System.out.println();
                if (a.equal(b)){
                    return new Constant(0);
                }
                if (a instanceof MultiplyDivide && b instanceof MultiplyDivide && !expand){
                    MultiplyDivide A=(MultiplyDivide)a;
                    MultiplyDivide B=(MultiplyDivide)b;
                    
                    if (A.bottom.equals(B.bottom)){
                        for (int i=0; i<A.top.size(); i++){
                            if (B.top.contains(A.top.get(i))){
                                B.top.remove(A.top.get(i));
                                Function f=A.top.remove(i);
                                return new MultiplyDivide(new Function[]{f,this},new Function[]{}).simplify();
                            }
                        }
                    }
                        
                }
                if (a instanceof MultiplyDivide){
                    MultiplyDivide A=(MultiplyDivide)a;
                    if (A.top.get(0).equals(new Constant(-1))){
                        A.top.remove(0);
                        return multiply(new Constant(-1),new Add(A,b));
                    }
                }
        if (a instanceof Constant){
            int aval=((Constant)a).val;
            if (b instanceof Constant){
                int bval=((Constant)b).val;
                
                if (bval==0){
                    return a;
                }
                //if (aval!=0){
                    return new Constant(aval-bval).simplify();
                //}
            }
        }
        if (b instanceof Constant){
            int bval=((Constant)b).val;
            if (bval==0){
                return a;
            }
        }
        if (a instanceof Subtract){
            Subtract A=(Subtract)a;
            if (A.a.equal(b)){
                return new Subtract(new Constant(0),A.b).simplify();
            }
            if (b instanceof Constant && A.b instanceof Constant){
                return new Subtract(A.a,new Add(b,A.b)).simplify();
            }
            //return (new Subtract(((Subtract)a).a,new Add(((Subtract)a).b,b))).simplify();
        }
        if (b instanceof Subtract){
            return (new Subtract(new Add(a,((Subtract)b).b),((Subtract)b).a)).simplify();
        }
        if (a instanceof Add){
            if (((Add)a).a instanceof Constant){
                if (b instanceof Constant){
                    return new Add(new Subtract(((Add)a).a,b),((Add)a).b).simplify();
                }
            }
        }
        if (a instanceof Add){
            if (((Add)a).b.equal(b)){
                return ((Add)a).a;
            }
        }
        if (b instanceof Add){
            Add B=(Add)b;
            return new Subtract(new Subtract(a,B.a),B.b).simplify();
        }
        return this;
    }
    public boolean equal(Function f){
        if (f instanceof Subtract){
            Subtract d=(Subtract)f;
            boolean A=d.a.equal(a);
            boolean B=d.b.equal(b);
            return A && B;
        }
        return false;
    }
    public double eval(double d){
        return a.eval(d)-b.eval(d);
    }
    public Function clone(){
        return new Subtract(a.clone(),b.clone());
    }
}