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
public class ExpressionConstant extends Expression {
    private final Object value;
    public ExpressionConstant(Object value){
        this.value=value;
    }
    protected ExpressionConstant(DataInputStream in) throws IOException{
        value=in.readInt();
    }
    @Override
    public Object evaluate(Context c){
        return value;
    }
    @Override
    public String toString(){
        return "~constant "+value+"~";
    }
    @Override
    protected void writeExpression(DataOutputStream out) throws IOException{
        out.writeInt((Integer) value);//TODO I'm lazy
    }

    public int getExpressionID(){
        return 3;
    }
}
