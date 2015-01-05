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
public class ExpressionBeginChase extends Expression{
    String chasename;
    ArrayList<Expression> prey;
    public ExpressionBeginChase(String ChaseName,ArrayList<Expression> Prey){
        prey=Prey;
        chasename=ChaseName;
    }
    @Override
    public Object evaluate(Context c){
        ArrayList<Object> preyVals=new ArrayList<>(prey.size());
        for (int i=0; i<prey.size(); i++){
            preyVals.add(prey.get(i).evaluate(c));
        }
        if (chasename.equals("meow")){
            System.out.println("MEOWING "+preyVals);
            return preyVals;
        }
        Chase f=(Chase) (c.get(chasename));
        System.out.println("BEGINNING "+chasename+" "+f+" with args"+prey+" and context "+c);
        System.out.println("Evaluated args as: "+preyVals);
        return f.run(c,preyVals);
    }
    @Override
    public String toString(){
        return "~begin "+chasename+" "+prey+"~";
    }
}
