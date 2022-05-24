package lib.engine;

import java.awt.*;

/** Anything that can be drawn with a Graphics object g at (x, y) with z zoom. **/
public interface MapDrawable {
    /**
     * Draw this object at the given location with the passed graphics object.
     * @param g the Graphics instance
     * @param x X position of top corner of tile (northwestern corner)
     * @param y Y position of top corner of tile (northwestern corner)
     * @param z Amount of zoom to draw with (Zoom is equal to the amount of pixels from the top to bottom corner)
     */
    void draw(Graphics g, double x, double y, double z);

    default void draw(Graphics g, Point pos, double z){
        draw(g, pos.x, pos.y, z);
    }
}
