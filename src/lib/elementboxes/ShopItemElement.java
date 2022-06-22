package lib.elementboxes;

import lib.engine.Buyable;
import lib.engine.Corners;
import lib.engine.Player;
import lib.engine.Shop;
import lib.geometry.Shape;

import java.awt.*;

public class ShopItemElement extends ElementBox {
    public static final int MARGIN = 8;
    public static final int PADDING = 4;
    public static final int WIDTH = (ShopElement.WIDTH - ShopElement.COLS*2*(MARGIN + PADDING)) / ShopElement.COLS;
    public static final int HEIGHT = WIDTH+12;
    public static final double SHAPE_ZOOM = WIDTH*0.8;

    public final Shop shop;
    public final Buyable item;
    private Polygon unitDrawPoly;
    private final TextElement costText;

    public ShopItemElement(Shop shop, Buyable item) {
        this.shop = shop;
        this.item = item;
        rect.width = WIDTH;
        rect.height = HEIGHT;
        setMargin(MARGIN);
        setPadding(PADDING);
        setBorder(true);

        selectable = true;
        hoverable = true;
        hoverHighlight = true;

        costText = new TextElement(item.buyCost()+"");
        costText.font = TextElement.GAME_FONT_SMALL;
        costText.rect.height = costText.font.getSize();
        costText.alignBottom = true;
        costText.fillX = true;
        addChild(costText);
    }

    @Override
    public void draw(Graphics g) {
        drawBackground(g);
        drawBorder(g);

        Player player = shop.getPlayer();
        boolean canBuy = player.canBuy(item);
        item.getMesh().draw(g,
                unitDrawPoly,
                canBuy ? player.color : Color.GRAY,
                canBuy ? player.faceColor : Color.BLACK,
                WIDTH);
        costText.draw(g);
    }

    @Override
    public void resize(ElementBox parent, int offset) {
        super.resize(parent, offset);
        unitDrawPoly = Shape.tilePolygon(rect.x + rect.width/2, rect.y + rect.width/2 - 10, SHAPE_ZOOM);
    }


}
