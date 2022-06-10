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
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends ElementPanel implements MouseInputListener, MouseMotionListener, MouseWheelListener {
    // ======== FIELDS
    /** The battle displayed on this panel. **/
    protected Battle battle;
    /** The player in this client of the battle. **/
    protected Player player;

    // ==== CAMERA
    /** Row position of camera. **/
    protected Number cameraRow = 0;
    /** Col position of camera. **/
    protected Number cameraCol = 0;
    /** Depth position of camera. **/
    protected Number cameraDepth = 0;
    /** Current zoom of camera. **/
    protected double zoom = 50.0;

    // ==== CURSOR
    /** Internal row position of cursor. **/
    protected int cursorRow;
    /** Internal col position of cursor. **/
    protected int cursorCol;
    /** Internal depth position of cursor. **/
    protected int cursorDepth;
    /** Displayed row position of cursor. **/
    protected Number showCursorRow = 0;
    /** Displayed col position of cursor. **/
    protected Number showCursorCol = 0;

    /** Elementbox that controls the shop menu. **/
    ElementBox shop;
    /** Element that displays current points. **/
    TextElement pointCounter;

    // ======== CONSTRUCTOR
    public GamePanel(Battle battle, Player player) {
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
        resizeElements();

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


        // Action mapping
        super.getActionMap().put("west", new CursorMove(-1,0));
        super.getActionMap().put("east", new CursorMove(1,0));
        super.getActionMap().put("north", new CursorMove(0,-1));
        super.getActionMap().put("south", new CursorMove(0,1));

        super.getActionMap().put("interact", new CursorInteract());
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
            moveCursor(row, col);
        }
    }

    public class CursorInteract extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            interact(cursorRow, cursorCol);
        }
    }


    // ======== METHODS
    // ==== MAIN DRAW
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw all tiles' bases
        Tile[][] map = battle.getMap();
        for (int r=0; r<map.length; r++){
            for (int c=0; c<map[r].length; c++){
                map[r][c].drawBase(g, getScreenPos(r, c), zoom);

                map[r][c].draw(g, getScreenPos(r+0.5, c+0.5), zoom);
            }
        }

        // Draw the cursor
        g.setColor(Color.GREEN);
        DrawUtils.drawInsetTile(g, getScreenPos(showCursorRow.doubleValue(), showCursorCol.doubleValue()), zoom, 0.1);

        // Update and draw screen elements
        pointCounter.setText(player.getPoints()+" pts");
        drawElements(g);
    }

    // ==== POSITIONING
    /** Get the screen position of a coordinate on the map by row&column&depth and height. **/
    public Point getScreenPos(double row, double col, double depth, double height){
        double relRow = row - cameraRow.doubleValue();
        double relCol = col - cameraCol.doubleValue();
        double relDepth = depth = cameraDepth.doubleValue();

        double x = zoom*(relRow*GuiConstants.ROW_X_OFFSET + relCol*GuiConstants.COL_X_OFFSET) + getWidth()/2.0;
        double y = zoom*(relRow*GuiConstants.ROW_Y_OFFSET + relCol*GuiConstants.COL_Y_OFFSET + relDepth*GuiConstants.DEPTH_Y_OFFSET + height*GuiConstants.HEIGHT_Y_OFFSET) + getHeight()/2.0;

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

    /** Get the grid's row and column position of a given X and Y screen coordinate. **/
    public GridPoint getGridPos(int x, int y){
        double relX = (x - super.getWidth()/2.0) / zoom;
        double relY = (y - super.getHeight()/2.0) / zoom;

        double row = (relX/GuiConstants.ROW_X_OFFSET + relY/ GuiConstants.ROW_Y_OFFSET)/2 + cameraRow.doubleValue();
        double col = (relX/GuiConstants.COL_X_OFFSET + relY/ GuiConstants.COL_Y_OFFSET)/2 + cameraCol.doubleValue();

        return new GridPoint(row, col);
    }

    // ==== MOUSE LISTENERS
    /** The last point the screen was clicked. **/
    private Point clickPoint;
    /** The grid position of the camera when the screen was clicked. **/
    private double clickCameraRow, clickCameraCol;
    @Override
    public void mouseClicked(MouseEvent e) {
        GridPoint clickGridPos = getGridPos(e.getX(), e.getY());
        setCursor((int)clickGridPos.row, (int)clickGridPos.col);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        clickPoint = e.getPoint();
        clickCameraRow = cameraRow.doubleValue();
        clickCameraCol = cameraCol.doubleValue();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int dx = e.getX() - clickPoint.x;
        int dy = e.getY() - clickPoint.y;
        cameraRow = clickCameraRow - (dx/GuiConstants.ROW_X_OFFSET + dy/GuiConstants.ROW_Y_OFFSET) / 2 / zoom;
        cameraCol = clickCameraCol - (dx/GuiConstants.COL_X_OFFSET + dy/GuiConstants.COL_Y_OFFSET) / 2 / zoom;
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double scale = Math.pow(GuiConstants.ZOOM_SCROLL_FACTOR, e.getWheelRotation());
        zoom *= scale;

        GridPoint pos = getGridPos(super.getWidth() - e.getX(), super.getHeight() - e.getY());

        double rowDistance = pos.row - cameraRow.doubleValue();
        double colDistance = pos.col - cameraCol.doubleValue();

        cameraRow = pos.row - rowDistance*scale;
        cameraCol = pos.col - colDistance*scale;
    }

    // ==== CAMERA
    /** Fit the cursor inside the camera screen, fitting inside certain margins from the edge of the screen.
     * Moves the cursor to the closest point where it will be inside these margins, if it is not already. **/
    public void moveCameraToCursor(){
        if (GuiConstants.CAMERA_FOLLOW_CURSOR) {
            Point cameraPos = getScreenPos(cameraRow.doubleValue(), cameraCol.doubleValue(), cameraDepth.doubleValue());
            Point cursorPos = getScreenPos(cursorRow+0.5, cursorCol+0.5, cursorDepth+0.5);
            int followXScreen = (int)(zoom* GuiConstants.FOLLOW_X_MARGIN);
            int followYScreen = (int)(zoom* GuiConstants.FOLLOW_Y_MARGIN);

            boolean cameraMoved = false;

            if (cursorPos.x < followXScreen){
                cameraPos.x += cursorPos.x - followXScreen; cameraMoved = true;
            } else if (cursorPos.x > super.getWidth() - followXScreen - shop.innerWidth()){
                cameraPos.x += cursorPos.x - super.getWidth() + followXScreen + shop.innerWidth(); cameraMoved = true;
            }

            if (cursorPos.y < followYScreen) {
                cameraPos.y += cursorPos.y - followYScreen; cameraMoved = true;
            } else if (cursorPos.y > super.getHeight() - followYScreen) {
                cameraPos.y += cursorPos.y - super.getHeight() + followYScreen; cameraMoved = true;
            }

            if (cameraMoved) moveCameraToScreenPoint(cameraPos.x, cameraPos.y);
        }
    }

    public void moveCameraToScreenPoint(int x, int y){
        GridPoint pos = getGridPos(x, y);
        if (GuiConstants.EASE_CAMERA){
            cameraRow = new AnimatedValue(GuiConstants.CAMERA_SPEED, cameraRow.doubleValue(), pos.row);
            cameraCol = new AnimatedValue(GuiConstants.CAMERA_SPEED, cameraCol.doubleValue(), pos.col);
        } else {
            cameraRow = pos.row;
            cameraCol = pos.col;
        }
    }

    // ==== CURSOR
    public void setCursor(int row, int col){
        if (row < 0 || row >= battle.numRows() || col < 0 || col >= battle.numCols()) return;
        battle.getTile(cursorRow, cursorCol).onCursorUnhover();
        cursorRow = row;
        cursorCol = col;

        if (GuiConstants.EASE_CURSOR) {
            showCursorRow = new AnimatedValue(GuiConstants.CURSOR_SPEED, showCursorRow.doubleValue(), cursorRow);
            showCursorCol = new AnimatedValue(GuiConstants.CURSOR_SPEED, showCursorCol.doubleValue(), cursorCol);
        } else {
            showCursorRow = cursorRow;
            showCursorCol = cursorCol;
        }

        battle.getTile(cursorRow, cursorCol).onCursorHover();
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
    public void interact(int row, int col){
        Tile tile = battle.getTile(row, col);
        System.out.println("interacted with "+tile);

        if (tile.isClaimed()){
            // do claimed stuff...?
        } else {
            tile.contest(player);
        }
    }

}
