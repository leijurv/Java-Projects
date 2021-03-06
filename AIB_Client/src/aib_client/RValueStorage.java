/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leif
 */
public class RValueStorage {
    ArrayList<BigInteger> RValues=new ArrayList<BigInteger>();
    ArrayList<Integer> denomIDs=new ArrayList<Integer>();
    String Path;
    public RValueStorage(String path){
        File f=new File(path);
        if (!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(RValueStorage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Path=path;
        try {
            read(Path);
        } catch (Exception ex) {
            Logger.getLogger(RValueStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public BigInteger getTotalValue(){
        BigInteger totalValue=new BigInteger("0");
        for (int i=0; i<denomIDs.size(); i++){
            totalValue=totalValue.add(BigInteger.TEN.pow(denomIDs.get(i)));
        }
        return totalValue;
    }
    private void read(String path) throws Exception{
        FileInputStream X=new FileInputStream(path);
        int total=X.available()/129;
        for (int i=0; i<total; i++){
            byte[] denomID=new byte[1];
            X.read(denomID);
            byte[] RValue=new byte[128];
            X.read(RValue);
            denomIDs.add(new Integer(denomID[0]));
            RValues.add(new BigInteger(RValue));
        }
        System.out.println(path);
    }
    public void save(){
        try {
            write(Path);
        } catch (Exception ex) {
            Logger.getLogger(RValueStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void write(String path) throws Exception{
        FileOutputStream X=new FileOutputStream(path);
        //File storage is in 129 byte blocks. In each block, the first byte is the denomination id. The rest is the R value
        for (int i=0; i<denomIDs.size(); i++){
            byte[] denominationID=new byte[1];
            denominationID[0]=denomIDs.get(i).byteValue();
            X.write(denominationID);
            byte[] RValue=new byte[128];
            byte[] temp=RValues.get(i).toByteArray();//Might be less than 128 bytes. Pad with 0s at the beginning.
            
            int offset=128-temp.length;
            System.arraycopy(temp, 0, RValue, offset, temp.length);
            X.write(RValue);
        }
        X.close();
    }
    public void add(ArrayList<BigInteger> rValues, ArrayList<Integer> denomIDS){
        if (rValues.size()!=denomIDS.size()){
            throw new IllegalArgumentException("MUST BE SAME SIZE");
        }
        for (int i=0; i<rValues.size(); i++){
            RValues.add(rValues.get(i));
            denomIDs.add(denomIDS.get(i));
        }
    }
}
