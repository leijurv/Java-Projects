/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lisp;

/**
 *
 * @author leijurv
 */
public class Expression {
    Atom result;
    String Value;
    public Expression(String value){
        Value=value;
    }
    public Atom eval(){
        if (Value.startsWith("(")){
            int i=1;
            int b=0;
            String f="";
            while(i==1||b!=0||!Value.substring(i,i+1).equals(" ")){
                if (Value.substring(i,i+1).equals("(")){
                    b++;
                }
                if (Value.substring(i,i+1).equals(")")){
                    b--;
                }
                f+=Value.substring(i,i+1);
                
                i++;
                
            }
             i++;
            String[] vals=new String[1];
            vals[0]="";
            while(!Value.substring(i,i+1).equals(")")||b!=0){
                if (Value.substring(i,i+1).equals("(")){
                    b++;
                }else{
                    if (Value.substring(i,i+1).equals(")")){
                    b--;
                }else{
                        if (b==0&&Value.substring(i,i+1).equals(" ")){
                            vals=add(vals,"");
                        }
                    }
                }
                if (b!=0||!Value.substring(i,i+1).equals(" ")){
                    vals[vals.length-1]+=Value.substring(i,i+1);
                }
                i++;
                
            }
            
            Expression[] v=new Expression[vals.length];
            for (int n=0; n<v.length; n++){
                v[n]=new Expression(vals[n]);
            }
            if (f.startsWith("(")){
                return ((Function)((new Expression(f)).eval())).eval(v);
            }
            return Function.get(f).eval(v);
        }else{
            return new AtomInt(Integer.parseInt(Value));
        }
    }
    public String[] add(String[] a, String b){
        String[] c=new String[a.length+1];
        System.arraycopy(a, 0, c, 0, a.length);
        c[a.length]=b;
        return c;
    }
}
