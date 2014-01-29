/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package aib_server;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
/**
 *
 * @author leijurv
 */
public abstract class LogEvent {
    public abstract String toString();
    public abstract Date time();
    public abstract void write(FileOutputStream f) throws Exception;
    public static LogEvent create(FileInputStream f) throws Exception{
        byte[] type=new byte[1];
        f.read(type);
        if (type[0]==0){
            //Transaction
            return new LogTransaction(f);
        }
        if (type[0]==1){
            return new LogDeposit(f);
        }
        return null;
    }
}
