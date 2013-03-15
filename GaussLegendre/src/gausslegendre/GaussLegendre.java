/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gausslegendre;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 *
 * @author leijurv
 */
public class GaussLegendre {
    static final MathContext m=new MathContext(5000);
static final BigDecimal TWO=new BigDecimal("2");
    static BigDecimal a=new BigDecimal("1");
    static BigDecimal b=(new BigDecimal("1")).divide(sqrt(TWO,m),m);
    static BigDecimal t=(new BigDecimal("1")).divide(new BigDecimal("4"));
    static BigDecimal p=new BigDecimal("1");
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        loop();
        loop();
        System.out.println(a.add(b).multiply(a.add(b)).divide(t.multiply(TWO.multiply(TWO)),m));
        // TODO code application logic here
    }
    public static void loop(){
        BigDecimal A=a.add(b).divide(TWO);
        BigDecimal B=sqrt(a.multiply(b),m);
        BigDecimal T=t.subtract(p.multiply(a.subtract(A).multiply(a.subtract(A))));
        BigDecimal P=TWO.multiply(p);
        a=A;
        b=B;
        t=T;
        p=P;
    }
    public static BigDecimal sqrt(BigDecimal x,MathContext mc) {//I copied this
    BigDecimal g = x.divide(TWO, mc);
    //System.out.print("Calculating the square root of approximately "+x +" calculated to "+mc.getPrecision()+" digits.");
	boolean done = false;
	final int maxIterations = mc.getPrecision() + 1;		
	for (int i = 0; !done && i < maxIterations; i++) {
		// r = (x/g + g) / 2
		BigDecimal r = x.divide(g, mc);
		r = r.add(g);
		r = r.divide(TWO);
		done = r.equals(g);
		g = r;
	}
        //System.out.println("It's approximately "+g);
	return g;
}
}
