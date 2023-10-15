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
 * @author frostybee
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
    
    HashSet<Point> pointList = new HashSet<>();
    
    
//    get elements from FXML
    @FXML
    private Canvas SimCanvas;
    @FXML
    private Button btnTest;
    @FXML
    private Button btnStop;
    @FXML
    private Button btnStart;
    @FXML
    private ChoiceBox scaleChoice;
    @FXML
    private Slider sldrDamping;
    @FXML
    private Label lblDamping;
    
    // list of choices for scale factor, 1 and then multiples of 2 (for math reasons)
    ObservableList<Integer> scaleChoiceItems = FXCollections.observableArrayList(1,2,4,6,8);
    
    @FXML
    public void initialize() {
        // create simulation object
        SimLogicWave1 simulation = new SimLogicWave1(SimCanvas, (int) SimCanvas.getWidth(), (int) SimCanvas.getHeight(), 1);
        CellularAnimTimer animation = new CellularAnimTimer(simulation);
        
        scaleChoice.setValue(1);
        scaleChoice.setItems(scaleChoiceItems);
        
        
        btnTest.setOnAction((event) -> {
            handleTestBtn(simulation, animation);
        });
         
        btnStop.setOnAction((event) -> {
            handleStopBtn(animation);
        });
        
        btnStart.setOnAction((event) -> {
            handleStartBtn(animation);
        });
        
        // add listenner to slider to change the damping during  simulation
        sldrDamping.valueProperty().addListener(new ChangeListener<Number>() {

                @Override
                public void changed(
                   ObservableValue<? extends Number> observableValue, 
                   Number oldValue, 
                   Number newValue) { 
                      simulation.setDamping(newValue.floatValue());
                  }
        });
        
        // add listenner to scaling choicebox to change the scaling. This clears the screen and stops the animation and clears the origin point list.
        scaleChoice.valueProperty().addListener(new ChangeListener<Number>() {

                @Override
                public void changed(
                   ObservableValue<? extends Number> observableValue, 
                   Number oldValue, 
                   Number newValue) {
                      simulation.setScaling(newValue.intValue());
                      simulation.clearScreen();
                      pointList.clear();
                      animation.stop();
                  }
        });
        
        // bind text property to the slider value
        lblDamping.textProperty().bind(Bindings.format("%.3f",sldrDamping.valueProperty()));
        
        // get coordinates of mouse on click
        SimCanvas.setOnMouseClicked((event) -> {
            newPoint(event.getX(),event.getY(), simulation);
        });
        
    }
    
    private void newPoint(double x, double y, CellularLogic simulation) {
        int xFloor = (int)Math.floor(x);
        int yFloor = (int)Math.floor(y);
        int xFloorScaled = (int)Math.floor(x)/simulation.getScaling();
        int yFloorScaled = (int)Math.floor(y)/simulation.getScaling();
        Point clickPoint = new Point(xFloorScaled, yFloorScaled);
        if (!pointList.contains(clickPoint) && xFloorScaled < simulation.getScaledX()-1 && yFloorScaled < simulation.getScaledY()-1) {
            
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
    
    private void handleTestBtn(SimLogicWave1 simulation, CellularAnimTimer animation){
        System.out.println("Test");
        
        System.out.println("STARTING THE SIMULATION");
        
        System.out.println(simulation.getDamping());
        
        animationRunning = true;
        
        animation.start();
 
    }
    
    private void handleStopBtn(CellularAnimTimer animation) {
        System.out.println("Stop button pressed");
        animation.stop();
        animationRunning = false;
        System.out.println("Animation stopped");
    }
    
    
    public void handleStartBtn(CellularAnimTimer animation) {
        System.out.println("Restarting animation button pressed");
        animation.start();
        animationRunning = true;
        System.out.println("Restarted animation");
    }
}
