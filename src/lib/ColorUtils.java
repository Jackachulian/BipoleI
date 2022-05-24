package lib;

import java.awt.*;

public class ColorUtils {
    public static Color blendColors(Color base, Color blend, double percent){
        return new Color(
                base.getRed() + (int)((blend.getRed()-base.getRed())*percent),
                base.getGreen() + (int)((blend.getGreen()-base.getGreen())*percent),
                base.getBlue() + (int)((blend.getBlue()-base.getBlue())*percent)
        );
    }
}
