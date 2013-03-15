/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package x_86;
import java.io.*;
/**
 *
 * @author leijurv
 */
public class X_86 {

    /**
     * @param args the command line arguments
     */
    static int eip=0;
    static int ebp=0;
    static int esp=0;
    static int base_stack_pointer=0;
    static int eflags=0;
    static int[] memory=new int[0];
    static String[] program=new String[0];
    static String[][] functions=new String[0][0];
    static String[] function_names=new String[0];
    static boolean f=false;
    public static void addcommand(String command){
        String[] newmem=new String[program.length+1];
        System.arraycopy(program, 0, newmem, 0, newmem.length-1);
        newmem[program.length]=command;
        program=newmem;
    }
    public static void run(String[] prog){
       
        //docommand("push ebp");
        
        eip=0;
        int op=0;
        while(eip<prog.length){
            op=eip;
            docommand(prog[eip]);
            if (op==eip){
                eip++;
            }
        }
    }
    public static int mod(int a, int b){
        if (a<b){
            return a;
        }else{
            return mod(a-b,b);
        }
    }
    public static void docommand(String command){
        if (command.split(" ")[0].equals("mov")){
            domov(command.split(" ")[1]);
        }
        if (command.split(" ")[0].equals("com")){
            docom(command.split(" ")[1]);
        }
        if (command.split(" ")[0].startsWith("j")){
            dojmp(command);
        }
        if (command.split(" ")[0].equals("call")){
            call(command.split(" ")[1]);
        }
        if (command.split(" ")[0].equals("inc")){
            docommand("mov "+command.split(" ")[1]+","+Integer.toString(get(command.split(" ")[1])+1));
        }
        if (command.split(" ")[0].equals("add")){
            docommand("mov "+command.split(",")[0].substring(4,command.split(",")[0].length())+","+Integer.toString(get(command.split(" ")[1].split(",")[0])+get(command.split(",")[1])));
        }
        if (command.split(" ")[0].equals("sub")){
            docommand("mov "+command.split(",")[0].substring(4,command.split(",")[0].length())+","+Integer.toString(get(command.split(" ")[1].split(",")[0])-get(command.split(",")[1])));
        }
        if (command.split(" ")[0].equals("mul")){
            docommand("mov "+command.split(",")[0].substring(4,command.split(",")[0].length())+","+Integer.toString(get(command.split(" ")[1].split(",")[0])*get(command.split(",")[1])));
        }
        if (command.split(" ")[0].equals("div")){
            docommand("mov "+command.split(",")[0].substring(4,command.split(",")[0].length())+","+Integer.toString(get(command.split(" ")[1].split(",")[0])/get(command.split(",")[1])));
        }
        if (command.split(" ")[0].equals("mod")){
            docommand("mov "+command.split(",")[0].substring(4,command.split(",")[0].length())+","+Integer.toString(mod(get(command.substring(4,11)),get(command.split(",")[1]))));
        }
        if (command.split(" ")[0].equals("push")){
            push(command.split(" ")[1]);
        }
    }
    public static void push(String pusher){
        add1mem();
        memory[memory.length-1]=get(pusher);
    }
    public static void pop(String poper){
        int pushed=memory[memory.length-1];
        int[] newmem=new int[memory.length-1];
        System.arraycopy(memory, 0, newmem, 0, newmem.length);
        memory=newmem;
        if (!poper.equals("")){
            docommand("mov "+poper+","+Integer.toString(pushed));
        }
        
    }
    public static void call(String command){
        if (command.equals("print")){
            System.out.println(memory[esp]);
            return;
        }
        push("eip");
        push("ebp");
        memory[memory.length-1]++;
        docommand("mov ebp,esp");
        docommand("add ebp,3");
        //TODO: Put the next line in the RUN function (if possible).
        //Needed because functions should be able to allocate how much memory they need.
        docommand("mov esp,0x4");
        base_stack_pointer=esp;
        
        if (command.equals("pr")){
        }
        for (int i=0; i<function_names.length; i++){
            if (function_names[i]==null){
                break;
            }
            
            if (function_names[i].equals(command)){
                run(functions[i]);
                break;
            }
        }
        for (int i=ebp; i<esp; i++){
            pop("");
        }
        if ("print".equals(command)){
            pop("");
        }
        esp=ebp-3;
        base_stack_pointer=esp;
        pop("ebp");
        pop("eip");
    }
    public static void docom(String stuff){
        docom(stuff.split(","));
    }
    public static void docom(String[] stuff){
        docom(stuff[0],stuff[1]);
    }
    public static void docom(String one, String two){
        int o=get(one);
        int t=get(two);
        if (o==t){
            eflags=0;
        }
        if (o<t){
            eflags=1;
        }
        if (o>t){
            eflags=-1;
        }
    } 
    public static void dojmp(String stuff){
        if (stuff.startsWith("jmp ")){
            doj(stuff.substring(4,stuff.length()));
        }
        if (stuff.startsWith("je ")){
            if (eflags==0){
                doj(stuff.substring(3,stuff.length()));
            }
        }
        if (stuff.startsWith("jg ")){
            if (eflags==-1){
                doj(stuff.substring(3,stuff.length()));
            }
        }
        if (stuff.startsWith("jl ")){
            if (eflags==1){
                doj(stuff.substring(3,stuff.length()));
            }
        }
        if (stuff.startsWith("jge ")){
            if (eflags!=1){
                doj(stuff.substring(4,stuff.length()));
            }
        }
        if (stuff.startsWith("jle ")){
            if (eflags!=-1){
                doj(stuff.substring(4,stuff.length()));
            }
        }
        if (stuff.startsWith("jne ")){
            if (eflags!=0){
                doj(stuff.substring(4,stuff.length()));
            }
        }
    }
    public static void doj(String stuff){
        eip=get(stuff);
    }
    public static void domov(String stuff){
        domov(stuff.split(","));
    }
    public static void domov(String[] stuff){
        domov(stuff[0],stuff[1]);
    }
    public static void domov(String destination, String source){
        
        if (destination.startsWith("[") && destination.endsWith("]")){
            if (destination.startsWith("[ebp")){
                if (destination.length()==5){
                    memory[ebp]=get(source);
                }else{
                    if (Integer.parseInt(destination.substring(5,destination.length()-1))>1000){
                        System.out.println(source);
                    }
                    memory[ebp+Integer.parseInt(destination.substring(5,destination.length()-1))]=get(source);
                    
                }
            }
            if (destination.startsWith("[esp")){
                if (destination.length()==5){
                    if (base_stack_pointer==esp){
                        base_stack_pointer--;
                    }
                    memory[esp]=get(source);
                    
                }else{
                    if (esp-Integer.parseInt(destination.substring(5,destination.length()-1))<base_stack_pointer){
                        base_stack_pointer--;
                    }
                    memory[esp-Integer.parseInt(destination.substring(5,destination.length()-1))]=get(source);
                }
            }
        }else{
            if ("esp".equals(destination)){
                addmemory(get(source)-memory.length+1+ebp);
            }
            if ("ebp".equals(destination)){
               ebp=get(source);
            }
        }
    }
    public static int get(String thing){
        if (thing.startsWith("0x")){
            return gethex(thing.substring(2,thing.length()));
        }
        if (thing.startsWith("[") && thing.endsWith("]")){
            if (thing.equals("[ebp]")){
                return memory[ebp];
            }
            if (thing.equals("[esp]")){
                return memory[esp];
            }
            if (thing.startsWith("[ebp+")){
                return memory[ebp+Integer.parseInt(thing.substring(5,thing.length()-1))];
            }
            if (thing.startsWith("[ebp-")){
                return memory[ebp-2-Integer.parseInt(thing.substring(5,thing.length()-1))];
            }
            if (thing.startsWith("[esp")){
                return memory[esp-Integer.parseInt(thing.substring(5,thing.length()-1))];
            }
        }
        if (thing.equals("ebp")){
            return ebp;
        }
        if (thing.equals("esp")){
            return esp;
        }
        if (thing.equals("eip")){
            return eip;
        }
        return Integer.parseInt(thing);
    }
    public static int[] getstack(){
        int[] stack=new int[esp-base_stack_pointer];
        for (int i=base_stack_pointer+1; i<=esp; i++){
            stack[i-base_stack_pointer-1]=memory[i];
        }
        return stack;
    }
    public static int gethex(String hex){
        int result=0;
        for (int i=0; i<hex.length(); i++){
            result+=hexind(hex.substring(i,i+1))*(int)Math.pow(16,hex.length()-i-1);
        }
        return result;
    }
    public static int hexind(String hex){
        String[] h={"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
        for (int i=0; i<h.length; i++){
            if (h[i].equals(hex)){
                return i;
            }
        }
        return -1;
    }
    public static void add1mem(){
        int[] newmem=new int[memory.length+1];
        System.arraycopy(memory, 0, newmem, 0, newmem.length-1);
        memory=newmem;
    }
    public static void addmemory(int howmuch){
        for (int i=0; i<howmuch; i++){
            add1mem();
        }
        esp=memory.length-1;
        base_stack_pointer=esp;
    }
    public static void addlinetofunc(String line){
        String[] ne=new String[functions[functions.length-1].length+1];
        System.arraycopy(functions[functions.length-1], 0, ne, 0, ne.length-1);
        ne[ne.length-1]=line;
        functions[functions.length-1]=ne;
    }
    public static void addfunc(String name){
        String[][] ne=new String[functions.length+1][0];
        System.arraycopy(functions, 0, ne, 0, ne.length-1);
        ne[ne.length-1]=new String[0];
        functions=ne;
        String[] ne1=new String[function_names.length+1];
        System.arraycopy(function_names, 0, ne1, 0, ne1.length-1);
        ne1[ne1.length-1]=name;
        function_names=ne1;
    }
    public static void readfile(String filename){
        if (!filename.endsWith(".x86")){
                throw new RuntimeException("Invalid extenstion (must end with .x86)");
            }
            try{
                FileInputStream fstream = new FileInputStream(filename);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null)   {
                    parseLine(strLine);
                }
                in.close();
            }catch (Exception e){
                System.err.println("Error: " + e.getMessage());
            }
    }
    public static void parseLine(String line){
        if (f){
            if (line.equals("end")){
                f=false;
            }else{
                addlinetofunc(line);
            }
            return;
        }
        if (line.endsWith(":")){
            addfunc(line.substring(0,line.length()-1));
            f=true;
            return;
        }
        addcommand(line);
    }
    public static void main(String[] args) {
        readfile(args[0]);
        run(program);
        //TODO: On line 102
    }
    
}
