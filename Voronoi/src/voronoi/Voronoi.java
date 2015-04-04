/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voronoi;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.WindowConstants;
import java.util.ArrayList;
/**
 *
 * @author leijurv
 */
public class Voronoi {
    static ArrayList<int[]> points = new ArrayList<>();
    static ArrayList<Color> colors = new ArrayList<>();
    static int[][] distSq;
    static int[][] rgb;
    static BufferedImage image = null;
    static Random r = new Random(5021);
    static boolean changed = false;
    static boolean running = false;
    static JFrame frame;
    public static void redraw() {//Completely redraw
        if (colors.isEmpty()) {
            return;
        }
        int height = image.getHeight();
        int width = image.getWidth();
        int np = points.size();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int closest = -1;
                int rdistSq = 0;
                for (int i = 0; i < np; i++) {
                    int[] coord = points.get(i);
                    int xDiff = x - coord[0];
                    int yDiff = y - coord[1];
                    int dist = xDiff * xDiff + yDiff * yDiff;
                    if (closest == -1 || dist < rdistSq) {
                        closest = i;
                        rdistSq = dist;
                    }
                }
                distSq[x][y] = rdistSq;
                int color = colors.get(closest).getRGB();
                rgb[x][y] = color;
                image.setRGB(x, y, color);
            }
            frame.repaint();
        }
    }
    public static void checkLatest() {//During click and drag, only redraw the latest
        if (colors.size() < 3) {
            redraw();
            return;
        }
        int closInd = points.size() - 1;
        int closRGB = colors.get(closInd).getRGB();
        int[] latest = points.get(closInd);
        int latestX = latest[0];
        int latestY = latest[1];
        int height = image.getHeight();
        int width = image.getWidth();
        search(latestX, 1, width, height, latestY, closRGB);
        search(latestX, -1, width, height, latestY, closRGB);
    }
    public static void search(int latestX, int dir, int width, int height, int latestY, int closRGB) {
        boolean hasHitYetX = false;
        for (int x = latestX - dir; x < width && x >= 0; x += dir) {
            int xDiff = x - latestX;
            int xDiffSq = xDiff * xDiff;
            boolean justHitX = false;
            boolean hasHitY = false;
            for (int y = 0; y < height; y++) {
                int currDist = distSq[x][y];
                int yDiff = y - latestY;
                int dist = xDiffSq + yDiff * yDiff;
                if (dist < currDist) {
                    image.setRGB(x, y, closRGB);
                    justHitX = true;
                    hasHitY = true;
                } else {
                    image.setRGB(x, y, rgb[x][y]);
                    if (hasHitY) {
                        while (y < height) {
                            image.setRGB(x, y, rgb[x][y]);
                            y++;
                        }
                        break;
                    }
                }
            }
            frame.repaint();
            if (justHitX) {
                hasHitYetX = true;
            } else {
                if (hasHitYetX) {
                    int numEmptyColumns = 0;
                    for (int XX = x - 2 * dir; XX < width && XX >= 0; XX += dir) {
                        boolean hitY = false;
                        for (int y = 0; y < height; y++) {
                            int d = rgb[XX][y];
                            if (image.getRGB(XX, y) != d) {
                                image.setRGB(XX, y, rgb[XX][y]);
                                hitY = true;
                            } else {
                                if (hitY) {
                                    break;
                                }
                            }
                        }
                        if (!hitY) {
                            numEmptyColumns++;
                        } else {
                            numEmptyColumns = 0;
                        }
                        if (numEmptyColumns > 5) {
                            return;
                        }
                    }
                    frame.repaint();
                    return;//Shape is over if has hit and not just hit
                }
            }
        }
    }
    public static void finishLatest() {
        if (colors.size() < 3) {
            return;
        }
        int closInd = points.size() - 1;
        int closRGB = colors.get(closInd).getRGB();
        int[] latest = points.get(closInd);
        int latestX = latest[0];
        int latestY = latest[1];
        int height = image.getHeight();
        int width = image.getWidth();
        for (int x = 0; x < width; x++) {
            int xDiff = x - latestX;
            int xDiffSq = xDiff * xDiff;
            for (int y = 0; y < height; y++) {
                int currDist = distSq[x][y];
                int yDiff = y - latestY;
                int dist = xDiffSq + yDiff * yDiff;
                if (dist < currDist) {
                    rgb[x][y] = closRGB;
                    distSq[x][y] = dist;
                }
                image.setRGB(x, y, rgb[x][y]);
            }
        }
    }
    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        frame = new JFrame("dank swamp kush");
        JComponent M = new JComponent() {
            private static final long serialVersionUID = 1L;
            @Override
            public void paintComponent(Graphics g) {
                if (image != null) {
                    g.drawImage(image, 0, 0, null);
                }
                points.stream().forEach((coord)->{
                    g.fillRect(coord[0] - 1, coord[1] - 1, 3, 3);
                });
            }
        };
        frame.setContentPane(M);
        M.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
            @Override
            public void mousePressed(MouseEvent e) {
                points.add(new int[] {e.getX(), e.getY()});
                addRandomColor();
                Do();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                finishLatest();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        M.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int[] c;
                c = points.get(points.size() - 1);
                c[0] = e.getX();
                c[1] = e.getY();
                Do();
            }
            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
        frame.setSize(10000, 10000);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        Thread.sleep(500);
        image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
        distSq = new int[frame.getWidth()][frame.getHeight()];
        rgb = new int[frame.getWidth()][frame.getHeight()];
        /*
         for (int i = 0; i < frame.getWidth(); i += 10) {
         points.add(new int[] {i, frame.getHeight() / 2});
         addRandomColor();
         }*/
        redraw();
    }
    public static void Do() {
        if (!running) {
            running = true;
            new Thread() {
                @Override
                public void run() {
                    changed = false;
                    checkLatest();
                    running = false;
                    if (changed) {
                        Do();
                    }
                }
            }.start();
        } else {
            changed = true;
        }
    }
    public static void addRandomColor() {
        colors.add(new Color(r.nextFloat(), r.nextFloat(), r.nextFloat()));
    }
}
