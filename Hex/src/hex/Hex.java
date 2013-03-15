/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hex;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 *
 * @author leif
 */
public class Hex {
    static HashMap<Character,String> encode=new HashMap<Character,String>();
    static HashMap<String,Character> decode=new HashMap<String,Character>();
    public static void add(char a, String  b){
        encode.put(a,b);
        decode.put(b,a);
    }
    public static String encode(String a){
        StringBuilder result=new StringBuilder();
        for (int i=0; i<a.length(); i++){
            result=result.append(encode.get(a.charAt(i)));
        }
        return result.toString();
    }
    public static String decode(String a){
        StringBuilder result=new StringBuilder();
        for (int i=0; i<a.length(); i+=4){
            result=result.append(decode.get(a.substring(i,i+4)));
        }
        return result.toString();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnsupportedEncodingException {
        add('0',"0000");
        add('1',"0001");
        add('2',"0010");
        add('3',"0011");
        add('4',"0100");
        add('5',"0101");
        add('6',"0110");
        add('7',"0111");
        add('8',"1000");
        add('9',"1001");
        add('a',"1010");
        add('b',"1011");
        add('c',"1100");
        add('d',"1101");
        add('e',"1110");
        add('f',"1111");
        String AES="abcd123";
        System.out.println(encode(AES));
        System.out.println(decode(encode(AES)));
        String x="a015BrownShelf";
        byte[] b=x.getBytes("UTF-8");
        for (int i=0; i<b.length; i++){
            String a=Integer.toBinaryString(b[i]&0xFF);
            while(a.length()<8){
                a="0"+a;
            }
            System.out.println(a);   
        }
    }
}
