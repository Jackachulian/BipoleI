package lib.engine;

import lib.ColorUtils;
import lib.GuiConstants;
import lib.timing.AnimatedValue;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/** Any tile on a battle's map.
 * Units may be placed on them,
 * and they can be claimed by a certain team. **/
public class Tile implements MapDrawable {
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

    public Tile(Battle battle, int row, int col) {
        this.battle = battle;
        this.row = row;
        this.col = col;

        type = rng.nextDouble() > 0.75 ? rng.nextInt(5) : 0;

        height = 21 - row - col*2 + (rng.nextDouble() > 0.25 ? 1 : 0);
//        height = rng.nextInt(8);

        int[] tc = TYPE_CORNERS[type];
        base = new Corners(height+tc[0], height+tc[1], height+tc[2], height+tc[3]);
        around = new Corners();
        lowest = new Corners();
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
    @Override
    public void draw(Graphics g, double x, double y, double z) {
        if (unit != null) unit.draw(g,x,y - z/2*height,z,this);
    }

    /** Draw the borders for the base of this tile. See drawBase(Graphics, Point, etc...) for details. **/
    public void drawBase(Graphics g, double x, double y, double z) {
        // Screen coordinates for corners
        int
                nwx = (int)x,
                nex = (int)(x + z* GuiConstants.ROW_X_OFFSET),
                swx = (int)(x + z* GuiConstants.COL_X_OFFSET),
                sex = (int)(x + z*(GuiConstants.ROW_X_OFFSET + GuiConstants.COL_X_OFFSET)), // ayo???
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


        // Fill base
        g.setColor(getLandColor());
        g.fillPolygon(new int[]{nwx, nex, sex, swx}, new int[]{nwy, ney, sey, swy}, 4);

        if (base.se > lowest.se) {
//            g.setColor(Color.CYAN);
//            g.fillRect(sex-4, sey-4, 8, 8);
//            g.setColor(getLandColor());

            // Southern face
            g.fillPolygon(new int[]{swx, swx, sex, sex}, new int[]{swy, swly, sely, sey}, 4);
            // Eastern face
            g.fillPolygon(new int[]{nex, nex, sex, sex}, new int[]{ney, nely, sely, sey}, 4);
        }


        // Northern and western border, always drawn
        g.setColor(owner!=null ? getColor() : Color.WHITE);
        g.drawLine(nwx, nwy, nex, ney); // northern border
        g.drawLine(nwx, nwy, swx, swy); // western border

        // southern border if corners not shared on southern side
        if (base.sw > lowest.sw || base.se > lowest.se) {
            g.drawLine(swx, swy, sex, sey);
        }
        // eastern border
        if (base.ne > lowest.ne || base.se > lowest.se) {
            g.drawLine(nex, ney, sex, sey);
        }

        // northeast height line if needed
        if ((base.ne > around.ne && !(GuiConstants.JOIN_SIDE_FACES && around.ne == -1)) || base.ne == around.ne && !GuiConstants.JOIN_SIDE_FACES) {
            g.drawLine(nex, ney, nex, GuiConstants.JOIN_SIDE_FACES ? neay : nely);
        }
        // southeast height line if needed
        if ((base.se > around.se && !(GuiConstants.JOIN_SIDE_FACES && around.se == -1))) {
            g.drawLine(sex, sey, sex, seay);
        }
        // southwest height line if needed
        if ((base.sw > around.sw && !(GuiConstants.JOIN_SIDE_FACES && around.sw == -1)) || base.sw == around.sw && !GuiConstants.JOIN_SIDE_FACES) {
            g.drawLine(swx, swy, swx, GuiConstants.JOIN_SIDE_FACES ? sway : swly);
        }

        // If being contested, draw moving diagonal lines
        if (contestor != null) {
            int SHIFT_SPEED = 1000;
            double hz = z/2;
            double az = (z/10);
            if (az < 1) az = 1;
            long cycle = System.nanoTime()%(SHIFT_SPEED*1000000);
            int yshift = (int)((double)cycle/(SHIFT_SPEED*1000000)*az);

            g.setColor(contestor.color);
            for (double yl = (nwy + yshift); yl < nwy + z; yl += az){
                if ((yl-nwy) < hz){
                    g.drawLine((int)(x - (yl-nwy)*2), (int)yl, (int)(x + (yl-nwy)*2), (int)yl);
                } else {
                    g.drawLine((int)(x - 2*z + (yl-nwy)*2), (int)yl, (int)(x + 2*z - (yl-nwy)*2), (int)yl);
                }
            }
        }
    }

    /** Draw the borders for the base of this tile.
     * x and y are the screen coordinates to draw at.
     * nh, wh, sh and eh are the heights of the northern, western, southern and eastern tiles, respectively. **/
    public void drawBase(Graphics g, Point pos, double z){
        drawBase(g, pos.x, pos.y, z);
    }

    /** Draw UI elements associated with this tile. **/
    public void drawUI() {

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
}
