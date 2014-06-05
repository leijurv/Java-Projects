/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package p2pfilestorage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leijurv
 */
public class FilePart {
    byte[] pubKey;
    int fileID;
    int partID;
    int numParts;
    long time;
    int nonce;
    byte[] contentHash;
    byte[] content;
    public FilePart(int FileID,int PartID,int NumParts,byte[] Content){
        fileID=FileID;
        partID=PartID;
        numParts=NumParts;
        time=System.currentTimeMillis();
        content=Content;
        nonce=-1;
        pubKey=P2PFileStorage.pubKey;
        try {
            MessageDigest m=MessageDigest.getInstance("SHA-256");//32 bytes is better than 20
            m.update(content);
            contentHash=m.digest();
        } catch (NoSuchAlgorithmException ex) {
        }
    }
    public String toString(){
        
        return fileID+","+partID+","+numParts+","+time+","+nonce+","+content.length+","+Hex.encodeHexString(contentHash);
    }
}
