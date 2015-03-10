/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;
import java.util.Arrays;
import java.util.HashMap;
/**
 *
 * @author leijurv
 */
public class Context {
    private final HashMap<String, Object>[] values;
    private Object pounceValue;
    public Context() {
        values = new HashMap[] {new HashMap<>()};
    }
    private Context(HashMap<String, Object>[] values) {
        this.values = values;
    }
    public Context subContext() {
        HashMap<String, Object>[] temp = new HashMap[values.length + 1];
        System.arraycopy(values, 0, temp, 0, values.length);
        temp[values.length] = new HashMap<>();
        return new Context(temp);
    }
    public Context superContext() {
        HashMap<String, Object>[] temp = new HashMap[values.length - 1];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = values[i];
        }
        return new Context(temp);
    }
    public Context topContext() {
        return new Context(new HashMap[] {values[0]});
    }
    public void defineLocal(String name, Object value) {
        values[values.length - 1].put(name, value);
    }
    public void set(String name, Object value) {
        for (int i = values.length - 1; i >= 0; i--) {//Start at lowest context
            if (values[i].get(name) != null) {//If this variable is defined here
                values[i].put(name, value);//Overwrite it
                return;
            }
        }
        defineLocal(name, value);//Otherwise define it as local
    }
    public Object get(String name) {
        for (int i = values.length - 1; i >= 0; i--) {
            Object possibleValue = values[i].get(name);
            if (possibleValue != null) {
                return possibleValue;
            }
        }
        System.out.println("WARNING: Unable to find requested variable named '" + name + "'. Returning null. Context is " + toString());
        return null;
    }
    @Override
    public String toString() {
        return Arrays.asList(values).toString();
    }
    public void Pounce(Object o) {
        pounceValue = o;
    }
    public Object getPounce() {
        return pounceValue;
    }
}
