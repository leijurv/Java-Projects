package tictactoev4;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import javax.swing.*;

public class TicTacToeV4 implements ActionListener {
    /*Instance Variables*/
    private int[][] winCombinations = new int[][] {
            {1, 2, 3}, {4, 5, 6}, {7, 8, 9}, //horizontal wins
            {1, 4, 7}, {2, 5, 8}, {3, 6, 9}, //virticle wins
            {1, 5, 9}, {3, 5, 7}             //diagonal wins
        };
    private JFrame window = new JFrame("Tic-Tac-Toe");
    private JButton buttons[] = new JButton[10];
    private int count = 0;
    private String letter = "";
    private boolean win = false;
    
    public TicTacToeV4(){
    /*Create Window*/
    window.setPreferredSize(new Dimension(300,300));
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setLayout(new GridLayout(3,3));
    
    /*Add Buttons To The Window*/
    for(int i = 1; i<=9; i++){
        buttons[i] = new JButton();
        window.add(buttons[i]);
        buttons[i].addActionListener(this);
    }
    
    /*Make The Window Visible*/
    window.setVisible(true);
    window.pack();
    }
    
    @Override
    public void actionPerformed(ActionEvent a) {


        /*Write the letter to the button and deactivate it*/
        for(int i = 1; i<= 9; i++){
            if(a.getSource() == buttons[i]){
                buttons[i].setText("X");
                buttons[i].setEnabled(false);
            }
        }
        
        count++;        
        AI();

    }
    
