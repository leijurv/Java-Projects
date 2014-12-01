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
public class ExpressionFunctionCall extends Expression {
    String funcname;
    ArrayList<Expression> args;
    public ExpressionFunctionCall(String FuncName, ArrayList<Expression> arg){
        args=arg;
        funcname=FuncName;
    }
    @Override
    public Object evaluate(Context c){
        ArrayList<Object> arg=new ArrayList<Object>(args.size());
        for (int i=0; i<args.size(); i++){
            arg.add(args.get(i).evaluate(c));
        }
        if (funcname.equals("print")){
            System.out.println("PRINTING "+arg);
            return arg;
        }
        Function f=(Function) (c.get(funcname));
        System.out.println("RUNNING "+f+"("+funcname+") with args"+args+" and context "+c);
        System.out.println("Evaluated args as: "+arg);
        return f.run(c,arg);
    }
    public String toString(){
        return "~call "+funcname+" "+args+"~";
    }
}
