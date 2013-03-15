package reversepolishcalculator;
public class ReversePolishCalculator {
    int[] stack=new int[0];
    int r=0;
    final String[] op={"+","-","*","/","^"};
    public static void main(String[] args) {
        ReversePolishCalculator calc=new ReversePolishCalculator();
        calc.parse("5 3 + 8 3 - *");
        System.out.println(calc.stack[calc.stack.length-1]);
    }
    ReversePolishCalculator(){}
    public void parse(String command){
        String[] commands=command.split(" ");
        for (int i=0; i<commands.length; i++){
            boolean r1=false;
            for (int n=0; n<op.length; n++){
                if (op[n].equals(commands[i])){
                    doOp(commands[i]);
                    r1=true;
                }
            }
            if (!r1){
                add(Integer.parseInt(commands[i]));
            }
        }
    }
    public void buttonNumber(int button){
        r=(10*r)+button;
    }
    public void buttonEnter(){
        add(r);
        r=0;
    }
    public void buttonOperation(String name){
        doOp(name);
    }
    public void doOp(String name){
        if (stack.length<2){
            return;
        }
        int b=pop();
        int a=pop();
        if (name.equals("+")){
            add(a+b);
        }
        if (name.equals("-")){
            add(a-b);
        }
        if (name.equals("*")){
            add(a*b);
        }
        if (name.equals("/")){
            add(a/b);
        }
        if (name.equals("^")){
            add((int)Math.pow(a,b));
        }
    }
    public void add(int x){
        int[] n=new int[stack.length+1];
        System.arraycopy(stack, 0, n, 0, n.length-1);
        n[stack.length]=x;
        stack=n;
    }
    public int pop(){
        int[] n=new int[stack.length-1];
        System.arraycopy(stack, 0, n, 0, n.length);
        int r=stack[n.length];
        stack=n;
        return r;
    }
}