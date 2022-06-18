package lib.data.actions;

import lib.engine.Action;
import lib.engine.Battle;
import lib.engine.Unit;

/** An action to generate a point for the player. **/
public class GeneratePoint extends Action {
    /** Display name of this action. (Can differ between other GeneratePoint actions) **/
    private final String name;
    /** The amount of points to generate. **/
    private final int amount;

    public GeneratePoint(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    @Override
    public boolean act(Unit unit) {
        unit.getOwner().addPoints(amount);
        return true;
    }
}
