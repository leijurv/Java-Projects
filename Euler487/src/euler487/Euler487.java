/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package euler487;

import java.math.BigInteger;

/**
 *
 * @author leijurv
 */
public class Euler487 {
    public static long powerMod(long base, long pow, long mod){
        if (pow==1){
            return base%mod;
        }
        if (pow%2==0){
            long a=powerMod(base,pow/2,mod);
            return (a*a)%mod;
        }
        return (base*powerMod(base,pow-1,mod))%mod;
    }
    public static float T1(long n, long k, long p){
        long s=0;
        long a=n%p;
        long bb=a+1;
        long s1=0;
        for (long i=1; i<=p; i++){
            if (i%1000000==0){
                System.out.println(((double)i)/((double)p));
            }
            long pm=powerMod(i,k,p);
            long d=-i*pm;
            s=(d+s)%p;
            if (i<=a){
                long dd=bb*pm+d;
                s1=(s1+dd)%p;
            }
        }
        
        System.out.println(a+","+p);
        System.out.println(s);
        s=s*(n/p+1)-s;
        System.out.println(s%p);
        s+=s1;
        System.out.println(s1);
        //long v=new BigInteger("2").modPow(new BigInteger("-1"),new BigInteger(p+"")).longValue();
        //s=s*v;
        s=s%p;
        s=s+p*10;
        return s%p;
        /*Block[{s}, s = 0; 
 For[i = 1, i <= p, i++, 
  s += (-2 + 2 i - 2 n + p*Floor[(n - i)/p])*PowerMod[i, k, p]]; 
 s = s*(Floor[n/p]); 
 For[i = 1, i <= Mod[n, p], i++, 
  s += (-2 + 2 i - 2 n + p*Floor[(n - i)/p])*PowerMod[i, k, p]]; 
 s = s*-1/2; Mod[s, p]]*/
    }
    public static float T2(long n, long k, long p){//p*((n-i)/p+1)-p)
        long s=0;
        long ss=(n)%p;
        long a=n%p;
        long s1=0;
        for (long i=1; i<=p; i++){
            if (i%1000000==0){
                System.out.println(i);
            }
            long pm=powerMod(i,k,p);
            long kk=(-1+i-ss)%p;
            long d=-kk*pm;
            s=(d+s)%p;
            if (i<=a){
                long d1=-(-1+i-ss)*pm;
            s1=(d+s1)%p;
            }
            //System.out.println(i+","+s+","+d+","+kk+","+powerMod(i,k,p)+",");
        }
        s=s%p;
        s=s+p*10;
        s=s%p;
        System.out.println("cat"+s);
        s=s*(n/p+1)-s;
        s+=s1;
        System.out.println(s%p);
        //long v=new BigInteger("2").modPow(new BigInteger("-1"),new BigInteger(p+"")).longValue();
        //s=s*v;
        s=s%p;
        s=s+p*10;
        return s%p;
        /*Block[{s}, s = 0; 
 For[i = 1, i <= p, i++, 
  s += (-2 + 2 i - 2 n + p*Floor[(n - i)/p])*PowerMod[i, k, p]]; 
 s = s*(Floor[n/p]); 
 For[i = 1, i <= Mod[n, p], i++, 
  s += (-2 + 2 i - 2 n + p*Floor[(n - i)/p])*PowerMod[i, k, p]]; 
 s = s*-1/2; Mod[s, p]]*/
    }
    public static long T(long n, long k, long p){//p*((n-i)/p+1)-p)
        long s=0;
        long ss=(n)%p;
        long a=n%p;
        long s1=0;
        /*
        for (long i=1; i<=p; i++){
            if (i%1000000==0){
                System.out.println(i);
            }
            long pm=powerMod(i,k,p);
            long kk=(-1+i-ss)%p;
            long d=-kk*pm;
            s=(d+s)%p;
            //System.out.println(i+","+s+","+d+","+kk+","+powerMod(i,k,p)+",");
        }*/
        s=s%p;
        s=s+p*10;
        s=s%p;
        System.out.println("cat"+s);
        s=s*(n/p+1);
        for (long i=a+1; i<=p; i++){
            long pm=powerMod(i,k,p);
            long kk=(-1+i-ss)%p;
            long d=-kk*pm;
            s=(s-d)%p;
        }
        
        s+=s1;
        System.out.println(s%p);
        //long v=new BigInteger("2").modPow(new BigInteger("-1"),new BigInteger(p+"")).longValue();
        //s=s*v;
        
        return s%p;
        /*Block[{s}, s = 0; 
 For[i = 1, i <= p, i++, 
  s += (-2 + 2 i - 2 n + p*Floor[(n - i)/p])*PowerMod[i, k, p]]; 
 s = s*(Floor[n/p]); 
 For[i = 1, i <= Mod[n, p], i++, 
  s += (-2 + 2 i - 2 n + p*Floor[(n - i)/p])*PowerMod[i, k, p]]; 
 s = s*-1/2; Mod[s, p]]*/
    }
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args){
        long[] primes={2000000011, 2000000033, 2000000063, 2000000087, 2000000089, 
2000000099, 2000000137, 2000000141, 2000000143, 2000000153, 
2000000203, 2000000227, 2000000239, 2000000243, 2000000269, 
2000000273, 2000000279, 2000000293, 2000000323, 2000000333, 
2000000357, 2000000381, 2000000393, 2000000407, 2000000413, 
2000000441, 2000000503, 2000000507, 2000000531, 2000000533, 
2000000579, 2000000603, 2000000609, 2000000621, 2000000641, 
2000000659, 2000000671, 2000000693, 2000000707, 2000000731, 
2000000741, 2000000767, 2000000771, 2000000773, 2000000789, 
2000000797, 2000000809, 2000000833, 2000000837, 2000000843, 
2000000957, 2000000983, 2000001001, 2000001013, 2000001043, 
2000001049, 2000001089, 2000001097, 2000001103, 2000001109, 
2000001119, 2000001127, 2000001137, 2000001149, 2000001151, 
2000001167, 2000001173, 2000001187, 2000001229, 2000001233, 
2000001247, 2000001257, 2000001277, 2000001287, 2000001349, 
2000001359, 2000001379, 2000001413, 2000001457, 2000001511, 
2000001517, 2000001527, 2000001539, 2000001551, 2000001557, 
2000001583, 2000001599, 2000001623, 2000001629, 2000001649, 
2000001677, 2000001727, 2000001743, 2000001821, 2000001833, 
2000001851, 2000001881, 2000001929, 2000001953, 2000001973};
        System.out.println(T(1077,40,5021));
        System.out.println(T((long) Math.pow(10,12),10000,primes[0]));
        BigInteger sum=BigInteger.ZERO;
        for (int i=0; i<primes.length; i++){
            long l=T((long) Math.pow(10,12),10000,primes[i]);
            sum=sum.add(new BigInteger(l+""));
            System.out.println("MEOWMEOW"+i+","+primes.length);
        }
        System.out.println(sum);
        // TODO code application logic here
    }
    
}
