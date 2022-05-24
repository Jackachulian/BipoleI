package lib.engine;

import java.util.function.Consumer;

public class Battle {
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
                map[r][c] = new Tile();
            }
        }
    }

    /** Place a tile on the map. **/
    public void placeTile(Tile tile, int x, int y){
        map[x][y] = tile;
    }
    /** Claim a tile by a player. **/
    public void claim(Player player, int x, int y){
        map[x][y].owner = player;
    }
    /** Claim a tile and place a new unit of the given type. **/
    public void claimAndPlaceUnit(Player player, UnitData unitType, int x, int y){
        map[x][y].owner = player;
        map[x][y].unit = new Unit(unitType, player);
    }


    /** Run something on each tile. **/
    public void forEachTile(Consumer<Tile> consumer){
        for (Tile[] tiles : map) {
            for (Tile tile : tiles) {
                consumer.accept(tile);
            }
        }
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
