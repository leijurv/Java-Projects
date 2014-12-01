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
    private HashMap<String,Object>[] values;
    private Object returnValue;
    public Context(){
        values=new HashMap[0];
    }
    private Context(HashMap<String,Object>[] val){
        values=val;
    }
    public Context subContext(){
        HashMap<String,Object>[] temp=new HashMap[values.length+1];
        for (int i=0; i<values.length; i++){
            temp[i]=values[i];
        }
        temp[values.length]=new HashMap<>();
        return new Context(temp);
    }
    public Context superContext(){
        HashMap<String,Object>[] temp=new HashMap[values.length-1];
        for (int i=0; i<temp.length; i++){
            temp[i]=values[i];
        }
        return new Context(temp);
    }
    public void defineLocal(String name,Object value){
        values[values.length-1].put(name,value);
    }
    public void set(String name,Object value){
        for (int i=values.length-1; i>=0; i--){
            if (values[i].get(name)!=null){
                values[i].put(name,value);
                return;
            }
        }
        defineLocal(name,value);
    }
    public Object get(String name){
        for (int i=values.length-1; i>=0; i--){
            if (values[i].get(name)!=null){
                return values[i].get(name);
            }
        }
        System.out.println("WARNING: Unable to find requested variable named '"+name+"'. Context is "+toString());
        return null;
    }
    public String toString(){
        return Arrays.asList(values).toString();
    }
    public void Return(Object o){
        returnValue=o;
    }
    public Object getReturn(){
        return returnValue;
    }
}
