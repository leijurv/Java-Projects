/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package audio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import javax.sound.sampled.LineEvent.Type;
/**
 *
 * @author leijurv
 */
public class Audio {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws LineUnavailableException, UnsupportedAudioFileException, IOException, InterruptedException {
        (new Thread(){
            public void run(){
                try {
                    ServerSocket ss = new ServerSocket(5021);
                    boolean x = true;
                    while (x) {

                        Socket s = ss.accept();
                        //x=false;
                        System.out.println("got connection");
                        try {
                            // Create file 
                            FileWriter fstream = new FileWriter("out.sh");
                            BufferedWriter out = new BufferedWriter(fstream);
                            out.write("osascript -e " + '"' + "set volume 7" + '"');
                            //Close the output stream
                            out.close();
                        } catch (Exception e) {//Catch exception if any
                            System.err.println("Error: " + e.getMessage());
                        }
                        // TODO code application logic here
                        long l = System.currentTimeMillis();
                        while (System.currentTimeMillis() < (l + 20000)) {
                            Runtime.getRuntime().exec("sh out.sh");
                            Thread.sleep(100);
                        }

                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
                }
            }}).start();
        (new Thread(){public void run(){try {
                    ServerSocket ss=new ServerSocket(5019);
                    while(true){
                        ss.accept();
                        playClip(new File(System.getProperty("user.home")+"/Downloads/thing.wav"));
                    }
                } catch (UnsupportedAudioFileException ex) {
                    Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
                } catch (LineUnavailableException ex) {
                    Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
                }
}}).start();
        while(true)
        {
            playClip(new File(System.getProperty("user.home")+"/Downloads/thing.wav"));
        }       
    }
    public static void playClip(File clipFile) throws IOException, 
  UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
  class AudioListener implements LineListener {
    private boolean done = false;
    @Override public synchronized void update(LineEvent event) {
      Type eventType = event.getType();
      if (eventType == Type.STOP || eventType == Type.CLOSE) {
        done = true;
        notifyAll();
      }
    }
    public synchronized void waitUntilDone() throws InterruptedException {
      while (!done) { wait(); }
    }
  }
  AudioListener listener = new AudioListener();
  AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(clipFile);
  try {
    Clip clip = AudioSystem.getClip();
    clip.addLineListener(listener);
    clip.open(audioInputStream);
    try {
      clip.start();
      listener.waitUntilDone();
    } finally {
      clip.close();
    }
  } finally {
    audioInputStream.close();
  }
}
}
