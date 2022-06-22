package lib.engine;

import lib.Camera;
import lib.Colors;
import lib.DrawUtils;
import lib.GuiConstants;
import lib.elementboxes.TextElement;
import lib.timing.AnimatedValue;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/** Any tile on a battle's map.
 * Units may be placed on them,
 * and they can be claimed by a certain team. **/
public class Tile {
    /** The battle that this tile is placed in. **/
    private final Battle battle;
    /** Row and col position of this tile. **/
    public final int row, col;
    /** The player that owns this tile (the color that is displayed on the ground). Null is no owner. **/
    private Player owner;
    /** The unit that is on this tile. Several units can be on land now owned by the unit's owner. Null is no unit. **/
    private Unit unit;

    /** Amount corners should be raised by corresponding to each type. (NW, SW, SE, NE) **/
    private static final int[][] TYPE_CORNERS = {
            {0, 0, 0, 0},
            {1, 0, 0, 1},
            {1, 1, 0, 0},
            {0, 1, 1, 0},
            {0, 0, 1, 1}
    };

    /** The type of tile this is. 0=flat, 1=north slope, 2=west slope, 3=south slope, 4=east slope. **/
    private final int type;
    /** Height of this tile. Base is 0. **/
    private final int height;
    /** Corners of this tile's base (Not the corners surrounding it). Used for gameplay and drawing. **/
    private final Corners base;
    /** Corners of the next lowest tiles around this tile when connected to other tiles. **/
    private final Corners around;
    /** Corners of the lowest tiles around this tile when connected to other tiles. **/
    private final Corners lowest;
    /** Polygon of this tile's borders on the screen. Reset each time this tile is drawn. Used for drawing and mouse click position grid position finding. **/
    private final Polygon polygon;
    /** Polygon of the eastern face. Used for mouse positional stuff **/
    private final Polygon leftFace;
    /** Polygon of the southern face. Used for mouse positional stuff **/
    private final Polygon rightFace;
    /** Center of this tile on the screen. Calculated on redraw. Used for various draws. **/
    public final Point center;

    private static final Random rng = new Random();

    // Contesting
    /** The player contesting this tile, if it is unclaimed. **/
    private Player contestor;
    /** The point value accumulated with this tile during contesting. **/
    private int contestValue;
    /** The timer that controls the delay for contesting and claiming this tile. **/
    private Timer contestTimer;
    /** Time in nanoseconds when this tile began to be contested. **/
    private long contestStartTime;

    // Display
    /** Current internal target brightness of this unit. **/
    private double brightness;
    /** Displayed brightness which animates towards actual brightness. **/
    private Number displayBrightness = 0;

    protected Tile(Battle battle, int row, int col) {
        this.battle = battle;
        this.row = row;
        this.col = col;

        type = rng.nextDouble() > 0.5 ? rng.nextInt(5) : 0;

        height = 21 - row - col*2 + (rng.nextDouble() > 0.25 ? 1 : 0);
//        height = rng.nextInt(8);

        int[] tc = TYPE_CORNERS[type];
        base = new Corners(height+tc[0], height+tc[1], height+tc[2], height+tc[3]);
        // These two Corners fields will be updated by the Battle's constructor shortly after this is created
        around = new Corners();
        lowest = new Corners();
        polygon = new Polygon();
        rightFace = new Polygon();
        leftFace = new Polygon();
        center = new Point();
    }

    // ==== CONTESTING
    /** Contest this tile by a player. **/
    public void contest(Player contestor){
        // If the player is already contesting this tile, return
        if (this.contestor == contestor) return;
        // If there is not an adjacent claimed tile in the battle, return **/
        if (!battle.isAdjacentClaimedTile(contestor, row, col)) return;
        // Subtract points. If contestor does not have enough points, return
        if (!contestor.subtractPoints(contestValue+1)) return;

        // Increment value by 1 and set the new contestor
        contestValue += 1;
        this.contestor = contestor;
        contestStartTime = System.currentTimeMillis();
        if (contestTimer == null) {
            contestTimer = new Timer(GameConstants.CAPTURE_TIME, evt -> onCapture());
            contestTimer.start();
        } else {
            contestTimer.restart();
        }
    }

    public void onCapture(){
        contestTimer.stop();
        owner = contestor;
        contestor = null;
        contestValue = 0;
    }

    // ==== DISPLAYING
    public void draw(Graphics g) {
        if (hasUnit()) unit.draw(g, this);
    }

