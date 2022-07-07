package lib.data;

import lib.data.actions.Contest;
import lib.data.actions.Move;
import lib.data.actions.Sell;
import lib.engine.Action;

public class Actions {
    public static final Action
        CONTEST = new Contest(),
        SELL = new Sell(),
        MOVE = new Move();
}
