/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chess;
import java.awt.Color;
/**
 *
 * @author leijurv
 */
public class Piece {
    final PieceType type;
    final ColorBlackWhite color;
    public Piece(PieceType type, boolean white) {
        this.type = type;
        color = white ? ColorBlackWhite.WHITE : ColorBlackWhite.BLACK;
    }
    public Color getColor() {
        int Kolor = color.base + type.value * color.multiplier;
        return new Color(Kolor, Kolor, Kolor);
    }
    public static enum PieceType {
        PAWN(1), KNIGHT(3), BISHOP(4), ROOK(5), QUEEN(9), KING(20);
        final int value;
        private PieceType(int value) {
            this.value = value;
        }
    }
    public static enum ColorBlackWhite {
        BLACK(0, 1, new Color(139, 69, 19)), WHITE(255, -1, new Color(100, 100, 100));
        final int base;
        final int multiplier;
        final Color empty;
        private ColorBlackWhite(int base, int multiplier, Color empty) {
            this.base = base;
            this.multiplier = multiplier;
            this.empty = empty;
        }
    }
}