    /** Draw the borders for the base of this tile. See drawBase(Graphics, Point, etc...) for details. **/
    public void drawBase(Graphics g, double x, double y) {
        // Screen coordinates for corners
        final int
                bx = (int)x,
                lx = (int)(x + Camera.zoom * Camera.rowXOffset),
                rx = (int)(x + Camera.zoom * Camera.colXOffset),
                fx = (int)(x + Camera.zoom * (Camera.rowXOffset + Camera.colXOffset)),

                by = (int)(y - Camera.zoom * Camera.DEPTH_Y_OFFSET*base.back()),
                ly = (int)(y + Camera.zoom * (Camera.rowYOffset - Camera.DEPTH_Y_OFFSET*base.left())),
                ry = (int)(y + Camera.zoom * (Camera.colYOffset - Camera.DEPTH_Y_OFFSET*base.right())),
                fy = (int)(y + Camera.zoom * (Camera.rowYOffset + Camera.colYOffset - Camera.DEPTH_Y_OFFSET*base.front())),

                lay = (int)(y + Camera.zoom * (Camera.rowYOffset - Camera.DEPTH_Y_OFFSET*around.left())),
                ray = (int)(y + Camera.zoom * (Camera.colYOffset - Camera.DEPTH_Y_OFFSET*around.right())),
                fay = (int)(y + Camera.zoom * (Camera.rowYOffset + Camera.colYOffset - Camera.DEPTH_Y_OFFSET*around.front())),

                lly = (int)(y + Camera.zoom * (Camera.rowYOffset - Camera.DEPTH_Y_OFFSET*lowest.left())),
                rly = (int)(y + Camera.zoom * (Camera.colYOffset - Camera.DEPTH_Y_OFFSET*lowest.right())),
                fly = (int)(y + Camera.zoom * (Camera.rowYOffset + Camera.colYOffset - Camera.DEPTH_Y_OFFSET*lowest.front()));

        // Reset polygon and add tile corner points
        // Block polygon access during so the mouse/other commands don't access the polygon while it's updating points
        synchronized (polygon) {
            polygon.reset();
            polygon.addPoint(bx, by);
            polygon.addPoint(lx, ly);
            polygon.addPoint(fx, fy);
            polygon.addPoint(rx, ry);
        }
        synchronized (leftFace) {
            leftFace.reset();
            leftFace.addPoint(lx, ly);
            leftFace.addPoint(lx, lly);
            leftFace.addPoint(fx, fly);
            leftFace.addPoint(fx, fy);
        }
        synchronized (rightFace) {
            rightFace.reset();
            rightFace.addPoint(rx, ry);
            rightFace.addPoint(rx, rly);
            rightFace.addPoint(fx, fly);
            rightFace.addPoint(fx, fy);
        }
        synchronized (center) {
            center.move(bx, DrawUtils.lerp(by, fy, 0.5));
        }

        // Fill base
        g.setColor(getLandColor());
        g.fillPolygon(polygon);

        // If the southeastern corner is lower, south and east wall faces are always needed
        if (base.front() > lowest.front()) {
            g.setColor(getLandColor());
            g.fillPolygon(leftFace);
//            g.setColor(Color.CYAN);
//            g.drawPolygon(leftFace);

            g.setColor(getLandColor());
            g.fillPolygon(rightFace);
//            g.setColor(Color.MAGENTA);
//            g.drawPolygon(rightFace);
        }

        // If being contested, draw moving diagonal lines
        if (contestor != null) {
            g.setColor(contestor.color);
            double cycle = (double)(System.currentTimeMillis()%GuiConstants.CONTEST_SHIFT_PERIOD)/GuiConstants.CONTEST_SHIFT_PERIOD;
            for (double i = GuiConstants.CONTESTED_STEP*cycle; i < 1; i += GuiConstants.CONTESTED_STEP) {
                // NW-NE-SW triangle
                g.drawLine(DrawUtils.lerp(bx, lx, i), DrawUtils.lerp(by, ly, i), DrawUtils.lerp(bx, rx, i), DrawUtils.lerp(by, ry, i));
                // SE-NE-SW triangle (Lerp backwards so lines move same direction)
                g.drawLine(DrawUtils.lerp(lx, fx, i), DrawUtils.lerp(ly, fy, i), DrawUtils.lerp(rx, fx, i), DrawUtils.lerp(ry, fy, i));
            }
        }

        // Northern and western border, always drawn
        g.setColor(owner!=null ? getColor() : Color.WHITE);
        g.drawLine(bx, by, lx, ly); // northern border
        g.drawLine(bx, by, rx, ry); // western border

        // southern border if both corners not shared on southern side
        if (base.right() > lowest.right() || base.front() > lowest.front()) {
            g.drawLine(rx, ry, fx, fy);
        }
        // eastern border if both corners not shared on eastern side
        if (base.left() > lowest.left() || base.front() > lowest.front()) {
            g.drawLine(lx, ly, fx, fy);
        }

        // left depth line if needed
        if ((base.left() > around.left() && !(GuiConstants.JOIN_SIDE_FACES && around.left() == -1)) || base.left() == around.left() && !GuiConstants.JOIN_SIDE_FACES) {
            g.drawLine(lx, ly, lx, GuiConstants.JOIN_SIDE_FACES ? lay : lly);
        }
        // front depth line if needed
        if ((base.front() > around.front() && !(GuiConstants.JOIN_SIDE_FACES && around.front() == -1))) {
            g.drawLine(fx, fy, fx, fay);
        }
        // right height line if needed
        if ((base.right() > around.right() && !(GuiConstants.JOIN_SIDE_FACES && around.right() == -1)) || base.right() == around.right() && !GuiConstants.JOIN_SIDE_FACES) {
            g.drawLine(rx, ry, rx, GuiConstants.JOIN_SIDE_FACES ? ray : rly);
        }


        // If this is an edge tile, draw the lower borders (south and east)
        // southern ground line if needed
        if (lowest.right() == -1 && lowest.front() == -1) {
            g.drawLine(rx, rly, fx, fly);
        }
        // eastern ground line if needed
        if (lowest.left() == -1 && lowest.front() == -1) {
            g.drawLine(lx, lly, fx, fly);
        }

//        g.setColor(Color.GREEN);
//        g.fillRect(bx-2, by-2, 4, 4);
//        g.setColor(Color.CYAN);
//        g.fillRect(lx-2, ly-2, 4, 4);
//        g.setColor(Color.MAGENTA);
//        g.fillRect(rx-2, ry-2, 4, 4);
//        g.setColor(Color.YELLOW);
//        g.fillRect(fx-2, fy-2, 4, 4);
    }

