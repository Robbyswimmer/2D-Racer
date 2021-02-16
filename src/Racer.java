import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.management.RuntimeMXBean;
import java.security.Key;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.sound.*;

public class Racer {

    private static Boolean endgame;
    private static BufferedImage background;
    private static BufferedImage player;
    private static BufferedImage player2;
    private static BufferedImage coverBackground;

    private static Boolean upPressed;
    private static Boolean downPressed;
    private static Boolean leftPressed;
    private static Boolean rightPressed;

    private static Boolean wPressed;
    private static Boolean sPressed;
    private static Boolean aPressed;
    private static Boolean dPressed;

    private static ImageObject p1;
    private static ImageObject p2;

    private static double p1Width;
    private static double p1Height;
    private static double p1OriginalX;
    private static double p1OriginalY;
    private static double p1Velocity;

    private static double p2Width;
    private static double p2Height;
    private static double p2OriginalX;
    private static double p2OriginalY;
    private static double p2Velocity;

    //max speed for the cars
    private static int maxSpeed;

    private static int xOffset;
    private static int yOffset;
    private static int winWidth;
    private static int winHeight;

    private static double pi;
    private static double twoPi;

    //used in the drawClock method to determine how many seconds
    //have elapsed since the beginning of the game
    private static long start = System.currentTimeMillis();

    private static JFrame appFrame;
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;

    private static int maxLaps;
    private static int currentLap;

    //drop down menus for selecting # laps and car to play with
    private static JComboBox lapList;
    private static JComboBox carList;
    private static JComboBox carList2;

    //these will be used to keep track of the current lap of each player,
    //whichever player is ahead will be used to increment the current lap counter
    private static int p1CurrentLap;
    private static int p2CurrentLap;

    //holding variables for the car images
    private static BufferedImage Red;
    private static BufferedImage Blue;
    private static BufferedImage Orange;
    private static BufferedImage Green;
    private static BufferedImage White;

    //array of barriers for the cars
    private static BufferedImage[] barriers;

    //variables for playing music
    private static AudioInputStream ais;
    private static Clip clip;

    //plays the sounds of explosions when cars collide
    private static AudioInputStream ais2;
    private static Clip clip2;
    private static Image image;

    // the remaining variables at the end of the asteroids chapter have been
    // omitted because they have been deemed unnecessary at this point in development
    // this includes things like flames, explosions, asteroids, enemies, and player bullets


    public Racer() {
        setup();
    }

    /**
     * Initializes important variables that need to be reset at the beginning of each game,
     * imports the game images, establishes the app frame, names the frame, catches any necessary
     * exceptions caused by initialization of images
     */
    public static void setup() {
        appFrame = new JFrame("2D Racer");
        xOffset = 0;
        yOffset = 0;
        winWidth = 500;
        winHeight = 500;
        pi = Math.PI;
        twoPi = 2 * pi;
        endgame = false;

        maxSpeed = 3;
        maxLaps = 3;
        currentLap = 1;

        p1Height = 50;
        p1Width = 50;

        //initial coords for the players
        p1OriginalX = 1080; //(double) xOffset + ((double) winWidth / 2.0) - (p1Width / 2.0) + 400;
        p1OriginalY = 525; //(double) yOffset + ((double) winHeight / 2.0) - (p1Height / 2.0) + 50;

        p2OriginalX = 1030; //(double) xOffset + ((double) winWidth / 2.0) - (p1Width / 2.0) + 400;
        p2OriginalY = 525; //(double) yOffset + ((double) winHeight / 2.0) - (p1Height / 2.0) + 100;

        System.out.println("P1 x: " + p1OriginalX + ", P1 y: " + p1OriginalY);
        System.out.println("P2 x: " + p2OriginalX + ", P2 y: " + p2OriginalY);

        try {

            //default images for the game
            background = ImageIO.read(new File("Images/Track3.png"));
            player = ImageIO.read(new File("Images/BlueCarLarge2.png"));
            player2 = ImageIO.read(new File("Images/RedTurn.png"));
            coverBackground = ImageIO.read(new File("Images/GraphicsCover.png"));

            //initializes all of the colors so that players can change the colors of their cars
            Blue = ImageIO.read(new File("Images/BlueCarLarge2.png"));
            Red = ImageIO.read(new File("Images/RedTurn.png"));
            Orange = ImageIO.read(new File("Images/OrangeTurn.png"));
            Green = ImageIO.read(new File("Images/GreenTurn.png"));
            White = ImageIO.read(new File("Images/WhiteTurn.png"));

            //use this to create an array of barriers to act as collision for the cars
//            for (int i = 0; i < 8; i++) {
//                if (i < 4)
//                    barriers[i] = ImageIO.read(new File("Images/wall-long.png"));
//                else
//                    barriers[i] = ImageIO.read(new File("Images/wall-short.png"));
//            }

        } catch (IOException ioe) {
            System.out.println("Find the right image you dingus");
        }

    }

