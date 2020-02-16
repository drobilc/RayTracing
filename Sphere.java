class Sphere extends Node {
    
    Vector3 position;
    double radius;

    public Sphere(Vector3 center, double radius) {
        this.position = center;
        this.radius = radius;
    }

    public Vector3 intersection(Ray ray) {
        Vector3 a = ray.position.subtract(this.position);
        double first = ray.direction.dot(a);
        first = first * first;
        double second = ray.direction.dot(ray.direction) * (a.dot(a) - this.radius * this.radius);
        double result2 = first - second;
        if (result2 < 0)
            return null;
        
        double result1 = ray.direction.inverse().dot(a);
        double denominator = ray.direction.dot(ray.direction);

        double firstIntersection = (result1 + Math.sqrt(result2)) / denominator;
        double secondIntersection = (result1 - Math.sqrt(result2)) / denominator;

        double t = firstIntersection;
        if (secondIntersection > 0 && secondIntersection < firstIntersection)
            t = secondIntersection;
        else if (firstIntersection < 0)
            return null;

        return ray.pointAt(t);
    }

    public Vector3 normal(Vector3 point) {        
        return point.subtract(this.position).normalize();
    }

}