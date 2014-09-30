/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rauzy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author leijurv
 */
public class Rauzy {
    static JComponent M;
    static String[] words={"1","12","1213"};
    static int X=0;
    static int Y=0;
    static ArrayList<int[]> points=new ArrayList<int[]>();
    static ArrayList<Integer> color=new ArrayList<Integer>();
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        for (int i=0; i<20; i++){
            System.out.println(i);
            //System.out.println(words[2]);
            inc();
        }
        char[] d=words[2].toCharArray();
        int[] vec=new int[3];
        for (char c : d){
            switch(c){
                case '1':
                    vec[0]++;
                    break;
                case '2':
                    vec[1]++;
                    break;
                case '3':
                    vec[2]++;
            }
            
            points.add(vec);
            vec=new int[3];
            vec[0]=points.get(points.size()-1)[0];
            vec[1]=points.get(points.size()-1)[1];
            vec[2]=points.get(points.size()-1)[2];
            color.add(Integer.parseInt(""+c));
        }
        System.out.println(vec[0]+","+vec[1]+","+vec[2]);
        for (int i=points.size()-10; i<points.size()-1; i++){
            System.out.println(points.size()-i);
            vec[0]+=points.get(i)[0];
            vec[1]+=points.get(i)[1];
            vec[2]+=points.get(i)[2];
        }
        System.out.println(vec[0]+","+vec[1]+","+vec[2]);
        JFrame frame=new JFrame("Rauzy");
        frame.setSize(2000,2000);
        BufferedImage a=new BufferedImage(frame.getWidth(),frame.getHeight(),BufferedImage.TYPE_INT_RGB);
        M=new JComponent() {
            public void paintComponent(Graphics g){
                if (a!=null){
                    g.drawImage(a,0,0,null);
                }
            }
        };
        
        frame.setContentPane(M);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        frame.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e){
                p(frame.getWidth(),frame.getHeight(),a);
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseMoved(MouseEvent e){
                X=e.getX();
                Y=e.getY();
                M.repaint();
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        p(frame.getWidth(),frame.getHeight(),a);
        
        frame.setVisible(true);
    }
    public static void p(int w, int h, BufferedImage a){
        Graphics g=a.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0,0,w,h);
        for(int i=0; i<points.size(); i++){
            
                    int[] x=points.get(i);
                    double[] q=new double[3];
                    q[0]=(double)x[0];
                    q[1]=(double)x[1];
                    q[2]=(double)x[2];
                    //double[][] rot={{0.422153, -0.314169, -0.850344}, {-0.314169, 0.829189, -0.462323}, {0.850344, 0.462323, 0.251342}};
double[][] rot={{0.4221648086, -0.3141620767, -0.8503405576}, {-0.3141620767, 
  0.8291938395, -0.4623199824}, {0.8503405576, 0.4623199824, 
  0.2513586481}};         
//double Rx=(double)Y/200;
                    //double Rx=0.53;
                    
                    q=mult(rot,q[0],q[1],q[2]);
                    
                    q[0]*=100;
                    q[1]*=100;
                    if (i%10000==0)
            //System.out.println(i+","+points.size());
                    System.out.println(q[0]+","+q[1]+","+q[2]);
                    //double Ry=(double)X/2000;
                    //System.out.println(Rx+","+Ry);
                    //q=mult(new double[][] {{Math.cos(Ry),0,Math.sin(Ry)},{0,1,0},{-Math.sin(Ry),0,Math.cos(Ry)}},q[0],q[1],q[2]);
                    g.setColor(new Color(color.get(i)*50,color.get(i)*50,color.get(i)*50));
                    g.fillRect(500-(int)q[1],300-(int)q[0],1,1);
                }
        System.out.println("done");
        M.repaint();
    }
    public static void inc(){
        String res=words[2]+words[1]+words[0];
        words[0]=words[1];
        words[1]=words[2];
        words[2]=res;
    }
    public static double[] mult(double[][] mat, double x, double y, double z){
        double[] res=new double[3];
        for (int i=0; i<3; i++){
            res[i]=mat[i][0]*x+mat[i][1]*y+mat[i][2]*z;
        }
        return res;
    }
}
