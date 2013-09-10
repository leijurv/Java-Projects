/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vectors;

/**
 *
 * @author leif
 */
public class Vectors {
    double xMag;
    double yMag;
    public Vectors(double XMag, double yMax, int y){
        xMag=XMag;
        yMag=yMax;
    }
    public Vectors(double angle, double mag){
        xMag=Math.cos(angle*Math.PI/180)*mag;
        yMag=Math.sin(angle*Math.PI/180)*mag;
    }
    public Vectors multiply(int scalar){
        return new Vectors(xMag*scalar,yMag*scalar,1);
    }
    public Vectors add(Vectors v){
        return new Vectors(xMag+v.xMag,yMag+v.yMag,1);
    }
    public Vectors subtract(Vectors v){
        return new Vectors(xMag-v.xMag,yMag-v.yMag,1);
    }
    public double mag(){
        return Math.sqrt(xMag*xMag+yMag*yMag);
    }
    public String toString(){
        return xMag+","+yMag;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Vectors A=new Vectors(28,44);
        Vectors B=new Vectors(56,26.5);
        Vectors C=new Vectors(270,31);
        System.out.println(A+":"+B+":"+C);
        System.out.println(B.subtract(A));
        System.out.println(A.multiply(2).subtract(B.multiply(3)).add(C.multiply(2)).mag());
    }
}
