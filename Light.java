class Light {

    public Vector3 ambientColor;
    public Vector3 diffuseColor;
    public Vector3 specularColor;

    public Vector3 position;

    public Light(Vector3 position, Vector3 ambientColor, Vector3 diffuseColor, Vector3 specularColor) {
        this.position = position;
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
    }

    public Light() {
        this(new Vector3(0, 0, 0), new Vector3(0.06, 0.06, 0.06), new Vector3(1, 1, 1), new Vector3(1, 1, 1));
    }

    public Light(Vector3 position) {
        this();
        this.position = position;
    }

}