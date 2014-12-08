/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleship;
import java.util.Random;
/**
 *
 * @author leijurv
 */
public class Board {
    int[][] board;
    public Board(Board b){
        board=new int[b.board.length][b.board[0].length];
        for (int i=0; i<b.board.length; i++){
            System.arraycopy(b.board[i],0,board[i],0,b.board[i].length);
        }
    }
    public Board(int[][] bo){
        Board b=new Board();
        b.board=bo;
        board=(new Board(b)).board;
    }
    public Board(){
        board=new int[10][10];
    }
    public int[] place(int length,int[][] filt,boolean need){
        Random r=new Random();
        int[][] filter=new int[10][10];
        for (int XX=0; XX<10; XX++){
            System.arraycopy(filt[XX],0,filter[XX],0,10);
        }
        for (int ii=0; ii<200; ii++){
            //System.out.println(new Board(filt));
            //for (int xp=0; xp<2; xp++){
            int xp=r.nextInt(2);
            int yp=1-xp;
            int x=r.nextInt(10-xp*(length-1));
            int y=r.nextInt(10-yp*(length-1));
            x-=xp;
            y-=yp;
            boolean w=true;
            int k;
            for (k=0; k<length; k++){
                x+=xp;
                y+=yp;
                if (x>9){
                    w=false;
                    break;
                }
                if (y>9){
                    w=false;
                    break;
                }
                if (isShip(x,y)||isShip(x+1,y)||isShip(x,y+1)||isShip(x-1,y)||isShip(x,y-1)){
                    w=false;
                    break;
                }
                //System.out.println(x+","+y+","+filter[x][y]);
                if (filter[x][y]==-1){
                    w=false;
                    break;
                }
                filter[x][y]+=2;
            }
            if (w){
                if (need){
                    for (int i=0; i<board.length; i++){
                        for (int j=0; j<board[0].length; j++){
                            if (filter[i][j]==1&&board[i][j]!=1&&filter[i][j]<2){
                                w=false;
                            }
                        }
                    }
                }
                if (w){
                    for (int i=0; i<board.length; i++){
                        for (int j=0; j<board[0].length; j++){
                            if (filter[i][j]>1){
                                board[i][j]=1;
                            }
                        }
                    }
                    return new int[]{x,y};
                }
            }
            /*
             for (int i=k-1; i>0; i--){
             x-=xp;
             y-=yp;
             if (filter[x][y]>1){
             filter[x][y]-=2;
             }else{
             System.out.println(i+","+k);
             }
             }*/
            for (int i=0; i<10; i++){
                for (int j=0; j<10; j++){
                    if (filter[i][j]>1){
                        filter[i][j]-=2;
                    }
                }
            }
            
        }
        return null;
    }
    @Override
    public String toString(){
        String res="";
        for (int y=0; y<board[0].length; y++){
            for (int x=0; x<board.length; x++){
                //res=res+(board[x][y]==-1?"M":(board[x][y]==1?"H":"O"));
                res=res+board[x][y];
            }
            res=res+"\n";
        }
        return res;
    }
    public boolean isShip(int x,int y){
        if (x<0||x>9){
            return false;
        }
        if (y<0||y>9){
            return false;
        }
        return board[x][y]==1;
    }
}
