package lib.geometry;

import java.awt.*;

/** A square face with given points to draw between. (Does not draw borders.) **/
public class Face implements ShapeComponent {
    public Vertex[] vertices;

    public Face(Vertex... vertices){
        this.vertices = vertices;
    }

    @Override
    public void draw(Graphics g, Shape parent) {
        g.setColor(parent.faceColor);
        Polygon facePolygon = new Polygon();
        for (Vertex v : vertices) {
            Point pos = parent.vertPos(v);
            facePolygon.addPoint(pos.x, pos.y);
        }
        g.fillPolygon(facePolygon);
    }
}
