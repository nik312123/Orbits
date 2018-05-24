package kepler;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

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
     * The orbitAngle from 0 to 2π of the satellite's center relative to the positive x-axis with an origin at the planet's center
     */
    private double orbitAngle = 0;
    
    /**
     * The instantaneous distance between the center of the elliptical orbit and the satellite in pixels
     */
    private double visualRadius;
    
    /**
     * The instantaneous distance between the center of the elliptical orbit and the satellite in pixels
     */
    private double radius;
    
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
     * The ellipse representing the satellite
     */
    private Ellipse2D satellite;
    
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
     * @param radiusOne One of the actual radii of the elliptical orbits in meters
     * @param radiusTwo One of the actual radii of the elliptical orbits in meters
     */
    Satellite(double radiusOne, double radiusTwo) {
        satelliteImage = Runner.getCompatibleImage("/planet.png");
        
        //Sets the major axis to the larger of the two given radii
        radiusMajor = Math.max(radiusOne, radiusTwo);
        radiusMinor = radiusOne + radiusTwo - radiusMajor;
        
        //Sets visual major and minor axes based on which way the full elliptical orbit fits
        if(radiusMinor / radiusMajor * (Runner.frameWidth() / 2 - SATELLITE_HEIGHT_WIDTH - 35) <= Runner.frameHeight() / 2 - SATELLITE_HEIGHT_WIDTH - 35) {
            radiusMajorVisual = Runner.frameWidth() / 2 - SATELLITE_HEIGHT_WIDTH - 35;
            radiusMinorVisual = radiusMinor / radiusMajor * radiusMajorVisual;
        }
        else {
            radiusMinorVisual = Runner.frameHeight() / 2 - SATELLITE_HEIGHT_WIDTH - 35;
            radiusMajorVisual = radiusMajor / radiusMinor * radiusMinorVisual;
        }
        
        //Gets planet and allows it to set its center coordinates based on the radii of the orbit
        planet = Runner.getPlanet();
        planet.setCenterCoordinates(this);
        
        //Initializes radius and sets up orbit ellipse
        visualRadius = getVisualRadius(orbitAngle);
        radius = visualRadius * radiusMajor / radiusMajorVisual;
        orbit = new Ellipse2D.Double((Runner.frameWidth() - 2 * radiusMajorVisual) / 2.0, (Runner.frameHeight() - 2 * radiusMinorVisual) / 2.0, 2 * radiusMajorVisual, 2 * radiusMinorVisual);
        satellite = new Ellipse2D.Double(radiusMajorVisual - SATELLITE_HEIGHT_WIDTH / 2, -SATELLITE_HEIGHT_WIDTH / 2, SATELLITE_HEIGHT_WIDTH, SATELLITE_HEIGHT_WIDTH);
    }
    
    /**
     * Calculates and returns the instantaneous angular velocity of the satellite in radians/second using ω = b/r^2 * sqrt(GM/a)
     * @return Instantaneous angular velocity
     */
    private double getAngularVelocity() {
        return radiusMinor / Math.pow(radius, 2) * Math.sqrt(GRAVITATIONAL_CONSTANT * planet.getMass() / radiusMajor);
    }
    
    /**
     * Changes the instantaneous radius based on the orbitAngle using r = (2 * h * b^2 + sqrt(2) * a * b * sqrt(a^2 * (1 - cos(2 * theta)) + b^2 * (1 + cos(2 * theta)) + h^2 * (cos(2 * theta) - 1))) / (2 * (a^2 * sin^2(theta) + b^2cos^2(theta))
     * (Modified from the usual ab/sqrt(a^2sin^2(theta) + b^2cos^2(theta)) to shift the ellipse such that the radius is relative to the right focus)
     */
    private double getVisualRadius(double theta) {
        double focusLength = -(planet.getCenterX() - Runner.frameWidth() / 2.0);
        double majorSquared = Math.pow(radiusMajorVisual, 2);
        double minorSquared = Math.pow(radiusMinorVisual, 2);
        double cosTwoTheta = Math.cos(2 * theta);
        return (2 * focusLength * minorSquared * Math.cos(theta) + Math.sqrt(2) * radiusMajorVisual * radiusMinorVisual * Math.sqrt(majorSquared * (1 - cosTwoTheta) + minorSquared * (1 + cosTwoTheta) + Math.pow(focusLength, 2) * (cosTwoTheta - 1))) / (2 * (majorSquared * Math.pow(Math.sin(theta), 2) + minorSquared * Math.pow(Math.cos(theta), 2)));
    }
    
    /**
     * Draws the satellite at the given orbitAngle and radius relative to the center of the ellipse and changes related values accordingly
     * @param g The graphics object used for drawing
     */
    void draw(Graphics g) {
        //Sets up graphics component with antialiasing
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        //Gets the visual and actual radii
        visualRadius = getVisualRadius(orbitAngle);
        radius = visualRadius * radiusMajor / radiusMajorVisual;
        
        /*
         * Transformation made to get x and y position of satellite based on the radius and orbitAngle with the origin at the right
         * focus and used to draw the position of the satellite
         */
        AffineTransform trans = new AffineTransform();
        trans.translate(visualRadius * Math.cos(orbitAngle) + planet.getCenterX() - satelliteImage.getWidth() / 2.0, visualRadius * -Math.sin(orbitAngle) + planet.getCenterY() - satelliteImage.getHeight() / 2.0);
        g2d.drawImage(satelliteImage, trans, null);
        
        satellite.setFrame(radiusMajorVisual - SATELLITE_HEIGHT_WIDTH / 2, -SATELLITE_HEIGHT_WIDTH / 2, SATELLITE_HEIGHT_WIDTH, SATELLITE_HEIGHT_WIDTH);
        
        double v = Math.sqrt(GRAVITATIONAL_CONSTANT * planet.getMass() * (2 / radius - 1 / radiusMajor));
        double vTransverse = getAngularVelocity() * radius;
        System.out.println("V = " + v);
        System.out.println("Vtransverse: " + vTransverse);
        System.out.println("Vradial: " + Math.sqrt(Math.pow(v, 2) - Math.pow(vTransverse, 2)));
        System.out.println("Smallest r: " + getVisualRadius(0) * radiusMajor / radiusMajorVisual);
        System.out.println("Largest r: " + getVisualRadius(Math.PI) * radiusMajor / radiusMajorVisual);
        System.out.println("Angular v: " + getAngularVelocity());
        System.out.println("Radius: " + radius);
        
        /*
         * Originally multiplied by 0.002 since that is roughly the rate of the timer tick. The current time is then stored
         * into the last time, and the difference between the current time and last time is multiplied by the angular
         * velocity to get an accurate period of orbit
         */
        if(isFirstTime) {
            isFirstTime = false;
            orbitAngle += 0.002 * getAngularVelocity();
            lastTime = System.nanoTime();
        }
        else {
            long currentTime = System.nanoTime();
            orbitAngle += (currentTime - lastTime) / 1000000000.0 * getAngularVelocity();
            lastTime = currentTime;
        }
        
        //If the orbitAngle is greater than 2π, subtract 2π since there is no need to let it have a chance of overflowing
        if(orbitAngle >= 2 * Math.PI)
            orbitAngle -= 2 * Math.PI;
    }
    
    /**
     * Draws the ellipse representing the orbit
     * @param g2d The graphics object used for drawing
     */
    void drawOrbit(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setStroke(orbitThickness);
        g2d.draw(orbit);
    }
    
    /**
     * Returns the scaled semi-major axis of the ellipse in pixels
     * @return The scaled semi-major axis
     */
    double getRadiusMajorVisual() {
        return radiusMajorVisual;
    }
    
    /**
     * Returns the scaled semi-minor axis of the ellipse in pixels
     * @return The scaled semi-minor axis
     */
    double getRadiusMinorVisual() {
        return radiusMinorVisual;
    }
    
    /**
     * Finds out if the satellite and planet ellipses would intersect and the point at wwhich they are closest.
     * @return Whether or not the satellite and planet would intersect
     */
    boolean intersectsPlanet() {
        Area satelliteArea = new Area(satellite);
        satelliteArea.intersect(new Area(planet.getPlanet()));
        return !satelliteArea.isEmpty();
    }
    
}
