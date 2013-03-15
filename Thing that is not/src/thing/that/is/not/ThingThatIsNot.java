/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package thing.that.is.not;

/**
 *
 * @author leijurv
 */
public class ThingThatIsNot {

    /**
     * @param args the command line arguments
     */
    public static boolean prime(int x){
        if (x%2==0){
            return false;
        }
        int y=3;
        int s=(int)Math.round(Math.sqrt(x));
        while(y<=s){
            if (x%y==0){
                return false;
            }
            y=y+2;
        }
        return true;
    }
    public static void main(String[] args) {
        for (int i=0; i<1000; i++){
            int c=i*(i-1);
            c+=41;
            System.out.print(i+ " "+c+" ");
            System.out.println(Boolean.toString(prime(c)));
        }
    }
}
