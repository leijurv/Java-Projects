/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iupac;
import java.util.*;
import java.awt.*;
/**
 *
 * @author leijurv
 */
public class HydroCarbon extends Molecule {
    static final String[] modNames = {"", "", "di", "tri", "tetra", "penta", "hexa"};
    static final String[] baseNames = {"", "meth", "eth", "prop", "but", "pent", "hex", "hept", "oct", "non", "dec"};
    static final String[] bondNames = {"", "ane", "ene", "yne"};
    static final String[] doNames = {"", "un", "do", "tri", "tetra", "penta", "hexa"};
    static final String[] decNames = {"", "dec", "cos", "triacont", "tetracont", "pentacont"};
    int numCarbon;
    int baseBondNum;
    Bond[] internalBonds;
    ArrayList<Molecule> sideMolecules;
    ArrayList<ArrayList<Bond>> sideBonds;
    ArrayList<ArrayList<Integer>> sideBondLocations;
    boolean isAcid;
    boolean isCyclic;
    public HydroCarbon(int n) {
        this(n, 1);
    }
    public HydroCarbon(int n, int numCovalent) {
        this.numCarbon = n;
        baseBondNum = numCovalent;
        internalBonds = new Bond[n - 1];
        for (int i = 0; i < n - 1; i++) {
            internalBonds[i] = new CovalentBond(numCovalent);
        }
        sideMolecules = new ArrayList<>();
        sideBonds = new ArrayList<>();
        sideBondLocations = new ArrayList<>();
        isCyclic = false;
        isAcid = false;
    }
    public void addBond(int loc) {
        int curBond = internalBonds[loc - 1].getValenceChange()[0];//Current amount of bonds there
        internalBonds[loc - 1] = new CovalentBond(curBond + 1);
        for (Bond bond : internalBonds) {
            if (bond.getValenceChange()[0] != curBond + 1) {
                return;
            }
        }
        baseBondNum = curBond + 1;//If every single bond is now the same, increase the baseBondNum accordingly
    }
    public void addMolecule(Molecule molecule, int loc) {
        int numCovalent = 1;
        if (molecule instanceof Atom) {
            Atom a = (Atom) molecule;
            if (a.numValence >= 4 && a.numValence < 8) {
                numCovalent = 8 - a.numValence;
            }
        }
        addMolecule(new CovalentBond(numCovalent), molecule, loc);
    }
    public void addMolecule(Bond bond, Molecule molecule, int loc) {
        for (int i = 0; i < sideBonds.size(); i++) {
            if (sideMolecules.get(i).equals(molecule)) {//If that molecule is already bond somewhere else
                sideBonds.get(i).add(bond);
                sideBondLocations.get(i).add(loc);
                return;
            }
        }
        sideBonds.add(new ArrayList<>(Arrays.asList(new Bond[] {bond})));
        sideMolecules.add(molecule);
        sideBondLocations.add(new ArrayList<>(Arrays.asList(new Integer[] {loc})));
    }
    public <E> String toStringWithOnlyCommas(ArrayList<E> a) {//Helper for toString
        String s = a.toString().replace(" ", "");
        s = s.substring(1, s.length() - 1);
        return s;
    }
    @Override
    public String toString() {
        return toString(false);
    }
    public String toString(boolean yl) {//For methane, if yl is true, output methyl, if yl is false, output methane
        ArrayList<Integer> oxyLocations = new ArrayList<>();//Locations of double bonds to oxygen
        ArrayList<String> prefixes = new ArrayList<>();//Prefixes like 3-bromo
        for (int i = 0; i < sideMolecules.size(); i++) {
            String modname = getModName(sideBondLocations.get(i).size());
            sideBondLocations.get(i).sort(null);//Can sort with null because Integer implements comparable
            if (sideMolecules.get(i).equals(Atom.get("oxygen")) && sideBonds.get(i).contains(new CovalentBond(2))) {//Double bond to oxygen
                for (int j = 0; j < sideBondLocations.get(i).size(); j++) {
                    if (sideBonds.get(i).get(j).equals(new CovalentBond(2))) {
                        oxyLocations.add(sideBondLocations.get(i).get(j));
                    }
                }
                continue;//Don't include oxygen twice
            }
            String ta = toStringWithOnlyCommas(sideBondLocations.get(i)) + "-" + modname + "%" + sideMolecules.get(i).toStringWithin();
            prefixes.add(ta);
        }
        prefixes.sort((Object o1, Object o2)->{
            String a = (String) o1;
            String b = (String) o2;
            return a.split("%")[1].compareTo(b.split("%")[1]);//Sort alphabetically by name, not including prefixes like di, tri..
        });
        String sf = "";
        if (!prefixes.isEmpty()) {
            sf = combine(prefixes.toArray(), "-");//Add -es between prefixes
            sf = sf.replace("%", "");//Remove % markers for final
        }
        String base = getBaseName(numCarbon);//such as eth, meth, prop...
        ArrayList<ArrayList<Integer>> extraBondLocations = new ArrayList<>();
        int maxBondNum = 0;
        for (int i = 0; i < internalBonds.length; i++) {
            int numBonds = internalBonds[i].getValenceChange()[0];
            if (numBonds != baseBondNum) {
                while (extraBondLocations.size() < numBonds + 1) {//Make sure array is big enough
                    extraBondLocations.add(new ArrayList<>());
                }
                extraBondLocations.get(numBonds).add(i + 1);
            }
            if (numBonds > maxBondNum) {
                maxBondNum = numBonds;
            }
        }
        String bondMod = "";
        boolean addA = false;//Add an a after the baseName? e.g. metha, etha, propa...
        for (int i = 0; i < extraBondLocations.size(); i++) {
            if (extraBondLocations.get(i).isEmpty()) {
                continue;
            }
            String bm = toStringWithOnlyCommas(extraBondLocations.get(i));
            bm = "-" + bm + "-" + modNames[extraBondLocations.get(i).size()] + bondNames[i];
            if (extraBondLocations.get(i).size() > 1) {//If including 2,3-diene for example, add an A
                addA = true;
            }
            bondMod = bondMod + bm;
        }
        if (bondMod.equals("")) {//If there are no bond modifiers
            bondMod = bondNames[baseBondNum];//Add the base one, e.g. ethANE
            if (yl) {//But if in YL mode
                if (maxBondNum <= 1) {
                    bondMod = "";
                }
                bondMod = bondMod + "yl";//We want ethYL
            }
        }
        String oxy = "";
        if (!oxyLocations.isEmpty()) {
            oxy = toStringWithOnlyCommas(oxyLocations);
            oxy = "-" + oxy + "-" + modNames[oxyLocations.size()] + "one";
        }
        if (yl) {
            if (sf.equals("") && bondMod.equals("yl")) {//If no prefixes and in yl mode
                return base + "yl";//Skip next step
            }
        }
        String result = (yl ? "(" : "") + sf + (isCyclic ? "cylco" : "") + base + (addA ? "a" : "") + bondMod + oxy + (yl ? ")" : "");
        if (isAcid) {
            result = result.substring(0, result.length() - 1) + "oic acid";
        }
        return result;
    }
    @Override
    public String toStringWithin() {
        return toString(true);
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof HydroCarbon) {
            HydroCarbon hc = (HydroCarbon) o;
            return hc.numCarbon == numCarbon && hc.baseBondNum == baseBondNum;
        }
        return false;
    }
    public static ArrayList<double[]> getLocations(double x, double y, double totalAng, int numCarbon, boolean isCyclic) {
        double preX = x;
        double preY = y;
        double size = 40;
        double angCh = isCyclic ? ((Math.PI * (numCarbon - 2)) / ((double) numCarbon)) : Math.PI / 7;
        double curX = preX;
        double curY = preY;
        ArrayList<double[]> screenLocations = new ArrayList<>();
        for (int i = 0; i < numCarbon; i++) {
            screenLocations.add(new double[] {curX, curY});
            preX = curX;
            preY = curY;
            int R = ((i % 2 == 0) ? 1 : -1);
            double angll = isCyclic ? ((Math.PI - angCh) * i) : R * angCh;
            angll += totalAng;
            curX = (preX + Math.cos(angll) * size);
            curY = (preY + Math.sin(angll) * size);
        }
        return screenLocations;
    }
    @Override
    public void draw(Graphics g, double x, double y, double totalAng, Bond b, boolean showNumbers) {
        ArrayList<double[]> screenLocations = getLocations(x, y, totalAng, numCarbon, isCyclic);
        double nameDist = 200;
        double nameAng = totalAng + Math.PI / 2;
        double nameX = Math.cos(nameAng) * nameDist + x;
        double nameY = Math.sin(nameAng) * nameDist + y;
        boolean drawName = totalAng == 0;
        if (drawName) {
            g.drawString(toString(), (int) nameX, (int) nameY);
        }
        for (int i = 0; i < numCarbon - (isCyclic ? 0 : 1); i++) {
            (i == numCarbon - 1 ? new CovalentBond(baseBondNum) : internalBonds[i]).draw(g, screenLocations.get(i)[0], screenLocations.get(i)[1], screenLocations.get((i + 1) % (screenLocations.size()))[0], screenLocations.get((i + 1) % (screenLocations.size()))[1]);
        }
        ArrayList<ArrayList<Bond>> bondss = new ArrayList<>();
        ArrayList<ArrayList<Molecule>> othh = new ArrayList<>();
        for (int i = 0; i < numCarbon; i++) {
            bondss.add(new ArrayList<>());
            othh.add(new ArrayList<>());
        }
        for (int i = 0; i < sideMolecules.size(); i++) {
            for (int j = 0; j < sideBonds.get(i).size(); j++) {
                Bond bond = sideBonds.get(i).get(j);
                Molecule mol = sideMolecules.get(i);
                bondss.get(sideBondLocations.get(i).get(j) - 1).add(bond);
                othh.get(sideBondLocations.get(i).get(j) - 1).add(mol);
            }
        }
        if (isAcid) {
            bondss.get(0).add(new CovalentBond(2));
            bondss.get(0).add(new CovalentBond(1));
            othh.get(0).add(Atom.get("oxygen"));
            othh.get(0).add(new Hydroxy());
        }
        if (showNumbers) {
            for (int i = 0; i < numCarbon; i++) {
                g.drawString("" + (i + 1), (int) screenLocations.get(i)[0], (int) screenLocations.get(i)[1]);
            }
        }
        double rad = 30;
        for (int i = 0; i < numCarbon; i++) {
            int numBond = bondss.get(i).size();
            if (numBond != 0) {
                double mlX = screenLocations.get(i)[0];
                double mlY = screenLocations.get(i)[1];
                double[] a = get(screenLocations, i, isCyclic);
                double start = a[0];
                double width = a[1];
                if ((i == 0 || i == numCarbon - 1) && numBond == 1 && !isCyclic) {
                    width /= 2;
                }
                for (int j = 0; j < numBond; j++) {
                    double an = (start) + (width / (numBond + 1)) * (j + 1);
                    int X = (int) (Math.cos(an) * rad + mlX);
                    int Y = (int) (Math.sin(an) * rad + mlY);
                    Bond bond = bondss.get(i).get(j);
                    bond.draw(g, mlX, mlY, X, Y);
                    othh.get(i).get(j).draw(g, X, Y, an, bond, false);
                }
            }
        }
    }
    public static double[] get(ArrayList<double[]> screenLocations, int i, boolean cyclic) {
        double prevX = screenLocations.get((i - 1 + screenLocations.size()) % (screenLocations.size()))[0];
        double prevY = screenLocations.get((i - 1 + screenLocations.size()) % (screenLocations.size()))[1];
        double nextX = screenLocations.get((i + 1) % (screenLocations.size()))[0];
        double nextY = screenLocations.get((i + 1) % (screenLocations.size()))[1];
        double mlX = screenLocations.get(i)[0];
        double mlY = screenLocations.get(i)[1];
        if (i == 0 && !cyclic) {
            if (screenLocations.size() == 1) {
                return new double[] {0, Math.PI};
            }
            double nd = Math.atan2(nextY - mlY, nextX - mlX);
            return new double[] {nd, Math.PI * 2};
        }
        if (i == screenLocations.size() - 1 && !cyclic) {
            double pd = Math.atan2(prevY - mlY, prevX - mlX);
            return new double[] {pd, Math.PI * 2};
        }
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
    public static String combine(Object[] s, String glue) {
        int k = s.length;
        if (k == 0) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        out.append(s[0]);
        for (int x = 1; x < k; ++x) {
            out.append(glue).append(s[x]);
        }
        return out.toString();
    }
    public static String getBaseName(int ind) {
        if (ind < baseNames.length) {
            return baseNames[ind];
        }
        return doNames[ind % 10] + decNames[ind / 10];
    }
    public static String getModName(int ind) {
        if (ind < modNames.length) {
            return modNames[ind];
        }
        return doNames[ind % 10] + decNames[ind / 10];
    }
}
