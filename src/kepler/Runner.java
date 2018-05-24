package kepler;

import nikunj.classes.GradientButton;
import nikunj.classes.PopUp;
import nikunj.classes.Sound;

import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Class that is used for initialization and running the program
 */
class Runner extends JPanel implements ActionListener {
    /**
     * Counter that forces the JFrame on top of other content while it is greater than zero
     */
    private static int alwaysOnTop = 50;
    
    /**
     * The x-position of the credit text
     */
    private final static int CREDIT_TEXT_X = 340;
    
    /**
     * The y-position of the first credit text item
     */
    private final static int CREDIT_TEXT_ORIGINAL_Y = 60;
    
    /**
     * Represents whether or not the music is muted
     */
    private static boolean musicMuted = false;
    
    /**
     * Represents whether or not the sound effects are muted
     */
    private static boolean sfxMuted = false;
    
    /**
     * Timer responsible for repainting the main content every X milliseconds
     */
    private static Timer repaintTimer;
    
    /**
     * JFrame container that contains all the components that are displayed on screen
     */
    private static JFrame mainFrame;
    
    /**
     * The JPanels clicked for to open a credits link
     */
    private static JPanel[] clickableNames = new JPanel[5];
    
    /**
     * Main background soundtrack
     */
    private static Sound main;
    
    /**
     * Sound effect for button clicking
     */
    private static Sound click;
    
    /**
     * The image representing the background
     */
    private static BufferedImage spaceBackground;
    
    /**
     * The image representing the credits text
     */
    private static BufferedImage creditsText;
    
    /**
     * Represents the close window button in an application
     */
    private static GradientButton closeButton;
    
    /**
     * Allows dragging if mouse is pressed down on this button
     */
    private static GradientButton draggableButton;
    
    /**
     * Allows the muting and unmuting of background music
     */
    private static GradientButton musicButton;
    
    /**
     * Allows the muting and unmuting of sound effects
     */
    private static GradientButton sfxButton;
    
