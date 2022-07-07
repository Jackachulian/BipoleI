package lib;

import lib.data.Units;
import lib.elementboxes.*;
import lib.elementboxes.ShopElement;
import lib.elementboxes.ShopItemElement;
import lib.engine.*;
import lib.geometry.Shape;
import lib.timing.AnimatedValue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;

public class GamePanel extends ElementPanel {
    // ======== FIELDS
    /** The battle displayed on this panel. **/
    protected Battle battle;
    /** The player in this client of the battle. **/
    protected Player player;

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
    /** Element that shows info about the current unit. **/
    InfoElement info;

    /** Contains the mesh blueprint being drawn on the screen. Set when mouse pressed if the pressed element is a ShopItemElement. **/
    ShopItemElement cursorDragItem;
    /** Polygon for unit being bought to be drawn. Set when mouse dragged if the pressed element is a ShopItemElement. **/
    Polygon buyPolygon;
    /** The current row and column the mesh blueprint is being displayed on, and where it will be placed when the mouse is released. **/
    int buyRow = -1, buyCol = -1;

    // ======== CONSTRUCTOR
    public GamePanel(Battle battle, Player player) {
        super(new RootElement(){

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
        info = new InfoElement(root);
        addElement(info);

        resizeElements();   // Sets the size of all elements to where they need to be
        updateMouseOvers(root);     // Initializes set of elements that need to be checked if mouse is over them
        System.out.println(checkMouseOver);

        // Mouse listener setup
        super.addMouseListener(this);
        super.addMouseMotionListener(this);
        super.addMouseWheelListener(this);

        // Input mapping
        super.getInputMap().put(KeyStroke.getKeyStroke("UP"), "north");
        super.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "south");
        super.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "west");
        super.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "east");

        super.getInputMap().put(KeyStroke.getKeyStroke("4"), "rotateLeft");
        super.getInputMap().put(KeyStroke.getKeyStroke("6"), "rotateRight");

        super.getInputMap().put(KeyStroke.getKeyStroke("Z"), "interact");
        super.getInputMap().put(KeyStroke.getKeyStroke("X"), "cancel");


        // Action mapping
        super.getActionMap().put("west", new CursorMove(-1,0));
        super.getActionMap().put("east", new CursorMove(1,0));
        super.getActionMap().put("north", new CursorMove(0,-1));
        super.getActionMap().put("south", new CursorMove(0,1));

        super.getActionMap().put("interact", new CursorInteract());
        super.getActionMap().put("cancel", new CursorCancel());

        super.getActionMap().put("rotateLeft", new RotateLeft());
        super.getActionMap().put("rotateRight", new RotateRight());

        // Find the castle and move the cursor there
        cursorToCastle();

        // Set camera to display this panel
        Camera.initialize(this);
    }

    // ======== METHODS
    // ==== MAIN DRAW
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Camera.refresh();

        // Draw all tiles' bases and unit meshes
        for (Tile tile : battle){
            tile.drawBase(g, getScreenPos(tile.row, tile.col));

            // Draw cursor if under the selected tile or the tile the animated cursor is coming from
            if ((tile.row == cursorRow && tile.col == cursorCol) ||
                    (GuiConstants.EASE_CURSOR && cursorAnimator.isAnimating() && tile.row == Math.max(cursorRow, fromCursorRow) && tile.col == Math.max(cursorCol, fromCursorCol))) {
                g.setColor(Color.GREEN);
                DrawUtils.drawInsetTile(g, getScreenPos(showCursorRow.doubleValue(), showCursorCol.doubleValue()), cursorCorners, 0.1);
            }

            tile.drawUnit(g);
        }

        // draw blueprint for unit being placed
        if (cursorDragItem != null) {
            boolean showInvalid = buyRow != -1 && (buyBlueprintTile().getOwner() != player || buyBlueprintTile().hasUnit());
            Color lineColor = showInvalid ? Colors.INVALID : player.colorPlace;
            Color faceColor = showInvalid ? Colors.INVALID_FACE : player.faceColorPlace;

            cursorDragItem.item.getMesh().draw(g, buyPolygon, Corners.FLAT, lineColor, faceColor, Camera.zoom);
        }

        // Draw all units' UI after bases and units drawn, even if it would be obscured by a tile in front of it
        for (Tile tile : battle) {
            tile.drawUI(g);
        }

        // Update and draw screen elements
        pointCounter.setText(player.getPoints()+" pts");
        drawElements(g);

