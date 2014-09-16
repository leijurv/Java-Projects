/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package threed;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JComboBox;
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
    static int x=0;
    static int y=0;
    static int offset=0;
    static boolean cross=true;
    public void paintComponent(Graphics g){
        long a=System.currentTimeMillis();
        offset=cross?0:(int)(M.getWidth()/7F);
        Mesh MM=new Mesh(render).transform(new Transform(0,0,0,(double)y/100,(double)x/100,0));
        MM.render(g);
        offset=!cross?0:(int)(M.getWidth()/7F);
        MM.transform(new Transform(0.2,0,0)).render(g);
        g.drawString("Render took "+(System.currentTimeMillis()-a)+"ms",10,10);
    }
    public ThreeD(){
        addMouseMotionListener(this);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //render=new Mesh("/Users/leijurv/Downloads/203164976.lrf");
        //System.out.println(render);
        Mesh.init();
        render[0]=Mesh.cube.transform(new Transform(-0.55,0,0,0,0,0,1,1,1.2));
        render[1]=Mesh.tri.transform(new Transform(-0.55,0,0.7));
        //render[1]=Mesh.cube.transform(new Transform(0.55,0.1,3));

        
        JFrame frame=new JFrame("M");
        frame.setContentPane(M);
        frame.setLayout(new FlowLayout());
        final JComboBox r=new JComboBox(new String[]{"Cross eyed","Un-cross eyed"});
        r.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                cross=r.getSelectedIndex()==0;
                M.repaint();
            }
        });
        frame.add(r);
        //frame.setFocusable(false);
        //M.setFocusable(true);
        //M.requestFocusInWindow();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(2000,2000);
	frame.setVisible(true);
        
    }
    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x=e.getX();
        y=e.getY();
        M.repaint();
    }
    
}
