/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package firstbits;

import cryptolib.Hex;
import cryptolib.RSAKeyPair;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.SecureRandom;
import org.bouncycastle.math.ec.ECPoint;
import java.util.ArrayList;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
/**
 *
 * @author leif
 */
public class Firstbits{
    static ArrayList<String> words = new ArrayList<String>();
    
    public static void load(String path) throws Exception {
        DataInputStream in = new DataInputStream(new FileInputStream(path));
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        while ((strLine = br.readLine()) != null) {
            if (strLine.length()>0)
            words.add(strLine);
        }
        in.close();
    }
    public static void man(String[] args) throws Exception{
        
        RSAKeyPair One=new RSAKeyPair();
        One.generate(new BigInteger("11575260807657935844760722888927105060378474472495534442387025934038035763996895963679929383222825440869059204232374806845271757529713625423846260767101823"),new BigInteger("12631119597916887114387929982495073735131808240515480847673072658788929837561896656039101985416779769742572099744362141289109277326736683259302383410121967"),new BigInteger("65537"),true);
        RSAKeyPair PointOne=new RSAKeyPair();
        PointOne.generate(new BigInteger("10660797639087488923925753862897307498919663069378891598062107064684131402797580819210618060393199908545305008301778950291364344375947545672597313175799397"),new BigInteger("7752875718364166168019652366357431102339055427319514977068357249058536550138578297077592814240239733447310714069500833457384646144520929769235249691138537"),new BigInteger("65537"),true);
        System.out.println(One.encode(new BigInteger("124524030925546340257258543597272072701631420034759129644333479235123216515685419496609357324841746481263830907104530940086457633153409471647946125656403945029719488025218954290845458480395801834325968247899967036723056681784116890910469060674150675497968643799875066856478502529948355201164627029271907753911")));
        
    }
    public static void main(String[] args) throws Exception{
        X9ECParameters params = SECNamedCurves.getByName("secp256k1");
        ECDomainParameters ecParams = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());
        SecureRandom secureRandom = new SecureRandom();
        ECPoint G=ecParams.getG();
        ECPoint w=ecParams.getG();
        MessageDigest md=MessageDigest.getInstance("sha-256");
        for (int i=1; true; i++){
            byte[] SECPublic=w.getEncoded();
            byte[] RIPEMD=Utils.sha256hash160(SECPublic);
            if (RIPEMD[0]==0 && RIPEMD[1]==0 && RIPEMD[2]==0){
                String s=getaddress(RIPEMD,md);
                System.out.println(i+"<"+s);
            
                break;
            }
            if (i%10000==0)System.out.println(i);
            w=w.add(G);
        }
    }
    public static String getaddress(BigInteger privKey) throws Exception{
        
        X9ECParameters params = SECNamedCurves.getByName("secp256k1");
        ECDomainParameters ecParams = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());
        SecureRandom secureRandom = new SecureRandom();
        byte[] SECPublic=ecParams.getG().multiply(privKey).getEncoded();
        MessageDigest md=MessageDigest.getInstance("sha-256");
        byte[] RIPEMD=Utils.sha256hash160(SECPublic);
        return getaddress(RIPEMD,md);
    }
    public static String getaddress(byte[] RIPEMD,MessageDigest md){
        
        byte[] q=new byte[21];
        for (int i=0; i<RIPEMD.length; i++){
            q[21-RIPEMD.length+i]=RIPEMD[i];
        }
        
        md.reset();
        byte[] X=md.digest(q);
        md.reset();
        byte[] XX=md.digest(X);
        byte[] W=new byte[25];
        System.arraycopy(q, 0, W, 0, q.length);
        System.arraycopy(XX, 0, W, 21, 4);
        return (tasdf(new BigInteger(W)));
    }
    public static void customaddresses(String[] args) throws Exception{
        MessageDigest md=MessageDigest.getInstance("sha-256");
        
        String FirstYAY="1KittensPurringiHaveAKittens3wFmFr";
       String SecondYAY="1HereAnExampLEBitcoinAddress3KTNvn";
        String ThirdYAY="12345678912345678912345678913HPoG2";
        String FourthYAY="1HereExampLeBitcoinAddresses11111";
        String s=       "1234567891234567891234567892111111";
        boolean good=true;
        for (int i=0; i<s.length(); i++){
            if (pos.indexOf(s.substring(i,i+1))==-1){
                System.out.println("BAD CHARACTERS! YOU GET A TIME OUT!");
               good=false;
            }
        }
        if (good){
        System.out.println("GOOD CHARACTERS! YOU DID GOOD.");
        }
        System.out.println(parse(s,md));
        /*
        char[] X=pos.toCharArray();
        for (int i=0; i<58; i++){
            //System.out.println(i);
            for (int j=0; j<58; j++){
                if (parse(s+X[i]+X[j],md)){
                    System.out.println(s+X[i]+X[j]);
                }
            }
        }*/
        
        //System.out.println(parse("1BitcoinEaterAddressDontSendf59kuE",md));
    }
    public static boolean parse(String s,MessageDigest md){
        BigInteger x=BigInteger.ZERO;
        BigInteger y=BigInteger.ONE;
        for (int i=s.length()-1; i>=0; i--){
            x=x.add(y.multiply(new BigInteger(Integer.toString(pos.indexOf(s.substring(i,i+1))))));
            y=y.multiply(new BigInteger("58"));
        }
        byte[] q=x.toByteArray();
        if (q.length>25){
            return false;
        }
        
        byte[] N=new byte[25];
        for (int i=0; i<q.length; i++){
            N[i+25-q.length]=q[i];
        }
        
        byte[] B=new byte[21];
        System.arraycopy(N, 0, B, 0, B.length);
        md.reset();
        byte[] X=md.digest(B);
        md.reset();
        byte[] XX=md.digest(X);
        for (int i=0; i<4; i++){
            N[i+21]=XX[i];
        }
        System.out.println(Hex.encodeHexString(N));
        System.out.println(Hex.encodeHexString(B));
        System.out.println(Hex.encodeHexString(XX));
        if (N[0]==0){
            System.out.println("GOOD FIRSTBYTE! YOU DID GOOD.");
        }else{
            System.out.println("BAD FIRSTBYTE! YOU GET A TIME OUT!");
        }
        if (N[1]!=0){
            System.out.println("GOOD SECONDBYTE! YOU DID GOOD.");
        }else{
            System.out.println("BAD SECONDBYTE! YOU GET A TIME OUT!");
        }
        System.out.println(tasdf(new BigInteger(Hex.encodeHexString(XX).substring(0,8),16).subtract(new BigInteger(Hex.encodeHexString(N).substring(Hex.encodeHexString(N).length()-8,Hex.encodeHexString(N).length()),16))));
        System.out.println(tasdf(new BigInteger(N)));
        for (int i=0; i<4; i++){
            if (N[21+i]!=XX[i]){
                return false;
            }
        }
        return true;
    }
    static final String pos="123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    public static String asdf(BigInteger i){
        
        if (i.compareTo(new BigInteger("58"))==-1){
            return pos.substring(i.intValue(),i.intValue()+1);
        }
        return asdf(i.divide(new BigInteger("58")))+pos.substring(i.mod(new BigInteger("58")).intValue(),i.mod(new BigInteger("58")).intValue()+1);
    }
    public static String tasdf(BigInteger I){
        //int w=I.toByteArray().length;
        //System.out.println(I.toString(16));
        //System.out.println(I.toString(16).length());
        int w=I.toString(16).length();
        String s=asdf(I);
        //System.out.println(w);
        
        for (int i=(50-w)/2; i>0; i--){
            s="1"+s;
        }
        
        return s;
    }
    public static void mani(String[] args) throws Exception {


        load("/Users/leif/Documents/derp.txt");
        for (String s : words){
            System.out.println(works("http://blockchain.info/q/getfirstbits/",s)+","+works("http://blockchain.info/q/getreceivedbyaddress/",s));
        }

    }
    public static String works(String A,String s){
        URL url;

        try {
            // get URL content
String r="";
            String a=A+s;
            url = new URL(a);
            URLConnection conn = url.openConnection();

            // open the stream and put it into BufferedReader
            BufferedReader br = new BufferedReader(
                               new InputStreamReader(conn.getInputStream()));

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                r=inputLine;
            }
            br.close();
return r;

        } catch (Exception e){}
        return "";
    }
}