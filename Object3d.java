abstract class Object3d {
    
    public Material material;

    public Object3d(Material material) {
        this.material = material;
    }

    abstract public Vector3 normal(Vector3 point);
    abstract public Vector3 intersection(Ray ray);

}