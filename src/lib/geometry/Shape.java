package lib.geometry;

import lib.Camera;
import lib.DrawUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Contains segments and faces to draw. **/
public class Shape {
    /** All faces to draw. (Drawn first) **/
    public final List<Face> faces;
    /** All segments to draw. (Drawn second) **/
    public final List<Segment> segments;
    /** The same as segments, but drawn the same color as the face.
     * Typically used to cover up segments below it from another face. (Drawn third) **/
    public final List<Segment> culledSegments;
    /** Sub-shapes to draw after this shape is drawn. **/
    public final List<Shape> children;

    public Shape() {
        this.faces = new ArrayList<>();
        this.culledSegments = new ArrayList<>();
        this.segments = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    /** Draw this shape at the given coordinates with the given color.
     * @param g the Graphics instance to draw with
     * @param polygon the Polygon of the tile's base with points [NW, SW, SE, NE].
     * @param segmentColor The color to draw segments on this shape.
     * @param faceColor The color to draw faces on this shape.
     */
    public void draw(Graphics g, Polygon polygon, Color segmentColor, Color faceColor, double zoom){
        // Initialize points array
        Point[] points = new Point[polygon.npoints];
        for (int i=0; i<points.length; i++){
            points[i] = new Point(polygon.xpoints[i], polygon.ypoints[i]);
        }

        // 1. draw faces
        g.setColor(faceColor);
        for (Face face : faces){
            Polygon facePolygon = new Polygon();
            for (Vertex v : face.vertices) {
                Point pos = vertPos(points, v, zoom);
                facePolygon.addPoint(pos.x, pos.y);
            }
            g.fillPolygon(facePolygon);
        }

        // 2. draw normal segments
        g.setColor(segmentColor);
        drawSegments(g, points, segments, zoom);

        // 3. draw culled segments
        g.setColor(faceColor);
        drawSegments(g, points, culledSegments, zoom);

        // 3. draw child shapes
        for (Shape shape : children){
            shape.draw(g, polygon, segmentColor, faceColor, zoom);
        }
    }

    private void drawSegments(Graphics g, Point[] points, List<Segment> segments, double zoom){
        //NW, NE, SE, SW
        for (Segment segment : segments){
            Point start = vertPos(points, segment.start, zoom);
            Point end = vertPos(points, segment.end, zoom);
            g.drawLine(start.x, start.y, end.x, end.y);
        }
    }

    /** Get the screen position of a vertex based on its base tile's polygon's points. **/
    private Point vertPos(Point[] points, Vertex vertex, double zoom) {
        Point nw = points[0], sw = points[1], se = points[2], ne = points[3];

        // Get the point on the base
        Point wr = DrawUtils.lerp(nw, sw, vertex.x+0.5);
        Point er = DrawUtils.lerp(ne, se, vertex.x+0.5);
        Point p = DrawUtils.lerp(wr, er, vertex.y+0.5);
        p.y += zoom * vertex.z * Camera.HEIGHT_Y_OFFSET;

        return p;
    }

    public void addFace(Face face){
        faces.add(face);
    }
    public void addFace(Vertex... vertices){
        faces.add(new Face(vertices));
    }
    public void addCulledSegment(Segment segment){
        culledSegments.add(segment);
    }
    public void addCulledSegment(Vertex start, Vertex end){
        culledSegments.add(new Segment(start, end));
    }
    public void addSegment(Segment segment){
        segments.add(segment);
    }
    public void addSegment(Vertex start, Vertex end){
        segments.add(new Segment(start, end));
    }
    public void addChild(Shape child){
        children.add(child);
    }

    // ==== Static method for creating a tile-shaped polygon (used for shop, nearly identical code can be found in tile's draw
    /** Create a polygon at the given screen coordinates. The northwestern corner of the tile will be located at the passed coordinates. **/
    public static Polygon tilePolygon(int x, int y, double z) {
        int
                nex = (int)(x + z* Camera.rowXOffset),
                swx = (int)(x + z* Camera.colXOffset),
                sex = (int)(x + z*(Camera.rowXOffset + Camera.colXOffset)), // ayo???
                ney = (int)(y + z * (Camera.rowYOffset)),
                swy = (int)(y + z * Camera.colYOffset),
                sey = (int)(y + z*(Camera.rowYOffset + Camera.colYOffset));

        Polygon polygon = new Polygon();
        polygon.addPoint(x, y);
        polygon.addPoint(nex, ney);
        polygon.addPoint(sex, sey);
        polygon.addPoint(swx, swy);
        return polygon;
    }

    // ==== Static methods for generating shapes
    /** Make a new rectangular prism
     * @param x x position of the center of the bottom face
     * @param y y position of the center of the bottom face
     * @param z z position of the center of the bottom face
     * @param width width of the prism
     * @param length length of the prism
     * @param height height of the prism
     * @return a rectangular prism shape to be drawn
     */
    public static Shape rectangularPrism(double x, double y, double z, double width, double length, double height, boolean cullBottom, boolean cullTop){
        // for simplicity of calculations
        width /= 2;
        length /= 2;

        Shape shape = new Shape();

        // Define vertices
        Vertex nel = new Vertex(x+width, y-length, z);
        Vertex swl = new Vertex(x-width, y+length, z);
        Vertex sel = new Vertex(x+width, y+length, z);
        Vertex nwh = new Vertex(x-width, y-length, z+height);
        Vertex neh = new Vertex(x+width, y-length, z+height);
        Vertex swh = new Vertex(x-width, y+length, z+height);
        Vertex seh = new Vertex(x+width, y+length, z+height);

        // Add faces
        shape.addFace(swl, sel, seh, swh);
        shape.addFace(nel, sel, seh, neh);
        shape.addFace(nwh, neh, seh, swh);

        // Add culled segments
        if (cullBottom) {
            shape.addCulledSegment(swl, sel);
            shape.addCulledSegment(sel, nel);
        }
        if (cullTop) {
            shape.addCulledSegment(nwh, swh);
            shape.addCulledSegment(neh, nwh);
            shape.addCulledSegment(swh, seh);
            shape.addCulledSegment(seh, neh);
        }

        // Add segments
        shape.addSegment(swl, sel);
        shape.addSegment(sel, nel);
        shape.addSegment(swl, swh);
        shape.addSegment(sel, seh);
        shape.addSegment(nel, neh);
        shape.addSegment(nwh, swh);
        shape.addSegment(neh, nwh);
        shape.addSegment(swh, seh);
        shape.addSegment(seh, neh);

        return shape;
    }
    public static Shape rectangularPrism(double x, double y, double z, double width, double length, double height){
        return rectangularPrism(x, y, z, width, length, height, false, false);
    }
    public static Shape rectangularPrism(double width, double length, double height){
        return rectangularPrism(0, 0, 0, width, length, height);
    }

    /** Make a new rectangular prism
     * @param x x position of the center of the bottom face
     * @param y y position of the center of the bottom face
     * @param z z position of the center of the bottom face
     * @param width width of the prism
     * @param length length of the prism
     * @param height height of the prism
     * @return a rectangular prism shape to be drawn
     */
    public static Shape triangularPrism(double x, double y, double z, double width, double length, double height){
        // for simplicity of calculations
        width /= 2;
        length /= 2;

        Shape shape = new Shape();

        // Define vertices
        Vertex nel = new Vertex(x+width, y-length, z);
        Vertex swl = new Vertex(x-width, y+length, z);
        Vertex sel = new Vertex(x+width, y+length, z);
        Vertex top = new Vertex(x-width, y-length, z+height);

        // Add faces
        shape.addFace(swl, sel, top);
        shape.addFace(nel, sel, top);

        // Add segments
        shape.addSegment(swl, sel);
        shape.addSegment(sel, nel);
        shape.addSegment(swl, top);
        shape.addSegment(sel, top);
        shape.addSegment(nel, top);

        return shape;
    }
    public static Shape triangularPrism(double width, double length, double height){
        return triangularPrism(0, 0, 0, width, length, height);
    }
}
