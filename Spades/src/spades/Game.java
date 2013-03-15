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
public class Game {
    static Game currentGame;
    Deck theDeck;
    static Deck deck;
    Random theRandom=new Random();
    Player[] thePlayers;
    int nextPlayerToPlay;
    int twoOfClubsIndx;
    Card[] currentHand=new Card[0];
    static Player player1;
    static Player player2;
    static Player player3;
    static Player player4;
    static Player[] players;
    public Game(Player[] Players){
        theDeck=new Deck(theRandom);
        deck=theDeck;
        thePlayers=Players;
        player1=thePlayers[0];
        player2=thePlayers[1];
        player3=thePlayers[2];
        player4=thePlayers[3];
        players=thePlayers;
    }
    public static void playTwoOfClubs(){
        players[currentGame.nextPlayerToPlay].playCard(currentGame.twoOfClubsIndx);
        currentGame.nextPlayerToPlay=(currentGame.nextPlayerToPlay+1)%4;
    }
    public static int findPlayerWonCurrentTrick(){
        Card[] hand=currentGame.currentHand;
        Card openingCard=hand[0];
        int openingSuit=openingCard.suit;
        int highestCard=-1;
        int highestCardID=0;
        int highestSpadeID=-1;
        int highestSpade=-1;
        boolean hasSpades=false;
        for (int i=0; i<hand.length; i++){
            Card currentCard=hand[i];
            if (currentCard.suit==openingSuit){
                if (currentCard.number>highestCard || currentCard.number==0){
                    highestCard=currentCard.number;
                    highestCardID=i;
                    if (highestCard==0){
                        highestCard=13;
                    }
                }
                
            }
                    
            if (currentCard.suit==3){
                hasSpades=true;
           
                if (currentCard.number>highestSpade || currentCard.number==0){
                    highestSpade=currentCard.number;
                    highestSpadeID=i;
                    if (highestSpade==0){
                        highestSpade=13;
                    }
                }
            }
        }
        
        if (!hasSpades){
            return highestCardID;
        }else{
            return highestSpadeID;
        }
    }
    public static void findTwoOfClubs(){
        for (int i=0; i<players.length; i++){
            Card[] h=players[i].hand;
            for (int j=0; j<h.length; j++){
                Card k=h[j];
                int s=k.suit;
                int n=k.number;
                if (s==2 && n==1){
                    currentGame.nextPlayerToPlay=i;
                    currentGame.twoOfClubsIndx=j;
                }
            }
        }
    }
    public static void sortCards(){
        player1.sortCardsBySuit();
        player2.sortCardsBySuit();
        player3.sortCardsBySuit();
        player4.sortCardsBySuit();
    }
    public static void makeGame(){
        Player[] players={new Player(),new Player(),new Player(),new Player()};
        currentGame=new Game(players);
    }
    public static void printDeck(){
        currentGame.theDeck.printDeck();
    }
}