//        g.setColor(Color.MAGENTA);
//        g.drawLine(Camera.originOffset.x-5, Camera.originOffset.y, Camera.originOffset.x+5, Camera.originOffset.y);
//        g.drawLine(Camera.originOffset.x, Camera.originOffset.y-5, Camera.originOffset.x, Camera.originOffset.y+5);
    }

    // ==== POSITIONING
    /** Get the screen position of a coordinate on the map by row&column&depth and height. **/
    public Point getScreenPos(double row, double col, double depth, double height){
        if (Camera.reverseRows) row = battle.numRows() - row - 1;
        if (Camera.reverseCols) col = battle.numCols() - col - 1;
        if (Camera.swapAxes) {
            double temp = row;
            row = col;
            col = temp;
        }

        double x = Camera.zoom *(row*Camera.rowXOffset + col*Camera.colXOffset)
                + getWidth()/2.0 - Camera.cameraX.doubleValue();
        double y = Camera.zoom *(row*Camera.rowYOffset + col*Camera.colYOffset - depth*Camera.DEPTH_Y_OFFSET + height*Camera.HEIGHT_Y_OFFSET)
                + getHeight()/2.0 - Camera.cameraY.doubleValue();

        return new Point((int)x, (int)y);
    }

    /** Get the screen position of a coordinate on the map by row, column and depth. (Zero height) **/
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
        for (Iterator<Tile> it = battle.reverseDrawOrder(); it.hasNext(); ) {
            Tile tile = it.next();
            if (tile.containsPoint(x, y)) {
                return new Point(tile.row, tile.col);
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
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        // if root is pressed, get the mouse position for panning
        if (rootSelected()) {
            clickPoint = e.getPoint();
            clickCameraX = Camera.cameraX.doubleValue();
            clickCameraY = Camera.cameraY.doubleValue();
        }

        // else; check if the pressed item is a ShopItemElement; if so, save it in field and create a polygon
        else {
            if (selectedElement instanceof ShopItemElement) {
                cursorDragItem = (ShopItemElement) selectedElement;

                buyPolygon = Shape.tilePolygon(e.getX(), (int) (e.getY() - Camera.zoom /2), Camera.zoom);
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        // if root pressed, pan screen
        if (rootSelected()) {
            int dx = e.getX() - clickPoint.x;
            int dy = e.getY() - clickPoint.y;
            Camera.cameraX = clickCameraX - dx;
            Camera.cameraY = clickCameraY - dy;
        }

        // otherwise, if a shopItemElement is pressed, make a polygon
        else {
            if (cursorDragItem != null && !shop.rect.contains(e.getPoint())) {
                // TODO: check if mouse intersects a tile, draw a virtual unit there with valid/invalid if so, otherwise draw dimmed
                if (!blueprintMapDisplayed() || !buyBlueprintTile().containsPoint(e.getX(), e.getY())) {
                    Point gridPos = getGridPos(e.getX(), e.getY());
                    if (gridPos != null) {
                        buyRow = gridPos.x;
                        buyCol = gridPos.y;
                        buyPolygon = battle.getTile(gridPos.x, gridPos.y).getPolygon();
                    } else {
                        buyRow = -1;
                        buyCol = -1;
                        buyPolygon = Shape.tilePolygon(e.getX(), (int) (e.getY() - Camera.zoom/2), Camera.zoom);
                    }
                }
            }
        }
    }

    public boolean blueprintMapDisplayed() {
        return buyRow != -1;
    }

    public Tile buyBlueprintTile() {
        return battle.getTile(buyRow, buyCol);
    }

    public Tile cursorTile() {
        return battle.getTile(cursorRow, cursorCol);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if (cursorDragItem != null
                && battle.withinBounds(buyRow, buyCol)
                && buyBlueprintTile().getOwner() == player
                && !buyBlueprintTile().hasUnit()) {
            buyItem(cursorDragItem.item, buyBlueprintTile());
        }
        cursorDragItem = null;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double scale = Math.pow(GuiConstants.ZOOM_SCROLL_FACTOR, e.getWheelRotation());
        Camera.zoom = Camera.zoom * scale;
//
//        double xDistance = e.getX() - cameraX.doubleValue();
//        double yDistance = e.getY() - cameraY.doubleValue();
//
//        cameraX = e.getX() - xDistance*scale;
//        cameraY = e.getY() - yDistance*scale;

        Camera.cameraX = (Camera.cameraX.doubleValue() + e.getX() - getWidth() / 2.0) * scale - e.getX() + getWidth() / 2.0;
        Camera.cameraY = (Camera.cameraY.doubleValue() + e.getY() - getHeight() / 2.0) * scale - e.getY() + getHeight() / 2.0;
    }

    // ==== CAMERA
    /** Fit the cursor inside the camera screen, fitting inside certain margins from the edge of the screen.
     * Moves the cursor to the closest point where it will be inside these margins, if it is not already. **/
    public void moveCameraToCursor(){
        if (GuiConstants.CAMERA_FOLLOW_CURSOR) {
            Point cursorPos = getScreenPos(cursorRow+0.5, cursorCol+0.5, battle.getTile(cursorRow, cursorCol).depth);

            int x = Camera.cameraX.intValue();
            int y = Camera.cameraY.intValue();

            int followXScreen = (int)(Camera.zoom *GuiConstants.FOLLOW_X_MARGIN);
            int followYScreen = (int)(Camera.zoom *GuiConstants.FOLLOW_Y_MARGIN);

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
            Camera.cameraX = new AnimatedValue(GuiConstants.CAMERA_SPEED, Camera.cameraX.doubleValue(), x);
            Camera.cameraY = new AnimatedValue(GuiConstants.CAMERA_SPEED, Camera.cameraY.doubleValue(), y);
        } else {
            Camera.cameraX = x;
            Camera.cameraY = y;
        }
    }

    // ==== CURSOR
    public void setCursor(int row, int col){
        if (row < 0 || row >= battle.numRows() || col < 0 || col >= battle.numCols()) return;
        battle.getTile(cursorRow, cursorCol).onCursorUnhover();

        if (!cursorAnimator.isAnimating()) {
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
        info.setTile(player, hoveredTile);
        moveCameraToCursor();
    }

    public void moveCursor(int dr, int dc){
        if (Camera.swapAxes) {
            int temp = dr;
            dr = dc;
            dc = temp;
        }
        if (Camera.reverseRows) dr = -dr;
        if (Camera.reverseCols) dc = -dc;
        setCursor(cursorRow+dr, cursorCol+dc);
    }

    /** Find the player's castle and move the cursor there. **/
    public void cursorToCastle() {
        for (int r=0; r<battle.numRows(); r++) {
            for (int c=0; c<battle.numCols(); c++) {
                Tile tile = battle.getTile(r, c);
                if (tile.hasUnit() && tile.getUnit().getData() == Units.CASTLE && tile.getOwner() == player) {
                    setCursor(r, c);
                    return;
                }
            }
        }
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
    public void mapInteract(int row, int col) {
        Tile tile = battle.getTile(row, col);
        System.out.println("interacted with "+tile);

        // If tile is claimed
        if (tile.isClaimed()) {
            // If there is a unit here, TODO: open its menu
            if (tile.hasUnit()) {

            }

            // If there is no unit, focus the shop window
            else {
                focusElement(shop);
            }
        }

        // If not claimed, try to contest it
        else {
            tile.contest(player);
        }
    }

    /** Buy an item for the given tile. **/
    public void buyItem(Buyable item, Tile tile) {
        if (player.subtractPoints(item.buyCost())) {
            if (item instanceof UnitData) {
                tile.placeUnit((UnitData) item);
                focusElement(root);
                selectElement(root);
            }
        }
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
            } else {
                onMove(row, col);
            }
        }
    }

    public class CursorInteract extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (rootSelected()) {
                mapInteract(cursorRow, cursorCol);
            } else if (selectedElement instanceof ShopItemElement) {
                buyItem(((ShopItemElement) selectedElement).item, cursorTile());
            } else {
                onInteract();
            }
        }
    }

    public class CursorCancel extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            onCancel();
        }
    }

    public class RotateLeft extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            Camera.rotate(cursorRow+0.5, cursorCol+0.5, cursorTile().depth, Math.toRadians(-90));
        }
    }

    public class RotateRight extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            Camera.rotate(cursorRow+0.5, cursorCol+0.5, cursorTile().depth, Math.toRadians(90));
        }
    }
}
