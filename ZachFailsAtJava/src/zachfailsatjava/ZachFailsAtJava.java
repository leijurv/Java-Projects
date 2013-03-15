/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zachfailsatjava;

import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author leif
 */
public class ZachFailsAtJava {

    /**
     * @param args the command line arguments
     */
    public static int[] add(int[] a, int b){
        int[] c=new int[a.length+1];
        for (int i=0; i<a.length; i++){
            c[i]=a[i];
        }
        c[a.length]=b;
        return c;
    }
    public static int[][] add(int[][] a){
        int[][] c=new int[a.length+1][0];
        for (int i=0; i<a.length; i++){
            c[i]=a[i];
        }
        c[a.length]=new int[0];
        return c;
    }
    public static int[][] readfile(String filename){
        int[][] result=new int[0][0];
        try{
            FileInputStream fstream = new FileInputStream(filename);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            int i=0;
            while ((strLine = br.readLine()) != null)   {
                String[] a=strLine.split(",");
                result=add(result);
                for (String x : a){
                    result[i]=add(result[i],Integer.parseInt(x));
                }
                i++;
            }
            in.close();
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return result;
    }
    public static void main(String[] args) {
        
        int[][] grid = readfile("/matrix.txt");
int gridSize = grid[0].length;
int[] sol = new int[gridSize];
 
//initialise solution
for (int i = 0; i < gridSize; i++) {
    sol[i] = grid[i][gridSize - 1];
}
for (int i = gridSize - 2; i >= 0; i--) {
    // Traverse down
    sol[0] += grid[0][i];
    for (int j = 1; j < gridSize; j++) {
        sol[j] = min(sol[j - 1] + grid[j][i], sol[j] + grid[j][i]);
    }
 
    //Traverse up
    for (int j = gridSize - 2; j >= 0; j--) {
        sol[j] = min(sol[j], sol[j+1] + grid[j][
                i]);
    }
    System.out.println(i+":"+gridSize);
}
int min=-1;
for (int i=0; i<sol.length; i++){
    if (sol[i]>min){
        min=sol[i];
    }
}
System.out.println(min);
    }
    public static int min(int a, int b){
        return a>b?a:b;
    }
}
