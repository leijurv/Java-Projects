/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
/**
 *
 * @author leijurv
 */
public class Compiler {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{
        String program2="chase main(abc){sum=0;i=0;purr(i<1000){if(i%5==0||i%3==0){sum=sum+i}i=i+1};meow(sum)}";
        String program="chase main(abc){i=sum=0;purr(i<1000){if(i%5!=0){if(i%3==0){sum=sum+i}}else{sum=sum+i};i=i+1};meow(sum)}";
        String program1="chase fac(r){if (r>=1) {pounce r *fac(r-1);} else {pounce (1);}}  chase main(abc){ br=1+(ab=fac(abc+ 3)*(5-abc)); meow(ab); meow(abc); meow(br); if ( br < ab ) { meow(5);}else{meow(6)};me=meow(br);meow(me)}";
        System.out.println("STARTING TO PARSE: "+program);
        System.out.println();
        long time=System.currentTimeMillis();
        ArrayList<Command> prograaa=toCommandList(parse(program));
        System.out.println();
        System.out.println("Parsed program: "+prograaa);
        System.out.println("Took "+(System.currentTimeMillis()-time)+"ms");
        System.out.println();
        System.out.println();
        System.out.println("Compiling...");
        System.out.println();
        System.out.println();
        byte[] compiled=compile(prograaa);
        runProgram(compiled);
    }
    public static byte[] compile(ArrayList<Command> program) throws IOException{
        long time=System.currentTimeMillis();
        ByteArrayOutputStream ou=new ByteArrayOutputStream();
        Command.writemultiple(new DataOutputStream(ou),program);
        byte[] compiled=ou.toByteArray();
        System.out.println();
        System.out.println();
        System.out.println("Done compiling. Took "+(System.currentTimeMillis()-time)+"ms");
        System.out.println("Compiled program: "+new String(compiled));
        return compiled;
    }
    public static void runProgram(byte[] compiled) throws IOException{
        System.out.println();
        System.out.println("Pre-reading program...");
        long time=System.currentTimeMillis();
        ByteArrayInputStream in=new ByteArrayInputStream(compiled);
        ArrayList<Command> progra=Command.readmultiple(new DataInputStream(in));
        System.out.println();
        System.out.println("Done pre-reading. Took "+(System.currentTimeMillis()-time)+"ms");
        System.out.println("Reading program...");
        time=System.currentTimeMillis();
        Context c=new Context();
        for (Command cc : progra){
            cc.execute(c);
        }
        System.out.println();
        System.out.println();
        System.out.println("Done reading. Took "+(System.currentTimeMillis()-time)+"ms");
        System.out.println("PROGRAM: "+c);
        System.out.println();
        ArrayList<Expression> prey=new ArrayList<>();
        prey.add(new ExpressionConstant(4));
        System.out.println("Running program with prey "+prey);
        time=System.currentTimeMillis();
        new ExpressionBeginChase("main",prey).evaluate(c);
        System.out.println("Running took "+(System.currentTimeMillis()-time)+"ms");
    }
    public static ArrayList<Object> curlyBrackets(ArrayList<Object> temp){
        int firstBracket=-1;
        int numBrackets=0;
        for (int i=0; i<temp.size(); i++){
            if (temp.get(i).equals("{")){
                numBrackets++;
                if (numBrackets==1){
                    firstBracket=i;
                }
            }
            if (temp.get(i).equals("}")){
                numBrackets--;
                if (numBrackets==0){
                    //System.out.println("aoeuaoeu"+temp);
                    ArrayList<Object> before=new ArrayList<>(temp.subList(0,firstBracket));//no curly brackets
                    ArrayList<Object> during=new ArrayList<>(temp.subList(firstBracket+1,i));//maybe curly brackets, needs to be parsed seprately
                    ArrayList<Object> after=new ArrayList<>(temp.subList(i+1,temp.size()));//maybe curly brackets
                    //System.out.println("aoeuaoeu1"+before);
                    //System.out.println("aoeuaoeu2"+during);
                    //System.out.println("aoeuaoeu3"+after);
                    ArrayList<Object> result=new ArrayList<>();
                    result.addAll(before);
                    result.add(postparse(during));
                    result.addAll(curlyBrackets(after));
                    //System.out.println("aoeuaoeu4"+result);
                    return result;
                }
            }
        }
        return temp;
    }
    public static void findBlocks(ArrayList<Object> temp){
        String[] keyWords={"chase","if","purr","pounce"};
        for (int i=0; i<temp.size(); i++){
            Object o=temp.get(i);
            if (o instanceof String){
                String s=(String) o;
                int key=-1;
                for (int j=0; j<keyWords.length; j++){
                    if (s.startsWith(keyWords[j])){
                        key=j;
                    }
                }
                if (key==-1){
                    continue;
                }
                System.out.println("Found "+keyWords[key]+" block");
                int numParen=0;
                ArrayList<String> paren=new ArrayList<>();
                boolean b=false;//Has reached first ( yet
                for (int j=i+1; j<temp.size();){
                    if (temp.get(j) instanceof String){
                        String a=(String) (temp.get(j));
                        if (a.equals("(")){
                            numParen+=1;
                            b=true;
                        }
                        if (a.equals(")")){
                            numParen--;
                            b=true;
                        }
                        if (b||key==3){
                            temp.remove(j);
                            paren.add(a);
                        }else{
                            j++;
                        }
                        if (numParen==0&&b){
                            break;
                        }
                    }else{
                        throw new RuntimeException("Non string");
                    }
                }
                //System.out.println(paren);
                //System.out.println(temp);
                Expression inParen=null;
                if (key!=0){
                    inParen=Expression.parse(paren.toArray());
                }
                switch (key){
                    case 0:
                        paren.remove(0);
                        paren.remove(paren.size()-1);//Remove parentheses around prey list
                        while (paren.contains(",")){
                            paren.remove(",");
                        }
                        String name=(String) temp.get(i+1);
                        ArrayList<Object> following=(ArrayList) (temp.get(i+2));
                        Chase func=new Chase(paren,toCommandList(following));
                        ExpressionSetVariable define=new ExpressionSetVariable(name,func);
                        temp.remove(i+2);
                        temp.remove(i+1);
                        temp.set(i,define);
                        findBlocks(temp);
                        break;
                    case 1:
                        ArrayList<Object> ifTrue=(ArrayList) temp.get(i+1);
                        if (temp.size()>i+2&&temp.get(i+2) instanceof String&&"else".equals((String) (temp.get(i+2)))){
                            ArrayList<Object> ifFalse=(ArrayList) temp.get(i+3);
                            temp.remove(i+1);
                            temp.remove(i+1);
                            temp.remove(i+1);
                            temp.set(i,new CommandBlink(inParen,toCommandList(ifTrue),toCommandList(ifFalse)));
                        }else{
                            temp.remove(i+1);
                            temp.set(i,new CommandBlink(inParen,toCommandList(ifTrue)));
                        }
                        break;
                    case 2:
                        ArrayList<Object> cont=(ArrayList) temp.get(i+1);
                        temp.remove(i+1);
                        temp.set(i,new CommandPurr(toCommandList(cont),inParen));
                        break;
                    case 3:
                        temp.set(i,new CommandPounce(inParen));
                }
            }
        }
    }
    public static ArrayList<Command> toCommandList(ArrayList<Object> temp){
        ArrayList<Command> res=new ArrayList<>(temp.size());
        for (Object temp1 : temp){
            res.add((Command) (temp1));
        }
        return res;
    }
    public static void expressions(ArrayList<Object> temp){
        ArrayList<Object> t=new ArrayList<>();
        for (int i=0; i<temp.size(); i++){
            Object o=temp.get(i);
            if (o instanceof String){
                String s=(String) o;
                temp.remove(i--);
                if (!s.equals(";")){
                    t.add(s);//add to t unless its a ;
                    continue;//skip the rest
                }
                //at this point, its just hit a ;
                if (!t.isEmpty()){//but not if t is empty and not an expression
                    i++;//set i to expession AFTER where ; was, because thats where we want to insert the expression
                }
            }
            if (!t.isEmpty()){
                temp.add(i,"**EXPRESSION LOCATION**");
                System.out.println("Found expression "+t+" within "+temp);
                temp.set(i,Expression.parse(t.toArray()));
                t=new ArrayList();
            }
        }
        if (!t.isEmpty()){
            temp.add(Expression.parse(t.toArray()));//add to end
        }
    }
    public static ArrayList<Object> parse(String p){
        ArrayList<Object> temp=Expression.lex(p);
        System.out.println("Lexed "+temp);
        return postparse(temp);
    }
    public static ArrayList<Object> postparse(ArrayList<Object> temp){
        System.out.println("Starting to find curly brackets in"+temp);
        temp=curlyBrackets(temp);
        System.out.println("Starting to find blocks in"+temp);
        findBlocks(temp);
        System.out.println("Starting to find expressions in"+temp);
        expressions(temp);
        return temp;
    }
}
