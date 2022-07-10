package lib.engine;

import lib.data.Actions;
import lib.geometry.Mesh;
import lib.geometry.Shape;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Contains all data that units use. **/
public abstract class UnitData implements Buyable {
    /** Display name of this unit. **/
    private final String name;
    /** Base cost to buy this unit.
     * A portion is retrieved when sold,
     * and enemies get a portion of this value when destroying this tile. **/
    public final int value;
    /** Base HP of this unit. **/
    public final int hp;
    /** Base ATK of this unit. **/
    public final int atk;
    /** Amount of delay (*in milliseconds*) before this unit can act. **/
    public final int delay;

    /** If this type of unit must automatically act when ready (farmers, etc). **/
    private boolean mustAutoAct;
    /** If this type of unit auto acts by default when placed. **/
    private boolean defaultAutoAct;
    /** If this unit can be sold. **/
    private boolean sellable = true;
    /** If this unit can move. **/
    private boolean movable = true;

    /** The action this unit type uses when acting. Null for no action. **/
    private Action action;

    /** The mesh of shapes to draw when drawing this unit. **/
    private final Mesh mesh;

    public UnitData(String name, int value, int hp, int atk, int delay) {
        this.name = name;
        this.value = value;
        this.hp = hp;
        this.atk = atk;
        this.delay = delay;
        this.mesh = new Mesh();
    }

    /** Add a shape to be drawn for this type of unit. **/
    public void addShape(Shape shape){
        mesh.add(shape);
    }

    /** Add an action that this type of unit can use. **/
    public void setAction(Action action) {
        this.action = action;
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
    public Mesh getMesh() {
        return mesh;
    }

    public boolean isMustAutoAct() {
        return mustAutoAct;
    }

    public void setMustAutoAct(boolean mustAutoAct) {
        this.mustAutoAct = mustAutoAct;
        if (mustAutoAct) this.defaultAutoAct = true;
    }

    public Action getAction() {
        return action;
    }

    public boolean hasAction() {
        return action != null;
    }

    public boolean isDefaultAutoAct() {
        return defaultAutoAct;
    }

    public void setDefaultAutoAct(boolean defaultAutoAct) {
        this.defaultAutoAct = defaultAutoAct;
    }

    public boolean isSellable() {
        return sellable;
    }

    public void setSellable(boolean sellable) {
        this.sellable = sellable;
    }

    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }
}
