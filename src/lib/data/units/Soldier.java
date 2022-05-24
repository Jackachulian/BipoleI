package lib.data.units;

import lib.engine.Buyable;
import lib.engine.UnitData;
import lib.geometry.Shape;

public class Soldier extends UnitData {
    public Soldier() {
        super("Soldier", 3, 5, 3);
        addShape(Shape.rectangularPrism(0.4, 0.4, 0.4));
    }
}
