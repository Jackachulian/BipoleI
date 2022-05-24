package lib.engine;

import lib.geometry.Shape;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Contains all data that units use. **/
public abstract class UnitData implements Buyable {
    /** Display name of this unit. **/
    public final String name;
    /** Base cost to buy this unit.
     * A portion is retrieved when sold,
     * and enemies get a portion of this value when destroying this tile. **/
    public final int value;
    /** Base HP of this unit. **/
    public final int hp;
    /** Base ATK of this unit. **/
    public final int atk;
    /** Amount of delay (in milliseconds) before this unit can act. **/
    public final int delay;

    /** All actions that this type of unit can use as their action. **/
    private ArrayList<Action> actions;

    /** All shapes to draw when drawing this unit. **/
    private final List<Shape> shapes;

    public UnitData(String name, int value, int hp, int atk, int delay) {
        this.name = name;
        this.value = value;
        this.hp = hp;
        this.atk = atk;
        this.delay = delay;
        this.actions = new ArrayList<>();
        this.shapes = new ArrayList<>();
    }

    /** Add a shape to be drawn for this type of unit. **/
    public void addShape(Shape shape){
        shapes.add(shape);
    }

    /** Draw all shapes for this unit type. **/
    public void drawShapes(Graphics g, double x, double y, double z, Color segmentColor, Color lineColor){
        for (Shape shape : shapes){
            shape.draw(g, x, y, z, segmentColor, lineColor);
        }
    }

    @Override
    public String displayName() {
        return name;
    }

    @Override
    public int buyCost() {
        return value;
    }

    @Override
    public List<Shape> getShapes() {
        return shapes;
    }
}
