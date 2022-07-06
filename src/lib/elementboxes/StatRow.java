package lib.elementboxes;

import lib.elementboxes.ElementBox;
import lib.engine.Unit;

public class StatRow extends ElementBox {
    /** The unit being displayed. (This element will be inactive if no unit) **/
    public Unit unit;

    public StatRow() {
        rect.height = 50;
        displayType = DisplayType.FLEX_ROW;
        fillX = true;
        bg = false;
    }
}
