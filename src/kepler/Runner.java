package kepler;

import nikunj.classes.Button;
import nikunj.classes.GradientButton;
import nikunj.classes.NumberField;
import nikunj.classes.PopUp;
import nikunj.classes.Sound;

import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.text.Highlighter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Class that is used for initialization and running the program
 */
class Runner extends JPanel implements ActionListener, KeyListener {
    /**
     * The x-position of the credit text
     */
    private static final int CREDIT_TEXT_X = 340;
    
    /**
     * The y-position of the first credit text item
     */
    private static final int CREDIT_TEXT_ORIGINAL_Y = 60;
    
    /**
     * The x-position to which the strings in settings are right aligned
     */
    private static final int SETTINGS_RIGHT_X = 495;
    
    /**
     * Counter that forces the JFrame on top of other content while it is greater than zero
     */
    private static int alwaysOnTop = 50;
    
    /**
     * Represents whether or not the music is muted
     */
    private static boolean musicMuted = false;
    
    /**
     * Represents whether or not the sound effects are muted
     */
    private static boolean sfxMuted = false;
    
    /**
     * Represents whether it is the first time getting resources
     */
    private static boolean firstTime = true;
    
    /**
     * Whether the checks in settings are ticked
     */
    private static boolean[] checkTicked = new boolean[8];
    
    /**
     * The String that represents the error message
     */
    private static String errorMessage;
    
    /**
     * Strings displayed in settings
     */
    private static final String[] SETTINGS_STRINGS = {"Orbit Radius One:", "Orbit Radius Two:", "Planet Mass:", "Show Velocity Value:", "Show Transverse Velocity Value:", "Show Radial Velocity Value:", "Show Periapsis Value:", "Show Apoapsis Value:", "Show Angular Velocity Value:", "Show Instantaneous Radius Value:", "Show Period:"};
    
    /**
     * Preformatted strings to put values into
     */
    private static final String[] SHOWN_VALUES = {"Velocity: %s m/s", "Transverse Velocity: %s m/s", "Radial Velocity: %s m/s", "Periapsis: %s m", "Apoapsis: %s m", "Angular Velocity: %s rad/s", "Instantaneous Radius: %s m", "Period: %s s"};
    
    /**
     * Contains references to the functions needed to get the values for SHOWN_VALES
     */
    private static ArrayList<Supplier<Double>> getValues;
    
    /**
     * JFrame container that contains all the components that are displayed on screen
     */
    private static JFrame mainFrame;
    
    /**
     * The JPanels clicked for to open a credits link
     */
    private static JPanel[] clickableNames = new JPanel[5];
    
    /**
     * Font used when drawing
     */
    private static Font drawingFont;
    
    /**
     * drawingFont but at a larger size for settings
     */
    private static Font drawingFontSettings;
    
    /**
     * Font for drawing calculated values
     */
    private static Font drawingFontValues;
    
    /**
     * The image representing the background
     */
    private static BufferedImage spaceBackground;
    
    /**
     * The image representing the credits text
     */
    private static BufferedImage creditsText;
    
    /**
     * The system's default highlighter
     */
    private static Highlighter defaultHighlighter;
    
    /**
     * Main background soundtrack
     */
    private static Sound main;
    
    /**
     * Sound effect for button clicking
     */
    private static Sound click;
    
    /**
     * Sound effect when error happens in settings
     */
    private static Sound errorSound;
    
    /**
     * The button that expands the settings pop-up when clicked
     */
    private static GradientButton settingsButton;
    
    /**
     * The button that expands the credits pop-up when clicked
     */
    private static GradientButton creditsButton;
    
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
     * The GradientButton used for saving the settings
     */
    private static GradientButton saveButton;
    
    /**
     * Each represent one of the check boxes for showing values
     */
    private static Button[] checkBoxes = new Button[8];
    
    /**
     * The pop-up that is used for displaying the credits
     */
    private static PopUp credits;
    
    /**
     * The pop-up that is used for displaying the settings
     */
    private static PopUp settings;
    
