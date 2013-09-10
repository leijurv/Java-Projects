/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author leif
 */
public class Gooey implements ChangeListener{
    JTabbedPane tabbedPane;
    JFrame frame;
    OverviewPane overviewPane;
    DepositRequestsPane dpPane;
    SendMoneyPane smp;
    public Gooey(){
        frame=new JFrame("AIB_Client");
	  tabbedPane=new JTabbedPane();
          overviewPane=new OverviewPane();
          tabbedPane.addTab("Overview", overviewPane);
          dpPane=new DepositRequestsPane();
          tabbedPane.addTab("Deposit Requests",dpPane);
          smp=new SendMoneyPane();
          tabbedPane.addTab("Send Money",smp);
          tabbedPane.addChangeListener(this);
	  frame.setSize(1000,700);
          W w=new W();
          w.setFocusable(true);
          w.setLayout(new BorderLayout());
          w.add(tabbedPane);
          frame.setContentPane(w);
          
	  //frame.setUndecorated(true);
     frame.setExtendedState(Frame.MAXIMIZED_BOTH);
	  frame.setVisible(true);
	  frame.addWindowListener(new WindowAdapter(){
	  public void windowClosing(WindowEvent e){
		System.exit(0);
	  }
	  });
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        if (ce.getSource().equals(tabbedPane)){
            if (tabbedPane.getSelectedIndex()==2){
                smp.setFocusable(true);
            }else{
                smp.setFocusable(false);
            }
        }
    }
    public class W extends JComponent implements KeyListener{
public W(){
    addKeyListener(this);
}
        

        @Override
        public void keyPressed(KeyEvent ke) {
        }

        @Override
        public void keyReleased(KeyEvent ke) {
        if (ke.getKeyCode()==KeyEvent.VK_DOWN){
            if (dpPane.selectedIndex!=AIB_Client.depositRequests.size()-1){
                dpPane.selectedIndex++;
            }
        }
        if (ke.getKeyCode()==KeyEvent.VK_UP){
            if (dpPane.selectedIndex!=0){
                dpPane.selectedIndex--;
            }
        }
        dpPane.repaint();
        }
@Override
    public void keyTyped(KeyEvent ke) {
        
    }
        
    }
}
