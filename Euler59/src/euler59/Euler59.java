/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package euler59;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
/**
 *
 * @author leif
 */
public class Euler59 {

    /**
     * @param args the command line arguments
     */
    private int[] Encrypt(int[] message, int[] key) {
    int[] encryptedMessage = new int[message.length];
 
    for (int i = 0; i < message.length; i++) {
        encryptedMessage[i] = message[i] ^ key[i%key.length];
    }
    return encryptedMessage;
}
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner=new Scanner(new File("/cipher1.txt"));
        int count = 0;
for (int i = 0; scanner.hasNext(); i++) {
int c = scanner.nextInt();
if (i % 3 == 0) c ^= 103;
else if ((i + 2) % 3 == 0) c ^= 111;
else c ^= 100;
count += c;
System.out.print((char) c);
}
System.out.println('\n');
System.out.println(count);
        // TODO code application logic here
    }
}