    /**
     * The pop-up that shows up if the inputted radii would result in a crash
     */
    private static PopUp error;
    
    /**
     * Represents the planet that the satellite orbits
     */
    private static Planet planet;
    
    /**
     * The satellite that orbits the planet
     */
    private static Satellite satellite;
    
    /**
     * The coefficient NumberFields used in settings
     */
    private static NumberField[] settingsInputBases = new NumberField[3];
    
    /**
     * The power of ten NumberFields used in settings
     */
    private static NumberField[] settingsInputPowers = new NumberField[3];
    
    /**
     * Responsible for initializing everything
     * @param args Required command-line args for main method
     */
    public static void main(String... args) throws IOException, UnsupportedAudioFileException {
        //Gets optimized images
        spaceBackground = getCompatibleImage("/spaceBackground.png");
        creditsText = getCompatibleImage("/credits.png");
        BufferedImage close = getCompatibleImage("/headerButtons/close.png");
        BufferedImage draggable = getCompatibleImage("/headerButtons/draggable.png");
        BufferedImage music = getCompatibleImage("/headerButtons/music.png");
        BufferedImage sfx = getCompatibleImage("/headerButtons/sfx.png");
        BufferedImage save = getCompatibleImage("/save.png");
        BufferedImage checkBox = getCompatibleImage("/checkBox.png");
        BufferedImage tickMark = getCompatibleImage("/tickMark.png");
        BufferedImage cog = getCompatibleImage("/cog.png");
        BufferedImage cogHover = getCompatibleImage("/cogHover.png");
        BufferedImage about = getCompatibleImage("/about.png");
        BufferedImage aboutHover = getCompatibleImage("/aboutHover.png");
        
        //Gets fonts
        try {
            drawingFont = Font.createFont(Font.TRUETYPE_FONT, getResource("/freeSans.ttf").openStream()).deriveFont(20f);
            drawingFontSettings = drawingFont.deriveFont(30f);
            drawingFontValues = drawingFont.deriveFont(11f);
        }
        catch(FontFormatException e) {
            e.printStackTrace();
        }
        
        //Gets audio files
        main = new Sound(getResource("/main.wav"), true);
        click = new Sound(getResource("/click.wav"), false);
        errorSound = new Sound(getResource("/error.wav"), false);
        
        //Initializes and sets up the Runner object that is mainly used as a JPanel
        Runner r = new Runner();
        r.setBounds(0, 0, 1200, 600);
        
        //Initializes and sets up JFrame
        mainFrame = new JFrame();
        mainFrame.setSize(1200, 600);
        
        //Initializes satellite and planet objects
        planet = new Planet(500000000000000.0);
        satellite = new Satellite(20, 30);
        
        // Timer responsible for repainting the main content every 2 milliseconds
        Timer repaintTimer = new Timer(2, r);
        
        setUpWindowButtons(close, draggable, music, sfx);
        
        //Initializes pop-ups
        credits = new PopUp(330, 30, mainFrame.getHeight() - 60, mainFrame.getHeight() - 60, 30, Color.BLACK, Color.ORANGE, 2);
        settings = new PopUp(15, 15, mainFrame.getWidth() - 30, mainFrame.getHeight() - 30, 30, Color.BLACK, Color.ORANGE, 2) {
            
            @Override
            public void mouseClicked(MouseEvent event) {}
        };
        
        setUpSettingsItems(save, checkBox, tickMark, cog, cogHover);
        
        setUpCreditsItems(about, aboutHover);
        
        //Sets all of the components' visibilities to true
        r.setVisible(true);
        closeButton.setVisible(true);
        draggableButton.setVisible(true);
        musicButton.setVisible(true);
        sfxButton.setVisible(true);
        credits.setVisible(true);
        settings.setVisible(true);
        error.setVisible(true);
        settingsButton.setVisible(true);
        creditsButton.setVisible(true);
        
        //Makes saveButton initially not visible
        saveButton.setVisible(false);
        
        //Adds the key listener to the necessary components
        mainFrame.addKeyListener(r);
        settings.addKeyListener(r);
        for(NumberField nf : settingsInputBases)
            nf.addKeyListener(r);
        for(NumberField nf : settingsInputPowers)
            nf.addKeyListener(r);
        saveButton.addKeyListener(r);
        for(Button b : checkBoxes)
            b.addKeyListener(r);
        
        //Adds components to JFrame
        for(NumberField nf : settingsInputBases)
            mainFrame.add(nf);
        for(NumberField nf : settingsInputPowers)
            mainFrame.add(nf);
        mainFrame.add(r);
        for(Button b : checkBoxes)
            mainFrame.add(b);
        mainFrame.add(saveButton);
        mainFrame.add(error);
        mainFrame.add(credits);
        mainFrame.add(settings);
        mainFrame.add(closeButton);
        mainFrame.add(draggableButton);
        mainFrame.add(musicButton);
        mainFrame.add(sfxButton);
        mainFrame.add(settingsButton);
        mainFrame.add(creditsButton);
        
        //Sets up the remainder of the JFrame
        Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setLocation((dim.width - mainFrame.getWidth()) / 2, (dim.height - mainFrame.getHeight()) / 2);
        mainFrame.getContentPane().setLayout(null);
        mainFrame.setUndecorated(true);
        mainFrame.setAlwaysOnTop(true);
        mainFrame.setResizable(false);
        
        //Initializes getValues to ArrayList full of methods to get values for SHOW_VALUES
        getValues = new ArrayList<>(Arrays.asList(satellite::getVelocity, satellite::getTransverseVelocity, satellite::getRadialVelocity, satellite::getPeriapsis, satellite::getApoapsis, satellite::getAngularVelocity, satellite::getRadius, satellite::getPeriod));
        
        //Plays the background soundtrack
        main.play();
        
        //Starts the repaintTimer
        repaintTimer.start();
        
        //Shows the JFrame
        mainFrame.setVisible(true);
    }
    
