package lib.data;

import lib.data.units.Castle;
import lib.data.units.Farmer;
import lib.data.units.Soldier;
import lib.data.units.Tractor;
import lib.engine.UnitData;

public class Units {
    public static final UnitData
        CASTLE = new Castle(),
        FARMER = new Farmer(),
        SOLDIER = new Soldier(),
        TRACTOR = new Tractor();
}
