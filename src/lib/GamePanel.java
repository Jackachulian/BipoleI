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
    /** Current zoom of camera. **/
    protected double zoom = 50.0;

    // ==== CURSOR
    /** Internal row position of cursor. **/
    protected int cursorRow;
    /** Internal col position of cursor. **/
    protected int cursorCol;
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
        setBackground(new Color(16,16,16));

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

        // Key listener setup
        super.getInputMap().put(KeyStroke.getKeyStroke("UP"), "up");
        super.getActionMap().put("up", new CursorWest());
        super.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "down");
        super.getActionMap().put("down", new CursorEast());
        super.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "left");
        super.getActionMap().put("left", new CursorNorth());
        super.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "right");
        super.getActionMap().put("right", new CursorSouth());
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
                map[r][c].drawBase(g, getScreenPos(r, c), zoom, c==map.length-1, r==map.length-1);
            }
        }

        // Draw the cursor
        g.setColor(Color.GREEN);
        DrawUtils.drawInsetTile(g, getScreenPos(showCursorRow.doubleValue(), showCursorCol.doubleValue()), zoom, 0.1);

        // Draw all tiles (Centered units)
        for (int r=0; r<map.length; r++){
            for (int c=0; c<map[r].length; c++){
                map[r][c].draw(g, getScreenPos(r+0.5, c+0.5), zoom);
            }
        }

        // Update and draw screen elements
        pointCounter.setText(player.getPoints()+" pts");
        drawElements(g);
    }

    // ==== POSITIONING
    /** Get the screen position of a coordinate on the map by row&column and height. **/
    public Point getScreenPos(double row, double col, double height){
        double relRow = row - cameraRow.doubleValue();
        double relCol = col - cameraCol.doubleValue();

        double x = (relRow*ROW_X_OFFSET + relCol*COL_X_OFFSET)*zoom + getWidth()/2.0;
        double y = (relRow*ROW_Y_OFFSET + relCol*COL_Y_OFFSET + height*HEIGHT_Y_OFFSET)*zoom + getHeight()/2.0;

        return new Point((int)x, (int)y);
    }

    /** Get the screen position of a coordinate on the map by row and column. (Zero height) **/
    public Point getScreenPos(double row, double col){
        return getScreenPos(row, col, 0);
    }

    /** Get the grid's row and column position of a given X and Y screen coordinate. **/
    public GridPoint getGridPos(int x, int y){
        double relX = (x - super.getWidth()/2.0) / zoom;
        double relY = (y - super.getHeight()/2.0) / zoom;

        double row = (relX/ROW_X_OFFSET + relY/ROW_Y_OFFSET)/2 + cameraRow.doubleValue();
        double col = (relX/COL_X_OFFSET + relY/COL_Y_OFFSET)/2 + cameraCol.doubleValue();

        return new GridPoint(row, col);
    }

    // ======== LISTENERS
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
        cameraRow = clickCameraRow - (dx/ROW_X_OFFSET + dy/ROW_Y_OFFSET) / 2 / zoom;
        cameraCol = clickCameraCol - (dx/COL_X_OFFSET + dy/COL_Y_OFFSET) / 2 / zoom;
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double scale = Math.pow(ZOOM_SCROLL_FACTOR, e.getWheelRotation());
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
        if (CAMERA_FOLLOW_CURSOR) {
            Point cameraPos = getScreenPos(cameraRow.doubleValue(), cameraCol.doubleValue());
            Point cursorPos = getScreenPos(cursorRow+0.5, cursorCol+0.5);
            int followXScreen = (int)(zoom*FOLLOW_X_MARGIN);
            int followYScreen = (int)(zoom*FOLLOW_Y_MARGIN);

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
        if (EASE_CAMERA){
            cameraRow = new AnimatedValue(CAMERA_SPEED, cameraRow.doubleValue(), pos.row);
            cameraCol = new AnimatedValue(CAMERA_SPEED, cameraCol.doubleValue(), pos.col);
        } else {
            cameraRow = pos.row;
            cameraCol = pos.col;
        }
    }

    // ==== KEY LISTENERS
    public void setCursor(int row, int col){
        if (row < 0 || row >= battle.numRows() || col < 0 || col >= battle.numCols()) return;
        cursorRow = row;
        cursorCol = col;
        showCursorRow = new AnimatedValue(CURSOR_SPEED, showCursorRow.doubleValue(), cursorRow);
        showCursorCol = new AnimatedValue(CURSOR_SPEED, showCursorCol.doubleValue(), cursorCol);
        moveCameraToCursor();
    }

    public void moveCursor(int dr, int dc){
        setCursor(cursorRow+dr, cursorCol+dc);
    }

    public class CursorNorth extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            moveCursor(0, -1);
        }
    }
    public class CursorSouth extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            moveCursor(0, 1);
        }
    }
    public class CursorWest extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            moveCursor(-1, 0);
        }
    }
    public class CursorEast extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            moveCursor(1, 0);
        }
    }

    // ======== CONSTANTS
    // ==== MAP DISPLAY
    public static final double
            ROW_X_OFFSET = -1.0,
            ROW_Y_OFFSET = 0.5,
            COL_X_OFFSET = 1.0,
            COL_Y_OFFSET = 0.5,
            HEIGHT_Y_OFFSET = -1.15;

    // ==== CURSOR
    public static final boolean EASE_CURSOR = true;
    public static final int CURSOR_SPEED = 150;

    // CAMERA
    public static final boolean EASE_CAMERA = true;
    public static final int CAMERA_SPEED = 250;
    public static final boolean CAMERA_FOLLOW_CURSOR = true;
    public static final double ZOOM_SCROLL_FACTOR = 0.8;
    public static final double FOLLOW_X_MARGIN = 2.0;
    public static final double FOLLOW_Y_MARGIN = 1.5;
}
