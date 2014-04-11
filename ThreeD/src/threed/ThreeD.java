/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package threed;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author leijurv
 */
public class ThreeD extends JComponent implements MouseMotionListener{
    static ThreeD M=new ThreeD();
    static Mesh[] render=new Mesh[2];
    static Mesh cube;
    static int x=0;
    static int y=0;
    public void paintComponent(Graphics g){
        for (Mesh m : render){
            m.render(g);
        }
    }
    public ThreeD(){
        addMouseMotionListener(this);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        cube=new Mesh();
        Vertex[][] cub={{new Vertex(-0.5,-0.5,-0.5),new Vertex(-0.5,0.5,-0.5),new Vertex(0.5,0.5,-0.5),new Vertex(0.5,-0.5,-0.5)},{new Vertex(-0.5,-0.5,-0.5),new Vertex(0.5,-0.5,-0.5),new Vertex(0.5,-0.5,0.5),new Vertex(-0.5,-0.5,0.5)},{new Vertex(-0.5,-0.5,-0.5),new Vertex(-0.5,-0.5,0.5),new Vertex(-0.5,0.5,0.5),new Vertex(-0.5,0.5,-0.5)}       ,    {new Vertex(0.5,0.5,0.5),new Vertex(0.5,0.5,-0.5),new Vertex(0.5,-0.5,-0.5),new Vertex(0.5,-0.5,0.5)}, {new Vertex(0.5,0.5,0.5),new Vertex(0.5,0.5,-0.5),new Vertex(-0.5,0.5,-0.5),new Vertex(-0.5,0.5,0.5)}, {new Vertex(0.5,0.5,0.5),new Vertex(-0.5,0.5,0.5),new Vertex(-0.5,-0.5,0.5),new Vertex(0.5,-0.5,0.5)}};
        Vertex[][] cub2=new Vertex[cub.length*2][3];
    for (int i=0; i<cub.length; i++){
        cub2[i*2]=new Vertex[] {cub[i][0],cub[i][1],cub[i][2]};
        cub2[i*2+1]=new Vertex[] {cub[i][2],cub[i][3],cub[i][0]};
    }
        cube.faces=cub2;
        
        DO();
        
        JFrame frame=new JFrame("M");
        frame.setContentPane(M);
        
        frame.setFocusable(false);
        M.setFocusable(true);
        M.requestFocusInWindow();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
	frame.setVisible(true);
        
    }
    public static void DO(){
        render[0]=cube.transform(new Transform(-0.55,0,0,((double)y)/100,((double)x)/100,0));
        render[1]=cube.transform(new Transform(0.55,0.1,0,((double)y)/100,((double)x)/100,0));
    }
    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x=e.getX();
        y=e.getY();
        DO();
        M.repaint();
    }
    
}
