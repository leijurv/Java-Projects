/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maththing;

import java.util.Scanner;

/**
 *
 * @author leijurv
 */
public class MathTHing {

    /**
     * @param args the command line arguments
     */
    static int t=20;//Width is 2t+1
    static int[][] v;
    public static int g(int x, int y){
        if (v[x+t][y+t]!=0){
            return v[x+t][y+t];
        }
        int r=(y==-1)?2:1;
        if (x>0 && y>-x && y<=x){
            r=g(x,y-1)+1;
        }
        if (y<0 && x>y+1 && x<=-y){
            r=g(x-1,y)+1;
        }
        if (x<0 && y>=x-1 && y<-x){
            r=g(x,y+1)+1;
        }
        if (y>0 && x>=-y && x<y){
            r=g(x+1,y)+1;
        }
        if (v[x+t][y+t]==0){
            v[x+t][y+t]=r;
        }
        return r;
    }
    public static void q(){
        for (int y=t; y>=-t; y--){
            for (int x=t; x>-t; x--){
                int c=g(-x,y);
                System.out.print(c);
                if (c<=9){
                    System.out.print(" ");
                }
                if (c<=99){
                    System.out.print(" ");
                }
                if (c<=999){
                    System.out.print(" ");
                }
                System.out.print(" ");
            }
            System.out.println(g(t,y));
        }
    }
    public static void main(String[] args) {
        Scanner scan=new Scanner(System.in);
        System.out.print("What is the width? (Actual width will be one more than twice your answer):");
        t=Integer.parseInt(scan.nextLine());
        v=new int[2*t+1][2*t+1];
        q();
    }
}
