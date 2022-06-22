package lib.elementboxes;

public class PointCounterElement extends TextElement {
    public PointCounterElement(){
        super();
        rect.width = 120;
        rect.height = 36;
        setBorder(true);
        borderTop = false;
        centerX = true;

        mouseOverUndim = false;
    }
}
