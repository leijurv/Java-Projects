/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author leijurv
 */
public class AIB_Client {
    static ArrayList<BigInteger> denominationPubKeys=new ArrayList<BigInteger>();
    static final BigInteger e=new BigInteger("65537");
    static final String RValuesPath=System.getProperty("user.home")+"/Dropbox/AIB/RValues";
    static final String DepositRequestsPath=System.getProperty("user.home")+"/Dropbox/AIB/DepositRequests";
    static final String webpage="http://localhost:5020/";
    static RValueStorage rValueStorage=new RValueStorage(RValuesPath);
    static ArrayList<DepositRequest> depositRequests=new ArrayList<DepositRequest>();
    static Gooey gooey;
    static BigInteger comPubKey;
    public static void getPublicKeys() throws Exception{
        String w=load(webpage+"getPubKeys/");
        String[] X=w.split(",");
        for (int i=0; i<X.length; i++){
            denominationPubKeys.add(new BigInteger(X[i],16));
        }
        String com=load(webpage+"getComPubKey");
        comPubKey=new BigInteger(com,16);
    }
    public static String load(String webpage) throws Exception{
        URL url = new URL(webpage);
URLConnection con = url.openConnection();
Reader r = new InputStreamReader(con.getInputStream());
StringBuilder buf = new StringBuilder();
while (true) {
  int ch = r.read();
  if (ch < 0)
    break;
  buf.append((char) ch);
}
return buf.toString();
    }
    public static void loadDepositRequests() throws Exception{
        FileInputStream X=new FileInputStream(DepositRequestsPath);
        while(X.available()>256){
            byte[] len=new byte[1];
            X.read(len);
            int i=new Integer(len[0]).intValue();
            byte[] W=new byte[257*i];
            X.read(W);
            depositRequests.add(new DepositRequest(i,W));
        }
        X.close();
    }
    public static void saveDepositRequests() throws Exception{
        File f=new File(DepositRequestsPath);
        if (!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(RValueStorage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        FileOutputStream W=new FileOutputStream(DepositRequestsPath);
        for (int i=0; i<depositRequests.size(); i++){
            W.write(depositRequests.get(i).export());
        }
        W.close();
    }
    public static void snip(BigInteger x){
        System.out.print(x.toString(16).substring(0,8)+"..."+x.toString(16).substring(x.toString(16).length()-8,x.toString(16).length()));
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        System.out.println("Loaded "+rValueStorage.denomIDs.size()+" R values worth "+new BigDecimal(rValueStorage.getTotalValue()).divide(new BigDecimal("100000000")).toString()+" BTC");
        getPublicKeys();
        System.out.println("Loaded "+denominationPubKeys.size()+" public keys");
        System.out.println("Loaded communication public key");
        loadDepositRequests();
        System.out.println("Loaded "+depositRequests.size()+" deposit requests");
        gooey=new Gooey();
    }
    
    
    public static void test(){
        RSAKeyPair One=new RSAKeyPair();
        One.generate(new BigInteger("11575260807657935844760722888927105060378474472495534442387025934038035763996895963679929383222825440869059204232374806845271757529713625423846260767101823"),new BigInteger("12631119597916887114387929982495073735131808240515480847673072658788929837561896656039101985416779769742572099744362141289109277326736683259302383410121967"),new BigInteger("65537"),true);
        RSAKeyPair PointOne=new RSAKeyPair();
        PointOne.generate(new BigInteger("10660797639087488923925753862897307498919663069378891598062107064684131402797580819210618060393199908545305008301778950291364344375947545672597313175799397"),new BigInteger("7752875718364166168019652366357431102339055427319514977068357249058536550138578297077592814240239733447310714069500833457384646144520929769235249691138537"),new BigInteger("65537"),true);
        //System.out.println(One.encode(new BigInteger("124524030925546340257258543597272072701631420034759129644333479235123216515685419496609357324841746481263830907104530940086457633153409471647946125656403945029719488025218954290845458480395801834325968247899967036723056681784116890910469060674150675497968643799875066856478502529948355201164627029271907753911")));
        denominationPubKeys.add(PointOne.modulus);
        denominationPubKeys.add(One.modulus);
        System.out.println(One.modulus.toByteArray().length);
        ArrayList<Integer> dValues=new ArrayList<Integer>();
        dValues.add(1);
        dValues.add(0);
        ArrayList<BigInteger> rValues=new ArrayList<BigInteger>();
        rValues.add(new BigInteger(1000,new Random()));
        rValues.add(new BigInteger(1000,new Random()));
        ArrayList<BigInteger> rPrimeValues=new ArrayList<BigInteger>();
        rPrimeValues.add(rValues.get(0).modPow(e,denominationPubKeys.get(dValues.get(0))));
        rPrimeValues.add(rValues.get(1).modPow(e,denominationPubKeys.get(dValues.get(1))));
        System.out.println(rPrimeValues.get(0));
        DepositRequest X=new DepositRequest(rPrimeValues,dValues);
        //byte[] WWW=x.export();
        //DepositRequest X=new DepositRequest(WWW[0],WWW);
        System.out.println(X.wValues.get(0));
        System.out.println(X.R2PrimeValues.get(0));
        OtherDepositRequest RRR=new OtherDepositRequest(X.toString());
        System.out.println(RRR.R2PrimeValues.get(0));
        ArrayList<BigInteger> R3PrimeValues=new ArrayList<BigInteger>();
        R3PrimeValues.add(One.decode(X.R2PrimeValues.get(0)));
        R3PrimeValues.add(PointOne.decode(X.R2PrimeValues.get(1)));
        System.out.println(R3PrimeValues.get(0));
        ArrayList<BigInteger> RVALUES=X.fufill(R3PrimeValues);
        System.out.println(RVALUES);
        System.out.println(Hex.encodeHexString(new byte[] {0}));
        rValueStorage.add(RVALUES, dValues);
        rValueStorage.save();
        System.out.println(rValueStorage.RValues);
        System.out.println(rValueStorage.denomIDs);
        System.out.println(rValues.get(0).equals(RVALUES.get(0)));
        System.out.println(rValues.get(1).equals(RVALUES.get(1)));
        System.out.println(One.modulus.bitLength());
    }
}