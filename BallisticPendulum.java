import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.JMenu;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BallisticPendulum implements ActionListener, ChangeListener {
    // Properties
    JFrame frame = new JFrame("Ballistic Pendulum Simulation");
    //JPanel panel = new JPanel();
    AnimationPanel panel = new AnimationPanel();
    AboutPanel about_panel = new AboutPanel();
    Timer timer = new Timer(1000/48, this); //The timer goes off at 48fps

    JMenuBar menuBar = new JMenuBar();
    JMenu helpMenu = new JMenu("Help");
    JMenuItem aboutMenu = new JMenuItem("About");
    
    //Sliders for length because we want it to be int, and initial velocity
    JSlider lengthSlider; 
    JTextField sliderValue; //To display the slider value
    JSlider ViSlider;
    JTextField ViValue; 
    
    //JTextFields for mass because we allow user to input doubles:
    JTextField massBulletInput; 
    JTextField massBobInput; 

    //Buttons:
    JButton resetButton = new JButton("Reset"); //Button to reset the pendulum setup

    //Since there are two parts to the animation, we need booleans to keep track of what is going
    boolean bulletLaunchFinished = false;

    // Event Listeners
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == timer) {
            
            //Animating bullet launch:
            if (panel.bulletX < (panel.pendulumBobX - panel.bobDimension/4)) {
                panel.bulletX += (0.1*panel.bulletVi); //Bullet moves faster if vi is increased
            }

            //Go to Part 2 Swinging animation once bullet hits the pendulum
            if (panel.bulletX == (panel.pendulumBobX - panel.bobDimension/4)) {
                bulletLaunchFinished = true;
            }

            //Animating pendulum swing with angle calculations
            if (panel.currentTheta < panel.goalTheta && bulletLaunchFinished) {
                panel.currentTheta += (0.0003*panel.bulletVi); //Angle adjusts faster if vi is faster
            }

            panel.repaint();

        } else if (evt.getSource() == aboutMenu) {
            frame.setContentPane(about_panel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setResizable(false);
            frame.setVisible(true);
        } else if (evt.getSource() == massBulletInput) {
            //Catch NumberFormatException
            try {
                panel.bulletMass = Double.parseDouble(massBulletInput.getText());
            } catch (NumberFormatException e) {
                massBulletInput.setText("Please input a double");
                e.printStackTrace();
            }
        } else if (evt.getSource() == massBobInput) {
            //Catch NumberFormatException
            try {
                panel.pendulumMass = Double.parseDouble(massBobInput.getText());
            } catch (NumberFormatException e) {
                massBobInput.setText("Please input a double");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent evt) {
        if (evt.getSource() == lengthSlider) {
            sliderValue.setText(lengthSlider.getValue()+"m");
            panel.pendulumMeter = lengthSlider.getValue();
        }

        if (evt.getSource() == ViSlider) {
            ViValue.setText(ViSlider.getValue()+"m/s");
            panel.bulletVi = ViSlider.getValue();
        }
    }

    // Methods

    // Constructor
    public BallisticPendulum() {
        panel.setPreferredSize(new Dimension(960, 540));
        panel.setLayout(null);

        //Add JComponents 
        lengthSlider = new JSlider(0, 45, 5); //Slider from 0-100m, with delta value of 5
        lengthSlider.setValue(5); //Set default value of 5m
        lengthSlider.setSize(250, 50);
        lengthSlider.setLocation(10, 50);
        lengthSlider.addChangeListener(this);
        lengthSlider.setMinorTickSpacing(5); //Set slider spacing 
        lengthSlider.setPaintTicks(true);
        lengthSlider.setPaintLabels(true);
        lengthSlider.setLabelTable(lengthSlider.createStandardLabels(5)); //Display slider spacing increments
        panel.add(lengthSlider);

        sliderValue = new JTextField(lengthSlider.getValue()+"m"); //To display the length of the pendulum
        sliderValue.setSize(50, 50);
        sliderValue.setLocation(260, 50);
        panel.add(sliderValue);

        ViSlider = new JSlider(0, 100, 0); //Create a new slider for initial velocity
        ViSlider.setValue(0); //Set default value of vi at 0m/s
        ViSlider.setSize(250, 50);
        ViSlider.setLocation(10, 125);
        ViSlider.addChangeListener(this);
        ViSlider.setMinorTickSpacing(10); //Set slider spacing 
        ViSlider.setPaintTicks(true);
        ViSlider.setPaintLabels(true);
        ViSlider.setLabelTable(lengthSlider.createStandardLabels(10)); //Display slider spacing increments
        panel.add(ViSlider);

        ViValue = new JTextField(ViSlider.getValue()+"m/s"); //To display the Vi of bullet value
        ViValue.setSize(50, 50);
        ViValue.setLocation(260, 125);
        panel.add(ViValue);

        //TextField for bullet mass input
        massBulletInput = new JTextField("Enter a double value for bullet mass. Default = 0.1kg");
        massBulletInput.setSize(300, 30);
        massBulletInput.setLocation(10, 200);
        panel.add(massBulletInput);

        //TextField for pendulum bob mass input
        massBobInput = new JTextField("Enter a double value for bob mass. Default = 1.0kg");
        massBobInput.setSize(300, 30);
        massBobInput.setLocation(10, 250);
        panel.add(massBobInput);

        //Reset button
        resetButton.setSize(200, 50);
        resetButton.setLocation(10, 350);
        resetButton.addActionListener(this);
        panel.add(resetButton);

        //Add menu items/panels 
        menuBar.add(helpMenu);
        menuBar.add(aboutMenu);
        frame.setJMenuBar(menuBar);

        //Start the timer:
        timer.start();

        //Add listeners for required properties:
        aboutMenu.addActionListener(this);

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