/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iupac;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.*;
/**
 *
 * @author leijurv
 */
public abstract class Molecule {
    public abstract String toString();
    public abstract String toStringWithin();
    public abstract boolean equals(Object o);
    public abstract void draw(Graphics g, double x, double y, double angle, Bond bond);
}
