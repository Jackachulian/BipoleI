package lib.geometry;

import java.awt.*;

/** A segment in 3D space to draw a line between two points. **/
public class Segment implements ShapeComponent {
    public final Vertex start;
    public final Vertex end;
    public final boolean culled;

    public Segment(Vertex start, Vertex end, boolean culled){
        this.start = start;
        this.end = end;
        this.culled = culled;
    }

    public Segment(Vertex start, Vertex end) {
        this(start, end, false);
    }

    @Override
    public void draw(Graphics g, Shape parent) {
        g.setColor(culled ? parent.faceColor : parent.segmentColor);
        Point startPos = parent.vertPos(start);
        Point endPos = parent.vertPos(end);
        g.drawLine(startPos.x, startPos.y, endPos.x, endPos.y);
    }
}
