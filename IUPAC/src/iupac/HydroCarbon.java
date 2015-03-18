/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iupac;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.*;
/**
 *
 * @author leijurv
 */
public class HydroCarbon extends Molecule {
    static final String[] modNames = {"", "", "di", "tri", "tetra", "penta", "hexa"};
    static final String[] baseNames = {"", "meth", "eth", "prop", "but", "pent", "hex", "hept", "oct", "non", "dec"};
    static final String[] bondNames = {"", "ane", "ene", "yne"};
    int numCarbon;
    int baseBondNum;
    Bond[] nb;
    ArrayList<Molecule> oth;
    ArrayList<ArrayList<Bond>> bonds;
    ArrayList<ArrayList<Integer>> locations;
    boolean isAcid;
    boolean isCyclic;
    public HydroCarbon(int n) {
        this(n, 1);
    }
    public HydroCarbon(int n, int numCovalent) {
        this.numCarbon = n;
        baseBondNum = numCovalent;
        nb = new Bond[n - 1];
        for (int i = 0; i < n - 1; i++) {
            nb[i] = new CovalentBond(numCovalent);
        }
        oth = new ArrayList<>();
        bonds = new ArrayList<>();
        locations = new ArrayList<>();
        isCyclic = false;
        isAcid = false;
    }
    public void addBond(int loc) {
        int curBond = nb[loc - 1].getValenceChange()[0];
        nb[loc - 1] = new CovalentBond(curBond + 1);
        for (int i = 0; i < nb.length; i++) {
            if (nb[i].getValenceChange()[0] != curBond + 1) {
                return;
            }
        }
        baseBondNum = curBond + 1;
    }
    public void addMolecule(Bond bond, Molecule molecule, int loc) {
        for (int i = 0; i < bonds.size(); i++) {
            if (oth.get(i).equals(molecule)) {
                bonds.get(i).add(bond);
                locations.get(i).add(loc);
                return;
            }
        }
        bonds.add(new ArrayList<>());
        oth.add(molecule);
        locations.add(new ArrayList<>());
        addMolecule(bond, molecule, loc);
    }
    public String toStringWithOnlyCommas(ArrayList a) {
        String s = a.toString().replace(" ", "");
        s = s.substring(1, s.length() - 1);
        return s;
    }
    public String toString() {
        return toString(false);
    }
    public String toString(boolean yl) {
        String sf = "";
        for (int i = 0; i < oth.size(); i++) {
            String modname = "{" + locations.get(i).size() + "ta}";
            if (locations.get(i).size() < modNames.length) {
                modname = modNames[locations.get(i).size()];
            }
            locations.get(i).sort(null);
            sf = sf + toStringWithOnlyCommas(locations.get(i)) + "-" + modname + oth.get(i).toStringWithin() + "-";
        }
        if (sf.length() != 0) {
            sf = sf.substring(0, sf.length() - 1);
        }
        String base = baseNames[numCarbon];
        ArrayList<Integer> extraBondLocations = new ArrayList<>();
        int maxBondNum = 0;
        for (int i = 0; i < nb.length; i++) {
            int numBonds = nb[i].getValenceChange()[0];
            if (numBonds != baseBondNum) {
                extraBondLocations.add(i + 1);
            }
            if (numBonds > maxBondNum) {
                maxBondNum = numBonds;
            }
        }
        String bondNum = bondNames[maxBondNum];
        String bondMod = "";
        if (!extraBondLocations.isEmpty()) {
            bondMod = extraBondLocations.toString().replace(" ", "");
            bondMod = bondMod.substring(1, bondMod.length() - 1);
            bondMod = "-" + bondMod + "-" + modNames[extraBondLocations.size()];
        }
        if (yl) {
            if (maxBondNum == 1) {
                bondNum = "";
            }
            bondNum = bondNum + "yl";
        }
        return sf + base + bondMod + bondNum;
    }
    @Override
    public String toStringWithin() {
        return "(" + toString(true) + ")";
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof HydroCarbon) {
            HydroCarbon hc = (HydroCarbon) o;
            return hc.numCarbon == numCarbon && hc.baseBondNum == baseBondNum;
        }
        return false;
    }
    @Override
    public void draw(Graphics g, int x, int y, double totalAng, Bond b) {
        int preX = x;
        int preY = y;
        double size = 70;
        double angCh = Math.PI / 7;
        int curX = preX;
        int curY = preY;
        ArrayList<int[]> screenLocations = new ArrayList<>();
        for (int i = 0; i < numCarbon; i++) {
            screenLocations.add(new int[] {curX, curY});
            preX = curX;
            preY = curY;
            int R = (i % 2 == 0) ? 1 : -1;
            curX = (int) (preX + Math.cos(totalAng + R * angCh) * size);
            curY = (int) (preY + Math.sin(totalAng + R * angCh) * size);
        }
        double nameDist = 200;
        double nameAng = totalAng + Math.PI / 2;
        double nameX = Math.cos(nameAng) * nameDist + x;
        double nameY = Math.sin(nameAng) * nameDist + y;
        boolean drawName = totalAng == 0;
        if (drawName) {
            g.drawString(toString(), (int) nameX, (int) nameY);
        }
        for (int i = 0; i < numCarbon - 1; i++) {
            nb[i].draw(g, screenLocations.get(i)[0], screenLocations.get(i)[1], screenLocations.get(i + 1)[0], screenLocations.get(i + 1)[1]);
        }
        ArrayList<ArrayList<Bond>> bondss = new ArrayList<>();
        ArrayList<ArrayList<Molecule>> othh = new ArrayList<>();
        for (int i = 0; i < numCarbon; i++) {
            bondss.add(new ArrayList<>());
            othh.add(new ArrayList<>());
        }
        for (int i = 0; i < oth.size(); i++) {
            for (int j = 0; j < bonds.get(i).size(); j++) {
                Bond bond = bonds.get(i).get(j);
                Molecule mol = oth.get(i);
                bondss.get(locations.get(i).get(j) - 1).add(bond);
                othh.get(locations.get(i).get(j) - 1).add(mol);
            }
        }
        for (int i = 0; i < numCarbon; i++) {
            //g.drawString(bondss.get(i) + "," + othh.get(i), screenLocations.get(i)[0], screenLocations.get(i)[1]);
        }
        double rad = 30;
        for (int i = 0; i < numCarbon; i++) {
            int numBond = bondss.get(i).size();
            if (numBond != 0) {
                int mlX = screenLocations.get(i)[0];
                int mlY = screenLocations.get(i)[1];
                double[] a = get(screenLocations, i);
                double start = a[0];
                double width = a[1];
                if ((i == 0 || i == numCarbon - 1) && numBond == 1) {
                    width /= 2;
                }
                for (int j = 0; j < numBond; j++) {
                    double an = (start) + (width / (numBond + 1)) * (j + 1);
                    int X = (int) (Math.cos(an) * rad + mlX);
                    int Y = (int) (Math.sin(an) * rad + mlY);
                    Bond bond = bondss.get(i).get(j);
                    bond.draw(g, mlX, mlY, X, Y);
                    othh.get(i).get(j).draw(g, X, Y, an, bond);
                }
            }
        }
    }
    public static double[] get(ArrayList<int[]> screenLocations, int i) {
        int mlX = screenLocations.get(i)[0];
        int mlY = screenLocations.get(i)[1];
        if (i == 0) {
            if (screenLocations.size() == 1) {
                return new double[] {0, Math.PI};
            }
            int nextX = screenLocations.get(i + 1)[0];
            int nextY = screenLocations.get(i + 1)[1];
            double nd = Math.atan2(nextY - mlY, nextX - mlX);
            return new double[] {nd, Math.PI * 2};
        }
        if (i == screenLocations.size() - 1) {
            int prevX = screenLocations.get(i - 1)[0];
            int prevY = screenLocations.get(i - 1)[1];
            double pd = Math.atan2(prevY - mlY, prevX - mlX);
            return new double[] {pd, Math.PI * 2};
        }
        int prevX = screenLocations.get(i - 1)[0];
        int prevY = screenLocations.get(i - 1)[1];
        int nextX = screenLocations.get(i + 1)[0];
        int nextY = screenLocations.get(i + 1)[1];
        double nd = Math.atan2(nextY - mlY, nextX - mlX);
        double pd = Math.atan2(prevY - mlY, prevX - mlX);
        double start = nd > pd ? pd : nd;
        double width = (Math.PI * 4 + Math.abs(nd - pd)) % (Math.PI * 2);
        if (width < Math.PI) {
            width = Math.PI * 2 - width;
            start = nd > pd ? nd : pd;
        }
        return new double[] {start, width};
    }
}
