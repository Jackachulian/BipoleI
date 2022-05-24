package lib.geometry;

/** A segment in 3D space to draw a line between two points. **/
public class Segment {
    public final Vertex start;
    public final Vertex end;

    public Segment(Vertex start, Vertex end){
        this.start = start;
        this.end = end;
    }

    public Segment(int sx, int sy, int sz, int ex, int ey, int ez){
        start = new Vertex(sx, sy, sz);
        end = new Vertex(ex, ey, ez);
    }
}
