/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rendergui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 *
 * @author leijurv
 */
public class RenderGUI extends JPanel implements KeyListener,MouseListener{

    
    public static class Angle{
        double xyangle;
        double zangle;
        public Angle(double x, double y){
            xyangle=x;
            zangle=y;
        }
        public void print(){
            System.out.print(xyangle);
            System.out.print(",");
            System.out.println(zangle);
        }
    }
    public static class Vertex{
        double x;
        double y;
        double z;
        public Vertex(double ecks, double why, double zee){
            x=ecks;
            y=why;
            z=zee;
        }
        public void print(){
            System.out.print("(");
            System.out.print(x);
            System.out.print(",");
            System.out.print(y);
            System.out.print(",");
            System.out.print(z);
            System.out.println(")");
        }
        public double distance(Vertex p2){
            double x1=p2.x;
            double y1=p2.y;
            double z1=p2.z;
            double xd=Math.abs(x-x1);
            double yd=Math.abs(y-y1);
            double zd=Math.abs(z-z1);
            double xyd=Math.sqrt((xd*xd)+(yd*yd));
            double d=Math.sqrt((xyd*xyd)+(zd*zd));
            return d;
        }
        public Angle angle(Vertex point){
            double x1=point.x;
            double y1=point.y;
            double z1=point.z;
            double xd=Math.abs(x-x1);
            double yd=Math.abs(y-y1);
            double zd=Math.abs(z-z1);
            double xyangle=0;
            if (xd==0){
                xyangle=0;
            }else{
                xyangle=Math.atan(yd/xd);
                if (xyangle>Math.PI/4){
                    xyangle=Math.PI/2-xyangle;
                }
            }
            double xyd=Math.sqrt((xd*xd)+(yd*yd));
            double zangle=Math.atan(zd/xyd);
            Angle ans=new Angle(xyangle,zangle);
            if (z1<z){
                ans.zangle=0-ans.zangle;
            }
            if (x1<x){
                ans.xyangle=0-ans.xyangle;
            }
            return ans;
        }
    }
    public static class Polygon{
        Vertex[] verticies=new Vertex[0];
        public Polygon(){
        }
        public Vertex[] add(Vertex[] a, Vertex b){
            Vertex[] c=new Vertex[a.length+1];
            System.arraycopy(a, 0, c, 0, a.length);
            c[a.length]=b;
            return c;
        }
        public void addvertex(Vertex p){
            verticies=add(verticies,p);
        }
        public void addpoint(double x, double y, double z){
            Vertex n=new Vertex(x,y,z);
            addvertex(n);
        }
        public void print(){
            for (int i=0; i<verticies.length; i++){
                verticies[i].print();
            }
        }
    }
    public static class Screen{
        double screendistance=10;
        Angle camerangle=new Angle(0,0);
        point2D[] points=new point2D[0];
        double[] distances=new double[0];
        Vertex camerapos;
        public Screen(){
            camerapos=new Vertex(0,0,0);
        }
        public point2D projectangle(Angle angle){
            double dxyangle=(angle.xyangle-camerangle.xyangle);
            double dzangle=(angle.zangle-camerangle.zangle);
            System.out.println(angle.zangle);
            double x=screendistance*Math.tan(dxyangle);
            double dist=Math.sqrt((x*x)+(screendistance*screendistance));
            double y=dist*Math.tan(dzangle);
            point2D ans=new point2D(x,y);
            return ans;
        }
        public point2D project(Vertex p){
            //camerapos.angle(p).print();
            return projectangle(camerapos.angle(p));
        }  
        public point2D[] add(point2D[] a, point2D b){
            point2D[] c=new point2D[a.length+1];
            System.arraycopy(a, 0, c, 0, a.length);
            c[a.length]=b;
            return c;
        }
        public double[] addd(double[] a, double b){
            double[] c=new double[a.length+1];
            System.arraycopy(a, 0, c, 0, a.length);
            c[a.length]=b;
            return c;
        }
        public void placepoint(point2D p){
            points=add(points,p);
        }
        public point2D drawvertex(Vertex p){
            placepoint(project(p));
            distances=addd(distances,camerapos.distance(p));
            return project(p);
        }
        public point2D[] drawpoly(Polygon p){
            point2D[] points=new point2D[p.verticies.length];
            for (int i=0; i<p.verticies.length; i++){
                points[i]=drawvertex(p.verticies[i]);
                
            }
            return points;
        }  
        public void drawpoint(double x, double y, double z){
            Vertex n=new Vertex(x,y,z);
            drawvertex(n);
        }
        public void print(){
            for (int i=0; i<points.length; i++){
                System.out.print("Distance:");
                System.out.print(distances[i]);
                System.out.print(" Coordinates on screen:");
                points[i].printpos();
            }
        }
        public void setcamerapos(Vertex newpos){
            camerapos=newpos;
        }
        public void setcameradist(double newdist){
            screendistance=newdist;
        }
        public void setcameraangle(Angle newangle){
            camerangle=newangle;
        }
            
    }
    public static class point2D{
        double x;
        double y;
        public point2D(double ecks, double why){
            x=ecks;
            y=why;
        }
        public void printpos(){
            System.out.print("(");
            System.out.print(x);
            System.out.print(",");
            System.out.print(y);
            System.out.println(")");
        }
    }
    public RenderGUI(){
        super();
        addKeyListener(this);
        addMouseListener(this);
    }
    @Override
    public void paintComponent(Graphics g){
        g.clearRect(0,0,640,480);
        //IT WORKS! 
        Screen screen=new Screen();
        //Camera setup: (Defaults are Position:0,0,0 and Rotation:0,0 (Looking down the Y axis) and Distance:10)
        Vertex campos=new Vertex(0,0,0);
        screen.setcamerapos(campos);
        Angle camang=new Angle(0,Math.PI/4);
        screen.setcameraangle(camang);
        screen.setcameradist(2);
        
        Polygon square=new Polygon();
        square.addpoint(0,1,0);
        square.addpoint(1,1,0);
        square.addpoint(1,1,1);
        square.addpoint(0,1,1);
        point2D[] points=screen.drawpoly(square);
        for (int i=0; i<points.length-1; i++){
            g.drawLine(20+(int)points[i].x*10,20+(int)points[i].y*10,20+(int)points[i+1].x*10,20+(int)points[i+1].y*10);
        }
        g.drawLine(20+(int)points[0].x*10,20+(int)points[0].y*10,20+(int)points[points.length-1].x*10,20+(int)points[points.length-1].y*10);
    }
    @Override
    public void keyPressed( KeyEvent key){
    }
    @Override
    public void keyTyped(KeyEvent key){ }   
    @Override
    public void keyReleased(KeyEvent key){ }
    @Override
    public void mousePressed(MouseEvent e) {
    }
    @Override
    public void mouseReleased(MouseEvent e) {
    }
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    @Override
    public void mouseExited(MouseEvent e) {
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getY()>400){
            nextTense();
        }
        repaint();
    }
    public static void main(String[] args) {
        int Width=640;
        int Height=480;
        JFrame frame=new JFrame("Render");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setSize(Width,Height);
        RenderGUI panel=new RenderGUI();
        panel.setFocusable(true);
        frame.setContentPane(panel);
        frame.setVisible(true);
        // TODO code application logic here
    }
}
