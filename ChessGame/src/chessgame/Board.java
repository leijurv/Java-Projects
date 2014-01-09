/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chessgame;

import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Board {
    int[][] state;
    public ArrayList<int[]> moves(int i, int j){
        if (state[i][j]==-1){
            return new ArrayList<int[]>();
        }
        int p=state[i][j];
        ArrayList<int[]> m=new ArrayList<int[]>();
        if (p==0){
            
        }
        
    }
}
