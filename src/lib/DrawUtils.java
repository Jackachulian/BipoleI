package lib;

import java.awt.*;

public class DrawUtils {
    public static void drawBar(Graphics g, double x, double y, double percent, Color fillColor){
        int width = (int)Camera.zoom;
        int height = (int)(Camera.zoom*0.1);
        int hw = width/2;
        int hh = height/2;
        int barWidth = (int)(Camera.zoom*percent);

        g.setColor(Colors.BAR_BG_COLOR);
        g.fillRect((int)x-hw, (int)y-hh, width, height);

        g.setColor(fillColor);
        g.fillRect((int)x-hw, (int)y-hh, barWidth, height);

        g.setColor(Colors.BAR_BORDER_COLOR);
        g.drawRect((int)x-hw, (int)y-hh, width, height);
    }

    public static void drawCenteredString(Graphics g, Rectangle rect, String text, Color textColor, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();

        g.setFont(font);
        if (textColor != null) g.setColor(textColor);
        g.drawString(text, x, y);
    }

    public static void drawInsetTile(Graphics g, double x, double y, NumberCorners corners, double inset){
//        int nwx = (int)(x + inset*z*(GuiConstants.ROW_X_OFFSET + GuiConstants.COL_X_OFFSET));
//        int nex = (int)(x + (1-inset)*z* GuiConstants.ROW_X_OFFSET + inset*z* GuiConstants.COL_X_OFFSET);
//        int swx = (int)(x + inset*z* GuiConstants.ROW_X_OFFSET + (1-inset)*z* GuiConstants.COL_X_OFFSET);
//        int sex = (int)(x + (1-inset)*z*(GuiConstants.ROW_X_OFFSET + GuiConstants.COL_X_OFFSET)); // funny
//
//        int nwy = (int)(y - z*GuiConstants.DEPTH_Y_OFFSET*corners.nw() + inset*z*(GuiConstants.ROW_Y_OFFSET + GuiConstants.COL_Y_OFFSET));
//        int ney = (int)(y - z*GuiConstants.DEPTH_Y_OFFSET*corners.ne() + (1-inset)*z* GuiConstants.ROW_Y_OFFSET + inset*z* GuiConstants.COL_Y_OFFSET);
//        int swy = (int)(y - z*GuiConstants.DEPTH_Y_OFFSET*corners.sw() + inset*z* GuiConstants.ROW_Y_OFFSET + (1-inset)*z* GuiConstants.COL_Y_OFFSET);
//        int sey = (int)(y - z*GuiConstants.DEPTH_Y_OFFSET*corners.se() + (1-inset)*z*(GuiConstants.ROW_Y_OFFSET + GuiConstants.COL_Y_OFFSET));

        double
                bnwx = x,
                bnex = (x + Camera.zoom * Camera.rowXOffset),
                bswx = (x + Camera.zoom * Camera.colXOffset),
                bsex = (x + Camera.zoom * ( Camera.rowXOffset + Camera.colXOffset)),
                bnwy = (y - Camera.zoom * Camera.DEPTH_Y_OFFSET*corners.nw()),
                bney = (y + Camera.zoom * (Camera.rowYOffset - Camera.DEPTH_Y_OFFSET*corners.ne())),
                bswy = (y + Camera.zoom * (Camera.colYOffset - Camera.DEPTH_Y_OFFSET*corners.sw())),
                bsey = (y + Camera.zoom * (Camera.rowYOffset + Camera.colYOffset - Camera.DEPTH_Y_OFFSET*corners.se()));

        int
                nwx = (int)(bnwx + inset*(bsex-bnwx)),
                nex = (int)(bnex + inset*(bswx-bnex)),
                swx = (int)(bswx + inset*(bnex-bswx)),
                sex = (int)(bsex + inset*(bnwx-bsex)),
                nwy = (int)(bnwy + inset*(bsey-bnwy)),
                ney = (int)(bney + inset*(bswy-bney)),
                swy = (int)(bswy + inset*(bney-bswy)),
                sey = (int)(bsey + inset*(bnwy-bsey));

        g.drawLine(nwx, nwy, nex, ney); // northern border
        g.drawLine(nwx, nwy, swx, swy); // western border
        g.drawLine(swx, swy, sex, sey); // southern border
        g.drawLine(nex, ney, sex, sey); // eastern border
    }
    public static void drawInsetTile(Graphics g, Point pos, NumberCorners corners, double inset){
        drawInsetTile(g, pos.x, pos.y, corners, inset);
    }

    /** Interpolate two double values by the given percentage. **/
    public static double lerp(double a, double b, double f) {
        return a + f*(b-a);
    }

    /** Interpolate two int values by the given percentage. **/
    public static int lerp(int a, int b, double f) {
        return (int)(a + f*(b-a));
    }

    /** Interpolate two int points by the given percentage. **/
    public static Point lerp(Point a, Point b, double f){
        return new Point(lerp(a.x, b.x, f), lerp(a.y, b.y, f));
    }
}
