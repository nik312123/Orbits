package kepler;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class that is used for initialization and running the program
 */
class Runner extends JPanel implements ActionListener {
    /**
     * JFrame container that contains all the components that are displayed on screen
     */
    private static JFrame mainFrame;
    
    /**
     * Timer responsible for repainting the main content every X milliseconds
     */
    private static Timer repaintTimer;
    
    /**
     * Counter that forces the JFrame on top of other content while it is greater than zero
     */
    private static int alwaysOnTop = 50;
    
    /**
     * Represents the planet that the satellite orbits
     */
    private static Planet planet;
    
    /**
     * The satellite that orbits the planet
     */
    private static Satellite satellite;
    
    /**
     * Responsible for initializing everything
     * @param args  Required command-line args for main method
     */
    public static void main(String... args) {
        //Initializes and sets up the Runner object that is mainly used as a JPanel
        Runner r = new Runner();
        r.setSize(1366, 690);
        r.setVisible(true);
    
        //Initializes and sets up JFrame
        mainFrame = new JFrame();
        mainFrame.setSize(1366, 690);
    
        //Initializes satellite and planet objects
        satellite = new Satellite(20, 30);
        planet = new Planet(500000000000000.0);
    
        //Allows the satellite and planet objects access to each other
        satellite.setPlanet(planet);
        planet.setCenterCoordinates(satellite);
        
        //Sets the timer to a delay of 10 seconds that triggers the Runner object's actionPerformed method upon every 'tick'
        repaintTimer = new Timer(10, r);
        
        //Adds Runner object to JFrame
        mainFrame.add(r);
        
        //Sets up the remainder of the JFrame
        Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setLocation(dim.width / 2 - mainFrame.getWidth() / 2, 5);
        mainFrame.setLayout(null);
        mainFrame.setAlwaysOnTop(true);
        
        //Starts the repaintTimer
        repaintTimer.start();
        
        //Shows the JFrame and requests that window focus is given to it
        mainFrame.setVisible(true);
        mainFrame.requestFocus();
    }
    
    /**
     * Responsible for drawing all of the content
     * @param g  The graphics object used for drawing
     */
    @Override
    public void paintComponent(Graphics g) {
        //Responsible for determining whether the JFrame should be on top of all other windows
        if(alwaysOnTop != 0)
            --alwaysOnTop;
        else
            mainFrame.setAlwaysOnTop(false);
        
        //Draws the planet and satellite
        planet.draw(g);
        satellite.draw(g);
    }
    
    /**
     * Repaints Runner JPanel every time the repaintTimer ticks
     * @param e  The ActionEvent that can be used to refer to a variety of details regarding why the actionPerformed method was called
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
    
    /**
     * Returns the JFrame's width
     * @return  The width of the JFrame
     */
    static int frameWidth() {
        return mainFrame.getWidth();
    }
    
    /**
     * Returns the JFrame's height
     * @return  The height of the JFrame
     */
    static int frameHeight() {
        return mainFrame.getHeight();
    }
    
}
