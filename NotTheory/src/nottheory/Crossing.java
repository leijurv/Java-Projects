/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nottheory;

import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Crossing {
    Crossing topNext;
    Crossing topPrev;
    Crossing belowNext;
    Crossing belowPrev;
    int topColor=-1;
    int belowNextColor=-1;
    public void tricolorable(){
        ArrayList<Integer> taken=new ArrayList<Integer>();
        taken.add(topColor);
        taken.add(belowNextColor);
        if (topPrev.belowNext.equals(this)){
            
        }
        if (topPrev.belowNext.equals(this)){
            if (topPrev.belowNextColor==-1){
                if (topColor==-1){
                    topColor=0;
                }else{
                    topPrev.belowNextColor=topColor;
                }
            }else{
                if (topColor==-1){
                    topColor=topPrev.belowNextColor;
                }else{
                    if (topColor!=topPrev.belowNextColor){
                        System.out.println("WAH");
                    }
                }
            }
        }
        
    }
}
