/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package euler247;

import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Euler247 {
    public static double S(double x, double y){
        return 0.5*(-x-y+Math.sqrt(4+x*x-2*x*y+y*y));
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        ArrayList<double[]> used=new ArrayList<double[]>();
        ArrayList<double[]> corners=new ArrayList<double[]>();
        corners.add(new double[]{1,0,0,0});
        while(true){
            double m=0;
            int in=0;
            for (int i=0; i<corners.size(); i++){
                double d=S(corners.get(i)[0],corners.get(i)[1]);
                if (d>m){
                    m=d;
                    in=i;
                }
            }
            corners.add(new double[]{m+corners.get(in)[0],corners.get(in)[1],1+corners.get(in)[2],corners.get(in)[3]});
            corners.add(new double[]{corners.get(in)[0],m+corners.get(in)[1],corners.get(in)[2],1+corners.get(in)[3]});
            //System.out.println(corners.get(in)[1]);
            double[] x=corners.remove(in);
            //System.out.println(corners.get(in)[1]);
            //System.out.println(m);
            used.add(x);
            if (x[2]==2 || x[3]==2){
                //System.out.println((used.size()-1)+","+x[2]+","+x[3]);
            }
            if (x[2]==3 && x[3]==3){
                System.out.println("Checking"+used.size());
                if (check(corners)){
                    System.out.println("LOLDONE");
                    System.out.println(used.size());
                break;
                }
            }
            if (used.size()%1000==0){
                System.out.println("asdf"+used.size());
            }
        }
        
    }
    public static boolean check(ArrayList<double[]> corners){
        for (double[] d : corners){
            if (d[2]<3 || d[3]<3){
                System.out.println("FALSE LOL "+d[0]+","+d[1]+","+d[2]+","+d[3]);
                return false;
            }
        }
        return true;
    }
}
