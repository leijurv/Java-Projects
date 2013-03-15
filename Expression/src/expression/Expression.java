/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package expression;

import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Expression {
    char operation;
    Expression first;
    Expression second;
    int value;
    boolean integer;
    public Expression(int a){
        value=a;
        integer=true;
        operation=' ';
        first=null;
        second=null;
    }
    public Expression(char op, Expression a, Expression b){
        value=-1;
        integer=false;
        operation=op;
        first=a;
        second=b;
    }
    public double eval(){
        if (integer){
            return (double)value;
        }
        if (operation=='+'){
            return first.eval()+second.eval();
        }
        if (operation=='-'){
            return first.eval()-second.eval();
        }
        if (operation=='*'){
            return first.eval()*second.eval();
        }
        if (operation=='/'){
            return first.eval()/second.eval();
        }
        if (operation=='%'){
            return first.eval()%second.eval();
        }
        return 0;
    }
    private final char[] ops={'+','-','*','/','%'};
    private static Expression parsenopar(String s){
        Expression x=new Expression(0);
        String current="";
        char op=' ';
        for (int i=0; i<s.length(); i++){
            char c=s.charAt(i);
            if (c=='+' || c=='*' || c=='/' || c=='-' || c=='%'){
                if (op==' '){
                    x=new Expression(Integer.parseInt(current));
                }else{
                    x=new Expression(op,x,new Expression(Integer.parseInt(current)));
                }
                current="";
                op=c;
            }else{
                current=current+c;
            }
        }
        if (op==' '){
                    x=new Expression(Integer.parseInt(current));
                }else{
                    x=new Expression(op,x,new Expression(Integer.parseInt(current)));
                }
        return x;
    }
    public static Expression parse(String s){
        if (!s.contains("(")){
            return parsenopar(s);
        }
        ArrayList<Integer> spar=new ArrayList<Integer>();
        ArrayList<Integer> epar=new ArrayList<Integer>();
        ArrayList<Integer> lpr=new ArrayList<Integer>();
        for (int i=0; i<s.length(); i++){
            if (s.substring(i,i+1).equals("(")){
                lpr.add(i);
            }
            if (s.substring(i,i+1).equals(")")){
                int x=lpr.remove(lpr.size()-1);
                spar.add(x);
                epar.add(i);
            }
        }
        Expression x=new Expression(0);
        String current="";
        char op=' ';
        for (int i=0; i<s.length(); i++){
            
            char c=s.charAt(i);
            if (c=='('){
                Expression result=null;
                for (int n=0; n<spar.size(); n++){
                    if (spar.get(n)==i){
                        result=parse(s.substring(i+1,epar.get(n)));
                        i=epar.get(n)+1;
                    }
                }
                if (op==' '){
                    x=result;
                }else{
                x=new Expression(op,x,result);
                }
                if (i<s.length()){
                op=s.charAt(i);
                }else{
                    return x;
                }
                current="";
            }else{
            if (c=='+' || c=='*' || c=='/' || c=='-' || c=='%'){
                if (op==' '){
                    x=new Expression(Integer.parseInt(current));
                }else{
                    x=new Expression(op,x,new Expression(Integer.parseInt(current)));
                }
                current="";
                op=c;
            }else{
                    current=current+c;
            }
            }
        }
        if (op==' '){
                    x=new Expression(Integer.parseInt(current));
                }else{
                    x=new Expression(op,x,new Expression(Integer.parseInt(current)));
                }
        return x;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Expression e=parse("2+(((5+(9/3))*(8-(6/2)))*2-1)");
        
        System.out.println(e.eval());
    }
}
