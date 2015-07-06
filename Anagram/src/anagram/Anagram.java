package anagram;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
public class Anagram {
    public static void main(String[] args) throws Exception {
        String beginning = "samdonahue.me";
        String ending = "homemadean.us";
        char[] begin = beginning.toLowerCase().toCharArray();
        char[] end = ending.toLowerCase().toCharArray();
        ArrayList<Character> endd = new ArrayList<>(end.length);
        for (char c : end) {
            endd.add(c);
        }
        int[] to = new int[begin.length];//To what index is each of the input letters going to
        for (int i = 0; i < to.length; i++) {
            int pos = endd.indexOf(begin[i]);//Where in the ending phrase is this letter?
            endd.set(pos, '*');//Don't have two letters going to the same place
            to[i] = pos;
        }
        try(ImageOutputStream output = new FileImageOutputStream(new File("/Users/leijurv/Documents/dank.gif"))) {
            GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_RGB, 1, true);
            for (int percent = 0; percent < 10; percent++) {//10 frames of the starting phrase
                writer.writeToSequence(draw(begin, to, 0));
            }
            for (int percent = 0; percent < 20; percent++) {//20 frames of transition
                writer.writeToSequence(draw(begin, to, ((float) percent) / 20));
            }
            for (int percent = 0; percent < 10; percent++) {//10 frames of ending phrase
                writer.writeToSequence(draw(begin, to, 1));
            }
            writer.close();
        }
    }
    public static BufferedImage draw(char[] begin, int[] to, float percent) {
        float charWidth = 9;
        BufferedImage image = new BufferedImage((int) (to.length * charWidth + 37), 30, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setFont(new Font("Courier", Font.PLAIN, 12));
        for (int i = 0; i < to.length; i++) {
            float target = to[i];
            float start = i;
            float current = (target - start) * percent + start;
            g.drawString(begin[i] + "", (int) (current * charWidth + 10), 20);
        }
        return image;
    }
}
