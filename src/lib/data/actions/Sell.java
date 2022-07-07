package lib.data.actions;

import lib.engine.Action;
import lib.engine.Player;
import lib.engine.Tile;
import lib.engine.Unit;

public class Sell extends Action {
    public Sell() {
        super("Sell");
    }

    /** Should be usable if the ready unit is owned by the player and can be sold (Or not ready but auto acts). **/
    @Override
    public boolean usable(Player player, Tile tile) {
        return tile.hasUnit() && (tile.getUnit().isReady() || tile.getUnit().getData().isMustAutoAct()) && tile.getUnit().getData().isSellable();
    }

    @Override
    public void act(Player player, Tile tile) {
        Unit soldUnit = tile.removeUnit();
        player.addPoints(soldUnit.sellValue());
    }

    @Override
    public String displayName(Player player, Tile tile) {
        return String.format("Sell (%d)", tile.getUnit().sellValue());
    }
}
