package securecommunication;
import java.io.*;
import java.math.BigInteger;
import java.util.*;
public class SecureCommunication{//implements PacketListener?
    static User me=new User(new RSAKeyPair(),new Address(5,6,7,8),"me");
    static boolean usepackets=false;
    public static class Address{
        int p1=0;
        int p2=0;
        int p3=0;
        int p4=0;
        public Address(int a, int b, int c, int d){
            p1=a;
            p2=b;
            p3=c;
            p4=d;
        }
    }
    public static class User{
        RSAKeyPair keypair=new RSAKeyPair();
        Address address=new Address(0,0,0,0);
        String name="";
        public User(RSAKeyPair a, Address b, String Name){
            name=Name;
            keypair=a;
            address=b;
        }
    }
    public static class Packet{
        Address to=new Address(0,0,0,0);
        Address from=new Address(0,0,0,0);
        BigInteger packetID=BigInteger.ZERO;
        BigInteger totalPackets=packetID;
        BigInteger checksum=packetID;
        String data="";
        public Packet(Address From, Address two, BigInteger PacketID, BigInteger TotalPackets, String dAta){
            to=two;
            packetID=PacketID;
            totalPackets=TotalPackets;
            data=dAta;
            from=From;
            //Assuming data is 128 bits
            
        }
        public Packet(String t){
            String p1=t.substring(0,8);
            String p2=t.substring(8,16);
            String p3=t.substring(16,24);
            String p4=t.substring(24,32);
            String q1=t.substring(32,40);
            String q2=t.substring(40,48);
            String q3=t.substring(48,56);
            String q4=t.substring(56,64);
            String PacketID=t.substring(64,80);
            String TotalPackets=t.substring(80,96);
            String Checksum=t.substring(96,112);
            data=t.substring(112,112+128);
            packetID=new BigInteger(PacketID,2);
            totalPackets=new BigInteger(TotalPackets,2);
            checksum=new BigInteger(Checksum,2);
            to=new Address(Integer.parseInt(q1,2),Integer.parseInt(q2,2),Integer.parseInt(q3,2),Integer.parseInt(q4,2));
            from=new Address(Integer.parseInt(p1,2),Integer.parseInt(p2,2),Integer.parseInt(p3,2),Integer.parseInt(p4,2));
        
        }
        public boolean checkChecksum(){
            BigInteger x=checksum;
            calculateCheckSum();
            return (x.compareTo(checksum)==0);
        }
        public void calculateCheckSum(){
            checksum=new BigInteger(Integer.toString(to.p1+to.p2+to.p3+to.p4));
            checksum=checksum.add(packetID);
            checksum=checksum.add(totalPackets);
            checksum=checksum.add(new BigInteger(data,2));
            checksum=checksum.mod(new BigInteger("512"));
        }
        @Override
        public String toString(){
            String p1=Integer.toString(to.p1,2);
            String p2=Integer.toString(to.p2,2);
            String p3=Integer.toString(to.p3,2);
            String p4=Integer.toString(to.p4,2);
            while(p1.length()<8){
                p1="0"+p1;
            }
            while(p2.length()<8){
                p2="0"+p2;
            }
            while(p3.length()<8){
                p3="0"+p3;
            }
            while(p4.length()<8){
                p4="0"+p4;
            }
            String two=p1+p2+p3+p4;
            p1=Integer.toString(to.p1,2);
            p2=Integer.toString(to.p2,2);
            p3=Integer.toString(to.p3,2);
            p4=Integer.toString(to.p4,2);
            while(p1.length()<8){
                p1="0"+p1;
            }
            while(p2.length()<8){
                p2="0"+p2;
            }
            while(p3.length()<8){
                p3="0"+p3;
            }
            while(p4.length()<8){
                p4="0"+p4;
            }
            String From=p1+p2+p3+p4;
            String PacketID=packetID.toString(2);
            while(PacketID.length()<16){
                PacketID="0"+PacketID;
            }
            String TotalPackets=totalPackets.toString(2);
            while(TotalPackets.length()<16){
                TotalPackets="0"+TotalPackets;
            }
            String Checksum=checksum.toString(2);
            while(Checksum.length()<16){
                Checksum="0"+Checksum;
            }
            return From+two+PacketID+TotalPackets+Checksum+data;
        }
    }
    public static class Message{
        String content="";
        String encodedcontent="";
        Packet[] packets=new Packet[0];
        Address to=new Address(0,0,0,0);//If this is a recieved message, this is actually whom it was from
        RSAKeyPair recipient_key=me.keypair;//BIG TODO: Fix this.
        BigInteger session_key=BigInteger.ZERO;
        BigInteger encoded_session_key=BigInteger.ZERO;
        String encoded_sessionKey="";
        boolean confirm_sender=true;
        boolean recieved=false;
        public Message(String Content, Address To){
            content=Content;
            to=To;
            
        }
        public Message(Packet[] PacketsRecieved){
            recieved=true;
            packets=PacketsRecieved;
        }
        public void calculatedata(){
            orderPackets();
            //TODO: Check things like that all TotalPackets are the same, that all the to/from addresses are the same, and, of course, the checksum.
            if (usepackets){
                to=packets[0].to;
            }
            
            for (int i=0; i<packets.length; i++){
                encodedcontent=encodedcontent+packets[i].data;
            }
            sessionkeyrecieved();
            System.out.println(encodedcontent);
            System.out.println(session_key);
            content=AES.decode(encodedcontent,session_key);
        }
        public void orderPackets(){
            Packet[] ne=new Packet[packets.length];
            for (int i=0; i<packets.length; i++){
                for (int n=0; n<packets.length; n++){
                    if (packets[n].packetID.toString().equals(Integer.toString(i))){
                        ne[i]=packets[n];
                    }
                }
            }
            packets=ne;
        }
        public void calculatepackets(){
            sessionkeysent();
            encodedcontent=AES.encode(content,session_key);
            encodedcontent=encoded_sessionKey+encodedcontent;
            BigInteger length=new BigInteger(Integer.toString(encodedcontent.length()));
            BigInteger numberofpackets=length.divide(new BigInteger(Integer.toString(128)));
            BigInteger ID=BigInteger.ZERO;
            String newcontent=encodedcontent;
            while(encodedcontent.length()>128){
                addpacket(new Packet(me.address,to,ID,numberofpackets,encodedcontent.substring(0,128)));
                encodedcontent=encodedcontent.substring(128,encodedcontent.length());
                ID=ID.add(BigInteger.ONE);
            }
            addpacket(new Packet(me.address,to,ID,numberofpackets,encodedcontent));
            encodedcontent=newcontent;
        }
        public int mod(int a, int b){
            if (a<b){
                return a;
            }
            return mod(a-b,b);
        }
        public void sessionkeysent(){
            getRandSessionKey();
            encodesessionkey();
            encoded_sessionKey=encoded_session_key.toString(2);
            while(encoded_sessionKey.length()<2048){
                encoded_sessionKey="0"+encoded_sessionKey;
            }
        }
        public void sessionkeyrecieved(){
            encoded_sessionKey=encodedcontent.substring(0,2048);
            encoded_session_key=new BigInteger(encoded_sessionKey,2);
            encodedcontent=encodedcontent.substring(2048,encodedcontent.length());
            decodesessionkey();
        }
        public void encodesessionkey(){
            if (confirm_sender){
                encoded_session_key=me.keypair.decode(recipient_key.encode(session_key));
            }else{
                encoded_session_key=recipient_key.encode(session_key);
            }
        }
        public void decodesessionkey(){
            if (confirm_sender){
                session_key=me.keypair.decode(recipient_key.encode(encoded_session_key));
            }else{
                session_key=me.keypair.decode(encoded_session_key);
            }
        }  
        public void getRandSessionKey(){
            session_key=new BigInteger(256,new Random());
        }
        public void addpacket(Packet packet){
            Packet[] n=new Packet[packets.length+1];
            System.arraycopy(packets, 0, n, 0, n.length-1);
            n[n.length-1]=packet;
            packets=n;
        }
        
    }
    public static String[] addstring(String packet, String[] packets){
            String[] n=new String[packets.length+1];
            System.arraycopy(packets, 0, n, 0, n.length-1);
            n[n.length-1]=packet;
            return n;
        }
    public static RSAKeyPair readRSAfile(String filename){
        RSAKeyPair thing=new RSAKeyPair();
        String[] thin=readfile(filename);
        thing.modulus=new BigInteger(thin[0]);
        if(thin[1].substring(0,1).equals("1")){
            thing.pub=new BigInteger(thin[1].substring(1,thin[1].length()));
        }
        if(thin[2].substring(0,1).equals("1")){
            thing.pri=new BigInteger(thin[2].substring(1,thin[2].length()));
        }
        return thing;
    }
    public static Address readAddressFile(String filename){
        Address result=new Address(0,0,0,0);
        int ind=0;
        try{
            FileInputStream fstream = new FileInputStream(filename);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                switch(ind){
                    case 0:
                        result.p1=Integer.parseInt(strLine);
                    break;
                    case 1:
                        result.p2=Integer.parseInt(strLine);
                    break;
                    case 2:
                        result.p3=Integer.parseInt(strLine);
                    break;
                    case 3:
                        result.p4=Integer.parseInt(strLine);
                    break;
                }
                ind++;
            }
            in.close();
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return result;
    }
    public static String[] readfile(String filename){
        String[] result=new String[0];
        try{
            FileInputStream fstream = new FileInputStream(filename);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                result=addstring(strLine,result);
            }
            in.close();
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return result;
    }
    public static void writeRSAfile(String filename, RSAKeyPair thing){
        try {
            FileOutputStream a=new FileOutputStream(System.getProperty("user.home")+"/Library/Application Support/scom/"+filename);
            DataOutputStream b=new DataOutputStream(a);
            BufferedWriter c=new BufferedWriter(new OutputStreamWriter(b));
            c.flush();
            c.write(thing.tostring()[0]);
            c.write("\n");
            c.write(thing.tostring()[1]);
            c.write("\n");
            c.write(thing.tostring()[2]);
            c.close();
        } catch (IOException ex) {
            
        } 
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
    public static void readMe(){
        readmyRSAkey();
    }
    public static void readmyRSAkey(){
        //TODO: Make this work on other platforms (not Mac)
        File thing=new File(System.getProperty("user.home")+"/Library/Application Support/scom/");
        thing.mkdirs();
        File key=new File(System.getProperty("user.home")+"/Library/Application Support/scom/"+me.name+"key.rsakey");
        if (!key.exists()){
            Scanner scan=new Scanner(System.in);
            Random rand=new Random();
            System.out.println("Generating a RSA key for you...");
            BigInteger e=new BigInteger(16,1,rand); //Uses prime in constructor because it doesn't really matter if it is prime (RSAKeyPair just finds the closest one)
            //TODO: Implement AKS primality search. (RSAKeyPair line 12). For this to work, P and Q *must* be prime. Ri
            BigInteger p=BigInteger.probablePrime(512,rand);
            BigInteger q=BigInteger.probablePrime(1536,rand);//So different so prevent Fermat Factorization
            
            System.out.println(q.subtract(p));
            System.out.println(BigInteger.TEN.pow(156).multiply(new BigInteger("2")));
            me.keypair.generate(p,q,e,true);
            writeRSAfile(me.name+"key.rsakey",me.keypair);
            System.out.println("Done");
        }else{
            me.keypair=readRSAfile(System.getProperty("user.home")+"/Library/Application Support/scom/"+me.name+"key.rsakey");
        }
    }
    public static void readOtherRSAkeys(){
        File dir=new File(System.getProperty("user.home")+"/Library/Application Support/scom/");
        File[] contents=dir.listFiles();
        
    }
    public static void testpackets(){
        Message message=new Message("cats are really awesome",new Address(5,0,0,0));
        message.calculatepackets();
        String messag="";
        for (int i=0; i<message.packets.length; i++){
            messag=messag+message.packets[i].toString();
        }
        Packet[] thing=new Packet[message.packets.length];
        for (int i=0; i<message.packets.length; i++){
            thing[i]=new Packet(messag.substring(i*240,(i+1)*240));
        }
        Message recieved=new Message(thing);
        
        recieved.calculatedata();
        System.out.println(recieved.content);
    }
    public static void messageDotTxt(){
        String t=readfile(System.getProperty("user.home")+"/Library/Application Support/scom/"+"message.txt")[0];
        if (t.substring(0,1).equals("0") || t.substring(0,1).equals("1")){
            System.out.println("Decoding file Library/Application Suppory/scom/message.txt ...");
            Message recieved;
            if (usepackets){
                Packet[] thing=new Packet[t.length()/240];
                for (int i=0; i<t.length()/240; i++){
                    thing[i]=new Packet(t.substring(i*240,(i+1)*240));
                }
                recieved=new Message(thing);
            }else{
                recieved=new Message(new Packet[0]);
                recieved.recipient_key=readRSAfile(System.getProperty("user.home")+"/Library/Application Support/scom/recipientkey.rsakey");
                recieved.encodedcontent=t;
            }
            recieved.calculatedata();
            writefile("message.txt",recieved.content);
        }else{
            System.out.println("Encoding file Library/Application Suppory/scom/message.txt ...");
            Message message=new Message(t,new Address(5,6,2,4));
            message.recipient_key=readRSAfile(System.getProperty("user.home")+"/Library/Application Support/scom/recipientkey.rsakey");
            //!!!!!DELETE NEXT TWO LINES WHEN COMPILING!!!!! (For testing only)
            me.keypair=readRSAfile(System.getProperty("user.home")+"/Library/Application Support/scom/recipientkey.rsakey");
            message.recipient_key=readRSAfile(System.getProperty("user.home")+"/Library/Application Support/scom/mekey.rsakey");
            message.calculatepackets();
            String messag="";
            for (int i=0; i<message.packets.length; i++){
                messag=messag+message.packets[i].toString();
            }
            if (!usepackets){
                messag=message.encodedcontent;
            }
            writefile("message.txt",messag);
        }
    }
    public static void main(String[] args) {
        readMe();
        messageDotTxt();
        //BIG TODO: encoded message length may overflow int, packet array length may overflow int, cannot think of fix for this.
    }
}