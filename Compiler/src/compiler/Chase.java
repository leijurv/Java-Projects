/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;
import static compiler.Compiler.verbose;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
/**
 *
 * @author leijurv
 */
public class Chase extends Expression {
    private final ArrayList<Command> contents;
    private final ArrayList<String> preyNames;
    public Chase(ArrayList<String> preyNames, ArrayList<Command> contents) {
        this.contents = contents;
        this.preyNames = preyNames;
    }
    protected Chase(DataInputStream in) throws IOException {
        contents = readmultiple(in);
        int numPreyNames = in.readInt();
        preyNames = new ArrayList<>(numPreyNames);
        for (int i = 0; i < numPreyNames; i++) {
            preyNames.add(in.readUTF());
        }
    }
    @Override
    public Object evaluate(Context c) {//foo=    > chase(bar){blah} <      this is defining a chase not running it
        return this;
    }
    public int getNumPrey() {
        return preyNames.size();
    }
    public Object run(Context c, ArrayList<Object> prey) {
        Context local = c.subContext();
        for (int i = 0; i < prey.size(); i++) {
            local.defineLocal(preyNames.get(i), prey.get(i));
        }
        if (preyNames.size() > prey.size()) {
            System.out.println("Received " + prey.size() + " prey, expected " + preyNames.size() + ". The last " + (preyNames.size() - prey.size()) + " prey will be null.");
        }
        for (int i = 0; i < contents.size(); i++) {
            if (verbose) {
                System.out.println("Line " + i + ": " + contents.get(i) + " with context " + local);
            }
            Command com = contents.get(i);
            if (com.execute(local)) {
                return local.getPounce();
            }
        }
        return null;
    }
    @Override
    public String toString() {
        return "chase (" + preyNames + "){" + contents + "}";
    }
    @Override
    protected void doWriteExpression(DataOutputStream out) throws IOException {
        writemultiple(out, contents);
        out.writeInt(preyNames.size());
        for (String preyName : preyNames) {
            out.writeUTF(preyName);
        }
    }
    @Override
    public byte getExpressionID() {
        return 1;
    }
}
