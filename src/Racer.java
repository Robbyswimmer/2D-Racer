import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.management.RuntimeMXBean;
import java.security.Key;
import java.util.ArrayList;

public class Racer {

    private static Boolean endgame;
    private static BufferedImage background;
    private static BufferedImage player;
    private static BufferedImage coverBackground;

    private static Boolean upPressed;
    private static Boolean downPressed;
    private static Boolean leftPressed;
    private static Boolean rightPressed;

    private static ImageObject p1;

    private static double p1Width;
    private static double p1Height;
    private static double p1OriginalX;
    private static double p1OriginalY;
    private static double p1Velocity;

    private static int xOffset;
    private static int yOffset;
    private static int winWidth;
    private static int winHeight;

    private static double pi;
    private static double twoPi;

    private static JFrame appFrame;
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;

    // the remaining variables at the end of the asteroids chapter have been
    // omitted because they have been deemed unnecessary at this point in development
    // this includes things like flames, explosions, asteroids, enemies, and player bullets


    public Racer() {
        setup();
    }

    public static void setup() {
        appFrame = new JFrame("2D Racer");
        xOffset = 0;
        yOffset = 0;
        winWidth = 500;
        winHeight = 500;
        pi = Math.PI;
        twoPi = 2 * pi;
        endgame = false;

        p1Height = 50;
        p1Width = 50;
        p1OriginalX = (double) xOffset + ((double) winWidth / 2.0) - (p1Width / 2.0);
        p1OriginalY = (double) yOffset + ((double) winHeight / 2.0) - (p1Height / 2.0);

        try {
            background = ImageIO.read(new File("Images/testTrack2.png"));
            player = ImageIO.read(new File("Images/BlueCarLarge2.png"));
            coverBackground = ImageIO.read(new File("Images/GraphicsCover.png"));

        } catch (IOException ioe) {
            System.out.println("Find the right image you dingus");
        }

    }

    public static void main(String[] args) {
        setup();
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setSize(1320, 937);

        JPanel myPanel = new JPanel();

        ImageIcon icon = new ImageIcon("Images/GraphicsCover.png");
        JLabel thumb = new JLabel();
        thumb.setIcon(icon);
        //drawCoverBackground();
        //start game button
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new StartGame());
        myPanel.add(newGameButton);

        //quit game button
        JButton quitButton = new JButton("Quit Game");
        quitButton.addActionListener(new QuitGame());
        myPanel.add(quitButton);

        bindKey(myPanel, "UP");
        bindKey(myPanel, "DOWN");
        bindKey(myPanel, "LEFT");
        bindKey(myPanel, "RIGHT");
        bindKey(myPanel, "F");

