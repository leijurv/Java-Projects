/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vector;

import java.util.ArrayList;
import reversepolishnotation.ReversePolishNotation;

/**
 *
 * @author leijurv
 */
public class Vector {

    int x;
    int y;
    boolean atom;

    public Vector(int X, int Y) {
        x = X;
        y = Y;
        atom = false;
    }

    public Vector(int X) {
        atom = true;
        x = X;
        y = 0;
    }
    private Vector(int X, int Y, boolean A){
        atom=A;
        x=X;
        y=Y;
    }
    public Vector add(Vector v) {
        return new Vector(v.x + x, v.y + y,atom&&v.atom);
    }

    public Vector subtract(Vector v) {
        return new Vector(x - v.x, y - v.y,atom&&v.atom);
    }

    public int dot(Vector v) {
        return x * v.x + y * v.y;
    }

    public String toString() {
        if (atom) {
            return Integer.toString(x);
        }
        return "(" + x + "," + y + ")";
    }

    public Vector multiply(int X) {
        return new Vector(X * x, X * y);
    }

    public Vector multiply(Vector v) {
        if (v.atom) {
            if (atom) {
                return new Vector(x * v.x);
            }
            return multiply(v.x);
        }
        if (atom) {
            return v.multiply(x);
        }
        throw new RuntimeException("Can't multiply vectors by vectors!");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        String[] vn={"u","v","z"};
        String[] v={"(5,-12)","(3,4)","(7,8)"};
        String e="(u+v-z).{(z.(u-v)),(2+3)}";
        
        eval(e,vn,v);
        
        
        
        
    }
    public static void eval(String e, String[] vn, String[] v){
        System.out.println("Expression in infix notation: "+e);
        String exp="";
        for (int i=0; i<e.length(); i++){
            exp=exp+e.charAt(i)+(i==e.length()-1?"":" ");
        }
        exp=exp.replace("{","(");
        exp=exp.replace("}",")");
        System.out.println("Expanded and replace brackets: "+exp);
        String message=exp;
        String[] ins=ReversePolishNotation.infixToRPN(message.split(" "));
        System.out.print("Converted to reverse polish notation: ");
        for (String n : ins){
            System.out.print(n+" ");
        }
        System.out.println();
        for (int n=0; n<ins.length; n++){
            for (int i=0; i<vn.length; i++){
            ins[n]=ins[n].replace(vn[i],v[i]);
        }
        }
        System.out.print("Using variables: ");
        for (int i=0; i<vn.length; i++){
            System.out.print(vn[i]+"="+v[i]+" ");
        }
        System.out.println();
        System.out.print("Replaced variables: ");
        for (String n : ins){
            System.out.print(n+" ");
        }
        System.out.println();
        ArrayList<Vector> stack = new ArrayList<Vector>();
        for (int i = 0; i < ins.length; i++) {
            if (ins[i].startsWith("(")) {
                String n = ins[i].substring(1, ins[i].length() - 1);
                String[] x = n.split(",");
                stack.add(new Vector(Integer.parseInt(x[0]), Integer.parseInt(x[1])));
            } else {
                try{
                    int n=Integer.parseInt(ins[i]);
                    stack.add(new Vector(n));
                    continue;
                }catch(Exception E){
                    
                }
                Vector b = stack.remove(stack.size() - 1);
                Vector a = stack.remove(stack.size() - 1);
                if (ins[i].equals("+")) {
                    stack.add(a.add(b));
                    continue;
                }
                if (ins[i].equals("-")) {
                    stack.add(a.subtract(b));
                    continue;
                }
                if (ins[i].equals(".")) {
                    stack.add(new Vector(a.dot(b)));
                    continue;
                }
                if (ins[i].equals("*")) {
                    stack.add(a.multiply(b));
                    continue;
                }
                if (ins[i].equals(",")){
                    if (!a.atom || !b.atom){
                        throw new RuntimeException("Vector coordinates can't be vectors!");
                    }
                    stack.add(new Vector(a.x,b.x));
                    continue;
                }
            }
        }
        System.out.println("Evaluated: "+stack.get(stack.size() - 1));
        if (stack.size()>1){
            System.out.println("ERROR: stack size "+stack.size()+", should be 1. Stack is "+stack);
        }
    }
}
