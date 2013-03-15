/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package texasholdem;

/**
 *
 * @author leijurv
 */
public class Controller {
    public Controller(){
        wins=new int[4];
    }
    Card[] p1;
    Card[] p2;
    Card[] p3;
    Card[] p4;
    Card[] flop;
    Card turn;
    Card river;
    int state;//0: Generate all, 1: Generate Flop, Turn, and River, 2: Generate Turn and River, 3: Generate River
    int total;
    int[] wins;
    Deck d;
    public void DoIteration(){
        d.shuffle();
        int winner=0;
        PokerHand[] h=generate(d);
        for (int i=0; i<4; i++){
            if (h[i].compareTo(h[winner])==1){
                winner=i;
            }
            
        }
        for (int i=0; i<4; i++){
            if (i!=winner && h[i].compareTo(h[winner])==0){
                return;//Tie, doesn't count
            }
        }
        wins[winner-1]++;
    }
    private void setupFlop(Deck d){
        flop=new Card[3];
        flop[0]=d.dealOne();
        flop[1]=d.dealOne();
        flop[2]=d.dealOne();
    }
    private void setupTurn(Deck d){
        turn=d.dealOne();
    }
    private void setupRiver(Deck d){
        river=d.dealOne();
    }
    private void setupCards(Deck d){
        p1=new Card[2];
            p1[0]=d.dealOne();
            p1[1]=d.dealOne();
            p2=new Card[2];
            p2[0]=d.dealOne();
            p2[1]=d.dealOne();
            p3=new Card[2];
            p3[0]=d.dealOne();
            p3[1]=d.dealOne();
            p4=new Card[2];
            p4[0]=d.dealOne();
            p4[1]=d.dealOne();
    }
    private PokerHand[] generate(Deck d){
        if (state<1){//0
            setupCards(d);
        }
        if (state<2){//Deal
            setupFlop(d);
        }
        if (state<3){//Flop
            setupTurn(d);
        }
        if (state<4){//Turn
            setupRiver(d);
        }
        PokerHand h1=new PokerHand();
        h1.addCard(p1[0]);
        h1.addCard(p1[1]);
        PokerHand h2=new PokerHand();
        h2.addCard(p2[0]);
        h2.addCard(p2[1]);
        PokerHand h3=new PokerHand();
        h3.addCard(p3[0]);
        h3.addCard(p3[1]);
        PokerHand h4=new PokerHand();
        h4.addCard(p4[0]);
        h4.addCard(p4[1]);
        for (Card c : flop){
            h1.addCard(c);
            h2.addCard(c);
            h3.addCard(c);
            h4.addCard(c);
        }
        h1.addCard(turn);
        h2.addCard(turn);
        h3.addCard(turn);
        h4.addCard(turn);
        h1.addCard(river);
        h2.addCard(river);
        h3.addCard(river);
        h4.addCard(river);
        return new PokerHand[] {h1,h2,h3,h4};
    }
}
