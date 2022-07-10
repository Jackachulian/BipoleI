package lib;

import lib.elementboxes.ElementBox;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Stack;
import java.util.function.Predicate;

public abstract class ElementPanel extends JPanel implements MouseInputListener, MouseMotionListener, MouseWheelListener {
    /** The root elementbox that contains all elements, mirroring this panel's dimensions.
     * The root is never directly drawn, but the elementPanel draws all of its children, which are drawn normally. **/
    protected RootElement root;
    /** The element that is currently selected.
     * If root is selected, functionality depends on the panel that extends this. Root is selected initially on start. **/
    protected ElementBox selectedElement;
    /** The element that is currently being hovered over. **/
    protected ElementBox hoveredElement;
    /** The element that is currently focused. Keyboard inputs are sent to this element. **/
    protected ElementBox focusedElement;
    /** All elements that need to check if mouse-overed. **/
    protected final HashSet<ElementBox> checkMouseOver;
    /** Boolean to keep track of the mouse state. **/
    public boolean mouseDown;

    public ElementPanel(RootElement root) {
        this.root = root;
        selectedElement = root;
        hoveredElement = root;
        focusedElement = root;

        checkMouseOver = new HashSet<>();

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                resizeElements();
            }
        });
    }

    /** Resize all elements in the given container, updating this ElementPanel's mouseOverElements list as needed. **/
    public void resizeElements(ElementBox container){
        container.rect.width = getWidth();
        container.rect.height = getHeight();
        container.resizeChildren();
    }

    /** Resize ALL components in this panel. **/
    public void resizeElements() {
        resizeElements(root);
    }

    /** Updates this panel's elements that require to be checked if the mouse is over them (even if not "hovered" which only one element can be). **/
    public void updateMouseOvers(ElementBox element) {
        if (element.mouseOverUndim) {
            checkMouseOver.add(element);
            element.dim();
        } else {
            checkMouseOver.remove(element);
        }
        for (ElementBox child : element.getChildren()) {
            updateMouseOvers(child);
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

    /** When Z is pressed (interact), call the interaction method on the currently selected item. **/
    public void onInteract() {
        selectedElement.onInteract();
    }

    /** When X is pressed (cancel/back), leave the current parent and traverse back until a selectable element is found and select it.
     * If this is the root, do nothing. **/
    public void onCancel() {
        if (focusedElement != root) focusedElement.dim();
        focusElement(root);
        selectElement(root);
    }

    public void onMove(int r, int c) {
        selectElement(focusedElement.onMove(r, c));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // focus and select moved to mousePressed
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseDown = true;
        ElementBox mouseElement = mouseElementExtremum(
                selectedElement.rect.contains(e.getPoint()) ? selectedElement : root,
                e.getPoint(),
                ElementBox::isSelectable
        );

        // If the pressed element is already selected, interact with it
        if (!mouseElement.selected) {
            selectElement(mouseElement);
            focusElement(mouseElementExtremum(
                    focusedElement.rect.contains(e.getPoint()) ? focusedElement : root,
                    e.getPoint(),
                    ElementBox::isFocusable
            ));
        }
        onInteract();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDown = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        ElementBox hoverElement = mouseElementExtremum(
                hoveredElement.rect.contains(e.getPoint()) ? hoveredElement : root,
                e.getPoint(),
                ElementBox::isHoverable
        );

        if (hoverElement != hoveredElement) {
            root.descUnhover();
            hoverElement(hoverElement);
        }

        // Check mouseOvers
        for (ElementBox element : checkMouseOver) {
            boolean elemMouseOver = element.rect.contains(e.getPoint());
            if (!element.mouseOver && elemMouseOver) {
                element.mouseOver();
            } else if (element.mouseOver && !elemMouseOver) {
                element.mouseOff();
            }
        }
    }

    public void selectElement(ElementBox element) {
        if (element == null || element == selectedElement) return;
        selectedElement.unselect();
        selectedElement = element;
        selectedElement.select();
//        System.out.println("selected "+selectedElement);
    }

    /** Returns true if a new element was hovered. **/
    public void hoverElement(ElementBox element) {
        if (element == null || element == hoveredElement) return;
        hoveredElement.unhover();
        hoveredElement = element;
        hoveredElement.hover();
//        System.out.println("hovered "+hoveredElement);
    }

    public void focusElement(ElementBox element) {
        if (element == null || element == focusedElement) return;
        focusedElement.unfocus();
        focusedElement = element;
        focusedElement.focus();
//        System.out.println("focused "+focusedElement);

        ElementBox newSelection = focusedElement.verifySelection();
        if (newSelection != null) selectElement(newSelection);
    }

    /** Get the element the mouse is over deepest in the hierarchy that passes the given condition.
     * Should always at least return the root/base container if no others are moused over.
     * A consumer is applied to the highest element in the hierarchy if there are any branches.
     * A method to run on the first child found in the container is passed, if one is found. **/
    public ElementBox mouseElementExtremum(ElementBox container, Point mousePos, Predicate<ElementBox> condition) {
        Stack<ElementBox> path = new Stack<>();
        mouseElementPath(path, container, mousePos);

        while (!path.isEmpty()) {
            ElementBox extremum = path.pop();
            if (condition.test(extremum)) {
                return extremum;
            }
        }

        return container;
    }

    /** Build the hierarchy of elements that contain the mouse position in the passed stack. **/
    public void mouseElementPath(Stack<ElementBox> path, ElementBox container, Point mousePos) {
        for (ElementBox element : container.getChildren()) {
            if (element.rect.contains(mousePos)) {
                path.add(element);
                mouseElementPath(path, element, mousePos);
                return;
            }
        }
    }

    public boolean rootSelected() {
        return selectedElement == root;
    }
}
