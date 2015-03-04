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
    public ExpressionConstant(Object value) {
        this.value = value;
    }
    protected ExpressionConstant(DataInputStream in) throws IOException {
        byte type = in.readByte();
        value = doRead(in, type);
    }
    @Override
    public Object evaluate(Context c) {
        return value;
    }
    @Override
    public String toString() {
        return "~constant " + value + "~";
    }
    @Override
    protected void doWriteExpression(DataOutputStream out) throws IOException {
        byte type = getType();
        out.writeByte(type);
        switch (type) {
            case 1:
                out.writeInt((Integer) value);
                return;
            case 2:
                out.writeDouble((Double) value);
                return;
            case 3:
                out.writeBoolean((Boolean) value);
                return;
            case 4:
                out.writeUTF((String) value);
                return;
            case 5:
                Object[] k = (Object[]) value;
                out.writeInt(k.length);
                for (Object k1 : k) {
                    out.writeInt((Integer) (k1));
                }
                return;
        }
        throw new IllegalStateException("There is no way this could ever happen");
    }
    private static Object doRead(DataInputStream in, byte type) throws IOException {
        switch (type) {
            case 1:
                return in.readInt();
            case 2:
                return in.readDouble();
            case 3:
                return in.readBoolean();
            case 4:
                return in.readUTF();
            case 5:
                int num = in.readInt();
                Object[] k = new Object[num];
                for (int i = 0; i < num; i++) {
                    k[i] = in.readInt();
                }
                return k;
            default:
                throw new IllegalStateException("Attempting to read constant with nonexistant identifier " + type);
        }
    }
    public byte getType() {
        if (value instanceof Integer) {
            return 1;
        }
        if (value instanceof Double) {
            return 2;
        }
        if (value instanceof Boolean) {
            return 3;
        }
        if (value instanceof String) {
            return 4;
        }
        if (value instanceof Object[]) {
            return 5;
        }
        throw new IllegalStateException("Type is unknown for " + value);
    }
    @Override
    public byte getExpressionID() {
        return 3;
    }
}
