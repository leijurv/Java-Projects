/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;



/**
 *
 * @author leijurv
 */
public class TicTacToe {

    /**
     * @param args the command line arguments
     */
    public static class Board{
        public Board(){
            for (int i=0; i<9; i++){
                a[i]=0;
            }
        }
        boolean m=true;
        int[] a=new int[9];
        public int[] possibleMoves(){
            int[] res=new int[0];
            for (int i=0; i<a.length; i++){
                if (a[i]==0){
                    res=add(res,i);
                }
            }
            return res;
        }
        public int[] add(int[] a, int b){
            int[] c=new int[a.length+1];
            System.arraycopy(a, 0, c, 0, a.length);
            c[a.length]=b;
            return c;
        }
        public int worstMove(){
            int[] possibleMoves=possibleMoves();
            Board[] moves=new Board[possibleMoves.length];
            int[] scores=new int[possibleMoves.length];
            for (int i=0; i<moves.length; i++){
                scores[i]=0;
                moves[i]=new Board();
                moves[i].a=a.clone();
                moves[i].a[possibleMoves[i]]=-1;
                moves[i].print();
                int win=moves[i].win();
                if (win!=0){
                    scores[i]=win;
                    if (win==1){
                        return possibleMoves[i];
                    }
                }else{
                    //scores[i]=moves[i].bestMove();
                }
            }
            int bestIndex=0;
            int best=2;
            for (int i=0; i<possibleMoves.length; i++){
                if (possibleMoves[i]<=best){
                    bestIndex=i;
                    best=possibleMoves[i];
                }
            }
            if (best==1){
                throw new RuntimeException("I FREAKIN WIN");
            }
            return bestIndex;
        }
        public int bestMove(){
            int[] possibleMoves=possibleMoves();
            Board[] moves=new Board[possibleMoves.length];
            int[] scores=new int[possibleMoves.length];
            for (int i=0; i<moves.length; i++){
                scores[i]=0;
                moves[i]=new Board();
                moves[i].a=a.clone();
                System.out.println(possibleMoves[i]);
                moves[i].a[possibleMoves[i]]=1;
                
                int win=moves[i].win();
                if (win!=0){
                    scores[i]=win;
                    if (win==1){
                        return possibleMoves[i];
                    }
                }else{
                    scores[i]=moves[i].worstMove();
                }
            }
            int bestIndex=0;
            int best=-1;
            for (int i=0; i<possibleMoves.length; i++){
                if (possibleMoves[i]>=best){
                    bestIndex=i;
                    best=possibleMoves[i];
                }
            }
            if (best==-1){
                throw new RuntimeException("I FREAKIN LOSE");
            }
            return bestIndex;
        }
        public int win(){
            for (int i=0; i<3; i++){
                if (a[i*3]==a[1+i*3] && a[1+i*3]==a[2+i*3] && a[i*3]!=0){//Horizontal
                    return a[i*3];
                }
                if(a[i]==a[i+3] && a[i]==a[i+6] && a[i]!=0){//Vertical
                    return a[i];
                }
                if(a[0]==a[4] && a[0]==a[8] && a[0]!=0){
                    return a[0];
                }
                if (a[2]==a[6] && a[2]==a[4] && a[2]!=0){
                    return a[4];
                }
            }
            return 0;
        }
        public void print(){
            System.out.print(a[0]);
            System.out.print(a[1]);
            System.out.println(a[2]);
            System.out.print(a[3]);
            System.out.print(a[4]);
            System.out.println(a[5]);
            System.out.print(a[6]);
            System.out.print(a[7]);
            System.out.println(a[8]);
            System.out.println();
        }
    }
    public static void main(String[] args) {
        Board b=new Board();
        b.a[0]=0;b.a[1]=0;b.a[2]=1;
        b.a[3]=0;b.a[4]=-1;b.a[5]=0;
        b.a[6]=1;b.a[7]=0;b.a[8]=-1;
        b.print();
        System.out.println(b.bestMove());
    }
}
