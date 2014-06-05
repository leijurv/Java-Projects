/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package p2pfilestorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static p2pfilestorage.P2PFileStorage.base;
/**
 *
 * @author leijurv
 */
public class StoredFile {
    String location;
    byte[] contentHash;
    int fileID;
    int numParts;
    int numBytes;
    long time;
    ArrayList<FilePart> parts;
    public StoredFile(String Loc, int FileID){
        location=Loc;
        File f=new File(base+location);
        if (!f.exists()){
            return;
        }
        fileID=FileID;
        read();
    }
    public void read(){
        File f=new File(base+location);
        time=f.lastModified();
        try {
            FileInputStream d=new FileInputStream(base+location);
            byte[] loc=location.getBytes();
            numBytes=d.available()+9+loc.length;
            numParts=numBytes/262144;
            if (numBytes%262144!=0){
                numParts++;//Needs to round up
            }
            parts=new ArrayList<FilePart>();
            for (int i=0; i<numParts; i++){
                byte[] b=new byte[0];
                if (i==0){
                    b=new byte[Math.min(d.available()+9+loc.length,262144)];
                    b[0]=(byte)(loc.length-129);
                    for (int n=0; n<loc.length; n++){
                        b[n+1]=loc[n];
                    }
                    byte[] mod=to8(new BigInteger(time+"").toByteArray());
                    for (int n=0; n<8; n++){
                        b[n+1+loc.length]=mod[n];
                    }
                    byte[] rest=new byte[Math.min(d.available(),262144-(9+loc.length))];
                    d.read(rest);
                    for (int n=0; n<rest.length; n++){
                        b[n+9+loc.length]=rest[n];
                    }
                }else{
                    b=new byte[Math.min(d.available(),262144)];
                    d.read(b);
                }
                FilePart fp=new FilePart(fileID,i,numParts,b);
                parts.add(fp);
            }
            d.close();
            contentHash=calcHash(location);
        } catch (Exception e){
            System.out.println(e);
        }
    }
    public boolean hasChanged(){
        byte[] a=calcHash(location);
        return diffHash(a);
    }
    public boolean diffHash(byte[] a){
        for (int i=0; i<a.length; i++){
            if (contentHash[i]!=a[i]){
                return true;
            }
        }
        return false;
    }
    public boolean exists(){
        return new File(base+location).exists();
    }
    public static byte[] calcHash(String loc){
        try {
            MessageDigest m=MessageDigest.getInstance("SHA-256");
            FileInputStream d=new FileInputStream(base+loc);
            byte[] r=new byte[d.available()];
            d.read(r);
            d.close();
            m.update(r);
            return m.digest();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }
    public void write(){
        try {
            int len=(int)parts.get(0).content[0]+129;
            byte[] name=new byte[len];
            for (int i=0; i<len; i++){
                name[i]=parts.get(0).content[i+1];
            }
            byte[] date=new byte[8];
            for (int i=0; i<8; i++){
                date[i]=parts.get(0).content[i+1+len];
            }
            FileOutputStream f = new FileOutputStream(base+new String(name));
            File F=new File(base+new String(name));
            for (int i=0; i<numParts; i++){
                if (i==0){
                    byte[] r=new byte[parts.get(0).content.length-(9+len)];
                    for (int n=0; n<r.length; n++){
                        r[n]=parts.get(0).content[n+(9+len)];
                    }
                    f.write(r);
                }else{
                    f.write(parts.get(i).content);
                }
            }
            f.close();
            F.setLastModified(new BigInteger(date).longValue());
        } catch (Exception ex) {
        } 
    }
    public byte[] to8(byte[] b){
        byte[] r=new byte[8];
        for (int i=0; i<b.length; i++){
            r[i+8-b.length]=b[i];
        }
        return r;
    }
}
