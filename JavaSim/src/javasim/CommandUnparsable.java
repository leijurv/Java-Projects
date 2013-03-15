/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javasim;

/**
 *
 * @author leijurv
 */
public class CommandUnparsable extends Command{
    public CommandUnparsable(int funcID, int classID, String content){
        super(content,classID,funcID);
    }
    /**
     * Runs the command.
     */
    @Override
    public final void run(){
        System.out.println("Attempting to run unparsable command: " + content);
                
    }
}
