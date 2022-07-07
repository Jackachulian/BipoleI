package lib.elementboxes;

import lib.data.Actions;
import lib.engine.Player;
import lib.engine.Tile;

public class ActionFirstRow extends ElementBox {
    /** The item that is selected, if this row is selected. **/
    public int index;

    public ActionFirstRow(Player player, Tile tile) {
        rect.height = 50;
        displayType = DisplayType.FLEX_ROW;
        fillX = true;
        bg = false;

        ActionElement move = new ActionElement(Actions.MOVE, player, tile);
        move.fillX = false;
        addChild(move);

        ActionElement sell = new ActionElement(Actions.SELL, player, tile);
        sell.fillX = false;
        addChild(sell);
    }
}
