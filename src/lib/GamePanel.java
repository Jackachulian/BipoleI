package lib;

import lib.elementboxes.ElementBox;
import lib.elementboxes.PointCounterElement;
import lib.elementboxes.ShopElement;
import lib.elementboxes.TextElement;
import lib.engine.Battle;
import lib.engine.Player;
import lib.engine.Shop;
import lib.engine.Tile;
import lib.timing.AnimatedValue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends ElementPanel {
    // ======== FIELDS
    /** The battle displayed on this panel. **/
    protected Battle battle;
    /** The player in this client of the battle. **/
    protected Player player;

    // ==== CAMERA
    /** X position of camera. (Relative to northwestern corner of map) **/
    protected Number cameraX = 0;
    /** Y position of camera. (Relative to northwestern corner of map) **/
    protected Number cameraY = 0;
    /** Current zoom of camera. **/
    protected double zoom = 50.0;

    // ==== CURSOR
    /** Internal row position of cursor. **/
    protected int cursorRow;
    /** Internal col position of cursor. **/
    protected int cursorCol;
    /** The row and column of the tile the cursor is moving from. Used in displaying. **/
    protected int fromCursorRow, fromCursorCol;
    /** Displayed row position of cursor. **/
    protected Number showCursorRow = 0;
    /** Displayed col position of cursor. **/
    protected Number showCursorCol = 0;
    /** Animator for cursor (row only), the isAnimating is checked for this if cursor ease is on. **/
    protected AnimatedValue cursorAnimator = new AnimatedValue(0);
    /** Displayed corner heights of the cursor. **/
    protected final NumberCorners cursorCorners = new NumberCorners();

    /** Elementbox that controls the shop menu. **/
    ElementBox shop;
    /** Element that displays current points. **/
    TextElement pointCounter;

    // ======== CONSTRUCTOR
    public GamePanel(Battle battle, Player player) {
        super(new ElementBox(){

        });
        this.battle = battle;
        this.player = player;
        setBackground(new Color(64,64,64));

        // Initialize screen refresh timer
        ActionListener updateScreen = evt -> super.repaint();
        Timer screenRefreshTimer = new Timer(20, updateScreen);
        screenRefreshTimer.setCoalesce(false);
        screenRefreshTimer.start();

        // Create elements
        shop = new ShopElement(Shop.defaultShop(player));
        addElement(shop);
        pointCounter = new PointCounterElement();
        addElement(pointCounter);

        resizeElements();   // Sets the size of all elements to where they need to be

        // Mouse listener setup
        super.addMouseListener(this);
        super.addMouseMotionListener(this);
        super.addMouseWheelListener(this);

        // Input mapping
        super.getInputMap().put(KeyStroke.getKeyStroke("UP"), "west");
        super.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "east");
        super.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "north");
        super.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "south");

        super.getInputMap().put(KeyStroke.getKeyStroke("Z"), "interact");
        super.getInputMap().put(KeyStroke.getKeyStroke("X"), "cancel");


        // Action mapping
        super.getActionMap().put("west", new CursorMove(-1,0));
        super.getActionMap().put("east", new CursorMove(1,0));
        super.getActionMap().put("north", new CursorMove(0,-1));
        super.getActionMap().put("south", new CursorMove(0,1));

        super.getActionMap().put("interact", new CursorInteract());
        super.getActionMap().put("cancel", new CursorCancel());
    }

    // ==== KEY LISTENERS
    public class CursorMove extends AbstractAction {
        final int row, col;

        public CursorMove(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (rootSelected()) {
                moveCursor(row, col);
            }
        }
    }

    public class CursorInteract extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (rootSelected()) {
                mapInteract(cursorRow, cursorCol);
            } else {

            }
        }
    }

    public class CursorCancel extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            onCancel();
        }
    }

    // ======== METHODS
    // ==== MAIN DRAW
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw all tiles' bases and unit meshes
        Tile[][] map = battle.getMap();
        for (int r=0; r<map.length; r++){
            for (int c=0; c<map[r].length; c++){
                map[r][c].drawBase(g, getScreenPos(r, c), zoom);

                // Draw cursor if under this tile or the target tile
                if ((r == cursorRow && c == cursorCol) ||
                        (GuiConstants.EASE_CURSOR && cursorAnimator.isRunning() && r == Math.max(cursorRow, fromCursorRow) && c == Math.max(cursorCol, fromCursorCol))) {
                    g.setColor(Color.GREEN);
                    DrawUtils.drawInsetTile(g, getScreenPos(showCursorRow.doubleValue(), showCursorCol.doubleValue()), zoom, cursorCorners, 0.1);
                }

                map[r][c].draw(g);
            }
        }

        // Draw all units' UI, even if it would be obscured by a tile in front of it
        for (Tile[] tiles : map) {
            for (Tile tile : tiles) {
                tile.drawUI(g, zoom);
            }
        }

        // Update and draw screen elements
        pointCounter.setText(player.getPoints()+" pts");
        drawElements(g);
    }

