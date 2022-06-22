package lib.engine;

import lib.Camera;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;

/** Represents a battle.
 * Is iterable. When iterated over, iterates over objet in screen draw order. **/
public class Battle implements Iterable<Tile> {
    /** All players engaging in this battle. **/
    protected Player[] players;

    /** All tiles in the map of this battle. **/
    protected Tile[][] map;

    public Battle(Player... players) {
        this.players = players;
        // Initialize with empty tiles
        map = new Tile[8][8];
        for (int r=0; r<map.length; r++){
            for (int c=0; c<map[0].length; c++){
                map[r][c] = new Tile(this, r, c);
            }
        }

        // Calculate the corners around each tile based on surrounding tiles' corners
        for (int r=0; r<map.length; r++) {
            for (int c=0; c<map[0].length; c++) {
                Tile tile = map[r][c];
                Corners
                        base = tile.getBase(),
                        around = tile.getAround(),
                        lowest = tile.getLowest(),
                        north = getBase(r, c-1),
                        northwest = getBase(r-1, c-1),
                        west = getBase(r-1, c),
                        southwest = getBase(r-1, c+1),
                        south = getBase(r, c+1),
                        southeast = getBase(r+1, c+1),
                        east = getBase(r+1, c),
                        northeast = getBase(r+1, c-1);

                around.setNw(nextLowestCorner(base.nw(), north.ne(), northwest.se(), west.sw()));
                around.setSw(nextLowestCorner(base.sw(), west.se(), southwest.ne(), south.nw()));
                around.setSe(nextLowestCorner(base.se(), south.ne(), southeast.nw(), east.sw()));
                around.setNe(nextLowestCorner(base.ne(), east.nw(), northeast.sw(), north.se()));

                lowest.setNw(lowestCorner(base.nw(), north.ne(), northwest.se(), west.sw()));
                lowest.setSw(lowestCorner(base.sw(), west.se(), southwest.ne(), south.nw()));
                lowest.setSe(lowestCorner(base.se(), south.ne(), southeast.nw(), east.sw()));
                lowest.setNe(lowestCorner(base.ne(), east.nw(), northeast.sw(), north.se()));
            }
        }
    }

    /** out of bounds corners, all -1 **/
    private static final Corners OOB = new Corners();
    private Corners getBase(int row, int col) {
        return withinBounds(row, col) ? map[row][col].getBase() : OOB;
    }

    /** Return the next lowest connected corner from the base. (Highest number that is not above base) **/
    private int nextLowestCorner(int base, int... corners) {
        int highest = -1;
        for (int corner : corners) {
            if (corner > highest && corner <= base) {
                highest = corner;
            }
        }
        return highest;
    }

    /** Return the lowest connected corner of the four. **/
    private int lowestCorner(int base, int c1, int c2, int c3) {
        return Math.min(Math.min(base, c1), Math.min(c2, c3));
    }

    /** Return the lowest connected corner of the four. **/
    private int lowestUngroundedCorner(int base, int... corners) {
        int lowest = base;
        for (int corner : corners) {
            if (corner < lowest && corner != -1) {
                lowest = corner;
            }
        }
        return lowest;
    }

    /** Iterate over the tiles in current draw order. (Depends on rotation) **/
    public Iterator<Tile> iterator() {
        return new DrawOrder();
    }

    public Iterator<Tile> reverseDrawOrder() {
        return new DrawOrder(!Camera.reverseRows, !Camera.reverseCols);
    }

    public class DrawOrder implements Iterator<Tile> {
        private int r;
        private int c;
        private final boolean reverseRows;
        private final boolean reverseCols;

        public DrawOrder(boolean reverseRows, boolean reverseCols) {
            this.reverseRows = reverseRows;
            this.reverseCols = reverseCols;

            if (reverseRows) r = numRows()-1;
            if (reverseCols) c = numCols()-1;
        }

        public DrawOrder() {
            this(Camera.reverseRows, Camera.reverseCols);
        }

        @Override
        public boolean hasNext() {
            return reverseRows ? r >= 0 : r < numRows();
        }

        @Override
        public Tile next() {
            Tile next = map[r][c];

            if (reverseCols) { c--; } else { c++; }

            if ((reverseCols && c < 0) || (!reverseCols && c >= numCols())) {
                if (reverseCols) { c = numCols()-1; } else { c = 0; }
                if (reverseRows) { r--; } else { r++; }
            }

//            System.out.printf("(%d,%d) ", r, c);

            return next;
        }
    }


    /** Claim a tile by a player. **/
    public void claim(Player player, int x, int y){
        map[x][y].setOwner(player);
    }

    /** Returns true if there is a tile adjacent to the passed coordinated owned by the player. **/
    public boolean isAdjacentClaimedTile(Player owner, int row, int col){
        return isOwnedByPlayer(owner, row-1, col)
                || isOwnedByPlayer(owner, row+1, col)
                || isOwnedByPlayer(owner, row, col-1)
                || isOwnedByPlayer(owner, row, col+1);
    }
    /** Returns true if the tile at the given index is owned by the passed player. **/
    public boolean isOwnedByPlayer(Player owner, int row, int col){
        return withinBounds(row, col) && map[row][col].getOwner() == owner;
    }
    /** Returns true if the passed coordinate is within the bounds of the map. **/
    public boolean withinBounds(int row, int col){
        return row >= 0 && row < map.length && col >= 0 && col < map[0].length;
    }

    /** Claim a tile and place a new unit of the given type. **/
    public void claimAndPlaceUnit(Player player, UnitData unitType, int x, int y){
        map[x][y].setOwner(player);
        map[x][y].setUnit(new Unit(unitType, this, player));
    }

    /** Retreive a tile by row and column index. **/
    public Tile getTile(int row, int col){
        return map[row][col];
    }

    // ======== ACCESSORS
    public Tile[][] getMap() {
        return map;
    }
    public int numRows(){
        return map.length;
    }
    public int numCols(){
        return map[0].length;
    }
}
