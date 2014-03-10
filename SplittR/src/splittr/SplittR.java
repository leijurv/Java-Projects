/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package splittr;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Scanner;
import org.mathIT.approximation.Interpolation;

/**
 *
 * @author leijurv
 */
public class SplittR {
static final NetworkParameters MainNet=NetworkParameters.prodNet();
static final BigInteger modulus=new BigInteger("fffffffffffffffffffffffffffffffffffffffffffffffffffffffefffffc2f",16);//This is the modulus used by privkeys
public static BigInteger interpolate(BigInteger x, BigInteger[] X, BigInteger[] Y){
    BigInteger n=BigInteger.ZERO;
    for (int i=0; i<X.length; i++){
        n=n.add(Y[i].multiply(basis(x,i,X)));
    }
    return n.mod(modulus);
}
public static BigInteger basis(BigInteger x, int j, BigInteger[] X){
    BigInteger n=BigInteger.ONE;
    for (int i=0; i<X.length; i++){
        if (i!=j)
        n=n.multiply(x.subtract(X[i]).multiply(invert(X[j].subtract(X[i]))));
    }
    return n;
}
public static BigInteger invert(BigInteger i){
    return i.modPow(new BigInteger("-1"),modulus);
}
public static BigInteger eval(BigInteger[] poly, BigInteger x){
    BigInteger result=BigInteger.ZERO;
    for (int i=0; i<poly.length; i++){
        result=result.add(x.modPow(new BigInteger(i+""),modulus).multiply(poly[i]));
    }
    return result.mod(modulus);
}
public static void split(){
    Random r=new SecureRandom();
        BigInteger secret=new BigInteger("c4bbcb1fbec99d65bf59d85c8cb62ee2db963f0fe106f483d9afa73bd4e39a8a",16);
        System.out.println("Secret is "+secret.toString(16));
        System.out.println("Address is "+new ECKey(secret).toAddress(MainNet).toString());
        BigInteger[] cc=new BigInteger[4];
        BigInteger sum=BigInteger.ZERO;
        for (int i=0; i<cc.length-1; i++){
            cc[i]=new BigInteger(250,r);
            sum=sum.add(cc[i]);
        }
        cc[cc.length-1]=secret.subtract(sum).add(modulus).mod(modulus);//Sum of cc is equal to secret
        BigInteger[] X={new BigInteger("2"),new BigInteger("3"),new BigInteger("4"),new BigInteger("5"),new BigInteger("6")};
        // ^^ These numbers can be anything greater than 1.
        BigInteger[] Y=new BigInteger[X.length];
        for (int i=0; i<X.length; i++){
            Y[i]=eval(cc,X[i]);
            System.out.println(X[i].toString(16)+","+Y[i].toString(16));
        }
        System.out.println(eval(cc,new BigInteger("1")).toString(16));
}
public static void recombinate(){
    //Recombination
    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println("Recombinating");
        Scanner scan=new Scanner(System.in);
        BigInteger[] NewX=new BigInteger[4];
        BigInteger[] NewY=new BigInteger[4];
        for (int i=0; i<4; i++){
            System.out.print("Part? >");
            String R=scan.nextLine();
            NewX[i]=new BigInteger(R.split(",")[0],16);
            NewY[i]=new BigInteger(R.split(",")[1],16);
        }
        //Lagrange interpolation of X and Y coordinates
        BigInteger result=(interpolate(new BigInteger("1"),NewX,NewY));
        System.out.println("Secret is "+result.toString(16));
        System.out.println("Address is "+new ECKey(result).toAddress(MainNet).toString());
        
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        //Four out of Five
        
        split();
        recombinate();
        
    }
    
}
