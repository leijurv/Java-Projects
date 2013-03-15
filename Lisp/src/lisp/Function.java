/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lisp;

/**
 *
 * @author leijurv
 */
public class Function extends Atom{
    static Function[] built_in={new Function("+",""),new Function("*",""),new Function("=",""),new Function("if",""),new Function("-",""),new Function("lambda",""),new Function("defun","")};
    String name;
    String value;
    String[] arg_names;
    public Function (String Name, String Value){
        super();
        name=Name;
        value=Value;
    }
    public Atom eval(Expression[] args){
        if (name.equals("+")){
            int res=0;
            for (int i=0; i<args.length; i++){
                res+=((AtomInt)args[i].eval()).value;
            }
            return new AtomInt(res);
        }
        if (name.equals("*")){
            return new AtomInt(((AtomInt)args[0].eval()).value*((AtomInt)args[1].eval()).value);
        }
        if (name.equals("-")){
            return new AtomInt(((AtomInt)args[0].eval()).value-((AtomInt)args[1].eval()).value);
        }
        if (name.equals("=")){
            Atom a0=args[0].eval();
            Atom a1=args[1].eval();
            if (((AtomInt)a0).value==((AtomInt)a1).value){
                return new AtomInt(1);
            }
            return new AtomInt(0);
        }
        if (name.equals("if")){
            AtomInt pred=(AtomInt)(args[0].eval());
            if (pred.value==0){
                return args[2].eval();
            }
            return args[1].eval();
        }
        if (name.equals("lambda")){
            String argnames=args[0].Value.substring(1,args[0].Value.length()-1);
            String[] Arg_names=argnames.split(" ");
            String Value=args[1].Value;
            
            Function f=new Function("",Value);
            f.arg_names=Arg_names;
            return f;
        }
        if (name.equals("defun")){
            String argnames=args[1].Value.substring(1,args[1].Value.length()-1);
            String[] Arg_names=argnames.split(" ");
            String Value=args[2].Value;
            
            Function f=new Function(args[0].Value,Value);
            f.arg_names=Arg_names;
                    
            Function[] ne=new Function[built_in.length+1];
            System.arraycopy(built_in, 0, ne, 0, ne.length-1);
            ne[ne.length-1]=f;
            built_in=ne;
            return new AtomInt(0);
        }
        System.out.println(((AtomInt)(args[0].eval())).value);
        Expression[] a=args;
        value=rv(value,a);
        
        return (AtomInt)(new Expression(value).eval());
    }
    public String rv(String t, Expression[] w){
        String c=t;
        for (int i=0; i<arg_names.length; i++){
            for (int n=0; n<c.length()-arg_names[i].length(); n++){
                String q=c.substring(n,n+arg_names[i].length());
                if (q.equals(arg_names[i])){
                    Atom r=w[i].eval();
                    if (r instanceof AtomInt){
                        c=c.substring(0,n)+Integer.toString(((AtomInt)r).value)+c.substring(n+arg_names[i].length(),c.length());
                    }
                    if (r instanceof Function){
                        Function y=(Function)r;
                        String d="(lambda (";
                        for (int f=0; f<y.arg_names.length-1; f++){
                            d=d+y.arg_names[i];
                            d+=" ";
                        }
                        d+=y.arg_names[y.arg_names.length-1];
                        d+=") ";
                        d+=y.value;
                        d+=")";
                        c=c.substring(0,n)+d+c.substring(n+arg_names[i].length(),c.length());
                    }
                }
            }
        }
        return c;
    }
    public static Function get(String name){
        //System.out.println(name);
        //System.out.println(built_in[built_in.length-1].name);
        for (int i=0; i<built_in.length; i++){
            if (built_in[i].name.equals(name)){
                return built_in[i];
            }
        }
        return null;
    }
}
