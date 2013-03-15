/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stagefive;

/**
 *
 * @author leif
 */
public class StageFive {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String a="109 182 6 11 88 214 74 77 153 177 109 195 76 37 188 166 188 73 109 158 15 208 42 5 217 78 209 147 4 81 80 169 109 22 96 169 3 29 214 215 9 198 77 112 8 30 117 124 86 96 73 177 50 161";
        String b="Cubum autem in duos cubos, aut quadratoquadratum in duos quadratoquadratos, et generaliter nullam in infinitum ultra quadratum potestatem in duos eiusdem nominis fas est dividere cuius rei demonstrationem mirabilem sane detexi. Hanc marginis exiguitas non caperet.";
        String[] c=a.split(" ");
        String e="";
        for (int i=0; i<b.length(); i++){
            e=e+(b.substring(i,i+1).equals(" ") || b.substring(i,i+1).equals(",") || b.substring(i,i+1).equals(".")?"":b.substring(i,i+1));
        }
        System.out.println(e);
        for (int i=0; i<c.length; i++){
            int d=Integer.parseInt(c[i]);
            System.out.print(e.substring(d-1,d));
        }
        // TODO code application logic here
    }
}
