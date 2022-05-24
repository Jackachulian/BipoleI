package lib.geometry;

/** A single vertex in 3D space to be used in points for segments and faces. **/
public class Vertex {
    /** X=row, Y=column, Z=height **/
    public final double x, y, z;

    public Vertex(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
