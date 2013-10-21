/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backgammon;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author leijurv
 */
public class Backgammon extends JComponent{
    static JFrame frame;
    static Backgammon M=new Backgammon();
    static Board board;
    static Board old;
    static final Random r=new Random(1);
    static ArrayList<Integer> dice=new ArrayList<Integer>();
    static int selected=0;
    public void paintComponent(Graphics g){
        board.show(g);
    }
    public static void roll(){
        dice=new ArrayList<Integer>();
        dice.add(r.nextInt(6)+1);
        dice.add(r.nextInt(6)+1);
        selected=0;
        if (dice.get(0)==dice.get(1)){
            dice.add(dice.get(0));
            dice.add(dice.get(0));
        }
    }
    public static void NEW(){
        board.move=!board.move;
        old=board;
        board=new Board(old);
        M.repaint();
    }
    public static void move(int Pos){
        int newPos=Pos+(board.move?dice.get(selected):-dice.get(selected));
            if (board.amt[newPos]==0 || !board.color[newPos]^board.move){
                board.amt[newPos]++;
                board.color[newPos]=board.move;
                board.amt[Pos]--;
                dice.remove(selected);
                selected=0;
                M.repaint();
                return;
            }
            if (board.color[newPos]^board.move){
                board.color[newPos]=board.move;
                board.injail[board.move?0:1]++;
                board.amt[Pos]--;
                dice.remove(selected);
                selected=0;
                M.repaint();
                return;
            }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         roll();
        selected=1;
        boolean[] color={true,false,true,false,true,false,true,false,true,false,true,true,false,true,false,true,true,false,true,false,true,false,true,false};
        int[] amt={2,0,0,0,0,5,0,3,0,0,0,5,5,0,0,0,3,0,5,0,0,0,0,2};
        old=new Board(color,amt);
        board=new Board(old);
        
        
        
        
        frame = new JFrame("Backgammon");
        M.setFocusable(true);
        (frame).setContentPane(M);
        JButton Dice=new JButton("Select Dice");
        Dice.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent ae){
            if (!dice.isEmpty())
            selected=(selected+1)%dice.size();
            M.repaint();
        }
        });
        M.add(Dice);
        JButton Move=new JButton("Move");
        Move.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent ae){
            if (dice.isEmpty()){
                return;
            }
            String pos=JOptionPane.showInputDialog("What piece do you want to move? (Number, 1-24, J for jail)");
             int Pos=0;
            if (pos.equals("J")){
                if (board.injail[board.move?1:0]==0){
                    return;
                }
                System.out.println("JAILING");
                int newPos=dice.get(selected);
                newPos=newPos*(board.move?1:-1);
                newPos+=board.move?-1:24;
                System.out.println(newPos);
                if (board.amt[newPos]<2||!board.color[newPos]^board.move){
                    System.out.println("KITTENS");
                    move(board.move?-1:24);
                }
            }else{
           
            try{
                Pos=Integer.parseInt(pos);
            }
            catch(Exception e){return;}
            }
            if (board.injail[board.move?1:0]!=0){
                System.out.println("meow");
                return;
            }
            if (!board.canMove(Pos,dice.get(selected),board.move)){
                return;
            }
            move(Pos);
        }
        });
        M.add(Move);
        JButton Done=new JButton("Done");
        Done.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent ae){
            if (dice.isEmpty()){
                roll();
                NEW();
            }
        }
        });
        M.add(Done);
        frame.setLayout(new FlowLayout());
        //frame.setSize(10, 10);
        //frame.setUndecorated(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
       
        
        
    }
}
