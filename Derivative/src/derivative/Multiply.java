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
        if (b instanceof Divide){
        Divide B=(Divide)b;
        return new Divide(new Multiply(B.top,a),B.bottom).simplify();
    }
        
        if (b instanceof Multiply && a instanceof X){
            if (((Multiply)b).divByX()){
                Multiply T=(Multiply)b;
                if (T.a instanceof ToThePower){
                    ToThePower ta=(ToThePower)T.a;
                    if (ta.base instanceof X){
                        return new Multiply(new ToThePower(new X(),new Subtract(ta.pow,new Constant(1))),T.b);
                    }
                }
                if (T.b instanceof ToThePower){
                    ToThePower ta=(ToThePower)T.b;
                    if (ta.base instanceof X){
                        return new Multiply(new ToThePower(new X(),new Subtract(ta.pow,new Constant(1))),T.a);
                    }
                }
                if (T.a instanceof Multiply){
                    if (((Multiply)T.a).divByX()){
                        return new Multiply(T.b,new Multiply(new X(),T.a).simplify());
                    }
                }
                if (T.b instanceof Multiply){
                    if (((Multiply)T.b).divByX()){
                        return new Multiply(T.a,new Multiply(new X(),T.b).simplify());
                    }
                }
                
            }
        }
        if ((a instanceof Tan && b instanceof Cos)){
            Tan A=(Tan)a;
            Cos B=(Cos)b;
            if (A.of.equal(B.of)){
                return new Sin(A.of).simplify();
            }
        }
        if ((a instanceof Sin && b instanceof Csc)){
            Sin A=(Sin)a;
            Csc B=(Csc)b;
            if (A.of.equal(B.of)){
                return new Constant(1);
            }
        }
        if ((a instanceof Cos && b instanceof Sec)){
            Cos A=(Cos)a;
            Sec B=(Sec)b;
            if (A.of.equal(B.of)){
                return new Constant(1);
            }
        }
        if (b instanceof ToThePower && (!(a instanceof Constant) && !(a instanceof ToThePower))){
            return new Multiply(b,a).simplify();
        }
        if (b instanceof Multiply){
            Multiply B=(Multiply)b;
            if (B.a instanceof ToThePower){
                if (!(a instanceof ToThePower) && !(a instanceof Constant)){
                    return new Multiply(B.a,new Multiply(B.b,a)).simplify();
                }
            }
        }
        if (b instanceof Csc){
            Sin s=new Sin(((Csc)b).of);
            if (a instanceof ToThePower){
                if (((ToThePower)a).base.equal(s)){
                    return new ToThePower(((ToThePower)a).base,new Subtract(((ToThePower)a).pow,new Constant(1))).simplify();
                }
            }
            if (a instanceof Multiply){
                Multiply A=(Multiply)a;
                if (A.divBy(s)){
                    
                if (A.a.equal(s)){
                    return A.b;
                }
                if (A.b.equal(s)){
                    return A.a;
                }
                if (A.a instanceof Multiply){
                    if (((Multiply)A.a).divBy(s)){
                        return new Multiply(A.b,new Multiply(A.a,b)).simplify();
                    }
                }
                if (A.b instanceof Multiply){
                    if (((Multiply)A.b).divBy(s)){
                        return new Multiply(A.a,new Multiply(A.b,b)).simplify();
                    }
                }
                }
            }
        }
        if (b instanceof Csc){
            Cos s=new Cos(((Csc)b).of);
            Cot co=new Cot(((Csc)b).of);
            if (a instanceof ToThePower){
                if (((ToThePower)a).base.equal(s)){
                    return new Multiply(new Cot(((Csc)b).of),new ToThePower(((ToThePower)a).base,new Subtract(((ToThePower)a).pow,new Constant(1)))).simplify();
                }
            }
            if (a instanceof Multiply){
                Multiply A=(Multiply)a;
                if (A.divBy(s)){
                if (A.a.equal(s)){
                    return new Multiply(co,A.b);
                }
                if (A.b.equal(s)){
                    return new Multiply(co,A.a);
                }
                if (A.a instanceof Multiply){
                    if (((Multiply)A.a).divBy(s)){
                        return new Multiply(A.b,new Multiply(A.a,b)).simplify();
                    }
                }
                if (A.b instanceof Multiply){
                    if (((Multiply)A.b).divBy(s)){
                        return new Multiply(A.a,new Multiply(A.b,b)).simplify();
                    }
                }
                }
            }
        }
        if (a instanceof ToThePower && b instanceof Cos){
            ToThePower A=(ToThePower)a;
            Cos B=(Cos)b;
            if (A.base instanceof Sin){
                if (((Sin)A.base).of.equal(B.of)){
            if (A.pow instanceof Subtract){
                Subtract s=(Subtract)A.pow;
                if (s.b instanceof Constant){
                    if (((Constant)s.b).val==1){
                        return new Multiply(new ToThePower(A.base,s.a),new Cot(B.of)).simplify();
                    }
                }
            }
                }
            }
        }
        return this;
    }
    public boolean divBy(Function f){
        if (a.equal(f) || b.equal(f)){
            return true;
        }
        if (a instanceof Multiply){
            if (((Multiply)a).divBy(f)){
                return true;
            }
        }
        if (b instanceof Multiply){
            if (((Multiply)b).divBy(f)){
                return true;
            }
        }
        if (a instanceof ToThePower){
            if (((ToThePower)a).base.equal(f)){
                return true;
            }
        }
        if (b instanceof ToThePower){
            if (((ToThePower)b).base.equal(f)){
                return true;
            }
        }
        return false;
    }
    public boolean divByX(){
        //This silly functions basically checks if a multiply has a power of x somewhere in it. Heuristic.
        if (a instanceof X || b instanceof X){
            //return true;
        }
        if (a instanceof ToThePower){
            ToThePower A=(ToThePower)a;
            if (A.base instanceof X){
                return true;
            }
            if (A.base instanceof Multiply){
                if (((Multiply)A.base).divByX()){
                    return true;
                }
            }
        }
        if (b instanceof ToThePower){
            ToThePower B=(ToThePower)b;
            if (B.base instanceof X){
                return true;
            }
            if (B.base instanceof Multiply){
                if (((Multiply)B.base).divByX()){
                    return true;
                }
            }
        }
        if (a instanceof Multiply){
            if (((Multiply)a).divByX()){
                return true;
            }
        }
        if (b instanceof Multiply){
            if (((Multiply)b).divByX()){
                return true;
            }
        }
        return false;
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
