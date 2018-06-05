package kepler;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/**
 * The class that represents the planet that the satellite orbits
 */
class Planet {
    /**
     * The height and width of the ellipse that is the planet
     */
    private static final int PLANET_HEIGHT_WIDTH = 75;
    
    /**
     * The current index of the planet pictures
     */
    private int planetFrame = 287;
    
    /**
     * Used to determine when to shift the planetFrame to the next
     */
    private int planetCounter = 0;
    
    /**
     * The mass in kg of the planet
     */
    private double mass;
    
    /**
     * The center coordinates of the planet
     */
    private double centerX, centerY;
    
    /**
     * Image that shows the planet being orbited (actually a picture of a star but whatever)
     */
    private static BufferedImage[] planetImage = new BufferedImage[288];
    
    /**
     * The ellipse representing the planet that the satellite orbits
     */
    private Ellipse2D planet;
    
    /**
     * The satellite that orbits the planet
     */
    private Satellite satellite;
    
    /**
     * Planet constructor that takes in the mass of the planet as an input and gets the planet picture
     * @param m The mass of the planet
     */
    Planet(double m) {
        if(Runner.isFirstTime()) {
            for(int i = 0; i < planetImage.length; ++i)
                planetImage[i] = Runner.getCompatibleImage("/star/star" + i + ".png");
        }
        mass = m;
    }
    
    /**
     * Draws the planet
     * @param g The graphics object used for drawing
     */
    void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        satellite.drawOrbit(g2d);
        AffineTransform trans = new AffineTransform();
        trans.translate(centerX - planetImage[planetFrame].getWidth() / 2, centerY - planetImage[planetFrame].getHeight() / 2);
        g2d.drawImage(planetImage[planetFrame], trans, null);
        
        //If the planet counter increases to a certain number, the planet frame is shifted and the planet counter is reset
        if(++planetCounter % 9 == 0) {
            //The planet frame is reset after reaching the last frame
            if(--planetFrame == -1)
                planetFrame = 287;
            planetCounter = 0;
        }
    }
    
    /**
     * Return's the mass of the planet
     * @return The mass of the planet
     */
    double getMass() {
        return mass;
    }
    
    /**
     * Sets the center coordinates of the planet to the right focus of the elliptical orbit, using the formula c = sqrt(a^2 - b^2)
     * @param s The satellite that orbits the planet
     */
    void setCenterCoordinates(Satellite s) {
        centerX = Runner.frameWidth() / 2 + Math.sqrt(Math.pow(s.getRadiusMajorVisual(), 2) - Math.pow(s.getRadiusMinorVisual(), 2));
        centerY = Runner.frameHeight() / 2.0;
        planet = new Ellipse2D.Double(centerX - Runner.frameWidth() / 2 - PLANET_HEIGHT_WIDTH / 2, -PLANET_HEIGHT_WIDTH / 2, PLANET_HEIGHT_WIDTH, PLANET_HEIGHT_WIDTH);
        satellite = s;
    }
    
    /**
     * Sets the planet ellipse's position based on the hypothetical radii in settings
     * @param radiusMajorVisual The semi-major radius in pixels
     * @param radiusMinorVisual The semi-minor radius in pixels
     */
    void setPlanetEllipse(double radiusMajorVisual, double radiusMinorVisual) {
        double centerX = Runner.frameWidth() / 2 + Math.sqrt(Math.pow(radiusMajorVisual, 2) - Math.pow(radiusMinorVisual, 2));
        planet = new Ellipse2D.Double(centerX - Runner.frameWidth() / 2 - PLANET_HEIGHT_WIDTH / 2, -PLANET_HEIGHT_WIDTH / 2, PLANET_HEIGHT_WIDTH, PLANET_HEIGHT_WIDTH);
    }
    
    /**
     * Returns the x-coordinate of the center of the planet
     * @return The x-coordinate of the center of the planet
     */
    double getCenterX() {
        return centerX;
    }
    
    /**
     * Returns the y-coordinate of the center of the planet
     * @return The y-coordinate of the center of the planet
     */
    double getCenterY() {
        return centerY;
    }
    
    /**
     * Returns planet ellipse
     * @return Planet ellipse
     */
    Ellipse2D getPlanet() {
        return planet;
    }
    
}
