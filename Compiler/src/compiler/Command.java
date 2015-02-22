/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
/**
 *
 * @author leijurv
 */
public abstract class Command {
    @Override
    public abstract String toString();
    public abstract boolean execute(Context c);
    protected static boolean isTrue(Object exp){
        return exp instanceof Boolean?(Boolean) exp:!exp.equals(0);
    }
    public abstract int getCommandID();
    protected abstract void doWrite(DataOutputStream out) throws IOException;
    public final void writeCommand(DataOutputStream out) throws IOException{
        System.out.println("Writing id"+getCommandID());
        out.writeInt(getCommandID());
        doWrite(out);
    }
    public static Command readCommand(DataInputStream in) throws IOException{
        int commandID=in.readInt();
        Command result=readCommandWithID(in,commandID);
        if(result==null){
            return null;//Or throw exception, haven't decided yet
        }
        if(result.getCommandID()!=commandID){
            throw new IOException("Failed to read properly");
        }
        System.out.println(result);
        return result;
    }
    private static Command readCommandWithID(DataInputStream in,int commandID) throws IOException{
        switch (commandID){
            case 0:
                return Expression.readExpression(in);
            case 1:
                return new CommandBlink(in);
            case 2:
                return new CommandPounce(in);
            default:
                System.out.println(commandID);
                return null;
        }
    }
    public static ArrayList<Command> readmultiple(DataInputStream in) throws IOException{
        int amt=in.readInt();
        System.out.println("AMT: "+amt);
        ArrayList<Command> commands=new ArrayList<>(amt);
        for (int i=0; i<amt; i++){
            commands.add(readCommand(in));
        }
        return commands;
    }
    public static void writemultiple(DataOutputStream out,ArrayList<Command> commands) throws IOException{
        out.writeInt(commands.size());
        for (Command c : commands){
            c.writeCommand(out);
        }
    }
}
