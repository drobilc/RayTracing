import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;
import javax.imageio.ImageIO;

class Raytracer {

    public static void main(String[] args) throws Exception {

        // Parse the OBJ file and convert it to array of Triangles
        Object3d[] scene = ObjLoader.parseFile(new File("renders/cubes.obj"));

        // Create a camera at position (0, 2, -6), facing forward
        Vector3 cameraPosition = new Vector3(0, 2, -6);
        Camera camera = new Camera(cameraPosition);

        // Construct a light, use a constructor that sets it at point p
        Light[] lights = { new Light(new Vector3(0, 4, -4)) };

        render(scene, camera, lights, 4, new File("renders/cubes.png"));
        
    }

    public static void render(Object3d[] scene, Camera camera, Light[] lights, int subsamples, File file) throws Exception {
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
                    color = color.add(calculateColor(intersection, scene, lights, camera, 2));
                }

                color = color.multiply(1.0 / subsamples);

                Color rgbColor = new Color((float) color.x, (float) color.y, (float) color.z);
                image.setRGB(j, i, rgbColor.getRGB());
            }
        }
        ImageIO.write(image, "png", file);
    }

    public static Vector3 calculateColor(Intersection intersection, Object3d[] scene, Light[] lights, Camera camera, int n) {
        Vector3 ambientLight = lights[0].ambientColor;
        if (intersection == null || n <= 0)
            return ambientLight;
        
        // Define some constants, that can be used in this function
        Object3d object = intersection.object;
        Material material = object.material;

        // Calculate the normal and view vector
        Vector3 normal = object.normal(intersection.point);
        Vector3 viewDirection = camera.position.subtract(intersection.point).normalize();
        
        // At the beginning, the color is simply ambient color
        Vector3 color = material.ambientColor.multiply(ambientLight);

        Vector3 phongColor = new Vector3();
        
        int totalHits = 0;
        int numberOfShadowRays = 8;
        double lightSphereRadius = 0.1;

        for (int i = 0; i < lights.length; i++) {
            Vector3 lightDirection = lights[i].position.subtract(intersection.point).normalize();
            Vector3 reflection = normal.multiply(2 * lightDirection.dot(normal)).subtract(lightDirection);
            
            double diffuse = Math.max(lightDirection.dot(normal), 0);
            Vector3 diffuseColor = material.diffuseColor.multiply(diffuse).multiply(lights[i].diffuseColor);

            double specular = Math.pow(Math.max(reflection.dot(viewDirection), 0), material.specularExponent);
            Vector3 specularColor = material.specularColor.multiply(specular).multiply(lights[i].specularColor);
        
            // Send multiple shadow rays from hit point to the light source (assume it is spherical)
            for (int j = 0; j < numberOfShadowRays; j++) {
                double phi = Math.random() * 2 * Math.PI;
                double psi = Math.random() * Math.PI;

                Vector3 pointOnSphere = new Vector3(
                    Math.sin(psi) * Math.cos(phi) * lightSphereRadius,
                    Math.sin(psi) * Math.sin(phi) * lightSphereRadius,
                    Math.cos(psi) * lightSphereRadius
                );
                Vector3 pointOnLightSphere = lights[i].position.add(pointOnSphere);
                Ray shadowRay = new Ray(intersection.point, pointOnLightSphere.subtract(intersection.point));
                boolean inShadow = intersects(shadowRay, scene);
                if (inShadow)
                    totalHits += 1;
            }

            phongColor = phongColor.add(diffuseColor.add(specularColor));
        }
        double shadowFactor = (double) totalHits / (numberOfShadowRays * lights.length);
        color = color.add(phongColor.multiply(1 - shadowFactor));

        if (material.illuminationModel == 3) {
            Vector3 d = intersection.ray.direction.normalize();
            Vector3 rayReflectionDirection = d.subtract(normal.multiply(2 * normal.dot(d)));
            Ray reflectionRay = new Ray(intersection.point, rayReflectionDirection);
            Intersection reflectionRayIntersection = findIntersection(reflectionRay, scene);
            Vector3 reflectionColor = calculateColor(reflectionRayIntersection, scene, lights, camera, n - 1);
            color = color.add(reflectionColor.multiply(material.specularColor));
        }
        
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

        return new Intersection(ray, closestIntersectingObject, closestIntersectionPoint);
    }

}