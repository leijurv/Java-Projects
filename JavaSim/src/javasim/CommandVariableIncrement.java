package javasim;
/**
 * A command that increments a variable.
 * 
 * @author leif
 */
public class CommandVariableIncrement extends Command{
    /**
     * The name of the variable to be incremented. NOTE: If there is a local var of a certain name, and a global one of the same name, it will change the global one.
     */
    String varname="";
    /**
     * The index of the variable, in a function, or in the class.
     */
    int varindex=0;
    public CommandVariableIncrement(String c, int classID, int funcID){
        super(c,classID,funcID);
        varname=c;
    }
    /**
     * Increments the variable
     */
    @Override
    public final void run(){
        varindex=JavaSim.classes[ClassID].varIndex(varname);
        boolean c=true;
        if (varindex==-1){
            varindex=JavaSim.classes[ClassID].functions[FuncID].varIndex(varname);
            c=false;
        }
        if (c){
            if(JavaSim.classes[ClassID].vars[varindex] instanceof IntVariable){
            (new CommandVariableSet(varname+"="+varname+"+1",ClassID,FuncID)).run();
            }else{
            }
        }else{
             if(JavaSim.classes[ClassID].functions[FuncID].vars[varindex] instanceof IntVariable){
                 (new CommandVariableSet(varname+"="+varname+"+1",ClassID,FuncID)).run();
             }
        }
        
    }
}
