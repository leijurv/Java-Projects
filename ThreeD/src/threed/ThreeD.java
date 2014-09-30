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
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
/**
 *
 * @author leijurv
 */
public class ThreeD extends JComponent implements MouseMotionListener,KeyListener,MouseListener {
    static ThreeD M=new ThreeD();
    static Mesh[] render=new Mesh[3];
    static Mesh MM;
    static double MouseX=0;
    static double MouseY=0;
    static boolean mouseCon=true;
    static double x=0;
    static double y=0;
    static double z=2;
    static int offset=0;
    static boolean cross=true;
    
    public void paintComponent(Graphics g){
        long a=System.currentTimeMillis();
        offset=cross ? 0 : (int) (M.getWidth()/7F);
        double mouseX=MouseX;
        double mouseY=MouseY;
        mouseX/=200;
        mouseY/=400;
        if (mouseY>Math.PI/3){
            mouseY=Math.PI/3;
        }
        if (mouseY<0){
            mouseY=0;
        }
        MM=new Mesh(render).transform(new Transform(0,0,0,mouseY,mouseX,0)).transform(new Transform(x,y,z));
        MM.render(g);
        if (mouseCon){
        offset=!cross ? 0 : (int) (M.getWidth()/6F);
        MM.transform(new Transform(0.2,0,0)).render(g);
        }
        g.drawString("Render took "+(System.currentTimeMillis()-a)+"ms",10,10);
    }
    public ThreeD(){
        addMouseMotionListener(this);
        addMouseListener(this);
        //addKeyListener(this);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        //System.out.println(180D/Math.PI*Mesh.angle(new Vertex(0,0,1),new Vertex(0,1,0)));
        //render=new Mesh("/Users/leijurv/Downloads/203164976.lrf");
        //System.out.println(render);
        Mesh.init();
        render[0]=Mesh.cube.transform(new Transform(-0.55,-1,0,0,0,0,1,1,1.2));
        render[1]=Mesh.tri.transform(new Transform(-0.55,-1,0.7));
        //render[0]=new Mesh();
        render[2]=Mesh.plane.transform(new Transform(0,0,0,0,0,0,0.1,1,0.1));
       //render[1]=Mesh.cube.transform(new Transform(0.55,0.1,3));
        render[0].light=new Mesh().light;
        JFrame frame=new JFrame("M");
        frame.setContentPane(M);
        frame.setLayout(new FlowLayout());
        final JComboBox<String> r=new JComboBox<>(new String[]{"Cross eyed","Paralell eyed"});
        r.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
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
        new Thread(){
            public void run(){
                while(true){
                    if (pres[0]){
                        x+=0.01;
                    }
                    if (pres[1]){
                        z-=0.01;
                    }
                    if (pres[2]){
                        x-=0.01;
                    }
                    if (pres[3]){
                        z+=0.01;
                    }
                    if (pres[0] || pres[1] || pres[2] || pres[3]){
                        M.repaint();
                    }
                    //System.out.println(render[0].light[0]+","+render[0].light[1]);
                    try{
                        Thread.sleep(100);
                    } catch (InterruptedException ex){
                        Logger.getLogger(ThreeD.class.getName()).log(Level.SEVERE,null,ex);
                    }
                }
            }
        }.start();
    }
    
    static boolean[] pres=new boolean[4];
    static boolean dragging=false;
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
        //M.repaint();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void keyReleased(KeyEvent e){
        int c=conv(e.getKeyChar());
        if (c!=-1){
            pres[c]=false;
        }
        //M.repaint();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public int conv(char c){
        switch (c){
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
@Override
    public void mouseDragged(MouseEvent e){
        //System.out.println(e);
        if (dragging && !mouseCon && lightInd!=-1){
            double X=(double)(e.getX());
            double Y=e.getY();
            X=X-300;
            Y=Y-300;
            X=X/10;
            Y=Y/10;
            X=X/(zCor+2);
            Y=Y/(zCor+2);
            double mouseX=MouseX;
        double mouseY=MouseY;
        mouseX/=200;
        mouseY/=400;
        if (mouseY>Math.PI/3){
            mouseY=Math.PI/3;
        }
        if (mouseY<0){
            mouseY=0;
        }
        MM.light[lightInd].x=X;
        MM.light[lightInd].y=Y;
            Mesh orig=MM.transform(new Transform(-x,-y,-z)).transform(new Transform(0,0,0,-mouseY,0,0)).transform(new Transform(0,0,0,0,-mouseX,0));
            render[0].light[lightInd]=new Vertex(orig.light[lightInd]);
            M.repaint();
            //System.out.println(e.getX()+","+e.getY());
        }
    }
    @Override
    public void mouseMoved(MouseEvent e){
        if (mouseCon){
        MouseX=e.getX();
        MouseY=e.getY();
        M.repaint();
        return;
        }
        
    }
    @Override
    public void mouseClicked(MouseEvent e){
        if (e.getButton()==3){
            mouseCon=!mouseCon;
        }
        M.repaint();
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    static double zCor=-1;
    static int lightInd=-1;
    @Override
    public void mousePressed(MouseEvent e){
        if (!mouseCon && e.getButton()==1){
            offset=0;
        double[] d=MM.lightDist(e.getX(),e.getY());
        if (d[0]<20){
        lightInd=(int) d[1];
        zCor=MM.light[lightInd].z;
        }else{
            lightInd=-1;
        }
        System.out.println(d[0]+","+d[1]);
        }
        dragging=true;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e){
        dragging=false;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e){
        dragging=false;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e){
        dragging=false;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
