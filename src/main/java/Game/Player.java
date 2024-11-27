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
        int activeBullet = Math.abs(random.nextInt()%6);
        gunLoadout[activeBullet] = true;
        name = n;
        currentBullet = 0;
        alive = true;
        //System.out.println(name + ": " + activeBullet);
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
