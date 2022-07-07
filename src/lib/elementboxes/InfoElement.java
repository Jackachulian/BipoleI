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

    /** The tile currently being displayed. **/
    public Tile tile;

    /** Index of the currently selected action. -1 means an item in the sell/move row is selected. **/
    public int index;

    /** ElementBox displaying the title of this element. **/
    TitleRow titleRow;
    /** ElementBox containing the HP, ATK and cooldown stats. **/
    StatRow statRow;
    /** Element containing all action buttons. **/
    ActionList actionList;

    StatElement hp;
    StatElement atk;
    StatElement cooldown;

    public InfoElement(ElementBox root) {
        this.root = root;

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
    public void setTile(Player player, Tile tile) {
        this.tile = tile;
        titleRow.setTile(tile);

        statRow.active = tile.hasUnit();
        index = tile.hasUnit() ? -1 : 0;

        actionList.clear();
        if (tile.hasUnit()) {
            if (tile.getUnit().ownedBy(player)) {
                actionList.addChild(new ActionFirstRow(player, tile));
            }
            for (Action action : tile.getUnit().getData().getActions()) {
                actionList.addChild(new ActionElement(action, player, tile));
            }
        } else {
            actionList.addChild(new ActionElement(Actions.CONTEST, player, tile));
        }

        resize(root, 0);
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (tile.hasUnit()) {
            hp.setValue(tile.getUnit().getHp()+"");
            atk.setValue(tile.getUnit().getAtk()+"");
            cooldown.setValue(
                    tile.getUnit().isReady() ? "Ready" : String.format("%.01fs", tile.getUnit().getCooldown()/1000.0)
            );
        }
    }
}
