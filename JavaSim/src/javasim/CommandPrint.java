package javasim;
/**
 * A command that prints something.
 * 
 * @author leif
 */
public class CommandPrint extends Command{
    static final String p="System.out.println(";
    public CommandPrint(String t, int classID,int funcID){
        super(t,classID,funcID);
    }
    /**
     * Runs the command.
     */
    @Override
    public final void run(){
        Expression exp=new Expression(content,ClassID,FuncID);
        System.out.println(exp.eval());
    }
}