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
public class Matrix {
    double[][] values;
    public Matrix(double[][] r){
        values=r;
    }
    public Matrix(Matrix m){
        values=new double[m.values.length][];
        for (int i=0; i<m.values.length; i++){
            values[i]=new double[m.values[i].length];
            for (int j=0; j<m.values[i].length; j++){
                values[i][j]=m.values[i][j];
            }
        }
    }
    public void addRow(int rowA, int rowB, double multiple){
        System.out.println("Adding "+multiple+" times row "+rowB+" to row "+rowA);
        for (int i=0; i<values[rowA].length; i++){
            values[rowA][i]+=values[rowB][i]*multiple;
        }
        print();
    }
    public void multiple(int row, double multiple){
        System.out.println("Multiplying row "+row+" by "+multiple);
        for (int i=0; i<values[row].length; i++){
            values[row][i]*=multiple;
        }
        print();
    }
    public void print(){
        for (int i=0; i<values.length; i++){
            for (int j=0; j<values[i].length; j++){
                System.out.print(values[i][j]+" ");
            }
            System.out.println();
        }
    }
    public double det(){
        if (values.length==1)
            return values[0][0];
        double sum=0;
        for (int i=0; i<values[0].length; i++){
            sum+=cofactor(0,i)*values[0][i];
        }
        return sum;
    }
    public double cofactor(int row, int col){
        int r;
        int c;
        double[][] N=new double[values.length-1][values.length-1];
            for (int j=c=0; j<values.length; j++)
                if (j!=row){
                    for (int k=r=0; k<values.length; k++)
                        if (k!=col)
                            N[c][r++]=values[j][k];
                    c++;
                }
            
            return new Matrix(N).det()*((row+col)%2==0?1:-1);
    }
    public Matrix inverse(){
        double[][] cofactor=new double[values.length][values.length];
        double det=det();
        for (int i=0; i<values.length; i++){
            for (int j=0; j<values.length; j++){
                cofactor[j][i]=cofactor(i,j)/det;
            }
        }
        return new Matrix(cofactor);
    }
    public Matrix transpose(){
        double[][] r=new double[values[0].length][values.length];
        for (int i=0; i<values.length; i++){
            for (int j=0; j<values[0].length; j++){
                r[j][i]=values[i][j];
            }
        }
        return new Matrix(r);
    }
    public Matrix pseudoinverse(){
return transpose().multiply(multiply(transpose()).inverse());        
//return transpose().multiply(this).inverse().multiply(transpose());
    }
    public Matrix multiply(Matrix m){
        double[][] result=new double[values.length][m.values[0].length];
        for (int i=0; i<result.length; i++){
            for (int j=0; j<result[0].length; j++){
                double sum=0;
                for (int k=0; k<m.values.length; k++){
                    sum+=values[i][k]*m.values[k][j];
                }
                result[i][j]=sum;
            }
        }
        return new Matrix(result);
    }
}
