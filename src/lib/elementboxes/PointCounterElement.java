package lib.elementboxes;

import lib.Colors;

import java.awt.*;

public class PointCounterElement extends TextElement {
    public PointCounterElement(){
        super();
        bg = true;
        bgColor = Colors.BG_DIM;
        rect.width = 120;
        rect.height = 36;
        setBorder(true);
        borderTop = false;
        centerX = true;

        mouseOverUndim = false;
    }
}
