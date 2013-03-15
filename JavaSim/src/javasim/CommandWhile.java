package javasim;
/**
 * A while loop command.
 * 
 * @author leif
 */
public class CommandWhile extends Command{
    /**
     * The predicate.
     */
    String predicate="";
    /**
     * Whether the predicate evaluates to "true".
     */
    boolean run=true;
    /**
     * The content. Can contain other loops.
     */
    Command[] ccontent=new Command[0];
    public CommandWhile(String Predicate, String[] content, int classID,int funcID){
        super("while("+Predicate+"){",classID,funcID);
        CommandParser parser=new CommandParser(content,ClassID,funcID);
        parser.parse();
        ccontent=parser.getParsed();
        predicate=Predicate;
    }
    /**
     * Runs the loop.
     */
    @Override
    public final void run(){
        BooleanParenthesisHandler handler=new BooleanParenthesisHandler(ClassID,FuncID);
        while(run){
            int r=handler.eval(predicate);
            run=(r==1);
            for (int i=0; i<ccontent.length; i++){
                if (run){
                    ccontent[i].run();
                }
            }
        }
        
    }
    
    
}
