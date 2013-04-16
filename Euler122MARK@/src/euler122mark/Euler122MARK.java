/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package euler122mark;

import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Euler122MARK {

    static ArrayList<Integer> binary = new ArrayList<Integer>();
    public static void main(String[] args){
        long l=System.currentTimeMillis();
        int[] lengths=new int[201];
        for (int i=0; i<lengths.length; i++){
            lengths[i]=99999;
        }
        ArrayList<Integer> chain=new ArrayList<Integer>();
        chain.add(1);
        Gen1(chain,lengths);
        int sum=0;
        for (int i=1; i<lengths.length; i++){
            //System.out.println((i)+":"+(lengths[i]-1));
            sum+=lengths[i]-1;
        }
        System.out.println(sum);
        System.out.println(System.currentTimeMillis()-l);
    }
    public static void Gen1(ArrayList<Integer> chain,int[] lengths){
        int largest=chain.get(chain.size()-1);
        if (chain.size()>=lengths[largest]){
            return;
        }
        lengths[largest]=chain.size();
        ArrayList<Integer> d=new ArrayList<Integer>();
        for (int i=0; i<chain.size(); i++){
            for (int n=i; n<chain.size(); n++){
                int b=chain.get(i)+chain.get(n);
                if (b<=200 && b>largest && !d.contains(b)){
                    d.add(b);
            chain.add(b);
            Gen1(chain,lengths);
            chain.remove(chain.size()-1);
                }
        }
        }
    }
    public static void man(String[] args) {
        int sum = 0;
        for (int i = 1; i <= 20; i++) {
            binary = new ArrayList<Integer>();
            genBinary(i);
            System.out.print("Done binary. ");
            ArrayList<Integer> y = new ArrayList<Integer>();
            y.add(1);
            int o = gen(y, i);
            sum += o;
            System.out.println(i + ", total so far: " + sum + ", size: " + o);
        }


    }

    public static void genBinary(int goal) {
        if (goal == 1) {
            if (!binary.contains(1)) {
                binary.add(0, 1);
            }
            return;
        }
        if (!binary.contains(0)) {
            binary.add(0, goal);
        }

        if (goal % 2 == 0) {
            genBinary(goal / 2);
            return;
        }
        genBinary(goal - 1);
    }

    public static ArrayList<Integer> x(ArrayList<Integer> alr, int goal) {
        ArrayList<Integer> r = new ArrayList<Integer>();
        int largest = alr.get(alr.size() - 1);
        for (int i = 0; i < alr.size(); i++) {
            for (int j = i; j < alr.size(); j++) {
                int k = alr.get(i) + alr.get(j);
                if (k <= goal && k > largest && !r.contains(k) && !alr.contains(k)) {
                    r.add(k);
                }
            }

        }
        return r;
    }
    public static int gen(ArrayList<Integer> alr, int goal) {
        if (alr.get(alr.size() - 1) > goal) {
            return -1;
        }
        if (alr.get(alr.size() - 1) == goal) {
            return alr.size()-1;
        }
        if (alr.size() >= binary.size()) {
            return -1;
        }
        if ((goal == 95 || goal == 127 || goal == 159 || goal == 167 || goal == 171 || goal >= 173) && alr.size() < 7) {
            System.out.println(alr);
        }
        int sum = -1;
        ArrayList<Integer> r = x(alr, goal);
        for (int k : r) {
            ArrayList<Integer> x = new ArrayList<Integer>(alr.size() + 1);
            for (int o : alr) {
                x.add(o);
            }
            x.add(k);
            int y = gen(x, goal);
                if (sum == -1 || y < sum) {
                    sum = y;
                }
            
        }
        return sum;
    }
}