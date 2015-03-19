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
public class Nitro extends Molecule {
    @Override
    public String toString() {
        return "NO2";
    }
    @Override
    public String toStringWithin() {
        return "nitro";
    }
    @Override
    public boolean equals(Object o) {
        return o instanceof Nitro;
    }
    @Override
    public void draw(Graphics g, double x, double y, double ang, Bond bond) {
        double centerX = x + Math.cos(ang) * 12;
        double centerY = y + Math.sin(ang) * 12;
        double start = ang + Math.PI;
        g.drawString("NO2", (int) centerX, (int) (centerY + 6));
    }
}