//    package lib.engine;
//
//import java.awt.*;
//
//    /** Anything that can be drawn with a Graphics object g at (x, y) with z zoom. **/
//    public interface MapDrawable {
//        /**
//         * Draw this object at the given location with the passed graphics object.
//         * @param g the Graphics instance
//         * @param x X position of top corner of tile (northwestern corner)
//         * @param y Y position of top corner of tile (northwestern corner)
//         * @param z Amount of zoom to draw with (Zoom is equal to the amount of pixels from the top to bottom corner)
//         */
//        void draw(Graphics g, double x, double y, double z);
//
//        default void draw(Graphics g, Point pos, double z){
//            draw(g, pos.x, pos.y, z);
//        }
//    }


    // ==== POSITIONING
    /** Get the screen position of a coordinate on the map by row&column&depth and height. **/
    public Point getScreenPos(double row, double col, double depth, double height){
        double x = zoom*(row*GuiConstants.ROW_X_OFFSET + col*GuiConstants.COL_X_OFFSET)
                + getWidth()/2.0 - cameraX.doubleValue();
        double y = zoom*(row*GuiConstants.ROW_Y_OFFSET + col*GuiConstants.COL_Y_OFFSET - depth*GuiConstants.DEPTH_Y_OFFSET + height*GuiConstants.HEIGHT_Y_OFFSET)
                + getHeight()/2.0 - cameraY.doubleValue();

        return new Point((int)x, (int)y);
    }

    /** Get the screen position of a coordinate on the map by row, column and depth. (Zero depth) **/
    public Point getScreenPos(double row, double col, double depth){
        return getScreenPos(row, col, depth, 0);
    }

    /** Get the screen position of a coordinate on the map by row & column. (Zero depth & height) **/
    public Point getScreenPos(double row, double col){
        return getScreenPos(row, col, 0);
    }

    /** Get the grid's row and column position of a given X and Y screen coordinate.
     * Checks each tile's polygon to see if the mouse tile is contained by the polygon.
     * @return the position of the tile; null if no tile was clicked **/
    public Point getGridPos(int x, int y){
        // Iterate over the map in reverse order, so tiles covered up by other tiles aren't selected in place of the one that is actually clicked on the screen.
        // A tile located before another tile in row-major order will never overlap it
        for (int r = battle.numRows()-1; r >= 0; r--) {
            for (int c = battle.numCols()-1; c >= 0; c--) {
                if (battle.getTile(r, c).containsPoint(x, y)) {
                    return new Point(r, c);
                }
            }
        }
        return null;
    }

    // ==== MOUSE LISTENERS
    /** The last point the screen was clicked. **/
    private Point clickPoint;
    /** The X and Y position of the camera when the screen was clicked. **/
    private double clickCameraX, clickCameraY;

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        if (rootSelected()) {
            Point clickGridPos = getGridPos(e.getX(), e.getY());
            if (clickGridPos == null) return;
            setCursor(clickGridPos.x, clickGridPos.y);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (rootSelected()) {
            clickPoint = e.getPoint();
            clickCameraX = cameraX.doubleValue();
            clickCameraY = cameraY.doubleValue();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (rootPressed()) {
            int dx = e.getX() - clickPoint.x;
            int dy = e.getY() - clickPoint.y;
            cameraX = clickCameraX - dx;
            cameraY = clickCameraY - dy;
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double scale = Math.pow(GuiConstants.ZOOM_SCROLL_FACTOR, e.getWheelRotation());
        zoom *= scale;
//
//        double xDistance = e.getX() - cameraX.doubleValue();
//        double yDistance = e.getY() - cameraY.doubleValue();
//
//        cameraX = e.getX() - xDistance*scale;
//        cameraY = e.getY() - yDistance*scale;

        cameraX = (cameraX.doubleValue() + e.getX() - getWidth()/2.0) * scale - e.getX() + getWidth()/2.0;
        cameraY = (cameraY.doubleValue() + e.getY() - getHeight()/2.0) * scale - e.getY() + getHeight()/2.0;
    }

    // ==== CAMERA
    /** Fit the cursor inside the camera screen, fitting inside certain margins from the edge of the screen.
     * Moves the cursor to the closest point where it will be inside these margins, if it is not already. **/
    public void moveCameraToCursor(){
        if (GuiConstants.CAMERA_FOLLOW_CURSOR) {
            Point cursorPos = getScreenPos(cursorRow+0.5, cursorCol+0.5, battle.getTile(cursorRow, cursorCol).getHeight());

            int x = cameraX.intValue();
            int y = cameraY.intValue();

            int followXScreen = (int)(zoom*GuiConstants.FOLLOW_X_MARGIN);
            int followYScreen = (int)(zoom*GuiConstants.FOLLOW_Y_MARGIN);

            boolean cameraMoved = false;

            if (cursorPos.x < followXScreen){
                x += cursorPos.x - followXScreen; cameraMoved = true;
            } else if (cursorPos.x > super.getWidth() - followXScreen - shop.innerWidth()){
                x += cursorPos.x - super.getWidth() + followXScreen + shop.innerWidth(); cameraMoved = true;
            }

            if (cursorPos.y < followYScreen) {
                y += cursorPos.y - followYScreen; cameraMoved = true;
            } else if (cursorPos.y > super.getHeight() - followYScreen) {
                y += cursorPos.y - super.getHeight() + followYScreen; cameraMoved = true;
            }

            if (cameraMoved) moveCameraToScreenPoint(x, y);
        }
    }

    public void moveCameraToScreenPoint(int x, int y){
        if (GuiConstants.EASE_CAMERA){
            cameraX = new AnimatedValue(GuiConstants.CAMERA_SPEED, cameraX.doubleValue(), x);
            cameraY = new AnimatedValue(GuiConstants.CAMERA_SPEED, cameraY.doubleValue(), y);
        } else {
            cameraX = x;
            cameraY = y;
        }
    }

    // ==== CURSOR
    public void setCursor(int row, int col){
        if (row < 0 || row >= battle.numRows() || col < 0 || col >= battle.numCols()) return;
        battle.getTile(cursorRow, cursorCol).onCursorUnhover();

        if (!cursorAnimator.isRunning()) {
            fromCursorRow = cursorRow;
            fromCursorCol = cursorCol;
        }

        cursorRow = row;
        cursorCol = col;
        Tile hoveredTile = battle.getTile(cursorRow, cursorCol);

        if (GuiConstants.EASE_CURSOR) {
            cursorAnimator = new AnimatedValue(GuiConstants.CURSOR_SPEED, showCursorRow.doubleValue(), cursorRow);
            showCursorRow = cursorAnimator;
            showCursorCol = new AnimatedValue(GuiConstants.CURSOR_SPEED, showCursorCol.doubleValue(), cursorCol);
            cursorCorners.easeTo(hoveredTile.getBase());
        } else {
            showCursorRow = new AnimatedValue(cursorRow);
            showCursorCol = new AnimatedValue(cursorCol);
            cursorCorners.set(hoveredTile.getBase());
        }

        hoveredTile.onCursorHover();
        moveCameraToCursor();
    }

    public void moveCursor(int dr, int dc){
        setCursor(cursorRow+dr, cursorCol+dc);
    }

    // ==== BATTLE INTERACTION

    /** Interact with a tile on the board.
     * If the tile is unclaimed and uncontested, start contesting it.
     * If the tile is being contested, counter-contest it.
     * If the tile is your empty land, opens the shop.
     * If the tile is one of your units, act with that unit if it is ready.
     * @param row tile's row (west-east)
     * @param col tile's column (north-south)
     */
    public void mapInteract(int row, int col){
        Tile tile = battle.getTile(row, col);
        System.out.println("interacted with "+tile);

        if (tile.isClaimed()){
            // do claimed stuff...?
        } else {
            tile.contest(player);
        }
    }

}
