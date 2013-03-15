/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package quaternion;

/**
 *
 * @author leijurv
 */
public class Quaternion {

    /**
     * @param args the command line arguments
     */
    double a;
    double i;
    double j;
    double k;
    public Quaternion(double A, double I, double J, double K){
        a=A;
        i=I;
        j=J;
        k=K;
    }
    public static void Zxmod(int l){
        for (int i=1; i<l; i++){
            if (coprime(i,l)){
            for (int n=1; n<l; n++){
                if (coprime(n,l)){
                int x=(n*i)%l;
                System.out.print(x);
                if (Integer.toString(x).length()==1){
                    System.out.print(" ");
                }
                System.out.print("  ");
                }
            }
            System.out.println();
            }
        }
    }
    public static boolean coprime(int a, int b){
        if (a==1 || b==1){
            return true;
        }
        
        for (int i=2; i<=a && i<=b; i++){    
            if (a%i==0 && b%i==0){
                return false;
            }
        }
        return true;
    }
    public static void main(String[] args) {
        System.out.println("Z x mod 16");
        int l=16;
        Zxmod(l);
        System.out.println("Z x mod 8");
        l=8;
        Zxmod(l);
        System.out.println("Z x mod 5");
        l=5;
        Zxmod(l);
        // TODO code application logic here
    }
}
