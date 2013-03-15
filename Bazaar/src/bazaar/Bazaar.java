package bazaar;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author leif
 */
public class Bazaar extends JComponent {

    static JButton barter;
    static JFrame frame;
    static Bazaar M;
    static JTextField in;
    static JTextField out;
    static int selected = -1;//(((125+10)/2)*(188/(12*2.54)*58.5/(12*2.54))/(9*16))/11
    //(188/(12*2.54)*58.5/(12*2.54))/9 is the amount in square yards, 1.315
    //(188/(12*2.54)*58.5/(12*2.54))/(9*16) is the weight in pounds of silk of 188x58.5c, 0.0822
    //(188/(12*2.54)*58.5/(12*2.54))/(9*16) * ((125+10)/2) is the amount of aureus for silk measuring 188x58.5cm 5.5491
    static final String[] ids = {"Glass Goblet", "Apples", "Lapis Lazuli","Carpet","Watermelon","Hunting Dog","Peach","China"};
    static final String[] paths = {"/Users/leijurv/Downloads/imgres.jpeg", "/Users/leijurv/Downloads/imgres-1.jpeg","/Users/leijurv/Downloads/lapis-1.jpeg","/Users/leijurv/Downloads/imgres-2.jpeg","/Users/leijurv/Downloads/imgres-3.jpeg","/Users/leijurv/Downloads/imgres-4.jpeg","/Users/leijurv/Downloads/imgres-5.jpeg","/Users/leijurv/Downloads/imgres-6.jpeg"};
    static final String[] currencyNames = {"Currency: Coins", "Currency: Jade", "Currency: Paper", "Currency: Spice", "Currency: Silk"};
    static final String[] currencyNames1 = {"Coins", "Jade", "Pieces of Paper", "Spices", "Silk"};
    static int[] amounts = new int[ids.length];
    static String sales = "";
    static final int[] cost = {10,2,6,20,15,25,7,13};
    static final boolean[] canBuyMultiple = {true, true, true,true,true,true,true,true};
    static final double[] amountWorthOneCoin = {1, 20, 7, 5, 10};
    static JLabel[] labels = new JLabel[ids.length];
    static JLabel selectedLabel;
    static final String backgroundPath = "/Users/leijurv/Documents/funny.png";
    static final boolean drawBackground = false;
    private static final long serialVersionUID = 1L;
    static boolean bartering = false;
    static JButton leave;
    static JButton agree;
    static JButton b;
    static int myOffer = -1;
    static int prevOffer = -1;
    static JComboBox currencybox;
    static int currency = 0;
    static int amount = -1;
    static JTextField amountT;
    static JLabel x;
    static JLabel sillyzack;
    
    //static final double[] amountWorthOneBanLiang=new double[] {1,0.5,12,12/5,0.5044};
    //static final String[] currencyNames={"Ban Liang","Liang","Zhu","Wu Zhu","Aurei"};//Liang=50 grams
    static int totalMoneyMade;
    @Override
    public void paintComponent(Graphics g) {
        
        if (System.currentTimeMillis() % 5 != 0) {
            M.repaint();
        }
        if (bartering || selected==-1 || !canBuyMultiple[selected]){
            sillyzack.setVisible(true);
        }else{
            sillyzack.setVisible(false);
        }
        if (selected == -1 || amounts[selected] < 1) {
            frame.remove(barter);
            frame.remove(leave);
            frame.remove(agree);
            frame.remove(in);
            frame.remove(b);
            amountT.setVisible(false);
            x.setVisible(false);
        } else {
            if (bartering) {
                frame.add(b);
                frame.add(leave);
                frame.add(agree);
                frame.remove(barter);
                amountT.setVisible(false);
                x.setVisible(false);
            }
            if (!bartering) {
                frame.remove(leave);
                frame.add(barter);
                frame.remove(agree);
                frame.remove(in);
                frame.remove(b);
                if (canBuyMultiple[selected]) {
                    amountT.setVisible(true);
                    x.setVisible(true);
                } else {
                    amountT.setVisible(false);
                    x.setVisible(false);
                }
            }
        }
        selectedLabel.setText(/*"Currently selected: " + (selected == -1 ? "None " : ids[selected]) + */(selected == -1 ? "" : amounts[selected] + " in stock."));
        if (drawBackground) {
            try {
                g.drawImage(loadandscale(backgroundPath, frame.getWidth(), frame.getHeight(), Image.SCALE_FAST), 0, 0, null);
            } catch (IOException ex) {
            }
        } else {
            g.setColor(Color.ORANGE);
            g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
            g.setColor(Color.BLACK);
        }
        for (int i = 0; i < labels.length; i++) {
            if (labels[i]!=null){
            int x = labels[i].getX() + labels[i].getWidth() / 2;
            int y = labels[i].getY() + labels[i].getHeight() + 10;
            g.drawString(ids[i], x, y);
            }
        }
        if (selected != -1) {
            g.setColor(Color.BLUE);
            g.drawLine(labels[selected].getX() - 4, labels[selected].getY() - 4, labels[selected].getX() - 4, labels[selected].getY() + labels[selected].getHeight() + 10);
            g.drawLine(labels[selected].getX() - 4, labels[selected].getY() - 4, labels[selected].getX() + labels[selected].getWidth() + 4, labels[selected].getY() - 4);
            g.drawLine(labels[selected].getX() + labels[selected].getWidth() + 4, labels[selected].getY() - 4, labels[selected].getX() + labels[selected].getWidth() + 4, labels[selected].getY() + labels[selected].getHeight() + 10);
            g.drawLine(labels[selected].getX() - 4, labels[selected].getY() + labels[selected].getHeight() + 10, labels[selected].getX() + labels[selected].getWidth() + 4, labels[selected].getY() + labels[selected].getHeight() + 10);
            //g.drawRect(labels[selected].getX()-10,labels[selected].getY()-10,labels[selected].getX() + labels[selected].getWidth() / 2,labels[selected].getY() + labels[selected].getHeight() + 10);
        }
        //g.drawString("meow",10,10);
    }

