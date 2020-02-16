public class Ray {
    
    public Vector3 position;
    public Vector3 direction;

    public Ray(Vector3 position, Vector3 direction) {
        this.position = position;
        this.direction = direction;
    }

    public Vector3 pointAt(double t) {
        return this.position.add(this.direction.multiply(t));
    }

    public String toString() {
        return String.format("%s + %s * t", this.position, this.direction);
    }

}