    /**
     * The pop up that is used for displaying the credits
     */
    private static PopUp credits;
    
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
     * @param args Required command-line args for main method
     */
    public static void main(String... args) throws IOException, UnsupportedAudioFileException {
        //Gets optimized images
        spaceBackground = getCompatibleImage("/spaceBackground.png");
        BufferedImage close = getCompatibleImage("/headerButtons/close.png");
        BufferedImage draggable = getCompatibleImage("/headerButtons/draggable.png");
        BufferedImage music = getCompatibleImage("/headerButtons/music.png");
        BufferedImage sfx = getCompatibleImage("/headerButtons/sfx.png");
        creditsText = getCompatibleImage("/credits.png");
        
        //Gets audio files
        main = new Sound(getResource("/main.wav"), true);
        click = new Sound(getResource("/click.wav"), false);
        
        //Initializes and sets up the Runner object that is mainly used as a JPanel
        Runner r = new Runner();
        r.setBounds(0, 0, 1200, 600);
        
        //Initializes and sets up JFrame
        mainFrame = new JFrame();
        mainFrame.setSize(1200, 600);
        
        //Initializes satellite and planet objects
        planet = new Planet(500000000000000.0);
        satellite = new Satellite(20, 30);
        
        //Sets the timer to a delay of 10 seconds that triggers the Runner object's actionPerformed method upon every 'tick'
        repaintTimer = new Timer(2, r);
        
        //Createst the close button that closes the application when clicked
        closeButton = new GradientButton(close, Color.BLACK, Color.RED, 35, 2, 2, 24, 24) {
            private static final long serialVersionUID = 1L;
            
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
            
            @Override
            public void mousePressed(MouseEvent e) {}
            
            @Override
            public void mouseReleased(MouseEvent e) {}
            
            @Override
            public void mouseEntered(MouseEvent e) {}
            
            @Override
            public void mouseExited(MouseEvent e) {}
            
            @Override
            public void mouseDragged(MouseEvent e) {}
            
            @Override
            public void mouseMoved(MouseEvent e) {}
            
        };
        
        //Creates the draggable button that drags the entire application window when pressing down and moving the mouse on the button
        draggableButton = new GradientButton(draggable, Color.BLACK, Color.BLUE, 35, 30, 2, 24, 24) {
            private static final long serialVersionUID = 1L;
            
            private Point originalLocation;
            private Point press;
            
            @Override
            public void mouseClicked(MouseEvent e) {}
            
            @Override
            public void mousePressed(MouseEvent e) {
                originalLocation = mainFrame.getLocation();
                press = e.getLocationOnScreen();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {}
            
            @Override
            public void mouseEntered(MouseEvent e) {}
            
            @Override
            public void mouseExited(MouseEvent e) {}
            
            @Override
            public void mouseDragged(MouseEvent e) {
                Point drag = e.getLocationOnScreen();
                int x = Math.round(originalLocation.x + drag.x - press.x);
                int y = Math.round(originalLocation.y + drag.y - press.y);
                mainFrame.setLocation(x, y);
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {}
        };
        
        //Creates the music button that mutes and unmutes the background audio when clicked
        musicButton = new GradientButton(music, Color.BLACK, new Color(0, 208, 208), 35, mainFrame.getWidth() - 54, 2, 24, 24) {
            private static final long serialVersionUID = 1L;
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if(musicMuted)
                    main.changeVolume(1.0);
                else
                    main.changeVolume(0);
                click.play();
                musicMuted = !musicMuted;
            }
            
            @Override
            public void mousePressed(MouseEvent e) {}
            
            @Override
            public void mouseReleased(MouseEvent e) {}
            
            @Override
            public void mouseEntered(MouseEvent e) {}
            
            @Override
            public void mouseExited(MouseEvent e) {}
            
            @Override
            public void mouseDragged(MouseEvent e) {}
            
            @Override
            public void mouseMoved(MouseEvent e) {}
            
            @Override
            public void afterDraw(Graphics g) {
                if(musicMuted) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.draw(new Line2D.Float(getX() + 4, 22, getX() + getWidth() - 4, 4));
                }
            }
        };
        
        //Creates the sfx button that mutes and unmutes the sound effects when clicked
        sfxButton = new GradientButton(sfx, Color.BLACK, Color.GREEN, 35, mainFrame.getWidth() - 26, 2, 24, 24) {
            private static final long serialVersionUID = 1L;
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if(sfxMuted) {
                    click.changeVolume(1.0);
                }
                else {
                    click.changeVolume(0.0);
                }
                click.play();
                sfxMuted = !sfxMuted;
            }
            
            @Override
            public void mousePressed(MouseEvent e) {}
            
            @Override
            public void mouseReleased(MouseEvent e) {}
            
            @Override
            public void mouseEntered(MouseEvent e) {}
            
            @Override
            public void mouseExited(MouseEvent e) {}
            
            @Override
            public void mouseDragged(MouseEvent e) {}
            
            @Override
            public void mouseMoved(MouseEvent e) {}
            
            @Override
            public void afterDraw(Graphics g) {
                if(sfxMuted) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.draw(new Line2D.Float(getX() + 4, 22, getX() + getWidth() - 4, 4));
                }
            }
        };
        
        //Initializes credits pop-up
        credits = new PopUp(330, 30, mainFrame.getHeight() - 60, mainFrame.getHeight() - 60, 30, Color.BLACK, Color.ORANGE, 2);
        
        //Initializes and sets up clickable credits
        for(int i = 0, y = CREDIT_TEXT_ORIGINAL_Y - 9; i < clickableNames.length; ++i, y += 40) {
            //Initializes credit names
            clickableNames[i] = new JPanel();
            JPanel b = clickableNames[i];
            b.setVisible(false);
            b.setLocation((int) (CREDIT_TEXT_X + 1 - credits.getExpandedX()), (int) (y - credits.getExpandedY()));
            b.setOpaque(false);
            b.setName(Integer.toString(i));
            
            //Adds mouse listener such that the user's default browser opens a URL depending on which button is clicked
            b.addMouseListener(new MouseListener() {
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    String url = "";
                    String name = ((JPanel) e.getSource()).getName();
                    int nameInt = Integer.parseInt(name);
                    switch(nameInt) {
                        case 0:
                            url = "https://natentine.com/royalty-free-music/zypheers-canyon";
                            break;
                        case 1:
                            url = "http://www.freesfx.co.uk/sfx/button";
                            break;
                        case 2:
                            url = "https://freesound.org/people/Autistic%20Lucario/sounds/142608/";
                            break;
                        case 3:
                            url = "mailto:aaron4game@gmail.com";
                            break;
                        case 4:
                            url = "mailto:nikchawla312@gmail.com";
                            break;
                    }
                    try {
                        if(!url.equals(""))
                            Desktop.getDesktop().browse(new URI(url));
                    }
                    catch(IOException | URISyntaxException e1) {
                        e1.printStackTrace();
                    }
                }
                
                @Override
                public void mousePressed(MouseEvent e) {}
                
                @Override
                public void mouseReleased(MouseEvent e) {}
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    if(b.isVisible()) {
                        mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        credits.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    credits.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    b.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
                
            });
        }
        
        //Sets size of clickable names
        clickableNames[0].setSize(86, 9);
        clickableNames[1].setSize(52, 9);
        clickableNames[2].setSize(89, 9);
        clickableNames[3].setSize(102, 9);
        clickableNames[4].setSize(85, 9);
        
        //Adds clickable names to credits pop-up and sets the credits pop-up's layout to null for absolute positioning
        for(JPanel b : clickableNames)
            credits.add(b);
        credits.setLayout(null);
        
        //Sets all of the components' visibilities to true
        r.setVisible(true);
        closeButton.setVisible(true);
        draggableButton.setVisible(true);
        musicButton.setVisible(true);
        sfxButton.setVisible(true);
        credits.setVisible(true);
        
        //Adds components to JFrame
        mainFrame.add(r);
        mainFrame.add(closeButton);
        mainFrame.add(draggableButton);
        mainFrame.add(musicButton);
        mainFrame.add(sfxButton);
        mainFrame.add(credits);
        
        //Sets up the remainder of the JFrame
        Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setLocation((dim.width - mainFrame.getWidth()) / 2, (dim.height - mainFrame.getHeight()) / 2);
        mainFrame.getContentPane().setLayout(null);
        mainFrame.setUndecorated(true);
        mainFrame.setAlwaysOnTop(true);
        mainFrame.setResizable(false);
        
        //Shows the JFrame and requests that window focus is given to it
        mainFrame.setVisible(true);
        mainFrame.requestFocus();
        
        //Starts the repaintTimer
        repaintTimer.start();
        
        //Plays the background soundtrack
        main.play();
        credits.setExpanding(true);
    }
    
    /**
     * Gets image optimized for processing
     * @param resource Absolute path relative to project directory of image
     * @return Optimized BufferedImage
     */
    static BufferedImage getCompatibleImage(String resource) {
        //Gets unoptimized BufferedImage
        BufferedImage current = null;
        try {
            current = ImageIO.read(getResource(resource));
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        
        //If BufferedImage doesn't exist, return null
        if(current == null)
            return null;
        
        //Gets local graphics configuration
        GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        
        //If the image is already optimized, it will be returned
        if(current.getColorModel().equals(gfxConfig.getColorModel()))
            return current;
        
        /*
         * Otherwise, it will create a BufferedImage container with the local graphics configuration's
         * standards and draw the unoptimized BufferedImage with the optimized configuration
         */
        BufferedImage optimized = gfxConfig.createCompatibleImage(current.getWidth(), current.getHeight(), current.getTransparency());
        Graphics2D g2d = optimized.createGraphics();
        g2d.drawImage(current, 0, 0, null);
        optimized.setAccelerationPriority(1);
        return optimized;
    }
    
    /**
     * Responsible for drawing all of the content
     * @param g The graphics object used for drawing
     */
    @Override
    public void paintComponent(Graphics g) {
        //Responsible for determining whether the JFrame should be on top of all other windows
        if(alwaysOnTop != 0)
            --alwaysOnTop;
        else
            mainFrame.setAlwaysOnTop(false);
        
        //Draws the planet, satellite, buttons, background, etc.
        g.drawImage(spaceBackground, 0, 0, null);
        planet.draw(g);
        satellite.draw(g);
        closeButton.draw(g);
        draggableButton.draw(g);
        musicButton.draw(g);
        sfxButton.draw(g);
        credits.draw(g);
        
        //Sets clickable JPanels to visible and draws the credit text if credits pop-up is fully expanded
        boolean creditsExpanded = credits.percentageExpanded() == 1.0;
        if(creditsExpanded)
            g.drawImage(creditsText, (int) Math.round(credits.getExpandedX()), (int) Math.round(credits.getExpandedY()), null);
        for(JPanel b : clickableNames)
            b.setVisible(creditsExpanded);
    }
    
    /**
     * Repaints Runner JPanel every time the repaintTimer ticks
     * @param e The ActionEvent that can be used to refer to a variety of details regarding why the actionPerformed method was called
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
    
    /**
     * Returns the JFrame's width
     * @return The width of the JFrame
     */
    static int frameWidth() {
        return mainFrame.getWidth();
    }
    
    /**
     * Returns the JFrame's height
     * @return The height of the JFrame
     */
    static int frameHeight() {
        return mainFrame.getHeight();
    }
    
    /**
     * Returns planet object
     * @return Planet object
     */
    static Planet getPlanet() {
        return planet;
    }
    
    /**
     * Returns a URL to whichever resource is given
     * @param resource Resource absolute path relative to project directory
     * @return URL to requested resource
     */
    private static URL getResource(String resource) {
        return Runner.class.getResource(resource);
    }
    
}
