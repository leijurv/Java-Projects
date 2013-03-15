package dad;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class Dad extends JPanel implements MouseListener{
    public Dad(){
        super();
        addMouseListener(this);
    }
    int place=0;
    @Override
    public void paintComponent(Graphics g){
        g.clearRect(0, 0, 640, 480);
        switch (place){
            case 0:
                g.drawString("Happy Birthday Dad!",320,240);
            break;
            case 1:
                g.drawString("Snowy is pregnant!",320,240);
            break;
            case 2:
                g.drawString("Was TED fun?",320,240);
            break;
            case 3:
                g.drawString("Love, Leif",320,240);
            break;
        }
    }
    @Override
    public void mousePressed(MouseEvent e) { 
        
    }
    @Override
    public void mouseReleased(MouseEvent e) { }
    @Override
    public void mouseEntered(MouseEvent e) { }
    @Override
    public void mouseExited(MouseEvent e) { }
    @Override
    public void mouseClicked(MouseEvent e) {
        place++;
        System.out.println(place);
        repaint();
    }
    
    public static void main(String[] args) {
        JFrame frame=new JFrame("Happy Birthday, Dad!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640,480);
        Dad panel=new Dad();
        panel.setFocusable(true);
        frame.setContentPane(panel);
        frame.setVisible(true);
    }
}
