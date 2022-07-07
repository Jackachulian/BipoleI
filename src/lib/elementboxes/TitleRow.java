package lib.elementboxes;

import lib.engine.Corners;
import lib.engine.Tile;
import lib.geometry.Shape;

import java.awt.*;

public class TitleRow extends ElementBox {
    public static final double UNIT_ZOOM = 40;
    public static final double TILE_ZOOM = 30;

    /** The title the InfoElement enclosing this is displaying. **/
    public Tile tile;
    /** The polygon to render the mesh on. **/
    public Polygon polygon;
    /** The text displaying the title of this unit. **/
    public TextElement title;
    /** Smaller text showing the tile's X, Y and Z position. **/
    public TextElement position;

    public TitleRow() {
        fillX = true;
        borderBottom = true;
        setPadding(InfoElement.MARGIN);
        displayType = DisplayType.COLUMN;
        rect.height = 75;
        bg = false;

        title = new TextElement();
        title.font = TextElement.GAME_FONT_LARGE;
        title.textXAlign = TextElement.Alignment.START;
        title.marginLeft = 64;
        title.fillX = true;
        title.rect.height = 50;
        addChild(title);

        position = new TextElement();
        position.font = TextElement.GAME_FONT_SMALL;
        position.undimmed = false;
        position.textXAlign = TextElement.Alignment.END;
        position.marginLeft = 4;
        position.rect.height = 8;
        position.fillX = true;
        addChild(position);
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (tile.hasUnit()) {
            tile.getUnit().getData().getMesh().draw(g, polygon, Corners.flatCorners(), tile.getOwner().color, tile.getOwner().faceColor, UNIT_ZOOM);
        } else {
            tile.drawOnPolygon(g, polygon);
        }
    }

    public void setTile(Tile tile) {
        this.tile = tile;
        polygon = Shape.tilePolygon(rect.x + 40, rect.y + 20, tile.hasUnit() ? UNIT_ZOOM : TILE_ZOOM);
        title.setText(tile.displayName());
        position.setText(String.format("X=%d Y=%d Z=%d", tile.row, tile.col, tile.depth));
    }

    @Override
    public void resize(ElementBox parent, int offset) {
        polygon = Shape.tilePolygon(rect.x + 40, rect.y + 20, (tile != null && tile.hasUnit()) ? UNIT_ZOOM : TILE_ZOOM);
        super.resize(parent, offset);
    }
}
