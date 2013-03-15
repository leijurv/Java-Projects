package javasim;
/**
 * A integer variable.
 * 
 * @author leif
 */
public class IntVariable extends Variable{
    public IntVariable(String Name, int Content, boolean STatic){
        super(Name,Integer.toString(Content),"int",STatic);
    }
}
