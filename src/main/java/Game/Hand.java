package Game;

import java.util.HashMap;
import java.util.Map;

public class Hand {
    public Map<CardType, Integer> cards;
    public String player;
    public Hand(Map<CardType, Integer> cards, String player){
        this.cards = cards;this.player = player;
    }
    public boolean IsLie(CardType liarsType){
        for (CardType card: cards.keySet()){
            if (card != CardType.Joker && card != liarsType) {
                System.out.println("Different card: " + card + " is not liars card: " + liarsType);
                return true;
            }
        }
        return false;
    }
    public Hand(){
        this.cards = new HashMap<>();
    }
    public void AddCard(CardType cardtype){
        if (cards.containsKey(cardtype)){
            cards.put(cardtype,cards.get(cardtype)+1);
        }
        else {
            cards.put(cardtype,1);
        }
    }
    public void PrintCards(){
        System.out.println(cards);
    }
    public boolean RemoveCards(Hand hand){
        for (CardType cardType: hand.cards.keySet()){
            if (!cards.containsKey(cardType)) return false;
            int diff = cards.get(cardType)-hand.cards.get(cardType);
            if (diff < 0) return false;
            if (diff == 0) cards.remove(cardType);
            else cards.put(cardType,diff);
        }
        return true;
    }
}
