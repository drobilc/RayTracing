class Vector3 {
    
    public double x, y, z;
    
    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3() {
        this(0, 0, 0);
    }

    public Vector3 add(Vector3 other) {
        return new Vector3(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vector3 subtract(Vector3 other) {
        return new Vector3(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public double dot(Vector3 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vector3 cross(Vector3 other) {
        return new Vector3(
            this.y * other.z - this.z * other.y,
            this.z * other.x - this.x * other.z,
            this.x * other.y - this.y * other.x
        );
    }

    public Vector3 multiply(Vector3 other) {
        return new Vector3(this.x * other.x, this.y * other.y, this.z * other.z);
    }

    public Vector3 multiply(double scalar) {
        return new Vector3(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public Vector3 inverse() {
        return this.multiply(-1);
    }

    public double norm() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public Vector3 normalize() {
        double norm = this.norm();
        return this.multiply(1 / norm);
    }

    public Vector3 clamp(double min, double max) {
        double x = Math.min(Math.max(min, this.x), max);
        double y = Math.min(Math.max(min, this.y), max);
        double z = Math.min(Math.max(min, this.z), max);
        return new Vector3(x, y, z);
    }

    public String toString() {
        return String.format("(%3.2f, %3.2f, %3.2f)", this.x, this.y, this.z);
    }

}