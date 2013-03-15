/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cholocate;

import java.util.ArrayList;

/**
 *
 * @author leif
 */
public class Board {
    /*BASE:
    Brown Circle, Yellow Circle, Green Circle, 
    Brown Triangle, Yellow Triangle, Green Triangle, 
    Brown Square, Yellow Square, Green Square
    
    
    40 SOLUTION:
    Brown Circle, Brown Triangle, Green Circle
    Green Square, Yellow Circle, Green Triangle
    Yellow Square, Yellow Triangle, Brown Square
    
    */ 
    static final String[] output={"Brown Circle", "Yellow Circle", "Green Circle", "Brown Triangle", "Yellow Triangle", "Green Triangle", "Brown Square", "Yellow Square", "Green Square"};
    int[] b=new int[9];
    public Board(int[] c){
        b=c;
    }
    public boolean isBrown(int pos){
        return b[pos]%3==0 || b[pos]==-1;
    }
    public boolean isGreen(int pos){
        return b[pos]%3==2 || b[pos]==-1;
    }
    public boolean isYellow(int pos){
        return b[pos]%3==1 || b[pos]==-1;
    }
    public boolean isCircle(int pos){
        return b[pos]<3 || b[pos]==-1;
    }
    public boolean isTriangle(int pos){
        return (b[pos]<6 && !isCircle(pos)) || b[pos]==-1;
    }
    public boolean isSquare(int pos){
        return b[pos]>5  || b[pos]==-1;
    }
    public boolean fits(){
        return fits40();
    }
    public boolean fits40(){
        boolean has=false;
        
        for (int i=0; i<3; i++){
            for (int n=0; n<2; n++){
                if (isGreen(i*3+n) && isSquare(i*3+n) && isCircle(i*3+n+1)){
                    has=true;
                }
            }
        }
        if (!has){
            return false;
        }
        has=false;
        
        for (int i=0; i<3; i++){
            for (int n=0; n<2; n++){
                if (isGreen(i*3+n+1) && isTriangle(i*3+n) && isCircle(i*3+n+1)){
                    has=true;
                }
            }
        }
        if (!has){
            return false;
        }
        has=false;
        
        for (int i=0; i<3; i++){
            for (int n=0; n<2; n++){
                if (isSquare(i*3+n) && isTriangle(i*3+n+1)){
                    has=true;
                }
            }
        }
        if (!has){
            return false;
        }
        
        has=false;
        for (int i=0; i<2; i++){
            for (int n=0; n<2; n++){
                if (isTriangle(i*3+n) && isTriangle(i*3+n+4)){
                    has=true;
                }
            }
        }
        if (!has){
            return false;
        }
        has=false;
        
        for (int i=0; i<2; i++){
            if (isBrown(i) && isTriangle(i+6)){
                has=true;
            }
        }
        if (!has){
            return false;
        }
        has=false;
        
        for (int i=0; i<2; i++){
            if (isYellow(i+4) && isSquare(i+6)){
                has=true;
            }
        }
        if (!has){
            return false;
        }
        has=false;
        for (int i=0; i<2; i++){
            if (isBrown(i+7) && isSquare(i+7) && isYellow(i+6)){
                has=true;
            }
        }
        if (!has){
            return false;
        }
        return true;
    }
    public boolean fits39(){
        boolean has1=false;
        for (int i=0; i<3; i++){
            if (isGreen(i*3) && isSquare(i*3+2)){
                has1=true;
            }
        }
        int n=0;
        if (!has1){
            return false;
        }
        boolean has2=false;
        for (int i=0; i<2; i++){
            if (isYellow(i*3+1) && isGreen(i*3+2) && isSquare(i*3+3) && isSquare(i*3+4)){
                has2=true;
            }
        }
        if (!has2){
            System.out.println(++n);
            return false;
        }
        boolean has3=false;
        for (int i=0; i<2; i++){
            if (isGreen(i) && isCircle(i+3) && isCircle(i+4) && isBrown(i+7)){
                has3=true;
            }
        }
        if (!has3){
            System.out.println(++n);
            return false;
        }
        boolean has4=false;
        for (int i=0; i<2; i++){
            if (isGreen(i+1) && isCircle(i+4) && isBrown(i+7)){
                has4=true;
            }
        }
        if (!has4){
            System.out.println(++n);
            return false;
        }
        return true;
    }
    public Board solve(){
        if (!fits()){
            return null;
        }
        int nt=-1;
        for (int i=0; i<b.length; i++){
            if (b[i]>nt){
                nt=b[i];
            }
        }
        nt++;
        if (nt==9){
            return this;
        }
        ArrayList<Integer> pos;
        pos = new ArrayList<Integer>();
        for (int i=0; i<b.length; i++){
            if (b[i]==-1){
                pos.add(i);
            }
        }
        for (int i=0; i<pos.size(); i++){
            int[] w=new int[9];
            System.arraycopy(b, 0, w, 0, b.length);
            w[pos.get(i)]=nt;
            Board B=new Board(w);
            Board u=B.solve();
            if (u!=null){
                return u;
            }
            
        }
        return null;
    }
    public String toString(){
        int i=0;
        return output[b[i++]]+", "+output[b[i++]]+", "+output[b[i++]]+"\n"+output[b[i++]]+", "+output[b[i++]]+", "+output[b[i++]]+"\n"+output[b[i++]]+", "+output[b[i++]]+", "+output[b[i++]];
    }
}
