package lib;

import lib.elementboxes.ElementBox;

/** ElementBox designed for the root that has some extra rules **/
public class RootElement extends ElementBox {
    /** When root is focused, make this focused but do not focus all children. **/
    @Override
    public void focus() {
        focused = true;
    }
}
