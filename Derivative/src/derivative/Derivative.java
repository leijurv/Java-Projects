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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner scan=new Scanner(System.in);
        System.out.print("What function do you want to take the derivitive of? >");
        String X=scan.nextLine();
        X=X.replace("sin","1 sin");
        X=X.replace("cos","1 cos");
        X=X.replace("tan","1 tan");
        X=X.replace("log","1 log");
        X=X.replace("sec","1 sec");
        X=X.replace("csc","1 csc");
        X=X.replace("cot","1 cot");
        String[] input=X.split(" ");
        String[] output=ReversePolishNotation.infixToRPN(input);
        Function f=parseReversePolish(output);
        //Function f=new Divide(new ToThePower(new X(),new Sin(new X())),new Add(new ToThePower(new X(),new X()),new Constant(1)));

        System.out.println("Interpreted as "+f);
        System.out.println("Derivitive is "+f.derivitive());
        System.out.println("Simplified: "+f.derivitive().simplify());
        
    }
    public static Function parseReversePolish(String[] x){
        ArrayList<Function> stack=new ArrayList<Function>();
        for (String s : x){
            boolean done=false;
            if (s.equals("+")){
                Function B=stack.remove(stack.size()-1);
                Function A=stack.remove(stack.size()-1);
                stack.add(new Add(A,B));
                done=true;
            }
            if (s.equals("-")){
                Function B=stack.remove(stack.size()-1);
                Function A=stack.remove(stack.size()-1);
                stack.add(new Subtract(A,B));
                done=true;
            }
            if (s.equals("*")){
                Function B=stack.remove(stack.size()-1);
                Function A=stack.remove(stack.size()-1);
                stack.add(new Multiply(A,B));
                done=true;
            }
            if (s.equals("/")){
                Function B=stack.remove(stack.size()-1);
                Function A=stack.remove(stack.size()-1);
                stack.add(new Divide(A,B));
                done=true;
            }
            if (s.equals("^")){
                Function B=stack.remove(stack.size()-1);
                Function A=stack.remove(stack.size()-1);
                stack.add(new ToThePower(A,B));
                done=true;
            }
            if (s.equals("x")){
                stack.add(new X());
                done=true;
            }
            if (s.equals("sin")){
                Function B=stack.remove(stack.size()-1);
                Function A=stack.remove(stack.size()-1);
                stack.add(new Sin(B));
                done=true;
            }
            if (s.equals("cos")){
                Function B=stack.remove(stack.size()-1);
                Function A=stack.remove(stack.size()-1);
                stack.add(new Cos(B));
                done=true;
            }
            if (s.equals("tan")){
                Function B=stack.remove(stack.size()-1);
                Function A=stack.remove(stack.size()-1);
                stack.add(new Tan(B));
                done=true;
            }
            if (s.equals("sec")){
                Function B=stack.remove(stack.size()-1);
                Function A=stack.remove(stack.size()-1);
                stack.add(new Sec(B));
                done=true;
            }
            if (s.equals("log")){
                Function B=stack.remove(stack.size()-1);
                Function A=stack.remove(stack.size()-1);
                stack.add(new Ln(B));
                done=true;
            }
            if (s.equals("csc")){
                Function B=stack.remove(stack.size()-1);
                Function A=stack.remove(stack.size()-1);
                stack.add(new Csc(B));
                done=true;
            }
            if (s.equals("cot")){
                Function B=stack.remove(stack.size()-1);
                Function A=stack.remove(stack.size()-1);
                stack.add(new Cot(B));
                done=true;
            }
            if (!done){
                stack.add(new Constant(Integer.parseInt(s)));
            }
        }
        return stack.get(stack.size()-1);
    }
}
