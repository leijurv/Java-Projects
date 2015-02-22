/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsonrpc;

import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;

/**
 *
 * @author leijurv
 */
public class JSONRPC {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
         BitcoinJSONRPCClient bitcoin = new BitcoinJSONRPCClient(false);
         System.out.println(bitcoin.getBlockChainInfo());
        // TODO code application logic here
    }
    
}
