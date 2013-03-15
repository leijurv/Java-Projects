/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package derivative;



/**
 *
 * @author leijurv
 */
public class Derivative {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Function f=new Add(new Multiply(new X(),new Constant(2)),new ToThePower(new X(), new Constant(10)));
        
        for (int i=0; i<6; i++){
        System.out.println(f);
        if (i==4){
        //Sec.simplifyToCos=true;
        //Tan.simplifyToSinCos=true;
        }
        f=f.derivitive().simplify();
        }
        //readUsername();
    }
}
