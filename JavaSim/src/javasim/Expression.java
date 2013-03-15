package javasim;
/**
 * A expression. int or String.
 * 
 * @author leif
 */
public class Expression {
    /**
     * The original String of the expression.
     */
    String orig="";
    /**
     * The index of which class it is in.
     */
    int ClassID;
    /**
     * The index of which function it is in, in that class.
     */
    int funcID;
    public Expression(String x, int classID, int FuncID){
        orig=x;
        ClassID=classID;
        funcID=FuncID;
    }
    /**
     * Evaluates the expression.
     * @return The evaluated expression
     */
    public int eval(){
        ExpressionParenthesisHandler t=new ExpressionParenthesisHandler();
        return t.eval(replacevars(replacevars(replacevars(orig,JavaSim.classes[ClassID].vars),JavaSim.classes[ClassID].functions[funcID].vars),JavaSim.classes[ClassID].functions));
    }
    /**
     * Replaces the variable in a String with their values
     * 
     * If the variable are strings, they will be replaced in quotations
     * @param a The String which needs the variables to be replaced
     * @param vars The variables to the replaced
     * @return The String of the replaced variables
     */
    public String replacevars(String a, Variable[] vars){
        String q=a;
        //TODO: Local function vars
        for (int i=0; i<vars.length; i++){
            for (int n=0; n<=q.length()-vars[i].name.length(); n++){
                if (q.substring(n,n+vars[i].name.length()).equals(vars[i].name)){
                    boolean r=true;
                    String prev="";
                    String nxt="";
                    if (n!=0){
                        prev=q.substring(n-1,n);
                        if (!w(prev)){
                            r=false;
                        }
                    }
                    if (n+vars[i].name.length()<q.length()){
                        nxt=q.substring(n+vars[i].name.length(),1+n+vars[i].name.length());
                        if (!w(nxt)){
                            r=false;
                        }
                    }
                    if (r){
                        String insert=vars[i].name;
                        int end=n+vars[i].name.length();
                        if (nxt.equals("(")){
                            if (!(vars[i] instanceof Function)){
                                //ERROR
                            }else{
                                Function f=(Function)vars[i];
                                f.run();
                                Expression rab=f.ret;
                                if (f.return_type.equals("int")){
                                    insert=Integer.toString(rab.eval());
                                }
                                //TODO: Get the arguments, then set the functions local variables to them, then run the function.
                                end=end+2;
                            }
                        }
                        if (vars[i] instanceof IntVariable){
                            insert=vars[i].string_content;
                        }
                        q=q.substring(0,n)+insert+q.substring(end,q.length());
                    }
                    
                }
            }
        }
        return q;
    }
    protected boolean w(String q){
        if (q.equals(JavaSim.quot)){
            return false;
        }
        if (ExpressionParenthesisHandler.contains(ExpressionParenthesisHandler.op,q)){
            return true;
        }
        if (q.equals("(")){
            return true;
        }
        if (q.equals(")")){
            return true;
        }
        if (q.equals("=")){
            return true;
        }
        if (q.equals(">")){
            return true;
        }
        if (q.equals("|")){
            return true;
        }
        return false;
    }
    /**
     * Evaluates the expression, with its return as a String
     * @return The evaluated expression
     */
    public String evalString(){
        //TODO: Add a parenthesis handler with capability for string addition. 
        if (orig.startsWith(JavaSim.quot) && orig.endsWith(JavaSim.quot)){
            return orig;
        }
        return null;
    }
    
    
}
