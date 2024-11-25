package Game;

import java.util.Random;

public class Player {
    boolean [] gunLoadout = {false,false,false,false,false,false};
    String name;
    int currentBullet;
    Player previousPlayer,nextPlayer;
    boolean alive;
    public Player(String n){
        Random random = new Random();
        gunLoadout[random.nextInt()%6] = true;
        name = n;
        currentBullet = 0;
        alive = true;
    }
    public void SetNeighbours(Player pp,Player np){
        previousPlayer = pp;
        nextPlayer = np;
    }
    public void ShootSelf(){
        if (gunLoadout[currentBullet]) {
            alive = false;
            previousPlayer.nextPlayer = this.nextPlayer;
            nextPlayer.previousPlayer = this.previousPlayer;
            return;
        }
        currentBullet++;
    }
}
