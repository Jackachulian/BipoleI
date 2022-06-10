package lib.engine;

import lib.ColorUtils;

import java.awt.*;
import java.net.InetAddress;

/** A player engaging in a battle. **/
public class Player {
    /** The color to draw this player's units. **/
    public final Color color;
    /** The color to draw this player's land. **/
    public final Color landColor;
    /** Color to draw faces for this player's units. **/
    public final Color faceColor;
    /** The amount of points this player has. **/
    private int points;

    public Player(Color color){
        this.color = color;

        landColor = ColorUtils.blendColors(Color.BLACK, color, 0.25);
        faceColor = ColorUtils.blendColors(Color.BLACK, color, 0.05);
    }

    public boolean canBuy(Buyable item){
        return points >= item.buyCost();
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addPoints(int amount){
        points += amount;
    }

    public boolean subtractPoints(int amount){
        if (amount > points) return false;
        points -= amount;
        return true;
    }
}
