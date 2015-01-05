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
public class CommandPounce extends Command{
    Expression toReturn;
    public CommandPounce(Expression ret){
        toReturn=ret;
    }
    @Override
    public boolean execute(Context c){
        c.Pounce(toReturn.evaluate(c));
        return true;
    }
    @Override
    public String toString(){
        return "$ret "+toReturn+"$";
    }
}
