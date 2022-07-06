package lib.engine;

/** An action that a Unit can make, typically if they are currently ready. **/
public abstract class Action {
    /** Display name of this action. **/
    private final String name;

    public Action(String name) {
        this.name = name;
    }

    /** Returns true if this action should be visible to the given player.
     * (As in they can use this given the right criteria but may not meet them right now)
     * Default is if the player owns the tile. **/
    public boolean visible(Player player, Tile tile) {
        return tile.ownedBy(player);
    }

    /** Return true if this action can currently be used. Default is if tile has a ready unit. **/
    public boolean usable(Player player, Tile tile) {
        return tile.hasUnit() && tile.getUnit().isReady();
    };

    /** Use this action. Returns true if the action could be used and was used; false if not. **/
    public abstract void act(Player player, Tile tile);

    /** Display name of this action. By default, it is the core required name passed through the constructor.
     * (Name may vary based on tile.) **/
    public String displayName(Tile tile) {
        return name;
    }
}
