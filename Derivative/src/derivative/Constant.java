/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package derivative;

/**
 *
 * @author leijurv
 */
public class Constant extends Function{
    int val;
    public Constant(int Val){
        val=Val;
    }
    public Function derivitive(){
        return new Constant(0);
    }
    public String toString(){
        return ""+val;
    }
    public Function simplify(){
        if (val<0){
            System.out.println(val);
            //return new Subtract(new Constant(0),new Constant(-val));
        }
        return this;
    }
    public boolean equal(Function f){
        if (f instanceof Constant){
            return val==((Constant)f).val;
        }
        return false;
    }
    public double eval(double d){
        return (double)val;
    }
}
