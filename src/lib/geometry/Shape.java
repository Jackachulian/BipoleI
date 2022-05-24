package lib.geometry;

import lib.GamePanel;

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
     * @param x X position of top corner of tile (northwestern corner)
     * @param y Y position of top corner of tile (northwestern corner)
     * @param segmentColor The color to draw segments on this shape.
     * @param faceColor The color to draw faces on this shape.
     */
    public void draw(Graphics g, double x, double y, double z, Color segmentColor, Color faceColor){
        // 1. draw faces
        g.setColor(faceColor);
        for (Face face : faces){
            int[] xPoints = new int[face.vertices.length];
            int[] yPoints = new int[face.vertices.length];
            for (int i=0; i<face.vertices.length; i++){
                xPoints[i] = (int)(x + z*(face.vertices[i].x*GamePanel.ROW_X_OFFSET + face.vertices[i].y*GamePanel.COL_X_OFFSET));
                yPoints[i] = (int)(y + z*(face.vertices[i].x*GamePanel.ROW_Y_OFFSET + face.vertices[i].y*GamePanel.COL_Y_OFFSET + face.vertices[i].z*GamePanel.HEIGHT_Y_OFFSET));
            }
            g.fillPolygon(xPoints, yPoints, face.vertices.length);
        }

        // 2. draw normal segments
        g.setColor(segmentColor);
        drawSegments(g, x, y, z, segments);

        // 3. draw culled segments
        g.setColor(faceColor);
        drawSegments(g, x, y, z, culledSegments);

        // 3. draw child shapes
        for (Shape shape : children){
            shape.draw(g, x, y, z, segmentColor, faceColor);
        }
    }

    private void drawSegments(Graphics g, double x, double y, double z, List<Segment> segments){
        for (Segment segment : segments){
            g.drawLine(
                    (int)(x + z*(segment.start.x*GamePanel.ROW_X_OFFSET + segment.start.y*GamePanel.COL_X_OFFSET)),
                    (int)(y + z*(segment.start.x*GamePanel.ROW_Y_OFFSET + segment.start.y*GamePanel.COL_Y_OFFSET + segment.start.z*GamePanel.HEIGHT_Y_OFFSET)),
                    (int)(x + z*(segment.end.x*GamePanel.ROW_X_OFFSET + segment.end.y*GamePanel.COL_X_OFFSET)),
                    (int)(y + z*(segment.end.x*GamePanel.ROW_Y_OFFSET + segment.end.y*GamePanel.COL_Y_OFFSET + segment.end.z*GamePanel.HEIGHT_Y_OFFSET))
            );
        }
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
