/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package texasholdem;

/**
 *
 * @author leijurv
 */
public class TexasHoldEm <Kittens>{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PokerHand h=new PokerHand();
        h.addCard(new Card(0,0));
        h.addCard(new Card(0,1));
        h.addCard(new Card(0,2));
        h.addCard(new Card(0,3));
        h.addCard(new Card(0,4));
        h.addCard(new Card(0,0));
        
        h.display();
        System.out.println(h.getRank());
    }
    public static void mai(String[] args){
        System.out.println("I don't know how to do the made hand. So I don't really know what to do. But it'll be kinda awkward to ask, because he already told me so");
    }
}
