package javasim;
/**
 * A parentheses handler for a Predicate 
 * 
 * Used in If, While, and Until
 * @author leif
 */
public class BooleanParenthesisHandler extends ParenthesisHandler{
    /**
     * The index of which class it is in.
     */
    int classID;
    /**
     * The index of which function it is in, in that class.
     */
    int funcID;
    public BooleanParenthesisHandler(int ClassID, int FuncID){
        super();
        classID=ClassID;
        funcID=FuncID;
    }
    /**
     * Evaluates a predicate with no parentheses
     * 
     * Simply calls evalorandpred(a)
     * @param a The predicate to be evaluated. Can't have parentheses.
     * @return Returns 0 if false, 1 if true
     */
    @Override
    public int evalnopar(String a){
        return evalorandpred(a);
    }
    /**
     * Evaluates a predicate with no parentheses, "||", or "&&"
     * 
     * Supports ==,>,<,<=,>=
     * @param a The predicate to be evaluated. Can't have parentheses or || or &&
     * 
     * @return Returns 0 if false, 1 if true
     */
    public int evalsinglepred(String a){
        //1=true
        //0=false
        if (a.equals("true")){
            return 1;
        }
        if (a.equals("false")){
            return 0;
        }
        //Evaluate predicate. (==, >, <, <=, >=). Return value. Each part should use a ExpressionParenthesisHandler or Expression datatype.
        if (a.indexOf(">=")!=-1){
            int[] t=evalS(a,">=");
            int firstI=t[0];
            int secondI=t[1];
            boolean result=(firstI>=secondI);
            if (result){
                return 1;
            }
            return 0;
        }
        if (a.indexOf("<=")!=-1){
            int[] t=evalS(a,"<=");
            int firstI=t[0];
            int secondI=t[1];
            boolean result=(firstI<=secondI);
            if (result){
                return 1;
            }
            return 0;
        }
        if (a.indexOf(">")!=-1){
            int[] t=evalS(a,">");
            int firstI=t[0];
            int secondI=t[1];
            boolean result=(firstI>secondI);
            if (result){
                return 1;
            }
            return 0;
        }
        if (a.indexOf("<")!=-1){
            int[] t=evalS(a,"<");
            int firstI=t[0];
            int secondI=t[1];
            boolean result=(firstI<secondI);
            if (result){
                return 1;
            }
            return 0;
        }
        
        if (a.indexOf("==")!=-1){
            int[] t=evalS(a,"==");
            int firstI=t[0];
            int secondI=t[1];
            boolean result=(firstI==secondI);
            if (result){
                return 1;
            }
            return 0;
        }
        
        //If un-evaluate-able, return false
        return 0;
    }
    /**
     * Evaluates a predicate with no parentheses
     * 
     * Evaluated a predicate with no parentheses, supports && and ||
     * @param a The predicate to be evaluated. Can't have parentheses
     * @return 0 if false, 1 if true
     */
    
    public int evalorandpred(String a){
        String or="||";
        String and="&&";
        if (a.indexOf(or)!=-1){
            String f=a.substring(0,a.indexOf(or));
            String s=a.substring(a.indexOf(or)+or.length(),a.length());
            int f1=0;
            f1=evalorandpred(f);
            int  s1=0;
            s1=evalorandpred(s);
            if (f1==1){
                return f1;
            }
            if (s1==1){
                return 1;
            }
            return 0;
        }
        if (a.indexOf(and)!=-1){
            String f=a.substring(0,a.indexOf("&&"));
            String s=a.substring(a.indexOf("&&")+2,a.length());

            int f1=evalorandpred(f);
            int s1=evalorandpred(s);
            if (f1==1 && s1==1){
                return 1;
            }
            return 0;
        }
        
        return evalsinglepred(a);
    }
    protected int[] evalS(String a, String b){
        
        String firstS=a.split(b)[0];
        String secondS=a.split(b)[1];
        Expression firstE=new Expression(firstS,classID,funcID);
        Expression secondE=new Expression(secondS,classID,funcID);
        int firstI=firstE.eval();
        int secondI=secondE.eval();
        int[] result={firstI,secondI};
        return result;
    }
}
