/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spades;

/**
 *
 * @author program
 */
public class Card {
    static final String[] cn={"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
    int number;//A2345679JQK
    static final String[] cs={"Hearts","Diamonds","Clubs","Spades"};
    int suit;
    public Card(int Number, int Suit){
        suit=Suit;
        number=Number;
    }
    public String toString(){
        return cn[number]+" of "+cs[suit];
        
    }
    public static void print(Card[] t){
        
        for (int i=0; i<t.length; i++){
            System.out.println(i+":"+t[i]);
        }
    }
}
