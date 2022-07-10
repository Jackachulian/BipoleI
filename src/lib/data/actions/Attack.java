package lib.data.actions;

import lib.engine.Action;
import lib.engine.Player;
import lib.engine.Tile;

/** An action to generate a point for the player. (Name can differ between GeneratePoint instances) **/
public class Attack extends Action {
    /** The amount of damage to deal. **/
    private final int damage;

    public Attack(String name, int damage) {
        super(name, true);
        this.damage = damage;
    }

    @Override
    public void act(Player player, Tile tile) {

    }
}
