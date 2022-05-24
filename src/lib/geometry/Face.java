package lib.geometry;

/** A square face with given points to draw between. (Does not draw borders.) **/
public class Face {
    public Vertex[] vertices;

    public Face(Vertex... vertices){
        this.vertices = vertices;
    }
}
