/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scom;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

/**
 *
 * @author leif
 */
public class SCom {

    /**
     * @param args the command line arguments
     */
    static RSAKeyPair mine=new RSAKeyPair();
    static String mename="";
    static BigInteger password=new BigInteger("5021");
    public static void main(String[] args) {
        //System.out.println((new Random()).nextInt(65536));
        readmyRSAkey();
        Scanner scan=new Scanner(System.in);
        System.out.println();
        String e;
        boolean n;
        do{
        System.out.print("Sending or recieving? (s/r)>");
        e=scan.nextLine();
        }while(!e.equals("s")&&!e.equals("r"));
        n=e.equals("s");
        System.out.print("Who is it "+(n?"to":"from")+"? >");
        String name=scan.nextLine();
        RSAKeyPair theirs=readRSAfile(System.getProperty("user.home")+"/Library/Application Support/scom/"+name+"_publicKey.rsakey");
        
        MessageCoder mc=new MessageCoder(mine,theirs);
        System.out.print("What's the message? >");
        String message=scan.nextLine();
        if (n){
            System.out.println(mc.encode(message));
            
        }else{
            System.out.println(mc.decode(message));
        }
        
    }
    public static ArrayList<String> readfile(String filename){
        ArrayList<String> result=new ArrayList<String>();
        try{
            FileInputStream fstream = new FileInputStream(filename);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                if (filename.equals(System.getProperty("user.home")+"/Library/Application Support/scom/"+mename+"key.rsakey")){
                    result.add(AES.decode(strLine, password));
                }else{
                result.add(strLine);
                }
            }
            in.close();
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return result;
    }
    public static void writefile(String filename, String thing){
        try {
            FileOutputStream a=new FileOutputStream(System.getProperty("user.home")+"/Library/Application Support/scom/"+filename);
            DataOutputStream b=new DataOutputStream(a);
            BufferedWriter c=new BufferedWriter(new OutputStreamWriter(b));
            c.flush();
            c.write(thing);
            c.close();
        } catch (IOException ex) {
            
        } 
    }
    public static void readmyRSAkey(){
        File thing=new File(System.getProperty("user.home")+"/Library/Application Support/scom/");
        thing.mkdirs();
        Scanner scan=new Scanner(System.in);
        File myname=new File(System.getProperty("user.home")+"/Library/Application Support/scom/name.txt");
        if (!myname.exists()){
            //Set mename to my name, somehow ask for it.
            System.out.print("What's your name? >");
            mename=scan.nextLine();
        try {
            FileOutputStream a=new FileOutputStream(System.getProperty("user.home")+"/Library/Application Support/scom/name.txt");
            DataOutputStream b=new DataOutputStream(a);
            BufferedWriter c=new BufferedWriter(new OutputStreamWriter(b));
            c.flush();
            c.write(mename);
            c.close();
        } catch (IOException ex) {
            
        } 
    
        }else{
            ArrayList<String> nameofme=readfile(System.getProperty("user.home")+"/Library/Application Support/scom/name.txt");
            mename=nameofme.get(0);
        }
        System.out.println("");
        File key=new File(System.getProperty("user.home")+"/Library/Application Support/scom/"+mename+"key.rsakey");
        if (!key.exists()){
            System.out.print("Set Password? (Letters and numbers) >");
        }else{
        System.out.print("Password? (Letters and numbers) >");
        }
        String pwd=scan.nextLine();
        password=new BigInteger(pwd.toLowerCase(),36);
        //TODO: Make this work on other platforms (not Mac)
        
        
        
        if (!key.exists()){
            
            Random rand=new Random();
            System.out.println("Generating a RSA key for you...");
            BigInteger e=new BigInteger(16,1,rand); //Uses prime in constructor because it doesn't really matter if it is prime (RSAKeyPair just finds the closest one)
            //TODO: Implement AKS primality search. (RSAKeyPair line 12). For this to work, P and Q *must* be prime. Ri
            BigInteger p=BigInteger.probablePrime(512,rand);
            BigInteger q=BigInteger.probablePrime(1536,rand);//So different so prevent Fermat Factorization
            
            //System.out.println(q.subtract(p));
            //System.out.println(BigInteger.TEN.pow(156).multiply(new BigInteger("2")));
            mine.generate(p,q,e,false);
            //System.out.println(mine);
            writeRSAfile(mename+"key.rsakey",mine);
            RSAKeyPair r=new RSAKeyPair();
            r.modulus=new BigInteger(mine.modulus.toString());
            r.pub=new BigInteger(mine.pub.toString());
            writeRSAfile(mename+"_publicKey.rsakey",r);
            System.out.println("Done");
        }else{
            mine=readRSAfile(System.getProperty("user.home")+"/Library/Application Support/scom/"+mename+"key.rsakey");
        }
        //System.out.println(mine);
    }
    public static void writeRSAfile(String filename, RSAKeyPair thing){
        try {
            FileOutputStream a=new FileOutputStream(System.getProperty("user.home")+"/Library/Application Support/scom/"+filename);
            DataOutputStream b=new DataOutputStream(a);
            BufferedWriter c=new BufferedWriter(new OutputStreamWriter(b));
            c.flush();
            //System.out.println(filename);
            if (filename.equals(mename+"key.rsakey")){
                String d=AES.encode(thing.tostring()[0],password);
                c.write(d);
                c.write("\n");
                c.write(AES.encode(thing.tostring()[1],password));
                c.write("\n");
                c.write(AES.encode(thing.tostring()[2],password));
                c.write("\n");
            }else{
            c.write(thing.tostring()[0]);
            c.write("\n");
            c.write(thing.tostring()[1]);
            c.write("\n");
            c.write(thing.tostring()[2]);
            
            }
            c.close();
        } catch (IOException ex) {
            
        } 
    }
    public static RSAKeyPair readRSAfile(String filename){
        RSAKeyPair thing=new RSAKeyPair();
        Object[] thin=readfile(filename).toArray();
        thing.modulus=new BigInteger((String)thin[0]);
        if(((String)thin[1]).substring(0,1).equals("1")){
            thing.pub=new BigInteger(((String)thin[1]).substring(1,((String)thin[1]).length()));
        }
        if(((String)thin[2]).substring(0,1).equals("1")){
            thing.pri=new BigInteger(((String)thin[2]).substring(1,((String)thin[2]).length()));
        }
        return thing;
    }
}
