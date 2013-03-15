/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javasim;

/**
 *
 * @author leijurv
 */
public class CommandFunctionCall extends Command{
    String FunctionName;
    public CommandFunctionCall(String functionname,int classID,int funcID){
        super(functionname,classID,funcID);
        FunctionName=functionname;
    }
    @Override
    public final void run(){
        JavaSim.classes[ClassID].runFunction(FunctionName);
    }
}
