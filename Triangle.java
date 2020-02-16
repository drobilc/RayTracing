public class Triangle extends Object3d {

    public Vertex v1, v2, v3;

    public Triangle(Vertex v1, Vertex v2, Vertex v3, Material material) {
        super(material);
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public Vector3 toBarycentricCoordinates(Vector3 point) {
        Vector3 edge1 = this.v2.position.subtract(this.v1.position);
        Vector3 edge2 = this.v3.position.subtract(this.v1.position);
        Vector3 v2 = point.subtract(this.v1.position); 

        double d00 = edge1.dot(edge1);
        double d01 = edge1.dot(edge2);
        double d11 = edge2.dot(edge2);

        double d20 = v2.dot(edge1);
        double d21 = v2.dot(edge2);

        double denominator = d00 * d11 - d01 * d01;

        double v = (d11 * d20 - d01 * d21) / denominator;
        double w = (d00 * d21 - d01 * d20) / denominator;
        double u = 1.0 - v - w;
        return new Vector3(u, v, w);
    }

    public Vector3 normal(Vector3 point) {
        /*Vector3 barycentricCoordinates = this.toBarycentricCoordinates(point);
        Vector3 normal1 = this.v1.normal.multiply(barycentricCoordinates.x);
        Vector3 normal2 = this.v2.normal.multiply(barycentricCoordinates.y);
        Vector3 normal3 = this.v3.normal.multiply(barycentricCoordinates.z);
        return normal1.add(normal2).add(normal3);*/
        return this.v1.normal.normalize();
    }

    public Vector3 intersection(Ray ray) {
        // We are using the Moller-Trumbore intersection algorithm
        double epsilon = 0.0000001;

        Vector3 edge1 = this.v2.position.subtract(this.v1.position);
        Vector3 edge2 = this.v3.position.subtract(this.v1.position);

        Vector3 h = ray.direction.cross(edge2);
        double a = edge1.dot(h);

        if (a > -epsilon && a < epsilon)
            return null;
        
        double f = 1.0 / a;
        Vector3 s = ray.position.subtract(this.v1.position);
        double u = f * s.dot(h);

        if (u < 0.0 || u > 1.0)
            return null;
        
        Vector3 q = s.cross(edge1);
        double v = f * ray.direction.dot(q);

        if (v < 0.0 || u + v > 1.0)
            return null;
        
        double t = f * edge2.dot(q);
        if (t > epsilon)
            return ray.pointAt(t);
        
        return null;
    }

    public String toString() {
        return String.format("[%s, %s, %s]", this.v1, this.v2, this.v3);
    }

}