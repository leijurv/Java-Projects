/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sql;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.property.Photo;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author leijurv
 */
public class SQL {
    static Connection connection;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        Class.forName("org.postgresql.Driver");
        
        String s="SELECT DISTINCT\n" +
"    a.attnum as num,\n" +
"    a.attname as name,\n" +
"    format_type(a.atttypid, a.atttypmod) as typ,\n" +
"    a.attnotnull as notnull, \n" +
"    com.description as comment,\n" +
"    coalesce(i.indisprimary,false) as primary_key,\n" +
"    def.adsrc as default\n" +
"FROM pg_attribute a \n" +
"JOIN pg_class pgc ON pgc.oid = a.attrelid\n" +
"LEFT JOIN pg_index i ON \n" +
"    (pgc.oid = i.indrelid AND i.indkey[0] = a.attnum)\n" +
"LEFT JOIN pg_description com on \n" +
"    (pgc.oid = com.objoid AND a.attnum = com.objsubid)\n" +
"LEFT JOIN pg_attrdef def ON \n" +
"    (a.attrelid = def.adrelid AND a.attnum = def.adnum)\n" +
"WHERE a.attnum > 0 AND pgc.oid = a.attrelid\n" +
"AND pg_table_is_visible(pgc.oid)\n" +
"AND NOT a.attisdropped\n" +
"AND pgc.relname = 'users'  -- Your table name here\n" +
"ORDER BY a.attnum;";
        String leifvcf="BEGIN:VCARD\n" +
"VERSION:3.0\n" +
"PRODID:-//Apple Inc.//Mac OS X 10.9.4//EN\n" +
"N:Jurv;Leif;;;\n" +
"FN:Leif Jurv\n" +
"ORG:Ancestor;\n" +
"EMAIL;type=INTERNET;type=WORK;type=pref:leijurv@gmail.com\n" +
"TEL;type=CELL;type=VOICE;type=pref:6503876684\n" +
"X-ABUID:7E60A851-8D5A-40EB-AFF8-DCC42CE1A913:ABPerson\n" +
"END:VCARD";
        
        
        File ari=new File("/Users/leijurv/Documents/leif.vcf");
        /*
VCard vcard = Ezvcard.parse(ari).first();
Photo p=vcard.getPhotos().get(0);
byte[] dd=p.getData();
//ImageIO.write(ImageIO.read(aa),"png",new File("/Users/leijurv/Documents/ari.png"));
System.out.println(new String(dd));
        
        if (true){
            //return;
        }*/
        FileInputStream a=new FileInputStream(ari);
        byte[] d=new byte[a.available()];
        a.read(d);
        String arivcf=new String(d);
        String no="";
        String stat="";
        String[] dd=arivcf.split("PHOTO")[0].split("\n");
        for (String sadf : dd){
            if (sadf.startsWith("N:") || sadf.startsWith("FN:")){
                stat=stat+sadf+"\n";
            }else{
                no=no+sadf+"\n";
            }
        }
        //no=no;
        System.out.println(no);
        System.out.println(stat);
        //System.out.println(no);
        String image="PHOTO"+arivcf.split("PHOTO")[1].split("\nEND")[0];
       // System.out.println(stat+image);
        if(true){
            //return;
        }
        Random r=new Random();
        System.out.println("Starting to connect");
        connection=getConnection();
        System.out.println("Connected");
        //Scanner scan=new Scanner(System.in);
        //while(true){
        Statement stmt=connection.createStatement();
        //stmt.executeUpdate("ALTER TABLE profiles RENAME TO cards");
//stmt.executeUpdate("CREATE TABLE bookcontents (bookid text, cardid text)");  
//stmt.executeUpdate("ALTER TABLE books DROP COLUMN email");
//stmt.executeUpdate("ALTER TABLE users ADD static text");
//stmt.executeUpdate("ALTER TABLE books ADD PRIMARY KEY (id)");
        //stmt.executeUpdate("ALTER TABLE books ALTER COLUMN handle SET NOT NULL");
        //stmt.executeUpdate("UPDATE users SET static='"+(stat+image)+"' WHERE handle='leif'");
        //stmt.executeUpdate("UPDATE cards SET vcf='"+(no)+"' WHERE handle='leif'");
        //stmt.executeUpdate("INSERT INTO cards (name,handle,pin,vcf,id) VALUES('secondary','jack','5487','"+no+"','"+Math.abs(r.nextLong())+"')"); 
        //stmt.executeUpdate("INSERT INTO books (name,id,cardid,handle) VALUES('People Ive sent main to','"+Math.abs(r.nextLong())+"','5821550105447243271','leif')");
        //stmt.executeUpdate("DELETE FROM JettySessions WHERE accesstime<"+(System.currentTimeMillis()-30000));
        stmt.executeUpdate("DELETE FROM jettysessionids");
        stmt.executeUpdate("DELETE FROM jettysessions");
        //stmt.executeUpdate("UPDATE users SET password='casdfasdfats' WHERE handle='leif'");
        ResultSet rs=stmt.executeQuery("SELECT * FROM users");
            
