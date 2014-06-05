/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package p2pfilestorage;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 *
 * @author leijurv
 */
public class P2PFileStorage {
    static final String base="/Users/leijurv/Desktop/Files/";
    static byte[] pubKey=new byte[128];
    static ArrayList<StoredFile> files=new ArrayList<StoredFile>();
    public static void scan(){
        File Base=new File(base);
        ArrayList<String> contents=new ArrayList<String>();
        ArrayList<File> directories=new ArrayList<File>();
        directories.add(Base);
        while(!directories.isEmpty()){
            File f=directories.remove(0);
            for (File F : f.listFiles()){
                if (F.isDirectory()){
                    directories.add(F);
                }else{
                    contents.add(F.getPath().substring(base.length(),F.getPath().length()));
                }
            }
        }
        System.out.println(contents);
        for (String Loc : contents){
            byte[] hash=StoredFile.calcHash(Loc);
            System.out.println("Read "+Loc+":"+Hex.encodeHexString(hash));
            boolean done=false;
            for (StoredFile file : files){
                if (!done && !file.diffHash(hash) && !file.exists()){
                    System.out.println("Rename from "+file.location+" to "+Loc);
                    file.location=Loc;
                    file.read();//Full reread is required because rename could have changed internal header length
                }
            }
            for (StoredFile file : files){
                if (file.location.equals(Loc)){
                    //Already has file
                    if (file.diffHash(hash)){
                        if (file.time<new File(base+Loc).lastModified()){
                            System.out.println("Updated file "+Loc);
                            file.read();
                        }else{
                            System.out.println("Outdated file "+Loc);
                            file.write();
                        }
                    }
                    done=true;
                }
            }
            if (!done){
                StoredFile f=new StoredFile(Loc,files.size());
                files.add(f);
                System.out.println("Added new file "+Loc);
                //New file
            }
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        scan();
        Scanner scan=new Scanner(System.in);
        scan.nextLine();
        scan();
        // TODO code application logic here
    }
    
}
