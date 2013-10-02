/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_client;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    }
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("Send")){
            String contents=Input.getText();
            OtherDepositRequest R=new OtherDepositRequest(contents);
            int result=JOptionPane.showConfirmDialog(null, "Are you sure you wish to send "+new BigDecimal(R.totalValue).divide(new BigDecimal("100000000")).toString()+" BTC?", "Are you sure?", JOptionPane.YES_NO_OPTION);
            if (result!=JOptionPane.YES_OPTION){
                return;
            }
            //System.out.println(R.R2PrimeValues);
            RValueStorage Rv=AIB_Client.rValueStorage;
            if (Rv.getTotalValue().compareTo(R.totalValue)==-1){
                JOptionPane.showMessageDialog(null,"You don't have enough money!");
                return;
            }
            Transaction r=new Transaction(R,Rv);
        }
    }
}
