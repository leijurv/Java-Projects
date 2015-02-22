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
    public abstract byte getCommandID();
    protected abstract void doWrite(DataOutputStream out) throws IOException;
    public final void writeCommand(DataOutputStream out) throws IOException{
        System.out.println("Writing command with ID "+getCommandID()+", "+this);
        out.writeByte(getCommandID());
        doWrite(out);
    }
    public static Command readCommand(DataInputStream in) throws IOException{
        byte commandID=in.readByte();
        System.out.println("Command ID: "+commandID);
        Command result=readCommandWithID(in,commandID);
        if (result==null){
            return null;//Or throw exception, haven't decided yet
        }
        if (result.getCommandID()!=commandID){
            throw new IOException("Failed to read properly");
        }
        System.out.println(result);
        return result;
    }
    private static Command readCommandWithID(DataInputStream in,byte commandID) throws IOException{
        switch (commandID){
            case 0:
                return Expression.readExpression(in);
            case 1:
                return new CommandBlink(in);
            case 2:
                return new CommandPounce(in);
            case 3:
                return new CommandPurr(in);
            default:
                System.out.println("RETURNING NULL"+commandID);
                return null;
        }
    }
    public static ArrayList<Command> readmultiple(DataInputStream in) throws IOException{
        int amt=in.readInt();
        System.out.println("Reading "+amt+" commands");
        ArrayList<Command> commands=new ArrayList<>(amt);
        for (int i=0; i<amt; i++){
            commands.add(readCommand(in));
        }
        return commands;
    }
    public static void writemultiple(DataOutputStream out,ArrayList<Command> commands) throws IOException{
        System.out.println("Writing "+commands.size()+" commands, "+commands);
        out.writeInt(commands.size());
        for (Command c : commands){
            c.writeCommand(out);
        }
    }
}
