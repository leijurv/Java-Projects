/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package texasholdem;
import java.util.ArrayList;
/**
 *
 * @author leijurv
 */
public class Hand {
    ArrayList<Card> cards=new ArrayList<Card>();
    public Hand(){
    }
    public ArrayList<Card> getCards(){
        ArrayList<Card> result=new ArrayList<Card>(cards.size());
        for (Card c:cards){
            result.add(new Card(c));
        }
        return result;
    }
    public void addCard(Card c){
        cards.add(c);
    }
    public Card removeCard(Card c){
        cards.remove(c);
        return c;
    }
    public Card removeCard(int i){
        return cards.remove(i);
    }
    public Card getCard(int i){
        return cards.get(i);
    }
    public Hand sortBySuit(){
        ArrayList<ArrayList<Card>> result=new ArrayList<ArrayList<Card>>();
        for (int i=0; i<4; i++){
            result.add(new ArrayList<Card>());
        }
        for (int i=0; i<cards.size(); i++){
            result.get(cards.get(i).getSuit()).add(new Card(cards.get(i)));
        }
        Hand r=new Hand();
        for (ArrayList<Card> b: result){
            for (Card q : b){
                r.addCard(new Card(q));
            }
        }
        return r;
    }
    public Hand sortByRank(){
        Hand answer=new Hand();
        ArrayList<Card> temp=new ArrayList<Card>();
        for (Card c : cards){
            temp.add(new Card(c));
        }
        while(!temp.isEmpty()){
            Card x=temp.remove(highest(temp));
            answer.addCard(x);
        }
        return answer;
    }
    private int highest(ArrayList<Card> a){
        int b=0;
        for (int i=0; i<a.size(); i++){
            if (a.get(i).getRank()>a.get(b).getRank()){
                b=i;
            }
        }
        return b;
    }
    public int size(){
        return cards.size();
    }
    public void display(){
        for (int i=0; i<cards.size(); i++){
            if (cards.get(i)==null){
                break;
            }
            cards.get(i).display();
        }
        System.out.println();
    }
}
