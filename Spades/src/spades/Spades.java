/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spades;

import java.util.Scanner;



/**
 *
 * @author program
 */
//Bid 1:
//Bid 2:
//Bid 3:
//Bid 4:
public class Spades {
    static Scanner scan=new Scanner(System.in);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Game.makeGame();
        Game.deck.shuffle();
        Game.deck.dealCards(Game.players);
        Game.sortCards();
        for (int i=0; i<4; i++){
            Card.print(Game.players[i].hand);
            scan.nextLine();
            for (int q=0; q<=25; q++){
                    System.out.println();
                }
            wait(5000);
        }
        Game.findTwoOfClubs();
        System.out.println(Game.currentGame.nextPlayerToPlay);
        Game.playTwoOfClubs();
        int hand=1;
        while(hand<=13){
            int i=Game.currentGame.nextPlayerToPlay;
            boolean done=false;
            while (!done){
                System.out.println("Hand: "+hand);
                System.out.println("Player: "+(i+1));
                System.out.println("What would you like to play? Your hand is:");
                Card.print(Game.players[i].hand);
                System.out.println("Currently on table: ");
                for (int q=0; q<Game.currentGame.currentHand.length-1; q++){
                    System.out.print(Game.currentGame.currentHand[q]);
                    System.out.print(", ");
                }
                if (Game.currentGame.currentHand.length!=0){
                    System.out.println(Game.currentGame.currentHand[Game.currentGame.currentHand.length-1]);       
                }else{
                    System.out.println("Nothing");
                }
                System.out.print(">");
                int n=Integer.parseInt(scan.nextLine());
                while (!Game.players[i].isValidPlay(n)){
                    System.out.println("Invalid move. What would you like to play?");
                    System.out.print(">");
                    n=Integer.parseInt(scan.nextLine());
                }
                for (int q=0; q<=25; q++){
                    System.out.println();
                }
                done=Game.players[i].playCard(n);
                i=Game.currentGame.nextPlayerToPlay;
                wait(5000);
                System.out.println("Current Tricks Taken:");
                System.out.println("Player 1: "+Game.player1.tricks_taken);
                System.out.println("Player 2: "+Game.player2.tricks_taken);
                System.out.println("Player 3: "+Game.player3.tricks_taken);
                System.out.println("Player 4: "+Game.player4.tricks_taken);
                
            }
         hand++;
        }
    }
    public  static void wait(int time){
        long st=System.currentTimeMillis();
        long c=st;
        while(st+time>c){
            c=System.currentTimeMillis();
        }
    }
}
