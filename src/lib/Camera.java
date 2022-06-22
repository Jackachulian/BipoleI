package lib;

import lib.timing.AnimatedValue;
import lib.timing.TimingFunction;

import java.io.Serializable;

public class Camera implements Serializable {
    // MAP DIMENSIONS
    public static double rowXOffset;
    public static double rowYOffset;
    public static double colXOffset;
    public static double colYOffset;

    public static final double DEPTH_Y_OFFSET = 0.5;
    public static final double HEIGHT_Y_OFFSET = -1.0;

    public static double rowSlope;
    public static double colSlope;
    /**
     * X position of camera. (Relative to northwestern corner of map)
     **/
    protected static Number cameraX = 0;
    /**
     * Y position of camera. (Relative to northwestern corner of map)
     **/
    protected static Number cameraY = 0;
    /**
     * Current zoom of camera.
     **/
    public static double zoom = 70.0;

    /** Current row position of origin of rotation. **/
    public static double originRow;
    /** Current col position of origin of rotation. **/
    public static double originCol;

    /** Current view angle of camera (in radians). Initially 45 degrees (pi/4). **/
    public static AnimatedValue angle = new AnimatedValue(Math.PI/4);

    /** Amount to shift corners' rotation by when drawing. Related to reverseRows, reverseCols, swapAxes, etc **/
    public static int cornerShift;

    /** The current target angle (in radians). **/
    public static double targetAngle = angle.doubleValue();

    /** If rows should be drawn in reverse order according to angle. **/
    public static boolean reverseRows;

    /** If columns should be drawn in reverse order according to angle. **/
    public static boolean reverseCols;

    /** Whether or not to swap col and row order for screen positioning. **/
    public static boolean swapAxes;

    /** Boolean to keep track of if this will need a recalculation on the next refresh. **/
    private static boolean needsRefresh = true;

    static {
        refresh();
    }

    // ANGLE METHODS
    /** Recalculates various fields of this class based on the current view angle. Doesn't recalculate when angle hasn't animated since last refresh. **/
    public static void refresh() {
        if (!needsRefresh) return;

        double angle = Camera.angle.doubleValue();
        System.out.println(Math.toDegrees(angle));
        reverseRows = Math.sin(angle) < 0;
        reverseCols = Math.cos(angle) < 0;
        swapAxes = Math.tan(angle) < 0;


        if (reverseRows && reverseCols) {
            cornerShift = 2;
        } else if (reverseRows || reverseCols) {
            if (reverseRows) {
                cornerShift = 1;
            } else {
                cornerShift = 3;
            }
        } else {
            cornerShift = 0;
        }

        double drawAngle = angle;
        while (drawAngle < 0) {
            drawAngle += Math.PI/2;
        }
        while (drawAngle > Math.PI/2) {
            drawAngle -= Math.PI/2;
        }

        rowXOffset = Math.cos(drawAngle);
        colXOffset = -Math.sin(drawAngle);

        rowYOffset = 0.5*Math.sin(drawAngle);
        colYOffset = 0.5*Math.cos(drawAngle);

//        System.out.printf("rx=%f, ry=%f, cx=%f, cy=%f %s %n", rowXOffset, rowYOffset, colXOffset, colYOffset, Camera.angle.isAnimating());

        rowSlope = rowYOffset / rowXOffset;
        colSlope = colYOffset / colXOffset;

        needsRefresh = Camera.angle.isAnimating();
    }

    /** Animate towards a certain angle (in radians). **/
    public static void rotateToAngle(double row, double col, double angle) {
        targetAngle = angle;
        originRow = row;
        originCol = col;
        double speedFactor = (Math.abs(targetAngle - Camera.angle.doubleValue())/(Math.PI/2));
        long time = (long)(GuiConstants.ROTATION_SPEED * speedFactor);
        Camera.angle = new AnimatedValue(TimingFunction.LINEAR, time, Camera.angle.doubleValue(), targetAngle);
        needsRefresh = true;
    }

    /** Rotate by a certain angle measurement around the passed point (in radians). **/
    public static void rotate(double row, double col, double angle) {
        rotateToAngle(row, col, targetAngle + angle);
    }
}