    public void AI(){
        //count++;
        if(buttons[1].getText().equals("O") && buttons[2].getText().equals("O") && buttons[3].getText().equals("")){
            buttons[3].setText("O");
            buttons[3].setEnabled(false);
        } else if(buttons[4].getText().equals("O") && buttons[5].getText().equals("O") && buttons[6].getText().equals("")){
            buttons[6].setText("O");
            buttons[6].setEnabled(false);
        } else if(buttons[7].getText().equals("O") && buttons[8].getText().equals("O") && buttons[9].getText().equals("")){
            buttons[9].setText("O");
            buttons[9].setEnabled(false);                
        } 
        
        else if(buttons[2].getText().equals("O") && buttons[3].getText().equals("O") && buttons[1].getText().equals("")){
            buttons[1].setText("O");
            buttons[1].setEnabled(false);                
        } else if(buttons[5].getText().equals("O") && buttons[6].getText().equals("O") && buttons[4].getText().equals("")){
            buttons[4].setText("O");
            buttons[4].setEnabled(false);                
        } else if(buttons[8].getText().equals("O") && buttons[9].getText().equals("O") && buttons[7].getText().equals("")){
            buttons[7].setText("O");
            buttons[7].setEnabled(false);                
        }
        
        else if(buttons[1].getText().equals("O") && buttons[3].getText().equals("O") && buttons[2].getText().equals("")){
            buttons[2].setText("O");
            buttons[2].setEnabled(false);                
        } else if(buttons[4].getText().equals("O") && buttons[6].getText().equals("O") && buttons[5].getText().equals("")){
            buttons[5].setText("O");
            buttons[5].setEnabled(false);                
        } else if(buttons[7].getText().equals("O") && buttons[9].getText().equals("O") && buttons[8].getText().equals("")){
            buttons[8].setText("O");
            buttons[8].setEnabled(false);                
        }
        
        else if(buttons[1].getText().equals("O") && buttons[4].getText().equals("O") && buttons[7].getText().equals("")){
            buttons[7].setText("O");
            buttons[7].setEnabled(false);                
        } else if(buttons[2].getText().equals("O") && buttons[5].getText().equals("O") && buttons[8].getText().equals("")){
            buttons[4].setText("O");
            buttons[4].setEnabled(false);                
        } else if(buttons[3].getText().equals("O") && buttons[6].getText().equals("O") && buttons[9].getText().equals("")){
            buttons[9].setText("O");
            buttons[9].setEnabled(false);                
        }
        
        else if(buttons[4].getText().equals("O") && buttons[7].getText().equals("O") && buttons[1].getText().equals("")){
            buttons[1].setText("O");
            buttons[1].setEnabled(false);                
        } else if(buttons[5].getText().equals("O") && buttons[8].getText().equals("O") && buttons[2].getText().equals("")){
            buttons[2].setText("O");
            buttons[2].setEnabled(false);                
        } else if(buttons[6].getText().equals("O") && buttons[9].getText().equals("O") && buttons[3].getText().equals("")){
            buttons[3].setText("O");
            buttons[3].setEnabled(false);                
        }
        
        else if(buttons[1].getText().equals("O") && buttons[7].getText().equals("O") && buttons[4].getText().equals("")){
            buttons[4].setText("O");
            buttons[4].setEnabled(false);                
        } else if(buttons[2].getText().equals("O") && buttons[8].getText().equals("O") && buttons[5].getText().equals("")){
            buttons[5].setText("O");
            buttons[5].setEnabled(false);                
        } else if(buttons[3].getText().equals("O") && buttons[9].getText().equals("O") && buttons[6].getText().equals("")){
            buttons[6].setText("O");
            buttons[6].setEnabled(false);                
        }
        
        else if(buttons[1].getText().equals("O") && buttons[5].getText().equals("O") && buttons[9].getText().equals("")){
            buttons[9].setText("O");
            buttons[9].setEnabled(false);                
        } else if(buttons[5].getText().equals("O") && buttons[9].getText().equals("O") && buttons[1].getText().equals("")){
            buttons[1].setText("O");
            buttons[1].setEnabled(false);                
        } else if(buttons[1].getText().equals("O") && buttons[9].getText().equals("O") && buttons[5].getText().equals("")){
            buttons[5].setText("O");
            buttons[5].setEnabled(false);                
        }
        
        else if(buttons[3].getText().equals("O") && buttons[5].getText().equals("O") && buttons[7].getText().equals("")){
            buttons[7].setText("O");
            buttons[7].setEnabled(false);                
        } else if(buttons[7].getText().equals("O") && buttons[5].getText().equals("O") && buttons[3].getText().equals("")){
            buttons[3].setText("O");
            buttons[3].setEnabled(false);                
        } else if(buttons[7].getText().equals("O") && buttons[3].getText().equals("O") && buttons[5].getText().equals("")){
            buttons[5].setText("O");
            buttons[5].setEnabled(false);                
        }
        
        else if(buttons[1].getText().equals("X") && buttons[2].getText().equals("X") && buttons[3].getText().equals("")){
            buttons[3].setText("O");
            buttons[3].setEnabled(false);
        } else if(buttons[4].getText().equals("X") && buttons[5].getText().equals("X") && buttons[6].getText().equals("")){
            buttons[6].setText("O");
            buttons[6].setEnabled(false);                
        } else if(buttons[7].getText().equals("X") && buttons[8].getText().equals("X") && buttons[9].getText().equals("")){
            buttons[9].setText("O");
            buttons[9].setEnabled(false);                
        } 
        
        else if(buttons[2].getText().equals("X") && buttons[3].getText().equals("X") && buttons[1].getText().equals("")){
            buttons[1].setText("O");
            buttons[1].setEnabled(false);                
        } else if(buttons[5].getText().equals("X") && buttons[6].getText().equals("X") && buttons[4].getText().equals("")){
            buttons[4].setText("O");
            buttons[4].setEnabled(false);                
        } else if(buttons[8].getText().equals("X") && buttons[9].getText().equals("X") && buttons[7].getText().equals("")){
            buttons[7].setText("O");
            buttons[7].setEnabled(false);                
        }
        
        else if(buttons[1].getText().equals("X") && buttons[3].getText().equals("X") && buttons[2].getText().equals("")){
            buttons[2].setText("O");
            buttons[2].setEnabled(false);                
        } else if(buttons[4].getText().equals("X") && buttons[6].getText().equals("X") && buttons[5].getText().equals("")){
            buttons[5].setText("O");
            buttons[5].setEnabled(false);                
        } else if(buttons[7].getText().equals("X") && buttons[9].getText().equals("X") && buttons[8].getText().equals("")){
            buttons[8].setText("O");
            buttons[8].setEnabled(false);                
        }
        
        else if(buttons[1].getText().equals("X") && buttons[4].getText().equals("X") && buttons[7].getText().equals("")){
            buttons[7].setText("O");
            buttons[7].setEnabled(false);                
        } else if(buttons[2].getText().equals("X") && buttons[5].getText().equals("X") && buttons[8].getText().equals("")){
            buttons[8].setText("O");
            buttons[8].setEnabled(false);                
        } else if(buttons[3].getText().equals("X") && buttons[6].getText().equals("X") && buttons[9].getText().equals("")){
            buttons[9].setText("O");
            buttons[9].setEnabled(false);                
        }
        
        else if(buttons[4].getText().equals("X") && buttons[7].getText().equals("X") && buttons[1].getText().equals("")){
            buttons[1].setText("O");
            buttons[1].setEnabled(false);                
        } else if(buttons[5].getText().equals("X") && buttons[8].getText().equals("X") && buttons[2].getText().equals("")){
            buttons[2].setText("O");
            buttons[2].setEnabled(false);                
        } else if(buttons[6].getText().equals("X") && buttons[9].getText().equals("X") && buttons[3].getText().equals("")){
            buttons[3].setText("O");
            buttons[3].setEnabled(false);                
        }
        
        else if(buttons[1].getText().equals("X") && buttons[7].getText().equals("X") && buttons[4].getText().equals("")){
            buttons[4].setText("O");
            buttons[4].setEnabled(false);                
        } else if(buttons[2].getText().equals("X") && buttons[8].getText().equals("X") && buttons[5].getText().equals("")){
            buttons[5].setText("O");
            buttons[5].setEnabled(false);                
        } else if(buttons[3].getText().equals("X") && buttons[9].getText().equals("X") && buttons[6].getText().equals("")){
            buttons[6].setText("O");
            buttons[6].setEnabled(false);                
        }
        
        else if(buttons[1].getText().equals("X") && buttons[5].getText().equals("X") && buttons[9].getText().equals("")){
            buttons[9].setText("O");
            buttons[9].setEnabled(false);                
        } else if(buttons[5].getText().equals("X") && buttons[9].getText().equals("X") && buttons[1].getText().equals("")){
            buttons[1].setText("O");
            buttons[1].setEnabled(false);                
        } else if(buttons[1].getText().equals("X") && buttons[9].getText().equals("X") && buttons[5].getText().equals("")){
            buttons[5].setText("O");
            buttons[5].setEnabled(false);                
        }
        
        else if(buttons[3].getText().equals("X") && buttons[5].getText().equals("X") && buttons[7].getText().equals("")){
            buttons[7].setText("O");
            buttons[7].setEnabled(false);                
        } else if(buttons[7].getText().equals("X") && buttons[5].getText().equals("X") && buttons[3].getText().equals("")){
            buttons[3].setText("O");
            buttons[3].setEnabled(false);                
        } else if(buttons[7].getText().equals("X") && buttons[3].getText().equals("X") && buttons[5].getText().equals("")){
            buttons[5].setText("O");
            buttons[5].setEnabled(false);                
        }
        
        else if(buttons[1].getText().equals("X") && buttons[5].getText().equals("O") && buttons[9].getText().equals("X")) {
            buttons[6].setText("O");
            buttons[6].setEnabled(false);            
        }    
        
        else if(buttons[3].getText().equals("X") && buttons[5].getText().equals("O") && buttons[7].getText().equals("X")) {
            buttons[4].setText("O");
            buttons[4].setEnabled(false);            
        }
        
        else if(buttons[5].getText().equals("")){
            buttons[5].setText("O");
            buttons[5].setEnabled(false);                
        }
        
        else if(buttons[1].getText().equals("")){
            buttons[1].setText("O");
            buttons[1].setEnabled(false);                
        }
        else {
            if(count >= 9)
                checkWin();
            else
                RandomMove();
        }
        
        checkWin();

    }
    
    public void RandomMove(){
        Random x = new Random();
        int y = 1 + x.nextInt(9);
        if(buttons[y].getText().equals("O") || buttons[y].getText().equals("X") ){
            RandomMove();
        } else {
            buttons[y].setText("O");
            buttons[y].setEnabled(false);
        }
    }
    
    public void checkWin(){ 
            
        /*Determine who won*/
        for(int i=0; i<=7; i++){
            if( buttons[winCombinations[i][0]].getText().equals(buttons[winCombinations[i][1]].getText()) && 
                buttons[winCombinations[i][1]].getText().equals(buttons[winCombinations[i][2]].getText()) && 
                !buttons[winCombinations[i][0]].getText().equals("")) {
                win = true;
            }
        }

        if(count % 2 == 1)
            letter = "O";
        else
            letter = "X";
        
        /*Show a dialog when game is over*/
        if(win == true){
            JOptionPane.showMessageDialog(null, letter + " wins!");
            System.exit(0);
        } else if(count >= 9 && win == false){
            JOptionPane.showMessageDialog(null, "The game was tie!");
            System.exit(0);
        }
    }
    public static void main(String[] args){
        new TicTacToeV4();
    }
}