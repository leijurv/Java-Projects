/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package othello;

import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author leijurv
 */
public class Board {
    static int depth = Integer.parseInt(JOptionPane.showInputDialog("How many moves should I look ahead?"));//How many moves each one looks ahead
    static final int size=8;
    byte[][] board = new byte[size][size];
    int sofar;
    boolean move;
    static final String[] values = {"B", " ", "R"};
    static int screenUpdateDepth = 0;
    Move Best=null;
    int BEst=0;
    public Board(byte[][] a, int b, boolean m) {
        sofar = b;
        for (int i = 0; i < size; i++) {
            System.arraycopy(a[i], 0, board[i], 0, size);
        }
        move = m;
    }
    public ArrayList<Move> possibleMoves(){
        ArrayList<Move> result=new ArrayList<Move>();
        for (int x=0; x<8; x++){
            for (int y=0; y<8; y++){
                if (board[x][y]==0){
                    boolean up=(y!=size-1)&&(board[x][y+1]==(move?-1:1));
                    boolean down=(y!=0)&&(board[x][y-1]==(move?-1:1));
                    boolean left=(x!=0)&&(board[x-1][y]==(move?-1:1));
                    boolean right=(x!=size-1)&&(board[x+1][y]==(move?-1:1));
                    boolean upright=(y!=size-1)&&(x!=size-1)&&(board[x+1][y+1]==(move?-1:1));
                    boolean upleft=(y!=size-1)&&(x!=0)&&(board[x-1][y+1]==(move?-1:1));
                    boolean downright=(y!=0)&&(x!=size-1)&&(board[x+1][y-1]==(move?-1:1));
                    boolean downleft=(y!=0)&&(x!=0)&&(board[x-1][y-1]==(move?-1:1));
                    if (up){
                        int X=x;
                        int Y=y+1;
                        Board b=new Board(board,sofar+1,!move);
                        while(X<size && Y<size && X>-1 && Y>-1 && board[X][Y]==(move?-1:1)){
                            b.board[X][Y]=(byte) (move?1:-1);
                            Y++;
                        }
                        if (X<size && Y<size && X>-1 && Y>-1 && board[X][Y]==(move?1:-1)){
                            b.board[x][y]=(byte)(move?1:-1);
                            result.add(new Move(b,x,y));
                        }
                    }
                    if (down){
                        int X=x;
                        int Y=y-1;
                        Board b=new Board(board,sofar+1,!move);
                        while(X<size && Y<size && X>-1 && Y>-1 && board[X][Y]==(move?-1:1)){
                            b.board[X][Y]=(byte) (move?1:-1);
                            Y--;
                        }
                        if (X<size && Y<size && X>-1 && Y>-1 && board[X][Y]==(move?1:-1)){
                            b.board[x][y]=(byte)(move?1:-1);
                            result.add(new Move(b,x,y));
                        }
                    }
                    if (left){
                        int X=x-1;
                        int Y=y;
                        Board b=new Board(board,sofar+1,!move);
                        while(X<size && Y<size && X>-1 && Y>-1 && board[X][Y]==(move?-1:1)){
                            b.board[X][Y]=(byte) (move?1:-1);
                            X--;
                        }
                        if (X<size && Y<size && X>-1 && Y>-1 && board[X][Y]==(move?1:-1)){
                            b.board[x][y]=(byte)(move?1:-1);
                            result.add(new Move(b,x,y));
                        }
                    }
                    if (right){
                        int X=x+1;
                        int Y=y;
                        Board b=new Board(board,sofar+1,!move);
                        while(X<size && Y<size && X>-1 && Y>-1 && board[X][Y]==(move?-1:1)){
                            b.board[X][Y]=(byte) (move?1:-1);
                            X++;
                        }
                        if (X<size && Y<size && X>-1 && Y>-1 && board[X][Y]==(move?1:-1)){
                            b.board[x][y]=(byte)(move?1:-1);
                            result.add(new Move(b,x,y));
                        }
                    }
                    if (upright){
                        int X=x+1;
                        int Y=y+1;
                        Board b=new Board(board,sofar+1,!move);
                        while(X<size && Y<size && X>-1 && Y>-1 && board[X][Y]==(move?-1:1)){
                            b.board[X][Y]=(byte) (move?1:-1);
                            Y++;
                            X++;
                        }
                        if (X<size && Y<size && X>-1 && Y>-1 && board[X][Y]==(move?1:-1)){
                            b.board[x][y]=(byte)(move?1:-1);
                            result.add(new Move(b,x,y));
                        }
                    }
                    if (upleft){
                        int X=x-1;
                        int Y=y+1;
                        Board b=new Board(board,sofar+1,!move);
                        while(X<size && Y<size && X>-1 && Y>-1 && board[X][Y]==(move?-1:1)){
                            b.board[X][Y]=(byte) (move?1:-1);
                            Y++;
                            X--;
                        }
                        if (X<size && Y<size && X>-1 && Y>-1 && board[X][Y]==(move?1:-1)){
                            b.board[x][y]=(byte)(move?1:-1);
                            result.add(new Move(b,x,y));
                        }
                    }
                    if (downright){
                        int X=x+1;
                        int Y=y-1;
                        Board b=new Board(board,sofar+1,!move);
                        while(X<size && Y<size && X>-1 && Y>-1 && board[X][Y]==(move?-1:1)){
                            b.board[X][Y]=(byte) (move?1:-1);
                            Y--;
                            X++;
                        }
                        if (X<size && Y<size && X>-1 && Y>-1 && board[X][Y]==(move?1:-1)){
                            b.board[x][y]=(byte)(move?1:-1);
                            result.add(new Move(b,x,y));
                        }
                    }
                    if (downleft){
                        int X=x-1;
                        int Y=y-1;
                        Board b=new Board(board,sofar+1,!move);
                        while(X<size && Y<size && X>-1 && Y>-1 && board[X][Y]==(move?-1:1)){
                            b.board[X][Y]=(byte) (move?1:-1);
                            Y--;
                            X--;
                        }
                        if (X<size && Y<size && X>-1 && Y>-1 && board[X][Y]==(move?1:-1)){
                            b.board[x][y]=(byte)(move?1:-1);
                            result.add(new Move(b,x,y));
                        }
                    }
                }
            }
        }
        for (int i=0; i<result.size()-1; i++){
            if (result.get(i).xPos==result.get(i+1).xPos && result.get(i).yPos==result.get(i+1).yPos){
                byte[][] N=new byte[size][size];
                byte[][] a=result.get(i).result.board;
                byte[][] b=result.get(i+1).result.board;
                for (int x=0; x<size; x++){
                    for (int y=0; y<size; y++){
                        if (a[x][y]==b[x][y]){
                            N[x][y]=a[x][y];
                        }
                        if (a[x][y]==0 && b[x][y]!=0){
                            N[x][y]=b[x][y];
                        }
                        if (a[x][y]!=0 && b[x][y]==0){
                            N[x][y]=a[x][y];
                        }
                        if (a[x][y]!=0 && b[x][y]!=0 && a[x][y]!=b[x][y]){
                            N[x][y]=(byte)(move?1:-1);
                        }
                    }
                }
                result.get(i).result.board=N;
                result.remove(i+1);
                i--;
            }
        }
        return result;
    }
    public String toString(){
        String result="";
        for (int i=size-1; i>=0; i--){
            for (int y=0; y<size; y++){
                result+=(values[board[i][y]+1]);
            }
            result+="\n";
        }
        return result;
    }
    public int eval(boolean corner){
        int red=0;
        int black=0;
        for (int x=0; x<size; x++){
            for (int y=0; y<size; y++){
                if (board[x][y]==1){
                    red++;
                }
                if (board[x][y]==-1){
                    black++;
                }
            }
        }
        if (corner){
        int cornerOffset=20;
        if (board[0][0]==1){
            red+=cornerOffset;
        }
        if (board[0][0]==-1){
            black+=cornerOffset;
        }
        if (board[0][size-1]==1){
            red+=cornerOffset;
        }
        if (board[0][size-1]==-1){
            black+=cornerOffset;
        }
        if (board[size-1][0]==1){
            red+=cornerOffset;
        }
        if (board[size-1][0]==-1){
            black+=cornerOffset;
        }
        if (board[size-1][size-1]==1){
            red+=cornerOffset;
        }
        if (board[size-1][size-1]==-1){
            black+=cornerOffset;
        }
        }
        return red-black;
    }
    public int solve(){
        if (sofar==depth){
            return eval(true);
        }
        boolean done=true;
        for (int x=0; x<size; x++){
            for (int y=0; y<size; y++){
                if (board[x][y]==0){
                    done=false;
                }
            }
        }
        if (done){
            return eval(false);
        }
        
        int best=move?-64:64;
        Move b=null;
        ArrayList<Move> moves=possibleMoves();
        for (Move m : moves){
            int x=m.result.solve();
            if ((x>=best && move) || (x<=best && !move)){
                best=x;
                b=m;
            }
        }
        Best=b;
        BEst=best;
        return best;
    }
}