package securecommunication;
import java.math.BigInteger;
public class XOR {
    BigInteger Key;
    public XOR(BigInteger key){
        Key=key;
    }
    public String decode(String thing){
        String result="";
        for (int i=0; i<thing.length()/(Key.toString(2).length()); i++){
            String c=thing.substring(i*(Key.toString(2).length()),(i+1)*(Key.toString(2).length()));
            result=result+frombin(Xor(c));
        }
        return result;
    }
    public String encode(String thing){
        String result="";
        for (int i=0; i<thing.length(); i++){
            String c=tobin(thing.substring(i,i+1));
            while(c.length()<6){
                c="0"+c;
            }
            result=result+Xor(c);
        }
        return result;
    }
    static String[] chars={"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"," ",".",",","!"};
    public String tobin(String thing){
        for (int i=0; i<chars.length; i++){
            if (chars[i].equalsIgnoreCase(thing)){
                return (new BigInteger(Integer.toString(i))).toString(2);
            }
        }
        return "0";
    }
    public String frombin(String thing){
        return chars[Integer.parseInt(new BigInteger(thing,2).toString(10))];
    }
    public String Xor(String t){
        String a=Key.toString(2);
        while(t.length()<a.length()){
            t="0"+t;
        }
        while(a.length()<t.length()){
            a="0"+a;
        }
        String res="";
        for (int i=0; i<t.length(); i++){
            res=res+xoR(t.substring(i,i+1),a.substring(i,i+1));
        }
        return res;
    }
    public String xoR(String a, String b){
        boolean q=!(a.equals(b));
        if (q){
            return "1";
        }
        return "0";
    }
}