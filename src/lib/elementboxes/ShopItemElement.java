package lib.elementboxes;

import lib.engine.Buyable;
import lib.engine.Shop;
import lib.geometry.Shape;

import java.awt.*;

public class ShopItemElement extends ElementBox {
    public static final int MARGIN = 8;
    public static final int PADDING = 4;
    public static final int WIDTH = (ShopElement.WIDTH - ShopElement.COLS*2*(MARGIN + PADDING)) / ShopElement.COLS;
    public static final int HEIGHT = WIDTH+12;

    Shop shop;
    Buyable item;

    public ShopItemElement(Shop shop, Buyable item) {
        this.shop = shop;
        this.item = item;
        width = WIDTH;
        height = HEIGHT;
        setMargin(MARGIN);
        setPadding(PADDING);
        setBorder(true);

        TextElement costText = new TextElement(item.buyCost()+" pts");
        costText.font = TextElement.GAME_FONT_SMALL;
        costText.height = costText.font.getSize();
        costText.alignBottom = true;
        costText.fillX = true;
        addChild(costText);
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);

        for (Shape shape : item.getShapes()){
            shape.draw(g,
                    x + width/2,
                    y + height/2,
                    WIDTH*0.8,
                    shop.getPlayer().canBuy(item) ? shop.getPlayer().color : Color.GRAY,
                    Color.BLACK);
        }
    }
}
