/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package derivative;

import java.util.ArrayList;
import java.util.Scanner;
import reversepolishnotation.ReversePolishNotation;



/**
 *
 * @author leijurv
 */
public class Derivative {
static final String[] num={"0","1","2","3","4","5","6","7","8","9"};
static final String[] let={"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","y","z"};
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        String r="sec(sin(cosx)^tanx)";
        
        Function f=preparse(r);
        
        System.out.println("Parsing finished");
        System.out.println(f);
        System.out.println(f.eval(3));//eval where x=3
        Function R=f.derivitive();
        System.out.println(R);
        System.out.println(R.simplify());//You need to work on the simplify function. 
        //The derivative function is correct.
        
        
        
        //Function F=new Multiply(new Multiply(new Tan(new X()),new Cos(new X())),new Csc(new X()));
        //System.out.println(F);
        //System.out.println(F.simplify());
    }
    public static boolean let(Object o){//Does o consist of letters?
        if (o instanceof String){
             String r=(String)o;
            if (r.length()>1){
                for (int i=0; i<r.length(); i++){
                    if (!let(r.substring(i,i+1))){
                        return false;
                    }
                }
                return true;
            }
            for (String s : let){
                if (((String)o).equals(s)){
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean num(Object o){//Is o a number?
        if ((o instanceof Constant)){
            return true;
        }
        if (o instanceof String){
           
            for (String s : num){
                if (((String)o).equals(s)){
                    return true;
                }
            }
        }
        return false;
    }
    public static Function preparse(String s){
        Object[] o=new Object[s.length()];
        for (int i=0; i<o.length; i++){
            o[i]=s.substring(i,i+1);
        }
        int n=0;
        for (int i=0; i<o.length; i++){
            if (o[i].equals(" ")){
                o[i]=null;
                n++;
            }
        }
        for (int i=0; i<o.length-1; i++){
            if (o[i]!=null&&o[i+1]!=null&&let(o[i])&&let(o[i+1])){
                o[i+1]=(String)o[i]+""+(String)o[i+1];
                o[i]=null;
                n++;
                //i--;
            }
            if (o[i]!=null&&o[i+1]!=null&&num(o[i])&&num(o[i+1])){
                o[i+1]=(String)o[i]+""+(String)o[i+1];
                o[i]=null;
                n++;
                //i--;
            }
        }
        for (int i=0; i<o.length-1; i++){
            if (o[i]==null&&o[i+1]!=null){
                o[i]=o[i+1];
                o[i+1]=null;
                i=-1;
            }
        }
        Object[] N=new Object[o.length-n];
        for (int i=0; i<N.length; i++){
            N[i]=o[i];
        }
        //Grouping together sequences of letters and numbers. e.g. 4,5,+,j,r,*,x becomes 45,+,jr,*,x
        //Note: x does not group it says by itself
        return parse(N);
    }
    public static Function Do(Object[] o, int i){//Evaluate the operator at i in o, 
        //replace a*b with the result, then evaluate that recursively
        Object[] N=new Object[3];
                for (int m=i-1; m<i+2; m++){
                    N[m-(i-1)]=o[m];
                }
                Object[] Cool=new Object[o.length-2];
                for (int j=0; j<i-1; j++){
                    Cool[j]=o[j];
                }
                for (int j=i+2; j<o.length; j++){
                    Cool[j-2]=o[j];
                }
                Cool[i-1]=parse(N);
                return parse(Cool);
    }
    public static Function tr(String a,Function f){
        if (a.equals("sin")){
            return new Sin(f);
        }
        if (a.equals("cos")){
            return new Cos(f);
        }
        if (a.equals("tan")){
            return new Tan(f);
        }
        if (a.equals("sec")){
            return new Sec(f);
        }
        if (a.equals("csc")){
            return new Csc(f);
        }
        if (a.equals("ln")){
            return new Ln(f);
        }
        if (a.equals("cot")){
            return new Cot(f);
        }
        return f;
    }
    public static Function parse(Object[] o){
        for (Object k : o){
            if (k instanceof Function){
                System.out.print("func "+k+"       ");
            }else{
                System.out.print(k+"       ");
            }
            
        }
        System.out.println();
        if (o.length==1){
            if (o[0] instanceof Function){
                return (Function)o[0];
            }
            if (o[0].equals("x")){
                return new X();
            }
            return new Constant(Integer.parseInt((String)o[0]));
        }
        if (o.length==3){
            Function First=parse(new Object[]{o[0]});
            String s=(String)o[1];
            Function Last=parse(new Object[]{o[2]});
            if (s.equals("^")){
                return new ToThePower(First,Last);
            }
            if (s.equals("+")){
                return new Add(First,Last);
            }
            if (s.equals("*")){
                return new Multiply(First,Last);
            }
            if (s.equals("/")){
                return new Divide(First,Last);
            }
        }
        //Parenthesis
        for (int i=0; i<o.length; i++){
            if (o[i].equals("(")){
                int n=1;
                int j;
                for (j=i+1; j<o.length&&n>0; j++){
                    if (o[j].equals("(")){
                        n++;
                    }
                    if (o[j].equals(")")){
                        n--;
                    }
                }
                Object[] N=new Object[j-i-2];
                for (int m=i+1; m<j-1; m++){
                    N[m-i-1]=o[m];
                }
                                Object[] Cool=new Object[o.length-(N.length+1)];
                for (int m=0; m<i; m++){
                    Cool[m]=o[m];
                }
                for (int m=j; m<o.length; m++){
                    Cool[m-j+i+1]=o[m];
                }
                Cool[i]=parse(N);
                return parse(Cool);
                
            }
        }
        //Functions
        for (int i=0; i<o.length; i++){
            if (o[i].equals("sin") || o[i].equals("cos") || o[i].equals("tan") || o[i].equals("sec") || o[i].equals("csc") || o[i].equals("cot") || o[i].equals("ln")){
                Object[] Cool=new Object[o.length-1];
                for (int j=0; j<i; j++){
                    Cool[j]=o[j];
                }
                for (int j=i; j<o.length-1; j++){
                    Cool[j]=o[j+1];
                }
                Function f=parse(new Object[] {o[i+1]});
                Cool[i]=tr((String)o[i],f);
                return parse(Cool);
            }
        }
        //Operators, using order of operations. Between * and /, 5*6/7 would be (5*6)/7, going left to right
        for (int i=0; i<o.length; i++){
            if (o[i].equals("^")){
                return Do(o,i);
            }
        }
        for (int i=0; i<o.length; i++){
            if (o[i].equals("*") || o[i].equals("/")){
                return Do(o,i);
            }
        }
        
        for (int i=0; i<o.length; i++){
            if (o[i].equals("+") || o[i].equals("-")){
                return Do(o,i);
            }
        }
        System.out.print("ERROR WHILE PARSING");
        for (Object k : o){
            System.out.print(k);
        }
        throw new RuntimeException("ERROR WHILE PARSING");
    }
    
    
}