    /** Determine if a given position on the screen is within this tile on the screen with the given zoom (including side faces). **/
    public boolean containsPoint(int x, int y) {
        return polygon.contains(x, y) || leftFace.contains(x, y) || rightFace.contains(x, y);
    }

    /** Draw the borders for the base of this tile.
     * x and y are the screen coordinates to draw at.
     * nh, wh, sh and eh are the heights of the northern, western, southern and eastern tiles, respectively. **/
    public void drawBase(Graphics g, Point pos){
        drawBase(g, pos.x, pos.y);
    }

    /** Draw UI elements associated with this tile. **/
    public void drawUI(Graphics g) {
        if (beingContested()) {
            DrawUtils.drawBar(g, center.x, center.y,
                    (double)(System.currentTimeMillis()-contestStartTime)/GameConstants.CAPTURE_TIME, contestor.color);

            DrawUtils.drawCenteredString(g,
                    new Rectangle(center.x, center.y, 0, 0),
                    contestValue+"",
                    Color.WHITE,
                    TextElement.GAME_FONT_SMALL);
        } else if (hasUnit()) {
            unit.drawUI(g, this);
        }
    }

    // ==== BRIGHTNESS
    /** Change to the specified brightness in n milliseconds. **/
    public void changeBrightness(double amount, int speed){
        brightness += amount;
        displayBrightness = new AnimatedValue(speed, displayBrightness.doubleValue(), brightness);
    }

    /** Get the line color of this unit after factoring in brightness. **/
    public Color getColor(){
        return brightenColor(owner.color, 1);
    }

    /** Get the face color of this unit after factoring in brightness. **/
    public Color getFaceColor(){
        return brightenColor(owner.faceColor, 0.125);
    }

    /** Get the face color of this unit after factoring in brightness. **/
    public static final Color OWNERLESS_LANDCOLOR = new Color(0, 0, 0, 255);
    public Color getLandColor(){
        return brightenColor(owner == null ? OWNERLESS_LANDCOLOR : owner.landColor, 0.25);
    }

    private Color brightenColor(Color base, double scale) {
        double brightness = displayBrightness.doubleValue();
        if (brightness == 0){
            return base;
        } else if (brightness > 0){
            return Colors.blendColors(base, Color.WHITE, displayBrightness.doubleValue()*scale);
        } else {
            return Colors.blendColors(base, Color.BLACK, -displayBrightness.doubleValue()*scale);
        }
    }

    // ==== HOVERING
    public void onCursorHover(){
        changeBrightness(0.2, GuiConstants.CURSOR_SPEED);
    }

    public void onCursorUnhover(){
        changeBrightness(-0.2, GuiConstants.CURSOR_SPEED);
    }

    // ==== INFO
    public boolean isClaimed(){
        return owner != null;
    }

    public boolean beingContested() {return contestor != null;}

    public boolean hasUnit() {return unit != null;}

    // ==== ACCESSORS

    public Battle getBattle() {
        return battle;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
        unit.resetCooldown();
    }

    public int getHeight() {
        return height;
    }

    public Corners getBase() {
        return base;
    }

    public Corners getAround() {
        return around;
    }

    public Corners getLowest() {
        return lowest;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public Polygon getLeftFace() {
        return leftFace;
    }

    public Polygon getRightFace() {
        return rightFace;
    }
}
