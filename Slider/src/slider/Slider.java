/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package slider;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author leijurv
 */
public class Slider extends JComponent{
    static int[] pattern={1,2,1,3};
    static int[][] board=new int[4][4];
    static Random r=new Random();
    static Slider M=new Slider();
    static int ind=0;
    static int tries=1000000;
    static HashMap<Integer,Integer> h;
    public void paintComponent(Graphics g){
        g.drawString((100*ind)/tries+"%",10,10);
        double avg=0;
        int i=0;
        for (int s=2; s<3000; s*=2){
            int num=h.get(s)==null?0:h.get(s);
            avg+=((double)num)*((double)s);
            double percent=((double)num)/((double)ind)*100;
            g.drawString(s+":"+num+"    "+percent+"%",10,25+15*i++);
        }
        avg/=(double)ind;
        g.drawString("Average: "+avg,10,25+15*i);
        for (int j=0; j<4; j++){
            for (int k=0; k<4; k++){
                g.drawString(board[j][k]+"",200+k*15,200+j*15);
            }
        }
    }
    public static boolean slide(int[] x){
        boolean hasChanged=false;
        boolean done=false;
        while (!done){
            done=true;
            for (int i=1; i<x.length; i++){
                if (x[i]==0 && x[i-1]!=0){
                    done=false;
                    hasChanged=true;
                    x[i]=x[i-1];
                    x[i-1]=0;
                    break;
                }
            }
        }
        for (int i=0; i<x.length-1; i++){
            if (x[i]==x[i+1] && x[i]!=0){
                x[i+1]*=2;
                for (int n=i; n>=1; n--){
                    x[n]=x[n-1];
                }
                x[0]=0;
                i++;
                hasChanged=true;
            }
        }
        return hasChanged;
    }
    public static int[] slide(int dir, int id){
        if (dir==3){
            return board[id];
        }
        if (dir==2){
            int[] x=new int[4];
            for (int i=0; i<4; i++){
                x[i]=board[id][3-i];
            }
            return x;
        }
        if (dir==1){
            int[] x=new int[4];
            for (int i=0; i<4; i++){
                x[i]=board[i][id];
            }
            return x;
        }
        if (dir==0){
            int[] x=new int[4];
            for (int i=0; i<4; i++){
                x[i]=board[3-i][id];
            }
            return x;
        }
        return null;
    }
    public static boolean move(int dir){
        boolean hasMoved=false;
        for (int i=0; i<4; i++){
            int[] x=slide(dir,i);
            if (slide(x)){
                hasMoved=true;
            }
            if (dir==3){
                board[i]=x;
            }
            if (dir==2){
                for (int n=0; n<4; n++){
                    board[i][3-n]=x[n];
                }
            }
            if (dir==1){
                for (int n=0; n<4; n++){
                    board[n][i]=x[n];
                }
            }
            if (dir==0){
                for (int n=0; n<4; n++){
                    board[3-n][i]=x[n];
                }
            }
        }
        return hasMoved;
    }
    public static void add(){
        int num=0;
        for (int i=0; i<board.length; i++){
            for (int n=0; n<board[i].length; n++){
                if (board[i][n]==0){
                    num++;
                }
            }
        }
        int id=r.nextInt(num);
        int R=0;
        for (int i=0; i<4; i++){
            for (int n=0; n<4; n++){
                if (board[i][n]==0){
                    if (R++==id){
                        board[i][n]=r.nextInt(10)==0?4:2;
                    }
                }
            }
        }
    }
    public static boolean possible(){
        for (int i=0; i<4; i++){
            for (int n=0; n<4; n++){
                if (board[i][n]==0){
                    return true;
                }
            }
        }
        for (int i=0; i<4; i++){
            for (int n=0; n<4; n++){
                if (i!=3){
                    if (board[i][n]==board[i+1][n]){
                        return true;
                    }
                }
                if (n!=3){
                    if (board[i][n+1]==board[i][n]){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static int getMax(){
        int max=0;
        for (int i=0; i<4; i++){
            for (int n=0; n<4; n++){
                if (board[i][n]>max){
                    max=board[i][n];
                }
            }
        }
        return max;
    }
    public static void clear(){
        for (int i=0; i<4; i++){
            for (int n=0; n<4; n++){
                board[i][n]=0;
            }
        }
    }
    public static void run(){
        add();
        add();
        //print();
        String[] x={"Up","Down","Left","Right"};
        int num=0;
        boolean print=false;
        int numSkip=0;
        for (int i=0; true; i++){
            if (!possible() || num>10){
                break;
            }
            int n=pattern[(i-numSkip)%4];
            if (num>5){
                n=0;
                numSkip++;
            }
            //int n=r.nextInt(4);
            if (print){
            System.out.println();
            System.out.println(x[n]);
            System.out.println();
            }
            if (move(n)){
                add();
                num=0;
            }else{
                num++;
            }
            if (print){
            print();
            }
        }
        //print();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        JFrame frame=new JFrame("2048");
        frame.setContentPane(M);
          frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
          System.out.println(WindowConstants.EXIT_ON_CLOSE);
	  //frame.setUndecorated(true);
     frame.setExtendedState(Frame.MAXIMIZED_BOTH);
	  frame.setVisible(true);
        h=new HashMap<Integer,Integer>();
        new Thread(){
            public void run(){
                while(true){
                    try {
                        Thread.sleep(25);
                        M.repaint();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Slider.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }.start();
        for (int i=0; i<tries; i++){
            ind=i;
            clear();
            run();
            int s=getMax();
            if (h.get(s)==null){
                h.put(s,0);
            }
            h.put(s,h.get(s)+1);
        }
        
    }
    public static void print(){
        for (int[] i : board){
            for (int x : i){
                System.out.print(x+" ");
            }
            System.out.println();
        }
    }
    
}
