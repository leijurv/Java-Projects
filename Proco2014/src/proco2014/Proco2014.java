/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proco2014;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.math.BigInteger;
/**
 *
 * @author leijurv
 */
public class Proco2014 {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        advL(scan);
    }
    public static void advA(Scanner scan) {
        String mn = scan.nextLine();
        int m = Integer.parseInt(mn.split(" ")[0]);
        int n = Integer.parseInt(mn.split(" ")[1]);
        String message = scan.nextLine();
        char[] mess = message.toCharArray();
        StringBuilder resp = new StringBuilder();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                resp.append(mess[j * m + i]);
            }
        }
        System.out.println(resp);
    }
    public static void advB(Scanner scan) {
        double b = Double.parseDouble(scan.nextLine());
        int numItems = Integer.parseInt(scan.nextLine());
        ArrayList<Double> price = new ArrayList<>(numItems);
        ArrayList<Double> utility = new ArrayList<>(numItems);
        ArrayList<Double> efficiency = new ArrayList<>(numItems);
        for (int i = 0; i < numItems; i++) {
            String s = scan.nextLine();
            double p = Double.parseDouble(s.split(" ")[0]);
            double u = Double.parseDouble(s.split(" ")[1]);
            price.add(p);
            utility.add(u);
            efficiency.add(u / p);
        }
        double totalUtility = 0;
        while (b > 0) {
            //System.out.println(price);
            //System.out.println(utility);
            //System.out.println(efficiency);
            if (price.isEmpty()) {
                break;
            }
            double maxEff = -1;
            int maxInd = 0;
            for (int i = 0; i < numItems; i++) {
                if (maxEff == -1 || efficiency.get(i) > maxEff) {
                    maxEff = efficiency.get(i);
                    maxInd = i;
                }
            }
            double p = price.remove(maxInd);
            double u = utility.remove(maxInd);
            double e = efficiency.remove(maxInd);
            numItems--;
            //System.out.println("Grabbing " + p + "," + u);
            if (b >= p) {
                b -= p;
                totalUtility += u;
                //System.out.println("Budget is now " + b);
                if (b == 0) {
                    break;
                }
                continue;
            }
            double f = b / p;
            totalUtility += f * u;
            b = 0;
            break;
        }
        System.out.println(totalUtility);
    }
    public static void advC(Scanner scan) {
        String[] tokens = scan.nextLine().split(" ");
        int numerator = 0;
        int denominator = 1;
        for (int i = -1; i < tokens.length; i += 4) {
            int mul = (i == -1 ? 1 : (tokens[i].equals("+") ? 1 : -1));
            int num = Integer.parseInt(tokens[i + 1]);
            int denom = Integer.parseInt(tokens[i + 3]);
            num *= mul;
            //System.out.print(numerator + "/" + denominator + " +");
            //double b = ((double) numerator) / ((double) denominator);
            //double a = ((double) num) / ((double) denom);
            int newNum = num * denominator + numerator * denom;
            denominator *= denom;
            numerator = newNum;
            //System.out.println(mul + " " + num + "/" + denom + "=" + numerator + "/" + denominator);
            //System.out.println(a + "," + b + "," + (a + b) + "," + (((double) numerator) / ((double) denominator)));
        }
        BigInteger b1 = BigInteger.valueOf(numerator);
        BigInteger b2 = BigInteger.valueOf(denominator);
        BigInteger gcd = b1.gcd(b2);
        int GCD = gcd.intValue();
        numerator /= GCD;
        denominator /= GCD;
        System.out.println(numerator + " / " + denominator);
    }
    public static void advD(Scanner scan) {
        double prob = Double.parseDouble(scan.nextLine());
        for (int i = 2; true; i++) {
            //System.out.println("Not " + i);
            if (prob(i) > prob) {
                System.out.println(i);
                return;
            }
        }
    }
    public static double prob(int numPeople) {
        //return 1D - Math.pow(364D / 365D, numPeople * (numPeople - 1) / 2D);
        //^^ Apparently that is only an approximation
        BigInteger n = BigInteger.valueOf(numPeople);
        BigInteger q = new BigInteger("100000");
        for (int i = 365 - numPeople + 1; i <= 365; i++) {
            q = q.multiply(BigInteger.valueOf(i));
        }
        BigInteger d = new BigInteger("365").pow(numPeople);
        double r = q.divide(d).intValue();
        r = r / 100000;//totally didn't get this algorithm from wikipedia
        //I probably would have done it monte carlo otherwise =DDDDD
        return 1 - r;
    }
    public static void advE(Scanner scan) {
        String mn = scan.nextLine();
        int n = Integer.parseInt(mn.split(" ")[0]);
        int m = Integer.parseInt(mn.split(" ")[1]);
        boolean[][] maze = new boolean[n][m];
        int bilboX = -1;
        int bilboY = -1;
        for (int i = 0; i < n; i++) {
            String line = scan.nextLine();
            for (int j = 0; j < m; j++) {
                char c = line.charAt(j);
                if (c == 'S') {
                    bilboX = i;
                    bilboY = j;
                }
                maze[i][j] = c != '#';
            }
        }
        int[][] cost = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                cost[i][j] = Integer.MAX_VALUE - 10;
            }
        }
        cost[bilboX][bilboY] = 1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (maze[i][j]) {
                    boolean c = false;
                    for (int dX = -1; dX <= 1; dX++) {
                        for (int dY = -1; dY <= 1; dY++) {
                            int x = i + dX;
                            int y = j + dY;
                            int C;
                            if (x < 0 || x >= n || y < 0 || y >= m) {
                                C = Integer.MAX_VALUE - 10;
                            } else {
                                C = cost[x][y];
                            }
                            if (C + 1 < cost[i][j]) {
                                cost[i][j] = C + 1;
                                //System.out.println("Cost at " + i + "," + j + " is now " + cost[i][j] + " because of " + x + "," + y);
                                c = true;
                            }
                        }
                    }
                    if (c) {
                        i = 0;
                        j = -1;//reset actually to 0 because its about to do j++
                    }
                }
            }
        }
        int minCost = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            if (cost[i][0] < minCost) {
                minCost = cost[i][0];
            }
            if (cost[i][m - 1] < minCost) {
                minCost = cost[i][m - 1];
            }
        }
        for (int i = 0; i < m; i++) {
            if (cost[0][i] < minCost) {
                minCost = cost[0][i];
            }
            if (cost[n - 1][i] < minCost) {
                minCost = cost[n - 1][i];
            }
        }
        if (minCost > 1000000) {
            System.out.println("NO ESCAPE!");
        } else {
            System.out.println(minCost);
        }
    }
    public static void advF(Scanner scan) {
        String nt = scan.nextLine();
        int n = Integer.parseInt(nt.split(" ")[0]);
        long t = Long.parseLong(nt.split(" ")[1]);
        int[] positions = new int[n];
        int[] directions = new int[n];
        //int maxPos = 0;
        for (int i = 0; i < n; i++) {
            String s = scan.nextLine();
            positions[i] = Integer.parseInt(s.split(" ")[0]);
            directions[i] = ((s.split(" ")[1]).equals("+") ? 1 : -1);
            //if (positions[i] > maxPos) {
            //    maxPos = positions[i];
            //}
        }
        for (int time = 0; time <= t; time++) {
            /*
             maxPos = positions[0];
             for (int i = 0; i < n; i++) {
             if (positions[i] > maxPos) {
             maxPos = positions[i];
             }
             }
             for (int x = 0; x <= maxPos; x++) {
             boolean p = false;
             for (int i = 0; i < n; i++) {
             if (positions[i] == x) {
             if (p) {
             System.out.print("%");
             }
             System.out.print(directions[i] == 1 ? ">" : (directions[i] == -1 ? "<" : "?"));
             p = true;
             }
             }
             if (!p) {
             System.out.print(" ");
             }
             }
             System.out.println();*/
            if (time == t) {
                for (int i = 0; i < n; i++) {
                    System.out.println(positions[i] + " " + (directions[i] == 1 ? "+" : "-"));
                }
                return;
            }
            for (int i = 0; i < n - 1; i++) {
                if (positions[i] == positions[i + 1]) {
                    if (directions[i] == 1 && directions[i + 1] == -1) {
                        directions[i] = 0 - directions[i];
                        directions[i + 1] = 0 - directions[i + 1];
                    }
                }
            }
            for (int i = 0; i < n; i++) {
                positions[i] += directions[i];
                if (i < n - 1) {
                    if (positions[i] == positions[i + 1]) {
                        positions[i]--;
                        directions[i] = -1;
                        directions[i + 1] = 1;
                        positions[i + 1]--;
                    }
                }
            }
        }
    }
    public static void advG(Scanner scan) {
        int n = Integer.parseInt(scan.nextLine());
        int[] spiders = new int[n];
        String[] nums = scan.nextLine().split(" ");
        for (int i = 0; i < n; i++) {
            spiders[i] = Integer.parseInt(nums[i]);
        }
        int numArrows = 0;
        while (true) {
            int st = -1;
            for (int start = 0; start < n; start++) {
                if (spiders[start] > 0) {
                    st = start;
                    break;
                }
            }
            if (st == -1) {
                break;//all spiders are gone
            }
            for (int end = st; end < n; end++) {
                if (spiders[end] > 0) {
                    spiders[end]--;
                } else {
                    break;//Just hit an empty column
                }
            }
            numArrows++;
        }
        System.out.println(numArrows);
    }
    public static void advH(Scanner scan) {
        String nk = scan.nextLine();
        int n = Integer.parseInt(nk.split(" ")[0]);
        int k = Integer.parseInt(nk.split(" ")[1]);
        int[] knowWithin = new int[n];
        for (int i = 0; i < n; i++) {
            knowWithin[i] = i + 1;
        }
        ArrayList<Integer> knowContains = new ArrayList<Integer>();
        while (true) {
            int[] N = new int[knowWithin.length - 1];
            int toRemove = -1;
            for (int i = 0; i < knowWithin.length; i++) {
                if (!knowContains.contains(knowWithin[i])) {
                    toRemove = i;
                    break;
                }
            }
            //System.out.println("Removing" + knowWithin[toRemove]);
            for (int i = 0; i < toRemove; i++) {
                N[i] = knowWithin[i];
            }
            for (int i = toRemove; i < N.length; i++) {
                N[i] = knowWithin[i + 1];
            }
            boolean b = guess(N, scan);
            if (b) {
                knowWithin = N;//We now know that it's contained in N
                if (N.length == k) {
                    break;
                }
                continue;
            }
            //Since it's in knowWithin but not in N, the thing we removed must be a part of it
            knowContains.add(knowWithin[toRemove]);
        }
        System.out.print("CLIQUE: ");
        print(knowWithin);
        System.out.println();
    }
    public static void print(int[] clique) {
        for (int i = 0; true;) {
            System.out.print(clique[i]);
            i++;
            if (i >= clique.length) {
                break;
            }
            System.out.print(" ");
        }
    }
    public static boolean guess(int[] clique, Scanner scan) {
        print(clique);
        System.out.println();
        System.out.flush();
        return scan.nextLine().equals("YES");
    }
    public static void advI(Scanner scan) {
        int n = Integer.parseInt(scan.nextLine());
        int[] A = new int[n];
        String[] nums = scan.nextLine().split(" ");
        int maxH = 0;
        for (int i = 0; i < n; i++) {
            A[i] = Integer.parseInt(nums[i]);
            if (A[i] > maxH) {
                maxH = A[i];
            }
        }
        int maxIs = 0;
        for (int x = 0; x < maxH; x++) {
            int numI = findIslands(A, x, n);
            if (numI > maxIs) {
                maxIs = numI;
            }
        }
        System.out.println(maxIs);
    }
    public static int findIslands(int[] A, int x, int n) {
        boolean isInIsland = false;
        int numIslands = 0;
        for (int i = 0; i < n; i++) {
            if (isInIsland) {
                if (A[i] <= x) {
                    isInIsland = false;
                    numIslands++;
                }
            } else {
                if (A[i] > x) {
                    isInIsland = true;
                }
            }
        }
        if (isInIsland) {
            numIslands++;
        }
        return numIslands;
    }
    public static void advJ(Scanner scan) {
        int n = Integer.parseInt(scan.nextLine()) * 2;
        int[] A = new int[n];
        String[] nums = scan.nextLine().split(" ");
        for (int i = 0; i < n; i++) {
            A[i] = Integer.parseInt(nums[i]);
        }
        System.out.println(split(A, 0, 0, 0, 0, n / 2));
    }
    public static int split(int[] i, int location, int diff, int numAlreadyInTeamA, int numAlreadyInTeamB, int max1Team) {
        if (location >= i.length) {
            return diff;
        }
        int c = i[location];
        int bestIfGoesInteamA = 10000000;
        if (numAlreadyInTeamA < max1Team) {
            bestIfGoesInteamA = split(i, location + 1, diff + c, numAlreadyInTeamA + 1, numAlreadyInTeamB, max1Team);
        }
        int bestIfGoesInTeamB = 100000000;
        if (numAlreadyInTeamB < max1Team) {
            bestIfGoesInTeamB = split(i, location + 1, diff - c, numAlreadyInTeamA, numAlreadyInTeamB + 1, max1Team);
        }
        return Math.abs(bestIfGoesInteamA) < Math.abs(bestIfGoesInTeamB) ? bestIfGoesInteamA : bestIfGoesInTeamB;
    }
    public static void advK(Scanner scan) {
        String nq = scan.nextLine();
        int n = Integer.parseInt(nq.split(" ")[0]);
        int q = Integer.parseInt(nq.split(" ")[1]);
        int[] f = new int[n];
        String[] nums = scan.nextLine().split(" ");
        ArrayList<Integer> factions = new ArrayList<>();//using an arraylist because i dont know how many factions there will be yet
        for (int i = 0; i < n; i++) {
            f[i] = Integer.parseInt(nums[i]);
            if (!factions.contains(f[i])) {
                factions.add(f[i]);
            }
        }
        int[] I = new int[q];
        int[] J = new int[q];
        for (int x = 0; x < q; x++) {
            String IJ = scan.nextLine();
            I[x] = Integer.parseInt(IJ.split(" ")[0]) - 1;
            J[x] = Integer.parseInt(IJ.split(" ")[1]) - 1;
        }
        for (int x = 0; x < q; x++) {
            int i = I[x];
            int j = J[x];
            int nnfc = (j - i + 1) / 2;
            int fac = -1;
            for (int fa : factions) {
                int nf = 0;
                for (int y = i; y <= j; y++) {
                    if (f[y] == fa) {
                        nf++;
                    }
                }
                if (nf > nnfc) {
                    fac = fa;
                    break;
                }
            }
            if (fac == -1) {
                System.out.println("NO");
            } else {
                System.out.println("YES " + fac);
            }
        }
    }
    public static void advL(Scanner scan) {
        String nk = scan.nextLine();
        int n = Integer.parseInt(nk.split(" ")[0]);
        int k = Integer.parseInt(nk.split(" ")[1]);
        int[] xCoords = new int[n];
        int[] yCoords = new int[n];
        for (int i = 0; i < n; i++) {
            String xy = scan.nextLine();
            xCoords[i] = Integer.parseInt(xy.split(" ")[0]);
            yCoords[i] = Integer.parseInt(xy.split(" ")[1]);
        }
        int[] possiblePartitions = new int[n];
        for (int i = 0; i < n; i++) {
            possiblePartitions[i] = i;
        }
        ArrayList<ArrayList<Integer>> partitions = partition(possiblePartitions, 0, k);
        double p = partitions.stream().parallel().map((partition)->{
            int[] xCoord = partition.stream().parallel().mapToInt((eagle)->xCoords[eagle]).toArray();
            int[] yCoord = partition.stream().parallel().mapToInt((eagle)->yCoords[eagle]).toArray();
            final double sumX = ((double) Arrays.stream(xCoord).sum()) / ((double) k);
            final double sumY = ((double) Arrays.stream(yCoord).sum()) / ((double) k);
            double diffX = Arrays.stream(xCoord).parallel().mapToDouble((x)->Math.abs(sumX - x)).sum();
            double diffY = Arrays.stream(yCoord).parallel().mapToDouble((y)->Math.abs(sumY - y)).sum();
            double x = diffX + diffY;
            //System.out.println("Partition " + partition + ": " + sumX + "," + sumY + " " + x);
            return x;
        }).mapToDouble(x->x).min().getAsDouble();
        System.out.println(p);
    }
    public static ArrayList<ArrayList<Integer>> partition(int[] a, int position, int k) {
        if (k == 0) {
            ArrayList<ArrayList<Integer>> dank = new ArrayList<>();
            dank.add(new ArrayList<>(k));//ensure correct capacity
            return dank;
        }
        if (a.length - position < k) {
            return new ArrayList<>();
        }
        if (a.length - position == k) {
            ArrayList<Integer> res = new ArrayList<>(k);
            for (int i = position; i < a.length; i++) {
                res.add(a[i]);
            }
            ArrayList<ArrayList<Integer>> dank = new ArrayList<>();
            dank.add(res);
            return dank;
        }
        ArrayList<ArrayList<Integer>> includingCur = partition(a, position + 1, k - 1);
        for (ArrayList<Integer> x : includingCur) {
            x.add(0, a[position]);
        }
        ArrayList<ArrayList<Integer>> notIncludingCur = partition(a, position + 1, k);
        ArrayList<ArrayList<Integer>> result = includingCur;
        result.addAll(notIncludingCur);
        return result;
    }
}
