/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class CommandWhile extends Command{
    Expression condition;
    ArrayList<Object> contents;
    public CommandWhile(ArrayList<Object> Contents, Expression cond){
        condition=cond;
        contents=Contents;
    }
    @Override
    public boolean execute(Context c){
        Context local=c.subContext();
        while((Boolean)(condition.evaluate(c))){
            for (Object o : contents){
                Command com=(Command)o;
                if (com.execute(local)){
                    c.Return(local.getReturn());
                    return true;
                }
            }
        }
        return false;
    }
    
}