    /**
     * Main method for initializing the game, adding buttons, adding drop down menus,
     * binding keyboard keys, creating the main app frame
     */
    public static void main(String[] args) {
        setup();
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setSize(1320, 937);

        JPanel myPanel = new JPanel();
        Cover();

        //start game button
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new StartGame());
        myPanel.add(newGameButton);

        //select laps drop down menu
        Integer[] laps = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        JLabel lapText = new JLabel("Select Laps");
        lapList = new JComboBox(laps);
        myPanel.add(lapText);
        myPanel.add(lapList);
        lapList.addActionListener(new LapListener());

        //select car player 1 drop down
        String[] cars = new String[]{"Red", "Blue", "Orange", "Green", "White"};
        JLabel carText1 = new JLabel("P1 Car");
        carList = new JComboBox(cars);
        myPanel.add(carText1);
        myPanel.add(carList);
        carList.addActionListener(new CarListener1());

        //select car player 2 drop down
        JLabel carText2 = new JLabel("P2 Car");
        carList2 = new JComboBox(cars);
        myPanel.add(carText2);
        myPanel.add(carList2);
        carList2.addActionListener(new CarListener2());

        //quit game button
        JButton quitButton = new JButton("Quit Game");
        quitButton.addActionListener(new QuitGame());
        myPanel.add(quitButton);

        //controls for player 1
        bindKey(myPanel, "UP");
        bindKey(myPanel, "DOWN");
        bindKey(myPanel, "LEFT");
        bindKey(myPanel, "RIGHT");

        //controls for player 2
        bindKey(myPanel, "W");
        bindKey(myPanel, "S");
        bindKey(myPanel, "A");
        bindKey(myPanel, "D");
        bindKey(myPanel, "F");

