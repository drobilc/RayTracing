public class Vertex {
    
    public Vector3 position;
    public Vector3 textureCoordinate;
    public Vector3 normal;
    
    public Vertex(Vector3 position, Vector3 textureCoordinate, Vector3 normal) {
        this.position = position;
        this.textureCoordinate = textureCoordinate;
        this.normal = normal;
    }

    public String toString() {
        return String.format("<%s, %s, %s>", this.position, this.textureCoordinate, this.normal);
    }

}