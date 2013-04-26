package main;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import java.io.*;
public class Main extends JPanel
{
	private static final long serialVersionUID = 1L;
	static ArrayList<Location> current=new ArrayList<Location>();
	
	static MainPanel M=new MainPanel();
	static int gen=1;
	static int S=5;
	static int maxCol=0;
	static int maxRow=0;
	static int minCol=0;
	static int minRow=0;
	static int rowPadding=5;
	static int colPadding=5;
	static int antX=10;
	static int antY=10;
	static int antDir=0;
	public static void calcNext(){
		synchronized(current){
			
		gen++;
		//System.out.println("Calculating generation "+gen);
		
		
		
		if (current.contains(new Location(antY,antX))){
			antDir--;
			if (antDir==-1){
				antDir+=4;
			}
			current.remove(new Location(antY,antX));
		}else{
			antDir++;
			antDir%=4;
			current.add(new Location(antY,antX));
		}
		switch(antDir){
		case 0:
			antY--;
			break;
		case 01:
			antX++;
			break;

		case 02:
			antY++;
			break;

		case 03:
			antX--;
			break;
			default:
				break;
		}
	}}
	public static void makeGliderGun(){
		current.add(new Location(4,35));
		current.add(new Location(5,1));
		current.add(new Location(5,2));
		current.add(new Location(6,1));
		current.add(new Location(6,2));
		current.add(new Location(3,13));
		current.add(new Location(3,14));
		current.add(new Location(4,12));
		current.add(new Location(5,11));
		current.add(new Location(6,11));
		current.add(new Location(7,11));
		current.add(new Location(8,12));
		current.add(new Location(9,13));
		current.add(new Location(9,14));
		current.add(new Location(6,15));
		current.add(new Location(6,17));
		current.add(new Location(7,17));
		current.add(new Location(5,17));
		current.add(new Location(4,16));
		current.add(new Location(8,16));
		current.add(new Location(6,18));
		current.add(new Location(5,21));
		current.add(new Location(4,21));
		current.add(new Location(3,21));
		current.add(new Location(5,22));
		current.add(new Location(4,22));
		current.add(new Location(3,22));
		current.add(new Location(2,23));
		current.add(new Location(2,25));
		current.add(new Location(1,25));
		current.add(new Location(6,23));
		current.add(new Location(6,25));
		current.add(new Location(7,25));
		current.add(new Location(3,35));
		current.add(new Location(4,35));
		current.add(new Location(3,36));
		current.add(new Location(4,36));
	}
	public static void makeGlider(){
		current.add(new Location(0,0));
		current.add(new Location(1,1));
		current.add(new Location(1,2));
		current.add(new Location(0,2));
		current.add(new Location(-1,2));
	}
        public static void makeAcorn(){
            current.add(new Location(2,0));
            current.add(new Location(2,1));
            current.add(new Location(0,1));
            current.add(new Location(1,3));
            current.add(new Location(2,4));
            current.add(new Location(2,5));
            current.add(new Location(2,6));
        }
        public static void makeFPentomino(){
            current.add(new Location(0,1));
            current.add(new Location(0,2));
            current.add(new Location(1,0));
            current.add(new Location(1,1));
            current.add(new Location(2,1));
        }
	public static void main(String[] args){
		current.clear();
		
		//Initial state here
		//makeAcorn();//Default
		
		new Main();
	}
	public static void write(String filename, String[] things){
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter("out.txt");
			  BufferedWriter out = new BufferedWriter(fstream);
			  out.flush();
			  for (String a : things){
				  out.write(a);
				  out.write(new char[] {(char)12});
			  }
			  //Close the output stream
			  out.close();
			  }catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
			  }
	}
	public Main(){
	
	JFrame frame=new JFrame("Conway's Game of Life");
	  M.setFocusable(true);
	  (frame).setContentPane(M);
	  frame.setLayout(new FlowLayout());
	  frame.setSize(1000,700);
	  //frame.setUndecorated(true);
     frame.setExtendedState(Frame.MAXIMIZED_BOTH);
	  frame.setVisible(true);
	  frame.addWindowListener(new WindowAdapter(){
	  public void windowClosing(WindowEvent e){
		RunTicker.running=false;
		System.exit(0);
	  }
	  });
}
}
