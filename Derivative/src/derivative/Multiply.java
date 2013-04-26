/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package derivative;

/**
 *
 * @author leijurv
 */
public class Multiply extends Function{
    Function a;
    Function b;
    public Multiply(Function A, Function B){
        a=A;
        b=B;
    }
    public Function derivitive(){
        return new Add(new Multiply(a,b.derivitive()),new Multiply(b,a.derivitive()));
    }
    
    public String toString(){
        String result="";
        boolean needs=true;
        if (a instanceof Constant){
            needs=false;
        }
        if (a instanceof X){
            needs=false;
        }
        if (needs){
            result="("+a+")*";
        }else{
            result=a+"*";
        }
        needs=true;
        if (b instanceof Constant){
            needs=false;
        }
        if (b instanceof X){
            needs=false;
        }
        if (needs){
            return result+"("+b+")";
        }
        return result+b;
    }/*
    public String toString(){
        return "("+a+")*("+b+")";
    }*/
    public Function simplify(){
        a=a.simplify();
        b=b.simplify();
        if (a instanceof Add){
            return (new Add(new Multiply(((Add)a).a,b),new Multiply(((Add)a).b,b))).simplify();
        }
        if (b instanceof Add){
            return (new Add(new Multiply(((Add)b).a,a),new Multiply(((Add)b).b,a))).simplify();
        }
        if (a instanceof Subtract){
            return (new Subtract(new Multiply(((Subtract)a).a,b),new Multiply(((Subtract)a).b,b))).simplify();
        }
        if (b instanceof Subtract){
            return (new Subtract(new Multiply(((Subtract)b).a,a),new Multiply(((Subtract)b).b,a))).simplify();
        }
        if (a instanceof Constant){
            int aval=((Constant)a).val;
            if (aval==0){
                return new Constant(0);
            }
            if (aval==1){
                return b;
            }
            if (b instanceof Constant){
                return (new Constant(aval*((Constant)b).val)).simplify();
            }
            
        }
        if (b instanceof Constant){
            int aval=((Constant)b).val;
            if (aval==0){
                return new Constant(0);
            }
            if (aval==1){
                return a;
            }
            return (new Multiply(b,a)).simplify();
        }
        if (a.equal(b)){
            return new ToThePower(a,new Constant(2)).simplify();
        }
        
        if (a instanceof ToThePower){
        if (b instanceof Multiply){
            Function base=((ToThePower)a).base;
            if (((Multiply)b).b.equal(base)){
                return new Multiply(((Multiply)b).a,new ToThePower(base,new Add(new Constant(1),((ToThePower)a).pow))).simplify();
            }
            if (((Multiply)b).a.equal(base)){
                return new Multiply(((Multiply)b).b,new ToThePower(base,new Add(new Constant(1),((ToThePower)a).pow))).simplify();
            }
            /*
            if (((Multiply)b).b instanceof ToThePower){
                        return new Multiply(((Multiply)b).a,new Multiply(((Multiply)b).b,a)).simplify();
                }
            if (((Multiply)b).a instanceof ToThePower){
                        return new Multiply(((Multiply)b).b,new Multiply(((Multiply)b).a,a)).simplify();
                }*/
        }
        }
        if (a instanceof Multiply){
            if (b instanceof ToThePower){
                Function base=((ToThePower)b).base;
                if (((Multiply)a).a.equal(base)){
                    return new Multiply(((Multiply)a).b,new ToThePower(base,new Add(new Constant(1),((ToThePower)b).pow))).simplify();
            
                } 
                if (((Multiply)a).b.equal(base)){
                    return new Multiply(((Multiply)a).a,new ToThePower(base,new Add(new Constant(1),((ToThePower)b).pow))).simplify();
                }/*
                if (((Multiply)a).b instanceof ToThePower){
                        return new Multiply(((Multiply)a).a,new Multiply(((Multiply)a).b,b)).simplify();
                }
                if (((Multiply)a).a instanceof ToThePower){
                    return new Multiply(((Multiply)a).b,new Multiply(((Multiply)a).a,b)).simplify();
                }*/
            }
        }
        if (a instanceof ToThePower){
            if (b instanceof ToThePower){
                if (((ToThePower)a).base.equal(((ToThePower)b).base)){
                    return (new ToThePower(((ToThePower)a).base,new Add(((ToThePower)a).pow,((ToThePower)b).pow))).simplify();
                }
            }
        }
        if (b instanceof ToThePower){
            if (a.equal(((ToThePower)b).base)){
                return (new ToThePower(a,new Add(new Constant(1),((ToThePower)b).pow))).simplify();
            }
        }
        if (a instanceof ToThePower){
            if (b.equal(((ToThePower)a).base)){
                return (new ToThePower(b,new Add(new Constant(1),((ToThePower)a).pow))).simplify();
            }
        }
        if (a instanceof Multiply){
            if (b instanceof Multiply){
            Multiply A=(Multiply)a;
            Multiply B=(Multiply)b;
            if (A.a instanceof Constant){
                if (B.a instanceof Constant){
                    return (new Multiply(new Multiply(A.a,B.a),new Multiply(A.b,B.b))).simplify();
                }
            }
            if (A.b.equal(B.a)){
                return new Multiply(A.a,new Multiply(new ToThePower(A.b,new Constant(2)),B.b)).simplify();
            }
        }
        }
        if (a instanceof Constant){
            if (b instanceof Multiply){
                if (((Multiply)b).a instanceof Constant){
                    return (new Multiply(new Multiply(a,((Multiply)b).a),((Multiply)b).b)).simplify();
                }
            }
        }
        if (a instanceof Multiply){
            Multiply A=(Multiply)a;
            if (A.b instanceof ToThePower){
                ToThePower AB=(ToThePower)A.b;
                if (AB.base.equal(b)){
                    return new Multiply(A.a,new ToThePower(AB.base,new Add(AB.pow,new Constant(1))));
                }
            }
            if (A.a instanceof ToThePower){
                ToThePower AB=(ToThePower)A.a;
                if (AB.base.equal(b)){
                    return new Multiply(A.b,new ToThePower(AB.base,new Add(AB.pow,new Constant(1))));
                }
            }
        }
        if (a instanceof Multiply){
            Multiply A=(Multiply)a;
            if (A.b.equal(b)){
                return (new Multiply(A.a,new Multiply(b,A.b))).simplify();
            }
            if (A.a.equal(b)){
                return (new Multiply(A.b,new Multiply(b,A.a).simplify())).simplify();
            }
            if (A.a instanceof Constant){
                return new Multiply(A.a,new Multiply(A.b,b)).simplify();
            }
        }
        if (b instanceof Multiply){
            Multiply B=(Multiply)b;
            if (B.b.equal(a)){
                return (new Multiply(B.a,new Multiply(a,B.b))).simplify();
            }
            if (B.a.equal(b)){
                return (new Multiply(B.b,new Multiply(b,B.a))).simplify();
            }
            if (B.a.equal(a)){
                return new Multiply(B.b,new Multiply(a,B.a)).simplify();
            }
            if (B.a instanceof Constant){
                return new Multiply(new Multiply(a,B.a),B.b).simplify();
            }
        }
        if (a instanceof Divide){
            Divide A=(Divide)a;
            if (b instanceof Divide){
                Divide B=(Divide)b;
                return new Divide(new Multiply(B.top,A.top),new Multiply(A.bottom,B.bottom)).simplify();
            }
            return new Divide(new Multiply(A.top,b),A.bottom).simplify();
        }
        return this;
    }
    public boolean equal(Function f){
        if (f instanceof Multiply){
            Multiply d=(Multiply)f;
            return (d.a.equal(a) && d.b.equal(b)) || (d.a.equal(b) && d.b.equal(a));
        }
        return false;
    }
    public double eval(double d){
        return a.eval(d)*b.eval(d);
    }
}
