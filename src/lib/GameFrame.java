package lib;

import lib.engine.Battle;
import lib.engine.Player;

import javax.swing.*;

public class GameFrame extends JFrame {

    public GameFrame() {
        super("Bipole I");
        setBounds(0, 0, 960, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void loadBattle(Battle battle, Player player){
        setContentPane(new GamePanel(battle, player));
        setVisible(true);
    }
}
