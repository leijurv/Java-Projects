/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loans;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 *
 * @author leijurv
 */
public class Loans extends JComponent{
    static ArrayList<Loan> loans=new ArrayList<Loan>();
    static ArrayList<String> people=new ArrayList<String>();
    static Loans M=new Loans();
    static JComboBox peopleC;
    static final long msInDay=1000*2;
    static int sel=0;
    static final File save=new File(System.getProperty("user.home")+"/Library/loans.cat");
    static boolean showPaid=false;
    static ArrayList<Loan> toDisplay=new ArrayList<Loan>();
    public void paintComponent(Graphics g){
        toDisplay=new ArrayList<Loan>();
        if (peopleC.getSelectedIndex()==0){
            for (Loan a : loans){
                if (!a.paid || !a.paid^showPaid){
                    toDisplay.add(a);
                }
            }
        }else{
            for (Loan a : loans){
                if (a.person==peopleC.getSelectedIndex() && (!a.paid || !a.paid^showPaid)){
                    toDisplay.add(a);
                }
            }
        }
        if (sel>=toDisplay.size()){
            sel=toDisplay.size();
        }
        if (sel<0){
            sel=0;
        }
        int total=0;
        for (Loan a : toDisplay){
            total+=a.getCurrentValue();
        }
        int Y=150;
        int YY=30;
        g.drawString("Total amount due: $"+((float)(total))/100F,M.getWidth()/2,Y-30);
        g.drawString("Often: How often interest applied (days)",10,YY+=15);
        g.drawString("Until: Days from start until first interest applied",10,YY+=15);
        g.drawString("Since: Days since start",10,YY+=15);
        g.drawString("Applied: Number of times interest has been applied",10,YY+=15);
        g.drawString("Original Date",50,Y);
        g.drawString("Original Amount", 250, Y);
        g.drawString("Rate",400,Y);
        g.drawString("Often",450,Y);
        g.drawString("Until",500,Y);
        g.drawString("Since",550,Y);
        g.drawString("Applied",600,Y);
        g.drawString("Current Amount Due",675,Y);
        for (int i=0; i<toDisplay.size(); i++){
            Loan a=toDisplay.get(i);
            int y=Y+20+i*14;
            if (i==sel){
                g.setColor(new Color(100,100,255));
                g.fillRect(40, 107+i*14, 1000, 14);
                g.setColor(Color.BLACK);
                
            }
            
            
            g.drawString(new Date(a.date).toString(),50,y);
            g.drawString("$"+((float)(a.origAmount))/100F,250,y);
            g.drawString(a.intRate*100+"%",400,y);
            g.drawString((float)a.per/(float)msInDay+"",450,y);
            g.drawString((float)a.until/(float)msInDay+"",500,y);
            String s=(((float)System.currentTimeMillis()-a.date) / (float)msInDay+"");
            g.drawString(s.substring(0,Math.min(4,s.length())),550,y);
            g.drawString(a.applied(System.currentTimeMillis())+"",600,y);
            g.drawString("$"+((float)(a.getCurrentValue()))/100F,675,y);
            
        }
    }
    public static void save(){
        try {
            DataOutputStream f=new DataOutputStream(new FileOutputStream(save));
            f.writeInt(people.size());
            for (String s : people){
                f.writeUTF(s);
            }
            f.writeInt(loans.size());
            for (Loan l : loans){
                l.write(f);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
    public static void read(){
        try{
            DataInputStream f=new DataInputStream(new FileInputStream(save));
            int p=f.readInt();
            people=new ArrayList<String>(p);
            for (int i=0; i<p; i++){
                people.add(f.readUTF());
            }
            int l=f.readInt();
            loans=new ArrayList<Loan>(l);
            for (int i=0; i<l; i++){
                loans.add(new Loan(f));
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
    public static void updateCombo(){
        peopleC.removeAllItems();
        peopleC.addItem("All");
        for (int i=0; i<people.size(); i++){
            peopleC.addItem(people.get(i));
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        read();
        loans.get(0).paid=true;
        JFrame frame=new JFrame("Loans");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(2000,2000);
        frame.setContentPane(M);
        frame.setLayout(new FlowLayout());
        frame.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                int a=(e.getY()-116)/14;
                if (a<=0){
                    return;
                }
                if (a>toDisplay.size()){
                    return;
                }
                sel=a-1;
                M.repaint();
                        //107+i*15
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mousePressed(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        JButton newLoan=new JButton("New Loan for All");
        peopleC=new JComboBox();
        updateCombo();
        peopleC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                M.repaint();
                newLoan.setText("New Loan for "+peopleC.getSelectedItem());
            }
        });
        JButton newPerson=new JButton("New Person");
        newPerson.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String name=JOptionPane.showInputDialog("Name?");
                people.add(name);
                updateCombo();
                save();
            }
        });
        newLoan.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int amt=Integer.parseInt(JOptionPane.showInputDialog("How much money? (in cents)"));
                float rate=Float.parseFloat(JOptionPane.showInputDialog("Interest rate? (percentage)"));
                long until=(long)(Float.parseFloat(JOptionPane.showInputDialog("How many days until first interest application? (decimals okay)"))* (float)msInDay);
                long per=(long)(Float.parseFloat(JOptionPane.showInputDialog("How often should interest be applied? (in days, decimals okay)"))* (float)msInDay);
                System.out.println(amt+","+rate+","+per/msInDay+","+until/msInDay);
                Loan l=new Loan(peopleC.getSelectedIndex(),amt,rate,per,until);
                //l.date-=3*msInDay;
                loans.add(l);
                M.repaint();
                save();
            }
        });
        JButton delete=new JButton("Delete");
        delete.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!toDisplay.isEmpty()){
                    loans.remove(toDisplay.get(sel));
                    M.repaint();
                    save();
                }
            }
        });
        JButton payFull=new JButton("Apply full payment");
        payFull.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!toDisplay.isEmpty()){
                    toDisplay.get(sel).paid=true;
                    M.repaint();
                    save();
                }
            }
        });
        JCheckBox d=new JCheckBox("Show paid loans",false);
        
        d.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showPaid=!showPaid;
                d.setSelected(showPaid);
                M.repaint();
            }
        });
        JButton k=new JButton("Kill person");
        k.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                people.remove(people.get(peopleC.getSelectedIndex()-1));
                M.repaint();
                save();
                updateCombo();
            }
        });
        frame.add(newPerson);
        frame.add(k);
        frame.add(peopleC);
        frame.add(d);
        frame.add(newLoan);
        frame.add(delete);
        frame.add(payFull);
        frame.setVisible(true);
        
        // TODO code application logic here
    }
    
}
