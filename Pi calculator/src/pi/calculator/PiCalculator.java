/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pi.calculator;

import java.math.*;
import java.util.Scanner;

/**
 *
 * @author leijurv
 */
public class PiCalculator {

    /**
     * @param args the command line arguments
     */
    static final MathContext res=new MathContext(digits(),RoundingMode.HALF_EVEN);
    static int k=0;
    private static final BigDecimal TWO = new BigDecimal("2");
    static final BigDecimal roottwo=sqrt(new BigDecimal("2"),res);
    static BigDecimal[] y={roottwo.subtract(new BigDecimal("1"),res)};
    static BigDecimal[] a={new BigDecimal("6").subtract(new BigDecimal("4").multiply(roottwo))};
    static BigDecimal pi=new BigDecimal("1").divide(a[0],res);
    public static int digits(){
        System.out.println("Pi calculator. Using the Borwein altorithm (1985). Note that last few digits are usually wrong. Verbose mode on. Copyright May 9, 2012 Lurf Jurv.");
        Scanner scan=new Scanner(System.in);
        System.out.print("How many digits? (At least six) >");
        String n=scan.nextLine();
        int ans=-1;
        try{
        ans=Integer.parseInt(n);
        }catch(Exception e){
        }
        while(ans<6){
            System.out.println("Try again >");
           n=scan.nextLine();
        ans=-1;
        try{
        ans=Integer.parseInt(n);
        }catch(Exception e){
        }
        }
        
        
        return ans;
    } 
    public static void main(String[] args) {
        
        BigDecimal prevPi=new BigDecimal("0");
        System.out.println("Setup done.");
        while(!prevPi.equals(pi)){
            loop();
            prevPi=pi;
            pi=new BigDecimal("1").divide(a[k],res);
            System.out.println();
            System.out.println("Iteration: "+k);
        }
        System.out.println(pi);
    }
    public static void loop(){
        BigDecimal fyk=f(y[k]);
        System.out.println("Calculating the "+(k+1)+"th term of y");
        
        BigDecimal resultY=(new BigDecimal("1").subtract(fyk)).divide(new BigDecimal("1").add(fyk),res);
        y=add(y,resultY);
        System.out.println("Done. The "+(k+1)+"th term of y is approximately "+y[k+1].add(new BigDecimal("1")).subtract(new BigDecimal("1"),new MathContext(10)).toString());
        System.out.println("Calculating the "+(k+1)+"th term of a");
        BigDecimal resultA=(a[k].multiply((new BigDecimal("1").add(resultY)).pow(4))).subtract(TWO.pow(2*k +3).multiply(resultY).multiply(new BigDecimal("1").add(resultY).add(resultY.pow(2))),res);
        a=add(a,resultA);
        System.out.println("Done. The "+(k+1)+"th term of a is approximately "+a[k+1].add(new BigDecimal("1")).subtract(new BigDecimal("1"),new MathContext(10)).toString());
        k++;
    }
    public static BigDecimal f(BigDecimal a){
        System.out.println("Calculating f("+a.add(new BigDecimal("1")).subtract(new BigDecimal("1"),new MathContext(10)).toString()+")");
        return sqrt(sqrt(new BigDecimal("1").subtract(a.pow(4)),res),res);
    }
    
 
public static BigDecimal sqrt(BigDecimal x,MathContext mc) {//I copied this
    BigDecimal g = x.divide(TWO, mc);
    System.out.print("Calculating the square root of approximately "+x.add(new BigDecimal("1")).subtract(new BigDecimal("1"),new MathContext(10)) +" calculated to "+mc.getPrecision()+" digits.");
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
        System.out.println("It's approximately "+g.add(new BigDecimal("1")).subtract(new BigDecimal("1"),new MathContext(10)).toString());
	return g;
}
    public static BigDecimal[] add(BigDecimal[] a, BigDecimal b){
        BigDecimal[] c=new BigDecimal[a.length+1];
        System.arraycopy(a, 0, c, 0, a.length);
        c[a.length]=b;
        return c;
    }
    
    }


