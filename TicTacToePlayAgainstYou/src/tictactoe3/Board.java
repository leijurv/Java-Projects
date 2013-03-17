/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe3;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author leijurv
 */
public class Board {

    static final int[][] d = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};
    int[] pos = new int[9];
    boolean move;

    public Board(int[] P, boolean m) {
        System.arraycopy(P, 0, pos, 0, 9);
        move = m;
    }

    public void show(Graphics g, int x, int y, int size) {
        g.setColor(Color.BLACK);
        g.drawLine(x + size / 3, y, x + size / 3, y + size);
        g.drawLine(x + 2 * size / 3, y, x + 2 * size / 3, y + size);
        g.drawLine(x, y + size / 3, x + size, y + size / 3);
        g.drawLine(x, y + 2 * size / 3, x + size, y + 2 * size / 3);
        for (int i = 0; i < pos.length; i++) {
            int X = i % 3 * size / 3 + x;
            int Y = ((i - i % 3) / 3) * size / 3 + y;
            if (pos[i] != 0) {
                if (pos[i] == -1) {
                    g.setColor(Color.BLUE);
                    g.drawOval(X, Y, size / 3, size / 3);
                } else {
                    g.setColor(Color.RED);
                    g.drawLine(X, Y, X + size / 3, Y + size / 3);
                    g.drawLine(X, Y + size / 3, X + size / 3, Y);
                }

            }
        }
    }

    public boolean full() {
        for (int i = 0; i < pos.length; i++) {
            if (pos[i] == 0) {
                return false;
            }
        }
        return true;
    }

    public int[] solve() {
        int n = done();
        if (n != 0) {
            return new int[]{n, -1};
        }
        boolean tie = false;
        boolean possible = false;
        int tiepos = -1;
        Board[] b = new Board[9];
        for (int i = 0; i < pos.length; i++) {
            if (pos[i] == 0) {
                possible = true;
                Board Q = new Board(pos, !move);
                Q.pos[i] = move ? 1 : -1;
                b[i] = Q;
                if (Q.done() != 0) {
                    return new int[]{Q.done(), i};
                }
            }
        }
        if (!possible) {
            return new int[]{0, -1};
        }
        for (int i = 0; i < pos.length; i++) {
            if (pos[i] == 0) {
                if (tiepos == -1) {
                    tiepos = i;
                }
                Board Q = b[i];
                int[] m = Q.solve();
                if (m[0] == (move ? 1 : -1)) {
                    return new int[]{m[0], i};
                } else {
                    if (m[0] == 0) {
                        tie = true;
                        tiepos = i;
                    }
                }
            }
        }
        if (tie) {
            return new int[]{0, tiepos};
        }
        return new int[]{move ? -1 : 1, tiepos};
    }

    public void render(Graphics g, int x, int y, int size) {
        if (size < 1) {
            return;
        }
        show(g, x, y, size);
        if (done() == 0) {
            if (!move) {
                for (int i = 0; i < pos.length; i++) {
                    int X = i % 3 * size / 3 + x;
                    int Y = ((i - i % 3) / 3) * size / 3 + y;
                    if (pos[i] == 0) {
                        Board Q = new Board(pos, !move);
                        Q.pos[i] = move ? 1 : -1;
                        Q.render(g, 2 + X, 2 + Y, size / 3 - 4);
                    }
                }
            } else {
                int[] n = solve();
                if (n[1] == -1) {
                    return;
                }
                Board Q = new Board(pos, !move);
                Q.pos[n[1]] = move ? 1 : -1;
                Q.render(g, x, y, size);
            }

        }
    }

    public int done() {
        for (int i = 0; i < d.length; i++) {
            if (pos[d[i][0]] != 0 && pos[d[i][0]] == pos[d[i][1]] && pos[d[i][1]] == pos[d[i][2]]) {
                return pos[d[i][0]];
            }
        }
        return 0;
    }
}