    /**
     * Sets up the buttons used in replacement of the windows buttons
     * @param close     The image used for the close button
     * @param draggable The image used for the draggable button
     * @param music     The image used for the music button
     * @param sfx       The image used for the sfx button
     */
    private static void setUpWindowButtons(BufferedImage close, BufferedImage draggable, BufferedImage music, BufferedImage sfx) {
        //Creates the close button that closes the application when clicked
        closeButton = new GradientButton(close, Color.BLACK, Color.RED, 35, 2, 2, 24, 24) {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                //Closes the program
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
            
            @Override
            public boolean onButton() {
                //If the mouse is on the button but not on the settings pop-up
                Point mousePos = MouseInfo.getPointerInfo().getLocation();
                Point location = mainFrame.getLocationOnScreen();
                mousePos = new Point((int) (mousePos.getX() - location.getX()), (int) (mousePos.getY() - location.getY()));
                return super.onButton() && !settings.getBounds().contains(mousePos);
            }
            
        };
        
        //Creates the draggable button that drags the entire application window when pressing down and moving the mouse on the button
        draggableButton = new GradientButton(draggable, Color.BLACK, Color.BLUE, 35, 30, 2, 24, 24) {
            /**
             * The original location of the JFrame when pressing down
             */
            private Point originalLocation;
            
            /**
             * The original location that the mouse is pressed down relative to the screen
             */
            private Point press;
            
            @Override
            public void mouseClicked(MouseEvent e) {}
            
            @Override
            public void mousePressed(MouseEvent e) {
                //Gets location of JFrame and where pressed relative to the screen
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
                /*
                 * Sets the location of the JFrame to its original location plus the shift due to the
                 * change in the drag location, ignoring the location pressed
                 */
                Point drag = e.getLocationOnScreen();
                int x = Math.round(originalLocation.x + drag.x - press.x);
                int y = Math.round(originalLocation.y + drag.y - press.y);
                mainFrame.setLocation(x, y);
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {}
            
            @Override
            public boolean onButton() {
                //If the mouse is on the button but not on the settings pop-up
                Point mousePos = MouseInfo.getPointerInfo().getLocation();
                Point location = mainFrame.getLocationOnScreen();
                mousePos = new Point((int) (mousePos.getX() - location.getX()), (int) (mousePos.getY() - location.getY()));
                return super.onButton() && !settings.getBounds().contains(mousePos);
            }
        };
        
        //Creates the music button that mutes and unmutes the background audio when clicked
        musicButton = new GradientButton(music, Color.BLACK, new Color(0, 208, 208), 35, mainFrame.getWidth() - 54, 2, 24, 24) {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                //Switches the music volume from on to off or vice versa
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
                //Draws a slash through the music button if muted
                drawCross(g, musicMuted, getX(), getWidth());
            }
            
            @Override
            public boolean onButton() {
                //If the mouse is on the button but not on the settings pop-up
                Point mousePos = MouseInfo.getPointerInfo().getLocation();
                Point location = mainFrame.getLocationOnScreen();
                mousePos = new Point((int) (mousePos.getX() - location.getX()), (int) (mousePos.getY() - location.getY()));
                return super.onButton() && !settings.getBounds().contains(mousePos);
            }
        };
        
        //Creates the sfx button that mutes and unmutes the sound effects when clicked
        sfxButton = new GradientButton(sfx, Color.BLACK, Color.GREEN, 35, mainFrame.getWidth() - 26, 2, 24, 24) {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                //Switches the sfx volume from on to off or vice versa
                if(sfxMuted) {
                    click.changeVolume(1.0);
                    errorSound.changeVolume(1.0);
                }
                else {
                    click.changeVolume(0.0);
                    errorSound.changeVolume(0.0);
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
                //Draws a slash through the sfx button if muted
                drawCross(g, sfxMuted, getX(), getWidth());
            }
            
            @Override
            public boolean onButton() {
                //If the mouse is on the button but not on the settings pop-up
                Point mousePos = MouseInfo.getPointerInfo().getLocation();
                Point location = mainFrame.getLocationOnScreen();
                mousePos = new Point((int) (mousePos.getX() - location.getX()), (int) (mousePos.getY() - location.getY()));
                return super.onButton() && !settings.getBounds().contains(mousePos);
            }
        };
    }
    
