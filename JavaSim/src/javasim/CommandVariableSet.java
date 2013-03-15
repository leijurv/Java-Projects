package javasim;
/**
 * A command that sets a variable.
 * 
 * @author leif
 */
public class CommandVariableSet extends Command{
    /**
     * The name of the variable to be changed
     */
    String var="";
    /**
     * The expression that the variable is being set to.
     */
    String expresstion="";
    /**
     * The type of the variable (String, int)
     */
    String type="";
    /**
     * The expression that the variable is being set to.
     */
    Expression v;
    public CommandVariableSet(String c,int classID, int funcID){
        super(c,classID,funcID);
        
    }
    /**
     * Calculate what the variable will be set to, but don't set it yet.
     */
    public void calculate(){
        int classID=ClassID;
        String c=content;
        if (c.split(" ").length>1){
            var=c.split("=")[0].split(" ")[1];
            type=c.split(" ")[0];
        }else{
            var=c.split("=")[0];
            int index=JavaSim.classes[classID].varIndex(c.split("=")[0]);
            Variable vasr;
            if (index!=-1){
                vasr=JavaSim.classes[classID].vars[index];
            }else{
                index=JavaSim.classes[classID].functions[FuncID].varIndex(c.split("=")[0]);
                vasr=JavaSim.classes[classID].functions[FuncID].vars[index];
            }
            
            type=vasr.type;
        }
        
        expresstion=c.split("=")[1];
        v=new Expression(expresstion,classID,FuncID);
        
    }
    /**
     * Sets the variable
     */
    @Override
    public final void run(){
        calculate();
        Class myClass=JavaSim.classes[ClassID];
        Function myf=myClass.functions[FuncID];
        Variable t=null;
        if (type.equals("int")){
            t=new IntVariable(var,v.eval(),false);
        }
        if (type.equals("String")){
            t=new StringVariable(var,v.evalString(),false);
        }
        if (myClass.varIndex(var)!=-1){
            myClass.vars[myClass.varIndex(var)]=t;
            return;
        }else{
            //ERROR
        }
        if (myf.varIndex(var)!=-1){
            myf.vars[myf.varIndex(var)]=t;
        }else{
            myf.addVar(t);
        }
    }
}
