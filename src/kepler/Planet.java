package kepler;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * The class that represents the planet that the satellite orbits
 */
class Planet {
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
    private static BufferedImage planetImage;
    
    /**
     * Planet constructor that takes in the mass of the planet as an input and gets the planet picture
     * @param m  The mass of the planet
     */
    Planet(double m) {
        try {
            planetImage = ImageIO.read(Runner.class.getResource("/star.png"));
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        mass = m;
    }
    
    /**
     * Draws the planet
     * @param g  The graphics object used for drawing
     */
    void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform trans = new AffineTransform();
        trans.translate(centerX - planetImage.getWidth()/2, centerY - planetImage.getHeight()/2);
        g2d.drawImage(planetImage, trans, null);
    }
    
    /**
     * Return's the mass of the planet
     * @return  The mass of the planet
     */
    double getMass() {
        return mass;
    }
    
    /**
     * Sets the center coordinates of the planet to the right focus of the elliptical orbit
     * @param s  The satellite that orbits the planet
     */
    void setCenterCoordinates(Satellite s) {
        centerX = Runner.frameWidth()/2 + Math.sqrt(Math.pow(s.getRadiusMajorVisual(), 2) - Math.pow(s.getRadiusMinorVisual(), 2));
        centerY = Runner.frameHeight()/2.0;
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
    
}
