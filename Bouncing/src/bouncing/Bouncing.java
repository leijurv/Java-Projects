package bouncing;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
/**
 *
 * @author leijurv
 */
public class Bouncing {
    static final double size=10;
    public static void main(String[] args) throws InterruptedException{
        Ball[] balls=new Ball[20];
        Random r=new Random(5021);
        
        for (int i=0; i<balls.length; i++){
            Ball b=new Ball(r.nextDouble()*(size-1)+0.5,r.nextDouble()*(size-1)+0.5,r.nextDouble()*size*0.003,r.nextDouble()*size*0.003);
            balls[i]=b;
        }
        JFrame frame=new JFrame("asdf");
        frame.setSize(2000,2000);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(new JComponent() {
            @Override
            public void paintComponent(Graphics g){
                int x=50;
                int y=50;
                g.setColor(Color.WHITE);
                g.fillRect(50,50,500,500);
                g.setColor(Color.BLACK);
                for (Ball b : balls){
                    b.draw(g,x,y);
                }
            }
        }
        );
        frame.setVisible(true);
        while (true){
            for (Ball b : balls){
                b.check(size);
                b.tick();
            }
            if (collide(balls)){
                //break;
            }
            frame.repaint();
            Thread.sleep(10);
        }
    }
    public static boolean collide(Ball[] balls){
        boolean r=false;
        boolean[] collide=new boolean[balls.length];
        for (int i=0; i<balls.length; i++){
            for (int j=i+1; j<balls.length; j++){
                if (balls[i].collision(balls[j])){
                    balls[i].collide(balls[j]);
                    r=true;
                    collide[i]=true;
                    collide[j]=true;
                }
            }
        }
        for (int i=0; i<balls.length; i++){
            if (balls[i].jc){
                if (!collide[i]){
                    balls[i].jc=false;
                }
            }else{
                balls[i].jc=collide[i];
            }
        }
        return r;
    }
    public static class Ball {
        double x;
        double y;
        double dx;
        double dy;
        boolean jc=false;
        public Ball(double X,double Y,double DX,double DY){
            x=X;
            y=Y;
            dx=DX;
            dy=DY;
        }
        public boolean collision(Ball b){
            double xx=x-b.x;
            double yy=y-b.y;
            return Math.sqrt(xx*xx+yy*yy)<1;
        }
        public void collide(Ball b){
            if (jc){
                return;
            }
            double ox=b.dx-dx;
            double oy=b.dy-dy;
            double oMag=Math.sqrt(ox*ox+oy*oy);
            double Ang=Math.atan(oy/ox);
            if (ox<0){
                Ang=Math.PI+Ang;
            }
            double xx=x-b.x;
            double yy=y-b.y;
            double Angg=Math.atan(yy/xx);
            if (xx<0){
                Angg=Math.PI+Angg;
            }
            double diff=Ang-Angg;
            double VtMag=Math.cos(diff)*oMag;
            double nvx=xx*VtMag;
            double nvy=yy*VtMag;
            double NVX=ox-nvx;
            double NVY=oy-nvy;
            b.dx=NVX+dx;
            b.dy=NVY+dy;
            dx=nvx+dx;
            dy=nvy+dy;
        }
        public void tick(){
            x+=dx;
            y+=dy;
        }
        public void check(double bound){
            if (x-0.5<0){
                dx=Math.abs(dx);
            }
            if (y-0.5<0){
                dy=Math.abs(dy);
            }
            if (x+0.5>bound){
                dx=-Math.abs(dx);
            }
            if (y+0.5>bound){
                dy=-Math.abs(dy);
            }
        }
        public void draw(Graphics g,int X,int Y){
            
            g.setColor(Color.BLACK);
            if (jc){
                g.setColor(Color.BLUE);
            }
            double d=500/size;
            int xx=(int) (X+d*x-d/2);
            int yy=(int) (Y+d*y-d/2);
            g.drawOval(xx,yy,(int) d,(int) d);
            g.drawLine((int) (xx+d/2),(int) (yy+d/2),(int) (xx+dx*1000+d/2),(int) (yy+dy*1000+d/2));
        }
    }
}
