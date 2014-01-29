/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_client;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author leif
 */
public class SendMoneyPane extends JComponent implements ActionListener{
    JTextField Input;
    JTextField amount;
    JButton Send;
    public SendMoneyPane(){
        setLayout(new FlowLayout());
        Send=new JButton("Send");
        Send.addActionListener(this);
        Send.setActionCommand("Send");
        Send.setFocusable(false);
        add(Send);
        Input=new JTextField(100);
        Input.setFocusable(true);
        add(Input);
        amount=new JTextField(15);
        amount.setText("Amount");
        Input.setText("Address");
        amount.setFocusable(true);
        add(amount);
        Input.addFocusListener(new FocusListener(){
            public void focusGained(FocusEvent e) {
                if (Input.getText().equals("Address"))
                Input.setText("");
            }
            public void focusLost(FocusEvent e) {
                if (Input.getText().equals("")){
                    Input.setText("Address");
                }
            }
        });
        amount.addFocusListener(new FocusListener(){
            public void focusGained(FocusEvent e) {
                if (amount.getText().equals("Amount"))
                amount.setText("");
            }
            public void focusLost(FocusEvent e) {
                if (amount.getText().equals("")){
                    amount.setText("Amount");
                }
            }
        });
    }
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("Send")){
            String contents=Input.getText();
            String Amount=amount.getText();
            BigInteger a=new BigDecimal(Amount).multiply(new BigDecimal("100000000")).toBigInteger();
            int result=JOptionPane.showConfirmDialog(null, "Are you sure you wish to send "+new BigDecimal(a).divide(new BigDecimal("100000000")).toString()+" BTC to "+AIB_Client.snip(new BigInteger(contents,16))+"?", "Are you sure?", JOptionPane.YES_NO_OPTION);
            if (result!=JOptionPane.YES_OPTION){
                return;
            }
            ArrayList<BigInteger> outAddr=new ArrayList<BigInteger>();
            ArrayList<BigInteger> outAmt=new ArrayList<BigInteger>();
            outAddr.add(new BigInteger(contents,16));
            outAmt.add(a);
            System.out.println(a);
            Transaction tx=new Transaction(outAddr,outAmt);
            
        }
    }

}
