/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package noise;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
/**
 *
 * @author leijurv
 */
public class Noise {
    public static final int SAMPLE_RATE=16*1024; // ~16KHz
    public static final int SECONDS=1;
    public static final int numRows=200;
    public static int[][] ca=new int[numRows][numRows*2+1];
    public static void main(String[] args) throws Exception{
        
        
        Socket s=new Socket("localhost",15565);
            DataOutputStream oo=new DataOutputStream(s.getOutputStream());
            oo.writeUTF("/");
            oo.writeUTF("-1");
            oo.writeUTF("3000");
            InputStream ii=s.getInputStream();
            final AudioFormat af=new AudioFormat(SAMPLE_RATE,8,1,true,true);
        SourceDataLine linee=AudioSystem.getSourceDataLine(af);
        linee.open(af,SAMPLE_RATE);
        linee.start();
        //Thread.sleep(2000);
        System.out.println("meow");
        byte[] x=new byte[1000];
        int j;
        System.out.println(ii.available());
        while((j=ii.read(x))>=0){
            //System.out.println(j);
            linee.write(x,0,j);
        }
        linee.drain();
        linee.close();
        System.out.println("done");
    }
    public static void main1(String[] args) throws Exception{
        int center=numRows;
        ca[0][center]=1;
        for (int row=1; row<numRows; row++){
            for (int pos=-row; pos<=row; pos++){
                int p=pos+center;
                ca[row][p]=next(ca[row-1][p-1]==1,ca[row-1][p]==1,ca[row-1][p+1]==1,150) ? 1 : 2;
            }
            for (int i=0; i<ca[row].length; i++){
                System.out.print(ca[row][i]);
            }
            System.out.println();
            /*
             int[] x=reorderRow(row);
             for (int i=0; i<x.length; i++){
             System.out.print(x[i]);
             }*/
            //System.out.println(fromPitch(getPitchFromRow(row)));
        }
        byte[][] sin=new byte[numRows][SECONDS*SAMPLE_RATE];
        int pitch=0;
        for (int row=0; row<numRows; row++){
            pitch=(pitch+getPitchFromRow(row))%128;
            generateTone(fromPitch(pitch),sin[row]);
        }
        
        /* System.out.println(Note.C4.ordinal());
         for (int p=0; p<128; p++){
         System.out.println(p+","+fromPitch(p));
         }*/
        final AudioFormat af=new AudioFormat(SAMPLE_RATE,8,1,true,true);
        SourceDataLine linee=AudioSystem.getSourceDataLine(af);
        linee.open(af,SAMPLE_RATE);
        linee.start();
        for (int row=0; row<numRows; row++){
         play(linee,sin[row],100);
         }
        
        TargetDataLine line;
        DataLine.Info info=new DataLine.Info(TargetDataLine.class,
                af); // format is an AudioFormat object
        if (!AudioSystem.isLineSupported(info)){
    // Handle the error ... 
        }
        line=(TargetDataLine) AudioSystem.getLine(info);
        line.open(af);
        System.out.println(line.getBufferSize());
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        int numBytesRead;
        byte[] data=new byte[SECONDS*SAMPLE_RATE];
// Begin audio capture.
        line.start();
        long time=System.currentTimeMillis();
        System.out.println("KITTY");
        while (true){
            numBytesRead=line.read(data,0,data.length);
            linee.write(data,0,numBytesRead);
            out.write(data,0,numBytesRead);
            linee.drain();
            System.out.println("Starting to record");
            Thread.sleep(100);
            System.out.println("Done recording");
        }
        /*
         
        
         byte[] sinn=new byte[SECONDS*SAMPLE_RATE];
         generateTone(20000,sinn);
         System.out.println("play");
         play(line,sinn,10000);*/
    }
    private static void play(SourceDataLine line,byte[] sin,int ms){
        ms=Math.min(ms,SECONDS*1000);
        int length=SAMPLE_RATE*ms/1000;
        int count=line.write(sin,0,length);
    }
    static enum Note {
        REST, A4, A4$, B4, C4, C4$, D4, D4$, E4, F4, F4$, G4, G4$, A5;
        final byte[] sin=new byte[SECONDS*SAMPLE_RATE];
        Note(){
            int n=this.ordinal();
            if (n>0){
                double exp=((double) n-1)/12d;
                double f=440d*Math.pow(2d,exp);
                System.out.println(n+","+f);
                generateTone(f,sin);
            }
        }
    }
    public static double fromPitch(int pitch){
        double adj=(double) pitch-69;
        double freq=440d*Math.pow(2,adj/12);
        return freq;
    }
    public static void generateTone(double freq,byte[] sin){
        for (int i=0; i<sin.length; i++){
            double period=(double) SAMPLE_RATE/freq;
            double angle=2.0*Math.PI*i/period;
            sin[i]=(byte) (Math.sin(angle)*127f);
        }
    }
    /*
     int[] reorderRow(
     int rowIndex) {

     int[][] cAHistory = cA.getGenerations();
     int[] row = cAHistory[rowIndex];
     int len = row.length;
     int[] reordered = new int[len];
     int mid = len / 2;
     reordered[len - 1] = row[mid];
     for (int i = 0; i < (len - 1) / 2; i++) {
     // Note that this favors one side of the CA over the
     // other side, but there seems to be no way to get around this.
     if (bias.equals(HorizontalBias.LEFT)) {
     reordered[len - (2 * i + 1) - 1] = row[mid - (i + 1)];
     reordered[len - (2 * i + 2) - 1] = row[mid + i + 1];
     } else {
     reordered[len - (2 * i + 1) - 1] = row[mid + i + 1];
     reordered[len - (2 * i + 2) - 1] = row[mid - (i + 1)];
     }
     }

     return reordered;
     }*/
    public static boolean next(boolean left,boolean middle,boolean right,int rule){
        int a=(right ? 1 : 0)+(middle ? 2 : 0)+(left ? 4 : 0);
        int b=rule>>a;
        //System.out.println(left+","+middle+","+right+","+a+","+b+","+b%2);
        return (b%2)!=0;
    }
    /*
     public static int calc(int[] row){
        
     }*/
    static int[] reorderRow(
            int rowIndex){
        int[][] cAHistory=ca;
        int[] row=cAHistory[rowIndex];
        int len=row.length;
        int[] reordered=new int[len];
        int mid=len/2;
        reordered[len-1]=row[mid];
        for (int i=0; i<(len-1)/2; i++){
            // Note that this favors one side of the CA over the
            // other side, but there seems to be no way to get around this.
            //if (bias.equals(HorizontalBias.LEFT)) {
            reordered[len-(2*i+1)-1]=row[mid-(i+1)];
            reordered[len-(2*i+2)-1]=row[mid+i+1];
            // } else {
            //  reordered[len - (2 * i + 1) - 1] = row[mid + i + 1];
            // reordered[len - (2 * i + 2) - 1] = row[mid - (i + 1)];
            // }
        }
        return reordered;
    }
    static int getPitchFromRow(
            int rowIndex){
        int[] reorderedRow=reorderRow(rowIndex);
        int pitch=0;
        for (int i=0; i<reorderedRow.length; i++){
            pitch=(2*pitch+reorderedRow[i])%128;
        }
        return pitch;
    }
    
    
    
}
