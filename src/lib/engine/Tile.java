package lib.engine;

import lib.GamePanel;

import javax.swing.*;
import java.awt.*;

/** Any tile on a battle's map.
 * Units may be placed on them,
 * and they can be claimed by a certain team. **/
public class Tile implements MapDrawable {
    /** The player that owns this tile (the color that is displayed on the ground). Null is no owner. **/
    protected Player owner;
    /** The unit that is on this tile. Several units can be on land now owned by the unit's owner. Null is no unit. **/
    protected Unit unit;

    // Contesting
    /** The player contesting this tile, if it is unclaimed. **/
    protected Player contestor;
    /** The point value accumulated with this tile during contesting. **/
    protected int contestValue;
    /** The timer that controls the delay for contesting and claiming this tile. **/
    private Timer contestTimer;
    /** Time in milliseconds when this tile began to be contested. **/
    private long contestStartTime;


    /** Create a new empty tile with no owner. **/
    public Tile() {

    }

    /** Contest this tile by a player. **/
    public void contest(Player contestor){
        if (!contestor.subtractPoints(contestValue+1)) return;
        contestValue += 1;
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

    @Override
    public void draw(Graphics g, double x, double y, double z) {
        if (unit != null) unit.draw(g,x,y,z);
    }

    /** Draw this tile's borders with boolean array.
     * sb determines if south border should be drawn (if drawing the last row).
     * eb determines if east border should be drawn (if drawing the last column). **/
    public void drawBase(Graphics g, double x, double y, double z, boolean sb, boolean eb){
        // Screen coordinates for corners
        int nwx = (int)x;
        int nex = (int)(x + z* GamePanel.ROW_X_OFFSET);
        int swx = (int)(x + z*GamePanel.COL_X_OFFSET);
        int sex = (int)(x + z*(GamePanel.ROW_X_OFFSET + GamePanel.COL_X_OFFSET));
        int nwy = (int)y;
        int ney = (int)(y + z*GamePanel.ROW_Y_OFFSET);
        int swy = (int)(y + z*GamePanel.COL_Y_OFFSET);
        int sey = (int)(y + z*(GamePanel.ROW_Y_OFFSET + GamePanel.COL_Y_OFFSET));

        // Fill base color
       if (owner != null){
           int[] xPoints = {nwx, nex, sex, swx};
           int[] yPoints = {nwy, ney, sey, swy};

           g.setColor(owner.landColor);
           g.fillPolygon(xPoints, yPoints, 4);
       }

        // Border
        g.setColor(owner!=null ? owner.color : Color.WHITE);
        g.drawLine(nwx, nwy, nex, ney); // northern border
        g.drawLine(nwx, nwy, swx, swy); // western border
        if (sb) g.drawLine(swx, swy, sex, sey); // southern border
        if (eb) g.drawLine(nex, ney, sex, sey); // eastern border
    }

    public void drawBase(Graphics g, Point pos, double z, boolean sb, boolean eb){
        drawBase(g, pos.x, pos.y, z, sb, eb);
    }
}
