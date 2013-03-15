package javasim;
/**
 * A function.
 * 
 * @author leif
 */
public class Function extends Variable{
    /**
     * The commands in the function.
     */
    Command[] content=new Command[0];
    /**
     * A command parser for the function. Needed so that commands are parsed at function runtime because parsing before the class they are in is initialized causes errors in checking whether a function call is valid.
     */
    CommandParser cp;
    /**
     * The name of the function
     */
    String Name="";
    /**
     * Whether it is public. This doesn't affect it in any way (so far). Later I will just have an array of keywords.
     */
    boolean Public;
    /**
     * The types of the arguments
     */
    String[] arg_types;
    /**
     * The names of the arguments
     */
    String[] arg_names;
    /**
     * The return type.
     */
    String return_type;
    /**
     * The local variables.
     */
    Variable[] vars=new Variable[0];
    Expression ret;
    public Function(CommandParser commands, String name, boolean PUblic, boolean STatic, String[] Arg_types, String[] Arg_names, String Return_type){
        super(name,"","Function",STatic);
        cp=commands;
        Name=name;
        Public=PUblic;
        arg_types=Arg_types;
        arg_names=Arg_names;
        return_type=Return_type;
    }
    /**
     * Runs the function
     */
    public void run(){
        cp.parse();
        content=cp.getParsed();
        for (int i=0; i<content.length; i++){
            if (content[i] instanceof CommandReturn){
                ret=((CommandReturn)content[i]).retv;
                break;
            }
            content[i].run();
        }
    }
    /**
     * Adds a local variable
     * @param var The variable to be added
     */
    public void addVar(Variable var){
        Variable[] ne=new Variable[vars.length+1];
        System.arraycopy(vars, 0, ne, 0, vars.length);
        ne[vars.length]=var;
        vars=ne;
    }
    /**
     * The index of the local variable
     * 
     * See the method in "Class"
     * @param name The variable to be searched
     * @return The index of the variable.
     */
    public int varIndex(String name){
        for (int i=0; i<vars.length; i++){
            if (vars[i] != null){
                if (vars[i].name.equals(name)){
                return i;
            }
            }
            
        }
        return -1;
    }
}
