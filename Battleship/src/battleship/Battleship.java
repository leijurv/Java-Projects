/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleship;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
/**
 *
 * @author leijurv
 */
public class Battleship {
    static int[][] count=new int[10][10];
    static int tot=1;
    static int maxX=-1;
    static int maxY=-1;
    static int[][] filter;
    static JFrame frame;
    static ArrayList<Integer> sunkLengths=new ArrayList<>();
    static ArrayList<int[]> sunkPositions=new ArrayList<>();
    static ArrayList<Boolean> sunkDirections=new ArrayList<>();
    static Board alreadySunk=new Board();
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        filter=new int[10][10];
        Random r=new Random(7);
        for (int i=0; i<40; i++){
            //filter[r.nextInt(10)][r.nextInt(10)]=-1;
        }
        //filter[r.nextInt(10)][r.nextInt(10)]=1;
        frame=new JFrame("");
        frame.setContentPane(new JComponent() {
            @Override
            public void paintComponent(Graphics g){
                g.drawString(tot+"",10,400);
                int[][] kitty=new int[10][10];
                int max=0;
                for (int x=0; x<10; x++){
                    for (int y=0; y<10; y++){
                        kitty[x][y]=count[x][y]*255;
                        kitty[x][y]=kitty[x][y]/(tot==0?1:tot);
                        if (kitty[x][y]>255){
                            System.out.println(tot+","+count[x][y]+","+kitty[x][y]);
                            kitty[x][y]=255;
                        }
                        if (kitty[x][y]<0){
                            System.out.println(tot+","+kitty[x][y]);
                            kitty[x][y]=0;
                        }
                        if (kitty[x][y]>max&&filter[x][y]!=1){
                            max=kitty[x][y];
                            maxX=x;
                            maxY=y;
                        }
                    }
                    //System.out.println(new Board(kitty));
                    draw(g,kitty,true);
                    draw(g,filter,false);
                }
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(2000,2000);
        frame.setVisible(true);
        frame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e){
                int x=(e.getX()-10)/32;
                int y=(e.getY()-35)/32;
                //y--;
                if (x<0||x>9||y<0||y>9){
                    return;
                }
                int a=filter[x][y];
                filter[x][y]=(a==1?0:(a==0?-1:1));
                tot=0;
                count=new int[10][10];
                sunkLengths=new ArrayList<>();
                sunkPositions=new ArrayList<>();
                sunkDirections=new ArrayList<>();
                findSunk();
                alreadySunk=gen();
                System.out.println(gen());
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            @Override
            public void mousePressed(MouseEvent e){
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            @Override
            public void mouseReleased(MouseEvent e){
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            @Override
            public void mouseEntered(MouseEvent e){
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            @Override
            public void mouseExited(MouseEvent e){
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        Board b=meow();
    }
    public static void draw(Graphics g,int[][] board,boolean shade){
        int size=32;
        for (int x=0; x<10; x++){
            g.drawRect(10,10+x*size,10*size,size);
            g.drawRect(10+x*size,10,size,10*size);
            g.drawString((x+1)+"",x*size+size/2,10);
            String s="ABCDEFGHIJ";
            g.drawString(s.substring(x,x+1)+"",0,x*size+size);
            for (int y=0; y<10; y++){
                if (shade){
                    int n=255-board[x][y];
                    if (n<0){
                        n=0;
                    }
                    //System.out.println(n);
                    //g.drawString(n+"",10,10);
                    g.setColor(new Color(n,n,n));
                    if (x==maxX&&y==maxY){
                        g.setColor(Color.BLUE);
                    }
                    g.fillRect(11+x*size,11+y*size,size-2,size-2);
                    g.setColor(Color.GREEN);
                    g.drawString((255-board[x][y])+"",10+(x)*size,10+size/2+(y)*size);
                }else{
                    g.setColor(board[x][y]==-1?Color.GREEN:(board[x][y]==0?Color.WHITE:Color.RED));
                    if (board[x][y]!=0){
                        g.fillRect(15+x*size,15+y*size,size-10,size-10);
                    }
                    //g.setColor(Color.GREEN);
                    //g.drawString(board[x][y]+"",10+size/2+(x)*size,10+size/2+(y)*size);
                }
                g.setColor(Color.BLACK);
            }
        }
    }
    public static Board meow(){
        count=new int[10][10];
        tot=1;
        Random r=new Random();
        for (tot=0; tot>=0;){
            //System.out.println("CAT");
            ArrayList<Integer> req=new ArrayList<>();
            req.add(2);
            req.add(3);
            req.add(3);
            req.add(4);
            req.add(5);
            for (int i : sunkLengths){
                req.remove(new Integer(i));
            }
            Board temp=new Board(alreadySunk);
            boolean d=true;
            while (!req.isEmpty()){
                int n=req.remove(r.nextInt(req.size()));
                if (temp.place(n,filter,req.isEmpty())==null){
                    d=false;
                    req=new ArrayList<>();
                }
            }
            if (!d){
                continue;
            }
            tot++;
            for (int x=0; x<10; x++){
                for (int y=0; y<10; y++){
                    count[x][y]+=temp.board[x][y];
                    //System.out.println(temp.board[x][y]);
                }
            }
            //System.out.println("DONE"+tot);
            frame.repaint();
        }
        return new Board(count);
    }
    public static void findSunk(){
        for (int i=0; i<10; i++){
            for (int j=0; j<10; j++){
                if (filter[i][j]==1){
                    System.out.println("Testing "+i+","+j);
                    boolean x=(i==9?false:filter[i+1][j]==1);
                    boolean y=(j==9?false:filter[i][j+1]==1);
                    if (i!=0&&x){
                        if (filter[i-1][j]!=-1){
                            System.out.println("prev");
                            continue;
                        }
                    }
                    if (j!=0&&y){
                        if (filter[i][j-1]!=-1){
                            System.out.println("pprev");
                            continue;
                        }
                    }
                    if (!x&&!y){
                        if (j!=9){
                            if (filter[i][j+1]!=-1){
                                continue;
                            }
                        }
                        if (i!=9){
                            if (filter[i+1][j]!=-1){
                                continue;
                            }
                        }
                        System.out.println("Ship at "+i+","+j);
                        /*
                        sunkLengths.add(1);
                        sunkDirections.add(false);
                        sunkPositions.add(new int[]{i,j});*/
                    }else{
                        int xp=x?1:0;
                        int yp=y?1:0;
                        int X=i;
                        int Y=j;
                        boolean oob=false;
                        while (true){
                            if (X<0||X>9){
                                X=(X<0?0:9);
                                System.out.println("OOBX");
                                oob=true;
                                break;
                            }
                            if (Y<0||Y>9){
                                System.out.println("OOBY");
                                Y=(Y<0?0:9);
                                oob=true;
                                break;
                            }
                            if (filter[X][Y]!=1){
                                if (filter[X][Y]==0){
                                    X=-1;
                                    Y=-1;
                                    break;
                                }
                                break;
                            }
                            X+=xp;
                            Y+=yp;
                        }
                        if (X==-1&&Y==-1){
                            continue;
                        }
                        if (!oob){
                            X-=xp;
                            Y-=yp;
                        }
                        System.out.println("Ship from "+i+","+j+" to "+X+","+Y);
                        sunkLengths.add(X-i+Y-j+1);
                        sunkDirections.add(x);
                        sunkPositions.add(new int[]{i,j});
                    }
                }
            }
        }
    }
    public static Board gen(){
        Board temp=new Board();
        for (int i=0; i<sunkLengths.size(); i++){
            int xp=sunkDirections.get(i)?1:0;
            int yp=1-xp;
            int num=sunkLengths.get(i);
            int x=sunkPositions.get(i)[0];
            int y=sunkPositions.get(i)[1];
            for (int k=0; k<num; k++){
                temp.board[x][y]=1;
                x+=xp;
                y+=yp;
            }
        }
        return temp;
    }
}
