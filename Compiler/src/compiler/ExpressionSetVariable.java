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
public class ExpressionSetVariable extends Expression{
    private final String variablename;
    private final Expression value;
    public ExpressionSetVariable(String varname,Expression val){
        variablename=varname;
        value=val;
    }
    protected ExpressionSetVariable(DataInputStream in) throws IOException{
        variablename=in.readUTF();
        value=readExpression(in);
    }
    @Override
    public Object evaluate(Context c){
        Object o=value.evaluate(c);
        c.set(variablename,o);
        return o;
    }
    @Override
    public String toString(){
        return "~set~ "+variablename+"="+value;
    }

    @Override
    protected void writeExpression(DataOutputStream out) throws IOException{
        out.writeUTF(variablename);
        value.writeExpression(out);
    }

    @Override
    public int getExpressionID(){
        return 6;
    }
}
