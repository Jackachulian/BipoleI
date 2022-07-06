package lib.geometry;

import lib.Camera;
import lib.DrawUtils;
import lib.GuiConstants;
import lib.engine.Corners;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Contains segments and faces to draw. **/
public class Shape {
    /** All components to draw. **/
    public final List<ShapeComponent> components;

    /** Corner heights for this tile. **/
    protected Corners corners;
    /** Screen position of NW corner. **/
    protected Point nw;
    /** Screen position of SW corner. **/
    protected Point sw;
    /** Screen position of SE corner. **/
    protected Point se;
    /** Screen position of NE corner. **/
    protected Point ne;
    /** The zoom this shape is drawn at. **/
    protected double zoom;
    /** Color for segments to be drawn in. **/
    protected Color segmentColor;
    /** Color for faces to be drawn in. **/
    protected Color faceColor;

    public Shape() {
        components = new ArrayList<>();
    }

    /** Draw this shape at the given coordinates with the given color.
     * @param g the Graphics instance to draw with
     * @param polygon the Polygon of the tile's base with points [NW, SW, SE, NE].
     * @param segmentColor The color to draw segments on this shape.
     * @param faceColor The color to draw faces on this shape.
     */
    public void draw(Graphics g, Polygon polygon, Corners corners, Color segmentColor, Color faceColor, double zoom){
        // Initialize shape properties
        this.corners = corners;
        nw = new Point(polygon.xpoints[0], polygon.ypoints[0]);
        sw = new Point(polygon.xpoints[1], polygon.ypoints[1]);
        se = new Point(polygon.xpoints[2], polygon.ypoints[2]);
        ne = new Point(polygon.xpoints[3], polygon.ypoints[3]);
        this.zoom = zoom;
        this.segmentColor = segmentColor;
        this.faceColor = faceColor;

        // Draw all components, in order
        for (ShapeComponent component : components) {
            component.draw(g, this);
        }
    }

    public Point vertPos(Vertex v) {
        // Get the point on the base

        Point wr = DrawUtils.lerp(nw, sw, v.x+0.5); // lerp between NW and SW for western side
        Point er = DrawUtils.lerp(ne, se, v.x+0.5); // lerp between NE and SE for eastern side
        Point point = DrawUtils.lerp(wr, er, v.y+0.5); // lerp between W and E for base position
        point.y += zoom * v.z * Camera.HEIGHT_Y_OFFSET; // add height to y position

//      Unused trigonometry-based unit angling code, semi-working but leaving out because units may clip adjacent walls if angled into them
//        double rowAngle = Math.atan((corners.right() - corners.front()) * -Camera.DEPTH_Y_OFFSET);
//        double colAngle = Math.atan((corners.left() - corners.front()) * -Camera.DEPTH_Y_OFFSET);
//
//        double rowX = -Math.sin( rowAngle );
//        double rowY = Math.cos( rowAngle );
//
//        double colX = -Math.sin( colAngle );
//        double colY = Math.cos( colAngle );
//
//        point.x += zoom * v.z * -Camera.HEIGHT_Y_OFFSET * ((rowX * Camera.rowXOffset) + (colX * Camera.colXOffset));
//        point.y -= zoom * v.z * -Camera.HEIGHT_Y_OFFSET * ((rowY * Camera.rowYOffset) + (colY * Camera.colYOffset));

        return point;
    }

   public void add(ShapeComponent component) {
        components.add(component);
   }
   public void addFace(Vertex... vertices) {
        add(new Face(vertices));
   }
    public void addSegment(Vertex start, Vertex end) {
        add(new Segment(start, end));
    }
    public void addCulledSegment(Vertex start, Vertex end) {
        add(new Segment(start, end, true));
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
        Vertex nwl = new Vertex(x-width, y-length, z);
        Vertex nel = new Vertex(x+width, y-length, z);
        Vertex swl = new Vertex(x-width, y+length, z);
        Vertex sel = new Vertex(x+width, y+length, z);
        Vertex top = new Vertex(x, y, z+height);

        // These five are only visible when rotating, draw first so that they are overlapped
        shape.addSegment(swl, nwl);
        shape.addSegment(nel, nwl);
        shape.addFace(swl, nwl, top);
        shape.addFace(nel, nwl, top);
        shape.addSegment(nwl, top);

        // Add faces
        shape.addFace(swl, sel, top);
        shape.addFace(nel, sel, top);

        // Add segments
        shape.addSegment(swl, sel);
        shape.addSegment(nel, sel);
        shape.addSegment(swl, top);
        shape.addSegment(sel, top);
        shape.addSegment(nel, top);

        return shape;
    }
    public static Shape triangularPrism(double width, double length, double height){
        return triangularPrism(0, 0, 0, width, length, height);
    }
}
