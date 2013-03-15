/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package euler122;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author leijurv
 */
public class Euler122 {

    /**
     * @param args the command line arguments
     */
    public static void add(ArrayList<Integer> q, ArrayList<Integer> alr, int i) {
        for (int n = i; n < q.size(); n++) {
            if (!alr.contains(q.get(n))) {
                alr.add(q.get(n));
            }
        }
    }

    public static ArrayList<Integer> DURP(int a, boolean first, int caller) {
        if (a == 1) {
            ArrayList<Integer> b = new ArrayList<Integer>();
            b.add(0);
            b.add(1);
            return b;
        }
        ArrayList<Integer> result = new ArrayList<Integer>();
        result.add(10000);
        boolean hasC = false;
        boolean c = false;
        ArrayList<Integer> durp = new ArrayList<Integer>();
        if (a > caller - a && !first) {
            c = true;
         //   durp = DURP(caller - a, false, caller);
        }
        for (int i = 1; i < a; i++) {
            ArrayList<Integer> alr = new ArrayList<Integer>();
            alr.add(0);
            int total = 1;
            ArrayList<Integer> q=new ArrayList<Integer>();
            ArrayList<Integer> b=new ArrayList<Integer>();
            q = DURP(i, false, a);
            add(q, alr, 1);
            b = DURP(a - i, false, a);
            add(b, alr, 1);
            if (!alr.contains(i)) {
                alr.add(i);
                total += q.get(0);
            }
            if (!alr.contains(a - i)) {
                total += b.get(0);
                alr.add(a - i);
            }
            add(durp,alr,1);
            alr.set(0, total);
            
            boolean d = alr.contains(caller - a);
            
            /*
            if (c) {
                if (hasC && d) {
                    if (alr.get(0) < result.get(0)) {
                        result = alr;
                    }
                }
                if (!hasC && d) {
                    result = alr;
                    hasC = true;
                }
                if (!hasC && !d) {
                    result = alr;
                }
            } else {
                if (alr.get(0) < result.get(0)) {
                    result = alr;
                }
            }*/
            if ((!c && alr.get(0)<result.get(0)) || c&&(!hasC&&d || !hasC && !d || hasC&&d&&(alr.get(0)<result.get(0)))){
                result=alr;
            }
            if (c&&!hasC&&d){
                hasC=true;
            }
        }
        //result.remove(1);
        //System.out.println(a+":"+result);
        if (first) {
            System.out.println(a + ":" + result);
        }
        return result;
    }

    public static void main(String[] args) {
        int total = 0;
        for (int i = 2; i <= 10; i++) {
            total += (DURP(i, true, i + 1).get(0));
        }
        System.out.println(total);
        //75
    }
}
