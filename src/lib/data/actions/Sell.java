package lib.data.actions;

import lib.engine.Action;
import lib.engine.Player;
import lib.engine.Tile;
import lib.engine.Unit;

public class Sell extends Action {
    public Sell() {
        super("Sell");
    }

    /** Should be visible if the tile is owned by the player and can be sold. **/
    @Override
    public boolean visible(Player player, Tile tile) {
        return super.visible(player, tile) && tile.hasUnit() && tile.getUnit().getData().isSellable();
    }

    // (Sell can only be used if unit is ready. Mostly to prevent selling right before dying)
    // previously it was can be sold whenever

    @Override
    public void act(Player player, Tile tile) {
        Unit soldUnit = tile.removeUnit();
        player.addPoints(soldUnit.sellValue());
    }

    @Override
    public String displayName(Tile tile) {
        return String.format("Sell (%d)", tile.getUnit().sellValue());
    }
}
