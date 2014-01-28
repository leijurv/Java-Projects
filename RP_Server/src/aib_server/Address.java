/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package aib_server;

import java.math.BigInteger;

/**
 *
 * @author leijurv
 */
public class Address {
    BigInteger address;
    BigInteger value;
    String depAddr;
    public Address(BigInteger Address, BigInteger Value, String Dlorb){
        value=Value;
        address=Address;
        depAddr=Dlorb;
    }
}
