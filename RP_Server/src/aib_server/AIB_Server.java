/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_server;

import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leif
 */
public class AIB_Server {
    static final BigInteger e=new BigInteger("65537");
    static final String Addresses=System.getProperty("user.home")+"/Dropbox/AIB_server/Addresses";
    static ArrayList<Address> addresses=new ArrayList<Address>();
    static RSAKeyPair comKeyPair=new RSAKeyPair();
    public static void setup(){
        addresses.add(new Address(new BigInteger("3233"),new BigInteger("5021"),"YOURFACE"));
        addresses.add(new Address(new BigInteger("10807"),new BigInteger("12340000"),"YOURLIFE"));
comKeyPair.pri=new BigInteger("83374156446774429873694550481645812108834138335187533508679883506841478344454653407583462002728379160005936010232152845900203607261418110281030190618566505266984514447967618960101472648126527225429103019628649092001207451156000110046429132143123004788038578362511400808819284172666518163971226737327430819985");
comKeyPair.pub=e;
comKeyPair.modulus=new BigInteger("95812518035600410503994805360704581672073207036300573104181267870519734981509839211152174319605986165096863495810633205242273997599318920084305802356152031410095278243554997838911172683159778396002043381602230364061683476559432122157675368167117435777718607888823487551080217245394174274812214070625155618899");
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        setup();
        ServerSocket S=new ServerSocket(5020);
        boolean running=true;
        System.out.println("Server running. =D");
        while(running){
            Socket s=S.accept();
            (new RequestThread(s)).start();
        }
    }
    public static boolean verify(byte[] b,byte[] c,BigInteger modulus) throws NoSuchAlgorithmException{

        RSAKeyPair comPubKey=new RSAKeyPair();
            comPubKey.modulus=modulus;
            comPubKey.pub=new BigInteger("17");
                MessageDigest m=MessageDigest.getInstance("SHA1");
                m.reset();
                m.update(b);
                byte[] sig=m.digest();
                System.out.println("Hsh: "+new BigInteger(sig).mod(modulus).toString(16));
                BigInteger k=comPubKey.encode(new BigInteger(c));
                System.out.println("Sig: "+k.toString(16));
                return k.compareTo(new BigInteger(sig).add(modulus).mod(modulus))==0;
    }
    public static byte[] hash(byte[] r){
        try {
            MessageDigest m=MessageDigest.getInstance("SHA1");
            m.reset();
            m.update(r);
            byte[] sig=m.digest();
            return sig;
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Son, you're an IDIOT");
        }
        return null;
    }
    public static String processTx(String s){
        System.out.println("Processing SendTX");
        if (s.length()!=0){
            byte[] total=Hex.decodeHex(s.toCharArray());
            int inputs=total[0];
            System.out.println(inputs+" inputs");
            System.out.println("Query length "+total.length);
            byte[] sigs=new byte[inputs*128];
            byte[] toats=new byte[total.length-sigs.length];
            for (int i=0; i<toats.length; i++){
                toats[i]=total[i];
            }
            for (int i=0; i<128*inputs; i++){
                sigs[i]=total[i+toats.length];
            }
            
            
            byte[][] input=new byte[inputs][128];
            for (int i=0; i<128; i++){
                for (int j=0; j<inputs; j++){
                    byte r=total[1+i+j*138];
                    //System.out.println(j+","+i);
                    input[j][i]=r;
                }
            }
            byte[][] inAmts=new byte[inputs][10];
            for (int i=0; i<10; i++){
                for (int j=0; j<inputs; j++){
                    inAmts[j][i]=total[129+i+j*138];
                }
            }
            BigInteger[] inAmt=new BigInteger[inputs];
            for (int i=0; i<inputs; i++){
                inAmt[i]=new BigInteger(inAmts[i]);
            }
            BigInteger toatInput=BigInteger.ZERO;
            for (int i=0; i<inputs; i++){
                toatInput=toatInput.add(inAmt[i]);
            }
            for (int i=0; i<inputs; i++){
                System.out.println("Input from "+new BigInteger(input[i]).toString(16)+" with value "+inAmt[i]);
            }
            System.out.println("Total input value "+toatInput);
            
            
            byte[] out=slice(total,138*inputs+1);
            int outputs=out[0];
            System.out.println(outputs+" outputs");
            byte[][] output=new byte[outputs][128];
            for (int i=0; i<128; i++){
                for (int j=0; j<outputs; j++){
                    byte r=out[1+i+j*138];
                    //System.out.println(j+","+i);
                    output[j][i]=r;
                }
            }
            byte[][] outAmts=new byte[outputs][10];
            for (int i=0; i<10; i++){
                for (int j=0; j<outputs; j++){
                    outAmts[j][i]=out[129+i+j*138];
                }
            }
            BigInteger[] outAmt=new BigInteger[outputs];
            for (int i=0; i<outputs; i++){
                outAmt[i]=new BigInteger(outAmts[i]);
            }
            for (int i=0; i<outputs; i++){
                System.out.println("Output to "+new BigInteger(output[i])+" with value "+outAmt[i]);
            }
            BigInteger toatOutput=BigInteger.ZERO;
            for (int i=0; i<outputs; i++){
                toatOutput=toatOutput.add(outAmt[i]);
            }
            System.out.println("Total output value "+toatOutput);
            
            if (toatOutput.compareTo(toatInput)==0){
                System.out.println("Total input equal to total output YAY");
            }else{
                System.out.println("Input value unequal to output value!! NO");
            }
            
            System.out.println("Verifying inputs");
            byte[][] sig=new byte[inputs][128];
            for (int i=0; i<128; i++){
                for (int j=0; j<inputs; j++){
                    sig[j][i]=total[toats.length+i+j*128];
                }
            }
            byte[] hash=hash(toats);
            System.out.println("Hash: "+Hex.encodeHexString(hash));
            boolean valid=true;
            for (int i=0; i<inputs; i++){
            try {
                System.out.println("Sig "+i+" has sig "+new BigInteger(sigs).toString(16));
                if (!verify(toats,sig[i],new BigInteger(input[i]))){
                    System.out.println("Invalid signature for input #"+i);
                    valid=false;
                    break;
                }
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(AIB_Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
            if (!valid){
                return "Your mom";
            }
            BigInteger[] inputAddr=new BigInteger[inputs];
            for (int i=0; i<inputs; i++){
                inputAddr[i]=new BigInteger(input[i]);
            }
            BigInteger[] outputAddr=new BigInteger[outputs];
            for (int i=0; i<outputs; i++){
                outputAddr[i]=new BigInteger(output[i]);
            }
            Address[] inputA=new Address[inputs];
            Address[] outputA=new Address[outputs];
            for (Address a : addresses){
                for (int i=0; i<inputs; i++){
                    if (a.address.compareTo(inputAddr[i])==0){
                        inputA[i]=a;
                    }
                }
                for (int i=0; i<outputs; i++){
                    if (a.address.compareTo(outputAddr[i])==0){
                        outputA[i]=a;
                    }
                }
            }
            BigInteger[] inputAddrValues=new BigInteger[inputs];
            for (int i=0; i<inputs; i++){
                if (inputA[i]!=null){
                    System.out.println("Input "+i+" matched to address "+inputA[i].address+" with value "+inputA[i].value);
                    inputAddrValues[i]=inputA[i].value;
                }else{
                    System.out.println("Unable to match input "+i+" with address "+inputAddr[i]+", transaction will probably fail");
                    inputAddrValues[i]=BigInteger.ZERO;
                }
                if (inAmt[i].compareTo(inputAddrValues[i])==1){
                    System.out.println("Not enough in address "+inputA[i].address+": "+inputAddrValues[i]+", needed at least"+inAmt[i]);
                    return "";
                }else{
                    if (inputA[i]!=null){
                        System.out.print("Subtracting "+inAmt[i]+" from address "+inputA[i].address+", bringing balance to ");
                        inputA[i].value=inputA[i].value.subtract(inAmt[i]);
                        System.out.println(inputA[i].value);
                    }
                }
            }
            for (int i=0; i<outputs; i++){
                if (outputA[i]==null){
                    System.out.println("Unable to match output "+i+" with address "+outputAddr[i]+", creating new Address");
                    addresses.add(new Address(outputAddr[i],BigInteger.ZERO,"Cadsf"));
                    outputA[i]=addresses.get(addresses.size()-1);
                }
                if (outputA[i]!=null){
                    System.out.println("Output "+i+" matched to address "+outputA[i].address+" with value "+outputA[i].value);
                    System.out.println("Adding value "+outAmt[i]);
                    outputA[i].value=outputA[i].value.add(outAmt[i]);
                }
            }
        }
        return "";
    }
    public static byte[] slice(byte[] input, int amt){
        byte[] output=new byte[input.length-amt];
        for (int i=0; i<output.length; i++){
            output[i]=input[i+amt];
        }
        return output;
    }
    public static String processGetAddr(String s){
        if (s.length()!=0){
        BigInteger b=new BigInteger(s,16);
        
        String addr="";
        //System.out.println(b);
        for (Address a : addresses){
            if (a.address.compareTo(b)==0){
                addr=a.depAddr;
            }
        }
        while(addr.length()<31){
            addr=" "+addr;
        }
        byte[] yourmom=new byte[128+31+128];
        byte[] B=to128(b);
        for (int i=0; i<128; i++){
            yourmom[i]=B[i];
        }
        for (int i=0; i<31; i++){
            yourmom[i+128]=addr.getBytes()[i];
        }
        for (int i=0; i<128; i++){
            yourmom[i+128+31]=0;
        }
            try {
                MessageDigest m=MessageDigest.getInstance("SHA1");
                m.reset();
                m.update(yourmom);
                byte[] sig=m.digest();
                //System.out.println("Hsh: "+new BigInteger(sig).toString(16));
                BigInteger k=comKeyPair.decode(new BigInteger(sig));
                //System.out.println("Sig: "+k.toString(16));
                byte[] S=to128(k);
                
                for (int i=0; i<128; i++){
                    yourmom[i+128+31]=S[i];
                }
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(AIB_Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println(yourmom.length);
            //System.out.println("=D "+Hex.encodeHexString(yourmom));
            return Hex.encodeHexString(yourmom);
        
        }
        return "";
    }
    public static String processGetBal(String s){
        if (s.length()!=0){
        BigInteger bb=new BigInteger(s,16);
        BigInteger b=BigInteger.ZERO;
        for (Address a : addresses){
            if (a.address.compareTo(bb)==0){
                b=a.value;
            }
        }
        byte[] a=toTen(b);
        byte[] yourmom=new byte[128+10+128];
        byte[] B=to128(bb);
        for (int i=0; i<128; i++){
            yourmom[i]=B[i];
        }
        for (int i=0; i<10; i++){
            yourmom[i+128]=a[i];
        }
        for (int i=0; i<128; i++){
            yourmom[i+128+10]=0;
        }
            try {
                MessageDigest m=MessageDigest.getInstance("SHA1");
                m.reset();
                m.update(yourmom);
                byte[] sig=m.digest();
                //System.out.println("Hsh: "+new BigInteger(sig).toString(16));
                BigInteger k=comKeyPair.decode(new BigInteger(sig));
                //System.out.println("Sig: "+k.toString(16));
                //byte[] S={};
                //if (k.toByteArray().length<=128){
                byte[] S=to128(k);
                //}
                //S=k.toByteArray();
                for (int i=0; i<128; i++){
                    yourmom[i+128+10]=S[i];
                }
            } catch (NoSuchAlgorithmException ex) {
            }
            //System.out.println(yourmom.length);
            //System.out.println("=D "+Hex.encodeHexString(yourmom));
            //System.out.println("NUR");
            return Hex.encodeHexString(yourmom);
        
        }
        //System.out.println("NUR");
        return "";
    }
    public static void snip(BigInteger x){
        System.out.print(x.toString(16).substring(0,8)+"..."+x.toString(16).substring(x.toString(16).length()-8,x.toString(16).length()));
    }
    public static byte[] to128(BigInteger x){
        byte[] X=new byte[128];
        byte[] xx=x.toByteArray();
        for (int i=0; i<xx.length; i++){
            if (xx.length==129){
                if (i<X.length){
                    X[i]=xx[i+1];
                }       
            }else{
                X[128-xx.length+i]=xx[i];
            }
        }
        return X;
    }
    public static byte[] toTen(BigInteger x){
        byte[] X=new byte[10];
        byte[] xx=x.toByteArray();
        for (int i=0; i<xx.length; i++){
            X[10-xx.length+i]=xx[i];
        }
        return X;
    }
}
