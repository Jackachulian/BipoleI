package lib;

import java.awt.*;

public class Colors {
    public static final int DIM_ALPHA = 150;
    public static final int PARTIAL_DIM_ALPHA = 200;

    public static final Color
            // Unit GUI
            TRANSPARENT = new Color(0,0,0,0),
            BAR_BG_COLOR = new Color(30, 30, 30, 200),
            BAR_BORDER_COLOR = new Color(8,8,8),
            READINESS_COLOR = new Color(236, 236, 236),

            // General ElementBoxes
            FG = new Color(244, 244, 244),
            FG_DIM = new Color(244, 244, 244, DIM_ALPHA),
            BG = new Color(8, 8, 8),
            BG_DIM = new Color(8, 8, 8, DIM_ALPHA),
            BORDER = new Color(236, 236, 236),
            BORDER_DIM = new Color(236, 236, 236, DIM_ALPHA),
            HOVER = new Color(150, 225, 150),
            SELECT = Color.GREEN,
            PRESELECT = new Color(125, 160, 110),
            PRESELECT_DIM = new Color(125, 160, 110, DIM_ALPHA),

            // Tiles
            ALLY = new Color(72, 132, 234),
            ENEMY = new Color(231, 75, 81),
            PLACEMENT_FADE = new Color(191, 191, 191, 200),
            VALID = new Color(31, 191, 95, PARTIAL_DIM_ALPHA),
            VALID_FACE = new Color(7, 44, 23, PARTIAL_DIM_ALPHA),
            INVALID = new Color(191, 31, 39, PARTIAL_DIM_ALPHA),
            INVALID_FACE = new Color(47, 6, 9, PARTIAL_DIM_ALPHA);

    public static Color blendColors(Color base, Color blend, double percent){
        if (percent < 0) percent = 0;
        if (percent > 1) percent = 1;
        return new Color(
                base.getRed() + (int)((blend.getRed()-base.getRed())*percent),
                base.getGreen() + (int)((blend.getGreen()-base.getGreen())*percent),
                base.getBlue() + (int)((blend.getBlue()-base.getBlue())*percent)
        );
    }
}
