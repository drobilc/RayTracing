class Intersection {
    
    public Object3d object;
    public Vector3 point;
    public Ray ray;

    public Intersection(Ray ray, Object3d object, Vector3 point) {
        this.ray = ray;
        this.object = object;
        this.point = point;
    }
}