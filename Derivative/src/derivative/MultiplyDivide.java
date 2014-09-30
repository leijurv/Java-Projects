/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package derivative;
import java.util.ArrayList;
/**
 *
 * @author leijurv
 */
public class MultiplyDivide extends Function {
    ArrayList<Function> top=new ArrayList<Function>();
    ArrayList<Function> bottom=new ArrayList<Function>();
    public MultiplyDivide(Function[] A,Function[] B){
        for (Function a : A){
            top.add(a.clone());
        }
        for (Function b : B){
            bottom.add(b.clone());
        }
    }
    public MultiplyDivide(Function[] A,ArrayList<Function> B){
        for (Function a : A){
            top.add(a.clone());
        }
        for (Function b : B){
            bottom.add(b.clone());
        }
    }
    public MultiplyDivide(ArrayList<Function> A,ArrayList<Function> B){
        for (Function a : A){
            top.add(a.clone());
        }
        for (Function b : B){
            bottom.add(b.clone());
        }
    }
    public static MultiplyDivide multiply(Function A,Function B){
        //System.out.println("mul"+A+","+B);
        return new MultiplyDivide(new Function[]{A,B},new Function[]{});
    }
    public Function derivitive(){
        if (bottom.isEmpty()){
            Function res=new Constant(0);
            for (int i=0; i<top.size(); i++){
                Function[] r=new Function[top.size()];
                int n=0;
                for (int j=0; j<i; j++){
                    r[n++]=top.get(j);
                }
                for (int j=1+i; j<top.size(); j++){
                    r[n++]=top.get(j);
                }
                r[top.size()-1]=top.get(i).derivitive();
                res=new Add(res,new MultiplyDivide(r,new Function[]{}));
            }
            return res;
        }
        Function f=new MultiplyDivide(top,new ArrayList<Function>());
        Function g=new MultiplyDivide(bottom,new ArrayList<Function>());
        return new MultiplyDivide(new Function[]{new Subtract(multiply(f.derivitive(),g),multiply(f,g.derivitive()))},new Function[]{new ToThePower(g,new Constant(2))});
        //return new Add(new MultiplyDivide(a,b.derivitive()),new MultiplyDivide(b,a.derivitive()));
    }
    public String toString(){
        String result="(";
        for (int i=0; i<top.size(); i++){
            if (top.get(i) instanceof Add||top.get(i) instanceof Subtract){
                result=result+"(";
                result=result+top.get(i);
                result=result+")";
            }else{
                result=result+top.get(i);
            }
            if (i!=top.size()-1){
                result=result+"*";
            }
        }
        result=result+")";
        if (!bottom.isEmpty()){
            result=result+"/(";
            for (int i=0; i<bottom.size(); i++){
                result=result+bottom.get(i);
                if (i!=bottom.size()-1){
                    result=result+"*";
                }
            }
            result=result+")";
        }
        return result;
    }
    /*
     public String toString(){
     return "("+a+")*("+b+")";
     }*/
    public boolean sw(Function a,Function b){
        if (a instanceof Constant){
            return false;
        }
        if (b instanceof Constant){
            return true;
        }
        if (a instanceof X){
            return false;
        }
        if (b instanceof X){
            return true;
        }
        if (a instanceof ToThePower){
            return false;
        }
        if (b instanceof ToThePower){
            return true;
        }
        return false;
    }
    public Function simplify(){
        //System.out.println("s"+this);
        for (int i=0; i<top.size(); i++){
            top.set(i,top.get(i).simplify());
        }
        for (int i=0; i<bottom.size(); i++){
            bottom.set(i,bottom.get(i).simplify());
        }
        if (top.contains(new Constant(0))){
            return new Constant(0);
        }
        while (top.contains(new Constant(1))&&top.size()>1){
            top.remove(new Constant(1));
        }
        if (bottom.isEmpty()&&top.size()==1){
            return top.get(0);
        }
        for (int i=0; i<top.size()-1; i++){
            if (sw(top.get(i),top.get(i+1))){
                Function f=top.get(i);
                top.set(i,top.get(i+1));
                top.set(i+1,f);
                i=-1;
            }
        }
        for (int i=0; i<top.size(); i++){
            Function t=top.get(i);
            if (t instanceof Subtract&&Subtract.expand){
                Subtract T=(Subtract) t;
                top.remove(i);
                return new Subtract(multiply(T.a,new MultiplyDivide(top,bottom)),multiply(T.b,new MultiplyDivide(top,bottom))).simplify();
            }
            if (t instanceof Add&&Subtract.expand){
                Add T=(Add) t;
                top.remove(i);
                return new Add(multiply(T.a,new MultiplyDivide(top,bottom)),multiply(T.b,new MultiplyDivide(top,bottom))).simplify();
            }
            if (t instanceof Constant&&i!=0){
                if (i==1&&top.get(0) instanceof Constant){
                    top.set(0,new Constant(((Constant) (top.get(0))).val*((Constant) (top.get(1))).val));
                    top.remove(1);
                    return simplify();
                }
                 Function f=top.get(0);
                 top.set(0,t);
                 top.set(i,f);

            }
            if (t instanceof ToThePower){
                ToThePower T=((ToThePower) t);
                if (top.contains(T.base)){
                    top.remove(T.base);
                    T.pow=new Add(new Constant(1),T.pow);
                    return simplify();
                }
                for (int j=0; j<bottom.size(); j++){
                    if (bottom.get(j) instanceof ToThePower){
                        ToThePower B=(ToThePower) (bottom.get(j));
                        if (B.base.equals(T.base)){
                            //(a^b)/(a^c)
                            B.pow=new Subtract(B.pow,T.pow);
                            top.remove(i);
                            return simplify();
                        }
                    }
                }
            }
            if (t instanceof MultiplyDivide){
                MultiplyDivide a=new MultiplyDivide(((MultiplyDivide) t).top,((MultiplyDivide) t).bottom);
                top.remove(i);
                top.addAll(a.top);
                bottom.addAll(a.bottom);
                return simplify();
            }
            if (t instanceof Subtract){
                if (((Subtract) t).a instanceof Constant){
                    if (((Constant) ((Subtract) t).a).val==0){
                        top.set(i,((Subtract) t).b);
                        top.add(0,new Constant(-1));
                        return simplify();
                    }
                }
            }
        }
        for (int i=0; i<top.size(); i++){
            for (int j=0; j<top.size(); j++){
                Function I=top.get(i);
                Function J=top.get(j);
                if (i!=j){
                    if (I.equal(J)){
                        top.remove(i);
                        top.remove(J);
                        top.add(0,new ToThePower(I,new Constant(2)));
                        return simplify();
                    }
                }
            }
        }
        for (int i=0; i<bottom.size(); i++){
            Function t=bottom.get(i);
            if (t instanceof ToThePower){
                ToThePower T=(ToThePower) t;
                if (top.contains(T.base)){
                    top.remove(T.base);
                    T.pow=new Add(new Constant(-1),T.pow);
                    return simplify();
                }
            }
        }
        if (top.size()==1&&top.get(0) instanceof Subtract){
            Subtract T=(Subtract) (top.get(0));
            return new Subtract(new MultiplyDivide(new Function[]{T.a},bottom),new MultiplyDivide(new Function[]{T.b},bottom)).simplify();
        }
        if (top.size()==1&&bottom.isEmpty()){
            return top.get(0).simplify();
        }
        if (top.isEmpty()){
            top.add(new Constant(1));
        }
        return this;
    }
    public boolean equal(Function f){
        if (f instanceof MultiplyDivide){
            MultiplyDivide d=(MultiplyDivide) f;
            MultiplyDivide D=new MultiplyDivide(d.top,d.bottom);
            for (int i=0; i<top.size(); i++){
                if (!D.top.contains(top.get(i))){
                    return false;
                }
                D.top.remove(top.get(i));
            }
            for (int i=0; i<bottom.size(); i++){
                if (!D.bottom.contains(bottom.get(i))){
                    return false;
                }
                D.bottom.remove(bottom.get(i));
            }
            return D.top.isEmpty() && D.bottom.isEmpty();
        }
        return false;
    }
    public double eval(double d){
        double Top=1;
        double Bottom=1;
        for (Function a : top){
            Top=Top*a.eval(d);
        }
        for (Function a : bottom){
            Bottom=Bottom*a.eval(d);
        }
        return Top/Bottom;
    }
    public Function clone(){
        return new MultiplyDivide(top,bottom);
    }
}
