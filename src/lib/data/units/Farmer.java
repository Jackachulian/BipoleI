package lib.data.units;

import lib.data.actions.GeneratePoint;
import lib.engine.UnitData;
import lib.geometry.Shape;

public class Farmer extends UnitData {
    public Farmer() {
        super("Farmer",5, 3, 0, 20000);
        setMustAutoAct(true);
        setMovable(false);
        setAction(new GeneratePoint("Point Farm", 1));
        addShape(Shape.triangularPrism(0.4, 0.4, 0.4));
    }
}
