/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scom;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


/**
 *
 * @author leif
 */
public class MainPanel extends JComponent implements DocumentListener,KeyListener{
    JTextField entry;
    public MainPanel(){
        JTextArea textArea=new JTextArea();
         textArea.setColumns(20);
        textArea.setLineWrap(true);
        textArea.setRows(5);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        add(textArea);
        entry = new JTextField(10);
        entry.addKeyListener(this);
        add(entry);
        
    }

    @Override
    public void insertUpdate(DocumentEvent de) {
        
    }

    @Override
    public void removeUpdate(DocumentEvent de) {
    }

    @Override
    public void changedUpdate(DocumentEvent de) {
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        System.out.println(ke.getKeyCode());
        if (ke.getKeyCode()==KeyEvent.VK_ENTER){
            System.out.println("Enter");
        }
                
    }
}
