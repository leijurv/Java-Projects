package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public final class MainPanel extends JComponent implements MouseListener,ActionListener,

ChangeListener,ItemListener{
	public static JSlider mySlider;
	private static final long serialVersionUID = 1L;
	JCheckBox unbounded;
	static boolean unbounded1=false;
	boolean showGrid=true;
	JCheckBox showgrid;
	boolean painting=true;
	public void createCheckBoxes(){
		unbounded=new JCheckBox("Unbounded");
		unbounded.addItemListener(this);
		add(unbounded);
		showgrid=new JCheckBox("Show Grid");
		showgrid.setSelected(true);
		showgrid.addItemListener(this);
		
		add(showgrid);
	}
	public void createSpeedSlider(){
		JPanel sliderPanel = new JPanel(new BorderLayout(5,5));
		mySlider = new JSlider(0,1000,500);
		mySlider.setInverted(false);
		mySlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
			
			RunTicker.setDelay(mySlider.getValue());
			}
		});
		sliderPanel.add(mySlider,BorderLayout.NORTH);
		sliderPanel.add(new JLabel("  Faster"),BorderLayout.WEST);
		JPanel speedPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		speedPanel.add(new JLabel("Life Cycle Speed"));
		sliderPanel.add(speedPanel, BorderLayout.CENTER);
		sliderPanel.add(new JLabel("Slower   "),BorderLayout.EAST);
		add(sliderPanel);
	}
	public void createZoomSlider(){
		//Create the slider.
	    JSlider framesPerSecond = new JSlider(JSlider.HORIZONTAL,
                1, 30, 5);


framesPerSecond.addChangeListener(this);

//Turn on labels at major tick marks.

framesPerSecond.setMajorTickSpacing(10);
framesPerSecond.setMinorTickSpacing(1);
framesPerSecond.setPaintTicks(true);
framesPerSecond.setPaintLabels(true);
framesPerSecond.setBorder(
BorderFactory.createEmptyBorder(0,0,10,0));
Font font = new Font("Serif", Font.ITALIC, 15);JLabel sliderLabel = new JLabel("Zoom", JLabel.CENTER);
sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
framesPerSecond.setFont(font);add(sliderLabel);
add(framesPerSecond);
	}
	public void createButtons(){
		JButton b1=new JButton("Next");
		JButton b2=new JButton("Start");
		JButton b3=new JButton("Stop");
		JButton b4=new JButton("Clear");
		b1.setVerticalTextPosition(AbstractButton.CENTER);
	    b1.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
	    b2.setVerticalTextPosition(AbstractButton.BOTTOM);
	    b2.setHorizontalTextPosition(AbstractButton.CENTER);
	    b3.setVerticalTextPosition(AbstractButton.BOTTOM);
	    b3.setHorizontalTextPosition(AbstractButton.CENTER);
	    b4.setVerticalTextPosition(AbstractButton.BOTTOM);
	    b4.setHorizontalTextPosition(AbstractButton.CENTER);
	    b1.setActionCommand("disable");
	    b2.setActionCommand("start");
	    b2.addActionListener(this);
	    b3.setActionCommand("stop");
	    b3.addActionListener(this);
	    b1.addActionListener(this);
	    b4.setActionCommand("Clear");
	    b4.addActionListener(this);
	    add(b1);
	    add(b2);
	    add(b3);
	    add(b4);
	}
	public MainPanel(){
		createCheckBoxes();
		createSpeedSlider();
		createZoomSlider();
		createButtons();
		addMouseListener(this);
    }
	public boolean ip(){
		synchronized((Object)painting){
			return painting;
		}
	}
	public void paintComponent(Graphics g){
		
		g.drawString("Generation: "+Main.gen,10,10);
		
		Main.maxRow=80;
		Main.maxCol=100;
		 Main.minRow=0;
		 Main.minCol=0;
		
		 synchronized(Main.current){
		 if (unbounded1){
			 Main.minRow=Main.rowPadding;
			 Main.minCol=Main.colPadding;
			 Main.maxRow=Main.current.size()==0?Main.maxRow:Main.current.get(0).Row;
			 Main.maxCol=Main.current.size()==0?Main.maxCol:Main.current.get(0).Col;
			 //Main.minRow=Main.maxRow;
			 //Main.minCol=Main.maxCol;
			 for (Location on:Main.current){
			//Systeout.println(on);
			if (on.Row<Main.minRow){
				Main.minRow=on.Row;
			}
			if (on.Col<Main.minCol){
				Main.minCol=on.Col;
			}
			if (on.Row>Main.maxRow){
				Main.maxRow=on.Row;
			}
			if (on.Col>Main.maxCol){
				Main.maxCol=on.Col;
			}
		}
		Main.minRow-=Main.rowPadding;
		Main.maxRow+=Main.rowPadding;
		Main.minCol-=Main.colPadding;
		Main.maxCol+=Main.colPadding;
		}
		 g.drawString("Display window:", 10, 40);
		 g.drawString("Max Row:"+Main.maxRow+", Min Row:" +Main.minRow,10,55);
		 g.drawString("Max Col:"+Main.maxCol+", Min Col:" +Main.minCol,10,70);
		 g.setColor(Color.BLACK);
		 if (showGrid){
		for (int row=Main.minRow; row<=Main.maxRow; row++){
			for (int col=Main.minCol; col<=Main.maxCol; col++){
				int R=Main.S*(row-Main.minRow) +80;
				int C=Main.S*(col-Main.minCol) +80;
					g.drawRect(C,R,Main.S,Main.S);
				}
			}
			
			//Systeout.println(q);
		}
			
                 for (Location l : Main.current){
				int R=Main.S*(l.Row-Main.minRow) +80;
				int C=Main.S*(l.Col-Main.minCol) +80;
				if (unbounded1){
				g.fillRect(C,R,Main.S,Main.S);
				}else{
					if (!(l.Row>Main.maxRow || l.Row<Main.minRow || l.Col<Main.minCol || l.Col>Main.maxCol)){
						g.fillRect(C,R,Main.S,Main.S);
					}
				}
			}
		
		 
		}
		synchronized((Object)painting){
		painting=false;
		}
	}
	public void mouseClicked(MouseEvent arg0) {
		int x=arg0.getX();
		int y=arg0.getY();
		x-=80;
		y-=80;
		x/=Main.S;
		y/=Main.S;
		x+=Main.minCol;
		y+=Main.minRow;
		Location l=new Location(y,x);
		synchronized(Main.current){
		if (Main.current.contains(l)){
			Main.current.remove(l);
		}else{
			Main.current.add(l);
		}
		}
		repaint();
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		//Systeout.println("DERP");
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
	    if (!source.getValueIsAdjusting()) {
	        int fps = (int)source.getValue();
	        Main.S=fps;
	        repaint();
	    }
		
	}
	
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		if (source==unbounded){
			if (e.getStateChange() == ItemEvent.DESELECTED) {
	            unbounded1=false;
	        }else{
	        	unbounded1=true;
	        }
			repaint();
		}
		if (source==showgrid){
			if (e.getStateChange()==ItemEvent.DESELECTED){
				showGrid=false;
			}else{
				showGrid=true;
			}
			repaint();
		}
	
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		
		 if ("disable".equals(e.getActionCommand())) {Main.calcNext();repaint();}
		 if ("start".equals(e.getActionCommand())){
			 System.out.println("Starting...");
			 if (!RunTicker.running){
				 (new RunTicker()).start();
			 }
		 }
		 if ("stop".equals(e.getActionCommand())){
			 System.out.println("Stopping...");
			 RunTicker.running=false;
		 }
		 if ("Clear".equals(e.getActionCommand())){
			 Main.current.clear();
			 repaint();
			 
		 }
	}
}