    /**
     * Initializes many things that are used in credits
     */
    private static void setUpCreditsItems(BufferedImage about, BufferedImage aboutHover) {
        //Sets up credits button
        creditsButton = new GradientButton(about, aboutHover, new Ellipse2D.Double(0, 0, 30, 31), 1168, 90, -15, -16, 30, 31) {
            @Override
            public void mouseClicked(MouseEvent e) {
            
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if(onButton() && !settings.getExpanding())
                    credits.setExpanding(true);
            }
            
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
        
        //Initializes and sets up clickable credits
        for(int i = 0, y = CREDIT_TEXT_ORIGINAL_Y - 11; i < clickableNames.length; ++i, y += 40) {
            //Initializes credit names
            clickableNames[i] = new JPanel();
            JPanel b = clickableNames[i];
            b.setVisible(false);
            b.setLocation((int) (CREDIT_TEXT_X - 1 - credits.getExpandedX()), (int) (y - credits.getExpandedY()));
            b.setOpaque(false);
            b.setName(Integer.toString(i));
            
            //Adds mouse listener such that the user's default browser opens a URL depending on which button is clicked
            b.addMouseListener(new MouseListener() {
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    //Opens URL in default browser based on which clickable JPanel is clicked
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
                        if(!url.equals("") && Desktop.isDesktopSupported())
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
                    //If the clickable JPanel is visible and the mouse is hovering over it, the cursor is switched to the hand cursor
                    if(b.isVisible()) {
                        mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        credits.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    //If the mouse exits the clickable JPanel, the cursor is switched back to the default cursor
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
    }
    
    private static void setUpSettingsItems(BufferedImage save, BufferedImage checkBox, BufferedImage tickMark, BufferedImage cog, BufferedImage cogHover) throws IOException {
        //Sets up settings button
        settingsButton = new GradientButton(cog, cogHover, new Ellipse2D.Double(0, 0, 30, 30), 1168, 50, -10, -12, 30, 30) {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(onButton() && !credits.getExpanding())
                    settings.setExpanding(true);
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
        
        //Sets up error pop-up
        error = new PopUp(450, 200, 300, 300, 15, Color.BLACK, Color.ORANGE, 2) {
            @Override
            public void onClick() {
                //Once the error pop-up is clicked, reenables NumberFields
                errorClick();
            }
        };
        
        //Initializes button used to save settings
        saveButton = new GradientButton(save, Color.BLACK, new Color(246, 138, 21), 35, 1089, (int) Math.round(settings.getExpandedY() + settings.getExpandedHeight() - 63 - 4), 88, 63) {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if(error.getWidth() == 0 && error.getHeight() == 0)
                    settingsSave();
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
            public boolean onButton() {
                return super.onButton() && error.percentageExpanded() != 1.0;
            }
            
        };
        
        //Initializes checkbox buttons
        for(int i = 0, y = 177; i < checkBoxes.length; ++i, y += 52) {
            final int buttonIndex = i;
            checkBoxes[i] = new Button(checkBox, SETTINGS_RIGHT_X + 8, y, 40, 40) {
                @Override
                public void mouseClicked(MouseEvent e) {
                    //If the checkbox button is clicked, it switches from being ticked to not being ticked or vice versa
                    checkTicked[buttonIndex] = !checkTicked[buttonIndex];
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
                    //If the checkbox is ticked, a tick mark is drawn
                    if(checkTicked[buttonIndex])
                        g.drawImage(tickMark, getX(), getY(), null);
                }
            };
            checkBoxes[i].setVisible(false);
        }
        
        //Sets up base and powerNumberFields
        for(int i = 0, y = 20; i < settingsInputBases.length; ++i, y += 53) {
            NumberField baseField = new NumberField(7, NumberField.STATE_DECIMAL, false);
            NumberField powerField = new NumberField(2, 3, NumberField.STATE_NORMAL);
            baseField.setBounds(SETTINGS_RIGHT_X + 5, y, 140, 40);
            powerField.setBounds(baseField.getX() + baseField.getWidth() + 100, y, 70, 40);
            baseField.setVisible(false);
            powerField.setVisible(false);
            baseField.setFont(drawingFontSettings);
            powerField.setFont(drawingFontSettings);
            settingsInputBases[i] = baseField;
            settingsInputPowers[i] = powerField;
        }
        
        //Sets default values for planet and satellite
        settingsInputBases[0].setText("3.0");
        settingsInputPowers[0].setText("1");
        settingsInputBases[1].setText("2.0");
        settingsInputPowers[1].setText("1");
        settingsInputBases[2].setText("5.0");
        settingsInputPowers[2].setText("14");
    }
    
    private static void drawCross(Graphics g, boolean audioMuted, int x, int width) {
        if(audioMuted) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.draw(new Line2D.Float(x + 4, 22, x + width - 4, 4));
        }
    }
    
    /**
     * Saves inputted radii and mass from settings if a collision between the planet and satellite won't happen
     */
    private static void settingsSave() {
        //Gets the base strings from the base NumberFields
        String radiusOneBase = settingsInputBases[0].getText();
        String radiusTwoBase = settingsInputBases[1].getText();
        String planetMassBase = settingsInputBases[2].getText();
        
        //Gets the power strings from the power NumberFields
        String radiusOnePower = settingsInputPowers[0].getText();
        String radiusTwoPower = settingsInputPowers[1].getText();
        String planetMassPower = settingsInputPowers[2].getText();
        
        //If any of the NumberFields are empty, an error is produced
        if(radiusOneBase.isEmpty() || radiusTwoBase.isEmpty() || planetMassBase.isEmpty() || radiusOnePower.isEmpty() || radiusTwoPower.isEmpty() || planetMassPower.isEmpty()) {
            errorStart("NumberFields cannot be left blank.");
            return;
        }
        
        //Based on NumberField bases and powers, gets corresponding double values
        double radiusOne = Double.parseDouble(radiusOneBase) * Math.pow(10, Integer.parseInt(radiusOnePower));
        double radiusTwo = Double.parseDouble(radiusTwoBase) * Math.pow(10, Integer.parseInt(radiusTwoPower));
        double planetMass = Double.parseDouble(planetMassBase) * Math.pow(10, Integer.parseInt(planetMassPower));
        
        //If the radii or planet mass is zero, an error is produces
        if(radiusOne == 0 || radiusTwo == 0 || planetMass == 0) {
            errorStart("Planet mass and orbital radii must be nonzero");
            return;
        }
        
        //If the satellite and planet would not crash
        if(!satellite.intersectsPlanet(radiusOne, radiusTwo)) {
            //Initializes planet and satellite with the above values
            planet = new Planet(planetMass);
            satellite = new Satellite(radiusOne, radiusTwo);
            
            //Replaces methods with methods from new instance of satellite
            getValues = new ArrayList<>(Arrays.asList(satellite::getVelocity, satellite::getTransverseVelocity, satellite::getRadialVelocity, satellite::getPeriapsis, satellite::getApoapsis, satellite::getAngularVelocity, satellite::getRadius, satellite::getPeriod));
            
            //Closes the settings pop-up
            settings.setExpanding(false);
        }
        else {
            //Finds which radius is larger so the error message can explain which radius needs to be increased or decreased
            boolean radiusOneBigger = radiusOne > radiusTwo;
            String bigger, smaller;
            if(radiusOneBigger) {
                bigger = "Radius One";
                smaller = "Radius Two";
            }
            else {
                bigger = "Radius Two";
                smaller = "Radius One";
            }
            errorStart(String.format("The difference between the inputted radii would cause a collision. You can either increase %s or decrease %s.", smaller, bigger));
        }
    }
    
    /**
     * Starts the error message pop-up
     * @param errorMessage The message that will be displayed in the error pop-up
     */
    private static void errorStart(String errorMessage) {
        Runner.errorMessage = errorMessage;
        error.setExpanding(true);
        errorSound.play();
        defaultHighlighter = settingsInputBases[0].getHighlighter();
        
        //Upon the error pop-up showing up, the NumberFields cannot be edited
        for(NumberField nf : settingsInputBases) {
            nf.setEditable(false);
            nf.setHighlighter(null);
            nf.setCaretColor(Color.WHITE);
        }
        for(NumberField nf : settingsInputPowers) {
            nf.setEditable(false);
            nf.setHighlighter(null);
            nf.setCaretColor(Color.WHITE);
        }
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
     * Sets all the NumberFields to their editable, default states
     */
    private static void errorClick() {
        for(NumberField nf : settingsInputBases) {
            nf.setEditable(true);
            nf.setHighlighter(defaultHighlighter);
            nf.setCaretColor(Color.BLACK);
        }
        for(NumberField nf : settingsInputPowers) {
            nf.setEditable(true);
            nf.setHighlighter(defaultHighlighter);
            nf.setCaretColor(Color.BLACK);
        }
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
        
        //Gets Graphics2D version of Graphics object for more functionality
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        //Sets graphics font to the main font
        g2d.setFont(drawingFont);
        
        //Draws the planet, satellite, buttons, background, pop-ups, etc.
        g2d.drawImage(spaceBackground, 0, 0, null);
        planet.draw(g2d);
        satellite.draw(g2d);
        closeButton.draw(g2d);
        draggableButton.draw(g2d);
        musicButton.draw(g2d);
        sfxButton.draw(g2d);
        settingsButton.draw(g2d);
        creditsButton.draw(g2d);
        
        //Gets ArrayList of indices of checked values
        ArrayList<Integer> trueIndices = new ArrayList<>();
        for(int i = 0; i < checkTicked.length; ++i) {
            if(checkTicked[i])
                trueIndices.add(i);
        }
        
        //Draws the checked settings values at the bottom
        g.setFont(drawingFontValues);
        g.setColor(Color.WHITE);
        int x = 1;
        for(int i = 0; i < trueIndices.size(); ++i) {
            int index = trueIndices.get(i);
            String valueString = String.format(SHOWN_VALUES[index], getScientific(0, getValues.get(index).get()));
            if(i != trueIndices.size() - 1)
                valueString += ", ";
            g.drawString(valueString, x, 595);
            x += g.getFontMetrics().stringWidth(valueString);
        }
        
        //Calls the pop-up draw methods as well as some methods that are used to draw content for the pop-ups
        credits.draw(g2d);
        settings.draw(g2d);
        drawSettingOptions(g2d);
        error.draw(g2d);
        drawErrorMessage(g2d);
        
        //Sets clickable JPanels to visible and draws the credit text if credits pop-up is fully expanded
        boolean creditsExpanded = credits.percentageExpanded() == 1.0;
        if(creditsExpanded) {
            g.drawImage(creditsText, (int) Math.round(credits.getExpandedX()), (int) Math.round(credits.getExpandedY()), null);
            
            //Sets to smaller font
            g.setFont(drawingFontValues);
            
            //Draws "Click to close" at bottom of credits pop-up
            String close = "Click to close";
            g.drawString(close, (mainFrame.getWidth() - g.getFontMetrics().stringWidth(close)) / 2, 560);
            
            //Sets font back to normal
            g.setFont(drawingFont);
        }
        //Sets the clickable JPanels' visibility based on whether or not the credits are expanded
        for(JPanel b : clickableNames)
            b.setVisible(creditsExpanded);
    }
    
    /**
     * Draws settings options if settings pop-up is fully expanded
     * @param g The graphics object used for drawing
     */
    private static void drawSettingOptions(Graphics g) {
        if(settings.percentageExpanded() == 1.0) {
            
            //Sets font to main font with greater size
            g.setFont(drawingFontSettings);
            g.setColor(Color.WHITE);
            
            //Draws each settings option
            for(int i = 0, y = 50; i < SETTINGS_STRINGS.length; ++i, y += 52)
                drawRightAlignedString(g, SETTINGS_STRINGS[i], y);
            
            //Sets NumberFields visible
            for(int i = 0; i < settingsInputBases.length; ++i) {
                NumberField nf = settingsInputBases[i];
                nf.setVisible(true);
                g.drawString("x 10 ^", nf.getX() + nf.getWidth() + 9, nf.getY() + 30);
                if(i != 2)
                    g.drawString("m", nf.getX() + nf.getWidth() + 180, nf.getY() + 30);
                else
                    g.drawString("kg", nf.getX() + nf.getWidth() + 180, nf.getY() + 30);
            }
            for(NumberField nf : settingsInputPowers)
                nf.setVisible(true);
            for(Button b : checkBoxes)
                b.setVisible(true);
            
            //Makes save button visible and draws it
            saveButton.setVisible(true);
            saveButton.draw(g);
            
            //Draws checkBoxes
            for(Button b : checkBoxes)
                b.draw(g);
        }
        else {
            //Hides contents of settings pop-up and requests focus back to JFrame
            mainFrame.requestFocusInWindow();
            saveButton.setVisible(false);
            for(NumberField nf : settingsInputBases)
                nf.setVisible(false);
            for(NumberField nf : settingsInputPowers)
                nf.setVisible(false);
            for(Button b : checkBoxes)
                b.setVisible(false);
        }
        
        //Sets font back to default drawing font
        g.setFont(drawingFont);
    }
    
    /**
     * Draws string right-aligned to SETTINGS_RIGHT_X
     * @param g The graphics object used for drawing
     * @param s The string to be drawn
     * @param y The y-position of the string
     */
    private static void drawRightAlignedString(Graphics g, String s, int y) {
        g.drawString(s, SETTINGS_RIGHT_X - g.getFontMetrics().stringWidth(s), y);
    }
    
    /**
     * Draws error message
     * @param g The graphics object used for drawing
     */
    private static void drawErrorMessage(Graphics g) {
        if(error.percentageExpanded() == 1.0) {
            g.setColor(Color.WHITE);
            String error = "Error:";
            g.drawString(error, (mainFrame.getWidth() - g.getFontMetrics().stringWidth(error)) / 2, 195 + 35);
            String[] errorSplit = errorMessage.split("\\s+");
            StringBuilder line = new StringBuilder();
            int lineIndex = 1;
            boolean drewOnLast = true;
            for(int i = 0; i < errorSplit.length; ++i) {
                if(line.length() + errorSplit[i].length() + 1 < 32) {
                    drewOnLast = false;
                    line.append(errorSplit[i]).append(" ");
                }
                else {
                    drewOnLast = true;
                    g.drawString(line.toString(), 460, 195 + 35 + 30 * lineIndex);
                    ++lineIndex;
                    line = new StringBuilder();
                    --i;
                }
            }
            if(!drewOnLast)
                g.drawString(line.toString(), 460, 195 + 35 + 30 * lineIndex);
            String close = "Click to close";
            g.drawString(close, (mainFrame.getWidth() - g.getFontMetrics().stringWidth(close)) / 2, 490);
        }
    }
    
    /**
     * Returns String in scientific notation from Double with the coefficient rounded to the hundredths
     * @param tenMultiple Current power of ten
     * @param normal      Current Double value
     * @return String in scientific notation with the coefficient rounded to the hundredths
     */
    private static String getScientific(int tenMultiple, double normal) {
        if(Double.isInfinite(normal))
            return "Very large";
        else if(Double.isNaN(normal) || normal == 0)
            return "0";
        else if(normal >= 10)
            return getScientific(tenMultiple + 1, normal / 10.0);
        else if(normal < 1)
            return getScientific(tenMultiple - 1, normal * 10);
        else {
            BigDecimal bigNormal = new BigDecimal(normal);
            bigNormal = bigNormal.setScale(2, BigDecimal.ROUND_HALF_UP);
            if(bigNormal.doubleValue() >= 10) {
                bigNormal = bigNormal.divide(BigDecimal.TEN, BigDecimal.ROUND_HALF_UP);
                ++tenMultiple;
            }
            else if(bigNormal.doubleValue() < 1) {
                bigNormal = bigNormal.multiply(BigDecimal.TEN);
                --tenMultiple;
            }
            return String.format("%.2f e %d", bigNormal.doubleValue(), tenMultiple);
        }
    }
    
    /**
     * Sets the firstTime variable to false
     */
    static void setFirstTimeFalse() {
        firstTime = false;
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
     * Returns whether or not it is the first time that the paintComponent method has been used
     * @return Whether or not paintComponent method has been called for first time
     */
    static boolean isFirstTime() {
        return firstTime;
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
    
    /**
     * Repaints Runner JPanel every time the repaintTimer ticks
     * @param e The ActionEvent that can be used to refer to a variety of details regarding why the actionPerformed method was called
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        mainFrame.repaint();
    }
    
    /**
     * See {@code KeyListener}
     * @param e See {@code KeyListener}
     */
    @Override
    public void keyTyped(KeyEvent e) {}
    
    /**
     * Based on the keys pressed, an action is triggered
     * @param e The mouse KeyEvent
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            if(settings.percentageExpanded() == 1.0) {
                if(error.getHeight() == 0 && error.getWidth() == 0)
                    settingsSave();
                else if(error.percentageExpanded() == 1.0) {
                    error.setExpanding(false);
                    errorClick();
                }
            }
            else if(credits.percentageExpanded() == 1.0)
                credits.setExpanding(false);
        }
    }
    
    /**
     * See {@code KeyListener}
     * @param e See {@code KeyListener}
     */
    @Override
    public void keyReleased(KeyEvent e) {}
    
}
