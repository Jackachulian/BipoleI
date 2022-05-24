package lib;

import java.awt.*;

public class DrawUtils {
    public static void drawInsetTile(Graphics g, double x, double y, double z, double inset){
        int nwx = (int)(x + inset*z*(GamePanel.ROW_X_OFFSET + GamePanel.COL_X_OFFSET));
        int nex = (int)(x + (1-inset)*z*GamePanel.ROW_X_OFFSET + inset*z*GamePanel.COL_X_OFFSET);
        int swx = (int)(x + inset*z*GamePanel.ROW_X_OFFSET + (1-inset)*z*GamePanel.COL_X_OFFSET);
        int sex = (int)(x + (1-inset)*z*(GamePanel.ROW_X_OFFSET + GamePanel.COL_X_OFFSET));
        int nwy = (int)(y + inset*z*(GamePanel.ROW_Y_OFFSET + GamePanel.COL_Y_OFFSET));
        int ney = (int)(y + (1-inset)*z*GamePanel.ROW_Y_OFFSET + inset*z*GamePanel.COL_Y_OFFSET);
        int swy = (int)(y + inset*z*GamePanel.ROW_Y_OFFSET + (1-inset)*z*GamePanel.COL_Y_OFFSET);
        int sey = (int)(y + (1-inset)*z*(GamePanel.ROW_Y_OFFSET + GamePanel.COL_Y_OFFSET));

        g.drawLine(nwx, nwy, nex, ney); // northern border
        g.drawLine(nwx, nwy, swx, swy); // western border
        g.drawLine(swx, swy, sex, sey); // southern border
        g.drawLine(nex, ney, sex, sey); // eastern border
    }
    public static void drawInsetTile(Graphics g, Point pos, double z, double inset){
        drawInsetTile(g, pos.x, pos.y, z, inset);
    }
}
