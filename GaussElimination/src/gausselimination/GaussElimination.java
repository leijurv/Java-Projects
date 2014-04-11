/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gausselimination;

/**
 *
 * @author leijurv
 */
public class GaussElimination {
    public static void Do(Matrix m){
        for (int i=0; i<m.values.length; i++){
            for (int j=0; j<i; j++){
                m.addRow(i, j, -m.values[i][j]);
            }
            if (m.values[i][i]!=0)
            m.multiple(i, 1/m.values[i][i]);
        }
        int r=m.values[0].length-m.values.length+1;
        for (int i=0; i<m.values.length; i++){
            int j=m.values.length-i-1;
            for (int n=m.values[j].length-r; n>j; n--){
                m.addRow(j, n, 0-m.values[j][n]);
            }
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        double[][] x={{1,1,1},{2,2,2},{3,3,3}};
        Matrix m=new Matrix(x);
        Do(m);
        x=new double[][] {{2,1,1},{1,2,1},{3,3,2}};
        m=new Matrix(x);
        Do(m);
        x=new double[][] {{1,1,1},{1,2,1},{1,1,2}};
        m=new Matrix(x);
        Do(m);
    }
    
}
