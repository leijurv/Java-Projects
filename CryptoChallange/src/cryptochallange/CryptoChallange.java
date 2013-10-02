/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cryptochallange;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author leijurv
 */
public class CryptoChallange {
public static void main(String[] args){
    String message="the whole grain goodness of blue chip dividend stocks has its limits utility stocks consumer staples pipelines telecoms and real estate investment trusts have all lost ground over the past month even while the broader market has been flat with the bond market signalling an expectation of rising interest rates the five-year rally for steady blue-chip dividend payers has stalled should you be scared if you own a lot of these stocks either directly or through mutual funds or exchange-traded funds david baskin president of baskin financial services has a two pronged answer keep your top quality dividend stocks but be prepared to follow his firms example in trimming holdings in stocks such as transcanada corp keyera corp and pembina pipeline corp lets have mr baskin run us through his thinking on dividend stocks which are a big part of the portfolios his firm puts together for clients a mini manifesto";
    //String message="the wholesome";
    message=message.replace(" ","");
    message=message.replace("-","");
    String s="";
    ArrayList<Character> vowels=new ArrayList<Character>();
    vowels.add('a');
    vowels.add('e');
    vowels.add('i');
    vowels.add('o');
    vowels.add('u');
    vowels.add('y');
    for (int i=0; i<message.length(); i++){
        if (vowels.contains(message.charAt(i))){
            s=s+"1";
        }else{
            s=s+"0";
        }
    }
    System.out.println(s);
    //String s="001 00101 00110 01100100 10 0011 0010 01010100 001000 010 100 010100";
    
    String t="0010011000010100101001001001101010100101";
    String P="10 0011 1110 1010 0100 0011 1111 0000 0100 1010 0010 1000 1011 1010 0001 0111 1100 1111 0000 1111 1000 1100 0010 1000 0111 1110 11";
    String K="S NE E SE N NE S NW E NE E NE S SE N SW E SW N NE S NW S SE S SE S SE S NW W NE S SE E NE S NE E NE W NE S NW     W SE W NE N SW S SE S SW E SE E NW E SW N SE W NW S NE    E NE S NW N NW N SE E SE W SW N NW W NE S SE W SW N NE E NW   N SW E SW W NE W NW S NE N NW N SW N SE W NW E SE E SE W SE N SW W SW S NE    W SW E SE N NW N SE W SE S NE W SW E SE                N NE N NE N SW N NW W NE N NW S NE S SW W SE E NW N NE E NW S NW N SE N NE E NE W SW N SE E SE E SW S SW N NE S NE N NW         ";
    K=K+"S SE E NE N NW W NW W SE W SW S NE W NE S SE N NW E NW W NE W SE N SE W SE E NW W SE E NE N NE N SW N NE N NE ";
    K=K+"S NE S NW N  NW W SE S SE N NE S NW S NE W NW S NE N NW N NW W SW S SE S NE E NE W SE N NW";
    K=K.replace("SE","00");
    K=K.replace("SW","01");
    K=K.replace("NW","10");
    K=K.replace("NE","11");
    
    K=K.replace("N","00");
    K=K.replace("E","01");
    K=K.replace("S","10");
    K=K.replace("W","11");
    String q=P+K;
    //String q="10 0011 0010 0101 0010 1011 00";
    s=s.replace(" ","");
    q=q.replace(" ","");
    System.out.println(s.length()+","+q.length());
    ArrayList<String> r=new ArrayList<String>();
    ArrayList<BigInteger> S=new ArrayList<BigInteger>();
    ArrayList<BigInteger> T=new ArrayList<BigInteger>();
    ArrayList<BigInteger> U=new ArrayList<BigInteger>();
    String Q="";
    for (int i=0; i<q.length()-8 && i<s.length()-8; i+=8){
        
        S.add(new BigInteger(s.substring(i,i+8),2));
        T.add(new BigInteger(q.substring(i,i+8),2));
        U.add(new BigInteger(s.substring(i,i+8),2).xor(new BigInteger(q.substring(i,i+8),2)));
        String QQ=U.get(U.size()-1).toString(2);
        while(QQ.length()<8){
            QQ="0"+QQ;
        }
        Q=Q+QQ;
        r.add(QQ);
    }
    System.out.println(r);
    System.out.println(S);
    System.out.println(T);
    System.out.println(U);
    System.out.println(Q);
    //String[] m={"987654","QPONM3","REDCL2","SFABK1","TGHIJ0","UVWXYZ"};
    //String[] m={"ZYXWVU","0JIHGT","1KBAFS","2LCDER","3MNOPQ","456789"};
String[] m={"FEDCBA",
"GXWVUT",
"HY765S",
"IZ894R",
"J0123Q",
"KLMNOP"};
    System.out.println(Q.length());
    System.out.println(new BigInteger(Q,2).toString(6));
    System.out.println("MEOW");
    for (int i=0; i+6<Q.length(); i+=6){
        String a=Q.substring(i,i+3);
        int A=new BigInteger(a,2).intValue();
        String b=Q.substring(i+3,i+6);
        int B=new BigInteger(b,2).intValue();
        
        //System.out.print(A+""+B);
        //if (A<6 && B<6)
        System.out.print(m[B].charAt(A));
    }
    //System.out.println(new BigInteger("101101",2).xor(new BigInteger("11000",2)).toString(2));
}
    /**
     * @param args the command line arguments
     */
    public static void mai(String[] args) {
        String s="4454113454112333534454124243424432514121231131135315544254424442434432514153435432423441112551355334134243225343114454345343225134314214325134125334121554153451335144441122514442544244441534512355154321345111131121235142543153332142435144531534143451254253154433515432534144343513544";
        //String s="52515053513003403040503505110101";
        s=s.substring(0,s.length());
       ArrayList<Integer> S=new ArrayList<Integer>();
       ArrayList<String> R=new ArrayList<String>();
       HashMap<Integer,String> r=new HashMap<Integer,String>();
       //r.put(52,"s");
       //r.put(51,"t");
       //r.put(50,"a");
       //r.put(53,"r");
       
       r.put(44,"s");
       r.put(54,"t");
       r.put(11, "a");
       r.put(34,"r");
       
       r.put(21,"b");
       r.put(31, "c");
       r.put(41,"d");
       r.put(51,"e");
       
       r.put(12,"f");
       r.put(22, "g");
       r.put(32,"h");
       r.put(42,"i");
       r.put(52,"j");
       
       r.put(13,"k");
       r.put(23,"l");
       r.put(33,"m");
       r.put(43,"n");
       r.put(53,"o");
       
       r.put(14,"p");
       r.put(24,"q");
       
       r.put(15,"u");
       r.put(25,"v");
       r.put(35,"w");
       r.put(45,"x");
       r.put(55, "y");
       String[][] passcode=new String[5][5];
       for (int i=0; i<s.length()-2; i+=2){
           int n=Integer.parseInt(s.substring(i,i+2));
           S.add(n);
           if (r.get(n)!=null){
               passcode[Integer.parseInt(s.substring(i+1,i+2))-1][Integer.parseInt(s.substring(i,i+1))-1]=r.get(n);
               R.add(r.get(n));
           }else{
               R.add(s.substring(i,i+2));
           }
       }
       System.out.println(S);
       
       int[] X=new int[56];
       for (int a : S){
           X[a]++;
       }
       for (int i=0; i<X.length; i++){
           if (X[i]!=0){
               System.out.println((r.get(i)!=null?r.get(i):i)+":"+X[i]);
           }
       }
       System.out.println(r);
       for (String T : R){
           System.out.print(T);
       }
       System.out.println(R);
       for(String[] SS : passcode){
           for (String SSS : SS){
               System.out.print(SSS==null?"?":SSS);
           }
           System.out.println();
       }
        // TODO code application logic here
    }
}
