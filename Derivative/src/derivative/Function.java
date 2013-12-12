/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package derivative;

/**
 *
 * @author leijurv
 */
public abstract class Function {
    public static boolean exp=true;
    public abstract Function derivitive();
    @Override
    public abstract String toString();
    public abstract Function simplify();
    public abstract boolean equal(Function f);
    public boolean equals(Object o){
        if (o instanceof Function){
            return equal((Function)o);
        }
        return false;
    }
    public abstract double eval(double d);
}
