package lib.geometry;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** A collection of shapes to be drawn in order. **/
public class Mesh {
    /** All shapes to draw in this mesh, in draw order. **/
    List<Shape> shapes;

    public Mesh() {
        shapes = new ArrayList<>();
    }

    /** Draw all shapes in this mesh according to the passed polygon base. **/
    public void draw(Graphics g, Polygon polygon, Color segmentColor, Color faceColor, double zoom) {
        for (Shape shape : shapes) {
            shape.draw(g, polygon, segmentColor, faceColor, zoom);
        }
    }

    /** Add a shape to the mesh. **/
    public void add(Shape shape) {
        shapes.add(shape);
    }
}
