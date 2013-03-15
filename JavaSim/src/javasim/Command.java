package javasim;
/**
 * A command. Doesn't do much; almost all the code is in the subclasses.
 * 
 * @author leif
 */
public class Command{
    /**
     * The string of the command. (In Print, it is what it is printing)
     */
    String content="";
    /**
     * The index of which class it is in.
     */
    int ClassID=0;
    /**
     * The index of which function it is in, in that class.
     */
    int FuncID;
    public Command(String c,int classID,int funcID){
        content=c;
        ClassID=classID;
        FuncID=funcID;
    }
    /**
     * Runs the current command.
     * 
     * Should be over-ridden by a final method in a subclass
     */
    public void run(){
        
    }
}
