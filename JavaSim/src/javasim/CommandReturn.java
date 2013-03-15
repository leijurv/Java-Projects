/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javasim;

/**
 *
 * @author leijurv
 */
public class CommandReturn extends Command{
    Expression retv;
    public CommandReturn(String returnThing, int classID, int funcID){
        super(returnThing,classID,funcID);
        retv=new Expression(returnThing,classID,funcID);
    }
}
