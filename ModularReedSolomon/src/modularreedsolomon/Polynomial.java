/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modularreedsolomon;

/**
 *
 * @author leif
 */
public class Polynomial {
    int mod;
    int[] c;
    public Polynomial(int[] cs, int Mod){
        mod=Mod;
        int zeros=0;
        for (int i=cs.length-1; i>=0; i--){
            if (cs[i]==0){
                zeros++;
            }else{
                break;
            }
        }
        int[] r=new int[cs.length-zeros];
        for (int i=0; i<r.length; i++){
            r[i]=cs[i]%mod;
        }
        c=r;
    }
    public Polynomial add(Polynomial p){
        if (p.c.length>c.length){
            return p.add(this);
        }
        int[] r=new int[c.length];
        System.arraycopy(c, 0, r, 0, r.length);
        for (int i=0; i<p.c.length; i++){
            r[i]+=p.c[i];
        }
        return new Polynomial(r,mod);
    }
    public Polynomial subtract(Polynomial p){
        int[] r=new int[p.c.length];
        for (int i=0; i<r.length; i++){
            r[i]=mod-p.c[i];
        }
        return add(new Polynomial(r,mod));
    }
    public Polynomial multiply(Polynomial p){
        int[] r=new int[p.c.length+c.length-1];
        for (int i=0; i<c.length; i++){
            for (int n=0; n<p.c.length; n++){
                r[i+n]+=c[i]*p.c[n];
            }
        }
        return new Polynomial(r,mod);
    }
    public String toString(){
        String s="";
        for (int i=c.length-1; i>=0; i--){
            s=s+c[i]+"x^"+i+",";
        }
        return s;
    }
    public int eval(int x){
        int r=0;
        for (int i=0; i<c.length; i++){
            r+=c[i]*Math.pow(x,i);
        }
        return r%mod;
    }
}
