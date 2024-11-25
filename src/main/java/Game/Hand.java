package Game;

import java.util.Map;

public class Hand {
    public Map<CardType, Integer> cards;
    public boolean lie;
    public Hand(Map<CardType, Integer> cards, CardType liarsType){
        this.cards = cards;
        lie = cards.size() == 1 && cards.containsKey(liarsType);
    }
}
