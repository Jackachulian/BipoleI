package lib.data.actions;

import lib.engine.Action;
import lib.engine.Battle;
import lib.engine.Unit;

/** An action to generate a point for the player. **/
public class GeneratePoint extends Action {
    /** The amount of points to generate. **/
    private final int amount;

    public GeneratePoint(int amount) {
        this.amount = amount;
    }

    @Override
    public void act(Battle battle, Unit unit) {
        unit.getOwner().addPoints(amount);
    }
}