        appFrame.getContentPane().add(myPanel, "South");
        appFrame.setVisible(true);
    }

    /**
     * This is the class for loading the cover page
     * aka start-up screen
     * Uses JPanel as a container and the ImageIcon to hols the image
     */
    public static void Cover() {
        image = new ImageIcon("Images/GraphicsCover.png").getImage();

        JPanel container = new MyBackground();
        appFrame.add(container);
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setSize(1320, 937);
        appFrame.setVisible(true);
    }

    public static class MyBackground extends JPanel {

        public MyBackground() {
            setBackground(new Color(0, true));
        }

        @Override
        public void paintComponent(Graphics g) {
            //Paint background first
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);

            //Paint the rest of the component. Children and self etc.
            super.paintComponent(g);
        }
    }

    /**
     * Responsible for drawing the images – dynamic and static –
     * that can be seen in the game
     */
    private static class Animate implements Runnable {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2d = (Graphics2D) g;

        public void run() {
            while (!endgame) {
                drawBackground();
                drawPlayer();
                drawClock();
                drawSpeed();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Exception caught in Animate");
                }
            }
        }
    }

    /**
     * detects if a player changes the number of laps from the dropdown
     */
    private static class LapListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            maxLaps = lapList.getSelectedIndex() + 1;
        }
    }

    /**
     * detects if player 1 changes their car
     */
    private static class CarListener1 implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int n = carList.getSelectedIndex();
            if (n == 0)
                player = Red;
            else if (n == 1)
                player = Blue;
            else if (n == 2)
                player = Orange;
            else if (n == 3)
                player = Green;
            else
                player = White;
        }
    }

    /**
     * detects if player 2 changes their car
     */
    private static class CarListener2 implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int n = carList2.getSelectedIndex();
            if (n == 0)
                player2 = Red;
            else if (n == 1)
                player2 = Blue;
            else if (n == 2)
                player2 = Orange;
            else if (n == 3)
                player2 = Green;
            else
                player2 = White;
        }
    }

    private static class StartGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            // endgame = true;
            start = System.currentTimeMillis();
            upPressed = false;
            downPressed = false;
            leftPressed = false;
            rightPressed = false;

            wPressed = false;
            sPressed = false;
            aPressed = false;
            dPressed = false;

            //instantiate the ImageObjects for the player cars
            p1 = new ImageObject(p1OriginalX, p1OriginalY, p1Width, p1Height, 0.0);
            p2 = new ImageObject(p2OriginalX, p2OriginalY, p1Width, p1Height, 0.0);

            p1Velocity = 0.0;
            p2Velocity = 0.0;

            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {
                System.out.println("Caught the exception in start game");
            }

            try {
                //play music
                ais = AudioSystem.getAudioInputStream(new File("Sounds/8-bit1.wav"));
                clip = AudioSystem.getClip();
                clip.open(ais);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();

                ais2 = AudioSystem.getAudioInputStream(new File("Sounds/chiptune-explosion-.wav"));
                clip2 = AudioSystem.getClip();
                clip2.open(ais2);

            } catch (UnsupportedAudioFileException uafe) {
            } catch (IOException ioe) {
            } catch (LineUnavailableException lue) {
            }

            endgame = false;
            Thread t1 = new Thread(new Animate());
            Thread t2 = new Thread(new PlayerMover());
            Thread t3 = new Thread(new CollisionChecker());
            t1.start();
            t2.start();
            t3.start();
        }
    }

    /**
     * Quits the game and ends all current threads
     */
    private static class QuitGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            endgame = true;
            clip.stop();
        }
    }

    private static class PlayerMover implements Runnable {

        private double velocityStep;
        private double rotateStep;

        //level of acceleration and rotation speed
        public PlayerMover() {
            velocityStep = 0.01;
            rotateStep = 0.03;
        }

        //checks if the cars are still on the track or not
        public static boolean inBounds(ImageObject playerCheck) {
            return playerCheck.getX() < 1123 && playerCheck.getX() > 207 && playerCheck.getY() > 216 && playerCheck.getY() < 837;
        }

        // Checks to see if the players are in the inner grass area
        public static boolean inBoundsInner(ImageObject playerCheck) {
            return playerCheck.getX() < 970 && playerCheck.getX() > 375 && playerCheck.getY() > 375 && playerCheck.getY() < 670;
        }

        // Checks to see if the players hit blue tent
        public static boolean hitBlueTent(ImageObject playerCheck) {
            return playerCheck.getX() > 445 && playerCheck.getX() < 815 && playerCheck.getY() > 28 && playerCheck.getY() < 175;
        }

        public static boolean hitBrownHouse(ImageObject playerCheck) {
            return playerCheck.getX() < 205 && playerCheck.getX() > -9 && playerCheck.getY() > 190 && playerCheck.getY() < 420;
        }

        public static boolean hitRedHouse(ImageObject playerCheck) {
            return playerCheck.getX() < 175 && playerCheck.getX() > -9 && playerCheck.getY() > 550 && playerCheck.getY() < 800;
        }

          public static boolean hitEndOfMap(ImageObject playerCheck) {
            return playerCheck.getX() >= 1310 || playerCheck.getX() <= 10 || playerCheck.getY() >= 920 || playerCheck.getY() <= 15;
        }

        public void run() {

            while (!endgame) {

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    System.out.println("Exception caught for PlayerMover");
                }

                //handle acceleration for player 1 and 2
                if (upPressed && p1Velocity < maxSpeed && inBounds(p1)) {
                    p1Velocity += velocityStep * 4;
                } else if (wPressed && p2Velocity < maxSpeed && inBounds(p2)) {
                    p2Velocity += velocityStep * 4;
                }

                // This slows down players outside of track.
                //FIXME: is this correct math? lol
                if (upPressed && !inBounds(p1)) {
                    p1Velocity = (double) maxSpeed * 0.8;
                } else if (wPressed && !inBounds(p2)) {
                    p2Velocity = (double) maxSpeed * 0.8;
                }

                // This slows down players inside of the inner grass area of the track.
                // Made this very drastic in case of cheating.
                if (upPressed && inBoundsInner(p1)) {
                    p1Velocity = (double) maxSpeed * 0.08;
                } else if (wPressed && inBoundsInner(p2)) {
                    p2Velocity = (double) maxSpeed * 0.08;
                }

                // Bouncing affect when player hits a building to avoid having player stuck on wall.
                if (hitBlueTent(p1) || hitBrownHouse(p1) || hitRedHouse(p1) || hitEndOfMap(p1)) {
                    p1Velocity = 0;
                    p1Velocity -= velocityStep * 100;
                } else if (hitBlueTent(p2) || hitBrownHouse(p2) || hitRedHouse(p2) || hitEndOfMap(p2)) {
                    p2Velocity = 0;
                    p2Velocity -= velocityStep * 100;
                }

                //handle air braking – slowing naturally because no acceleration
                if (!upPressed && !downPressed && p1Velocity > 0) {
                    p1Velocity -= velocityStep * 3;
                } else if (!wPressed && !sPressed && p2Velocity > 0) {
                    p2Velocity -= velocityStep * 3;
                }

                // Speeds the players back up after being stopped.
                if (upPressed && downPressed && p1Velocity < 0) {
                    p1Velocity += velocityStep * 3;
                } else if (wPressed && sPressed && p2Velocity < 0) {
                    p2Velocity += velocityStep * 3;
                }

                //handle braking for player 1 and 2
                if (downPressed && p1Velocity * -1 < maxSpeed / 2 && inBounds(p1))
                    p1Velocity -= velocityStep * 5;
                else if (sPressed && p2Velocity * -1 < maxSpeed / 2 && inBounds(p2)) {
                    p2Velocity -= velocityStep * 5;
                }

                //handle left rotation for player 1 and 2
                if (leftPressed) {
                    if (p1Velocity < 0)
                        p1.rotate(-rotateStep);
                    else
                        p1.rotate(rotateStep);
                }

                if (aPressed) {
                    if (p1Velocity < 0)
                        p2.rotate(-rotateStep);
                    else
                        p2.rotate(rotateStep);
                }

                //handle right rotation for player 1 and 2
                if (rightPressed) {
                    if (p1Velocity < 0)
                        p1.rotate(rotateStep);
                    else
                        p1.rotate(-rotateStep);
                }

                if (dPressed) {
                    if (p1Velocity < 0)
                        p2.rotate(rotateStep);
                    else
                        p2.rotate(-rotateStep);
                }

                p1.move(-p1Velocity * Math.cos(p1.getAngle() - pi / 2.0), p1Velocity * Math.sin(p1.getAngle() - pi / 2.0));
                p2.move(-p2Velocity * Math.cos(p2.getAngle() - pi / 2.0), p2Velocity * Math.sin(p2.getAngle() - pi / 2.0));
                //p1.screenWrap(xOffset, xOffset + winWidth, yOffset, yOffset + winHeight);
            }

        }
    }

    /**
     * Determines whether or not one of the bound keys has been
     * pressed and sets the correct variable to TRUE
     */
    private static class KeyPressed extends AbstractAction {

        private String action;

        public KeyPressed() {
            action = "";
        }

        public KeyPressed(String input) {
            action = input;
        }

        public void actionPerformed(ActionEvent e) {
//            System.out.println("Key pressed");

            if (action.equals("UP")) upPressed = true;
            if (action.equals("DOWN")) downPressed = true;
            if (action.equals("LEFT")) leftPressed = true;
            if (action.equals("RIGHT")) rightPressed = true;

            if (action.equals("W")) wPressed = true;
            if (action.equals("S")) sPressed = true;
            if (action.equals("A")) aPressed = true;
            if (action.equals("D")) dPressed = true;
        }
    }


    /**
     * Determines whether or not one of the bound keys has been
     * pressed and sets the correct variable to FALSE
     */
    private static class KeyReleased extends AbstractAction {

        private String action;

        public KeyReleased() {
            action = "";
        }

        public KeyReleased(String input) {
            action = input;
        }

        public void actionPerformed(ActionEvent e) {
//            System.out.println("Key released");

            if (action.equals("UP")) upPressed = false;
            if (action.equals("DOWN")) downPressed = false;
            if (action.equals("LEFT")) leftPressed = false;
            if (action.equals("RIGHT")) rightPressed = false;

            if (action.equals("W")) wPressed = false;
            if (action.equals("S")) sPressed = false;
            if (action.equals("A")) aPressed = false;
            if (action.equals("D")) dPressed = false;
        }
    }

    /**
     * Method responsible for binding the keyboard keys to the game
     *
     * @param myPanel imports the game panel
     * @param input   represents the given keyboard input as a string
     */
    private static void bindKey(JPanel myPanel, String input) {
        System.out.println("Key bound");

        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("pressed " + input), input + " pressed");
        myPanel.getActionMap().put(input + " pressed", new KeyPressed(input));

        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released " + input), input + " released");
        myPanel.getActionMap().put(input + " released", new KeyReleased(input));
    }

    /**
     * The main method responsible for drawing the stop clock during the game
     */
    private static void drawClock() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        DecimalFormat df = new DecimalFormat("##.##");

        //gets the current time and compares it against when execution began
        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000f;
        Stroke oldStroke = g2d.getStroke();

        //sets the brush, color, and draws the strings / shapes for the clock
        g2d.setColor(Color.WHITE);
        g2d.fillRect(80, 75, 230, 100);
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke(5));
        g2d.drawRect(80, 75, 232, 102);
        g2d.setStroke(oldStroke);
        g2d.setFont(new Font("TimesRoman", Font.BOLD, 30));
        g2d.drawString(df.format(sec) + " seconds", 100, 115);
        g2d.drawString("Laps: " + currentLap + "/" + maxLaps, 100, 155);
    }

    /**
     * Display the current speed for each player along with
     * lap information
     */
    private static void drawSpeed() {

        Graphics g = appFrame.getGraphics();
        Graphics2D g2d = (Graphics2D) g;

        //draw the rectangle to contain the speed text
        g2d.setColor(Color.white);
        g2d.fillRect(860, 75, 300, 100);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(5));
        g2d.drawRect(860, 75, 300, 100);

        //draw the text to display current speed
        DecimalFormat df = new DecimalFormat("###");
        g2d.setFont(new Font("TimesRoman", Font.BOLD, 30));
        g2d.drawString("P1 Speed: " + df.format(p1Velocity * 20) + " MPH", 880, 115);
        g2d.drawString("P2 Speed: " + df.format(p2Velocity * 20) + " MPH", 880, 155);
    }

    /**
     * Draws the player sprites
     */
    private static void drawPlayer() {

        //import graphics
        Graphics g = appFrame.getGraphics();
        Graphics2D g2d = (Graphics2D) g;

        //draw the players
        g2d.drawImage(rotateImageObject(p1).filter(player, null), (int) (p1.getX() + 0.5), (int) (p1.getY() + 0.5), null);
        g2d.drawImage(rotateImageObject(p2).filter(player2, null), (int) (p2.getX() + 0.5), (int) (p2.getY() + 0.5), null);
    }

    //draw barriers for the cars if necessary
    //FIXME decide if this should be used or not
    private static void drawBarriers() {

        //import graphics
        Graphics g = appFrame.getGraphics();
        Graphics2D g2d = (Graphics2D) g;

    }

    /**
     * Responsible for rotating the object attached to the player image
     * so that it can track movement accurately
     *
     * @param obj the given image that is being rotated
     * @return returns the transformation applied to the image object
     */
    private static AffineTransformOp rotateImageObject(ImageObject obj) {
        AffineTransform at = AffineTransform.getRotateInstance(-obj.getAngle(), obj.getWidth() / 2.0, obj.getyHeight() / 2.0);
        return new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    }

    /**
     * Draws the main background of the game which is the race track with
     * building and other details
     */
    private static void drawBackground() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(background, xOffset, yOffset, null);
    }

    /**
     * Class for collision checking thread
     */
    private static class CollisionChecker implements Runnable {
        public void run() {
            while (!endgame) {

                if (collisionOccurs(p1, p2)) {
                    System.out.println("CRASH between p1 and p2!!!!");
                    clip2.setFramePosition(0);
                    clip2.start();

                    p1Velocity = 0;
                    p1Velocity -= .01 * 100;
                    p2Velocity = 0;
                    p2Velocity -= .01 * 100;
//                    p1Velocity -= .01;
//                    p2Velocity -= .01;
                }
            }
        }
    }

    /**
     * Used to determine if two objects are overlapping
     */
    private static Boolean isInside(double p1x, double p1y, double p2x1, double p2y1, double p2x2, double p2y2) {
        Boolean ret = false;
        if (p1x >= p2x1 && p1x <= p2x2) {
            if (p1y > p2y1 && p1y < p2y2) {
                ret = true;
            }
            if (p1y >= p2y2 && p1y <= p2y1) {
                ret = true;
            }
        }
        if (p1x >= p2x2 && p1x <= p2x1) {
            if (p1y > p2y1 && p1y < p2y2) {
                ret = true;
            }
            if (p1y >= p2y2 && p1y <= p2y1) {
                ret = true;
            }
        }
        return ret;
    }

    /**
     * @return returns the coordinates at which a collision is occurring in the game
     */
    private static Boolean collisionOccursCoordinates(double p1x1, double p1y1, double p1x2, double p1y2, double p2x1,
                                                      double p2y1, double p2x2, double p2y2) {
        Boolean ret = false;
        if (isInside(p1x1, p1y1, p2x1, p2y1, p2x2, p2y2)) {
            ret = true;
        }
        if (isInside(p1x1, p1y2, p2x1, p2y1, p2x2, p2y2)) {
            ret = true;
        }
        if (isInside(p1x2, p1y1, p2x1, p2y1, p2x2, p2y2)) {
            ret = true;
        }
        if (isInside(p1x2, p1y2, p2x1, p2y1, p2x2, p2y2)) {
            ret = true;
        }
        if (isInside(p2x1, p2y1, p1x1, p1y1, p1x2, p1y2)) {
            ret = true;
        }
        if (isInside(p2x1, p2y2, p1x1, p1y1, p1x2, p1y2)) {
            ret = true;
        }
        if (isInside(p2x2, p2y1, p1x1, p1y1, p1x2, p1y2)) {
            ret = true;
        }
        if (isInside(p2x2, p2y2, p1x1, p1y1, p1x2, p1y2)) {
            ret = true;
        }
        return ret;
    }

    /**
     * @return returns whether or not a collision has occurred in the game
     */
    private static boolean collisionOccurs(ImageObject obj1, ImageObject obj2) {

        //printing coords to check for overlapping objects
        System.out.println("P1 x: " + obj1.x + ", P1 y: " + obj1.y);
        System.out.println("P2 x: " + obj2.x + ", P2 y: " + obj2.y);
        System.out.println();
        System.out.println();

        //limit the amount of input being printed out
        try {
            Thread.sleep(800);
        } catch (InterruptedException ie) {
            System.out.println("ie");
        }

        return collisionOccursCoordinates(obj1.getX(), obj1.getY(), obj1.getX() + obj1.getWidth(),
                obj1.getY() + obj1.getyHeight(), obj2.getX(), obj2.getY(), obj2.getX() + obj2.getWidth(),
                obj2.getY() + obj2.getyHeight());
    }


