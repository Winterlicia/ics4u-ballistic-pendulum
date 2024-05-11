import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BallisticPendulum implements ActionListener, ChangeListener {
    // Properties
    JFrame frame = new JFrame("Ballistic Pendulum Simulation");
    JPanel panel = new JPanel();
    AnimationPanel animationPanel = new AnimationPanel();
    HelpPanel helpPanel = new HelpPanel();
    AboutPanel aboutPanel = new AboutPanel();
    Timer timer = new Timer(1000/48, this); //The timer goes off at 48fps

    JMenuBar menuBar = new JMenuBar();
    JMenuItem simulationMenuItem = new JMenuItem("Simulation");
    JMenuItem helpMenuItem = new JMenuItem("Help");
    JMenuItem aboutMenuItem = new JMenuItem("About");

    CardLayout cardLayout = new CardLayout();
    
    //Sliders for length because we want it to be int, and initial velocity
    JSlider lengthSlider; 
    JTextField lengthValue; //To display the slider value
    JSlider ViSlider;
    JTextField ViValue; 
    
    //JTextFields for mass because we allow user to input doubles:
    JTextField massBulletInput; 
    JTextField massBobInput; 

    //Buttons:
    JButton resetButton = new JButton("Reset"); //Button to reset the pendulum setup
    JButton launchButton = new JButton("Launch"); //Button to start the bullet launch

    //Labels:
    JLabel angleResultLabel = new JLabel();
    JLabel errorMessageLabel = new JLabel();

    //Since there are two parts to the animation, we need booleans to keep track of what is going on
    boolean bulletLaunchFinished = false;
    boolean startLaunch = false;
    static boolean pendulumLaunchFinished = false;

    // Event Listeners
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == timer) {

            //Only launch if button is pressed:
            if (startLaunch == true) {
                //Update masses:
                //Catch NumberFormatException in bullet mass input
                try {
                    animationPanel.bulletMass = Double.parseDouble(massBulletInput.getText());
                    System.out.println(animationPanel.bulletMass);
                } catch (NumberFormatException e) {
                    massBulletInput.setText("Please input a double");
                    e.printStackTrace();
                }

                //Catch NumberFormatException in pendulum mass input
                try {
                    animationPanel.pendulumMass = Double.parseDouble(massBobInput.getText());
                } catch (NumberFormatException e) {
                    massBobInput.setText("Please input a double");
                    e.printStackTrace();
                }

                //Handle angle exception when arccos(theta) cannot be calculated.
                if (Double.isNaN(animationPanel.goalTheta)) {
                    errorMessageLabel.setFont(new Font("Arial", Font.BOLD, 30));
                    errorMessageLabel.setText("Bullet mass or initial velocity too large");
                    forceReset();
                    startLaunch = false;
                }

                //Handle exception where pendulum length/mass is so large, the angle is effectively zero (negligible):
                if (animationPanel.goalTheta == 0.0) {
                    errorMessageLabel.setFont(new Font("Arial", Font.BOLD, 18));
                    errorMessageLabel.setText("Pendulum length or bob mass is too large, angle can't change");
                    forceReset();
                    startLaunch = false;
                }

                 //Handle exception where bullet can't move if initial velocity is zero:
                 if (animationPanel.bulletVi == 0) {
                    errorMessageLabel.setFont(new Font("Arial", Font.BOLD, 18));
                    errorMessageLabel.setText("The system cannot move if the bullet has zero initial velocity");
                    forceReset();
                    startLaunch = false;
                }

                //Animating bullet launch:
                if (animationPanel.bulletX < (animationPanel.pendulumBobX - animationPanel.bobDimension/4) && !bulletLaunchFinished) {
                    animationPanel.bulletX += (0.48*animationPanel.bulletVi); //Bullet moves faster if vi is increased.
                    System.out.println(0.48*animationPanel.bulletVi);

                    if ((animationPanel.pendulumBobX - animationPanel.bobDimension/4) < animationPanel.bulletX) { //If current bulletX overshoots the goal, set them equal and break out of this loop
                        animationPanel.bulletX = animationPanel.pendulumBobX - animationPanel.bobDimension/4;
                        bulletLaunchFinished = true;
                    } 
                }

                //Go to Part 2 Swinging animation once bullet hits the pendulum
                //System.out.println(panel.bulletX+" = "+(panel.pendulumBobX - panel.bobDimension/4));
                if (animationPanel.bulletX == (animationPanel.pendulumBobX - animationPanel.bobDimension/4)) {
                    System.out.println("Bullet launch successful");
                    bulletLaunchFinished = true;
                }

                //Animating pendulum swinging with the bullet, using angle calculations
                if (animationPanel.currentTheta < animationPanel.goalTheta && bulletLaunchFinished && !pendulumLaunchFinished) {
                    animationPanel.currentTheta += (0.0005*animationPanel.bulletVi); //Angle adjusts faster if vi is faster
                    
                    if (animationPanel.goalTheta < animationPanel.currentTheta) { //If current theta overshoots the goal, set them equal and break out of this loop
                        animationPanel.currentTheta = animationPanel.goalTheta;
                        pendulumLaunchFinished = true;
                    } 
                }

                System.out.println(animationPanel.currentTheta+ " = " + animationPanel.goalTheta);
                if (animationPanel.currentTheta == animationPanel.goalTheta) {
                    launchButton.setText("Launch successful!");
                    animationPanel.RESETTING_FACTOR = 1;
                    startLaunch = false;
                    bulletLaunchFinished = false;
                    pendulumLaunchFinished = true;
                    angleResultLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                    angleResultLabel.setText("    = "+Math.round(Math.toDegrees(animationPanel.goalTheta))+"°");

                    forceReset();
                }
            }
            animationPanel.repaint();

        } else if (evt.getSource() == launchButton) {
            launchButton.setText("Launching...");
            startLaunch = true;

        } else if (evt.getSource() == resetButton) {
            resetSimulation();
        } else if (evt.getSource() == simulationMenuItem) {
            cardLayout.show(panel, "Animation");
        } else if (evt.getSource() == helpMenuItem) {
            cardLayout.show(panel, "Help");
        } else if (evt.getSource() == aboutMenuItem) {
            cardLayout.show(panel, "About");
        }
    }

    @Override
    public void stateChanged(ChangeEvent evt) {
        if (evt.getSource() == lengthSlider) {
            lengthValue.setText(lengthSlider.getValue()+"m");
            animationPanel.pendulumMeter = lengthSlider.getValue();
        }

        if (evt.getSource() == ViSlider) {
            ViValue.setText(ViSlider.getValue()+"m/s");
            animationPanel.bulletVi = ViSlider.getValue();
        }
    }

    // Methods
    //Function to reset the entire simulation:
    private void resetSimulation() {
        //Reset JComponents and AnimationPanel Properties:
        lengthSlider.setEnabled(true);
        ViSlider.setEnabled(true);
        massBulletInput.setEditable(true);
        massBobInput.setEditable(true);
        launchButton.setEnabled(true);

        lengthSlider.setValue(5);
        ViSlider.setValue(0);
        massBulletInput.setText("0.1");
        massBobInput.setText("1.0");
        angleResultLabel.setText("");
        launchButton.setText("Launch");
        errorMessageLabel.setText("");

        startLaunch = false;
        bulletLaunchFinished = false;
        pendulumLaunchFinished = false;

        animationPanel.pendulumMeter = 5;
        animationPanel.pendulumBobX = 600;
        animationPanel.pendulumBobY = 375;
        animationPanel.pendulumMass = 1.0;
        animationPanel.bulletX = 500.0;
        animationPanel.bulletMass = 0.1;
        animationPanel.bulletVi = 0.0;
        animationPanel.currentTheta = 0;
        animationPanel.goalTheta = 0;
        animationPanel.RESETTING_FACTOR = 0;
    }

    private void forceReset() {
        //Force the user to reset the button at the end of each simulation if they want to go again
        launchButton.setFont(new Font("Arial", Font.BOLD, 11));
        launchButton.setText("Please reset to start again");
        launchButton.setFont((Font) UIManager.getLookAndFeelDefaults().get("defaultFont"));
        //Prevent user from touching the JComponents until they reset the simulation:
        lengthSlider.setEnabled(false);
        ViSlider.setEnabled(false);
        massBulletInput.setEditable(false);
        massBobInput.setEditable(false);
        launchButton.setEnabled(false);
    }

    // Constructor
    public BallisticPendulum() {
        panel.setPreferredSize(new Dimension(960, 540));
        panel.setLayout(cardLayout);
        panel.add(animationPanel, "Animation");
        panel.add(helpPanel, "Help");
        panel.add(aboutPanel, "About");

        cardLayout.show(panel, "Animation");

        //Add menu items/panels 
        menuBar.add(simulationMenuItem);
        menuBar.add(helpMenuItem);
        menuBar.add(aboutMenuItem);

        simulationMenuItem.addActionListener(this);
        helpMenuItem.addActionListener(this);
        aboutMenuItem.addActionListener(this);

        frame.setJMenuBar(menuBar);

        //Add JComponents 
        JLabel lengthSliderLabel = new JLabel("Length Slider (m)");
        lengthSliderLabel.setSize(200, 40);
        lengthSliderLabel.setLocation(10, 10);
        animationPanel.add(lengthSliderLabel);

        lengthSlider = new JSlider(5, 45, 5); //Slider from 0-100m, with delta value of 5
        lengthSlider.setValue(5); //Set default value of 5m
        lengthSlider.setSize(250, 50);
        lengthSlider.setLocation(10, 50);
        lengthSlider.addChangeListener(this);
        lengthSlider.setMinorTickSpacing(5); //Set slider spacing 
        lengthSlider.setPaintTicks(true);
        lengthSlider.setPaintLabels(true);
        lengthSlider.setLabelTable(lengthSlider.createStandardLabels(5)); //Display slider spacing increments
        animationPanel.add(lengthSlider);

        lengthValue = new JTextField(lengthSlider.getValue()+"m"); //To display the length of the pendulum
        lengthValue.setSize(50, 50);
        lengthValue.setLocation(260, 50);
        animationPanel.add(lengthValue);

        JLabel viSliderLabel = new JLabel("Initial Velocity Slider (m/s)");
        viSliderLabel.setSize(200, 40);
        viSliderLabel.setLocation(10, 115);
        animationPanel.add(viSliderLabel);

        ViSlider = new JSlider(0, 50, 0); //Create a new slider for initial velocity
        ViSlider.setValue(0); //Set default value of vi at 0m/s
        ViSlider.setSize(250, 50);
        ViSlider.setLocation(10, 145);
        ViSlider.addChangeListener(this);
        ViSlider.setMinorTickSpacing(10); //Set slider spacing 
        ViSlider.setPaintTicks(true);
        ViSlider.setPaintLabels(true);
        ViSlider.setLabelTable(ViSlider.createStandardLabels(10)); //Display slider spacing increments
        animationPanel.add(ViSlider);

        ViValue = new JTextField(ViSlider.getValue()+"m/s"); //To display the Vi of bullet value
        ViValue.setSize(50, 50);
        ViValue.setLocation(260, 145);
        animationPanel.add(ViValue);

        //Label and TextField for bullet mass input
        JLabel inputDescriptor = new JLabel("Bullet & Pendulum Mass Input (kg)");
        inputDescriptor.setSize(220, 40);
        inputDescriptor.setLocation(10, 195);
        animationPanel.add(inputDescriptor);

        massBulletInput = new JTextField("0.1");
        massBulletInput.setSize(300, 30);
        massBulletInput.setLocation(10, 230);
        animationPanel.add(massBulletInput);

        //TextField for pendulum bob mass input
        massBobInput = new JTextField("1.0");
        massBobInput.setSize(300, 30);
        massBobInput.setLocation(10, 265);
        animationPanel.add(massBobInput);

        //Reset button
        resetButton.setSize(200, 50);
        resetButton.setLocation(50, 300);
        resetButton.addActionListener(this);
        animationPanel.add(resetButton);

        //Launch button
        launchButton.setSize(200, 50);
        launchButton.setLocation(50, 355);
        launchButton.addActionListener(this);
        animationPanel.add(launchButton);

        //JLabel to show the final angle result based on the equation:
        angleResultLabel.setHorizontalAlignment(JLabel.LEFT);
        angleResultLabel.setSize(100, 50);
        angleResultLabel.setLocation(10, 460);
        animationPanel.add(angleResultLabel);

        //Error message label
        errorMessageLabel.setHorizontalAlignment(JLabel.CENTER);
        errorMessageLabel.setSize(930-380, 50);
        errorMessageLabel.setLocation(380, 400);
        animationPanel.add(errorMessageLabel);

        //Start the timer:
        timer.start();

        //Add listeners for required properties:

        //Add default settings
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    

    // Main
    public static void main(String[] args) {
        new BallisticPendulum();
    }
}