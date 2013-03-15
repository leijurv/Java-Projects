/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package falling.tone;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author leijurv
 */
public class FallingTone {
    static final float SAMPLE_RATE=8000f;
public static void sound(float hz, float msecs, double vol)
           throws LineUnavailableException {

       if (hz <= 0) {
           throw new IllegalArgumentException("Frequency <= 0 hz");
       }

       if (msecs <= 0) {
           throw new IllegalArgumentException("Duration <= 0 msecs");
       }

       if (vol > 1.0 || vol < 0.0) {
           throw new IllegalArgumentException("Volume out of range 0.0 - 1.0");
       }

       byte[] buf = new byte[(int) (SAMPLE_RATE * msecs / 1000)];

       for (int i = 0; i < buf.length; i++) {
           double angle = i / (SAMPLE_RATE / (hz-(i/SAMPLE_RATE*1000/msecs))) * 2.0 * Math.PI;
           buf[i] = (byte) (Math.sin(angle) * 127.0 * vol);
       }

       // shape the front and back 10ms of the wave form
       for (int i = 0; i < SAMPLE_RATE / 100.0 && i < buf.length / 2; i++) {
           buf[i] = (byte) (buf[i] * i / (SAMPLE_RATE / 100.0));
           buf[buf.length - 1 - i] =
                   (byte) (buf[buf.length - 1 - i] * i / (SAMPLE_RATE / 100.0));
       }

       AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
       SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
       sdl.open(af);
       sdl.start();
       sdl.write(buf, 0, buf.length);
       sdl.drain();
       sdl.close();
   }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws LineUnavailableException {
        sound(20000,1000,1);
        // TODO code application logic here
    }
}
