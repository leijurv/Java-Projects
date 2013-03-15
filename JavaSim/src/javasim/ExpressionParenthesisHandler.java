package javasim;
/**
 * A parentheses handler for expressions.
 * 
 * @author leif
 */
public class ExpressionParenthesisHandler extends ParenthesisHandler{
    /**
     * The operations allowed.
     */
    static final String[] op={"+","-","*","/","%"};
    public ExpressionParenthesisHandler(){
        super();
    }
    /**
     * Evaluates a expression
     * @param string The expression to be evaluated. Can't have parentheses
     * @return 
     */
    @Override
    public int evalnopar(String string){
        int a=0;
        int b=0;
        int co=0;
        
        boolean w=false;
        for (int i=0; i<string.length(); i++){
            String c=string.substring(i,i+1);
            if (contains(op,c)){
                if (w){
                    switch (co){
                        case 0:
                            a=a+b;
                        break;
                        case 1:
                            a=a-b;
                        break;
                        case 2:
                            a=a*b;
                        break;
                        case 3:
                            a=a/b;
                        break;
                        case 4:
                            a=a%b;
                        break;
                    }
                   
                }
                w=true;
                co=index(op,c);
                b=0;
            }else{
                if (w){
                    b=(b*10)+Integer.parseInt(c);
                }else{
                    a=(a*10)+Integer.parseInt(c);
                }
                
            }
        }
        switch (co){
            case 0:
                a=a+b;
            break;
            case 1:
                a=a-b;
            break;
            case 2:
                a=a*b;
            break;
            case 3:
                a=a/b;
            break;
            case 4:
                a=a%b;
            break;
        }
        return a;
    }
}
