package lib.elementboxes;

import lib.Colors;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** An element on the screen with various properties. basically html elements but cool. **/
public class ElementBox {
    protected List<ElementBox> children;

    /** Rectangle enclosing this element. Contains X, Y, width and height of this element on the screen. **/
    public Rectangle rect;
    /** Extra space added outside the border of this element on all 4 sides. **/
    public int marginLeft, marginRight, marginTop, marginBottom;
    /** Extra space added inside the border of this element on all 4 sides. **/
    public int padLeft, padRight, padTop, padBottom;
    /** If child element sof this element should fill all remaining space in a certain axis. **/
    public boolean fillX, fillY;
    /** If elements should be pushed as far as possible to the right or bottom. **/
    public boolean alignRight, alignBottom;
    /** If elements should be centered on the X and Y axes. **/
    public boolean centerX, centerY;
    /** If each side of the border is drawn. **/
    public boolean borderLeft, borderRight, borderTop, borderBottom;
    /** Color of this tile's text and other foreground, depending on the elementbox type. **/
    public Color color = Colors.FG_COLOR;
    /** Foreground color if this item is not focused or hovered over. **/
    public Color colorFaded = Colors.FG_COLOR_FADED;
    /** Color of the background of this element. **/
    public Color bgColor = Colors.BG_COLOR;
    /** Background color if not focused. **/
    public Color bgColorFaded = Colors.BG_COLOR_FADED;
    /** Color of the 1px border of this element. **/
    public Color borderColor = Colors.BORDER_COLOR;
    /** Border color if not focused. **/
    public Color borderColorFaded = Colors.BORDER_COLOR_FADED;
    /** Border color if hovered over. **/
    public Color hoverColor = Colors.HOVER_COLOR;
    /** Border color if selected. **/
    public Color selectColor = Colors.SELECT_COLOR;
    /** If this element should be selected when it is clicked/interacted with. **/
    public boolean selectable;
    /** If this element should be highlighted when hovered over, but does not have to be selectable. **/
    public boolean hoverHighlight;

    /** Indicates that a resize is needed after these elements' height was changed by an internal method. **/
    public boolean resizeNeeded;
    /** If this element is currently selected by the player. **/
    public boolean selected;
    /** If preselected, meaning once its container is selected this will gain selection, and it has a special color. Only works with some containers. **/
    public boolean preselected;
    /** If a descendant of this element is currently selected. **/
    public boolean focused;
    /** If this element is currently being hovered over by the player. **/
    public boolean hovered;
    /** If a descendant of this element is currently hovered. **/
    public boolean descHovered;

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

    public ElementBox() {
        this(0,0,0,0);
    }

    public ElementBox(int x, int y, int width, int height) {
        rect = new Rectangle(x, y, width, height);
        children = new ArrayList<>();
    }

    /** Draw this element on the screen.
     * When extended, drawBackground(), drawChildren() and drawBorder() can be separated. **/
    public void draw(Graphics g) {
        drawBackground(g);
        drawChildren(g);
        drawBorder(g);
    }

