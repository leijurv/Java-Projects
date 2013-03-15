/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package runtimeq;

import java.io.IOException;

/**
 *
 * @author leijurv
 */
public class Runtimeq {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Runtime.getRuntime().exec("python "+System.getProperty("user.home")+"Dropbox/mcp/runtime/startclient.py");
        // TODO code application logic here
    }
}
