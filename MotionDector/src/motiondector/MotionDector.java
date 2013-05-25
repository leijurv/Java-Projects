/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package motiondector;

import java.io.*;

/**
 *
 * @author leijurv
 */
public class MotionDector {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        Process p=Runtime.getRuntime().exec("/Users/leijurv/Downloads/ImageSnap-v0.2.5/imagesnap");
        InputStream i=p.getInputStream();
        System.out.println(i.available());
        // TODO code application logic here
    }
}
