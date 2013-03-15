package javasim;
/**
 * A parser for a simulated class.
 * 
 * @author leif
 */
public class ClassParser {
    /**
     * The source
     */
    String[] src=new String[0];
    /**
     * The result.
     */
    Class result=new Class();
    /**
     * The instruction pointer. Exists because parsing functions changes it.
     */
    int eip=0;
    /**
     * The index of which class it is in.
     */
    int classID=0;
    /**
     * Whether it has been parsed.
     */
    boolean parsed=false;
    public ClassParser(String[] content, String name, int ClassID){
        src=content;
        classID=ClassID;
    }
    /**
     * Parses one line
     * 
     * Parses one line. If it is a function it CAN change eip.
     * @param t The line to be parsed
     */
    public void parse1(String t){
        boolean Public=(t.startsWith("public"));
        boolean Static=(t.contains("static"));
        String ep="\\)";
        String sp="\\(";
        if (t.endsWith("){")){
            //FUNCTION
            String[] a=t.split(sp);
            String args2=a[1];
            String args1=args2.split(ep)[0];
            String[] args=args1.split(",");
            String[] names=new String[args.length];
            String[] types=new String[args.length];
            if (args.length!=1){
                for (int i=0; i<names.length; i++){
                names[i]=args[i].split(" ")[0];
            }
            for (int i=0; i<names.length; i++){
                types[i]=args[i].split(" ")[1];
            }
            }
            
            
            String[] d=new String[0];
            eip++;
            int brackets=1;
            while(brackets!=0){
                
                if (src[eip].startsWith("}")){
                    brackets--;
                }
                if (src[eip].endsWith("{")){
                    brackets++;
                }
                if (brackets!=0){
                    d=add(d,src[eip]);
                }
                
                eip++;
                
            }
            
            String[] b=t.split(sp)[0].split(" ");
            String name=b[b.length-1];
            String ret_type=b[b.length-2];
            CommandParser cp=new CommandParser(d,classID,result.functions.length);
            result.addFunction(new Function(cp,name,Public,Static,types,names,ret_type));
        }
        if (Variable.validDatatype(t.split(" ")[0]) && t.contains("=")){//TODO: Allow thing like "int x" not "int x=0"
            String datatype=t.split(" ")[0];
            String name=t.split("=")[0].split(" ")[1];
            String content=t.split("=")[1];
            Variable[] v=new Variable[result.vars.length+1];
            System.arraycopy(result.vars, 0, v, 0, v.length-1);
            if (datatype.equals("int")){
                v[v.length-1]=new IntVariable(name,Integer.parseInt(content),false);
            }else{
                v[v.length-1]=new Variable(name,content,datatype,false);
            }
            result.vars=v;
        }
        
    }
    /**
     * Parses the functions and variables it has.
     */
    public void parse(){
        while(eip<src.length){
            int o=eip;
            parse1(src[eip]);
            if (o==eip){
                eip++;
            }
        }
        parsed=true;
    }
    protected String[] add(String[] a, String b){
        String[] c=new String[a.length+1];
        System.arraycopy(a, 0, c, 0, a.length);
        c[a.length]=b;
        return c;
    }
}
