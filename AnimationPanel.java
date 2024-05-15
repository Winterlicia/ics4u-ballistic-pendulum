import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Draws the simulation of the ballistic pendulum on the screen
 */
public class AnimationPanel extends JPanel {
    //Properties
    //General Properties:
    /**
     * A constant value for the acceleration due to gravity, which is 9.81 m/s^2
     */
    final double GRAVITY = 9.81;

    /** 
     * A constant value for the resetting factor, which is 0
     * Used to reset the dotted line at the end of a launch
     */
    int RESETTING_FACTOR = 0;

    /** 
     * The image of the equation that will be displayed on the screen
     */
    BufferedImage imgEquation;

    //Pendulum:
    /**
     * The length of the pendulum rope in meters
     * Default value is 5 meters
     * Each meter is equivalent to 10 pixels
     */
    int pendulumMeter = 5;
    
    //Bob:
    /**
     * The x coordinate of the top of the pendulum rope
     */
    final int originX = 600;
    
    /**
     * The y coordinate of the top of the pendulum rope
     */
    final int originY = 0;

    /**
     * The dimensions of the bob that will be displayed on the screen
     */
    final int bobDimension = 32;

    /**
     * The x coordinate of the bob
     */
    double pendulumBobX = 600;

    /**
     * The y coordinate of the bob
     */
    double pendulumBobY = 375;

    /**
     * The mass of the pendulum bob in kilograms
     * Default value is 1.0 kg
     */
    double pendulumMass = 1.0;

    //Bullet:
    /**
     * The x coordinate of the bullet
     */
    double bulletX = 500.0; 

    /**
     * The y coordinate of the bullet
     */
    int bulletY = originY + bobDimension/2;

    /**
     * The mass of the bullet in kilograms
     * Default value is 0.1 kg
     */
    double bulletMass = 0.1;

    /**
     * The initial velocity of the bullet in m/s
     * Default value is 0.0 m/s
     */
    double bulletVi = 0.0;

    //Angle:
    /**
     * The current angle of the pendulum in radians
     */
    double currentTheta = 0;

    /**
     * The goal angle of the pendulum in radians
     */
    double goalTheta = 0;

    //Methods    
    /**
     * Draws the components of the panel, with animations
     * @param g The graphics object used to draw the components
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setStroke(new BasicStroke(7.0f));
        g2d.drawLine(350, 0, 350, 540); //Line that separates the inputs and the actual pendulum

        //Write the formula on the screen by uploading a .png image:
        g.drawImage(imgEquation, 10, 420, null);

        //Break out of void method if length exceeds integer value
        if (pendulumMeter > 100000) {
            return;
        }

        // (1) Animate bullet being shot, change bulletX, bulletY variables overtime:
        g.setColor(Color.BLUE);
        g.fillRect((int)(10*pendulumMeter * Math.sin(currentTheta) + bulletX), (int)(10*pendulumMeter * Math.cos(currentTheta) + bulletY), 20, 10); 

        // (2) Animate ball swing, following the exponential equation 1.3^(x-30) and using collision velocity
        g.setColor(Color.GRAY);
        //Calculate change in bob position, using Bx = Lsina, By = Lcosa
        this.goalTheta = calculateAngle();
        
        this.pendulumBobX = 10*pendulumMeter * Math.sin(currentTheta) + originX;
        this.pendulumBobY = 10*pendulumMeter * Math.cos(currentTheta) + originY;

        g2d.setStroke(new BasicStroke(3.0f));
        g2d.drawLine(originX + bobDimension/2, originY, (int)(pendulumBobX + bobDimension/2), (int) pendulumBobY); //Account for bob length/width
        
        g.setColor(Color.BLACK);
        g.fillOval((int) pendulumBobX, (int) pendulumBobY, bobDimension, bobDimension);

        //Once the launch has finished, draw the final/result position of the bob:
        if (this.currentTheta == this.goalTheta && BallisticPendulum.pendulumLaunchFinished) {
            finalPosition(g2d);
        }
        
    }

    /**
     * Calculates the angle of the pendulum using the ballistic pendulum equation
     * @return The angle of the pendulum in radians
     */
    private double calculateAngle() {
        return Math.acos(1 - Math.pow(this.bulletMass*this.bulletVi, 2) / (Math.pow(this.bulletMass+this.pendulumMass, 2)*2*GRAVITY*this.pendulumMeter)); 
    }

    /**
     * Draws the final position of the pendulum bob after the launch
     * @param g2d
     */
    private void finalPosition(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        // Set dashed/dotted stroke
        float[] dashPattern = {5, 5}; // 5 pixels on, 5 pixels off
        g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f));

        //Calculate an extended pendulum length:
        final int EXTENSION_LENGTH = 150;
        int extendedX = (int) (EXTENSION_LENGTH * Math.sin(currentTheta) + pendulumBobX);
        int extendedY = (int) (EXTENSION_LENGTH * Math.cos(currentTheta) + pendulumBobY);
        g2d.drawLine((originX + bobDimension/2) * RESETTING_FACTOR, originY * RESETTING_FACTOR, (originX + bobDimension/2) * RESETTING_FACTOR, (originY + 375) * RESETTING_FACTOR); //Original y-axis line
        g2d.drawLine((originX + bobDimension/2) * RESETTING_FACTOR, originY * RESETTING_FACTOR, (extendedX + bobDimension/2) * RESETTING_FACTOR, extendedY * RESETTING_FACTOR); //Final pendulum line position

        g2d.setStroke(new BasicStroke(3.0f)); //Revert back to normal settings
    }

    //Constructor 
    /**
     * Constructs a new AnimationPanel object
     */
    AnimationPanel() {
        super();

        setLayout(null);
        
        //Try to read the image from both the jar file and local drive
        InputStream equationClass = this.getClass().getResourceAsStream("Equation.png");
        
        if (equationClass != null) {
            try {
                imgEquation = ImageIO.read(equationClass);
            } catch (IOException e) {
                System.out.println("Unable to read/load image from jar");
                e.printStackTrace();
            }
        } else { //If it can't be found on the jar, search it locally
            try {
                imgEquation = ImageIO.read(new File("Equation.png"));
            } catch (IOException e) {
                System.out.println("Unable to read/load image");
                e.printStackTrace();
            }
        }

        
    } 
}
