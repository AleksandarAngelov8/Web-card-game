package Game;

import java.util.Random;

public class Player {
    boolean [] gunLoadout = {false,false,false,false,false,false};
    public String name;
    int currentBullet;
    Player previousPlayer,nextPlayer;
    public boolean alive;
    public Hand hand;
    public Player(String n){
        hand = new Hand();
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
    public boolean ShootSelf(){
        if (gunLoadout[currentBullet]) {
            alive = false;
            previousPlayer.nextPlayer = this.nextPlayer;
            nextPlayer.previousPlayer = this.previousPlayer;
            System.out.println(name + " successfully shot themselves.");
            return true;
        }
        System.out.println(name + " shot a blank.");
        currentBullet++;
        return false;
    }

}
