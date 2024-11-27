package Game;

import java.util.List;

public class Game {
    int totalRounds, roundNumber;
    Round currentRound;
    List<Player> players;
    public Game(List<String> usernames, int tR){
        for (int i = 0; i < usernames.size(); i++){
            Player player = new Player(usernames.get(i));
            players.add(player);
        }
        for (int i = 0; i < usernames.size(); i++){
            int prev = (i-1)<0?2:i-1;
            int next = (i+1)>2?0:i+1;
            players.get(i).SetNeighbours(players.get(prev),players.get(next));
        }
        totalRounds = tR;
        roundNumber = 0;
    }
    public void SetupNewRound(){
        roundNumber++;
        if (roundNumber > totalRounds) return;//finish game
        currentRound = new Round(this);
    }
    public void PrintPlayers(){
        System.out.println(players);
    }
}
