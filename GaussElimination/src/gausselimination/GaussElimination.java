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
        /*double[][] x={
            {-1,3,6,8},
            {-3,3,2,4},
            {2,-2,-4,6}};*/
        //double[][] x={{2,1,11},{1,-3,2}};
        double[][] x={{2,1},{1,-3}};
        Matrix m=new Matrix(x);
        System.out.println(m.det());
        //m.print();
        //System.out.println("meow"+m.det());
        //m.inverse().print();
        //Do(m);
        //m.pseudoinverse().print();
        //Do(m);
        //m.pseudoinverse().multiply(m).print();
        //m.multiply(m.pseudoinverse()).print();
        
        
        
        //-x-3y-6z=-8
        //-3x+3y+2z=4
        //2x-2y-yz=6
    }
    
}
