package lib.elementboxes;

import lib.engine.Corners;
import lib.engine.Tile;
import lib.engine.Unit;
import lib.geometry.Mesh;
import lib.geometry.Shape;

import java.awt.*;

public class TitleRow extends ElementBox {
    public static final double SHAPE_ZOOM = 40;

    /** The title the InfoElement enclosing this is displaying. **/
    public Tile tile;
    /** The mesh to render at the top-left. Null if none. **/
    public Mesh mesh;
    /** The polygon to render the mesh on. **/
    public Polygon polygon;
    /** The text displaying the title of this unit. **/
    public TextElement title;

    public TitleRow() {
        fillX = true;
        rect.height = 50;
        borderBottom = true;
        setPadding(InfoElement.MARGIN);
        displayType = DisplayType.ROW;
        bg = false;

        title = new TextElement();
        title.font = TextElement.GAME_FONT_LARGE;
        title.textXAlign = TextElement.Alignment.START;
        title.marginLeft = 64;
        title.fillX = true;
        title.fillY = true;
        addChild(title);
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (mesh != null) {
            mesh.draw(g, polygon, Corners.flatCorners(), tile.getOwner().color, tile.getOwner().faceColor, SHAPE_ZOOM);
        }
    }

    public void setTile(Tile tile) {
        this.tile = tile;
        title.setText(tile.displayName());
        mesh = tile.hasUnit() ? tile.getUnit().getData().getMesh() : null;
    }

    @Override
    public void resize(ElementBox parent, int offset) {
        super.resize(parent, offset);
        polygon = Shape.tilePolygon(rect.x + 40, rect.y + 20, SHAPE_ZOOM);
    }
}
