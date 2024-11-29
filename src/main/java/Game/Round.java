package Game;

import com.google.gson.Gson;

import java.util.*;

public class Round {
    Game game;
    public CardType liarsCard;
    public Player currentPlayer;
    public Hand lastPlayedHand;
    Random random;
    Map<String, Object> turnInformation;
    public int restartCounter;
    public boolean forcedToCall;
    public Round(Game g){
        random = new Random();
        game = g;
        SetNewLiarsCard();
        SetCurrentPlayer();
        turnInformation = new HashMap<>();

        DistributeCards();
        restartCounter = -1;
        forcedToCall = false;
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
    public boolean IterateTurn(Character moveType){
        if (moveType == 'C'){
            turnInformation.put("playerMoving",currentPlayer.name);
            turnInformation.put("previousPlayer",(lastPlayedHand!=null)?lastPlayedHand.player:"");
            turnInformation.put("moveType",moveType);
            if (lastPlayedHand != null) turnInformation.put("playedHand",lastPlayedHand.cards);

            return Call();
        }
        System.out.println("Invalid move");
        return false;
    }
    public boolean IterateTurn(Character moveType, String handUnformatted){
        if (moveType == 'P'){
            turnInformation.put("playerMoving",currentPlayer.name);
            turnInformation.put("previousPlayer",(lastPlayedHand!=null)?lastPlayedHand.player:"");
            turnInformation.put("moveType",moveType);

            String handS = ConvertHandString(handUnformatted);

            int numberOfCardsPlayed = 0;
            Map<CardType,Integer> cards = new HashMap();
            for (int i = 0; i < handS.length()-1; i+=2){
                Character cardTypeC = handS.charAt(i);
                int number = handS.charAt(i+1) - '0';
                numberOfCardsPlayed+=number;
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
                    case 'J':
                        cardType = CardType.Joker;
                        break;
                }
                cards.put(cardType,number);
            }
            Hand hand = new Hand(cards,currentPlayer.name);
            //turnInformation.put("playedHand",cards);
            Map<CardType,Integer> allegedCards = new HashMap();
            allegedCards.put(liarsCard,numberOfCardsPlayed);

            turnInformation.put("allegedPlayedHand",allegedCards);
            return Play(hand);
        }
        System.out.println("Invalid move");
        return false;
    }
    private boolean Call(){
        if (lastPlayedHand == null) {
            System.out.println("There is no last hand");
            return false;
        }

        turnInformation.put("wasLie",lastPlayedHand.IsLie(liarsCard));
        boolean shootingSuccess;
        if (lastPlayedHand.IsLie(liarsCard)){
            System.out.println("It was a lie");
            shootingSuccess = currentPlayer.previousPlayer.ShootSelf();
        }
        else{
            System.out.println("It was NOT a lie");
            shootingSuccess = currentPlayer.ShootSelf();
        }
        turnInformation.put("shootingSuccess", shootingSuccess);
        lastPlayedHand = null;
        EndRound();
        return true;
    }
    private boolean Play(Hand hand){
        lastPlayedHand = hand;
        boolean success = currentPlayer.hand.RemoveCards(hand);
        if (success) {
            if (currentPlayer.hand.cards.isEmpty()){
                currentPlayer.previousPlayer.nextPlayer = currentPlayer.nextPlayer;
                currentPlayer.nextPlayer.previousPlayer = currentPlayer.previousPlayer;

                Player otherPlayer = currentPlayer.previousPlayer;
                if (currentPlayer.nextPlayer == otherPlayer){
                    System.out.println("Forced to eat pant");
                    forcedToCall = true;
                }
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
        restartCounter = 0;
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
    public Map<String, Object> FetchTurnInformation(){
        return turnInformation;
    }
    static public void SetAliveNeighbours(Game game){
        List<String> usernames = new ArrayList<>();
        List<Player> alivePlayer = new ArrayList<>();
        for (Player player: game.players)
        {
            if (player.alive){
                usernames.add(player.name);
                alivePlayer.add(player);
            }
        }
        if (alivePlayer.size() == 2){
            alivePlayer.get(0).nextPlayer = alivePlayer.get(1);
            alivePlayer.get(0).previousPlayer = alivePlayer.get(1);
            alivePlayer.get(1).nextPlayer = alivePlayer.get(0);
            alivePlayer.get(1).previousPlayer = alivePlayer.get(0);
            return;
        }
        for (int i = 0; i < usernames.size(); i++){
            int prev = (i-1)<0?2:i-1;
            int next = (i+1)>2?0:i+1;
            game.players.get(i).SetNeighbours(game.players.get(prev),game.players.get(next));
        }
    }
    private void EndRound(){
        if (CheckOnlyAlive()){
            FinishGame();
        }
        SetAliveNeighbours(game);
        SetNewLiarsCard();
        DistributeCards();
        SetCurrentPlayer();
        forcedToCall = false;
    }
    private void SetCurrentPlayer(){
        List<Player> alivePlayer = new ArrayList<>();
        for (Player player: game.players)
        {
            if (player.alive){
                alivePlayer.add(player);
            }
        }
        currentPlayer = game.getAlivePlayers().get(Math.abs(random.nextInt()%alivePlayer.size()));
    }
}
