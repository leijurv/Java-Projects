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
public class ExpressionGetVariable extends Expression {
    private final String varname;
    public ExpressionGetVariable(String varname) {
        this.varname = varname;
    }
    protected ExpressionGetVariable(DataInputStream in) throws IOException {
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
}
