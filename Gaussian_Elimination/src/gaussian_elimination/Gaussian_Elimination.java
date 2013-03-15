/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaussian_elimination;

import fraction.Fraction;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leijurv
 */
public class Gaussian_Elimination {

    /**
     * @param args the command line arguments
     */
    public static void Gaussian(Matrix matrix){
        for (int i=0; i<matrix.values.length; i++){
            if (matrix.values[i][i].equalsZero()){
                //This is for if zeros on main diagonal
                //Otherwise it divides by 0
                matrix.AddRows(i,(i==0?1:0),new Fraction(1));
                //matrix.print();
            }
            for (int n=0; n<i; n++){
                if (!matrix.values[i][n].equalsZero()){
                    matrix.AddRows(i,n,matrix.values[i][n].neg().simplify());
                    //matrix.print();
                }
            }
            
            if (!matrix.values[i][i].equalsOne()){
                matrix.MultiplyRow(i, matrix.values[i][i].inv());
                //matrix.print();
            }
        }
        for (int j=0; j<matrix.values.length; j++){
            int i=matrix.values.length-j-1;
            for (int n=matrix.values[i].length-2; n>i; n--){
                matrix.AddRows(i, n, matrix.values[i][n].neg());
                //matrix.print();
            }
        }
    }
    public static class Matrix{
        public Fraction[][] values;
        public Fraction determinant(){
            if (values.length==2){
                return values[0][0].multiply(values[1][1]).add(values[0][1].multiply(values[1][0]).neg());
            }
            boolean pos=false;
            Fraction ans=new Fraction(0);
            for (int i=0; i<values.length; i++){
                Fraction[][] temp=new Fraction[values.length][values[0].length];
                for (int n=0; n<values.length; n++){
                    temp[n]=new Fraction[values[n].length-1];
                    for (int j=0; j<temp[n].length; j++){
                        temp[n][j]=new Fraction(values[n][j+1].getNum(),values[n][j+1].getDen());
                    }
                }
                for (int j=i; j<temp.length-1; j++){
                    temp[j]=temp[j+1];
                }
                Fraction[][] temp1=new Fraction[temp.length-1][0];
                System.arraycopy(temp, 0, temp1, 0, temp.length-1);
                
                
                Matrix m=new Matrix(temp1);
                //System.out.println(m);
                pos=!pos;
                Fraction r=m.determinant().multiply(values[i][0]);
                //System.out.println(r);
                if (pos){
                    
                    ans=ans.add(r);
                }else{
                    ans=ans.add(r.neg());
                }
            }
            return ans;
        }
        public Matrix(Fraction[][] Values){
            values=Values;
        }
        public void AddRows(int rowa,int rowb, Fraction coefficent){
            for (int i=0; i<values[rowa].length; i++){
                Fraction f=((values[rowb][i].multiply(coefficent)).add(values[rowa][i]));
                //System.out.print(f.Num+"/"+f.Den+"=");
                Fraction x=f.simplify();
                //System.out.println(x.Num+"/"+x.Den);
                values[rowa][i]=x;
            }
        }
        public void MultiplyRow(int row, Fraction m){
            for (int i=0; i<values[row].length; i++){
                Fraction f=(values[row][i].multiply(m));
                //System.out.print(f.Num+"/"+f.Den+"=");
                Fraction x=f.simplify();
                //System.out.println(x.Num+"/"+x.Den);
                values[row][i]=x;
            }
        }
        public String toString(){
            String res="";
            for (int i=0; i<values.length; i++){
                for (int n=0; n<values[i].length; n++){
                    res=res+values[i][n]+(n==values[i].length-1?"":",");
                }
                res=res+"\n";
            }
            return res;
        }
        public void print1(){
            System.out.println(this);}
        public void print(){
            Matrix m=this;
            int len=values.length;
            for (Fraction[] z : m.values){
            for (int i=0; i<z.length; i++){
                Fraction y=z[i];
                String u=y.toString();
                if (u.startsWith("-")){
                    System.out.print(y+((i==z.length-1)?"":defaults[len-1][i]));
                }else{
                    System.out.print(((i==0 || i==z.length-1)?"":"+")+y+((i==z.length-1)?"":defaults[len-1][i]));
                }
                if (i==z.length-2){
                    System.out.print("=");
                }
            }
            System.out.println();
            
        }
            System.out.println();
        }
        
    }
    
