/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loans;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 *
 * @author leijurv
 */
public class Loan{
    long date;
    int origAmount;
    float intRate;
    long per;
    long until;
    int person;
    boolean paid;
    public Loan(int Person, int amt, float rate, long Per, long Until){
        person=Person;
        date=System.currentTimeMillis();
        origAmount=amt;
        intRate=rate/100;
        per=Per;
        until=Until;
        paid=false;
    }
    public int getCurrentValue(){
        return getValue(System.currentTimeMillis());
    }
    public int getValue(long time){
        int amt=applied(time);
        if (amt==0){
            return origAmount;
        }
        
        float orig=((float)origAmount);
        float val=orig*(float)Math.pow(1+intRate,amt);
        return (int)(val);
    }
    public int applied(long time){
        long since=time-date;
        //System.out.println("Since"+since);
        long b=since-until;
        //System.out.println("Until"+until);
        //System.out.println("m"+Loans.msInDay);
        //System.out.println("b"+b);
        if (b<=0){
            return 0;
        }
        return (int)(b/per+1);
    }
    public void write(DataOutputStream a){
        try {
            a.writeLong(date);
            a.writeBoolean(paid);
            a.writeInt(origAmount);
            a.writeFloat(intRate);
            a.writeLong(per);
            a.writeLong(until);
            a.writeInt(person);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
    public Loan(DataInputStream a){
        try {
            date=a.readLong();
            paid=a.readBoolean();
            origAmount=a.readInt();
            intRate=a.readFloat();
            per=a.readLong();
            until=a.readLong();
            person=a.readInt();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
    public boolean equals(Object o){
        if (!(o instanceof Loan)){
            return false;
        }
        Loan l=(Loan)o;
        if (date!=l.date) return false;
        if (paid!=l.paid) return false;
        if (origAmount!=l.origAmount) return false;
        if (intRate!=l.intRate) return false;
        if (per!=l.per) return false;
        if (until!=l.until) return false;
        if (person!=l.person) return false;
        return true;
    }
}
