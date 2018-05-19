package kepler;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * The class that represents the satellite that orbits the planet
 */
class Satellite {
    /**
     * The height and width of the ellipse that is the satellite
     */
    private static final int SATELLITE_HEIGHT_WIDTH = 30;
    
    /**
     * The last time of System.nanoTime() that was recorded last
     */
    private long lastTime;
    
    /**
     * The physics gravitational constant accurate to some degree
     */
    private static final double GRAVITATIONAL_CONSTANT = 6.671281903963040991511534289 * Math.pow(10, -11);
    
    /**
     * The angle from 0 to 2π of the satellite's center relative to the positive x-axis with an origin at the planet's center
     */
    private double angle = 0;
    
    /**
     * The instantaneous distance between the center of the elliptical orbit and the satellite in pixels
     */
    private double visualRadius;
    
    /**
     * The actual given radii of the semi-major and semi-minor axes of the ellipse in meters
     */
    private double radiusMajor, radiusMinor;
    
    /**
     * The scaled radii of the semi-major and semi-minor axes of the ellipse in pixels
     */
    private double radiusMajorVisual, radiusMinorVisual;
    
    /**
     * Represents whether or not it is the first time that the Satellite.draw() method has been called
     */
    private boolean isFirstTime = true;
    
    /**
     * The ellipse representing the orbit that the satellite takes
     */
    private Ellipse2D orbit;
    
    /**
     * Thickness of the line drawn for the orbit
     */
    private BasicStroke orbitThickness = new BasicStroke(2);
    
    /**
     * Represents the planet that the satellite orbits
     */
    private Planet planet;
    
    /**
     * Image that shows the satellite that orbits the planet (actually a planet but whatever)
     */
    private static BufferedImage satelliteImage;
    
    /**
     * Satellite constructor that sets the actual and visual axes, gets the satellite image, and sets up the orbit ellipse
     * @param radiusOne  One of the actual radii of the elliptical orbits in meters
     * @param radiusTwo  One of the actual radii of the elliptical orbits in meters
     */
    Satellite(double radiusOne, double radiusTwo) {
        try {
            satelliteImage = ImageIO.read(Runner.class.getResource("/planet.png"));
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        
        //Sets the major axis to the larger of the two given radii
        radiusMajor = Math.max(radiusOne, radiusTwo);
        radiusMinor = radiusOne + radiusTwo - radiusMajor;
        
        //Sets visual major and minor axes based on which way the full elliptical orbit fits
        if(radiusMinor / radiusMajor * (Runner.frameWidth()/2 - SATELLITE_HEIGHT_WIDTH - 20) <= Runner.frameHeight()/2 - SATELLITE_HEIGHT_WIDTH - 20) {
            radiusMajorVisual = Runner.frameWidth()/2 - SATELLITE_HEIGHT_WIDTH - 20;
            radiusMinorVisual = radiusMinor / radiusMajor * radiusMajorVisual;
        }
        else {
            radiusMinorVisual = Runner.frameHeight()/2 - SATELLITE_HEIGHT_WIDTH - 20;
            radiusMajorVisual = radiusMajor / radiusMinor * radiusMinorVisual;
        }
        planet = Runner.getPlanet();
        planet.setCenterCoordinates(this);
        changeVisualRadius();
        System.out.println(visualRadius);
        orbit = new Ellipse2D.Double((Runner.frameWidth() - 2 * radiusMajorVisual)/2.0, (Runner.frameHeight() - 2 * radiusMinorVisual)/2.0, 2 * radiusMajorVisual, 2 * radiusMinorVisual);
        System.out.println(orbit.getY());
        System.out.println(orbit.getY() + orbit.getHeight());
    }
    
    /**
     * Calculates and returns the instantaneous angular velocity of the satellite in radians/second using ω = b/r^2 * sqrt(GM/a)
     * @return Instantaneous angular velocity
     */
    private double getSatelliteVelocity() {
        double radius = radiusMajor/radiusMajorVisual * Math.sqrt(Math.pow(getCenterX() - planet.getCenterX(), 2) + Math.pow(getCenterY() - planet.getCenterY(), 2));
        return radiusMinor / Math.pow(radius, 2) * Math.sqrt(GRAVITATIONAL_CONSTANT * planet.getMass() / radiusMajor);
    }
    
    /**
     * Changes the instantaneous radius based on the angle using r = (2 * h * b^2 + sqrt(2) * a * b * sqrt(a^2 * (1 - cos(2 * theta)) + b^2 * (1 + cos(2 * theta)) + h^2 * (cos(2 * theta) - 1))) / (2 * (a^2 * sin^2(theta) + b^2cos^2(theta))
     * (Modified from the ususal ab/sqrt(a^2sin^2(theta) + b^2cos^2(theta)) to shift the ellipse such that the radius is relative to the right focus)
     */
    private void changeVisualRadius() {
        double focusLength = -(planet.getCenterX() - Runner.frameWidth()/2.0);
        double majorSquared = Math.pow(radiusMajorVisual, 2);
        double minorSquared = Math.pow(radiusMinorVisual, 2);
        double cosTwoTheta = Math.cos(2 * angle);
        visualRadius = (2 * focusLength * minorSquared * Math.cos(angle) + Math.sqrt(2) * radiusMajorVisual * radiusMinorVisual * Math.sqrt(majorSquared * (1 - cosTwoTheta) + minorSquared * (1 + cosTwoTheta) + Math.pow(focusLength, 2) * (cosTwoTheta - 1))) / (2 * (majorSquared * Math.pow(Math.sin(angle), 2) + minorSquared * Math.pow(Math.cos(angle), 2)));
    }
    
    /**
     * Draws the satellite at the given angle and radius relative to the center of the ellipse and changes related values accordingly
     * @param g  The graphics object used for drawing
     */
    void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform trans = new AffineTransform();
        changeVisualRadius();
        g2d.setColor(Color.WHITE);
        g2d.setStroke(orbitThickness);
        g2d.draw(orbit);
        trans.translate(visualRadius * Math.cos(angle) + planet.getCenterX() - satelliteImage.getWidth()/2.0, visualRadius * -Math.sin(angle) + planet.getCenterY() - satelliteImage.getHeight()/2.0);
        g2d.drawImage(satelliteImage, trans, null);
        if(isFirstTime) {
            isFirstTime = false;
            angle += 0.01 * getSatelliteVelocity();
            lastTime = System.nanoTime();
        }
        else {
            long currentTime = System.nanoTime();
            angle += (currentTime - lastTime)/1000000000.0 * getSatelliteVelocity();
            lastTime = currentTime;
        }
        if(angle >= 2 * Math.PI)
            angle -= 2 * Math.PI;
    }
    
    /**
     * Returns the scaled semi-major axis of the ellipse in pixels
     * @return  The scaled semi-major axis
     */
    double getRadiusMajorVisual() {
        return radiusMajorVisual;
    }
    
    /**
     * Returns the scaled semi-minor axis of the ellipse in pixels
     * @return  The scaled semi-minor axis
     */
    double getRadiusMinorVisual() {
        return radiusMinorVisual;
    }
    
    /**
     * Returns the center x-value of the satellite
     * @return  The center x-value of the satellite
     */
    private double getCenterX() {
        return visualRadius * Math.cos(angle) + planet.getCenterX();
    }
    
    /**
     * Returns the center y-value of the satellite
     * @return  The center y-value of the satellite
     */
    private double getCenterY() {
        return visualRadius * -Math.sin(angle) + planet.getCenterY();
    }

}
