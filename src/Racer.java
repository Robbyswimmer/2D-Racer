import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class Racer {

    private static Boolean endgame;
    private static BufferedImage background;
    private static BufferedImage player;

    private static Boolean upPressed;
    private static Boolean downPressed;
    private static Boolean leftPressed;
    private static Boolean rightPressed;

    //FIXME this is supposed to be of type 'ImageObject' but I don't know what that is
    private static BufferedImage p1;

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

        try {
            background = ImageIO.read(new File("Images/testTrack.png"));
            player = ImageIO.read(new File("Images/BlueCarLarge2.png"));

        } catch (IOException ioe) {
            System.out.println("Find the right image you dingus");
        }
    }

    public static void main(String[] args) {

        setup();
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setSize(1320, 937);

        JPanel myPanel = new JPanel();

        //start game button
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new StartGame());
        myPanel.add(newGameButton);

        //quit game button
        JButton quitButton = new JButton("Quit Game");
        quitButton.addActionListener(new QuitGame());
        myPanel.add(quitButton);

        //FIXME implement bindKey method
//        bindKey(myPanel, "UP");
//        bindKey(myPanel, "DOWN");
//        bindKey(myPanel, "LEFT");
//        bindKey(myPanel, "RIGHT");
//        bindKey(myPanel, "F");

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

                // I added this line because if you don't have it,
                // the program will continually draw backgrounds and
                // crash your computer :(

                // you should probably leave it until other components are built :)

                endgame = true;
            }
        }
    }

    private static class StartGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {
                System.out.println("Caught the exception for sleeping!");
            }

            Thread t1 = new Thread(new Animate());
            t1.start();
        }
    }

    private static class QuitGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            endgame = true;
        }
    }

    private static void drawPlayer() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(player, xOffset + 600, yOffset + 300, null);
    }

    private static void drawBackground() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(background, xOffset, yOffset, null);
    }

}
