/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iupac;
import java.awt.*;
import java.io.*;
/**
 *
 * @author leijurv
 */
public class Atom extends Molecule {
    public static final int[] valence = {0, 1, 2, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 4, 5, 6, 7, 8};
    public static final String[] inNames = {"", "hydro", "helio", "lithio", "berylo", "boro", "carbo", "nitro", "oxy", "flouro", "neo", "sodio", "magno", "alumino", "silico", "phosphoro", "sulf", "chloro", "argo", "potaso", "calco", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "bromo"};
    public static String[] codes = new String[120];
    public static String[] names = new String[120];
    public final int atomicNumber;
    public final int numValence;
    public Atom(int atomicNumber) {
        this.atomicNumber = atomicNumber;
        numValence = valence[atomicNumber];
    }
    @Override
    public String toString() {
        return names[atomicNumber];
    }
    @Override
    public String toStringWithin() {
        return inNames[atomicNumber];
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof Atom) {
            return ((Atom) o).atomicNumber == atomicNumber;
        }
        return false;
    }
    @Override
    public void draw(Graphics g, double x, double y, double ang, Bond b, boolean show) {
        double centerX = x + Math.cos(ang) * 7;
        double centerY = y + Math.sin(ang) * 7;
        int leftoverV = numValence - b.getValenceUsage()[1];
        double start = ang + Math.PI;
        for (int i = 0; i < leftoverV; i++) {
            double an = start + (Math.PI * 2 / (leftoverV + 1)) * (i + 1);
            double X = centerX + 10 * Math.cos(an);
            double Y = centerY + 10 * Math.sin(an);
            g.drawRect((int) X, (int) Y, 1, 1);
        }
        int X = numValence + b.getValenceChange()[1] - 8;
        X = -X;
        String s = X < 0 ? X + "" : (X == 0 ? "" : "+" + X);
        g.drawString(codes[atomicNumber] + s, (int) centerX, (int) (centerY + 6));
    }
    public static Atom get(String name) {
        for (int i = 0; i < names.length; i++) {
            if (names[i] != null && names[i].equalsIgnoreCase(name)) {
                return new Atom(i);
            }
        }
        throw new IllegalStateException("Atom " + name + " does not exist");
    }
    public static void Imp() {
        try(BufferedReader br = new BufferedReader(new FileReader(new File("/Users/leijurv/Downloads/pt-data1.csv")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                int atomicNumber = Integer.parseInt(data[0]);
                String code = data[1].replace(" ", "");
                String name = data[2].replace(" ", "");
                System.out.println(atomicNumber + "," + code + "," + name);
                codes[atomicNumber] = code;
                names[atomicNumber] = name;
            }
        } catch (IOException e) {
            names = new String[] {"", "hydrogen", "helium", "lithium", "beryllium", "boron", "carbon", "nitrogen", "oxygen", "flourine", "neon", "sodium", "magnesium", "aluminum", "silicon", "phosphorous", "sulfur", "chlorine", "argon", "potassium", "calcium", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "bromine"};
            codes = new String[] {"", "H", "He", "Li", "Be", "B", "C", "N", "O", "F", "Ne", "Na", "Mg", "Al", "Si", "P", "S", "Cl", "Ar", "K", "Ca", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "Br"};
        }
    }
    static{
        Imp();
    }
}
