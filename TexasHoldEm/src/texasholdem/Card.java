/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package texasholdem;

import java.awt.Graphics;

/**
 *
 * @author leijurv
 */
public class Card {
    int suit;
    int rank;
    public Card(int Suit, int Rank){
        suit=Suit;
        rank=Rank;
    }
    public Card(Card c){
        suit=c.suit;
        rank=c.rank;
    }
    public int getSuit(){
        return suit;
    }
    public int getRank(){
        return rank;
    }
    public void display(Graphics g, int x, int y){
        
    }
    public boolean equals(Card c){
        return compareTo(c)==0;
    }
    public int compareTo(Card c){
        if (rank==0){
            return c.rank==0?0:1;
        }
        if (c.rank==0){
            return rank==0?0:-1;
        }
        if (c.rank>rank){
            return -1;
        }
        if (rank>c.rank){
            return 1;
        }
        return 0;
    }
    public String Rank(){
        if (rank==0){
            return "A";
        }else{
            if (rank<10){
                return ""+(rank+1);
            }else{
                if (rank==10){
                    return "J";
                }
                if (rank==11){
                    return "Q";
                }
                if (rank==12){
                    return "K";
                }
                
            }
        }
        return "NO RANK! GAH";
    }
    public String Suit(){
        switch(suit){
            case 0:
                return "Spades";
            case 1:
                return "Clubs";
            case 2:
                return "Hearts";
            case 3:
                return "Diamonds";
        }
        return "NO SUIT! GAH";
    }
    public String toString(){
        return Rank()+" of "+Suit();
    }
    public void display(){
        System.out.println(this);
    }
}
