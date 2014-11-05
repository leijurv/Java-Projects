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
public class CommandIf extends Command{
    Command[] contents;
    String condition;
    public CommandIf(String Condition, Command[] Contents){
        condition=Condition;
        contents=Contents;
    }
    @Override
    public String[] compile(int start){
        ArrayList<String> result=new ArrayList<String>();
        int pos=start+1;
        for (Command c : contents){
            String[] r=c.compile(pos);
            for (String s : r){
                result.add(s);
            }
            pos+=r.length;
        }
        String[] res=new String[result.size()+1];
        for (int i=1; i<res.length; i++){
            res[i]=result.get(i-1);
        }
        res[0]="!"+condition+"; "+pos;
        return res;
    }
    
}
