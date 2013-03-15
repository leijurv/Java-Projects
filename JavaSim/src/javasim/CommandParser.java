package javasim;
/**
 * A parser of commands.
 * 
 * Use only in functions.
 * @author leif
 */
public class CommandParser {
    /** 
     * The instruction pointer. Needed because if statements/loops modify it.
     */
    int eip=0;
    /**
     * The commands to be parsed. (Don't know why it's called to)
     */
    String[] to=new String[0];
    /**
     * The index of which class it is in.
     */
    int classID=0;
    /**
     * The index of which function it is in, in that class.
     */
    int funcID;
    /**
     * The parsed form of the commands.
     */
    Command[] parsed=new Command[0];
    public CommandParser(String[] commands, int ClassID, int FuncID){
        to=commands;
        classID=ClassID;
        funcID=FuncID;
    }
    /**
     * Parses a single command
     * @param c The command to be parsed
     * @return The command that was parsed
     */
    public Command parse(String c){
        //TODO: Check for semicolon/bracket at the end
        for (int i=0; i<Variable.datatypes.length; i++){
            if (c.startsWith(Variable.datatypes[i] + " ")){
                return new CommandVariableSet(c,classID,funcID);
            }
        }
        if (c.split(" ").length==1){
            if (Variable.validName(c.split("=")[0])){
                return new CommandVariableSet(c,classID,funcID);
            }
        }
        if (c.startsWith("if(") && c.endsWith("){")){
            String[] d=new String[0];
            eip++;
            int brackets=1;
            while(brackets!=0){
                
                if (to[eip].startsWith("}")){
                    brackets--;
                }
                if (to[eip].endsWith("{")){
                    brackets++;
                }
                if (brackets!=0){
                    d=add(d,to[eip]);
                }
                
                eip++;
                
            }
            return new CommandIf(c.substring(3,c.length()-2),d,classID,funcID);
        }
        if (c.startsWith("while(") && c.endsWith("){")){
            String[] d=new String[0];
            eip++;
            int brackets=1;
            while(brackets!=0){
                
                if (to[eip].startsWith("}")){
                    brackets--;
                }
                if (to[eip].endsWith("{")){
                    brackets++;
                }
                if (brackets!=0){
                    d=add(d,to[eip]);
                }
                
                eip++;
                
            }
            return new CommandWhile(c.substring(6,c.length()-2),d,classID,funcID);
        }
        if (c.startsWith("until(") && c.endsWith("){")){
            String[] d=new String[0];
            eip++;
            int brackets=1;
            while(brackets!=0){
                
                if (to[eip].startsWith("}")){
                    brackets--;
                }
                if (to[eip].endsWith("{")){
                    brackets++;
                }
                if (brackets!=0){
                    d=add(d,to[eip]);
                }
                
                eip++;
                
            }
            return new CommandWhile(c.substring(6,c.length()-2),d,classID,funcID);
        }
        if(c.equals("}else{")){
            return new CommandElse(classID,funcID);
        }
        if (c.endsWith("++")){
            return new CommandVariableIncrement(c.substring(0,c.length()-2),classID,funcID);
        }
        
        if (c.startsWith(CommandPrint.p) && c.endsWith(")")){
            return new CommandPrint(c.substring(CommandPrint.p.length(),c.length()-1),classID,funcID);
        }
        if (c.startsWith("return ")){
            return new CommandReturn(c.substring(7,c.length()),classID,funcID);
        }
        if (c.endsWith(")")){
            if (c.contains("(")){
                String q=c.split("\\(")[0];
                if (JavaSim.classes[classID].FunctionExists(q)){
                   return new CommandFunctionCall(q,classID,funcID); 
                }
                    
                
            }
        }
        //For the LOLs, I could make it so if a command is not parse-able, it just ignores it.
        return new CommandUnparsable(funcID,classID,c);
    }
    protected Command[] add(Command[] a, Command b){
        Command[] c=new Command[a.length+1];
        System.arraycopy(a, 0, c, 0, a.length);
        c[a.length]=b;
        return c;
    }
    protected String[] add(String[] a, String b){
        String[] c=new String[a.length+1];
        System.arraycopy(a, 0, c, 0, a.length);
        c[a.length]=b;
        return c;
    }
    /**
     * Parses the commands.
     * 
     */
    public void parse(){
        Command[] result=new Command[0];
        eip=0;
        while(eip<to.length){
            int o=eip;
            result=add(result,parse(to[eip]));
            if (o==eip){
                eip++;
            }
        }
        parsed=result;
    }
    /**
     * Gets the parsed commands.
     * 
     * If they haven't already been parsed, it returns an empty array.
     * @return The array of parsed commands.
     */
    public Command[] getParsed(){
        return parsed;
    }
}
