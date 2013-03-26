package ghost;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Ghost {

    static ArrayList<String> words = new ArrayList<String>();
    static char[] alpha = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    public static void load(String path) throws Exception {
        FileInputStream fstream = new FileInputStream(path);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        while ((strLine = br.readLine()) != null) {
            words.add(strLine);
        }
        in.close();
    }

    public static ArrayList<String> startsWith(String start) {
        ArrayList<String> result = new ArrayList<String>();
        for (String S : words) {
            if (S.startsWith(start) && S.length() > 3) {
                result.add(S);
            }
        }
        return result;
    }

    public static int[] solve(String sofar) {
        int M = sofar.length() % 2 == 0 ? 1 : -1;
        if ((words.contains(sofar) && sofar.length() > 3) || startsWith(sofar).isEmpty()) {
            return new int[]{M, -1};
        }
        if (sofar.length() < 5) {
            System.out.println(sofar);
        }
        for (int i = 0; i < alpha.length; i++) {
            int[] q = solve(sofar + alpha[i]);
            if (q[0] == M) {
                return new int[]{q[0], i};
            }
        }
        return new int[]{-M, -1};

    }

    public static void main(String[] args) {
        try {
            load("/Users/leijurv/Dropbox/Java-Projects/Ghost/Ghostwords.txt");
            //Change to "/Users/USERNAME/Downloads/Java-Projects-master/Ghost/Ghostwords.txt" or whereever you downloaded it to.
        } catch (Exception e) {
            System.out.println("There was an error. Maybe you didn't change the path on line 56?");
            return;
        }
        Scanner scan = new Scanner(System.in);
        System.out.print("What is it so far? >");
        String sofar = scan.nextLine();
        ArrayList<String> s = startsWith(sofar);
        if (s.isEmpty()) {
            System.out.println("No words start with that.");
            return;
        }
        System.out.println("Searching...");
        int[] x = solve(sofar);
        System.out.println(s.size() + " words start with " + sofar + ": " + s);
        System.out.println(x[0] == 1 ? "First player wins" : "Second player wins");
        System.out.println(x[1] == -1 ? "Any move results in other player winning" : ("Optimal move is " + alpha[x[1]]));

    }
}
