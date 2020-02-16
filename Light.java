class Light {

    public double ambientIntensity;
    public double diffuseIntensity;
    public double specularIntensity;

    public Vector3 ambientColor;
    public Vector3 diffuseColor;
    public Vector3 specularColor;

    public Vector3 position;

    public Light(Vector3 position, Vector3 ambientColor, double ambientIntensity, Vector3 diffuseColor, double diffuseIntensity, Vector3 specularColor, double specularIntensity) {
        this.position = position;
        this.ambientColor = ambientColor;
        this.ambientIntensity = ambientIntensity;
        this.diffuseColor = diffuseColor;
        this.diffuseIntensity = diffuseIntensity;
        this.specularColor = specularColor;
        this.specularIntensity = specularIntensity;
    }

    public Light() {
        this(new Vector3(0, 0, 0), new Vector3(1, 1, 1), 0.1, new Vector3(1, 1, 1), 0.8, new Vector3(1, 1, 1), 0.3);
    }

    public Light(Vector3 position) {
        this();
        this.position = position;
    }

}