    public static int convertToCurrent(double value) {
        return (int)(value / amountWorthOneCoin[currency] * ((double) amount));
    }

    public static int convertFromCurrent(double value) {
        return (int)(value * amountWorthOneCoin[currency] / ((double) amount));
    }

    public static String c() {
        return currencyNames1[currency];
    }

    public static void barterlaikaboss() {
        try {
            int offer = convertFromCurrent(Integer.parseInt(in.getText()));

            if (tooLow) {
                if (offer > myOffer / 2 && offer > prevOffer * 1.05D) {
                    tooLow = false;
                    //prevOffer = offer;
                    myOffer = (int)(((double)myOffer + (double)offer) / 1.5D);
                    //out.setText("My offer is "+convertToCurrent(myOffer));
                } else {
                    return;
                }

            }
            if (offer < prevOffer||(offer==prevOffer&&offer<myOffer*.93D)) {
                
                out.setText("Your offers are going down. Please  " + convertToCurrent(myOffer) + " " + c());
                return;
            }
            if (offer < myOffer / 2) {
                //Toolow
                out.setText("That offer is unreasonable. My offer stands at " + convertToCurrent(myOffer) + " " + c());
                tooLow = true;
                prevOffer = offer;
                return;
            }
            if ((double)myOffer * 0.93D < offer) {
                out.setText("That offer is quite reasonable. I agree to " + convertToCurrent(offer) + " " + c());
                frame.add(currencybox);
                amounts[selected]-=amount;
                totalMoneyMade+=offer*amount;
                sales +="\nSold "+amount+" "+ids[selected]+" for "+offer*amount+" coins.";
                System.out.println(totalMoneyMade);
                System.out.println(sales);
                bartering = false;
                selected = -1;
                myOffer = -1;
                prevOffer = -1;
                frame.repaint();
                return;
            }
            int old = myOffer;
            myOffer = (int)(((double)myOffer * 15 + (double)offer * 5) / 20D);
            if (myOffer <= cost[selected] + 1) {
                
                out.setText("That offer is too low. My offer stands at " + convertToCurrent(myOffer) + " " + c());
            } else {
                prevOffer = offer;
                if (myOffer==prevOffer){
                    out.setText("That offer is quite reasonable. I agree to " + convertToCurrent(offer) + " " + c());
                frame.add(currencybox);
                sales +="\nSold "+amount+" "+ids[selected]+" for "+offer*amount+" coins.";
                amounts[selected]-=amount;
                totalMoneyMade+=offer*amount;
                System.out.println(totalMoneyMade);
                System.out.println(sales);
                bartering = false;
                selected = -1;
                myOffer = -1;
                prevOffer = -1;
                frame.repaint();
                return;
                }
                out.setText("My offer is " + convertToCurrent(myOffer) + " " + c());
            }

            
            /*
            int x = offer + Integer.parseInt(in.getText());
            if (x <=  cost[selected] * 2+1) {
            out.setText("No. " + offer + ".");
            } else {
            offer = (int) Math.ceil(((double) x) / 2D);
            out.setText("My offer is " + offer);
            }*/
        } catch (Exception e) {
        }
    }

