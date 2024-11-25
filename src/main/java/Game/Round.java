package Game;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Round {
    Game game;
    CardType liarsCard;
    Player currentPlayer;
    Hand lastPlayedHand;
    public Round(Game g){
        game = g;
        Random random = new Random();
        int cardId = random.nextInt()%3;
        switch (cardId){
            case 0:
                liarsCard = CardType.Ace;
                break;
            case 1:
                liarsCard = CardType.King;
                break;
            case 2:
                liarsCard = CardType.Queen;
                break;
        }
        currentPlayer = game.players.get(random.nextInt()%3);
    }
    public void IterateTurn(String move){
        if (move.charAt(0) == 'C'){
            Call();
        }
        else {
            Map<CardType,Integer> cards = new HashMap();
            for (int i = 1; i < move.length(); i+=2){
                int number = move.charAt(i) - '0';
                Character cardTypeC = move.charAt(i+1);
                CardType cardType = null;
                switch (cardTypeC){
                    case 'A':
                        cardType = CardType.Ace;
                        break;
                    case 'K':
                        cardType = CardType.King;
                        break;
                    case 'Q':
                        cardType = CardType.Queen;
                        break;
                }
                cards.put(cardType,number);
            }
            Hand hand = new Hand(cards,liarsCard);
            Play(hand);
        }
    }
    public void Call(){
        if (lastPlayedHand.lie){
            currentPlayer.previousPlayer.ShootSelf();
        }
        else{
            currentPlayer.ShootSelf();
        }
    }
    public void Play(Hand hand){
        lastPlayedHand = hand;
    }
}
