package lib.elementboxes;

import java.awt.*;
import java.util.ArrayList;

public class TextElement extends ElementBox {
    public String[] text;
    public Font font = GAME_FONT;
    public boolean wrap;
    public boolean wrapCalced;

    public static final Font GAME_FONT_SMALL = new Font("monospace", Font.PLAIN, 13);
    public static final Font GAME_FONT = new Font("monospace", Font.PLAIN, 16);
    public static final Font GAME_FONT_BIG = new Font("monospace", Font.PLAIN, 20);

    public TextElement(String... text){
        super();
        this.text = text;
    }

    public TextElement() {
        super();
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (text.length == 0) return;
        if (wrap && !wrapCalced) calcWrapText(g);
        g.setColor(color);
        g.setFont(font);

        FontMetrics metrics = g.getFontMetrics(font);
        int lineHeight = metrics.getHeight();
        for (int i=0; i<text.length; i++){
            String line = text[i];

            int strX = x + (width - metrics.stringWidth(line)) / 2;
            int strY = y + (height - lineHeight*(text.length-1-i)*2 + metrics.getAscent()) / 2;

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
            if (lineWidth + wordWidth > width) {
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

        if (text.length * metrics.getAscent() > height){
            height = text.length * metrics.getAscent();
            resizeNeeded = true;
        }

        wrapCalced = true;
    }
}
