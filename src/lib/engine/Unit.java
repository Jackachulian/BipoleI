package lib.engine;

import lib.Camera;
import lib.Colors;
import lib.DrawUtils;

import javax.swing.*;
import java.awt.*;

/** A unit that can exist on top of a tile in a battle. **/
public class Unit {
    /** The battle this unit is in. **/
    private final Battle battle;
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
    private double speed = 1.0;

    /** Whether this fighter is currently ready. **/
    private boolean ready;

    /** If this unit is currently auto acting. (Configurable by the player, if this unit type is not mustAutoAct) **/
    private boolean autoAct;

    public Unit(UnitData data, Battle battle, Player owner) {
        this.data = data;
        this.battle = battle;
        this.owner = owner;

        hp = data.hp;
        atk = data.atk;

        autoAct = data.isDefaultAutoAct();

        int delay = (int)(data.delay / speed);
        actionTimer = new Timer(delay, e -> onReady());
        actionTimer.setRepeats(data.isMustAutoAct());
    }

    /** Set this unit to ready when its action timer is done. Uses the first action if this is an auto-acting unit. **/
    public void onReady(){
        ready = true;
        if (data.isMustAutoAct()){
            act(0);
            ready = false;
            actionStartTime = System.currentTimeMillis();
            // timer repeats if this unit is auto acting, so the timer has already repeated, so no need to restart timer
        }
    }

    /** Effect to run when acting.
     * @param actionIndex the index of the action in this unit's data's action list to use.
     * @return true if the action could be used and was used; false if not **/
    public boolean act(int actionIndex){
        if (!ready) return false;
        if (data.getActions().get(actionIndex).act(this)) {
            resetCooldown();
            return true;
        } else {
            return false;
        }
    }

    /** Use up this fighter's action, restarting their action timer cooldown. **/
    public void resetCooldown(){
        ready = false;
        actionStartTime = System.currentTimeMillis();
        actionTimer.start();
    }

    /** Get the percentage that this unit is ready. Used in displaying. **/
    public double readinessPercent(){
        return Math.min((double)(System.currentTimeMillis() - actionStartTime) / data.delay, 1.0);
    }

    /** Draw this unit on the screen. **/
    public void draw(Graphics g, Tile tile){
        data.getMesh().draw(g, tile.getPolygon(), tile.getColor(), tile.getFaceColor(), Camera.zoom);
    }

    /** Draw UI elements associated with this unit. **/
    public void drawUI(Graphics g, Tile tile) {
        // Readiness bar
        if (!ready){
            DrawUtils.drawBar(g, tile.center.x, tile.center.y + Camera.zoom*0.25, readinessPercent(), Colors.READINESS_COLOR);
        }
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
