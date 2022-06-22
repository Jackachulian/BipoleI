package lib.elementboxes;

import lib.Colors;
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
    /** Row of the currently selected item. **/
    private int selectedRow;
    /** Column of the currently selected item. **/
    private int selectedCol;

    public ShopElement(Shop shop) {
        this.shop = shop;
        fillY = true;
        rect.width = WIDTH;
        alignRight = true;
        setPadding(PADDING);
        padTop = TOP_PADDING;
        setBorder(false);
        borderLeft = true;
        displayType = DisplayType.COLUMN;
        updateShopItemChildren();

        hoverable = true;
        focusable = true;
        mouseOverUndim = true;
    }

    /** Update shop children. Creates a bunch of rows of items to create a grid. **/
    public void updateShopItemChildren(){
        int rows = shop.getItems().size() - (shop.getItems().size() / COLS);
        clear();
        for (int r=0; r<rows; r++){
            ElementBox row = new ElementBox();
            addChild(row);
            row.displayType = DisplayType.ROW;
            row.rect.height = ShopItemElement.HEIGHT + 2*ShopItemElement.MARGIN;
            row.fillX = true;
            row.setBorder(false);
            for (int c=0; c<COLS; c++){
                if (r*COLS + c >= shop.getItems().size()) break;
                ShopItemElement itemElement = new ShopItemElement(shop, shop.getItem(r*COLS + c));
                row.addChild(itemElement);
            }
        }
    }

    @Override
    public ElementBox verifySelection() {
        // Check all elements.
        // Keep track of selected and preselected indices,
        // so if a selected item is not found, the preselected can be selected,
        // and if a selected item is found, preselected indices can be un-preselected
        setSelectedItem(-1, -1);
        int preselectRow = -1, preselectCol = -1;

        for (int r=0; r<children.size(); r++) {
            ElementBox row = children.get(r);
            for (int c=0; c<row.getChildren().size(); c++) {
                ElementBox item = row.getChildren().get(c);
                if (item.selected) {
                    setSelectedItem(r, c);
                }
                if (item.preselected) {
                    preselectRow = r;
                    preselectCol = c;
                }
            }
        }

        // If an item is selected, un-preselect any preselected item and return none, as it is already selected
        if (selectedRow != -1) {
            if (preselectRow != -1) {
                itemAt(preselectRow, preselectCol).preselected = false;
                return null;
            }
        }
        // If no items are selected but an item is preselected, return the pre-selected item to be selected
        if (preselectRow != -1) {
            setSelectedItem(preselectRow, preselectCol);
            ElementBox preselectedItem = itemAt(preselectRow, preselectCol);
            preselectedItem.preselected = false;
            return preselectedItem;
        }

        // If no items are selected or preselected, select first item if possible, otherwise return no item
        if (children.isEmpty()) return null;
        ElementBox firstRow = children.get(0);
        if (firstRow.children.isEmpty()) return null;
        setSelectedItem(0, 0);
        return firstRow.children.get(0);
    }

    @Override
    public void unfocus() {
        super.unfocus();
        if (itemSelected()) selectedItem().preselected = true;
    }

    public ElementBox selectedRowElement() {
        return children.get(selectedRow);
    }

    public ElementBox selectedItem() {
        return selectedRowElement().children.get(selectedCol);
    }

    public void setSelectedItem(int row, int col) {
        selectedRow = row;
        selectedCol = col;
    }

    public boolean itemSelected() {
        return selectedRow != -1;
    }

    public ElementBox itemAt(int r, int c) {
        return children.get(r).children.get(c);
    }

    @Override
    public ElementBox onMove(int r, int c) {
        // LEFT
        if (c == -1) {
            if (selectedCol > 0) {
                selectedCol -= 1;
            }
            return selectedItem();
        }

        // RIGHT
        else if (c == 1) {
            if (selectedCol < ShopElement.COLS && selectedRowElement().children.size() > selectedCol+1) {
                selectedCol += 1;
            }
            return selectedItem();
        }

        // UP
        else if (r == -1) {
            if (selectedRow > 0) {
                selectedRow -= 1;
            }
            return selectedItem();
        }

        // DOWN
        else {
            if (selectedRow < children.size()-1 && children.get(selectedRow+1).children.size() > selectedCol) {
                selectedRow += 1;
            }
            return selectedItem();
        }
    }
}
