public class Camera {
    
    public Vector3 position;
    public Vector3 forward, up, right;

    // Camera parameters
    double focalLength = 8;
    double imageSensorWidth = 19.2;
    double imageSensorHeight = 10.8;
    
    int imageWidth = 1920;
    int imageHeight = 1080;
    
    double imageLeft = -imageSensorWidth / 2;
    double imageRight = imageSensorWidth / 2;
    double imageBottom = -imageSensorHeight / 2;
    double imageTop = imageSensorHeight / 2;

    public Camera(Vector3 position, Vector3 forward, Vector3 up) {
        this.position = position;
        this.forward = forward.normalize();
        this.up = up.normalize();
        this.right = this.forward.cross(this.up).normalize();
    }

    public Camera() {
        this(new Vector3(0, 0, 0), new Vector3(0, 0, 1), new Vector3(0, 1, 0));
    }

    public Camera(Vector3 position) {
        this(position, new Vector3(0, 0, 1), new Vector3(0, 1, 0));
    }

    public Ray rayThroughPixel(int x, int y) {
        return this.rayThroughPixel(x, y, 0.5, 0.5);
    }

    public Ray rayThroughPixel(int x, int y, double u, double v) {
        // The (u, v) is a coordinate in [0,1]^2 for subsampling
        Vector3 rayDirection1 = this.right.multiply(imageLeft + (imageRight - imageLeft) * ((x + u) / imageWidth));
        Vector3 rayDirection2 = this.up.multiply(imageBottom + (imageTop - imageBottom) * ((y + v) / imageHeight));
        Vector3 rayDirection3 = this.forward.multiply(focalLength);
        Vector3 rayDirection = rayDirection1.add(rayDirection2.inverse()).add(rayDirection3);
        return new Ray(this.position, rayDirection);
    }

}