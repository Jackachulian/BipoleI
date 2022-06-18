package lib.engine;

import lib.ColorUtils;
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
    public void drawBase(Graphics g, double x, double y, double z) {
        // Screen coordinates for corners
        final int
                nwx = (int)x,
                nex = (int)(x + z* GuiConstants.ROW_X_OFFSET),
                swx = (int)(x + z* GuiConstants.COL_X_OFFSET),
//                sex = (int)(x + z*(GuiConstants.ROW_X_OFFSET + GuiConstants.COL_X_OFFSET)), // ayo???
                sex = nwx, // ayo???
                nwy = (int)(y - z * GuiConstants.DEPTH_Y_OFFSET*base.nw),
                ney = (int)(y + z * (GuiConstants.ROW_Y_OFFSET - GuiConstants.DEPTH_Y_OFFSET*base.ne)),
                swy = (int)(y + z * (GuiConstants.COL_Y_OFFSET - GuiConstants.DEPTH_Y_OFFSET*base.sw)),
                sey = (int)(y + z*(GuiConstants.ROW_Y_OFFSET + GuiConstants.COL_Y_OFFSET - GuiConstants.DEPTH_Y_OFFSET*base.se)),
                sway = (int)(y + z * (GuiConstants.COL_Y_OFFSET - GuiConstants.DEPTH_Y_OFFSET*around.sw)),
                neay = (int)(y + z * (GuiConstants.ROW_Y_OFFSET - GuiConstants.DEPTH_Y_OFFSET*around.ne)),
                seay = (int)(y + z * (GuiConstants.ROW_Y_OFFSET + GuiConstants.COL_Y_OFFSET - GuiConstants.DEPTH_Y_OFFSET*around.se)),
                swly = (int)(y + z * (GuiConstants.ROW_Y_OFFSET - GuiConstants.DEPTH_Y_OFFSET*lowest.sw)),
                nely = (int)(y + z * (GuiConstants.COL_Y_OFFSET - GuiConstants.DEPTH_Y_OFFSET*lowest.ne)),
                sely = (int)(y + z*(GuiConstants.ROW_Y_OFFSET + GuiConstants.COL_Y_OFFSET - GuiConstants.DEPTH_Y_OFFSET*lowest.se));

        // Reset polygon and add tile corner points
        // Block polygon access during so the mouse/other commands don't access the polygon while it's updating points
        synchronized (polygon) {
            polygon.reset();
            polygon.addPoint(nwx, nwy);
            polygon.addPoint(nex, ney);
            polygon.addPoint(sex, sey);
            polygon.addPoint(swx, swy);
        }
        synchronized (center) {
            center.move(nwx, DrawUtils.lerp(nwy, sey, 0.5));
        }

        // Fill base
        g.setColor(getLandColor());
        g.fillPolygon(polygon);

        // If the southeastern corner is lower, south and east wall faces are always needed
        if (base.se > lowest.se) {
//            g.setColor(Color.CYAN);
//            g.fillRect(sex-4, sey-4, 8, 8);
//            g.setColor(getLandColor());

            // Southern wall face
            g.fillPolygon(new int[]{swx, swx, sex, sex}, new int[]{swy, swly, sely, sey}, 4);
            // Eastern wall face
            g.fillPolygon(new int[]{nex, nex, sex, sex}, new int[]{ney, nely, sely, sey}, 4);
        }

        // If being contested, draw moving diagonal lines
        if (contestor != null) {
            g.setColor(contestor.color);
            double cycle = (double)(System.currentTimeMillis()%GuiConstants.CONTEST_SHIFT_PERIOD)/GuiConstants.CONTEST_SHIFT_PERIOD;
            for (double i = GuiConstants.CONTESTED_STEP*cycle; i < 1; i += GuiConstants.CONTESTED_STEP) {
                // NW-NE-SW triangle
                g.drawLine(DrawUtils.lerp(nwx, nex, i), DrawUtils.lerp(nwy, ney, i), DrawUtils.lerp(nwx, swx, i), DrawUtils.lerp(nwy, swy, i));
                // SE-NE-SW triangle (Lerp backwards so lines move same direction)
                g.drawLine(DrawUtils.lerp(nex, sex, i), DrawUtils.lerp(ney, sey, i), DrawUtils.lerp(swx, sex, i), DrawUtils.lerp(swy, sey, i));
            }
        }

        // Northern and western border, always drawn
        g.setColor(owner!=null ? getColor() : Color.WHITE);
        g.drawLine(nwx, nwy, nex, ney); // northern border
        g.drawLine(nwx, nwy, swx, swy); // western border

        // southern border if both corners not shared on southern side
        if (base.sw > lowest.sw || base.se > lowest.se) {
            g.drawLine(swx, swy, sex, sey);
        }
        // eastern border if both corners not shared on eastern side
        if (base.ne > lowest.ne || base.se > lowest.se) {
            g.drawLine(nex, ney, sex, sey);
        }

        // northeast depth line if needed
        if ((base.ne > around.ne && !(GuiConstants.JOIN_SIDE_FACES && around.ne == -1)) || base.ne == around.ne && !GuiConstants.JOIN_SIDE_FACES) {
            g.drawLine(nex, ney, nex, GuiConstants.JOIN_SIDE_FACES ? neay : nely);
        }
        // southeast depth line if needed
        if ((base.se > around.se && !(GuiConstants.JOIN_SIDE_FACES && around.se == -1))) {
            g.drawLine(sex, sey, sex, seay);
        }
        // southwest height line if needed
        if ((base.sw > around.sw && !(GuiConstants.JOIN_SIDE_FACES && around.sw == -1)) || base.sw == around.sw && !GuiConstants.JOIN_SIDE_FACES) {
            g.drawLine(swx, swy, swx, GuiConstants.JOIN_SIDE_FACES ? sway : swly);
        }


        // If this is an edge tile, draw the lower borders (south and east)
        // southern ground line if needed
        if (lowest.sw == -1 && lowest.se == -1) {
            g.drawLine(swx, swly, sex, sely);
        }
        // eastern ground line if needed
        if (lowest.ne == -1 && lowest.se == -1) {
            g.drawLine(nex, nely, sex, sely);
        }
    }

    /** Determine if a given position on the screen is within this tile on the screen with the given zoom. **/
    public boolean containsPoint(int x, int y) {
        return polygon.contains(x, y);
    }

    /** Draw the borders for the base of this tile.
     * x and y are the screen coordinates to draw at.
     * nh, wh, sh and eh are the heights of the northern, western, southern and eastern tiles, respectively. **/
    public void drawBase(Graphics g, Point pos, double z){
        drawBase(g, pos.x, pos.y, z);
    }

    /** Draw UI elements associated with this tile. **/
    public void drawUI(Graphics g, double z) {
        if (beingContested()) {
            DrawUtils.drawBar(g, center.x, center.y, z,
                    (double)(System.currentTimeMillis()-contestStartTime)/GameConstants.CAPTURE_TIME, contestor.color);

            DrawUtils.drawCenteredString(g,
                    new Rectangle(center.x, center.y, 0, 0),
                    contestValue+"",
                    Color.WHITE,
                    TextElement.GAME_FONT_SMALL);
        } else if (hasUnit()) {
            unit.drawUI(g, this, z);
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
            return ColorUtils.blendColors(base, Color.WHITE, displayBrightness.doubleValue()*scale);
        } else {
            return ColorUtils.blendColors(base, Color.BLACK, -displayBrightness.doubleValue()*scale);
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
}