//    private static Boolean collisionOccurs(ImageObject obj1, ImageObject obj2) {
//        Boolean ret = false;
//        if (collisionOccursCoordinates(obj1.getX(), obj1.getY(), obj1.getX() + obj1.getWidth(),
//                obj1.getY() + obj1.getyHeight(), obj1.getX(), obj2.getY(), obj2.getX() + obj2.getWidth(),
//                obj2.getY() + obj2.getyHeight())) {
//            ret = true;
//        }
//        return ret;
//    }

    /**
     * This class defines the ImageObject which is essentially the object that is attached
     * to the image that is responsible primarily for movement and collision detection / handling
     */
    private static class ImageObject {

        private double x;
        private double y;
        private double xWidth;
        private double yHeight;
        private double angle;
        private double internalAngle;
        private ArrayList<Double> coords;
        private ArrayList<Double> triangles;
        private double comX;
        private double comY;

        public ImageObject() {
        }

        public ImageObject(double xInput, double yInput, double xWidthInput, double yHeightInput, double angleInput) {
            x = xInput;
            y = yInput;
            xWidth = xWidthInput;
            yHeight = yHeightInput;
            angle = angleInput;
            internalAngle = 0.0;
            coords = new ArrayList<Double>();
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getWidth() {
            return xWidth;
        }

        public double getyHeight() {
            return yHeight;
        }

        public double getAngle() {
            return angle;
        }

        public double getInternalAngle() {
            return internalAngle;
        }

        public void setAngle(double angleInput) {
            angle = angleInput;
        }

        public void setInternalAngle(double interalAngleInput) {
            internalAngle = interalAngleInput;
        }

        public ArrayList<Double> getCoords() {
            return coords;
        }

        public void setCoords(ArrayList<Double> coordsInput) {
            coords = coordsInput;
            generateTriangles();
            //printTriangles();
        }

        public void generateTriangles() {
            triangles = new ArrayList<Double>();
            comX = getComX();
            comY = getComY();

            for (int i = 0; i < coords.size(); i = i + 2) {
                triangles.add(coords.get(i));
                triangles.add(coords.get(i + 1));

                triangles.add(coords.get((i + 2) % coords.size()));
                triangles.add(coords.get((i + 3) % coords.size()));

                triangles.add(comX);
                triangles.add(comY);
            }
        }

        public void printTriangles() {
            for (int i = 0; i < triangles.size(); i = i + 6) {
                System.out.print("p0x: " + triangles.get(i) + ", p0y: " + triangles.get(i + 1));
                System.out.print("p1x: " + triangles.get(i + 2) + ", p1y: " + triangles.get(i + 3));
                System.out.print("p2x: " + triangles.get(i + 4) + ", p2y: " + triangles.get(i + 5));

            }
        }

        public double getComX() {
            double ret = 0;

            if (coords.size() > 0) {
                for (int i = 0; i < coords.size(); i = i + 2) {
                    ret += coords.get(i);
                }
                ret /= (coords.size() / 2.0);
            }
            return ret;
        }

        public double getComY() {
            double ret = 0;

            if (coords.size() > 0) {
                for (int i = 1; i < coords.size(); i = i + 2) {
                    ret += coords.get(i);
                }
                ret /= (coords.size() / 2.0);
            }
            return ret;
        }

        public void move(double xinput, double yinput) {
            x += xinput;
            y += yinput;
        }

        public void moveTo(double xinput, double yinput) {
            x = xinput;
            y = yinput;
        }

        public void screenWrap(double leftEdge, double rightEdge, double topEdge, double bottomEdge) {
            if (x > rightEdge) {
                moveTo(leftEdge, getY());
            }
            if (x < leftEdge) {
                moveTo(rightEdge, getY());
            }
            if (y > bottomEdge) {
                moveTo(getX(), topEdge);
            }
            if (y < topEdge) {
                moveTo(getX(), bottomEdge);
            }
        }

        public void rotate(double angleInput) {
            angle += angleInput;
            while (angle > twoPi) {
                angle -= twoPi;
            }
            while (angle < 0) {
                angle += twoPi;
            }
        }

        public void spin(double internalAngleInput) {
            internalAngle += internalAngleInput;
            while (internalAngle > twoPi) {
                internalAngle -= twoPi;
            }
            while (internalAngle < 0) {
                internalAngle += twoPi;
            }
        }
    }
}
