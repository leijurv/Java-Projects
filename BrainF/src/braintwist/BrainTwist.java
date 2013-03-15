package braintwist;

import java.io.*;
import java.util.*;

public class BrainTwist {

    static final byte EOF = 10;
    static final boolean ASCIIoutput = true;
    static final String rot13 = "-,+[-[                     >>++++[>++++++++<-]                             <+<-[             >+>+>-[>>>]     <[[>+<-]>>+>]    <<<<<-         ]                   ]>>>[-]+          >--[-[<->[-]]]<[        ++++++++++++<[     >-[>+>>]       >[+[<+>-]>+>>]   <<<<<-          ]                     >>[<+>-]           >[                  -[            -<<[-]>>     ]<<[<<->>-]>>   ]<<[<<+>>-]          ]                        <[-]                      <.[-]                 <-,+                 ] ";
    static final String countdown = ",[[->+>+<<]>>[-<<+>>]<<>-.]";
    static final String quine392 = "->++>+++>+>+>+++>>>>>>>>>>>>>>>>>>>>+>+>++>+++>++>>+++>+>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>+>+>>+++>>+++>>>>>+++>+>>>>>>>>>++>+++>+++>+>>+++>>>+++>+>++>+++>>>+>+>++>+++>+>+>>+++>>>>>>>+>+>>>+>+>++>+++>+++>+>>+++>>>+++>+>++>+++>++>>+>+>++>+++>+>+>>+++>>>>>+++>+>>>>>++>+++>+++>+>>+++>>>+++>+>+++>+>>+++>>+++>>++[[>>+[>]++>++[<]<-]>+[>]<+<+++[<]<+]>+[>]++++>++[[<++++++++++++++++>-]<+++++++++.<][Slight modification of Erik Bosman's ingenious 410-byte quine.Daniel B Cristofani (cristofdathevanetdotcom)http://www.hevanet.com/cristofd/braintwist/]";
    static final String squares = "++++[>+++++<-]>[<+++++>-]+<+[>[>+>+<<-]++>>[<<+>>-]>>>[-]++>[-]+ >>>+[[-]++++++>>>]<<<[[<++++++++<++>>-]+<.<[>----<-]<]<<[>>>>>[>>>[-]+++++++++<[>-<-]+++++++++>[-[<->-]+[<<<]]<[>+<-]>]<<-]<<-]";
    static final String fibbonacci = ">++++++++++>+>+[[+++++[>++++++++<-]>.<++++++[>--------<-]+<<<]>.>>[[-]<[>+<-]>>[<<+>+>-]<[>+<-[>+<-[>+<-[>+<-[>+<-[>+<-[>+<-[>+<-[>+<-[>[-]>+>+<<<-[>+<-]]]]]]]]]]]+>>>]<<<]";
    static final String myfibbonaccioverflowing = ".+>+<[-[->>+>+<<<]>>>[-<<<+>>>]<<<>><[->+>+<<]>>[-<<+>>]<<.+]";
    static final String thuemorse = ">>++++++[>++++++++<-]+[[>.[>]+<<[->-<<<]>[>+<<]>]>++<++]";
    static final String collatzfail = ">,[ [ ----------[  >>>[>>>>]+[[-]+<[->>>>++>>>>+[>>>>]++[->+<<<<<]]<<<] ++++++[>------<-]>--[>>[->>>>]+>+[<<<<]>-],< ]> ]>>>++>+>>[<<[>>>>[-]+++++++++<[>-<-]+++++++++>[-[<->-]+[<<<<]]<[>+<-]>] >[>[>>>>]+[[-]<[+[->>>>]>+<]>[<+>[<<<<]]+<<<<]>>>[->>>>]+>+[<<<<]]>[[>+>>[<<<<+>>>>-]>]<<<<[-]>[-<<<<]]>>>>>>>]>>+[[-]++++++>>>>]<<<<[[<++++++++>-]<.[-]<[-]<[-]<]<,]";
    static final String quine = ">++++++++>++++++>+>+++>++++++>++>+++++>++++>++++++>+>+++>++++>+++++>+>++++++>+++>+++>+++++>+++++++>+++>++++++>++++>++>++>++++++>++++>+++>+++>+>++>++>+++++>+++++>++>++++>++++++>+>++>++++>+++++>++>++++++>++++>+++>+>++++++>++++>+++>+>+++++>++++>+++>+>++++++++>++>++++>++++++>+>++>++++>+++++>+>+++++>+++>+>++>++++>+++++>+>+++>++++++>+>+++>++++++>++>+++++>++++>++++++>+>+++>++++>+++++>+>++++++>+++>+++>+++++>+++++++>+++>+++>++++++>++>++>+++++>++>++++++>++++>+++>+++++++>++>+++++>+++>+++>+++>+++++++>++>++>++>++>++++>++++++>+>++>++++>+++++>++>++++++>++++>++++++++>+++>+>++++++>++++>+++>+>+++++>++++>+++>+>++>++++>++++++>+>++>++++>+++++>+>+++++>+++>+>++>++++>+++++>+>+++>++++++>++>+++++>++++>++++++>+>+++>++++>+++++>+>++++++>+++>+++>+++++>+>+>+>+>+>+>+>+>+>+>++>++>+>++>++>+>+>+>++>++>+>++>++>++>++++++++>++>++>++>+>+>++>++>++++>++++>++>++>++++>+++>++++++>++++>++>++++++>+++>+++>+++++>+>+>+>++>++>+>+>+>+>+>+>++>++>+>+>+>+>+>+>++>++>+>+>+>++>++>+>+>+>+>++>++>+>+>+>+>++>++>+>+>+>++>+++++>++>++++++++>++++++>++++>+++>+>+>+>++>+++++>+>+>+>+>+>++>++>++>++++++>++>+++++>++++>++>++>++++++>+++>+++>++++++>++++>++>++++++>+++>+++++>+>+++>++++++>++>+++++>+++++>++++>++>++++++>+++>+++++>+>++++++>++>+++++>++>++++++>++++>+++>+++>+>++>+>++>+++++>+++++>+++>+++>+++>++++>++>++>++#>>>-<<<[[>+>+<<-]>[>]+[<]>-[[>]<+[<]>-]<<]>>-[>]#>>>+++++[>+++<-]>[>+++>>++++>>++++>>+++>>++++++>>++++++>>+++[<<]>-]<->>-->>++>>>>>>+>>+++>>+>>++++++++++[<<]+[-<+]-[>]<+#[->+<[+[->+]->+<-[+<-]+<-]>[->+]->>>>.<<<[>.<-]>[>>]<<.[<<]+[-<+]-[>]<+]<+#[->+<[+[->+]->+<-[+<-]+<-]>[->+]->[[>>+<<-]>>-]<.[<<]+[-<+]-[>]<+]";
    static final String multiply = "+++++first number is five>++++++second number is six<->[->+>+>+<<<]>>>[-<<<+>>>]<<<<[->>[-<+>]>[->+<<+>]>[-<+>]<<<<]>.";
    static final String bottlesofbeeronthewall = getFile("/Users/leif/Documents/beer.txt");
    static final String golden = getFile("/Users/leif/Documents/golden.txt");
    //static String prime=getFile("/Users/leif/Downloads/prime.b");
    static final String gameoflife = getFile("/Users/leif/Documents/gameoflife.b");
    static final String myquineepicfaildonteventry = " >++ a ton  >>++++++++[>++++>++++++<<-]>-[<++>-]>-----<<[->+>>+<<<]>>>[-<<<+>>>]<<-->[->+>+<<]>>[-<<+>>]<-- <[->>++>+>++<<<<]>>>[-<<<+>>>] >[<+>-] <+++++<+++++++ <++++[->>>+>+<<<<]>>>>[-<<<<+>>>>]<+ begin copy [<]>[[>>>>>>>>+>>>>>>>+<<<<<<<<<<<<<<<-]>]>>>>>>>>[[-<<<<<<<<<<<<<<<+>>>>>>>>>>>>>>>]>]<<<<<<<<  [<]<[<]<[<][[>]>[>]>[>]+ [<]<[<]<[<]-] [>]>[>]>[>]  -<[-]+[>]<[-<<<<<<<<-[+>-]+<[-]+[>]<]-[+<-]+<.";//0:62>,1:60<,2:43+,3:45-,4:93:],5:94:],6:46.
    static final String mymultiplyepidfaildonteventry = ">+++++>++++++   -<[->>+>+<<<] >>>[-<<<+>>>]    [[>]<<[->>+>+<<<]>>>[-<<<+>>>]<<<>><[->+>+<<]>>[-<<+>>]<  [<+>-]<  [<]>>-]    <<<<.>.>.>.>.>.";
    static final String derp="<[<]<<++++++++[->++++++++<]>--..[-]>>[<<++++++++[->++++++++<]>--.[-]>[-<+<+>>]<<[->>+<<]<+++++++[->++++++<]>+>[<.>-]<[-]>>-[>]>[>]+[<]<[<]>[[>]>[>]<+[<]<[<]>-]>]>[.>]";
    static final String myquinebase = ">>  >+++++>+++++>   <[<]++[<<++++++++[->++++++++<]>--.[-]>-]>[<<++++++++[->++++++++<]>--.[-]>[-<+<+>>]<<[->>+<<]<+++++++[->++++++<]>+>[<.>-]<[-]>>-[>]>[>]+[<]<[<]>[[>]>[>]<+[<]<[<]>-]>]>[.>]";
    static String c = ">>"+get()+derp;
    static ArrayList<Byte> mem = new ArrayList<Byte>();
    static int pointer = 0;
    static int loc = 0;
    static boolean a = false;
    static boolean done = false;
public static String get(){
    String result="";
    for (int i=0; i<derp.length(); i++){
        int x=(int)(byte)derp.charAt(i);
        result=result+">";
        for (int n=1; n<=x;n++){
            result=result+"+";
        }
    }
    return result;
}
    public static void next() {
        if (loc == c.length()) {
            done = true;
            return;
        }
        if (pointer >= mem.size()) {
            mem.add((byte) 0);
        }
        while (pointer < 0) {
            mem.add(0, (byte) 0);
            pointer++;
        }
        String cur = c.substring(loc, loc + 1);
        if (cur.equals("+")) {
            mem.set(pointer, (byte) (mem.get(pointer) + 1));
        }
        if (cur.equals("-")) {
            mem.set(pointer, (byte) (mem.get(pointer) - 1));
        }
        if (cur.equals(">")) {
            pointer++;
        }
        if (cur.equals("<")) {
            pointer--;
        }
        if (cur.equals("[")) {
            if (mem.get(pointer) == 0) {
                int alr = 1;
                loc++;
                while (alr != 0) {
                    String cc = c.substring(loc, loc + 1);
                    if (cc.equals("[")) {
                        alr++;
                    }
                    if (cc.equals("]")) {
                        alr--;
                    }
                    loc++;
                }
                return;
            }
        }
        if (cur.equals("]")) {
            if (mem.get(pointer) != 0) {
                int alr = -1;
                loc--;
                while (alr != 0) {
                    String cc = c.substring(loc, loc + 1);
                    if (cc.equals("[")) {
                        alr++;
                    }
                    if (cc.equals("]")) {
                        alr--;
                    }
                    loc--;
                }
                loc += 2;
                return;
            }
        }
        if (cur.equals(",")) {
            Scanner scan = new Scanner(System.in);
            System.out.print("Input required. >");
            String s = scan.nextLine();
            byte[] b = {};
            try {
                b = s.getBytes("US-ASCII");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (b.length == 0) {
                mem.set(pointer, EOF);//EOF
            } else {
                mem.set(pointer, b[0]);
            }
        }
        if (cur.equals(".")) {
            if (!ASCIIoutput) {
                System.out.println(((int) (byte) mem.get(pointer)));
            } else {
                System.out.print(((char) (byte) mem.get(pointer)));
            }
            output = output + (char) (byte) mem.get(pointer);
        }
        loc++;
    }
    static String output = "";

    public static void run() {
        while (!done) {
            next();
        }
    }

    public static void main(String[] args) {
        long t = System.currentTimeMillis();
        run();
        System.out.println();
        /*
         String x="<[<]++[<<++++++++[->++++++++<]>--.[-]>-]>[<<++++++++[->++++++++<]>--.[-]>[-<+<+>>]<<[->>+<<]<+++++++[->++++++<]>+>[<.>-]<[-]>>-[>]>[>]+[<]<[<]>[[>]>[>]<+[<]<[<]>-]>]>[.>]";
         for (int i=0; i<x.length(); i++){
         System.out.print(">");
         for (int n=0; n<(byte)x.charAt(i); n++){
         System.out.print("+");
         }
         }*/
        //System.out.println(System.currentTimeMillis()-t);
        System.out.println(c);
        System.out.println(c.equals(output));
    }

    public static String getFile(String filename) {
        ArrayList<String> a = read(filename);
        String r = "";
        for (String aa : a) {
            r = r + aa;
        }
        return r;
    }

    public static ArrayList<String> read(String filename) {
        ArrayList<String> a = new ArrayList<String>();
        try {
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                a.add(strLine);
            }
            //Close the input stream
            in.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return a;
    }
}
