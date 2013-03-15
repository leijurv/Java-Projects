package javasim;
/**
 * A parentheses handler.
 * 
 * @author leif
 */
public abstract class ParenthesisHandler {
    public ParenthesisHandler(){
        
    }
    
    public abstract int evalnopar(String a);
    /**
     * Evaluates a String
     * @param a The String to be evaluated
     * @return The evaluated value
     */
    public int eval(String a){
        int[] pl=new int[0];
        int[] spar=new int[0];
        int[] epar=new int[0];
        for (int i=0; i<a.length(); i++){
            String c=a.substring(i,i+1);
            if (c.equals("(")){
                pl=addendint(pl,i);
            }
            if (c.equals(")")){
                spar=addendint(spar,pl[pl.length-1]);
                pl=delend(pl);
                epar=addendint(epar,i);
            }
        }
        if (spar.length==0){
            return evalnopar(a);
        }
        String b=a;
        String p=a.substring(spar[0]+1,epar[0]);
        int q=evalnopar(p);
        String w=Integer.toString(q);
        b=b.substring(0,spar[0])+w+b.substring(epar[0]+1,b.length());
        return eval(b);
    }
    protected static int[] delend(int[] a){
        int[] c=new int[a.length-1];
        System.arraycopy(a, 0, c, 0, a.length-1);
        return c;
    }  
    protected static int[] addendint(int[] a, int b){
        
        int[] c=new int[a.length+1];
        System.arraycopy(a, 0, c, 0, a.length);
        c[a.length]=b;
        return c;
    }
    protected static boolean contains(String[] a, String b){
        for (int i=0; i<a.length; i++){
            if (a[i].equals(b)){
                return true;
            }
        }
        return false;
    }
    protected static int index(String[] a, String b){
        for (int i=0; i<a.length; i++){
            if (a[i].equals(b)){
                return i;
            }
        }
        return -1;
    }
}