    /** Draw the background of this element.Typically called before children are drawn. **/
    public void drawBackground(Graphics g) {
        g.setColor(isFocused() ? bgColor : bgColorFaded);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

    /** Draw all children of this element Typically called before border is drawn. **/
    public void drawChildren(Graphics g) {
        for (ElementBox child : children){
            child.draw(g);
        }
    }

    /** Draw the border of this element. **/
    public void drawBorder(Graphics g) {
        g.setColor(
            selected
                ? selectColor
                : hovered
                    ? hoverColor
                    : preselected
                        ? isFocused()
                            ? Colors.PRESELECT_COLOR
                            : Colors.PRESELECT_COLOR_FADED
                        : isFocused()
                            ? borderColor
                            : borderColorFaded);
        drawBorderLines(g);
    }

    /** Draw each individual border line. Color is not changed in this method. Useful in extending subclasses. **/
    public void drawBorderLines(Graphics g) {
        if (borderLeft) g.drawLine(rect.x, rect.y, rect.x, rect.y+rect.height);
        if (borderRight) g.drawLine(rect.x+rect.width, rect.y, rect.x+rect.width, rect.y+rect.height);
        if (borderTop) g.drawLine(rect.x, rect.y, rect.x+rect.width, rect.y);
        if (borderBottom) g.drawLine(rect.x, rect.y+rect.height, rect.x+rect.width, rect.y+rect.height);
    }

    /** Called whenever this element, any parent element, or the game window is resized.
     * Re-initializes properties that change with size.
     * Only called if it needs to be recalculated.
     * @param parent the parent of this elementbox
     * @param offset the amount in pixels to offset this element by in its row/column
     *               (this is used if there are multiple children)
     */

    public void resize(ElementBox parent, int offset) {
        rect.x = parent.rect.x;
        rect.y = parent.rect.y;

        if (alignRight) {
            rect.x += parent.rect.width - parent.padRight - marginRight - rect.width;
        } else if (centerX) {
            rect.x += (parent.totalWidth()-totalWidth())/2;
        } else {
            rect.x += parent.padLeft + marginLeft;
        }
        if (alignBottom) {
            rect.y += parent.rect.height - parent.padBottom - marginBottom - rect.height;
        } else if (centerY) {
            rect.x += (parent.totalHeight()-totalHeight())/2;
        }else {
            rect.y += parent.padTop + marginTop;
        }

        if (parent.displayType.doOffset){
            if (parent.displayType.isRow){
                rect.x += offset;
            } else {
                rect.y += offset;
            }
        }

        if (fillX) rect.width = parent.rect.width - parent.padLeft - marginLeft - marginRight - parent.padRight;

        if (fillY) rect.height = parent.rect.height - parent.padTop - marginTop - marginBottom - parent.padBottom;

//        System.out.println(hashCode() + " " + this + " (parent: " + parent.hashCode() + ")");

        if (children.size() == 0) return;
        if (displayType.flex) {
            int availableSpace = displayType.isRow ? parent.rect.width : parent.rect.height;
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
                    child.rect.width = elementSize;
                } else {
                    child.rect.height = elementSize;
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
                        ? child.marginLeft + child.rect.width + child.marginRight
                        : child.marginTop + child.rect.height + child.marginBottom;
            }
        } else {
            for (ElementBox child : children){
                child.resize(this, 0);
            }
        }
    }

    /** Select this element and focus all descendants. **/
    public void select() {
        selected = true;
        for (ElementBox child : children) {
            child.focus();
        }
    }

    /** Focus this element and all descendants. **/
    public void focus() {
        focused = true;
        for (ElementBox child : children) {
            child.focus();
        }
    }

    /** Deselect this element and unfocus all descendants. **/
    public void deselect() {
        selected = false;
        for (ElementBox child : children) {
            child.unfocus();
        }
    }

    /** Unfocus this element and all descendants. **/
    public void unfocus() {
        focused = false;
        for (ElementBox child : children) {
            child.unfocus();
        }
    }

    /** Method to run when this element is interacted with (Z pressed or clicked while selected). **/
    public void onInteract(){}

    /** Method to run when X is pressed while a child of this element is selected. **/
    public void onCancel(ElementBox child){}

    /** Methods to run when the arrow keys/WASD are used while a child of this element is selected. **/
    public void onLeft(ElementBox child){}
    public void onRight(ElementBox child){}
    public void onUp(ElementBox child){}
    public void onDown(ElementBox child){}

    /** Method to run when the mouse is pressed while hovered over this element. **/
    public void onMousePress(Point pressPoint){}

    /** Method to run when the mouse is dragged from this element. **/
    public void onMouseDrag(Point dragPoint){}

    /** Method to run when the mouse is released from this element after dragging. **/
    public void onMouseRelease(Point releasePoint){}

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
        return marginLeft + padLeft + rect.width + padRight + marginRight;
    }
    public int totalHeight(){
        return marginTop + padTop + rect.height + padBottom + marginBottom;
    }
    public int innerWidth(){
        return padLeft + rect.width + padRight;
    }
    public int innerHeight(){
        return padTop + rect.height + padBottom;
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

    /** If this should be faded or not. **/
    public boolean isFocused() {
        return descHovered || hovered || selected || focused;
    }

    // Accessors
    public List<ElementBox> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return rect.toString();
    }


}
