package javasim;
/**
 *
 * @author leif
 */
public class CommandIf extends Command{
    /**
     * The predicate
     */
    String predicate="";
    /**
     * Whether it should run
     */
    boolean run=true;
    /**
     * The commands inside. Can be other if statements/loops
     */
    Command[] ccontent=new Command[0];
    public CommandIf(String Predicate, String[] content, int classID, int funcID){
        super("if("+Predicate+"){",classID,funcID);
        CommandParser parser=new CommandParser(content,ClassID,funcID);
        parser.parse();
        ccontent=parser.getParsed();
        predicate=Predicate;
    }
    /**
     * Runs the if statement
     * 
     * It also runs all the code inside.
     */
    @Override
    public final void run(){
        BooleanParenthesisHandler handler=new BooleanParenthesisHandler(ClassID,FuncID);
        int r=handler.eval(predicate);
        run=(r==1);
        for (int i=0; i<ccontent.length; i++){
            if (run){
                ccontent[i].run();
            }
            if (ccontent[i] instanceof CommandElse){
                run=!run;
            }
        }
    }
    
    
}
