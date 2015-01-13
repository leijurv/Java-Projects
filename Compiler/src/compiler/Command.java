/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

/**
 *
 * @author leijurv
 */
public abstract class Command{
    @Override
    public abstract String toString();
    public abstract boolean execute(Context c);
    protected static boolean isTrue(Object exp){
        return (exp instanceof Boolean) ? ((Boolean) exp) : (!exp.equals(0));
    }
}
