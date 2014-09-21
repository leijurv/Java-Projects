/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package khet;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import khet.Piece.Dir;
import khet.Piece.PieceType;

/**
 *
 * @author leijurv
 */
public class Khet {

    static JComponent M;
    static boolean turn=true;
    static int selX=-1;
    static int selY=-1;
    static boolean moving=false;
    static Piece pec;
    static Board b=new Board();
    static Board prev;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        M=new JComponent() {
            public void paintComponent(Graphics g){
                b.draw(g,10,10);
                if (moving){
                    pec=lazer(g);
                }
                //System.out.println(p.x+","+p.y);
                g.setColor(Color.GREEN);
                if (selX!=-1){
                    int x=selX*64+42;
                    int y=(7-selY)*64+42;
                    g.drawRect(x,y,64,64);
                }
            }
        };
        JFrame frame=new JFrame("Khet");
        frame.setContentPane(M);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(2000,2000);
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e){
                if (moving){
                    if (e.getKeyCode()==KeyEvent.VK_ENTER){
                        if (pec.type!=null){
                            b.board[pec.x][pec.y]=pec.shot();
                        }
                        moving=false;
                        turn=!turn;
                        selX=-1;
                        prev=null;
                        M.repaint();
                    }
                    if (e.getKeyCode()==KeyEvent.VK_ESCAPE){
                        b=prev;
                        selX=-1;
                        moving=false;
                        pec=null;
                        prev=null;
                        M.repaint();
                    }
                    return;
                }
                if (selX!=-1){
                    int[] mov=fromChar(e.getKeyChar());
                    if (mov!=null){
                        int curX=selX;
                        int curY=selY;
                        int x=curX+mov[0];
                        int y=curY+mov[1];
                        if (x<0||x>9||y<0||y>7){
                            return;
                        }
                        Piece n=b.board[x][y];
                        Piece p=b.board[curX][curY];
                        System.out.println(p+","+n);
                        if (b.board[x][y]!=null){
                            if ((b.board[x][y].type!=PieceType.OBELISK||(b.board[curX][curY].type!=PieceType.OBELISK&&b.board[curX][curY].type!=PieceType.DOUBLEOBELISK))&&(p.type!=PieceType.SCARAB)){
                                return;
                            }
                        }
                        System.out.println(p+","+n);
                        if (p==null){
                            return;
                        }
                        prev=new Board(b);
                        if (p.type==PieceType.PYRAMID||p.type==PieceType.PHAROH){
                            b.board[curX][curY]=null;
                            b.board[x][y]=p;
                            p.x=x;
                            p.y=y;
                        }
                        if (p.type==PieceType.SCARAB){
                            System.out.println(n);
                            if (n==null){
                                return;
                            }
                            n.x=curX;
                            n.y=curY;
                            p.x=x;
                            p.y=y;
                            b.board[x][y]=p;
                            b.board[curX][curY]=n;
                        }
                        if (p.type==PieceType.OBELISK){
                            if (n==null){
                                b.board[x][y]=p;
                                p.x=x;
                                p.y=y;
                                b.board[curX][curY]=null;
                            }else{
                                if (n.type==PieceType.OBELISK){
                                    n.type=PieceType.DOUBLEOBELISK;
                                    b.board[curX][curY]=null;
                                }else{
                                    return;
                                }
                            }
                        }
                        if (p.type==PieceType.DOUBLEOBELISK){
                            if (n==null){
                                b.board[x][y]=new Piece(x,y,PieceType.OBELISK,null,p.silver);
                                b.board[curX][curY]=new Piece(curX,curY,PieceType.OBELISK,null,p.silver);
                            }else{
                                if (n.type==PieceType.OBELISK){
                                    n.type=PieceType.DOUBLEOBELISK;
                                    p.type=PieceType.OBELISK;
                                }else{
                                    return;
                                }
                            }
                        }
                        //turn=!turn;
                        selX=-1;
                        moving=true;
                        M.repaint();
                        return;
                    }
                    System.out.println(e.getKeyCode());
                    if (e.getKeyCode()==KeyEvent.VK_LEFT){
                        if (selX!=-1){
                            prev=new Board(b);
                            if (b.board[selX][selY].rotateLeft()){
                                //turn=!turn;
                                selX=-1;
                                moving=true;
                                M.repaint();
                                return;
                            }
                        }
                    }
                    if (e.getKeyCode()==KeyEvent.VK_RIGHT){
                        if (selX!=-1){
                            prev=new Board(b);
                            if (b.board[selX][selY].rotateRight()){
                                //turn=!turn;
                                selX=-1;
                                moving=true;
                                M.repaint();
                                return;
                            }
                        }
                    }
                }
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void keyTyped(KeyEvent e){
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void keyReleased(KeyEvent e){
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        frame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e){
                int x=((e.getX()-43)/64);
                int y=7-((e.getY()-67)/64);
                if (x<0||x>9||y<0||y>7){
                    return;
                }
                if (b.board[x][y]!=null){
                    if (!b.board[x][y].silver^turn){
                        if (!moving){
                            selX=x;
                            selY=y;
                        }
                    }
                }
                M.repaint();
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
        frame.setVisible(true);
    }

    public static Piece lazer(Graphics g){
        g.setColor(Color.BLUE);
        int y1=(turn ? 31 : -3)*16+74;
        int y11=(turn ? 30 : -2)*16+74;
        int y2=(turn ? 7 : 0)*64+74;
        int x=(turn ? 9 : 0)*64+74;
        g.drawLine(x-16,y1,x+16,y1);
        g.drawLine(x-16,y1,x,y11);
        g.drawLine(x+16,y1,x,y11);
        g.drawLine(x,y11,x,y2);
        return b.laser(g,10,10,turn ? 9 : 0,turn ? 0 : 7,turn ? Dir.UP : Dir.DOWN);
    }

    public static int[] fromChar(char c){
        switch (c){
            case 'q':
                return new int[]{-1,1};
            case 'w':
                return new int[]{0,1};
            case 'e':
                return new int[]{1,1};
            case 'a':
                return new int[]{-1,0};
            case 'd':
                return new int[]{1,0};
            case 'z':
                return new int[]{-1,-1};
            case 'x':
                return new int[]{0,-1};
            case 'c':
                return new int[]{1,-1};
        }
        return null;
    }
}