    static String[][] defaults={{"x"},{"x","y"},{"x","y","z"},{"w","x","y","z"},{"a","b","c","d","e"}};
    public static void main(String[] args) throws InterruptedException {
        boolean brokeJava=false;
        try{
            solve();
        }catch(Exception e){
            brokeJava=true;
             e.printStackTrace();
             e.printStackTrace();
             e.printStackTrace();
             System.out.println("Error code number 235437980573419857019234870123895472105190238547824906759208465097213498504307901734972350987490765098130466591235. \n You must read this aloud and apologize to Java! SINCERELY! It can detect sarcasm!");
             (new YOUBROKEIT(e)).start();
        }
       if (brokeJava){
           System.out.println("Error code number 235437980573419857019234870123895472105190238547824906759208465097213498504307901734972350987490765098130466591235. \n You must read this aloud and apologize to Java! SINCERELY! It can detect sarcasm!");
      
           
           System.out.println("YOU  BROKE JAVA. APOLIGIZE, OR IT WILL BITE");
           Thread.sleep(1000);
       }
    }
    public static class YOUBROKEIT extends Thread{
        Exception e;
        public YOUBROKEIT(Exception E){
            e=E;
        }
        public void run(){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                (new YOUBROKEIT(e)).start();
                }
            while(true){
                e.printStackTrace();
            }
        }
    }
    public static void solve(){
        //x+y+z+a=10
        //2x-y-3z-a=-13
        //x-y+z+a=6
        //x+3y-z-a=-1
        Scanner scan=new Scanner(System.in);
        System.out.print("How big is the matrix? >");
        String x=scan.nextLine();
        int len=Integer.parseInt(x);
        int[][] vals=new int[len][len+1];
        Fraction[][] f=new Fraction[vals.length][vals[0].length];
        Fraction[][] y=new Fraction[vals.length][vals[0].length-1];
        for (int i=1; i<=len; i++){
            System.out.print("Items of row "+i+"? (seperate by , ) >");
            String n=scan.nextLine();
            String[] a=n.split(",");
            //f[i-1]=new Fraction[a.length];
            for (int q=0; q<a.length; q++){
                f[i-1][q]=new Fraction(a[q]);
                if (q<len){
                    y[i-1][q]=new Fraction(a[q]);
                }
                
            }
            
            //Only for system of equation. Just put it in
            //System.out.print("Answer for equation "+i+"? >");
            //f[i-1][f[i-1].length-1]=new Fraction(scan.nextLine());
        }
        //int[][] vals1={{1,-2,3,1,3},{2,-1,-1,1,4},{1,2,-3,-1,1},{3,-1,1,-2,-4}};
        //vals=vals1;
        //len=4;
        
        
        //Matrix m=new Matrix(new Fraction[][]{{new Fraction(1),new Fraction(1),new Fraction(1),new Fraction(1),new Fraction(10)},{new Fraction(2),new Fraction(-1),new Fraction(-3),new Fraction(-1),new Fraction(-13)},{new Fraction(1),new Fraction(-1),new Fraction(1),new Fraction(1),new Fraction(6)},{new Fraction(1),new Fraction(3),new Fraction(-1),new Fraction(-1),new Fraction(-1)}});
        Matrix m=new Matrix(f);
        
        Matrix u=new Matrix(y);
        System.out.println(u.toString());
        System.out.println("Trimming to square...");
        System.out.println("Determinant: "+u.determinant());
        System.out.println(m);
        //m.print();
        Gaussian(m);
        System.out.println("In reduced row-echleon form:");
        System.out.println(m);
        System.out.println("If it's a linear equation:");
        for (int i=0; i<m.values.length; i++){
                System.out.println(defaults[len-1][i]+"="+m.values[i][m.values[i].length-1]);
        }
    }
}
