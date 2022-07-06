package lib.elementboxes;

import java.awt.*;

public class StatElement extends ElementBox {
    public TextElement header;
    public TextElement value;

    public StatElement(String headerText) {
        displayType = DisplayType.FLEX_COLUMN;
        fillY = true;
        setBorder(true);
        bg = false;

        header = new TextElement();
        header.fillX = true;
        header.font = TextElement.GAME_FONT_SMALL;
        header.setText(headerText);
        addChild(this.header);

        value = new TextElement();
        value.marginBottom = 4;
        value.fillX = true;
        value.font = TextElement.GAME_FONT;
        addChild(value);
    }

    public void setValue(String text) {
        value.setText(text);
    }
}
