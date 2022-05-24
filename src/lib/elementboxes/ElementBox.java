package lib.elementboxes;

import lib.engine.MapDrawable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** An element on the screen with various properties. basically html elements but cool. **/
public class ElementBox {
    protected List<ElementBox> children;

    public int x, y, width, height;
    public int marginLeft, marginRight, marginTop, marginBottom;
    public int padLeft, padRight, padTop, padBottom;
    public boolean fillX, fillY;
    public boolean alignRight, alignBottom;
    public boolean centerX, centerY;
    public boolean borderLeft, borderRight, borderTop, borderBottom;
    public Color color;
    public Color bgColor = Color.BLACK;
    public Color borderColor = Color.WHITE;
    /** Indicates that a resize is needed after these elements' height was changed by an internal method. **/
    public boolean resizeNeeded;

    /** Describes the way that children are arranged within this element. **/
    public enum DisplayType {
        ABSOLUTE(false, false, false),
        ROW(true, true, false),
        COLUMN(true, false, false),
        FLEX_ROW(true, true, true),
        FLEX_COLUMN(true, false, true);

        boolean doOffset;
        boolean isRow;
        boolean flex;
        DisplayType(boolean doOffset, boolean isRow, boolean flex) {
            this.doOffset = doOffset;
            this.isRow = isRow;
            this.flex = flex;
        }
    }
    public DisplayType displayType = DisplayType.COLUMN;

    public ElementBox(int x, int y, int width, int height) {
        children = new ArrayList<>();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public ElementBox(){
        children = new ArrayList<>();
    }

    public void draw(Graphics g) {
        // Draw background
        g.setColor(bgColor);
        g.fillRect(x, y, width, height);

        // Draw children
        for (ElementBox child : children){
            child.draw(g);
        }

        // Draw border (above children)
        g.setColor(borderColor);
        if (borderLeft) g.drawLine(x, y, x, y+height);
        if (borderRight) g.drawLine(x+width, y, x+width, y+height);
        if (borderTop) g.drawLine(x, y, x+width, y);
        if (borderBottom) g.drawLine(x, y+height, x+width, y+height);
    }

    /** Called whenever this element, any parent element, or the game window is resized.
     *
     * @param parent the parent of this elementbox
     * @param offset the amount in pixels to offset this element by in its row/column
     *               (this is used if there are multiple children)
     */
    public void resize(ElementBox parent, int offset) {
        x = parent.x;
        y = parent.y;

        if (alignRight) {
            x += parent.width - parent.padRight - marginRight - width;
        } else if (centerX) {
            x += (parent.totalWidth()-totalWidth())/2;
        } else {
            x += parent.padLeft + marginLeft;
        }
        if (alignBottom) {
            y += parent.height - parent.padBottom - marginBottom - height;
        } else if (centerY) {
            x += (parent.totalHeight()-totalHeight())/2;
        }else {
            y += parent.padTop + marginTop;
        }

        if (parent.displayType.doOffset){
            if (parent.displayType.isRow){
                x += offset;
            } else {
                y += offset;
            }
        }

        if (fillX) width = parent.width - parent.padLeft - marginLeft - marginRight - parent.padRight;

        if (fillY) height = parent.height - parent.padTop - marginTop - marginBottom - parent.padBottom;

//        System.out.println(hashCode() + " " + this + " (parent: " + parent.hashCode() + ")");

        if (children.size() == 0) return;
        if (displayType.flex) {
            int availableSpace = displayType.isRow ? parent.width : parent.height;
            for (ElementBox child : children){
                if (displayType.isRow){
                    availableSpace -= (child.marginLeft + child.padLeft + child.padRight + child.marginLeft);
                } else {
                    availableSpace -= (child.marginTop + child.padTop + child.padBottom + child.marginBottom);
                }
            }
            int elementSize = availableSpace / children.size();
            int totalSize = 0;
            for (ElementBox child : children) {
                if (displayType.isRow) {
                    child.width = elementSize;
                } else {
                    child.height = elementSize;
                }
                child.resize(this, totalSize);
                if (displayType.isRow) {
                    totalSize += child.totalWidth();
                } else {
                    totalSize += child.totalHeight();
                }
            }

        } else if (displayType.doOffset) {
            int totalSize = 0;
            for (ElementBox child : children){
                child.resize(this, totalSize);
                totalSize += displayType.isRow
                        ? child.marginLeft + child.width + child.marginRight
                        : child.marginTop + child.height + child.marginBottom;
                System.out.println(hashCode() + " " + totalSize);
            }
        } else {
            for (ElementBox child : children){
                child.resize(this, 0);
            }
        }
    }

    /** Add an element as a child of this element. **/
    public void addChild(ElementBox child) {
        children.add(child);
    }

    /** Remove all children of this elementbox. **/
    public void clear(){
        children.clear();
    }

    // Setters kinda but not accessors whatever that should be called
    public void setBorder(boolean border) {
        borderLeft = border;
        borderRight = border;
        borderTop = border;
        borderBottom = border;
    }

    public void setMargin(int margin) {
        marginLeft = margin;
        marginRight = margin;
        marginTop = margin;
        marginBottom = margin;
    }
    public void setMargin(int xMargin, int yMargin){
        marginLeft = xMargin;
        marginRight = xMargin;
        marginTop = yMargin;
        marginBottom = yMargin;
    }

    public void setPadding(int padding) {
        padLeft = padding;
        padRight = padding;
        padTop = padding;
        padBottom = padding;
    }
    public void setPadding(int xPadding, int yPadding) {
        padLeft = xPadding;
        padRight = xPadding;
        padTop = yPadding;
        padBottom = yPadding;
    }

    // Info
    public int totalWidth(){
        return marginLeft + padLeft + width + padRight + marginRight;
    }
    public int totalHeight(){
        return marginTop + padTop + height + padBottom + marginBottom;
    }
    public int innerWidth(){
        return padLeft + width + padRight;
    }
    public int innerHeight(){
        return padTop + height + padBottom;
    }

    public boolean isResizeNeeded(){
        if (resizeNeeded) return true;

        for (ElementBox child : children){
            if (child.isResizeNeeded()){
                return true;
            }
        }

        return false;
    }

    // Accessors
    public List<ElementBox> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return String.format("(%d,%d) %dx%d", x, y, width, height);
    }
}
