package lib.engine;

import lib.Colors;

import java.awt.*;

/** A player engaging in a battle. **/
public class Player {
    /** The color to draw this player's units. **/
    public final Color color;
    /** Unit placement color version of color. **/
    public final Color colorPlace;
    /** The color to draw this player's land. **/
    public final Color landColor;
    /** Color to draw faces for this player's units. **/
    public final Color faceColor;
    /** Unit placement color version of faceColor. **/
    public final Color faceColorPlace;
    /** The amount of points this player has. **/
    private int points;

    public Player(Color color){
        this.color = color;

        landColor = Colors.blendColors(Color.BLACK, color, 0.25);
        faceColor = Colors.blendColors(Color.BLACK, color, 0.05);

        colorPlace = Colors.blendColors(color, Colors.PLACEMENT_FADE,0.5);
        faceColorPlace = new Color(faceColor.getRed(), faceColor.getGreen(), faceColor.getBlue(), 175);
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

    /** Subtract points from this player. If they had enough points, return true. If not, do not subtract any points. **/
    public boolean subtractPoints(int amount){
        if (amount > points) return false;
        points -= amount;
        return true;
    }
}
