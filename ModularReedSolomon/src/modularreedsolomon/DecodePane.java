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
public class DecodePane extends JComponent{
    
    JTextField In;
    JButton encode;
    String result="";
    public void paintComponent(Graphics g){
        g.drawString(result,50,50);
    }
    public DecodePane(){
        super();
        this.setLayout(new FlowLayout(FlowLayout.CENTER,10,50));
        In=new JTextField("cats");
        this.add(In);
        encode=new JButton("Go");
        encode.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                String message=In.getText();
                String[] ss;ss = message.split(" ");
            int[] sss=new int[ss.length];
            for (int i=0; i<ss.length; i++){
                sss[i]=Integer.parseInt(ss[sss.length-i-1]);
            }
                result=ModularReedSolomon.decode(3,sss);
            }
        });
        this.add(encode);
        
    }
}
