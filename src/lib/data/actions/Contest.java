package lib.data.actions;

import lib.engine.Action;
import lib.engine.Player;
import lib.engine.Tile;

public class Contest extends Action {
    public Contest() {
        super("Contest");
    }

    /** Contest is visible to all players if the tile is unclaimed. **/
    @Override
    public boolean visible(Player player, Tile tile) {
        return !tile.isClaimed();
    }

    /** Usability is determined by Tile's contestable() method. **/
    @Override
    public boolean usable(Player player, Tile tile) {
        return tile.contestable(player);
    }

    @Override
    public void act(Player player, Tile tile) {
        tile.contest(player);
    }

    @Override
    public String displayName(Tile tile) {
        return String.format("Contest (%d)", tile.contestCost());
    }
}
