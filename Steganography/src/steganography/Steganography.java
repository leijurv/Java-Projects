/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steganography;
import java.awt.image.*;
import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.*;
import javax.swing.filechooser.*;
/**
 *
 * @author leijurv
 */
public class Steganography {
    static BufferedImage originalImage = null;
    static BufferedImage withData = null;
    static Image scaledOriginal = null;
    static Long seed = null;
    static String password = null;
    static File importLocation = null;
    static File exportLocation = null;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        final JFileChooser fc = new JFileChooser();
        JFrame frame = new JFrame("Steganography");
        JComponent M = new JComponent() {
            public void paintComponent(Graphics g) {
                g.drawString((seed == null) ? "Password not set" : "Password set to " + password, 10, 10);
                if (withData != null && seed != null) {
                    String data = readString();
                    g.drawString(data == null ? "No data" : "Data: " + data, 300, 300);
                }
            }
        };
        M.setLayout(new FlowLayout());
        JButton encodeData = new JButton("encud data");
        JButton open = new JButton("open file");
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fc.addChoosableFileFilter(new ImageFilter());
                fc.setAcceptAllFileFilterUsed(false);
                int returnVal = fc.showDialog(open, "Cchuse a dank file pls");
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        originalImage = ImageIO.read(file);
                        withData = ImageIO.read(file);//TODO just clone the previous one
                        importLocation = file;
                        exportLocation = new File(file.getParent() + File.separatorChar + file.getName().split("\\.")[0] + "Output.bmp");
                        scaledOriginal = originalImage.getScaledInstance(200, -1, Image.SCALE_SMOOTH);
                        JLabel orig = new JLabel("Original", new ImageIcon(scaledOriginal), JLabel.LEFT);
                        orig.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
                        M.add(orig);
                    } catch (IOException ex) {
                        Logger.getLogger(Steganography.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }//Silently ignore if user cancelled choose file dialog
                frame.repaint();
            }
        });
        M.add(open);
        encodeData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (seed == null) {
                    JOptionPane.showMessageDialog(null, "Lol u gotta set a password first", "Lol u gotta set a password first", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (withData == null) {
                    JOptionPane.showMessageDialog(null, "where is image????? need import imag first!!!", "i cri evri tim", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String data = JOptionPane.showInputDialog("Whatcha wanna encode?");
                writeString(data);
                frame.repaint();
            }
        });
        JButton setPassword = new JButton("set pathwurd");
        setPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                password = JOptionPane.showInputDialog("What is teh passwurd?");
                seed = seedFromString(password);
                frame.repaint();
            }
        });
        JButton export = new JButton("export");
        export.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (withData == null) {
                    JOptionPane.showMessageDialog(null, "where is image????? need import imag first!!!", "i cri evri tim", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int result = JOptionPane.showConfirmDialog(null, "Gonna write to " + exportLocation + ", is that cool?", "choose one", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        String formatName = "bmp";
                        if (!ImageIO.write(withData, formatName, exportLocation)) {
                            throw new IOException("IMAGE FORMAT '" + formatName + "' IS NOT SUPPORTED WAHHH");
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, ex.toString(), "Lol something broke", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        M.add(setPassword);
        M.add(encodeData);
        M.add(export);
        frame.setContentPane(M);
        frame.setSize(10000, 10000);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        /*
         BufferedImage a = ImageIO.read(new File("/Users/leijurv/Downloads/dankOutput.bmp"));
         long seed = 5021;
         SteganographicOutputStream out = new SteganographicOutputStream(seed, a);
         DataOutputStream o = new DataOutputStream(out);
         SteganographicInputStream in = new SteganographicInputStream(seed, a);
         DataInputStream i = new DataInputStream(in);
         for (int x = 0; x < 10; x++) {
         //o.writeUTF("Hello, I am currently 15 years old and I want to become a walrus. I know there’s a million people out there just like me, but I promise you I’m different. On December 14th, I’m moving to Antartica; home of the greatest walruses. I’ve already cut off my arms, and now slide on my stomach everywhere I go as training. I may not be a walrus yet, but I promise you if you give me a chance and the support I need, I will become the greatest walrus ever. If you have any questions or maybe advice, just inbox me. Thank you all so much ~~");//o.writeUTF("adoeuaoesntuhaetosaeou");
         }
         for (int x = 0; x < 10; x++) {
         System.out.println(i.readUTF());
         }
         //ImageIO.write(a, "BMP", new File("/Users/leijurv/Downloads/dankOutput.bmp"));
         /*System.out.println(read(r, a));
         System.out.println(read(r, a));
         System.out.println(read(r, a));*/
    }
    public static void writeString(String s) {
        SteganographicOutputStream out = new SteganographicOutputStream(seed, withData);
        DataOutputStream o = new DataOutputStream(out);
        try {
            o.writeUTF(s);
        } catch (IOException e) {
        }
    }
    public static String readString() {
        SteganographicInputStream in = new SteganographicInputStream(seed, withData);
        DataInputStream i = new DataInputStream(in);
        try {
            return i.readUTF();
        } catch (Exception e) {
            return null;
        }
    }
    public static class SteganographicOutputStream extends OutputStream {
        final Random r;
        final BufferedImage a;
        final boolean[][] modified;
        public SteganographicOutputStream(long seed, BufferedImage a) {
            this.r = new Random(seed);
            this.a = a;
            this.modified = new boolean[a.getWidth()][a.getHeight()];
        }
        @Override
        public void write(int b) throws IOException {
            if (b > 128) {
                b -= 256;//Converting from [0,255] to [-127,128] in the worst possible way
            }
            byte x = (byte) b;
            writeData(r, a, x, modified);
        }
    }
    public static class SteganographicInputStream extends InputStream {
        final Random r;
        final BufferedImage a;
        final boolean[][] modified;
        public SteganographicInputStream(long seed, BufferedImage a) {
            this.r = new Random(seed);
            this.a = a;
            this.modified = new boolean[a.getWidth()][a.getHeight()];
        }
        @Override
        public int read() throws IOException {
            byte x = readData(r, a, modified);
            int X = (int) x;
            if (X < 0) {
                X += 256;//Converting from [-128,127] to [0,255] in the worst possible way
            }
            return X;
        }
    }
    public static int[] getRandomLocation(Random r, BufferedImage a, boolean[][] modified) {
        int x = r.nextInt(a.getWidth());
        int y = r.nextInt(a.getHeight());
        if (modified[x][y]) {
            //System.out.println("Already hit " + x + "," + y + " skipping");
            return getRandomLocation(r, a, modified);//We don't want to overwrite previous data, so choose again if we've already hit this pixel
        }
        int bit = r.nextInt(3) * 8 + r.nextInt(3);//Last 4 bits of any of the 3 RGB bytes
        modified[x][y] = true;
        return new int[] {x, y, bit};
    }
    public static void writeData(Random r, BufferedImage a, byte b, boolean[][] modified) {
        for (int i = 0; i < 8; i++) {
            boolean bit = ((b >> i) & 1) == 1;
            //System.out.println("Bit " + i + " is " + bit);
            int[] location = getRandomLocation(r, a, modified);
            int x = location[0];
            int y = location[1];
            int position = location[2];
            int color = a.getRGB(x, y);
            int newColor = setBit(color, position, bit);
            //System.out.println("Changing color at " + x + "," + y + " from " + color + " to " + newColor);
            a.setRGB(x, y, newColor);
        }
    }
    public static byte readData(Random r, BufferedImage a, boolean[][] modified) {
        byte c = 0;
        for (int i = 0; i < 8; i++) {
            int[] location = getRandomLocation(r, a, modified);
            int x = location[0];
            int y = location[1];
            int position = location[2];
            int color = a.getRGB(x, y);
            byte result = (byte) ((color & (1 << position)) >> position);
            //System.out.println("Bit " + i + " at " + x + "," + y + " is " + result + " color " + color);
            c |= (result << i);
        }
        //System.out.println("Read byte " + c);
        return c;
    }
    public static int getBit(int ID, int position) {
        return ((ID >> position) & 1);
    }
    public static int setBit(int ID, int position, boolean set) {
        return (ID & (~(1 << position))) | ((set ? 1 : 0) << position);
    }
    public static long seedFromString(String s) {
        long seed = 0;
        for (int i = 0; i < s.length(); i++) {
            seed += s.charAt(i) << (i * 7);
//Pretty much fills up seed with the contents of the string, with a little overlap if the string is more that 8 bytes
        }
        return seed;
    }
    public static class ImageFilter extends FileFilter {
        //Accept all directories and all gif, jpg, tiff, or png files.
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return false;
            }
            String extension = getExtension(f);
            if (extension != null) {
                if (extension.equals("tiff")
                        || extension.equals("tif")
                        || extension.equals("gif")
                        || extension.equals("jpeg")
                        || extension.equals("jpg")
                        || extension.equals("png") || extension.equals("bmp")) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }
        //The description of this filter
        @Override
        public String getDescription() {
            return "Just Images";
        }
        public static String getExtension(File f) {
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');
            if (i > 0 && i < s.length() - 1) {
                ext = s.substring(i + 1).toLowerCase();
            }
            return ext;
        }
    }
}
