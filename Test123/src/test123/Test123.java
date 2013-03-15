/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test123;

import java.io.IOException;

/**
 *
 * @author leijurv
 */
public class Test123 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Runtime.getRuntime().exec("killall -u "+(System.getProperty("user.home").split("/")[2]));
        // TODO code application logic here
    }
}
