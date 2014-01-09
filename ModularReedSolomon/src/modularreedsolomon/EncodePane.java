/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modularreedsolomon;

import java.awt.*;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author leijurv
 */
public class EncodePane extends JComponent{
    
    JTextField In;
    public EncodePane(){
        super();
        this.setLayout(new FlowLayout());
        In=new JTextField("cats");
        this.add(In);
        
    }
}
