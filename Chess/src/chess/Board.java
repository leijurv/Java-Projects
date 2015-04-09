package chess;
import chess.Piece.ColorBlackWhite;
import chess.Piece.PieceType;
import java.awt.image.*;
import java.awt.Color;
/**
 *
 * @author leijurv
 */
public class Board {
    Piece[][] board = new Piece[8][8];
    public Board() {
        board[0][0] = new Piece(PieceType.ROOK, true);
        board[1][0] = new Piece(PieceType.KNIGHT, true);
        board[2][0] = new Piece(PieceType.BISHOP, true);
        board[3][0] = new Piece(PieceType.QUEEN, true);
        board[4][0] = new Piece(PieceType.KING, true);
        board[5][0] = new Piece(PieceType.BISHOP, true);
        board[6][0] = new Piece(PieceType.KNIGHT, true);
        board[7][0] = new Piece(PieceType.ROOK, true);
        for (int i = 0; i < 8; i++) {
            board[i][1] = new Piece(PieceType.PAWN, true);
        }
        board[0][7] = new Piece(PieceType.ROOK, false);
        board[1][7] = new Piece(PieceType.KNIGHT, false);
        board[2][7] = new Piece(PieceType.BISHOP, false);
        board[3][7] = new Piece(PieceType.QUEEN, false);
        board[4][7] = new Piece(PieceType.KING, false);
        board[5][7] = new Piece(PieceType.BISHOP, false);
        board[6][7] = new Piece(PieceType.KNIGHT, false);
        board[7][7] = new Piece(PieceType.ROOK, false);
        for (int i = 0; i < 8; i++) {
            board[i][6] = new Piece(PieceType.PAWN, false);
        }
    }
    public Board(BufferedImage imag) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                board[x][y] = pieceFromPixel(imag.getRGB(x, 7 - y));
            }
        }
    }
    public static Piece pieceFromPixel(int c) {
        Board b = new Board();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (b.board[x][y] != null) {
                    if (b.board[x][y].getColor().getRGB() == c) {
                        return b.board[x][y];
                    }
                }
            }
        }
        return null;
    }
    public void draw(BufferedImage image) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                image.setRGB(x, 7 - y, getColor(x, y).getRGB());
            }
        }
    }
    public Color getColor(int x, int y) {
        if (board[x][y] != null) {
            return board[x][y].getColor();
        }
        ColorBlackWhite c = ((x + y) & 1) == 1 ? ColorBlackWhite.WHITE : ColorBlackWhite.BLACK;
        return c.empty;
    }
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            for (int x = 0; x < 8; x++) {
                if (board[x][i] == null) {
                    s.append("null");
                } else {
                    s.append(board[x][i].color.multiplier);
                    s.append(board[x][i].type.toString());
                }
                s.append(" ");
            }
            s.append("\n");
        }
        return s.toString();
    }
}
