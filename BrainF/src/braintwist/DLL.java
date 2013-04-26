/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package braintwist;

/**
 *
 * @author leijurv
 */
public class DLL<E> {
    DLL<E> prev;
    DLL<E> next;
    E current;
    public DLL(E e){
        current=e;
    }
}
