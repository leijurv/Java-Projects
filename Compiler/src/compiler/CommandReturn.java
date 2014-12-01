/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

/**
 *
 * @author leijurv
 */
public class CommandReturn extends Command{
    Expression toReturn;
    public CommandReturn(Expression ret){
        toReturn=ret;
    }
    @Override
    public boolean execute(Context c){
        c.Return(toReturn.evaluate(c));
        return true;
    }
    public String toString(){
        return "$ret "+toReturn+"$";
    }
}
