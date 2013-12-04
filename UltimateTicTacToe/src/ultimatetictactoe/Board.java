/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ultimatetictactoe;

import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Board {
    MiniBoard[] mb=new MiniBoard[9];
    boolean move=true;
    int where=-1;
    public Board(){
        for (int i=0; i<9; i++){
            mb[i]=new MiniBoard();
        }
    }
    public Board(Board b, int Where){
        for (int i=0; i<9; i++){
            mb[i]=new MiniBoard(b.mb[i]);
        }
        move=!b.move;
        where=Where;
    }
    public void check(){
        if(where>2){
            where=0;
        }
        if(where!=-1){
            if (mb[where].win()!=0 || mb[where].open().length==0){
                if(where<2){
                where++;
                check();
                }
                //where=-1;
            }
        }
                                            //where=1;
    }
    public String toString(){
        String s="{";
        for (int i=0; i<9; i++){
            s=s+mb[i];
            if (i!=8){
                s=s+",";
            }
        }
        return s+"}";
    }
    public int solve(int depth){
        if (depth<57){
            //System.out.println(this);
            //return 0;
        }
        
        int[] win=new int[9];
        for (int i=0; i<9; i++){
            win[i]=mb[i].win();
        }
                                            /*if (win[1]!=0){
                                                return win[1];
                                            }*/
        int overall=MiniBoard.check(win);
        if (overall!=0){
            return overall;
        }
        ArrayList<Board> next=new ArrayList<Board>();
        ArrayList<Integer> boards=new ArrayList<Integer>();
        if (where==-1){
            for (int i=0; i<9; i++){
                if (mb[i].win()==0){
                    boards.add(i);
                }
            }
        }else{
            boards.add(where);
        }
        for (int B : boards){
            int[] positions=mb[B].open();
            for (int P : positions){
                MiniBoard m=new MiniBoard(mb[B]);
                m.d[P]=move?1:-1;
                Board x=new Board(this,P);
                x.mb[B]=m;
                x.check();
                next.add(x);
            }
        }
        if (next.size()==0){
            return 0;
        }
        for (Board b : next){
            //System.out.println(b+" "+where);
        }
        boolean foundTie=false;
        int[] evals=new int[next.size()];
        for (int i=0; i<next.size(); i++){
            evals[i]=next.get(i).solve(depth+1);
            if (evals[i]==(move?1:-1)){
                //System.out.println(this+"w"+evals[i]);
                return evals[i];
            }
            if (evals[i]==0){
                foundTie=true;
            }
        }
        if (foundTie){
            //System.out.println(this+" 0");
            return 0;
        }
        //System.out.println(this+" "+(move?-1:1));
        return move?-1:1;
    }
}
