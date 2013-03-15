/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lisp;

/**
 *
 * @author leijurv
 */
public class Lisp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String lambdaTest="((lambda (arg e) (arg e)) (lambda (r) (+ r 1)) 6)";//Should be 7
        Expression w=new Expression("(defun f (x) (if (= x 1) 1 (* x (f (- x 1)))))");
        Expression e=new Expression("(f 2)");
        w.eval();
        System.out.println(((AtomInt)e.eval()).value);
    }
}
 