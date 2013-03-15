/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spades;

import java.util.Random;

/**
 *
 * @author program
 */
public class Deck {
    static final Card[] newDeck=getNew();
    Card[] deck;
    Random rand;
    public Deck(Random r){
        deck=newDeck;
        rand=r;
    }
    public void shuffle(){
        randomize(rand);
        randomize(rand);
    }
    public final void randomize(Random r){
        for (int i=0; i<52; i++){
            Switch(deck,i,r.nextInt(52));
        }
    }
    public void Switch(Card[] Deck, int a, int b){
        Card n=Deck[a];
        Deck[a]=Deck[b];
        Deck[b]=n;
    }
    public void printDeck(){
        for (int i=0; i<52; i++){
            System.out.println(deck[i]);
        }
    }
    public void dealCards(Player[] thePlayers){
        for (int i=0; i<deck.length; i++){
            thePlayers[i%4].recieveCard(deck[i]);
        }
    }
    public static Card[] getNew(){
        Card[] result=new Card[52];
        for (int i=0; i<52; i++){
            result[i]=new Card(i%13,(i-i%13)/13);
        }
        return result;
    }
}
