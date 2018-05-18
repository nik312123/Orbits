package kepler;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

/**
 * The class that represents the satellite that orbits the planet
 */
class Satellite {
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
     * An ellipse that represents the satellite's shape
     */
    private static final Ellipse2D SATELLITE_SHAPE = new Ellipse2D.Double(0, 0, 30, 30);
    
    /**
     * Represents the planet that the satellite orbits
     */
    private Planet planet;
    
    /**
     * Satellite constructor that sets the actual and visual axes
     * @param radiusOne  One of the actual radii of the elliptical orbits in meters
     * @param radiusTwo  One of the actual radii of the elliptical orbits in meters
     */
    Satellite(double radiusOne, double radiusTwo) {
        //Sets the major axis to the larger of the two given radii
        radiusMajor = Math.max(radiusOne, radiusTwo);
        radiusMinor = radiusOne + radiusTwo - radiusMajor;
        
        //Sets visual major and minor axes based on which way the full elliptical orbit fits
        if(radiusMinor / radiusMajor * (Runner.frameWidth()/2 - SATELLITE_SHAPE.getWidth() - 20) <= Runner.frameHeight()/2 - SATELLITE_SHAPE.getHeight() - 20) {
            radiusMajorVisual = Runner.frameWidth()/2 - SATELLITE_SHAPE.getWidth() - 20;
            radiusMinorVisual = radiusMinor / radiusMajor * radiusMajorVisual;
        }
        else {
            radiusMinorVisual = Runner.frameHeight()/2 - SATELLITE_SHAPE.getHeight() - 20;
            radiusMajorVisual = radiusMajor / radiusMinor * radiusMinorVisual;
        }
        visualRadius = radiusMajorVisual;
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
     * Changes the instantaneous radius based on the angle using r = ab/sqrt(a^2sin^2(theta) + b^2cos^2(theta))
     */
    private void changeVisualRadius() {
        visualRadius = radiusMajorVisual * radiusMinorVisual / Math.sqrt(Math.pow(radiusMajorVisual * Math.sin(angle), 2) + Math.pow(radiusMinorVisual * Math.cos(angle), 2));
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
        trans.translate(visualRadius * Math.cos(angle) + (Runner.frameWidth() - SATELLITE_SHAPE.getWidth())/2.0, visualRadius * -Math.sin(angle) + (Runner.frameHeight() - SATELLITE_SHAPE.getHeight())/2.0);
        g2d.setColor(Color.BLUE);
        g2d.fill(trans.createTransformedShape(SATELLITE_SHAPE));
        angle += 0.01 * getSatelliteVelocity();
        if(angle >= 2 * Math.PI)
            angle -= 2 * Math.PI;
    }
    
    /**
     * Sets the planet object variable to the planet object initialized in Runner
     * @param p  Represents the planet that the satellite orbits
     */
    void setPlanet(Planet p) {
        planet = p;
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
        return visualRadius * Math.cos(angle) + Runner.frameWidth()/2.0;
    }
    
    /**
     * Returns the center y-value of the satellite
     * @return  The center y-value of the satellite
     */
    private double getCenterY() {
        return visualRadius * -Math.sin(angle) + Runner.frameHeight()/2.0;
    }

}
