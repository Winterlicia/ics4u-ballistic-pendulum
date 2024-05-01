import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

class AnimationPanel extends JPanel {
//Properties
    final double GRAVITY = 9.81; //Acceleration due to g

    //Pendulum:
    int pendulumMeter = 5; //Default pendulum rope measurement = 5m. Let every 1m = 10pixels
    
    //Bob:
    final int originX = 600, originY = 0; //x,y coordinates at the top of the pendulum rope
    int bobDimension = 64;
    int pendulumBobX = 600, pendulumBobY = 375;
    double pendulumMass = 1.0;

    //Bullet:
    int bulletX = 500, bulletY = 20;
    double bulletMass = 0.1;
    int bulletVi = 0; //Default bullet speed = 0

    //Angle:
    double currentTheta = 0;
    double goalTheta = 0;

    //Deflections:

    

//Methods    
    //paintComponent shows the way the panel is drawn, with animations:
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //Break out of void method if length exceeds integer value
        if (pendulumMeter > 100000) {
            return;
        }

        // (1) Animate bullet being shot, change bulletX, bulletY variables overtime:
        g.setColor(Color.BLUE);
        g.fillRect(bulletX, 10*pendulumMeter + originY + bobDimension/2, 20, 10); 

        // (2) Animate ball swing, following the exponential equation 1.3^(x-30) and using collision velocity
        g.setColor(Color.GRAY);
        //Calculate change in bob position, using Bx = Lsina, By = Lcosa
        this.goalTheta = calculateAngle();
        System.out.println(this.goalTheta);
        
        pendulumBobX = (int) (10*pendulumMeter * Math.sin(currentTheta) + originX);
        pendulumBobY = (int) (10*pendulumMeter * Math.cos(currentTheta) + originY);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3.0f));
        g2d.drawLine(originX + bobDimension/2, originY, pendulumBobX + bobDimension/2, pendulumBobY); //Account for bob length/width
        
        g.setColor(Color.BLACK);
        g.fillOval(pendulumBobX, pendulumBobY, bobDimension, bobDimension);
        
    }

    private double calculateAngle() {
        return Math.acos(1 - Math.pow(this.bulletMass*this.bulletVi, 2) / (Math.pow(this.bulletMass+this.pendulumMass, 2)*2*GRAVITY*this.pendulumMeter)); 
        //Returns angle in radians
    }

    private void originalSetup() {

    }

    //Constructor 
    AnimationPanel() {
        super();
    } 
}
