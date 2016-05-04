package sirds;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.*;

public class SIRDS extends JComponent {
    static final int size = 400;
    static BufferedImage left = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
    static BufferedImage right = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
    static int pixelOffset = 4;
    public void paintComponent(Graphics g) {
        g.drawImage(left, 5, 5, null);
        g.drawImage(right, size + 15, 5, null);
    }
    public static void main(String[] args) {
        BufferedImage base = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics c = base.getGraphics();
        c.setFont(new Font("Comic Sans", Font.BOLD, 90));
        String[] R = JOptionPane.showInputDialog("Message? (7 letters per line max, lines seperated by commas)").split(",");
        for (int i = 0; i < R.length; i++) {
            c.drawString(R[i], 20, (i + 1) * 80);
        }
        Random r = new Random();
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                left.setRGB(i, j, r.nextBoolean() ? -1 : left.getRGB(i, j));
                if (base.getRGB(i, j) != base.getRGB(0, 0)) {
                    right.setRGB(i, j, left.getRGB(i - pixelOffset, j));
                } else if (i < size - pixelOffset && base.getRGB(i + pixelOffset, j) != base.getRGB(0, 0)) {
                    right.setRGB(i, j, r.nextBoolean() ? -1 : right.getRGB(i, j));
                } else {
                    right.setRGB(i, j, left.getRGB(i, j));
                }
            }
        }
        JFrame frame = new JFrame("SIRDS");
        frame.setContentPane(new SIRDS());
        frame.setSize(size * 2 + 20, size + 30);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
