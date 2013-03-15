package javasim;
/**
 * A else command.
 * 
 * The code that uses it is in CommandIf
 * 
 * @author leif
 */
public class CommandElse extends Command{
    public CommandElse(int classID,int funcID){
        super("}else{",classID,funcID);
    }
    //Should this do anything else?
}
