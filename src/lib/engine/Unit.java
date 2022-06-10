package lib.engine;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/** A unit that can exist on top of a tile in a battle. **/
public class Unit {
    /** The type of unit this is. **/
    private final UnitData data;
    /** The player that owns and controls this unit. **/
    private final Player owner;

    /** Timer before this unit can act again. **/
    private final Timer actionTimer;
    /** Time since this user last acted or began becoming ready. **/
    private long actionStartTime;

    /** Amount of damage this unit can take before destroyed. (No max) **/
    private int hp;
    /** Amount of damage this unit deals when attacking. **/
    private int atk;
    /** Amount damage is decreased by when attacked. **/
    private int defense;
    /** Amount this fighter's act speed is increased by. 1 is 100%. **/
    private double speed = 1;

    /** Whether this fighter is currently ready. **/
    private boolean ready;
    /** If this unit automatically acts when ready. **/
    private boolean autoAct;

    public Unit(UnitData data, Player owner) {
        this.data = data;
        this.owner = owner;

        hp = data.hp;
        atk = data.atk;

        int delay = (int)(data.delay / speed);
        actionTimer = new Timer(delay, e -> onReady());
        actionTimer.setRepeats(autoAct);
        actionStartTime = System.nanoTime();
    }

    /** Effects to run when this unit becomes ready. **/
    public void onReady(){
        ready = true;
        if (autoAct){
            act();
        }
    }

    /** Effect to run when acting. **/
    public void act(){
        if (!ready) return;
    }

    /** Use up this fighter's action, restarting their action timer cooldown. **/
    public void resetCooldown(){
        ready = false;
        actionStartTime = System.nanoTime();
        actionTimer.start();
    }

    /** Draw this unit on the screen. **/
    public void draw(Graphics g, double x, double y, double z, Tile tile){
        data.drawShapes(g, x, y, z, tile.getColor(), tile.getFaceColor());
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public UnitData getData() {
        return data;
    }

    public Player getOwner() {
        return owner;
    }
}
