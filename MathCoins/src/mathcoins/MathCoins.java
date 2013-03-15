/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mathcoins;

/**
 *
 * @author leijurv
 */
public class MathCoins {

    /**
     * @param args the command line arguments
     */
    static String p1="HTH";
    static String p2="THT";
    static String[] t={"T","H"};
    static String[] a={"00"};
    public static int[] next(int[] prev, int c){
        int prev1=prev[0];
        int prev2=prev[1];
        String P1=p1.substring(0,prev1)+t[c];
        String P2=p2.substring(0,prev2)+t[c];
        String PP1=P1;
        
        String PP2=P2;
        int[] res={0,0};
        if (PP1.length()==1){
            if (p1.startsWith(PP1)){
                res[0]=1;
            }
        }
        
        if (PP1.length()==2){
            if (p1.startsWith(PP1)){
                res[0]=2;
            }
        }
        if (PP1.length()==3){
            if (p1.startsWith(PP1)){
                res[0]=3;
            }
        }
        if (PP2.length()==1){
            if (p2.startsWith(PP2)){
                res[1]=1;
            }
        }
        
        if (PP2.length()==2){
            if (p2.startsWith(PP2)){
                res[1]=2;
            }
        }
        if (PP2.length()==3){
            if (p2.startsWith(PP2)){
                res[1]=3;
            }
        }
        return res;
          
        
        
    }
    public static int[] t(String a){
        int[] r={Integer.parseInt(a.substring(0,1)),Integer.parseInt(a.substring(1,2))};
        return r;
    }
    public static String nex(String t, int a){
        int[] r=next(t(t),a);
        return Integer.toString(r[0])+Integer.toString(r[1]);
    }
    public static void add(String b){
        String[] ne=new String[a.length+1];
        for (int i=0; i<a.length; i++){
            ne[i]=a[i];
        }
        ne[a.length]=b;
        a=ne;
    }
    public static boolean contains(String b){
        for (int i=0; i<a.length; i++){
            if (a[i].equals(b)){
                return true;
            }
        }
        return false;
    }
    public static void main(String[] args) {
        int c=1;
        System.out.println("    H  T");
       for (int i=0; i<c; i++){
           String H=nex(a[i],1);
           String T=nex(a[i],0);
           System.out.print(p1.substring(0,t(a[i])[0])+" ");
           System.out.print(p2.substring(0,t(a[i])[1]));
           System.out.print(a[i]+": ");
           System.out.print(H);
           System.out.print(" ");
           System.out.println(T);
           if (!contains(T) && !T.substring(0,1).equals("3") && !T.substring(1,2).equals("3")){
               add(T);
           }
           if (!contains(H) && !H.substring(0,1).equals("3") && !H.substring(1,2).equals("3")){
               add(H);
           }
           c=a.length;
       }  
    }
}
