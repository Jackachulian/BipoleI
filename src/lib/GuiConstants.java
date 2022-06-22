package lib;

import java.io.Serializable;

public class GuiConstants implements Serializable {// ======== CONSTANTS


    // ==== MAP
    public static final boolean JOIN_SIDE_FACES = false;
    /** The period of one line movement on a contested tile, in milliseconds. **/
    public static final int CONTEST_SHIFT_PERIOD = 800;
    /** The amount of lines to draw diagonally on a contested tile. **/
    private static final int CONTESTED_LINE_COUNT = 5;
    /** Step to use when drawing contested tiles (auto-calculated) **/
    public static final double CONTESTED_STEP = 1.0/GuiConstants.CONTESTED_LINE_COUNT;

    // ==== CURSOR
    public static final boolean EASE_CURSOR = true;
    public static final int CURSOR_SPEED = 200;

    // ==== CAMERA
    public static final boolean EASE_CAMERA = true;
    public static final int CAMERA_SPEED = 250;
    /** Amount of milliseconds it takes to rotate 90 degrees. **/
    public static final int ROTATION_SPEED = 5000;
    public static final boolean CAMERA_FOLLOW_CURSOR = true;
    public static final double ZOOM_SCROLL_FACTOR = 0.85;
    public static final double FOLLOW_X_MARGIN = 2.0;
    public static final double FOLLOW_Y_MARGIN = 1.5;
}