            while (rs.next()){
                String statc=rs.getString("static");
                if (statc!=null){
                System.out.println(statc==null?null:statc.split("\n[a-zA-Z]").length);
                for (String S : statc.split("\n[a-zA-Z]")){
                    System.out.println(S.substring(0,Math.min(100,S.length())));
                }
               System.out.println(rs.getString("handle")+","+rs.getString("password")+","+rs.getString("email")+","+(statc==null?null:statc.substring(statc.length()-100,statc.length())));
            }}
            System.out.println();
            
            rs=stmt.executeQuery("SELECT * FROM cards");
            
            while (rs.next()){
                System.out.println(rs.getString("handle")+","+rs.getString("pin")+","+rs.getString("vcf")+","+rs.getString("id"));
            }
            
            rs=stmt.executeQuery("SELECT * FROM books");
            
            while (rs.next()){
                System.out.println(rs.getString("name")+","+rs.getString("handle")+","+rs.getString("id")+","+rs.getString("cardid"));
            }
            rs=stmt.executeQuery("SELECT * FROM bookcontents");
            
            while (rs.next()){
                System.out.println(rs.getString("bookid")+","+rs.getString("cardid")+",");
            }
            /*rs=stmt.executeQuery(s);
            
            while (rs.next()){
                System.out.println(rs.getString("num")+","+rs.getString("name")+","+rs.getString("typ")+","+rs.getString("notnull")+","+rs.getString("comment")+","+rs.getString("primary_key")+",");
            }*/
            
            //stmt.executeQuery("DELETE FROM JettySessions");
            //stmt.executeQuery("DELETE FROM JettySessionids");
        //}
        // TODO code application logic here
    }
    private static Connection getConnection() throws URISyntaxException,SQLException{
        URI dbUri=new URI("postgres://bhjhciorgwrnaz:E3t1kkAQzX3qaaozEyvqZodoE-@ec2-54-204-42-178.compute-1.amazonaws.com:5432/d669f23raf6bv4");
        String username=dbUri.getUserInfo().split(":")[0];
        String password=dbUri.getUserInfo().split(":")[1];
        String dbUrl="jdbc:postgresql://"+dbUri.getHost()+dbUri.getPath()+"?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
        return DriverManager.getConnection(dbUrl,username,password);
    }
    public static String getCards(String handle){
        try{
            Statement stmt=connection.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT * FROM cards WHERE handle='"+handle+"'");
            ArrayList<String> a=new ArrayList<String>();
            while(rs.next()){
                a.add('"'+rs.getString("pin")+'/'+rs.getString("name")+'"');
            }
            return a.toString();
        } catch (Exception ex){
        }
        return null;
    }
}
