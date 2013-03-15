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
public class PokerHand <T> extends Hand implements Comparable<T>{
    private static int compare(int a, int b){
        if (a>b){
            return 1;
        }
        if (a<b){
            return -1;
        }
        return 0;
    }
    private static int higher(Hand a, Hand b){
        ArrayList<Card> c=a.sortByRank().getCards();
                ArrayList<Card> d=b.sortByRank().getCards();
                for (int i=0; i<5; i++){
                   if (c.get(i).compareTo(d.get(i))!=0){
                       return c.get(i).compareTo(d.get(i));
                   }
                }
                return 0;
    }
    public int compareTo(PokerHand h){
        Rank m=getRank();
        Rank t=h.getRank();
        if (m.getID()>t.getID()){
            return 1;
        }
        if (m.getID()<t.getID()){
            return -1;
        }
        switch(m.getID()){
            case 0:
                if (compare(((RankHighCard)m).getRank(),((RankHighCard)t).getRank())!=0){
                    return compare(((RankHighCard)m).getRank(), ((RankHighCard)t).getRank());
                }
                return higher(madeHand(this,m),madeHand(h,t));
            case 1:
                if (compare(((RankPair)m).getRank(),((RankPair)t).getRank())!=0){
                    return compare(((RankPair)m).getRank(), ((RankPair)t).getRank());
                }
                return higher(this,h);
            case 2:
                if (compare(((RankTwoPair)m).getRanks()[0],((RankTwoPair)t).getRanks()[0])!=0){
                    return compare(((RankTwoPair)m).getRanks()[0], ((RankTwoPair)t).getRanks()[0]);
                }
                if (compare(((RankTwoPair)m).getRanks()[1],((RankTwoPair)t).getRanks()[1])!=0){
                    return compare(((RankTwoPair)m).getRanks()[1], ((RankTwoPair)t).getRanks()[1]);
                }
                return higher(this,h);
            case 3:
                if (compare(((RankThreeOfAKind)m).getRank(),((RankThreeOfAKind)t).getRank())!=0){
                    return compare(((RankThreeOfAKind)m).getRank(), ((RankThreeOfAKind)t).getRank());
                }
                return higher(this,h);
            case 4:
                return compare(((RankStraight)m).getRank(), ((RankStraight)t).getRank());
            case 5:
                return higher(this,h);
            case 6:
                if (compare(((RankFullHouse)m).getRanks()[0],((RankFullHouse)t).getRanks()[0])!=0){
                    return compare(((RankFullHouse)m).getRanks()[0], ((RankFullHouse)t).getRanks()[0]);
                }
                if (compare(((RankFullHouse)m).getRanks()[1],((RankFullHouse)t).getRanks()[1])!=0){
                    return compare(((RankFullHouse)m).getRanks()[1], ((RankFullHouse)t).getRanks()[1]);
                }
                return higher(this,h);
            case 7:
                if (compare(((RankFourOfAKind)m).getRank(),((RankFourOfAKind)t).getRank())!=0){
                    return compare(((RankFourOfAKind)m).getRank(), ((RankFourOfAKind)t).getRank());
                }
                return higher(this,h);
            case 8:
                return compare(((RankStraightFlush)m).getRank(), ((RankStraightFlush)t).getRank());
            default:
                throw new UnsupportedOperationException("I'm sorry Dave, I don't know how to do that.");
        }
    }
    private static Hand madeHand(Hand h, Rank r){
        //BTW, it needs to have the cards needed for the rank first, then the other cards from highest to lowest.
        //Has to have exactly 5 cards
        ArrayList<Card> rankSorted=h.sortByRank().getCards();
        ArrayList<Card> suitSorted=h.sortBySuit().getCards();
        if (r instanceof RankHighCard){
            ArrayList<Card> c=new ArrayList<Card>(5);
            for (int i=0; i<5; i++){
                c.add(new Card(rankSorted.get(i)));
            }
            Hand result=new Hand();
            for (Card C : c){
                result.addCard(new Card(C));
            }
            return result;
                   
        }
        return new Hand();
    }
    public Rank getRank(){
        ArrayList<Card> rankSorted=sortByRank().getCards();
        ArrayList<Card> suitSorted=sortBySuit().getCards();
        Rank current=new RankHighCard(rankSorted.get(0).getRank());
        for (int i=0; i<rankSorted.size()-1; i++){
            if (rankSorted.get(i).getRank()==(rankSorted.get(i+1)).getRank()){
                if (i<rankSorted.size()-2 && rankSorted.get(i+1).getRank()==(rankSorted.get(i+2)).getRank()){
                    if (i<rankSorted.size()-3 && rankSorted.get(i+2).getRank()==(rankSorted.get(i+3)).getRank()){
                        current=new RankFourOfAKind(rankSorted.get(i).getRank());// 4 of a kind
                        break;
                    }
                    current=new RankThreeOfAKind(rankSorted.get(i).getRank());// 3 of a kind
                    break;
                }
                current=new RankPair(rankSorted.get(i).getRank());// Pair
                break;
            }
        }
        if (current instanceof RankPair){
            boolean b=false;
            for (int i=0; i<rankSorted.size()-1; i++){
                if (rankSorted.get(i).getRank()==(rankSorted.get(i+1)).getRank()){
                    if (b){
                    current=new RankTwoPair(((RankPair)current).getRank(),rankSorted.get(i).getRank());//Two pair
                    break;
                    }else{
                        b=true;
                    }
                }
            }
        }
        
        //THIS DOES NOT WORK! 1,2,2,3,4,5 will not be regognized! Must be run on different array with duplciates removed
        for (int i=0; i<rankSorted.size()-4; i++){
            if (rankSorted.get(i).getRank()==1+rankSorted.get(i+1).getRank()){
                if (rankSorted.get(i+1).getRank()==1+rankSorted.get(i+2).getRank()){
                    if (rankSorted.get(i+2).getRank()==1+rankSorted.get(i+3).getRank()){
                        if ((rankSorted.get(i+3).getRank()==12 && rankSorted.get(i+4).getRank()==0 )||rankSorted.get(i+3).getRank()==1+rankSorted.get(i+4).getRank()){
                            current=new RankStraight(rankSorted.get(i).getRank());
                        }
                    }
                }
            }
        }
        for (int i=0; i<suitSorted.size()-4; i++){
            if (suitSorted.get(i).getSuit()==suitSorted.get(i+1).getSuit()){
                if (suitSorted.get(i+1).getSuit()==suitSorted.get(i+2).getSuit()){
                    if (suitSorted.get(i+2).getSuit()==suitSorted.get(i+3).getSuit()){
                        if (suitSorted.get(i+3).getSuit()==suitSorted.get(i+4).getSuit()){
                            if (current instanceof RankStraight){
                                current=new RankStraightFlush(suitSorted.get(i).getRank());
                            }else{
                                current=new RankFlush(suitSorted.get(i).getSuit());
                            }
                            
                        }
                    }
                }
            }
        }
        if (current instanceof RankPair){
            boolean b=false;
            for (int i=0; i<rankSorted.size()-1; i++){
                if (rankSorted.get(i).getRank()==(rankSorted.get(i+1)).getRank()){
                    if (b){
                        if (rankSorted.get(i+1).getRank()==(rankSorted.get(i+2)).getRank()){
                    current=new RankFullHouse(((RankPair)current).getRank(),rankSorted.get(i).getRank());//Two pair
                    break;
                        }
                    }else{
                        b=true;
                    }
                }
            }
        }
        if (current instanceof RankThreeOfAKind){
            boolean b=false;
            for (int i=0; i<rankSorted.size()-1; i++){
                if (rankSorted.get(i).getRank()==(rankSorted.get(i+1)).getRank()){
                    if (b){
                    current=new RankFullHouse(((RankThreeOfAKind)current).getRank(),rankSorted.get(i).getRank());//Two pair
                    break;
                        
                    }else{
                        if (rankSorted.get(i+1).getRank()==(rankSorted.get(i+2)).getRank()){
                            b=true;
                            i+=2;
                        }
                    }
                }
            }
        }
        
        return current;
    }

    @Override
    public int compareTo(Object t) {
        if (t instanceof PokerHand){
            return compareTo((PokerHand)t);
        }
        throw new RuntimeException("");
    }
}
