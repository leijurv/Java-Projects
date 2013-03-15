/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package texasholdem;

import java.util.Random;

/**
 *
 * @author leijurv
 */
public class Deck extends Hand{
    public Deck(){
        for (int suit=0; suit<4; suit++){
            for (int rank=0; rank<13; rank++){
                cards.add(new Card(suit,rank));
            }
        }
    }
    public Card dealOne(){
        Card c=cards.remove(0);
        cards.add(c);
        return c;
    }
    public void shuffle(){
        Random r=new Random();
        for (int i=0; i<cards.size(); i++){
            int j=r.nextInt(cards.size());
            Card c=cards.get(i);
            cards.set(i,cards.get(j));
            cards.set(j,c);
        }
    }
}