    /**
     * @param args the command line arguments
     */
    public static JLabel load(String path, int width, int height, final int selectedID) throws IOException {
        Image i = loadandscale(path, width, height, Image.SCALE_SMOOTH);
        Icon icon = new ImageIcon(i);
        JLabel label = new JLabel(icon);

        label.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent me) {
                if (!bartering) {
                    out.setText(ids[selectedID] + " selected");
                    selected = selectedID;
                    M.repaint();
                }
            }
        });
        System.out.println("Loaded " + ids[selectedID] + " with id " + selectedID);
        labels[selectedID] = label;
        return label;
    }

    public static Image loadandscale(String path, int width, int height, int scalingmethod) throws IOException {
        BufferedImage o = ImageIO.read(new File(path));
        Image i = o.getScaledInstance(width, height, scalingmethod);
        return i;
    }
    static boolean tooLow = false;

    public static void min(String[] args) {
        System.out.print("\u202E");
        System.out.println("DERP");
    }
public static void man(String[] args) throws UnknownHostException{
    InetAddress i=InetAddress.getByAddress(new byte[] {-64,-88,1,111});
    System.out.println(i.getCanonicalHostName());
}
    public static void main(String[] args) throws IOException {
        M = new Bazaar();
        for (int i = 0; i < ids.length; i++) {
            amounts[i] = 10;
        }
        amounts[1]=30;
        amounts[2]=18;
        amounts[0]=20;
        amounts[3]=5;
        amounts[5]=2;
        amounts[6]=25;
        frame = new JFrame("Trade");
        M.setFocusable(true);
        (frame).setContentPane(M);
        frame.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 11));
        
        selectedLabel = new JLabel();
        frame.add(selectedLabel);

        out = new JTextField();
        out.setColumns(50);
        out.setEditable(false);
        frame.add(out);

        in = new JTextField();
        in.setColumns(10);

        amountT = new JTextField();
        amountT.setColumns(10);
        x = new JLabel("Amount:");
        frame.add(x);
        frame.add(amountT);
        sillyzack=new JLabel("                                                                                                      ");
        JLabel kitten=new JLabel("                  ");
        agree = new JButton("Agree");
        agree.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (ae.getActionCommand().equals("Agree")) {
                    amounts[selected]-=amount;
                    sales +="\nSold "+amount+" "+ids[selected]+" for "+myOffer*amount+" coins.";
                    totalMoneyMade+=myOffer*amount;
                    System.out.println(totalMoneyMade);
                    System.out.println(sales);
                    bartering = false;
                    selected = -1;
                    out.setText("A pleasure doing business with you.");
                    
                    myOffer = -1;
                    prevOffer = -1;
                    frame.add(currencybox);
                    frame.repaint();
                }
            }
        });
        agree.setActionCommand("Agree");
        leave = new JButton("Leave");
        leave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (ae.getActionCommand().equals("Leave")) {
                    out.setText("It seems that we could not make a deal.");
                    bartering = false;
                    selected = -1;
                    myOffer = -1;
                    frame.add(currencybox);
                    prevOffer = -1;
                    frame.repaint();
                }
            }
        });
        leave.setActionCommand("Leave");
        frame.add(leave);
        frame.remove(leave);
        barter = new JButton("Barter");
        barter.setActionCommand("Barter");
        barter.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (ae.getActionCommand().equals("Barter")) {
                    //amounts[selected]--;
                    boolean barter = true;
                    try {
                        amount = Integer.parseInt(amountT.getText());
                    } catch (Exception e) {
                        barter = false;
                    }
                    if (!canBuyMultiple[selected]){
                        amount=1;
                        barter=true;
                    }
                    if (barter) {
                        if (amounts[selected]>=amount){
                        frame.add(leave);
                        frame.add(in);
                        myOffer = cost[selected]+5;
                        out.setText("My offer is " + convertToCurrent(myOffer) + " " + c());
                        bartering = true;
                        frame.setSize(frame.getWidth() - 1, frame.getHeight() - 1);
                        frame.setSize(frame.getWidth() + 1, frame.getHeight() + 1);
                        frame.remove(currencybox);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Bazaar.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        frame.repaint();
                        }else{
                            out.setText("Not enough");
                        }
                    } else {
                        out.setText("Please enter a valid amount.");
                    }
                }
            }
        });
        
        b = new JButton("Make an offer");
        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (ae.getActionCommand().equals("Enter")) {
                    barterlaikaboss();
                }
            }
        });
        b.setActionCommand("Enter");
        currencybox = new JComboBox(currencyNames);
        currencybox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                currency = currencybox.getSelectedIndex();
            }
        });
        frame.add(currencybox);
        frame.add(kitten);
        frame.add(sillyzack);
        for (int i = 0; i < paths.length; i++) {
            frame.add(load(paths[i], -1, 200, i));
        }
        //frame.setUndecorated(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        M.repaint();
    }


}