package lib.engine;

import lib.geometry.Shape;

import java.util.List;

public interface Buyable {
    String displayName();
    int buyCost();
    List<Shape> getShapes();
}
