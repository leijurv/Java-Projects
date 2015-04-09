/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chess;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
/**
 *
 * @author leijurv
 */
public class Chess {
    /**
     * @param args the command line arguments
     */
    public static void main1(String[] args) throws IOException {
        Board b = new Board();
        BufferedImage baseChess = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
        b.draw(baseChess);
        //ImageIO.write(baseChess, "bmp", new File("/Users/leijurv/Downloads/baseChess.bmp"));
        Piece[][] board = b.board;
        board[6][1] = board[2][7];
        board[2][7] = null;
        board[1][5] = board[1][6];
        board[1][6] = null;
        board[5][6] = null;
        board[6][6] = null;
        board[6][5] = board[0][1];
        board[7][4] = board[3][0];
        board[3][0] = null;
        board[3][3] = board[3][1];
        board[3][1] = null;
        board[3][2] = board[5][0];
        board[5][0] = null;
        board[4][1] = null;
        //System.out.println(b);
        BufferedImage fullChess = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
        b.draw(fullChess);
        //ImageIO.write(fullChess, "bmp", new File("/Users/leijurv/Downloads/fullChess.bmp"));
        BufferedImage cool = ImageIO.read(new File("/Users/leijurv/Downloads/dank.jpg"));
        int offset = cool.getWidth() - 8;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                cool.setRGB(x, y, baseChess.getRGB(x, y));
                cool.setRGB(x + offset, y, fullChess.getRGB(x, y));
            }
        }
        ImageIO.write(cool, "bmp", new File("/Users/leijurv/Downloads/dankkkkkk.bmp"));
        System.out.println(new Board(cool));
    }
    public static void main(String[] args) throws IOException {
        BufferedImage cool = ImageIO.read(new File("/Users/leijurv/Downloads/dankkkkkkOutput.bmp"));
        System.out.println(new Board(cool));
    }
}
