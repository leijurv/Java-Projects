/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package euler96;

import java.io.*;
import java.util.*;

/**
 *
 * @author leif
 */
public class Euler96 {

    /**
     * @param args the command line arguments
     */
    static final String[] starBurstLeo={"900104002","080060070","000000000","400000001","070000030","300000007","000000000","030070080","100209004"};
    static final String[] hard1={"000000000","000003085","001020000","000507000","004000100","0900000000","500000073","002010000","000040009"};
    static final String[] hard={"120400300","300010050","006000100",
        "700090000","040603000","003002000","500080700","007000005","000000098"};
    
    public static void main(String[] args) {
        //Do();
        long l=System.currentTimeMillis();
        
        String[] x=hard;
     //  x=starBurstLeo;
        
                //int[][] matrix=file().get(0);
        int[][] matrix=parse(x);
        writeMatrix(matrix);
        if (solve(0,0,matrix)) {   // solves in place
            System.out.println(System.currentTimeMillis()-l);
            writeMatrix(matrix);
        }else
            System.out.println("NONE");
        

    }
    static void writeMatrix(int[][] solution) {
        for (int i = 0; i < 9; ++i) {
            if (i % 3 == 0)
                System.out.println(" -----------------------");
            for (int j = 0; j < 9; ++j) {
                if (j % 3 == 0) System.out.print("| ");
                System.out.print(solution[i][j] == 0
                                 ? " "
                                 : Integer.toString(solution[i][j]));

                System.out.print(' ');
            }
            System.out.println("|");
        }
        System.out.println(" -----------------------");
    }
static void Do(){
    List<int[][]> derp = file();
        int tally = 0;
        for (int[][] x : derp) {
            solve(0, 0, x);
            int add = x[0][0] * 100 + x[0][1] * 10 + x[0][2];
            tally += add;
            System.out.println(add);
        }
        System.out.println(tally);
}
    static boolean solve(int i, int j, int[][] cells) {
        if (i==1 && j==0){
            System.out.println(cells[0][0]);
        }
        if (i == 9) {
            i = 0;
            if (++j == 9) {
                return true;
            }
        }
        if (cells[i][j] != 0) // skip filled cells
        {
            return solve(i + 1, j, cells);
        }

        for (int val = 1; val <= 9; ++val) {
            if (legal(i, j, val, cells)) {// possible value
                cells[i][j] = val;
                if (solve(i + 1, j, cells)) {
                    return true;
                }
            }
        }
        cells[i][j] = 0; // reset on backtrack
        return false;
    }

    static boolean legal(int i, int j, int val, int[][] cells) {
        for (int k = 0; k < 9; ++k) // row
        {
            if (val == cells[k][j] || val == cells[i][k]) {
                return false;
            }
        }

        int boxRowOffset = (i / 3) * 3;
        int boxColOffset = (j / 3) * 3;
        for (int k = 0; k < 3; ++k) // box
        {
            for (int m = 0; m < 3; ++m) {
                if (val == cells[boxRowOffset + k][boxColOffset + m]) {
                    return false;
                }
            }
        }

        return true; // no violations, so it's legal
    }
    public static int[][] parse(String[] a) {
        int[][] x = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int n = 0; n < 9; n++) {
                if (!a[i].substring(n, n + 1).equals("0")) {
                    x[i][n] = Integer.parseInt(a[i].substring(n, n + 1));
                }
            }
        }
        return x;
    }

    public static List<int[][]> file() {
        try {
            ArrayList<String[]> patterns = new ArrayList<String[]>();
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream = new FileInputStream("/sudoku.txt");
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            int i = 0;
            String[] current = new String[9];
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                if (i % 10 != 0) {
                    current[i % 10 - 1] = strLine;
                }
                if (i % 10 == 9) {
                    patterns.add(current);
                    current = null;
                    current = new String[9];
                }
                i++;
            }
            //Close the input stream
            in.close();
            List<int[][]> l = new ArrayList<int[][]>();
            for (String[] n : patterns) {
                l.add(parse(n));
            }
            return l;
        } catch (Exception e) {//Catch exception if any
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        }

        return new ArrayList<int[][]>();
    }
}
