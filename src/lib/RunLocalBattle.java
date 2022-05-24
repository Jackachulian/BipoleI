package lib;

import lib.data.Units;
import lib.engine.Battle;
import lib.engine.Player;

import java.awt.*;

public class RunLocalBattle {
    public static void main(String[] args) {
        GameFrame frame = new GameFrame();
        Player player = new Player(new Color(72, 132, 234));
        Player enemy = new Player(new Color(231, 75, 81));
        Battle battle = new Battle(player, enemy);

        battle.claimAndPlaceUnit(player, Units.CASTLE, 7, 0);
        battle.claim(player, 7, 1);
        battle.claimAndPlaceUnit(player, Units.FARMER, 6, 0);
        battle.claimAndPlaceUnit(player, Units.SOLDIER, 6, 1);

        battle.claimAndPlaceUnit(enemy, Units.CASTLE, 0, 7);
        battle.claim(enemy, 0, 6);
        battle.claimAndPlaceUnit(enemy, Units.FARMER, 1, 7);
        battle.claimAndPlaceUnit(enemy, Units.SOLDIER, 1, 6);

        frame.loadBattle(battle, player);
    }
}
