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
    /** The tile this unit is currently on. **/
    private Tile tile;
    /** The type of unit this is. **/
    private final UnitData data;
    /** The player that owns and controls this unit (not the tile it is on). **/
    private final Player owner;

    /** Timer before this unit can act again. **/
    private final Timer actionTimer;
    /** Time since this user last acted or began becoming ready. **/
    private long actionStartTime;

    /** Amount of damage this unit can take before destroyed. (No max) **/
    private int hp;
    /** Amount of damage this unit deals when attacking. **/
    private int atk;
    /** Amount this fighter's act speed is increased by. 1 is 100%. **/
    private double speed = 1.0;

    /** Whether this fighter is currently ready. **/
    private boolean ready;

    /** If this unit is currently auto acting. (Configurable by the player, if this unit type is not mustAutoAct) **/
    private boolean autoAct;

    /** The tile this unit is targeting for its action. If auto attacking this may be used.
     * If manual, this is set before attacking by moving the cursor to attack a unit. **/
    private Tile target;

    public Unit(UnitData data, Battle battle, Player owner) {
        this.data = data;
        this.battle = battle;
        this.owner = owner;

        hp = data.hp;
        atk = data.atk;

        autoAct = data.isDefaultAutoAct();

        int delay = getDelay();
        actionTimer = new Timer(delay, e -> onReady());
        actionTimer.setRepeats(data.isMustAutoAct());
    }

    /** Set this unit to ready when its action timer is done. Uses the first action if this is an auto-acting unit. **/
    public void onReady(){
        ready = true;
        if (data.isMustAutoAct()){
            autoAct();
            ready = false;
            actionStartTime = System.currentTimeMillis();
            // timer repeats if this unit is auto acting, so the timer has already repeated, so no need to restart timer
        }
    }

    /** Get the total time it takes for this unit to become ready after acting. **/
    public int getDelay() {
        return (int)(data.delay / speed);
    }

    /** Get the amount of milliseconds until this unit is ready. **/
    public int getCooldown() {
        return Math.max((int) (actionStartTime + data.delay - System.currentTimeMillis()), 0);
    }

    /** The base amount of points this tile is worth. Increases when upgraded. **/
    public int value() {
        return data.value;
    }

    /** Points earned by selling this unit ((1/3 * value) * (HP% + 25%)). Rounded up. **/
    public int sellValue() {
        return (int)Math.ceil((value() / 3.0) * (1.0 * hp / data.hp  + 0.25));
    }

    /** Points earned by defeating this unit (1/2 value). **/
    public int defeatValue() {
        return (int)Math.ceil(0.5 * value());
    }

    /** Effect to run when auto acting.
     * @return true if the action could be used and was used; false if not **/
    public boolean autoAct(){
        return tile.act(owner, data.getAction());
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

    /** Deal damage to another tile.
     * If there is a unit, it is attacked and becomes unclaimed if unit is defeated.
     * If not, destroy the tile. **/
    public void dealDamage(Tile target, int amount) {
        target.takeDamage(tile, amount);
    }

    /** Deal damage to the selected target. **/
    public void dealDamage(int amount) {
        dealDamage(target, amount);
    }

    /** Take damage from another unit. **/
    public void takeDamage(Tile attacker, int amount) {

    }

    /** Draw this unit on the screen. **/
    public void draw(Graphics g, Tile tile){
        data.getMesh().draw(g, tile.getPolygon(), tile.getBase(), tile.getColor(), tile.getFaceColor(), Camera.zoom);
    }

    /** Draw UI elements associated with this unit. **/
    public void drawUI(Graphics g, Tile tile) {
        // Readiness bar
        if (!ready){
            DrawUtils.drawBar(g, tile.center.x, tile.center.y + Camera.zoom*0.25, readinessPercent(), Colors.READINESS_COLOR);
        }
    }

    public boolean ownedBy(Player player) {
        return owner == player;
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

    public int getHp() {
        return hp;
    }

    public int getAtk() {
        return atk;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public Tile getTarget() {
        return target;
    }

    public void setTarget(Tile target) {
        this.target = target;
    }
}
