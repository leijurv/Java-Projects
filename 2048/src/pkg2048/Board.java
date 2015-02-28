/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2048;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
/**
 *
 * @author leijurv
 */
public class Board {
    byte[][] board;
    int inGameScore;
    static final String[] moveNames = new String[] {"UP","DOWN","LEFT","RIGHT"};
    public Board() {
        board = new byte[4][4];
        inGameScore = 0;
    }
    public Board(byte[][] board,int score) {
        this(board,score,false);
    }
    public Board(byte[][] board,int score,boolean copy) {
        if (copy) {
            this.board = new byte[4][4];
            for (int row = 0; row < 4; row++) {
                System.arraycopy(board[row],0,this.board[row],0,4);
            }
        } else {
            this.board = board;
        }
        this.inGameScore = score;
    }
    public Board move(int dir) {
        int tmpScore = inGameScore;
        byte[][] res = new byte[4][4];
        switch (dir) {
            case 0: //UP
                for (int column = 0; column < 4; column++) {
                    byte[] col = new byte[] {board[0][column],board[1][column],board[2][column],board[3][column]};
                    tmpScore += moveRow(col);
                    res[0][column] = col[0];
                    res[1][column] = col[1];
                    res[2][column] = col[2];
                    res[3][column] = col[3];
                }
                break;
            case 1:
                //DOWN
                for (int column = 0; column < 4; column++) {
                    byte[] col = new byte[] {board[3][column],board[2][column],board[1][column],board[0][column]};
                    tmpScore += moveRow(col);
                    res[0][column] = col[3];
                    res[1][column] = col[2];
                    res[2][column] = col[1];
                    res[3][column] = col[0];
                }
                break;
            case 2://LEFT
                for (int row = 0; row < 4; row++) {
                    byte[] ro = new byte[] {board[row][0],board[row][1],board[row][2],board[row][3]};
                    tmpScore += moveRow(ro);
                    res[row] = ro;
                }
                break;
            case 3://RIGHT
                for (int row = 0; row < 4; row++) {
                    byte[] ro = new byte[] {board[row][3],board[row][2],board[row][1],board[row][0]};
                    tmpScore += moveRow(ro);
                    res[row][0] = ro[3];
                    res[row][1] = ro[2];
                    res[row][2] = ro[1];
                    res[row][3] = ro[0];
                }
        }
        return new Board(res,tmpScore);
    }
    public static int moveRow(byte[] x) {
        for (int i = 0; i < 3; i++) {
            if (x[i] == 0 && x[i + 1] != 0) {
                x[i] = x[i + 1];
                x[i + 1] = 0;
                i = i == 0 ? -1 : i - 2;
            }
        }
        int score = 0;
        for (int i = 0; i < 3; i++) {
            if (x[i] == x[i + 1] && x[i] != 0) {
                x[i]++;
                score += Math.pow(2,x[i]);
                x[i + 1] = 0;
                for (int j = i + 1; j < x.length - 1; j++) {
                    if (x[j] == 0 && x[j + 1] != 0) {
                        x[j] = x[j + 1];
                        x[j + 1] = 0;
                        j = j == 0 ? -1 : j - 2;
                    }
                }
            }
        }
        return score;
    }
    public static ArrayList<int[]> getOpenSpots(byte[][] board) {
        ArrayList<int[]> openSpots = new ArrayList<>();
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                if (board[row][column] == 0) {
                    openSpots.add(new int[] {row,column});
                }
            }
        }
        return openSpots;
    }
    public static int getNumOpenSpots(byte[][] board) {
        int num = 0;
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                if (board[row][column] == 0) {
                    num++;
                }
            }
        }
        return num;
    }
    public static boolean isFull(byte[][] board) {
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                if (board[row][column] != 0) {
                    return false;
                }
            }
        }
        return true;
    }
    public static int[] getRandomOpenSpot(byte[][] board) {
        ArrayList<int[]> openSpots = getOpenSpots(board);
        if (openSpots.isEmpty()) {
            return null;
        }
        return openSpots.get(new Random().nextInt(openSpots.size()));
    }
    public void placeRandomly() {
        int[] spot = getRandomOpenSpot(board);
        if (spot == null) {
            return;
        }
        board[spot[0]][spot[1]] = (byte) (new Random().nextInt(10) == 0 ? 2 : 1);
    }
    public double getScore1() {
        return getNumOpenSpots(board);
    }
    public double getScore() {
        return inGameScore * getNumOpenSpots(board);
    }
    public double getScore2() {
        double score = 0;
        int numOpen = 0;
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                double sc = Math.pow(2,board[x][y]);
                if ((x == 1 || x == 2) && (y == 1 || y == 2)) {
                    sc = -sc;
                }
                score += sc;
            }
        }
        return score * getNumOpenSpots(board);
    }
    public double getScore3() {
        double score = 0;
        byte scoreLimit = 3;
        double base = 1.2;
        int numOpen = 0;
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (board[x][y] > scoreLimit) {
                    double sc = Math.pow(base,board[x][y]);
                    if ((x == 1 || x == 2) && (y == 1 || y == 2)) {
                        sc = -sc * 0.4;
                    }
                    score += sc;
                }
                if (board[x][y] == 0) {
                    numOpen++;
                }
            }
        }
        score = score * getMultiplier(getNumOpenSpots(board));
        //score += numOpen;
        return score;
    }
    public static double getMultiplier(int numOpen) {
        switch (numOpen) {
            case 1:
                return 0.6;
            case 2:
                return 0.7;
            case 3:
                return 0.8;
            case 4:
                return 0.9;
            default:
                return 1;
        }
    }
    public double[] solve(int depth,double weight) {
        if (depth == 0) {
            if (isFull(board)) {
                for (int i = 0; i < 4; i++) {
                    if (move(i).inGameScore != inGameScore) {//Checking if the move happened
                        return new double[] {weight * getScore(),-1};
                    }
                }
                return new double[] {-100 * weight,-1};//Unable to move
            }
            return new double[] {weight * getScore(),-1};
        }
        double maxScore = -1000000000;
        int maxMove = -1;
        int numcont = 0;
        for (int move = 0; move < 4; move++) {
            ArrayList<Board> possibles = new ArrayList<>();
            ArrayList<Double> weights = new ArrayList<>();
            Board result = move(move);
            if (result.equal(board)) {//If nothing moved, then don't keep looking
                numcont++;
                continue;
            }
            ArrayList<int[]> openSpots = getOpenSpots(result.board);
            double w = 1 / ((double) openSpots.size());
            for (int[] coord : openSpots) {
                Board tmp2 = new Board(result.board,result.inGameScore,true);
                tmp2.board[coord[0]][coord[1]] = 2;
                possibles.add(tmp2);
                weights.add(weight * 0.9 * w);
                Board tmp4 = new Board(result.board,result.inGameScore,true);
                tmp4.board[coord[0]][coord[1]] = 4;
                possibles.add(tmp4);
                weights.add(weight * 0.1 * w);
            }
            double moveScore = 0;
            for (int i = 0; i < possibles.size(); i++) {
                Board b = possibles.get(i);
                double boardScore = b.solve(depth - 1,weights.get(i))[0];
                moveScore += boardScore;
            }
            if (moveScore > maxScore || maxMove == -1) {
                maxScore = moveScore;
                maxMove = move;
            }
        }
        return new double[] {maxScore,maxMove};
    }
    public String toString() {
        int[][] b = new int[4][4];
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                b[x][y] = (int) (board[x][y] == 0 ? 0 : Math.pow(2,board[x][y]));
            }
        }
        return inGameScore + "\n" + b[0][0] + " " + b[0][1] + " " + b[0][2] + " " + b[0][3] + "\n" + b[1][0] + " " + b[1][1] + " " + b[1][2] + " " + b[1][3] + "\n" + b[2][0] + " " + b[2][1] + " " + b[2][2] + " " + b[2][3] + "\n" + b[3][0] + " " + b[3][1] + " " + b[3][2] + " " + b[3][3] + "\n";
    }
    public void draw(Graphics g) {
        g.drawString("Score: " + inGameScore,50,50);
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                g.drawString((int) (board[row][column] == 0 ? 0 : Math.pow(2,board[row][column])) + "",column * 100 + 100,row * 100 + 100);
            }
        }
    }
    public boolean equal(byte[][] oth) {
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (oth[x][y] != board[x][y]) {
                    return false;
                }
            }
        }
        return true;
    }
}
