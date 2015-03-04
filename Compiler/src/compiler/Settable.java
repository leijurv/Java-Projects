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
public abstract class Settable extends Expression {
    public abstract void set(Context c, Object value);
}
