package lib.elementboxes;

import lib.engine.Action;
import lib.engine.Player;
import lib.engine.Tile;

import java.awt.*;

public class ActionElement extends TextElement {
    /** The action to run when interacting with this button. **/
    private final Action action;
    /** Player to run the action with. **/
    private final Player player;
    /** Tile to run the action on. **/
    private final Tile tile;

    public ActionElement(Action action, Player player, Tile tile) {
        this.action = action;
        this.player = player;
        this.tile = tile;

        active = action.visible(player, tile);
        undimmed = action.usable(player, tile);
        setText(action.displayName(player, tile));

        selectable = true;
        hoverable = true;
        hoverHighlight = true;

        fillX = true;
        rect.height = 42;
        setMargin(4);
        setBorder(true);
    }

    @Override
    public void draw(Graphics g) {
        active = action.visible(player, tile);
        undimmed = action.usable(player, tile);
        selectable = undimmed;
        setText(action.displayName(player, tile));
        super.draw(g);
    }

    @Override
    public void onInteract() {
        tile.act(player, action);
        if (!action.usable(player, tile)) {
            unselect();
        }
    }
}
