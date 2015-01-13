/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author leijurv
 */
public abstract class Expression extends Command{
    @Override
    public boolean execute(Context c){
        evaluate(c);
        return false;
    }
    public abstract Object evaluate(Context c);
    private static final String[] let={"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","y","z"};
    private static boolean let(Object o){//Does o consist of letters?
        if (o instanceof String){
            String r=(String) o;
            if (r.length()>1){
                for (int i=0; i<r.length(); i++){
                    if (!let(r.substring(i,i+1))){
                        return false;
                    }
                }
                return true;
            }
            for (String s:let){
                if (((String) o).equals(s)){
                    return true;
                }
            }
        }
        return false;
    }
    private static boolean num(Object o){//Is o a number?
        if ((o instanceof ExpressionConstant)){
            return true;
        }
        if (o instanceof String){
            try{
                Integer.parseInt((String) o);//Le lazy
                return true;
            }catch (NumberFormatException e){
            }
        }
        return false;
    }
    private static boolean eq(Object o){
        if (o instanceof String){
            return (((String) o).replace("!","").length()==0)||(((String) o).replace("=","").length()==0)||(((String) o).replace("|","").length()==0)||(((String) o).replace("&","").length()==0);
        }
        return false;
    }
    public static ArrayList<Object> lex(String s){
        Object[] objects=new Object[s.length()];
        for (int i=0; i<objects.length; i++){
            objects[i]=s.substring(i,i+1);
        }
        int numItemsRemoved=0;
        for (int i=0; i<objects.length; i++){
            if (objects[i].equals(" ")){
                objects[i]=null;
                numItemsRemoved++;
            }
        }
        for (int i=0; i<objects.length-1; i++){
            if (objects[i]!=null&&objects[i+1]!=null&&eq(objects[i])&&eq(objects[i+1])){
                objects[i+1]=(String) objects[i]+(String) objects[i+1];
                objects[i]=null;
                numItemsRemoved++;
                i=Math.max(i-2,0);
            }
            if (objects[i]!=null&&objects[i+1]!=null&&let(objects[i])&&let(objects[i+1])){
                objects[i+1]=(String) objects[i]+(String) objects[i+1];
                objects[i]=null;
                numItemsRemoved++;
                i=Math.max(i-2,0);
            }
            if (objects[i]!=null&&objects[i+1]!=null&&num(objects[i])&&num(objects[i+1])){
                objects[i+1]=(String) objects[i]+(String) objects[i+1];
                objects[i]=null;
                numItemsRemoved++;
                i=Math.max(i-2,0);
            }
        }
        for (int i=0; i<objects.length-1; i++){
            if (objects[i]==null&&objects[i+1]!=null){
                objects[i]=objects[i+1];
                objects[i+1]=null;
                i-=2;
                if (i==-2){
                    i++;
                }
            }
        }
        Object[] N=new Object[objects.length-numItemsRemoved];
        System.arraycopy(objects,0,N,0,N.length);
        return new ArrayList(Arrays.asList(N));
        //Grouping together sequences of letters and numbers. e.g. 4,5,+,j,r,*,x becomes 45,+,jr,*,x
        //Note: x does not group it says by itself
    }
    private static Expression Do(Object[] o,int i){//Evaluate the operator at i in o, 
        //replace a*b with the result, then evaluate that recursively
        Object[] N=new Object[3];
        for (int m=i-1; m<i+2; m++){
            N[m-(i-1)]=o[m];
        }
        Object[] result=new Object[o.length-2];
        System.arraycopy(o,0,result,0,i-1);
        System.arraycopy(o,i+2,result,i,o.length-(i+2));
        result[i-1]=parse(N);
        return parse(result);
    }
    public static Expression parse(Object[] o){//I TOTALLY didn't copy this from my derivative project
        System.out.print("Parsing expression ");
        for (Object k:o){
            System.out.print(k+"       ");
        }
        System.out.println();
        if (o.length>2&&o[1] instanceof String&&((String) o[1]).equals("=")){//must be setting a variable
            Object[] Cool=new Object[o.length-2];
            for (int i=0; i<Cool.length; i++){
                Cool[i]=o[i+2];
            }
            return new ExpressionSetVariable((String) o[0],parse(Cool));
        }
        if (o.length==1){
            if (o[0] instanceof Expression){
                return (Expression) o[0];
            }
            String s=(String) o[0];
            try{
                int r=Integer.parseInt(s);
                return new ExpressionConstant(r);
            }catch (NumberFormatException e){
                return new ExpressionGetVariable(s);
            }
        }
        //System.out.println(o.length);
        if (o.length==3){
            Expression First=parse(new Object[]{o[0]});
            String s=(String) o[1];
            Expression Last=parse(new Object[]{o[2]});
            return new ExpressionOperator(First,s,Last);
        }
        //Parenthesis
        for (int i=0; i<o.length; i++){
            if (o[i].equals("(")){
                int n=1;
                int j;
                for (j=i+1; j<o.length&&n>0; j++){
                    if (o[j].equals("(")){
                        n++;
                    }
                    if (o[j].equals(")")){
                        n--;
                    }
                }
                Object[] N=new Object[j-i-2];
                for (int m=i+1; m<j-1; m++){
                    N[m-i-1]=o[m];
                }
                Object[] Cool=new Object[o.length-(N.length+1)];
                System.arraycopy(o,0,Cool,0,i);
                System.arraycopy(o,j,Cool,i+1,o.length-j);
                Cool[i]=parse(N);
                return parse(Cool);
            }
        }
        //Functions
        for (int i=0; i<o.length; i++){
            if (o[i] instanceof String){
                String s=(String) o[i];
                if (let(s)&&o[i+1] instanceof Expression){
                    Object[] Cool=new Object[o.length-1];
                    System.arraycopy(o,0,Cool,0,i);
                    System.arraycopy(o,i+1,Cool,i,o.length-(i+1));
                    Expression f=parse(new Object[]{o[i+1]});
                    ArrayList<Expression> args=new ArrayList<>();
                    args.add(f);
                    Cool[i]=new ExpressionBeginChase(s,args);
                    return parse(Cool);
                }
            }
        }
        //Operators, using order of operations. Between * and /, 5*6/7 would be (5*6)/7, going left to right
        for (int i=0; i<o.length; i++){
            if (o[i].equals("%")){
                return Do(o,i);
            }
        }
        for (int i=0; i<o.length; i++){
            if (o[i].equals("^")){
                return Do(o,i);
            }
        }
        for (int i=0; i<o.length; i++){
            if (o[i].equals("*")||o[i].equals("/")){
                return Do(o,i);
            }
        }
        for (int i=0; i<o.length; i++){
            if (o[i].equals("+")||o[i].equals("-")){
                return Do(o,i);
            }
        }
        for (int i=0; i<o.length; i++){
            if (o[i].equals(">")||o[i].equals("<")||o[i].equals("==")||o[i].equals("!=")||o[i].equals("<=")||o[i].equals(">=")){
                return Do(o,i);
            }
        }
        for (int i=0; i<o.length; i++){
            if (o[i].equals("||")||o[i].equals("&&")){
                return Do(o,i);
            }
        }
        System.out.print("ERROR WHILE PARSING");
        for (Object k:o){
            System.out.print(k);
        }
        throw new RuntimeException("ERROR WHILE PARSING");
    }
}
