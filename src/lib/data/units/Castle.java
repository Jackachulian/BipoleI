package lib.data.units;

import lib.engine.UnitData;
import lib.geometry.Shape;

public class Castle extends UnitData {
    public Castle() {
        super("Castle",500, 25, 0);

        final double
                SIZE = 0.7,
                HEIGHT = 0.3,

                TOWER_SIZE = 0.15,
                TOWER_SPACING = (SIZE-TOWER_SIZE)/2,
                TOWER_HEIGHT = 0.1,

                CENTER_TOWER_SIZE = 0.2,
                CENTER_TOWER_HEIGHT = 0.2,
                CENTER_TOWER_TRIANGLE_HEIGHT = 0.25;

        // Base
        addShape(Shape.rectangularPrism(SIZE, SIZE, HEIGHT));

        // Towers
        for (int x=-1; x<=1; x++) {
            for (int y=-1; y<=1; y++){
                // Center tower
                if (x==0 && y==0){
                    addShape(Shape.rectangularPrism(
                            0, 0, HEIGHT,
                            CENTER_TOWER_SIZE, CENTER_TOWER_SIZE, CENTER_TOWER_HEIGHT)
                    );
                    addShape(Shape.triangularPrism(
                            0, 0, HEIGHT+CENTER_TOWER_HEIGHT,
                            CENTER_TOWER_SIZE, CENTER_TOWER_SIZE, CENTER_TOWER_TRIANGLE_HEIGHT)
                    );
                }

                // Side bricks
                else {
                    addShape(Shape.rectangularPrism(
                            TOWER_SPACING*x, TOWER_SPACING*y, HEIGHT,
                            TOWER_SIZE, TOWER_SIZE, TOWER_HEIGHT, true, false)
                    );
                }
            }
        }
    }
}
