package Game;

import com.google.gson.Gson;

import java.util.*;

public class Round {
    Game game;
    public CardType liarsCard;
    public Player currentPlayer;
    Hand lastPlayedHand;
    Random random;
    public Round(Game g){
        random = new Random();
        game = g;
        SetNewLiarsCard();
        currentPlayer = game.players.get(Math.abs(random.nextInt()%3));

        DistributeCards();
    }
    private void SetNewLiarsCard(){
        CardType newCard = liarsCard;
        while (newCard == liarsCard){
            int cardId = Math.abs(random.nextInt()%3);
            switch (cardId){
                case 0:
                    newCard = CardType.Ace;
                    break;
                case 1:
                    newCard = CardType.King;
                    break;
                case 2:
                    newCard = CardType.Queen;
                    break;
            }
        }
        liarsCard = newCard;
    }
    // Example: <C> for call, <P2Q>
    public boolean IterateTurn(Character moveType, String handUnformatted){
        String handS = ConvertHandString(handUnformatted);
        if (moveType == 'C'){
            return Call();
        }
        else if (moveType == 'P'){
            Map<CardType,Integer> cards = new HashMap();
            for (int i = 0; i < handS.length()-1; i+=2){
                Character cardTypeC = handS.charAt(i);
                int number = handS.charAt(i+1) - '0';
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
            Hand hand = new Hand(cards);
            return Play(hand);
        }
        else {
            System.out.println("Invalid move");
            return false;
        }
    }
    public boolean Call(){
        if (lastPlayedHand == null) return false;
        if (lastPlayedHand.IsLie(liarsCard)){
            System.out.println("It was a lie");
            if (!currentPlayer.previousPlayer.ShootSelf())
                currentPlayer = currentPlayer.nextPlayer;
            else{
                if (CheckOnlyAlive()){
                    FinishGame();
                }
                currentPlayer = currentPlayer.nextPlayer;
                SetNewLiarsCard();
                DistributeCards();
            }
        }
        else{
            System.out.println("It was NOT a lie");
            if (!currentPlayer.ShootSelf())
                currentPlayer = currentPlayer.nextPlayer;
            else{
                if (CheckOnlyAlive()){
                    FinishGame();
                }
                currentPlayer = currentPlayer.nextPlayer;
                SetNewLiarsCard();
                DistributeCards();
            }
        }
        return true;
    }
    public boolean Play(Hand hand){
        lastPlayedHand = hand;
        boolean success = currentPlayer.hand.RemoveCards(hand);
        if (success) {
            if (currentPlayer.hand.cards.isEmpty()){
                currentPlayer.previousPlayer.nextPlayer = currentPlayer.nextPlayer;
                currentPlayer.nextPlayer.previousPlayer = currentPlayer.previousPlayer;
            }
            currentPlayer = currentPlayer.nextPlayer;
        }
        return success;
    }
    private void DistributeCards(){
        for (Player player: game.players){
            player.hand = new Hand();
        }
        List<CardType> cards = new ArrayList<CardType>();
        for (int i = 0; i<6; i++){
            cards.add(CardType.Ace);
            cards.add(CardType.King);
            cards.add(CardType.Queen);
            if (i < 2) cards.add(CardType.Joker);
        }
        Collections.shuffle(cards);
        for (int i = 0; i<15;){
            for (Player player: game.players){
                if (!player.alive) {
                    i++;
                    continue;
                }
                player.hand.AddCard(cards.get(i));
                i++;
            }
        }
    }
    private boolean CheckOnlyAlive(){
        int count = 0;
        for (Player player: game.players){
            if (player.alive) count++;
        }
        return count == 1;
    }
    public void FinishGame(){
        System.out.println("Game finished:");
        System.out.print("Winner is: ");
        for (Player player: game.players){
            if (player.alive) System.out.println(player.name);
        }
    }
    private String ConvertHandString(String handUnformatted){
        Map handCards = new Gson().fromJson(handUnformatted, Map.class);
        String hand = "";
        for (Object cardType: handCards.keySet()){
            int number = (int)Double.parseDouble(handCards.get(cardType).toString());
            if (number == 0) continue;
            hand += cardType.toString() + number;
        }
        System.out.println("Playing: "+hand);
        return hand;
    }
}
