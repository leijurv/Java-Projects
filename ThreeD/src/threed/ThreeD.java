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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
public class ThreeD extends JComponent implements MouseMotionListener,KeyListener{
    static ThreeD M=new ThreeD();
    static Mesh[] render=new Mesh[3];
    static double mouseX=0;
    static double mouseY=0;
    static double x=0;
    static double y=0;
    static double z=2;
    static int offset=0;
    static boolean cross=true;
    public void paintComponent(Graphics g){
        long a=System.currentTimeMillis();
        offset=cross?0:(int)(M.getWidth()/7F);
        mouseX/=400;
        mouseY/=400;
        if (mouseY>Math.PI/3){
            mouseY=Math.PI/3;
        }
        if (mouseY<0){
            mouseY=0;
        }
        Mesh MM=new Mesh(render).transform(new Transform(0,0,0,mouseY,mouseX,0)).transform(new Transform(x,y,z));
        MM.render(g);
        offset=!cross?0:(int)(M.getWidth()/7F);
        MM.transform(new Transform(0.2,0,0)).render(g);
        g.drawString("Render took "+(System.currentTimeMillis()-a)+"ms"+pres[0]+pres[1]+pres[2]+pres[3],10,10);
    }
    public ThreeD(){
        addMouseMotionListener(this);
        //addKeyListener(this);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //render=new Mesh("/Users/leijurv/Downloads/203164976.lrf");
        //System.out.println(render);
        Mesh.init();
        render[0]=Mesh.cube.transform(new Transform(-0.55,-1,0,0,0,0,1,1,1.2));
        render[1]=Mesh.tri.transform(new Transform(-0.55,-1,0.7));
        render[2]=Mesh.plane.transform(new Transform(0,0,0,0,0,0,0.1,1,0.1));
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
        r.setFocusable(false);
        frame.add(r);
        frame.addKeyListener(M);
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
        mouseX=e.getX();
        mouseY=e.getY();
        M.repaint();
    }
    static boolean[] pres=new boolean[4];
    @Override
    public void keyTyped(KeyEvent e){
        
        //System.out.println(e.getKeyChar());
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyPressed(KeyEvent e){
        int c=conv(e.getKeyChar());
        if (c!=-1){
            pres[c]=true;
        }
        M.repaint();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyReleased(KeyEvent e){
        int c=conv(e.getKeyChar());
        if (c!=-1){
            pres[c]=false;
        }
        M.repaint();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public int conv(char c){
        switch(c){
            case 'w':
                return 0;
            case 'a':
                return 1;
            case 's':
                return 2;
            case 'd':
                return 3;
        }
        return -1;
    }
}
