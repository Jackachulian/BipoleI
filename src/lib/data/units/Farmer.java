package lib.data.units;

import lib.engine.UnitData;
import lib.geometry.Shape;

public class Farmer extends UnitData {
    public Farmer() {
        super("Farmer",5, 3, 0);
        addShape(Shape.triangularPrism(0.4, 0.4, 0.2));
    }
}
