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
public class IUPAC {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        HydroCarbon demo = new HydroCarbon(6);
        demo.addMolecule(new CovalentBond(1), Atom.get("flourine"), 1);
        demo.addBond(2);
        demo.addBond(2);
        demo.addBond(4);
        demo.addBond(4);
        demo.addMolecule(new CovalentBond(1), Atom.get("bromine"), 1);
        demo.addMolecule(new CovalentBond(1), Atom.get("flourine"), 3);
        demo.addMolecule(new CovalentBond(1), Atom.get("flourine"), 3);
        HydroCarbon side = new HydroCarbon(2);
        side.addBond(1);
        demo.addMolecule(new CovalentBond(1), side, 3);
        demo.addMolecule(new CovalentBond(1), new HydroCarbon(2), 6);
        HydroCarbon tetraflourine = new HydroCarbon(6);
        for (int i = 1; i <= 6; i++) {
            for (int j = 0; j < 2; j++) {
                tetraflourine.addMolecule(new CovalentBond(1), Atom.get("flourine"), i);
            }
        }
        tetraflourine.addMolecule(new CovalentBond(2), Atom.get("flourine"), 1);
        tetraflourine.addMolecule(new CovalentBond(1), Atom.get("flourine"), 6);
        HydroCarbon t = new HydroCarbon(6);
        t.addMolecule(new CovalentBond(2), Atom.get("oxygen"), 3);
        JFrame frame = new JFrame("dank");
        frame.setContentPane(new JComponent() {
            public void paintComponent(Graphics g) {
                g.setFont(new Font("Courier", Font.PLAIN, 12));
                tetraflourine.draw(g, 200, 200, 0, null);
                demo.draw(g, 500, 500, 0, null);
                t.draw(g, 600, 100, 0, null);
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(100000, 10000);
        frame.setVisible(true);
    }
}
