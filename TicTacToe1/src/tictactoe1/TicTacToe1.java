/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe1;

/**
 *
 * @author leijurv
 */
public class TicTacToe1 {

    /**
     * @param args the command line arguments
     */
    public static class Board{
        static int prev=0;
        static long xW=0;
        static long Draw=0;
        static long yW=0;
        static long eye=0;
        static long total=0;
        public Board(){
            
        }
        int n=1;
        int[] a={0,0,0,0,0,0,0,0,0};
        public int[] add(int[] a, int b){
            int[] c=new int[a.length+1];
            for (int i=0; i<a.length; i++){
                c[i]=a[i];
            }
            c[a.length]=b;
            return c;
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
        public int[] pM(){
            int[] r=new int[0];
            for (int i=0; i<a.length; i++){
                if (a[i]==0){
                    r=add(r,i);
                }
            }
            return r;
        }
        public long possibilities(){
            
            total++;
            if (win()!=0){
                eye++;
                if (win()==1){
                    xW++;
                }else{
                    yW++;
                }
            
                return 1;
            }
            int[] p=pM();
            Board[] m=new Board[p.length];
            long res=0;
            for (int r=0; r<m.length; r++){
                m[r]=new Board();
                m[r].a=a.clone();
                m[r].a[p[r]]=n;
                m[r].n=0-n;
                res+=m[r].possibilities();
            }
            if (res==0){
                res++;
                eye++;
                Draw++;
                
                
            }
            
            return res;
        }
        public String n(int a){
            return a==0?" ":a==1?"X":"O";
        }
        public void print(){
            System.out.print(n(a[0]));
            System.out.print(n(a[1]));
            System.out.println(n(a[2]));
            System.out.print(n(a[3]));
            System.out.print(n(a[4]));
            System.out.println(n(a[5]));
            System.out.print(n(a[6]));
            System.out.print(n(a[7]));
            System.out.println(n(a[8]));
        }
    }
    public static void main(String[] args) {
        Board b=new Board();
        System.out.println("Total win/draw positions: "+b.possibilities());
        System.out.println("X won: "+Board.xW);
        System.out.println("O won: "+Board.yW);
        System.out.println("Draws: "+Board.Draw);
    }
}
