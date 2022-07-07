package lib.data.actions;

import lib.engine.Action;
import lib.engine.Player;
import lib.engine.Tile;
import lib.engine.Unit;

public class Move extends Action {
    public Move() {
        super("Move");
    }

    /** Should be usable if the unit is movable, in addition to being owned by the player and being ready. **/
    @Override
    public boolean usable(Player player, Tile tile) {
        return super.usable(player, tile) && tile.getUnit().getData().isMovable();
    }

    @Override
    public void act(Player player, Tile tile) {
        // TODO: implement movement
    }
}
