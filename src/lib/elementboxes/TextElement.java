package lib.elementboxes;

import java.awt.*;
import java.util.ArrayList;

public class TextElement extends ElementBox {
    public static final Font GAME_FONT_SMALL, GAME_FONT, GAME_FONT_LARGE;

    static {
        Font baseFont = new Font("monospace", Font.PLAIN, 12);
        GAME_FONT_SMALL = baseFont.deriveFont(14f);
        GAME_FONT = baseFont.deriveFont(18f);
        GAME_FONT_LARGE = baseFont.deriveFont(22f);
    }

    public String[] text;
    public Font font = GAME_FONT;
    public boolean wrap;
    public boolean wrapCalced;
    public Alignment textXAlign = Alignment.CENTER;
    public Alignment textYAlign = Alignment.CENTER;

    public enum Alignment {
        START, CENTER, END
    }

    public TextElement(String... text){
        super();
        this.text = text;
        bg = false;
    }

    public TextElement() {
        this(new String[0]);
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (text.length == 0) return;
        if (wrap && !wrapCalced) calcWrapText(g);
        g.setColor(undimmed || focused ? color : colorFaded);
        g.setFont(font);

        FontMetrics metrics = g.getFontMetrics(font);
        int lineHeight = metrics.getHeight();
        for (int i=0; i<text.length; i++){
            String line = text[i];

            int strX;
            if (textXAlign == Alignment.START) {
                strX = rect.x;
            } else if (textXAlign == Alignment.END) {
                strX = rect.x + rect.width - metrics.stringWidth(line);
            } else {
                strX = rect.x + (rect.width - metrics.stringWidth(line)) / 2;
            }


            int strY;
            if (textYAlign == Alignment.START) {
                strY = rect.y;
            } else if (textYAlign == Alignment.END) {
                strY = rect.y + rect.height - lineHeight*(text.length-1-i)*2 - metrics.getAscent();
            } else {
                strY = rect.y + (rect.height - lineHeight*(text.length-1-i)*2 + metrics.getAscent()) / 2;
            }

            g.drawString(line, strX, strY);
        }
    }

    public void setText(String... text){
        this.text = text;
    }

    /** Calculate the wrapping of this text.
     * Should be called if wrap is true but wrapCalced is false.
     * Calculates screen distance and wraps text into a new array of strings.
     * Also automatically sets height if it is lower than the calculated height. **/
    public void calcWrapText(Graphics g){
        if (!wrap || text == null) return;

        FontMetrics metrics = g.getFontMetrics(font);
        ArrayList<String> lines = new ArrayList<>();
        // Consolidate ALL words into one array
        String[] words = String.join(" ", text).split("\\s+");

        StringBuilder line = new StringBuilder();
        int lineWidth = 0;
        for (String word : words) {
            int wordWidth = metrics.stringWidth(word);
            if (lineWidth + wordWidth > rect.width) {
                lines.add(line.toString());
                line = new StringBuilder();
                lineWidth = 0;
            }

            lineWidth += wordWidth;
            line.append(word);
            line.append(" ");
        }

        lines.add(line.toString());
        text = lines.toArray(new String[0]);

        if (text.length * metrics.getAscent() > rect.height){
            rect.height = text.length * metrics.getAscent();
            resizeNeeded = true;
        }

        wrapCalced = true;
    }
}
