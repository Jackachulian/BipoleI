package lib;

import java.io.Serializable;

public class GuiConstants implements Serializable {// ======== CONSTANTS
    // MAP DIMENSIONS
    public static final double
            ROW_X_OFFSET = -1.0,
            ROW_Y_OFFSET = 0.5,
            COL_X_OFFSET = 1.0,
            COL_Y_OFFSET = 0.5,
            DEPTH_Y_OFFSET = 0.5,
            HEIGHT_Y_OFFSET = -1.15;

    // ==== MAP
    public static final boolean JOIN_SIDE_FACES = false;

    // ==== CURSOR
    public static final boolean EASE_CURSOR = true;
    public static final int CURSOR_SPEED = 150;

    // ==== CAMERA
    public static final boolean EASE_CAMERA = true;
    public static final int CAMERA_SPEED = 250;
    public static final boolean CAMERA_FOLLOW_CURSOR = true;
    public static final double ZOOM_SCROLL_FACTOR = 0.8;
    public static final double FOLLOW_X_MARGIN = 2.0;
    public static final double FOLLOW_Y_MARGIN = 1.5;
}