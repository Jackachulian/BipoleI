package lib;

import lib.elementboxes.ElementBox;
import lib.engine.Battle;
import lib.engine.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class ElementPanel extends JPanel {
    /** The root elementbox that contains all elements, mirroring this panel's dimensions. **/
    protected ElementBox root;

    public ElementPanel() {
        root = new ElementBox();

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                resizeElements();
            }
        });
    }

    public void resizeElements(){
        root.width = getWidth();
        root.height = getHeight();
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
}
