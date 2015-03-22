/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iupac;
import java.awt.*;
/**
 *
 * @author leijurv
 */
public abstract class Molecule {
    @Override
    public abstract String toString();
    public abstract String toStringWithin();
    @Override
    public abstract boolean equals(Object o);
    public abstract void draw(Graphics g, double x, double y, double angle, Bond bond, boolean showNumbers);
}
