package lib.engine;

/** An action that a Unit can make, typically if they are currently ready. **/
public abstract class Action {
    public abstract void act(Battle battle, Unit unit);
}
