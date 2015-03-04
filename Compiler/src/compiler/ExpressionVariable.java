 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
/**
 *
 * @author leijurv
 */
public class ExpressionVariable extends Expression implements Settable {
    private final String varname;
    public ExpressionVariable(String varname) {
        this.varname = varname;
    }
    protected ExpressionVariable(DataInputStream in) throws IOException {
        varname = in.readUTF();
    }
    @Override
    public Object evaluate(Context c) {
        return c.get(varname);
    }
    @Override
    public String toString() {
        return "~var " + varname + "~";
    }
    @Override
    protected void doWriteExpression(DataOutputStream out) throws IOException {
        out.writeUTF(varname);
    }
    @Override
    public byte getExpressionID() {
        return 4;
    }
    @Override
    public void set(Context c, Object value) {
        c.set(varname, value);
    }
    public String getVarname() {
        return varname;
    }
}
