/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modularreedsolomon;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author leijurv
 */
public class EncodePane extends JComponent{
    
    JTextField In;
    JButton encode;
    JComboBox errors;
    String result="";
    public void paintComponent(Graphics g){
        g.drawString(result,50,50);
    }
    public EncodePane(){
        super();
        this.setLayout(new FlowLayout(FlowLayout.CENTER,10,50));
        In=new JTextField("cats");
        this.add(In);
        errors=new JComboBox(new String[]{"1","2","3","4","5"});
        this.add(errors);
        encode=new JButton("Idiot");
        encode.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                String message=In.getText();
                int error=errors.getSelectedIndex()+1;
                System.out.println(error);
                int[] m=new int[message.length()];
                for (int i=0; i<m.length; i++){
                    for (int j=0; j<ModularReedSolomon.asdf.length; j++){
                        if (ModularReedSolomon.asdf[j]==message.charAt(message.length()-i-1)){
                            m[i]=j;
                        }
                    }
                }
                result=ModularReedSolomon.encode(error,m);
            }
        });
        this.add(encode);
        
    }
}
