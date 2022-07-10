package lib.elementboxes;

import lib.data.Actions;
import lib.engine.Player;
import lib.engine.Tile;

public class ActionRow extends ElementBox {
    /** The item that is selected, if this row is selected. **/
    public int index;

    public ActionRow() {
        rect.height = 50;
        displayType = DisplayType.FLEX_ROW;
        fillX = true;
        bg = false;
    }
}
