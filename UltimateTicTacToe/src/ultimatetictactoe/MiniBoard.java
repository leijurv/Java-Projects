/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ultimatetictactoe;

/**
 *
 * @author leijurv
 */
public class MiniBoard {
    int[] d=new int[9];
    public MiniBoard(){
        for (int i=0; i<9; i++){
            d[i]=0;
        }
    }
    public MiniBoard(MiniBoard m){
        for (int i=0; i<9; i++){
            d[i]=m.d[i];
        }
    }
    public String toString(){
        String s="{";
        for (int i=0; i<9; i++){
            s=s+d[i];
            if (i!=8){
                s=s+",";
            }
        }
        return s+"}";
    }
    public int[] open(){
        int total=0;
        for (int i=0; i<9; i++){
            if (d[i]==0){
                total++;
            }
        }
        int[] result=new int[total];
        int pos=0;
        for (int i=0; i<9; i++){
            if (d[i]==0){
                result[pos++]=i;
            }
        }
        return result;
    }
    public int win(){
        return check(d);
    }
    public static int check(int[] c){
        for (int i=0; i<3; i++){
            if (c[i*3]==c[i*3+1] && c[i*3+1]==c[i*3+2]){
                if (c[i*3]!=0){
                    return c[i*3];
                }
            }
            if (c[i]==c[i+3] && c[i+3]==c[i+6]){
                if (c[i]!=0){
                    return c[i];
                }
            }
        }
        if (c[0]==c[4] && c[4]==c[8]){
            if (c[0]!=0){
                return c[0];
            }
        }
        if (c[2]==c[4] && c[4]==c[6]){
            if (c[2]!=0){
                return c[2];
            }
        }
        return 0;
    }
}
