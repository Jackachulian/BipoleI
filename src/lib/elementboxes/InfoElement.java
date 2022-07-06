package lib.elementboxes;

import lib.Colors;
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

    /** Index of the currently selected item. **/
    public int index;

    /** ElementBox displaying the title of this element. **/
    TitleRow titleRow;
    /** ElementBox containing the HP, ATK and cooldown stats. **/
    StatRow statRow;

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
