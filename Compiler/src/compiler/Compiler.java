/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;
import java.io.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;
/**
 *
 * @author leijurv
 */
public class Compiler {
    static final boolean verbose = false;
    static final String programFloatingPointTesting = "chase main ( kush ) { meow ( 5 + 5^2 );blink(5.2^2>27){meow(55+1)}a=(kush>=4);meow(a);a=a||false;a=a&&true;meow(a)blink(a){meow(55)}else{meow(66)} blink(true){meow(1)};meow(5.0==5)} ";
    static final String programArrayTesting = "chase main(){a=[1,3,[5],6,67];meow(a[1]);meow(a[2]);meow(a[2][0]);meow(a[1]);a[1]=[4,5];meow(a[1][0]);a[1][0]=7;meow(a[1][0]);meow([5,[6]][1][0]);meow(len(array(100)));}";
    static final String programEuler1 = "chase main(limit){\n"
            + "  sum=0;\n"
            + "  i=0;\n"
            + "  purr(i<limit){\n"
            + "    blink(i%5==0||i%3==0){\n"
            + "      sum=sum+i\n"
            + "    }\n"
            + "    i=i+1\n"
            + "  };\n"
            + "  meow(sum)\n"
            + "}";
    static final String programOtherEuler1 = "chase main(){i=sum=0;purr(i<1000){blink(i%5!=0){blink(i%3==0){sum=sum+i}}else{sum=sum+i};i=i+1};meow(sum)}";
    static final String programChaseTesting = "chase fac(r){blink (r>=1) {pounce r *fac(r-1);} else {pounce (1);}}  chase main(abc){ br=1+(ab=fac(abc+ 3)*(5-abc)); meow(ab); meow(abc); meow(br); blink ( br < ab ) { meow(5);}else{meow(6)};me=meow(br);meow(me)}";
    static final String programMapTest = "chase map(f,a){i=0;l=len(a);r=array(l);purr(i<l){r[i]=f(a[i]);i=i+1};pounce r} chase fac(r){blink (r>=1) {pounce r *fac(r-1);} else {pounce (1);}} chase main(r){a=[4,5,6];b=map(fac,a);meow(b);meow(b+1);}";
    static final String programSettingTesting = "chase main(abc){x=[5,6];[x[0],x[1]]=[x[1],x];meow(x);[a,b]=[1,2];[b,a]=[a,b];meow([a,b]);meow(1==[a,b]);meow(1==[a,b]-1);c=[5,6];c[(c=1)]=5;meow(c)}";
    static final String programFibbonacciTest = "chase main(abc){\n"
            + "  prev=1;\n"
            + "  current=1;\n"
            + "  purr(current<abc){\n"
            + "    meow(current);\n"
            + "    [prev,current]=[current,prev+current];\n"
            + "  }\n}";
    static final String programReverseTest = "chase reverse(a){[i,l]=[0,len(a)];r=array(l);purr(i<l){r[i]=a[l-i-1];i=i+1}pounce r} chase main(abc){x=[1,2,3,4];meow(x);meow(reverse(x))}";
    static final String programReverseMapTest = "chase reverse(a){[i,l]=[0,len(a)];r=array(l);purr(i<l){r[i]=a[l-i-1];i=i+1}pounce r} chase map(f,a){i=0;l=len(a);r=array(l);purr(i<l){r[i]=f(a[i]);i=i+1};pounce r} chase main(abc){a=[[1,2],[4,7,5]];meow(map(reverse,a))}";
    static final String programJenTest = "chase main(abc){x=[5,6];y=x;x[0]=3;meow(x);meow(y)}";
    static final String[] examples = {programFibbonacciTest, programEuler1, programFloatingPointTesting, programArrayTesting, programOtherEuler1, programChaseTesting, programMapTest, programSettingTesting, programReverseTest, programReverseMapTest, programJenTest};
    static final String[] exampleNames = {"Fibbonacci numbers up to input", "Project Euler #1", "Floating point testing", "Array testing", "Other way of Project Euler #1", "Chase testing", "Map testing", "Setting testing", "Reverse testing", "Reverse Map testing", "Jen testing"};
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main1(String[] args) throws IOException {
        String program = programEuler1;
        println("STARTING TO PARSE: " + program);
        println();
        long time = System.currentTimeMillis();
        ArrayList<Command> prograaa = toCommandList(parse(program));
        println();
        println("Parsed program: " + prograaa);
        println("Took " + (System.currentTimeMillis() - time) + "ms");
        println();
        println();
        println("Compiling...");
        println();
        println();
        byte[] compiled = compile(prograaa);
        runProgram(compiled);
        //run(prograaa);
    }
    public static void main(String[] args) {
        Gooey.setup();
    }
    public static byte[] compile(ArrayList<Command> program) throws IOException {
        long time = System.currentTimeMillis();
        ByteArrayOutputStream ou = new ByteArrayOutputStream();
        Command.writemultiple(new DataOutputStream(ou), program);
        byte[] compiled = ou.toByteArray();
        println();
        println();
        println("Done compiling. Took " + (System.currentTimeMillis() - time) + "ms");
        println("Compiled program: " + new String(compiled));
        return compiled;
    }
    public static void runProgram(byte[] compiled) throws IOException {
        println();
        println("Pre-reading program...");
        long time = System.currentTimeMillis();
        ByteArrayInputStream in = new ByteArrayInputStream(compiled);
        ArrayList<Command> progra = Command.readmultiple(new DataInputStream(in));
        println();
        println("Done pre-reading. Took " + (System.currentTimeMillis() - time) + "ms");
        run(progra);
    }
    public static void run(ArrayList<Command> progra) {
        println("Reading program...");
        long time = System.currentTimeMillis();
        Context c = new Context();
        for (Command cc : progra) {
            cc.execute(c);
        }
        println();
        println();
        println("Done reading. Took " + (System.currentTimeMillis() - time) + "ms");
        println("PROGRAM: " + c);
        println();
        ArrayList<Expression> prey = new ArrayList<>();
        Chase main = (Chase) c.get("main");
        if (main.getNumPrey() != 0) {
            String pr = JOptionPane.showInputDialog("What should the input to the main function be?");
            prey.add(new ExpressionConstant(Integer.parseInt(pr)));
        }
        ExpressionBeginChase toCall = new ExpressionBeginChase("main", prey);
        println("Running program with prey " + prey);
        time = System.currentTimeMillis();
        toCall.evaluate(c);
        println("Running took " + (System.currentTimeMillis() - time) + "ms");
    }
    public static ArrayList<Object> curlyBrackets(ArrayList<Object> temp) {
        int firstBracket = -1;
        int numBrackets = 0;
        for (int i = 0; i < temp.size(); i++) {
            if (temp.get(i).equals("{")) {
                numBrackets++;
                if (numBrackets == 1) {
                    firstBracket = i;
                }
            }
            if (temp.get(i).equals("}")) {
                numBrackets--;
                if (numBrackets == 0) {
                    //println("aoeuaoeu"+temp);
                    ArrayList<Object> before = new ArrayList<>(temp.subList(0, firstBracket));//no curly brackets
                    ArrayList<Object> during = new ArrayList<>(temp.subList(firstBracket + 1, i));//maybe curly brackets, needs to be parsed seprately
                    ArrayList<Object> after = new ArrayList<>(temp.subList(i + 1, temp.size()));//maybe curly brackets
                    //println("aoeuaoeu1"+before);
                    //println("aoeuaoeu2"+during);
                    //println("aoeuaoeu3"+after);
                    ArrayList<Object> result = new ArrayList<>();
                    result.addAll(before);
                    result.add(postparse(during));
                    result.addAll(curlyBrackets(after));
                    //println("aoeuaoeu4"+result);
                    return result;
                }
            }
        }
        if (numBrackets == 0) {
            return temp;
        }
        if (numBrackets > 0) {
            throw new IllegalStateException("Not enough } in " + temp);
        } else {
            throw new IllegalStateException("Too many } in " + temp);
        }
    }
    private static final String[] keyWords = {"chase", "blink", "purr", "pounce"};
    public static void findBlocks(ArrayList<Object> temp) {
        for (int i = 0; i < temp.size(); i++) {
            Object o = temp.get(i);
            if (o instanceof String) {
                String s = (String) o;
                int key = -1;
                for (int j = 0; j < keyWords.length; j++) {
                    if (s.startsWith(keyWords[j])) {
                        key = j;
                        break;
                    }
                }
                if (key == -1) {
                    continue;
                }
                println("Found " + keyWords[key] + " block");
                int numParen = 0;
                ArrayList<String> paren = new ArrayList<>();
                boolean b = false;//Has reached first ( yet
                for (int j = i + 1; j < temp.size();) {
                    if (temp.get(j) instanceof String) {
                        String a = (String) (temp.get(j));
                        if (a.equals("(")) {
                            numParen++;
                            b = true;
                        }
                        if (a.equals(")")) {
                            numParen--;
                            b = true;
                        }
                        if (b || key == 3) {//Key #3, pounces, doesn't need parentheses, while all the others do
                            temp.remove(j);
                            paren.add(a);
                        } else {
                            j++;
                        }
                        if (numParen == 0 && b) {//Reached end of parentheses
                            break;
                        }
                    } else {
                        throw new RuntimeException("Non string");
                    }
                }
                //println(paren);
                //println(temp);
                Expression inParen = null;
                if (key != 0) {//If key#0, chase, paren is the list of prey names, which is not an expression
                    inParen = Expression.parse(paren.toArray());
                }
                switch (key) {
                    case 0:
                        paren.remove(0);
                        paren.remove(paren.size() - 1);//Remove parentheses around prey list
                        while (paren.contains(",")) {
                            paren.remove(",");
                        }
                        String name = (String) temp.get(i + 1);
                        ArrayList<Command> following = checkIsCommandList(temp.get(i + 2), "Trying to get contents of function " + name + ".");
                        Chase func = new Chase(paren, following);
                        ExpressionSetVariable define = new ExpressionSetVariable(name, func);
                        temp.remove(i + 2);
                        temp.remove(i + 1);
                        temp.set(i, define);
                        findBlocks(temp);
                        break;
                    case 1:
                        ArrayList<Command> ifTrue = checkIsCommandList(temp.get(i + 1), "Trying to get contents of if statement.");
                        if (temp.size() > i + 2 && temp.get(i + 2) instanceof String && "else".equals((String) (temp.get(i + 2)))) {
                            ArrayList<Command> ifFalse = checkIsCommandList(temp.get(i + 3), "Trynig to get contents of else.");
                            temp.remove(i + 1);
                            temp.remove(i + 1);
                            temp.remove(i + 1);
                            temp.set(i, new CommandBlink(inParen, ifTrue, ifFalse));
                        } else {
                            temp.remove(i + 1);
                            temp.set(i, new CommandBlink(inParen, ifTrue));
                        }
                        break;
                    case 2:
                        ArrayList<Command> cont = checkIsCommandList(temp.get(i + 1), "Trying to get contents of while loop.");
                        temp.remove(i + 1);
                        temp.set(i, new CommandPurr(cont, inParen));
                        break;
                    case 3:
                        temp.set(i, new CommandPounce(inParen));
                        break;
                    default:
                        throw new IllegalStateException("This should never happen");
                }
            }
        }
    }
    public static ArrayList<Command> toCommandList(ArrayList<Object> temp) {
        return toCommandList(temp, "^unknown state^");
    }
    public static ArrayList<Command> toCommandList(ArrayList<Object> temp, String failMessage) {
        ArrayList<Command> res = new ArrayList<>(temp.size());
        for (Object temp1 : temp) {
            if (temp1 instanceof Command) {
                res.add((Command) (temp1));
            } else {
                throw new ClassCastException("Expected " + temp1 + " to be a command. " + failMessage);
            }
        }
        return res;
    }
    public static void expressions(ArrayList<Object> temp) {
        ArrayList<Object> t = new ArrayList<>();
        for (int i = 0; i < temp.size(); i++) {
            Object o = temp.get(i);
            if (o instanceof String) {
                String s = (String) o;
                temp.remove(i--);
                if (!s.equals(";")) {
                    t.add(s);//add to t unless its a ;
                    continue;//skip the rest
                }
                //at this point, its just hit a ;
                if (!t.isEmpty()) {//but not if t is empty and not an expression
                    i++;//set i to expession AFTER where ; was, because thats where we want to insert the expression
                }
            }
            if (!t.isEmpty()) {
                temp.add(i, "**EXPRESSION LOCATION**");
                println("Found expression " + t + " within " + temp);
                temp.set(i, Expression.parse(t.toArray()));
                t = new ArrayList<>();
            }
        }
        if (!t.isEmpty()) {//Finished going through, ends with an expression
            temp.add("**EXPRESSION LOCATION**");//Add to end
            println("Found expression " + t + " within " + temp);
            temp.set(temp.size() - 1, Expression.parse(t.toArray()));//Replace end
        }
    }
    public static ArrayList<Object> parse(String p) {
        ArrayList<Object> temp = Expression.lex(p);
        println("Lexed " + temp);
        return postparse(temp);
    }
    public static ArrayList<Object> postparse(ArrayList<Object> temp) {
        println("Starting to find curly brackets in" + temp);
        temp = curlyBrackets(temp);
        println("Starting to find blocks in" + temp);
        findBlocks(temp);
        println("Starting to find expressions in" + temp);
        expressions(temp);
        return temp;
    }
    @SuppressWarnings("unchecked")//It *is* checked, but netbeans just doesn't realize that
    public static ArrayList<Object> checkIsArrayList(Object o, String message, String type) {
        if (!(o instanceof ArrayList)) {
            throw new IllegalStateException("Expected " + o + " to be an instance of ArrayList<" + type + ">. " + message);
        }
        return (ArrayList<Object>) o;
    }
    public static ArrayList<Command> checkIsCommandList(Object o, String message) {
        return checkIsCommandList(o, message, message);
    }
    public static ArrayList<Command> checkIsCommandList(Object o, String messageIfNotArrayList, String messageIfNotCommandList) {
        return toCommandList(checkIsArrayList(o, messageIfNotArrayList, "Command"), messageIfNotCommandList);
    }
    public static void println() {
        Gooey.println();
    }
    public static void println(String m) {
        Gooey.println(m);
    }
}
