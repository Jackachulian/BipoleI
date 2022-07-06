package lib.engine;

/** An effect on a unit. A source can be specified.
 * toString is used to generate the text. **/
public abstract class Effect {
    /** The object this effect came from. No source means it is sourced from the unit itself. **/
    public Object source;

    /** The effect to run on a unit when this effect is applied. **/
    public abstract void apply(Unit unit);

    /** The effect to run on the unit when this effect is removed. **/
    public abstract void remove(Unit unit);
}
