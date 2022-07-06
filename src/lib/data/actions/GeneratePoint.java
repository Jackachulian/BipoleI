package lib.data.actions;

import lib.engine.*;

/** An action to generate a point for the player. (Name can differ between GeneratePoint instances) **/
public class GeneratePoint extends Action {
    /** The amount of points to generate. **/
    private final int amount;

    public GeneratePoint(String name, int amount) {
        super(name);
        this.amount = amount;
    }

    @Override
    public void act(Player player, Tile tile) {
        tile.getUnit().getOwner().addPoints(amount);
    }
}
