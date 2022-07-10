package lib.elementboxes;

import lib.Colors;
import lib.data.Actions;
import lib.engine.Action;
import lib.engine.Player;
import lib.engine.Tile;
import lib.engine.Unit;

import java.awt.*;

public class InfoElement extends ElementBox {
    public static final int WIDTH = 240;
    public static final int MARGIN = 8;

    /** The root that this InfoElement is on, needed for resizing. **/
    private final ElementBox root;
    /** The player viewing this infoElement. **/
    public final Player player;

    /** The tile currently being displayed. **/
    public Tile tile;

    /** Keeps track of the unit that was last displayed.
     * If this changed, this element needs to be reset to show the right actions, etc */
    private Unit lastDrawUnit;

    /** ElementBox displaying the title of this element. **/
    TitleRow titleRow;
    /** ElementBox containing the HP, ATK and cooldown stats. **/
    StatRow statRow;
    /** Element containing all action buttons. **/
    ActionList actionList;

    StatElement hp;
    StatElement atk;
    StatElement cooldown;

    public InfoElement(ElementBox root, Player player) {
        this.root = root;
        this.player = player;

        rect.width = WIDTH;
        setBorder(true);
        displayType = DisplayType.COLUMN;
        stretch = true;
        bgColor = Colors.BG_DIM;

        titleRow = new TitleRow();
        addChild(titleRow);

        statRow = new StatRow();
        addChild(statRow);

        actionList = new ActionList();
        addChild(actionList);

        hp = new StatElement("HP");
        statRow.addChild(hp);
        atk = new StatElement("ATK");
        statRow.addChild(atk);
        cooldown = new StatElement("Cooldown");
        statRow.addChild(cooldown);
    }

    /** Display a tile and its properties on this element. **/
    public void setTile(Tile tile) {
        this.tile = tile;
        titleRow.setTile(tile);

        statRow.active = tile.hasUnit();
        if (!statRow.active) statRow.rect.width = 0;

        actionList.clear();
        if (tile.hasUnit()) {
            if (tile.getUnit().ownedBy(player)) {
                if (Actions.SELL.visible(player, tile) || Actions.MOVE.visible(player, tile)) {
                    ActionRow row = new ActionRow();
                    actionList.addChild(row);

                    ActionElement move = new ActionElement(Actions.MOVE, player, tile);
                    move.fillX = false;
                    row.addChild(move);

                    ActionElement sell = new ActionElement(Actions.SELL, player, tile);
                    sell.fillX = false;
                    row.addChild(sell);
                }

                if (tile.getUnit().getData().hasAction()) {
                    actionList.addChild(new ActionElement(tile.getUnit().getData().getAction(), player, tile));
                }
            }
        } else {
            actionList.addChild(new ActionElement(Actions.CONTEST, player, tile));
        }

        resize(root, 0);
    }

    @Override
    public void draw(Graphics g) {
        if (lastDrawUnit != tile.getUnit()) {
            setTile(tile);
        }
        super.draw(g);
        if (tile.hasUnit()) {
            hp.setValue(tile.getUnit().getHp()+"");
            atk.setValue(tile.getUnit().getAtk()+"");
            cooldown.setValue(
                    tile.getUnit().isReady() ? "Ready" : String.format("%.01fs", tile.getUnit().getCooldown()/1000.0)
            );
        }
        lastDrawUnit = tile.getUnit();
    }
}
