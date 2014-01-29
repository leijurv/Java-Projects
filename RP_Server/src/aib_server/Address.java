/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package aib_server;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import java.math.BigInteger;

/**
 *
 * @author leijurv
 */
public class Address {
    BigInteger address;
    BigInteger value;
    String depAddr;
    BigInteger privKey;
    static final NetworkParameters MainNet=NetworkParameters.prodNet();
    public Address(BigInteger Address, BigInteger Value, BigInteger PrivKey){
        value=Value;
        address=Address;
        
       depAddr=new ECKey(PrivKey).toAddress(MainNet).toString();
       privKey=PrivKey;
    }
}
