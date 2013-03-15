/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nottheory;

/**
 *
 * @author leijurv
 */
public class NotTheory {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Crossing a=new Crossing();
        Crossing b=new Crossing();
        Crossing c=new Crossing();
        a.belowNext=b;
        a.topNext=b;
        a.topPrev=c;
        a.belowPrev=c;
        b.topNext=c;
        b.topPrev=a;
        b.belowPrev=a;
        b.belowNext=c;
        c.topNext=a;
        c.topPrev=b;
        c.belowNext=a;
        b.belowPrev=b;
        a.tricolorable();
        // TODO code application logic here
    }
}
