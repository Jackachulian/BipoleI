package lib.geometry;

import java.awt.*;

/** A shape that can be translated and scaled in relation to its parent and included as a shape component. (UNUSED) **/
public class TranslatedShape implements ShapeComponent {
    /** The shape to draw. **/
    public final Shape shape;
    /** Offset of this shape from parent. **/
    public final Vertex translate;

//    public static final Vertex DEFAULT_TRANSLATE = new Vertex(0,0,0);

    public TranslatedShape(Shape shape, Vertex translate) {
        this.shape = shape;
        this.translate = translate;
    }

    @Override
    public void draw(Graphics g, Shape parent) {
        Polygon polygon = new Polygon();
    }
}
