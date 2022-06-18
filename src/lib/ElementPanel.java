package lib;

import lib.elementboxes.ElementBox;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import java.util.Stack;

public abstract class ElementPanel extends JPanel implements MouseInputListener, MouseMotionListener, MouseWheelListener {
    /** The root elementbox that contains all elements, mirroring this panel's dimensions.
     * The root is never directly drawn, but the elementPanel draws all of its children, which are drawn normally. **/
    protected ElementBox root;
    /** The element that is currently selected.
     * If root is selected, functionality depends on the panel that extends this. Root is selected initially on start. **/
    protected ElementBox selectedElement;
    /** The parent of the selected element. Null if the selected element is root. **/
    protected ElementBox selectedParent;
    /** The path of parents of the selected element. **/
    protected Stack<ElementBox> selectionPath;
    /** The element that is currently being hovered over. **/
    protected ElementBox hoveredElement;
    /** The path of parents of the hovered element. **/
    protected Stack<ElementBox> hoverPath;
    /** The element that was "pressed" (mouse click started while hovered over the element). **/
    protected ElementBox pressedElement;

    public ElementPanel(ElementBox root) {
        this.root = root;
        selectedElement = root;
        selectionPath = new Stack<>();
        hoveredElement = root;
        hoverPath = new Stack<>();

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                resizeElements();
            }
        });
    }

    public void resizeElements(){
        root.rect.width = getWidth();
        root.rect.height = getHeight();
        for (ElementBox child : root.getChildren()){
            child.resize(root, 0);
        }
    }

    public void drawElements(Graphics g){
        for (ElementBox element : root.getChildren()){
            if (element.isResizeNeeded()) element.resize(root, 0);
            element.draw(g);
        }
    }

    public void addElement(ElementBox element){
        root.addChild(element);
    }

    /** Find the selectable element that the given point (typically mouse hover/click point) is currently over. **/
    public ElementBox getHoveredElement(ElementBox element, Point clickPoint, boolean onlySelectable) {
        return getHoveredElement(element, clickPoint, element, onlySelectable);
    }

    public ElementBox getHoveredElement(ElementBox element, Point clickPoint, ElementBox youngestElement, boolean onlySelectable) {
        for (ElementBox child : element.getChildren()) {
            if (child.rect.contains(clickPoint)) {
                if (!onlySelectable || child.selectable) {
                    youngestElement = child;
                }
                return getHoveredElement(child, clickPoint, youngestElement, onlySelectable);
            }
        }
        return youngestElement;
    }

    public ElementBox getParentOf(ElementBox element) {
        if (element == root) return null;
        return findParentIn(root, element);
    }

    public ElementBox findParentIn(ElementBox element, ElementBox targetChild) {
        for (ElementBox child : element.getChildren()) {
            if (child == targetChild) {
                return element;
            } else {
                ElementBox parent = findParentIn(child, targetChild);
                if (parent != null) return parent;
            }
        }
        return null;
    }

    /** Get the chain of element children that leads to a certain element. **/
    public void getElementPathTo(ElementBox element, Stack<ElementBox> path) {
        path.clear();
        path.push(root);
        findElementPathTo(element, path);
        path.pop();
    }

    public void findElementPathTo(ElementBox element, Stack<ElementBox> path) {
        for (ElementBox child : path.peek().getChildren()) {
            path.push(child);
            if (element == child) return;
            findElementPathTo(element, path);
            if (path.peek() == element) return;
            path.pop();
        }
    }

    /** When X is pressed (cancel/back), leave the current parent and traverse back until a selectable element is found and select it.
     * If this is the root, do nothing. **/
    public void onCancel() {
        while (!selectionPath.isEmpty()) {
            ElementBox parent = selectionPath.pop();
            parent.focused = false;
            if (parent.selectable) {
                selectElement(parent);
                break;
            }
        }
        if (selectionPath.isEmpty()) {
            selectElement(root);
        }
    }

    /** Call onLeft on the selected element's parent. **/

    @Override
    public void mouseClicked(MouseEvent e) {
        if (selectedElement.rect.contains(e.getPoint())) {
            ElementBox clicked = getHoveredElement(selectedElement, e.getPoint(), true);
            if (clicked == selectedElement) {
                selectedElement.onInteract();
            } else {
                selectElement(clicked);
            }
        } else {
            ElementBox clicked = getHoveredElement(root, e.getPoint(), true);
            selectElement(clicked);
        }
    }

    public void selectElement(ElementBox element) {
        if (selectedElement == element) return;
        selectedElement.deselect();
        if (element != null && element.selectable) {
            selectedElement = element;
        } else {
            selectedElement = root;
        }
        selectedElement.select();

        root.unfocus();
        getElementPathTo(selectedElement, selectionPath);
        for (ElementBox elementBox : selectionPath) {
            elementBox.focused = true;
        }

        if (!selectionPath.isEmpty()) {
            selectedParent = selectionPath.peek();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (hoveredElement == null) return;
        pressedElement = hoveredElement;
        pressedElement.onMousePress(e.getPoint());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (pressedElement == null) return;
        pressedElement.onMouseDrag(e.getPoint());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (pressedElement == null) return;
        pressedElement.onMouseRelease(e.getPoint());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (hoveredElement.rect.contains(e.getPoint())) {
            ElementBox hovered = getHoveredElement(hoveredElement, e.getPoint(), false);
            hoverElement(hovered);
        } else {
            ElementBox hovered = getHoveredElement(root, e.getPoint(), false);
            hoverElement(hovered);
        }
    }

    public void hoverElement(ElementBox element) {
        if (hoveredElement == element) return;
        hoveredElement.hovered = false;
        if (element != null && element.selectable) {
            hoveredElement = element;
        } else {
            hoveredElement = root;
        }
        hoveredElement.hovered = true;

        for (ElementBox elementBox : hoverPath) {
            elementBox.descHovered = false;
        }
        getElementPathTo(hoveredElement, hoverPath);
        System.out.println(hoverPath);
        for (ElementBox elementBox : hoverPath) {
            elementBox.descHovered = true;
        }
    }

    public boolean rootSelected() {
        return selectedElement == root;
    }

    public boolean rootPressed() {
        return pressedElement == root;
    }
}
