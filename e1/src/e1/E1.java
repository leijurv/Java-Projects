/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package e1;

/**
 *
 * @author leijurv
 */
public class E1 {

    /**
     * @param args the command line arguments
     */
    static int inc=0;
    public static void main(String[] args) {
        int acc=0;
        while(inc<1000){
            if(inc%5==0||inc%3==0){
                acc=acc+inc;
            }
            inc++;
        }
        System.out.println(acc);
        // TODO code application logic here
    }
}
