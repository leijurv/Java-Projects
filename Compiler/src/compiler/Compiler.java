/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 *
 * @author leijurv
 */
public class Compiler {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        String program="function main(abc){sum=0;i=0;while(i<1000){if(i%5==0||i%3==0){sum=sum+i}i=i+1};print(sum)}";
        String program1="function fac(r){if (r<1) {return 1} else {return r *fac(r-1)}}  function main(abc){ br=1+(ab=fac(abc+ 3)*(5-abc)); print(ab); print(abc); print(br); if ( br > ab ) { print(5)}else{print(6)}}";
        System.out.println("STARTING TO PARSE: "+program);
        System.out.println();
        ArrayList<Object> progra=parse(program);
        System.out.println(progra);
        Context c=new Context().subContext();
        for (Object o : progra){
            Command cc=(Command) o;
            cc.execute(c);
        }
        System.out.println();
        System.out.println();
        System.out.println("FINISHED PARSING");
        System.out.println("PROGRAM: "+c);
        System.out.println();
        ArrayList<Expression> arg=new ArrayList<>();
        //arg.add(new ExpressionConstant(4));
        new ExpressionFunctionCall("main",arg).evaluate(c);
    }
    public static ArrayList<Object> curlyBrackets(String temp){
        int firstBracket=-1;
        int numBrackets=0;
        for (int i=0; i<temp.length(); i++){
            if (temp.charAt(i)=='{'){
                numBrackets++;
                if (numBrackets==1){
                    firstBracket=i;
                }
            }
            if (temp.charAt(i)=='}'){
                numBrackets--;
                if (numBrackets==0){
                    String before=temp.substring(0,firstBracket);//no curly brackets
                    String during=temp.substring(firstBracket+1,i);//maybe curly brackets, needs to be parsed seprately
                    String after=temp.substring(i+1,temp.length());//maybe curly brackets
                    ArrayList<Object> result=new ArrayList<>();
                    result.add(before);
                    result.add(parse(during));
                    result.addAll(curlyBrackets(after));
                    return result;
                }
            }
        }
        ArrayList<Object> result=new ArrayList<>();
        result.add(temp);
        return result;
    }
    public static void semicolons(ArrayList<Object> temp){
        for (int i=0; i<temp.size(); i++){
            Object o=temp.get(i);
            if (o instanceof String){
                String s=(String) o;
                if (s.contains(";")){
                    int pos=s.indexOf(";");
                    String before=s.substring(0,pos);
                    String after=s.substring(pos+1,s.length());
                    temp.remove(i);
                    temp.add(i,before);
                    temp.add(i+1,after);
                    semicolons(temp);
                    return;
                }
            }
        }
    }
    public static void findBlocks(ArrayList<Object> temp){
        String[] keyWords={"function","if","while","return"};
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
                switch (key){
                    case 0:
                        String name=s.split("function ")[1].split("\\(")[0];
                        String arguments=s.split("\\(")[1];
                        arguments=arguments.substring(0,arguments.length()-1);// )
                        String[] args=arguments.split(",");
                        ArrayList<Object> following=(ArrayList) (temp.get(i+1));
                        Function func=new Function(new ArrayList<>(Arrays.asList(args)),following);
                        ExpressionSetVariable definition=new ExpressionSetVariable(name,func);
                        temp.remove(i+1);
                        temp.remove(i);
                        temp.add(i,definition);
                        findBlocks(temp);
                        break;
                    case 1:
                        if (s.charAt(3)==' '){
                            s="if"+s.substring(3,s.length());
                        }
                        Expression condition=parseExpression(s.substring(2,s.length()));
                        ArrayList<Object> ifTrue=(ArrayList) temp.get(i+1);
                        if (temp.size()>i+2&&temp.get(i+2) instanceof String&&"else".equals((String) (temp.get(i+2)))){
                            ArrayList<Object> ifFalse=(ArrayList) temp.get(i+3);
                            temp.remove(i+1);
                            temp.remove(i+1);
                            temp.remove(i+1);
                            temp.set(i,new CommandIf(condition,ifTrue,ifFalse));
                        }else{
                            temp.remove(i+1);
                            temp.set(i,new CommandIf(condition,ifTrue));
                        }
                        break;
                    case 2:
                        if (s.charAt(6)==' '){
                            s="while"+s.substring(6,s.length());
                        }
                        Expression cond=parseExpression(s.substring(5,s.length()));
                        ArrayList<Object> cont=(ArrayList) temp.get(i+1);
                        temp.remove(i+1);
                        temp.set(i,new CommandWhile(cont,cond));
                        break;
                    case 3:
                        String val=s.split("return ")[1];
                        temp.set(i,new CommandReturn(parseExpression(val)));
                }
            }
        }
    }
    public static void expressions(ArrayList<Object> temp){
        for (int i=0; i<temp.size(); i++){
            Object o=temp.get(i);
            if (o instanceof String){
                String s=(String) o;
                /*
                 int equals=s.indexOf("=");
                 if (equals!=-1){
                 String varname=s.substring(0,equals);
                 if (s.startsWith("var ")){
                 varname=s.substring(4,equals);
                 }
                 String val=s.substring(equals+1,s.length());
                 ExpressionSetVariable v=new ExpressionSetVariable(varname,parseExpression(val));
                 temp.set(i,v);
                 continue;
                 }*/
                //Well, must be an expression then
                temp.set(i,parseExpression(s));
            }
        }
    }
    public static Expression parseExpression(String p){
        return Expression.preparse(p);
    }
    public static void regularize(ArrayList<Object> temp){
        for (int i=0; i<temp.size(); i++){
            if (temp.get(i) instanceof String){
                String s=(String)temp.get(i);
                while(s.startsWith(" ")){
                    s=s.substring(1,s.length());
                }
                while(s.endsWith(" ")){
                    s=s.substring(0,s.length()-1);
                }
                temp.set(i,s);
            }
        }
        while (temp.contains("")){
            temp.remove("");
        }
    }
    public static ArrayList<Object> parse(String p){
        ArrayList<Object> temp=curlyBrackets(p);
        regularize(temp);
        semicolons(temp);
        regularize(temp);
        findBlocks(temp);
        regularize(temp);
        expressions(temp);
        return temp;
    }
}
