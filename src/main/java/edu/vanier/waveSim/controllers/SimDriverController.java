package edu.vanier.waveSim.controllers;

import edu.vanier.waveSim.models.CellularAnimTimer;
import edu.vanier.waveSim.models.CellularLogic;
import javafx.fxml.FXML;
import edu.vanier.waveSim.deprecated.Grid;
import edu.vanier.waveSim.deprecated.GridPixel;
import edu.vanier.waveSim.deprecated.SimLogic;
import edu.vanier.waveSim.models.SimLogicWave1;
import java.util.ArrayList;
import java.util.HashSet;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class of the MainApp's UI.
 *
 * @author TODO
 */
public class SimDriverController {

    private final static Logger logger = LoggerFactory.getLogger(SimDriverController.class);

    private boolean animationRunning = false;
    
    
    /**Point object for use in array of origin points*/
    private class Point{
        private int x;
        private int y;
        
        Point(int x, int y){
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Point other = (Point) obj;
            if (this.x != other.x) {
                return false;
            }
            return this.y == other.y;
        }
        
    }
    
    HashSet<Point> pointList;
    
    
//    get elements from FXML
    @FXML
    private Canvas SimCanvas;
    @FXML
    private Button btnPlay;
    @FXML
    private Button btnPause;
    @FXML
    private Button btnReset;
    @FXML
    private ChoiceBox scaleChoice;
    @FXML
    private ChoiceBox simTypeChoice;
    @FXML
    private Slider sldrDamping;
    @FXML
    private Label lblDamping;
    
    // list of choices for scale factor, 1 and then multiples of 2 (for math reasons)
    ObservableList<Integer> scaleChoiceItems = FXCollections.observableArrayList(1,2,4,6,8);
    
    //list of simulation types, simple wave, etc
    ObservableList<String> simTypeChoiceItems = FXCollections.observableArrayList("Simple Ripple");
    
    /**
     * Initialize the FXML file of the simulation, assignee events to the controllers and 
     * import the simulation the the FXML file.
     */
    @FXML
    public void initialize() {
        // create simulation object
        SimLogicWave1 simulation = new SimLogicWave1(SimCanvas, (int) SimCanvas.getWidth(), (int) SimCanvas.getHeight(), 1);
        CellularAnimTimer animation = new CellularAnimTimer(simulation);
        simulation.clearScreen();
        
        pointList = new HashSet<>();
        
        // set ChoiceBox elements
        scaleChoice.setValue(1);
        scaleChoice.setItems(scaleChoiceItems);
        
        simTypeChoice.setValue("Simple Ripple");
        simTypeChoice.setItems(simTypeChoiceItems);
        
        
        btnPlay.setOnAction((event) -> {
            handlePlayBtn(simulation, animation);
        });
         
        btnPause.setOnAction((event) -> {
            handlePauseBtn(animation);
        });
        
        btnReset.setOnAction((event) -> {
            ResetScreenAndAnim(simulation, animation,simulation.getScaling());
        });
        
        // add listener to slider to change the damping during  simulation, Comes from (ukasp, JavaFX: Slider class 2022) see README
        sldrDamping.valueProperty().addListener(new ChangeListener<Number>() {

                @Override
                public void changed(
                   ObservableValue<? extends Number> observableValue, 
                   Number oldValue, 
                   Number newValue) {
                      // map damping
                      simulation.setDamping(1-newValue.floatValue());
                  }
        });
        
        // add listener to scaling choicebox to change the scaling. This clears the screen and stops the animation and clears the origin point list.
        scaleChoice.valueProperty().addListener(new ChangeListener<Number>() {

                @Override
                public void changed(
                   ObservableValue<? extends Number> observableValue, 
                   Number oldValue, 
                   Number newValue) {
                      ResetScreenAndAnim(simulation, animation,newValue.intValue());
                  }
        });
        
        //add listener to simulation type choicebox to change the simulation type. This will change the simulation logic.
        simTypeChoice.valueProperty().addListener(new ChangeListener<String>()  {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                
            }
                
            
        });
        
        // bind text property to the slider value
        lblDamping.textProperty().bind(Bindings.format("%.3f",sldrDamping.valueProperty()));
        
        // get coordinates of mouse on click
        SimCanvas.setOnMouseClicked((event) -> {
            newPoint(event.getX(),event.getY(), simulation);
        });
        
    }
    
    /**
     * Create a red point in the simulation on the selected x and y coordinate
     * that follows the chosen cellular logic.
     * @param x The horizontal position of the new point
     * @param y The vertical position of the new point 
     * @param simulation The cellular logic that the point will follow
     */
    private void newPoint(double x, double y, CellularLogic simulation) {
        int xFloor = (int)Math.floor(x);
        int yFloor = (int)Math.floor(y);
        int xFloorScaled = (int)Math.floor(x)/simulation.getScaling();
        int yFloorScaled = (int)Math.floor(y)/simulation.getScaling();
        Point clickPoint = new Point(xFloorScaled, yFloorScaled);
        if (!pointList.contains(clickPoint) && xFloorScaled < simulation.getScaledX()-1 && yFloorScaled < simulation.getScaledY()-1 && xFloorScaled > 0 && yFloorScaled > 0) {
            
            if (animationRunning == false) {
                // add the point to the ArrayList of current points.
                pointList.add(clickPoint);
            }
            // set the point in the simulation
            simulation.setPoint(xFloor, yFloor);
            // add the point to the canvas as Color.RED
            // the scaling must be adjusted because colorCell uses array coorrdinates, not canvas coordinates
            simulation.colorCell(xFloorScaled, yFloorScaled, Color.RED);
        }else if (animationRunning == false && pointList.contains(clickPoint)){
            pointList.remove(clickPoint);
            // if the point was removed from the array, remove from canvas.
            if (simulation.removePoint(xFloor, yFloor)) {
                simulation.colorCell(xFloorScaled, yFloorScaled, simulation.getBackgroundColor());
            }
        }
    }
    
    
    /**
     * Event that is activated when the play button is clicked.
     * The animation will play.
     * @param simulation the simulation on of the animation
     * @param animation the animation it will handle
     */
    private void handlePlayBtn(SimLogicWave1 simulation, CellularAnimTimer animation){
        System.out.println("Play");
        
        System.out.println("STARTING THE SIMULATION");
        
        System.out.println(simulation.getDamping());
        
        animationRunning = true;
        
        animation.start();
 
    }
    /**
     * Event that is activated when the pause button is clicked.
     * The animation will stop.
     * @param animation the animation it will handle
     */
    private void handlePauseBtn(CellularAnimTimer animation) {
        System.out.println("Stop button pressed");
        animation.stop();
        animationRunning = false;
        System.out.println("Animation stopped");
    }
    
    /**
     * Reset the animation and screen
     * The animation will stop and the simulation will be cleared.
     * @param simulation CellularLogic object providing the simulation target 
     * @param animation CellularAnimTimer object providing the animation it will handle
     * @param scaling scaling by which to reset the animation with
     */
    public void ResetScreenAndAnim(CellularLogic simulation, CellularAnimTimer animation ,int scaling) {
        simulation.setScaling(scaling);
        simulation.clearScreen();
        pointList.clear();
        animation.stop();
        animationRunning = false;
    }
}
