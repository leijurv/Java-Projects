/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lisp;

/**
 *
 * @author leijurv
 */
public class AtomInt extends Atom{
    int value;
    public AtomInt(Expression e){
    super();
    }
    public AtomInt(int val){
        super();
        value=val;
    }
}
