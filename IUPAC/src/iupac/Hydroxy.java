/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iupac;
import java.awt.Graphics;
/**
 *
 * @author leijurv
 */
public class Hydroxy extends Molecule {
    @Override
    public String toString() {
        return "OH";
    }
    @Override
    public String toStringWithin() {
        return "hydroxy";
    }
    @Override
    public boolean equals(Object o) {
        return o instanceof Hydroxy;
    }
    @Override
    public void draw(Graphics g, double x, double y, double ang, Bond bond, boolean show) {
        double centerX = x + Math.cos(ang) * 12;
        double centerY = y + Math.sin(ang) * 12;
        double start = ang + Math.PI;
        g.drawString("OH", (int) centerX, (int) (centerY + 6));
    }
}