        appFrame.getContentPane().add(myPanel, "South");
        appFrame.setVisible(true);

    }

    private static class Animate implements Runnable {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2d = (Graphics2D) g;

        public void run() {
            while (!endgame) {
                drawBackground();
                drawPlayer();
                try {
                    Thread.sleep(32);
                } catch (InterruptedException e) {
                    System.out.println("Exception caught in Animate");
                }

            }
        }
    }

    private static class StartGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            // endgame = true;
            upPressed = false;
            downPressed = false;
            leftPressed = false;
            rightPressed = false;

            p1 = new ImageObject(p1OriginalX, p1OriginalY, p1Width, p1Height, 0.0);
            p1Velocity = 0.0;

            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {
                System.out.println("Caught the exception in start game");
            }

            endgame = false;
            Thread t1 = new Thread(new Animate());
            Thread t2 = new Thread(new PlayerMover());
            t1.start();
            t2.start();
        }
    }

    private static class QuitGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            endgame = true;
        }
    }

    private static class PlayerMover implements Runnable {

        private double velocityStep;
        private double rotateStep;

        public PlayerMover() {
            velocityStep = 0.01;
            rotateStep = 0.01;
        }

        public void run() {

            while (!endgame) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    System.out.println("Exception caught for PlayerMover");
                }

                if (upPressed) {
                    p1Velocity += velocityStep;
                }

                if (downPressed) p1Velocity -= velocityStep;

                if (leftPressed) {
                    if (p1Velocity < 0)
                        p1.rotate(-rotateStep);
                    else
                        p1.rotate(rotateStep);
                }

                if (rightPressed) {
                    if (p1Velocity < 0)
                        p1.rotate(rotateStep);
                    else
                        p1.rotate(-rotateStep);
                }

                p1.move(-p1Velocity * Math.cos(p1.getAngle() - pi / 2.0), p1Velocity * Math.sin(p1.getAngle() - pi / 2.0));
                //p1.screenWrap(xOffset, xOffset + winWidth, yOffset, yOffset + winHeight);
            }

        }
    }

    private static class KeyPressed extends AbstractAction {

        private String action;

        public KeyPressed() {
            action = "";
        }

        public KeyPressed(String input) {
            action = input;
        }

        public void actionPerformed(ActionEvent e) {
            System.out.println("Key pressed");

            if (action.equals("UP")) upPressed = true;
            if (action.equals("DOWN")) downPressed = true;
            if (action.equals("LEFT")) leftPressed = true;
            if (action.equals("RIGHT")) rightPressed = true;
        }
    }

    private static class KeyReleased extends AbstractAction {

        private String action;

        public KeyReleased() {
            action = "";
        }

        public KeyReleased(String input) {
            action = input;
        }

        public void actionPerformed(ActionEvent e) {
            System.out.println("Key released");

            if (action.equals("UP")) upPressed = false;
            if (action.equals("DOWN")) downPressed = false;
            if (action.equals("LEFT")) leftPressed = false;
            if (action.equals("RIGHT")) rightPressed = false;
        }
    }

    private static void bindKey(JPanel myPanel, String input) {
        System.out.println("Key bound");

        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("pressed " + input), input + " pressed");
        myPanel.getActionMap().put(input + " pressed", new KeyPressed(input));

        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released " + input), input + " released");
        myPanel.getActionMap().put(input + " released", new KeyReleased(input));
    }

    private static void drawPlayer() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        //g2d.rotate(90);
        g2d.drawImage(rotateImageObject(p1).filter(player, null), (int) (p1.getX() + 0.5), (int) (p1.getY() + 0.5), null);
    }


    private static AffineTransformOp rotateImageObject(ImageObject obj) {
        AffineTransform at = AffineTransform.getRotateInstance(-obj.getAngle(), obj.getWidth() / 2.0, obj.getyHeight() / 2.0);
        return new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    }

    private static void drawCoverBackground() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(coverBackground, xOffset, yOffset, null);
    }

    private static void drawBackground() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(background, xOffset, yOffset, null);
    }

    private static Boolean isInside(double p1x, double p1y, double p2x1, double p2y1, double p2x2, double p2y2) {
        Boolean ret = false;
        if (p1x > p2x1 && p1x < p2x2) {
            if (p1y > p2y1 && p1y < p2y2) {
                ret = true;
            }
            if (p1y > p2y2 && p1y < p2y1) {
                ret = true;
            }
        }
        if (p1x > p2x2 && p1x < p2x1) {
            if (p1y > p2y1 && p1y < p2y2) {
                ret = true;
            }
            if (p1y > p2y2 && p1y < p2y1) {
                ret = true;
            }
        }
        return ret;
    }

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

    //FIXME is it supposed to be getyHeight() or getHeight()
    private static Boolean collisonOccurs(ImageObject obj1, ImageObject obj2) {
        Boolean ret = false;
        if (collisionOccursCoordinates(obj1.getX(), obj1.getY(), obj1.getX() + obj1.getWidth(),
                obj1.getY() + obj1.getyHeight(), obj1.getX(), obj2.getY(), obj2.getX() + obj2.getWidth(),
                obj2.getY() + obj2.getyHeight())) {
            ret = true;
        }
        return ret;
    }

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

        //FIXME this was converted from vector to arraylist, so if errors exist check the conversion
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
