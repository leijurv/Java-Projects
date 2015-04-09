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
import java.io.FileInputStream;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;
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
    static final JLabel dataLabel = new JLabel("Data:");
    static final JLabel data = new JLabel("");
    static JPanel dataPanel = new JPanel(new FlowLayout());
    static JLabel image = new JLabel("", new ImageIcon(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)), JLabel.LEFT);
    static final JLabel currentBorder = new JLabel("");
    static final int numBytes = 3;
    static final int numBitsModifiable = 3;
    static final boolean writePayload = false;
    static int borderRadius = 0;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        dataPanel.add(dataLabel);
        dataPanel.add(data);
        data.setBorder(BorderFactory.createLineBorder(Color.black));
        currentBorder.setBorder(BorderFactory.createLineBorder(Color.black));
        JFrame frame = new JFrame("Stegnawgraphie");
        JComponent M = new JComponent() {
            private static final long serialVersionUID = 1L;//because netbeans gets pissed if i dont have this
            @Override
            public void paintComponent(Graphics g) {
                g.drawString((seed == null) ? "Password not set" : "Password set to " + password, 10, 10);
                if (withData != null && seed != null) {
                    String d = readString();
                    if (d != null) {
                        d = String.format("<html><div WIDTH=%d>%s</div><html>", (frame.getWidth() * 2) / 3, d);
                        data.setText(d);//YAY HTML IN JLABELS!!!!!
                        //I'm going to start writing java app guis with CSS!!!!!
                        dataLabel.setText("Data:");
                    } else {
                        data.setText(new String(doRead(100)));//If it's unreadable, show the first 100 bytes in case only part of it is corrupted
                        dataLabel.setText("No data/unreadable data (wrong password/wrong border radius)");
                    }
                } else {
                    dataLabel.setText("No data yet");
                }
                currentBorder.setText("Current border: " + borderRadius);
            }
        };
        M.setLayout(new FlowLayout());
        JButton encodeData = new JButton("encud data");
        JButton open = new JButton("open file");
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                fc.addChoosableFileFilter(new ImageFilter());
                fc.setAcceptAllFileFilterUsed(false);
                int returnVal = fc.showDialog(open, "Cchuse a dank file pls");
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        originalImage = ImageIO.read(file);
                        withData = ImageIO.read(file);//TODO just clone the previous one
                        importLocation = file;
                        exportLocation = new File(file.getParent() + File.separatorChar + file.getName().split("\\.")/* because split uses regex and . is a thing in regex that means stuff, escape it*/[0] + "Output.bmp");
                        scaledOriginal = originalImage.getScaledInstance(200, -1, Image.SCALE_SMOOTH);//so apparently if you set one of the dimensions to -1 it preserves aspect ratio. if only i learned that a long time ago
                        image.setText("Original");
                        image.setIcon(new ImageIcon(scaledOriginal));
                        image.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));//ooh fancy borders
                    } catch (IOException ex) {
                        Logger.getLogger(Steganography.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }//Silently ignore if user cancelled choose file dialog
                frame.setSize(frame.getWidth() - 1, frame.getHeight() - 1);//the BEST way to get a JFrame to repaint!!!
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
                String data1 = JOptionPane.showInputDialog("Whatcha wanna encode?");
                double bytesBeingUsed = 4 + data1.length(); //DataOutputStream.writeUTF writes an int before the string to indicate the length
                double capacity = numBytes * numBitsModifiable;
                capacity *= image.getWidth();
                capacity *= image.getHeight();
                capacity /= 8;//converting capacity in bits to capacity in bytes
                double usedUp = bytesBeingUsed / capacity;
                usedUp = (int) (usedUp * 100 * 100);
                usedUp /= 100;//round to 2 sig figs
                String usagePercent = usedUp + "%";
                //Not sure about these next 4 lines... it gives 100% if youve used more than like 3% of the capacity, doesn't seem right
                double singleByteCollisionChance = bytesBeingUsed / capacity;
                double singleByteNonCollisionChance = 1 - singleByteCollisionChance;
                double nonCollisionChance = Math.pow(singleByteNonCollisionChance, bytesBeingUsed);
                double collisionChance = 1 - nonCollisionChance;
                collisionChance = (int) (collisionChance * 100 * 100);
                collisionChance /= 100;
                String collisionPercent = collisionChance + "%";
                if (JOptionPane.showConfirmDialog(null,
                        "That data is " + bytesBeingUsed + " bytes long, and will take up " + usagePercent + " of this image's " + capacity + " byte storage capacity. It has a " + collisionPercent + " chance of corrupting another message of the same length with a different password.", "choose pls", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    return;
                }
                writeString(data1);
                frame.repaint();
            }
        });
        JButton setBorder = new JButton("set bordr");
        setBorder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String data = null;
                if (seed != null && withData != null) {
                    data = readString();
                }
                borderRadius = Integer.parseInt(JOptionPane.showInputDialog("How many pixels around the edge should be ignored?"));
                if (data != null) {
                    writeString(data);
                }
                frame.repaint();
            }
        });
        JButton setPassword = new JButton("set pathwurd");
        setPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                password = JOptionPane.showInputDialog("What is teh passwurd?");
                if (password == null) {
                    return;
                }
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
        M.add(setBorder);
        M.add(currentBorder);
        M.add(encodeData);
        M.add(export);
        M.add(image);
        M.add(dataPanel);
        frame.setContentPane(M);
        frame.setSize(10000, 10000);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    public static void writeString(String s) {
        SteganographicOutputStream out = new SteganographicOutputStream(seed, withData);
        DataOutputStream o = new DataOutputStream(out);
        try {
            o.writeUTF(s);
        } catch (IOException e) {
        }
        if (writePayload) {
            try(FileInputStream extraPayloadStream = new FileInputStream(new File("** some file =) **"))) {
                byte[] payload = new byte[extraPayloadStream.available()];
                extraPayloadStream.read(payload);
                //TODO maybe write payload file name & extension as writeUTF here?
                o.writeInt(payload.length);
                o.write(payload);
                JOptionPane.showMessageDialog(null, "wrote payload successfully", "wrote payload successfully", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }
    public static String readString() {
        SteganographicInputStream in = new SteganographicInputStream(seed, withData);
        DataInputStream i = new DataInputStream(in);
        String result;
        try {
            result = i.readUTF();
        } catch (Exception e) {
            //result is already null
            return null;
        }
        //TODO read payload if present
        return result;
    }
    public static byte[] doRead(int numBytes) {
        SteganographicInputStream in = new SteganographicInputStream(seed, withData);
        byte[] result = new byte[numBytes];
        try {
            in.read(result);
        } catch (IOException ex) {
        }
        return result;
    }
    public static class SteganographicOutputStream extends OutputStream {
        final Random r;
        final BufferedImage a;
        final boolean[][][] modified;
        public SteganographicOutputStream(long seed, BufferedImage a) {
            this.r = new Random(seed);
            this.a = a;
            this.modified = new boolean[a.getWidth()][a.getHeight()][numBytes * numBitsModifiable];
        }
        @Override
        public void write(int b) throws IOException {
            if (b > 128) {
                b -= 256;//Converting from [0,255] to [-127,128] in the worst possible way
            }
            byte x = (byte) b;
            writeData(r, a, x, modified);
        }
        //TODO override some of the other write functions
    }
    public static class SteganographicInputStream extends InputStream {
        final Random r;
        final BufferedImage a;
        final boolean[][][] modified;
        public SteganographicInputStream(long seed, BufferedImage a) {
            this.r = new Random(seed);
            this.a = a;
            this.modified = new boolean[a.getWidth()][a.getHeight()][numBytes * numBitsModifiable];
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
        //TODO override some of the other read functions
    }
    public static int[] getRandomLocation(Random r, BufferedImage a, boolean[][][] modified) {
        int x = r.nextInt(a.getWidth() - 2 * borderRadius) + borderRadius;//so as not to modify some... things... that are right at the edge of the
        int y = r.nextInt(a.getHeight() - 2 * borderRadius) + borderRadius;//image which need to be pixel perfect
        int Byte = r.nextInt(numBytes);
        int Bit = r.nextInt(numBitsModifiable);
        if (modified[x][y][Byte * numBitsModifiable + Bit]) {
            //System.out.println("Already hit " + x + "," + y + " skipping");
            return getRandomLocation(r, a, modified);//We don't want to overwrite previous data, so choose again if we've already hit this pixel
        }
        int bit = Byte * 8 + Bit;
        modified[x][y][Byte * numBitsModifiable + Bit] = true;
        return new int[] {x, y, bit};
    }
    public static void writeData(Random r, BufferedImage a, byte b, boolean[][][] modified) {
        byte[] mask = new byte[1];
        r.nextBytes(mask);
        b ^= mask[0];//So now the bits it's writing are evenly distributed 1s and 0s, because it was xored with random-ish data
        //this is NSA grade encryption here. just xor your data with the output of java.util.random. the password is the seed to the RNG.
        for (int i = 0; i < 8; i++) {
            int bit = ((b >> i) & 1);
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
    public static byte readData(Random r, BufferedImage a, boolean[][][] modified) {
        byte[] mask = new byte[1];
        r.nextBytes(mask);//WHY doesn't Random have a readByte function????? I just want one byte without having to create a whole new array!
        byte c = mask[0];
        for (int i = 0; i < 8; i++) {
            int[] location = getRandomLocation(r, a, modified);
            int x = location[0];
            int y = location[1];
            int position = location[2];
            int color = a.getRGB(x, y);
            byte result = (byte) ((color & (1 << position)) >> position);
            //System.out.println("Bit " + i + " at " + x + "," + y + " is " + result + " color " + color);
            c ^= (result << i);
        }
        //System.out.println("Read byte " + c);
        return c;
    }
    public static int getBit(int ID, int position) {
        return ((ID >> position) & 1);
    }
    public static int setBit(int ID, int position, int set) {
        return (ID & (~(1 << position))) | (set << position);
    }
    public static long seedFromString(String s) {
        long seedGen = 0;
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            seedGen += chars[i] << (i * 7);
//Pretty much fills up seed with the contents of the string, with a little overlap if the string is more that 8 bytes
        }
        return seedGen;
    }
    public static class ImageFilter extends FileFilter {
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return false;
            }
            String extension = getExtension(f);
            if (extension != null) {
                String[] possibilities = ImageIO.getReaderFileSuffixes();//Only accept files that ImageIO can read
                for (String ext : possibilities) {
                    if (ext.equalsIgnoreCase(extension)) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
        //The description of this filter
        @Override
        public String getDescription() {
            return "Just images that ImageIO can handle";
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
