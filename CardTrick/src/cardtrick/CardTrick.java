/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cardtrick;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author leijurv
 */
public class CardTrick extends JComponent implements ItemListener{
    static CardTrick M=new CardTrick();
    static String[] cards={"A","2","3","4","5","6","7","8"};
    static String[] suits={"Spades","Clubs","Diamonds","Hearts"};
    static JCheckBox[] y=new JCheckBox[5];
    static int[] T={0,0,0,0,0};
    static int[] seq={0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1};
    static boolean done=false;
    static String theAnswer="Enter the secret number to find out their card!";
    public void paintComponent(Graphics g){
        String[] r=get(T,seq);
        for (int i=0; i<5; i++){
            if (done){
                g.drawString(r[i],y[i].getX(),y[i].getY()+30);
            }
        }
        String[] n=p(seq);
        for (int i=0; i<n.length; i++){
            g.drawString(n[i],10,150+15*i);
        }
        g.drawString("Two Left",y[0].getX(),y[0].getY()+50);
        g.drawString("One Left",y[1].getX(),y[1].getY()+50);
        g.drawString("One Right",y[2].getX(),y[2].getY()+50);
        g.drawString("Two Right",y[3].getX(),y[3].getY()+50);
        g.drawString("Top",y[4].getX(),y[4].getY()+50);
        g.drawString(theAnswer,10,10);
    }
    public static String[] p(int[] x){
        int l=x.length;
        String[] result=new String[4];
        for (int i=0; i<l; i++){
            if (i%8==0){
                result[i/8]="";
            }
            result[i/8]=result[i/8]+g(i,x)+",";
            
        }
        return result;
    }
    public static String g(int i, int[] x){
        int l=x.length;
        
        return (rep(cards[(4*x[(i+2)%l])+(2*x[(i+3)%l])+x[(i+4)%l]]+" of "+suits[(2*x[i%l])+x[(i+1)%l]]));
    }
    public static String rep(String x){
        return x;
    }
    public static String[] get(int[] x,int[] w){
        int[] q={0,0,0,0,0,0,0,0,0,0};
        int n=0;
        for (int i : x){
            q[n]=i;
            n++;
        }
        int l=w.length;
        n=0;
        while((w[(n+0)%l]!=q[0]) || (w[(n+1)%l]!=q[1]) || (w[(n+2)%l]!=q[2]) || (w[(n+3)%l]!=q[3]) || (w[(n+4)%l]!=q[4])){
        n=n+1;
        }
        int s=5;
        int e=w[(n+s)%l];
        String[] r=new String[5];
    q[s]=e;
    s=s+1;
    e=w[(n+s)%l];
    q[s]=e;
    s=s+1;
    e=w[(n+s)%l];
    q[s]=e;
    s=s+1;
    e=w[(n+s)%l];
    q[s]=e;
    s=s+1;
    e=w[(n+s)%l];
    q[s]=e;
    r[0]=g(0,q);
    r[1]=g(1,q);
    r[2]=g(2,q);
    r[3]=g(3,q);
    r[4]=g(4,q);
    return r;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        JFrame frame=new JFrame("Groovy magic twick");
	  M.setFocusable(true);
	  (frame).setContentPane(M);
	  frame.setLayout(new FlowLayout());
	  frame.setSize(1000,700);
	  //frame.setUndecorated(true);
     frame.setExtendedState(Frame.MAXIMIZED_BOTH);
	  frame.setVisible(true);
	  frame.addWindowListener(new WindowAdapter(){
	  public void windowClosing(WindowEvent e){
		System.exit(0);
	  }
	  });
          for (int i=0; i<5; i++){
              y[i]=new JCheckBox("Check if Red");
              y[i].addItemListener(M);
          }
          for (int i=0; i<5; i++){
              
              M.add(y[i]);
          }
          JButton DD=new JButton("Enter Secret Number");
          DD.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent ae){
          int pos=Integer.parseInt(JOptionPane.showInputDialog("What is the secret number?"));
          
          int[] q={0,0,0,0,0,0,0,0,0,0};
        int n=0;
        for (int i : T){
            q[n]=i;
            n++;
        }
        int l=seq.length;
        n=0;
        while((seq[(n+0)%l]!=q[0]) || (seq[(n+1)%l]!=q[1]) || (seq[(n+2)%l]!=q[2]) || (seq[(n+3)%l]!=q[3]) || (seq[(n+4)%l]!=q[4])){
        n=n+1;
        }
        int indexOffset=n+5;
        
        theAnswer="The Secret Card is"+g(indexOffset+pos,seq);
        M.repaint();
          }});
          M.add(DD);
          done=true;
          M.repaint();
          
          
    }

    @Override
    public void itemStateChanged(ItemEvent ie) {
        Object o=ie.getSource();
        for (int i=0; i<y.length; i++){
            if (o==y[i]){
                T[i]=ie.getStateChange()==2?0:1;
            }
        }
        M.repaint();
        
        }
}
