package lib.engine;

/** An action that a Unit can make, typically if they are currently ready. **/
public abstract class Action {
    /** Use this action. Returns true if the action could be used and wwas used; false if not. **/
    public abstract boolean act(Unit unit);
}
