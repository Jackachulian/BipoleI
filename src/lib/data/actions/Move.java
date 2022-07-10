package lib.data.actions;

import lib.engine.Action;
import lib.engine.Player;
import lib.engine.Tile;
import lib.engine.Unit;

public class Move extends Action {
    public Move() {
        super("Move");
    }

    @Override
    public boolean visible(Player player, Tile tile) {
        return super.visible(player, tile) && tile.getUnit().getData().isMovable();
    }

    @Override
    public boolean usable(Player player, Tile tile) {
        return super.usable(player, tile);
    }

    @Override
    public void act(Player player, Tile tile) {
        // TODO: implement movement
    }
}
