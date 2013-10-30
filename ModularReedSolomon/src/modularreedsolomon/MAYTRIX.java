/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modularreedsolomon;

import java.math.BigInteger;

/**
 *
 * @author leijurv
 */
public class MAYTRIX {
    int nrows;
    int ncols;
    int[][] data;

    public MAYTRIX(int[][] dat) {
        this.data = dat;
        this.nrows = dat.length;
        this.ncols = dat[0].length;
    }

    public MAYTRIX(int nrow, int ncol) {
        this.nrows = nrow;
        this.ncols = ncol;
        data = new int[nrow][ncol];
    }
    public static MAYTRIX transpose(MAYTRIX matrix) {
    MAYTRIX transposedMatrix = new MAYTRIX(matrix.ncols, matrix.nrows);
    for (int i=0;i<matrix.nrows;i++) {
        for (int j=0;j<matrix.ncols;j++) {
            transposedMatrix.data[j][i]=(matrix.data[i][j]);
        }
    }
    return transposedMatrix;
}public int getValueAt(int i ,int j){
    return data[i][j];
}
    public static int determinant(MAYTRIX matrix,int mod){
    if (matrix.nrows == 1) {
	return matrix.getValueAt(0, 0);
    }
    if (matrix.ncols==2) {
        return (matrix.getValueAt(0, 0) * matrix.getValueAt(1, 1)) - ( matrix.getValueAt(0, 1) * matrix.getValueAt(1, 0));
    }
    int sum = 0;
    for (int i=0; i<matrix.ncols; i++) {
        sum += (i%2==0?1:-1) * matrix.getValueAt(0, i) * determinant(createSubMatrix(matrix, 0, i),mod);
        sum%=mod;//This line is very important. If you're correcting 4 or more errors, you will have int overflows.
    }
    return sum;
} 
    public static MAYTRIX createSubMatrix(MAYTRIX matrix, int excluding_row, int excluding_col) {
    MAYTRIX mat = new MAYTRIX(matrix.nrows-1, matrix.ncols-1);
    int r = -1;
    for (int i=0;i<matrix.nrows;i++) {
        if (i==excluding_row)
            continue;
            r++;
            int c = -1;
        for (int j=0;j<matrix.ncols;j++) {
            if (j==excluding_col)
                continue;
            mat.data[r][++c]=matrix.getValueAt(i, j);
        }
    }
    return mat;
} 
    public static MAYTRIX cofactor(MAYTRIX matrix,int mod) {
    MAYTRIX mat = new MAYTRIX(matrix.nrows, matrix.ncols);
    for (int i=0;i<matrix.nrows;i++) {
        for (int j=0; j<matrix.ncols;j++) {
            mat.data[i][j]=((i%2==0?1:-1) * (j%2==0?1:-1) * determinant(createSubMatrix(matrix, i, j),mod));
        }
    }
    
    return mat;
}
    public static MAYTRIX inverse(MAYTRIX matrix,int mod) {
    return (transpose(cofactor(matrix,mod)).multiplyByConstant(determinant(matrix,mod),mod));
}
    public MAYTRIX multiplyByConstant(int d,int mod){
        MAYTRIX m=new MAYTRIX(nrows,ncols);
        for (int i=0; i<nrows; i++){
            for (int j=0; j<ncols; j++){
                int q=((data[i][j]*invert(d,mod))%mod+mod)%mod;
                m.data[i][j]=q;
            }
        }
        return m;
    }
    public static int invert(int a, int mod){
        return new BigInteger(a+"").modPow(new BigInteger("-1"),new BigInteger(mod+"")).intValue();
    }
}
