package lib.elementboxes;

import lib.engine.Buyable;
import lib.engine.Shop;

import java.awt.*;

/** An element on the screen with various properties. basically html elements but cool. **/
public class ShopElement extends ElementBox {
    public static final int WIDTH = 160;
    public static final int PADDING = 8;
    public static final int TOP_PADDING = PADDING + 8;
    public static final int COLS = 2;

    protected Shop shop;

    public ShopElement(Shop shop) {
        this.shop = shop;
        fillY = true;
        width = WIDTH;
        alignRight = true;
        setPadding(PADDING);
        padTop = TOP_PADDING;
        setBorder(false);
        borderLeft = true;
        displayType = DisplayType.COLUMN;
        updateShopItemChildren();
    }

    /** Update shop children. Creates a bunch of rows of items to create a grid. **/
    public void updateShopItemChildren(){
        int rows = shop.getItems().size() - (shop.getItems().size() / COLS);
        clear();
        for (int r=0; r<rows; r++){
            ElementBox row = new ElementBox();
            addChild(row);
            row.displayType = DisplayType.ROW;
            row.height = ShopItemElement.HEIGHT + 2*ShopItemElement.MARGIN;
            row.fillX = true;
            row.setBorder(false);
            for (int c=0; c<COLS; c++){
                if (r*COLS + c >= shop.getItems().size()) break;
                ShopItemElement itemElement = new ShopItemElement(shop, shop.getItem(r*COLS + c));
                row.addChild(itemElement);
            }
        }
    }
}
