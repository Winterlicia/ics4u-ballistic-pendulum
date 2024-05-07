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

class AnimationPanel extends JPanel {
//Properties
    //General Properties:
    final double GRAVITY = 9.81; //Acceleration due to g
    int RESETTING_FACTOR = 0; //Used to reset the dotted line at the end of a launch
    BufferedImage imgEquation;

    //Pendulum:
    int pendulumMeter = 5; //Default pendulum rope measurement = 5m. Let every 1m = 10pixels
    
    //Bob:
    final int originX = 600, originY = 0; //x,y coordinates at the top of the pendulum rope
    final int bobDimension = 32;
    double pendulumBobX = 600, pendulumBobY = 375;
    double pendulumMass = 1.0;

    //Bullet:
    double bulletX = 500.0; 
    int bulletY = originY + bobDimension/2;
    double bulletMass = 0.1;
    double bulletVi = 0.0; //Default bullet speed = 0

    //Angle:
    double currentTheta = 0;
    double goalTheta = 0;

//Methods    
    //paintComponent shows the way the panel is drawn, with animations:
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

    private double calculateAngle() {
        return Math.acos(1 - Math.pow(this.bulletMass*this.bulletVi, 2) / (Math.pow(this.bulletMass+this.pendulumMass, 2)*2*GRAVITY*this.pendulumMeter)); 
    }

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
    AnimationPanel() {
        super();
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
