import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;
import javax.imageio.ImageIO;

class Raytracer {

    public static void render360View(int divisions, Object3d[] scene, Light light) throws Exception {
        double deltaAngle = 2 * Math.PI / divisions;
        for (int i = 0; i < divisions; i++) {
            double angle = i * deltaAngle;
            Vector3 cameraPosition = (new Vector3(Math.cos(angle), 0, Math.sin(angle))).multiply(6);
            Vector3 cameraRotaton = cameraPosition.inverse();
            Camera camera = new Camera(cameraPosition, cameraRotaton, new Vector3(0, 1, 0));
            System.out.println("Rendering frame " + i);
            render(scene, camera, light, 4, new File(String.format("renders/render%03d.png", i)));
        }
    }

    public static void main(String[] args) throws Exception {

        // Parse the OBJ file and convert it to array of Triangles
        Object3d[] scene = ObjLoader.parseFile(new File("floor_cube.obj"));

        // Create a camera at position (0, 0, -5), facing forward
        Camera camera = new Camera(new Vector3(0, 2, -6), new Vector3(0, 0, 1), new Vector3(0, 1, 0));

        // Construct a light, use a constructor that sets it at point p
        Light light = new Light(new Vector3(0, 4, -4));

        render(scene, camera, light, 4, new File("render.png"));
        // render360View(360, scene, light);
        
    }

    public static void render(Object3d[] scene, Camera camera, Light light, int subsamples, File file) throws Exception {
        BufferedImage image = new BufferedImage(camera.imageWidth, camera.imageHeight, BufferedImage.TYPE_INT_RGB);
        
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                Vector3 color = new Vector3();

                for (int k = 0; k < subsamples; k++) {
                    // First, we create a ray that is passing through pixel (i,j) on image
                    Ray ray = camera.rayThroughPixel(j, i, Math.random(), Math.random());

                    // Then we find the intersection of ray and objects on scene
                    Intersection intersection = findIntersection(ray, scene);

                    // Calculate the color using hit point and set it as pixel value
                    color = color.add(calculateColor(intersection, scene, light, camera));
                }

                color = color.multiply(1.0 / subsamples);

                Color rgbColor = new Color((float) color.x, (float) color.y, (float) color.z);
                image.setRGB(j, i, rgbColor.getRGB());
            }
        }
        ImageIO.write(image, "png", file);
    }

    public static Vector3 calculateColor(Intersection intersection, Object3d[] scene, Light light, Camera camera) {
        if (intersection == null)
            return light.ambientColor;
        
        // Send multiple shadow rays from hit point to the light source (assume it is spherical)
        int numberOfShadowRays = 6;
        int numberOfHits = 0;
        double lightSphereRadius = 0.5;
        for (int i = 0; i < numberOfShadowRays; i++) {
            // Generate a shadow ray inside a cone pointing to the light source
            double phi = Math.random() * 2 * Math.PI;
            double psi = Math.random() * Math.PI;
            
            Vector3 pointOnSphere = new Vector3(
                Math.sin(psi) * Math.cos(phi) * lightSphereRadius,
                Math.sin(psi) * Math.sin(phi) * lightSphereRadius,
                Math.cos(psi) * lightSphereRadius
            );
            Vector3 pointOnLightSphere = light.position.add(pointOnSphere);
            Ray shadowRay = new Ray(intersection.point, pointOnLightSphere.subtract(intersection.point));
            boolean inShadow = intersects(shadowRay, scene);
            if (inShadow)
                numberOfHits += 1;

        }
        double percentageOfRaysInShadow = (double) numberOfHits / numberOfShadowRays;
        
        Vector3 normal = intersection.object.normal(intersection.point);
        Vector3 lightDirection = light.position.subtract(intersection.point).normalize();
        Vector3 viewDirection = camera.position.subtract(intersection.point).normalize();
    
        Vector3 reflection = normal.multiply(2 * (lightDirection.dot(normal))).subtract(lightDirection);

        double diffuse = lightDirection.dot(normal);
        diffuse = Math.max(0, diffuse);
        double specular = Math.pow(Math.max(reflection.dot(viewDirection), 0), intersection.object.material.specularExponent);
        specular = Math.max(0, specular);

        Vector3 ambientColor = intersection.object.material.ambientColor.multiply(light.ambientColor);
        Vector3 diffuseColor = intersection.object.material.diffuseColor.multiply(light.diffuseColor).multiply(diffuse);
        Vector3 specularColor = intersection.object.material.specularColor.multiply(light.specularColor).multiply(specular);
        
        Vector3 color = ambientColor;
        color = color.add(diffuseColor.add(specularColor).multiply(1 - percentageOfRaysInShadow));
        
        return color.clamp(0, 1);
    }

    public static boolean intersects(Ray ray, Object3d[] scene) {
        for (int i = 0; i < scene.length; i++) {
            Vector3 intersectionPoint = scene[i].intersection(ray);
            if (intersectionPoint != null)
                return true;
        }
        return false;
    }

    public static Intersection findIntersection(Ray ray, Object3d[] scene) {
        Object3d closestIntersectingObject = null;
        Vector3 closestIntersectionPoint = null;
        double closestIntersectionPointNorm = Double.POSITIVE_INFINITY;

        for (int i = 0; i < scene.length; i++) {
            Object3d currentObject = scene[i];
            Vector3 intersectionPoint = currentObject.intersection(ray);
            if (intersectionPoint != null) {
                double norm = (intersectionPoint.subtract(ray.position)).norm();
                if (closestIntersectionPoint == null || norm <= closestIntersectionPointNorm) {
                    closestIntersectingObject = currentObject;
                    closestIntersectionPoint = intersectionPoint;
                    closestIntersectionPointNorm = norm;
                }
            }
        }

        if (closestIntersectingObject == null)
            return null;

        return new Intersection(closestIntersectingObject, closestIntersectionPoint);
    }

}