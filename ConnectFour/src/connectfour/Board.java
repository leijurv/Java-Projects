/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connectfour;

/**
 *
 * @author leijurv
 */
public class Board {

    static int depth = 0;
    int[][] board = new int[7][6];
    int sofar;
    boolean move;
    static final String[] values = {"B", " ", "R"};
    static int screenUpdateDepth = 0;
    int Eval = -2;

    public Board(int[][] a, int b, boolean m) {
        sofar = b;
        for (int i = 0; i < 7; i++) {
            System.arraycopy(a[i], 0, board[i], 0, 6);
        }
        move = m;
    }

    public int eval() {
        if (Eval == -2) {
            Eval = reval();
            return eval();
        }
        return Eval;
    }

    public int reval() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                if (board[i][j] != 0 && board[i + 0][j] == board[i + 1][j] && board[i + 1][j] == board[i + 2][j] && board[i + 2][j] == board[i + 3][j]) {

                    return board[i][j];
                }
            }
        }
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] != 0 && board[i][j] == board[i][j + 1] && board[i][j + 1] == board[i][j + 2] && board[i][j + 2] == board[i][j + 3]) {
                    return board[i][j];
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] != 0 && board[i][j] == board[i + 1][j + 1] && board[i + 1][j + 1] == board[i + 2][j + 2] && board[i + 2][j + 2] == board[i + 2][j + 3]) {
                    return board[i][j];
                }
                if (board[i][j + 3] != 0 && board[i][j + 3] == board[i + 1][j + 2] && board[i + 1][j + 2] == board[i + 2][j + 1] && board[i + 2][j + 1] == board[i + 3][j]) {
                    return board[i][j + 3];
                }
            }
        }
        return 0;
    }

    public int solve() {
        if (sofar == depth) {
            return eval();
        }
        int j = eval();
        if (j != 0) {
            return j;
        }
        int worst = move ? -1 : 1;
        Board[] B = new Board[7];
        int[] Evals = new int[7];
        for (int i = 0; i < 7; i++) {
            B[i] = null;
            Evals[i] = -2;
            if (board[i][0] == 0) {
                Board b = new Board(board, sofar + 1, !move);
                int height = 0;
                while (height < 6 && b.board[i][height] == 0) {
                    height++;
                }
                height--;
                b.board[i][height] = move ? 1 : -1;
                B[i] = b;
                Evals[i] = b.eval();
            }
        }
        for (int i = 0; i < 7; i++) {
            if (B[i] != null) {
                int result = Evals[i];
                if ((move && result == 1) || (!move && result == -1)) {
                    if (sofar == 0) {
                        ConnectFour.optimalMove = i;
                        ConnectFour.M.repaint();
                    }
                    return result;
                }
            }
        }
        for (int i = 0; i < 7; i++) {
            Board b = B[i];
            if (b != null) {
                int result = b.solve();
                if (sofar < screenUpdateDepth) {
                    ConnectFour.updateScreen(b, result);
                }
                if ((move && result == 1) || (!move && result == -1)) {
                    if (sofar == 0) {
                        ConnectFour.optimalMove = i;
                        ConnectFour.M.repaint();
                    }
                    return result;
                }
                if (result == 0 && worst != 0) {
                    if (sofar == 0) {
                        ConnectFour.optimalMove = i;
                        ConnectFour.M.repaint();
                    }
                    worst = 0;
                }
            }
        }
        if (ConnectFour.optimalMove == -1) {
            ConnectFour.optimalMove = -2;
        }
        return worst;
    }

    @Override
    public String toString() {
        String result = "";
        for (int j = 0; j < 6; j++) {
            for (int i = 0; i < 7; i++) {
                result = result + values[1 + board[i][j]] + " ";
            }
            result = result + "\n";
        }
        result = result + values[move ? 2 : 0] + "'s turn";
        return result;
    }
}
