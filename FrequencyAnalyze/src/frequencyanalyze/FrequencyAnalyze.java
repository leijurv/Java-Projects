/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package frequencyanalyze;

import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class FrequencyAnalyze {
    public static void analyze(String[] a){
        ArrayList<String> order=new ArrayList<String>();
        ArrayList<Integer> occurances=new ArrayList<Integer>();
        for (String s : a){
            if (order.contains(s)){
                int pos=order.indexOf(s);
                occurances.set(pos,occurances.get(pos)+1);
            }else{
                order.add(s);
                occurances.add(1);
            }
        }
        for (int i=0; i<order.size()-1; i++){
            if (occurances.get(i)>occurances.get(i+1)){
                int f=occurances.get(i);
                String F=order.get(i);
                order.set(i,order.get(i+1));
                occurances.set(i, occurances.get(i+1));
                order.set(i+1,F);
                occurances.set(i+1,f);
                i=-1;
            }
        }
        for (int i=0; i<order.size(); i++){
            System.out.println(order.get(i)+occurances.get(i));
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        analyze(new String[] {"a","b","a","c","b","b","c","a","a","b","d"});
        // TODO code application logic here
    }
}
