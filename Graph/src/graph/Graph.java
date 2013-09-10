/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author leif
 */
public class Graph extends JComponent{
    static Graph M=new Graph();
    int anchorID=0;
    ArrayList<Node> nodes=new ArrayList<Node>();
    ArrayList<Edge> edges=new ArrayList<Edge>();
    public void paintComponent(Graphics g){
        for (Node n : nodes){
            int[] pos=ToScreen(n.x,n.y);
            g.drawString(n.name, pos[0], pos[1]-15);
            g.fillOval(pos[0]-2, pos[1]-2, 4, 4);
        }
        for (Edge e : edges){
            int[] end1=ToScreen(nodes.get(e.nodeID1).x,nodes.get(e.nodeID1).y);
            int[] end2=ToScreen(nodes.get(e.nodeID2).x,nodes.get(e.nodeID2).y);
            g.drawLine(end1[0],end1[1],end2[0],end2[1]);
            g.drawString(e.length+","+((double)Math.round(e.getCurLength()*100))/100,(end1[0]+end2[0])/2,(end1[1]+end2[1])/2);
        }
    }
    public void normalize(){
        double x=nodes.get(anchorID).x;
        double y=nodes.get(anchorID).y;
        for (Node n : nodes){
            n.x-=x;
            n.y-=y;
        }
    }
    public void tick(){
        for (Edge e : edges){
            e.calculateTension();
            //System.out.println(e.tension);
        }
        for (Node n : nodes){
            n.calculateTension();
            //System.out.println(n.tensionX+":"+n.tensionY);
        }
        for (Node n : nodes){
            n.enactTension();
            
        }
        normalize();
        for (Node n : nodes){
            System.out.println(n);
        }
        for (Edge e : edges){
            System.out.println(e);
        }
    }
    public int[] ToScreen(double x, double y){
        return new int[] {(int)(300+x*50),(int)(300-y*50)};
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        M.edges.add(new Edge(0,1,M,3));
        M.edges.add(new Edge(1,2,M,4));
        M.edges.add(new Edge(2,3,M,5));
        M.edges.add(new Edge(3,0,M,1));
        M.edges.add(new Edge(0,2,M,3));
        M.nodes.add(new Node("A",0,1,1,M));
        M.nodes.add(new Node("B",1,0,0,M));
        M.nodes.add(new Node("C",2,1,0,M));
        M.nodes.add(new Node("D",3,0,1,M));
        /*
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();
        M.tick();*/
        setupScreen();
        // TODO code application logic here
    }
    public static void setupScreen(){
        JFrame frame=new JFrame("Conway's Game of Life");
        JButton j=new JButton("NEXT");
        j.addActionListener(new ActionListener(){public void actionPerformed (ActionEvent ae){Graph.M.tick();Graph.M.repaint();}});
	  M.setFocusable(true);
	  (frame).setContentPane(M);
	  frame.setLayout(new FlowLayout());
          frame.add(j);
	  frame.setSize(1000,700);
	  //frame.setUndecorated(true);
     frame.setExtendedState(Frame.MAXIMIZED_BOTH);
	  frame.setVisible(true);
	  frame.addWindowListener(new WindowAdapter(){
	  public void windowClosing(WindowEvent e){
		System.exit(0);
	  }
	  });
    }
}
