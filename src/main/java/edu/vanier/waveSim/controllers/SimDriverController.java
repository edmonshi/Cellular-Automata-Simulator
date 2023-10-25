package edu.vanier.waveSim.controllers;

import com.opencsv.CSVReader;
import edu.vanier.waveSim.models.CellularAnimTimer;
import edu.vanier.waveSim.models.CellularLogic;
import javafx.fxml.FXML;
import edu.vanier.waveSim.models.ConwayGameOfLifeLogic;
import edu.vanier.waveSim.models.SimLogicWave1;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
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
    
    int scale = 1;
    int delayMillis = 1;
    
    
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
    CellularLogic[] simulationsList = new CellularLogic[3];
    CellularLogic simulation;
    CellularAnimTimer animation;
    
    
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
    @FXML
    private Slider sldrSpeed;
    @FXML
    private Label lblSpeed;
    @FXML
    private MenuItem itmSave;
    @FXML
    private MenuItem itmLoad;
    @FXML
    private Button btnSaveRender;
    @FXML
    private Button btnPauseRender;
    @FXML
    private Button btnResetRender;
    
    // list of choices for scale factor, 1 and then multiples of 2 (for math reasons)
    ObservableList<Integer> scaleChoiceItems = FXCollections.observableArrayList(1,2,4,6,8);
    
    //list of simulation types, simple wave, etc
    ObservableList<String> simTypeChoiceItems = FXCollections.observableArrayList("Simple Ripple", "Conway's Game of Life");
    
    /**
     * Initialize the FXML file of the simulation, assignee events to the controllers and 
     * import the simulation the the FXML file.
     */
    @FXML
    public void initialize() {
        // create simulation objects

        SimLogicWave1 WaveSim = new SimLogicWave1(SimCanvas, (int) SimCanvas.getWidth(), (int) SimCanvas.getHeight(), 1);
        ConwayGameOfLifeLogic Conway = new ConwayGameOfLifeLogic(SimCanvas, (int) SimCanvas.getWidth(), (int) SimCanvas.getHeight(), 1);
        
        // initialize default simulation
        simulation = WaveSim;
        
        simulationsList[0] = simulation;
        simulationsList[1] = WaveSim;
        simulationsList[2] = Conway;
        
        // initialize default animation object
        animation = new CellularAnimTimer(simulation);
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
        itmSave.setOnAction((event)->{
            try {
                handleSaveItm(simulation);
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
        });
        itmLoad.setOnAction((event)->{
            handleLoadItm(simulation);
        });
        // add listener to damping slider to change the damping during  simulation, Comes from (ukasp, JavaFX: Slider class 2022) see README
        sldrDamping.valueProperty().addListener(new ChangeListener<Number>() {

                @Override
                public void changed(
                   ObservableValue<? extends Number> observableValue, 
                   Number oldValue, 
                   Number newValue) {
                      // map damping
                      WaveSim.setDamping(1-newValue.floatValue());
                  }
        });
        
        // add listener to speed slider to change the damping during  simulation, Comes from (ukasp, JavaFX: Slider class 2022) see README
        sldrSpeed.valueProperty().addListener(new ChangeListener<Number>() {

                @Override
                public void changed(
                   ObservableValue<? extends Number> observableValue, 
                   Number oldValue, 
                   Number newValue) {
                      // map damping
                      animation.setDelayMillis(newValue.intValue());
                      delayMillis = newValue.intValue();
                  }
        });
        
        // add listener to scaling choicebox to change the scaling. This clears the screen and stops the animation and clears the origin point list.
        scaleChoice.valueProperty().addListener(new ChangeListener<Number>() {

                @Override
                public void changed(
                   ObservableValue<? extends Number> observableValue, 
                   Number oldValue, 
                   Number newValue) {
                      scale = newValue.intValue();
                      ResetScreenAndAnim(simulation, animation,newValue.intValue());
                  }
        });
        
        //add listener to simulation type choicebox to change the simulation type. This will change the simulation logic.
        simTypeChoice.valueProperty().addListener(new ChangeListener<String>()  {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                animation.stop();
                simulation = changeSim(newValue, simulationsList, simulation);
                animation = new CellularAnimTimer(simulation);
                animation.setDelayMillis(delayMillis);
                ResetScreenAndAnim(simulation, animation, scale);
            }  
        });
        
        // bind text property to the slider value damping
        lblDamping.textProperty().bind(Bindings.format("%.3f",sldrDamping.valueProperty()));
        // bind text property to the slider value speed
        lblSpeed.textProperty().bind(Bindings.format("%1.0f",sldrSpeed.valueProperty()));
        
        // get coordinates of mouse on click
        SimCanvas.setOnMouseClicked((event) -> {
            newPoint(event.getX(),event.getY(), simulation);
        });
        
    }
    
    /**TODO Documentation -> switched the active simulation
     */
    private CellularLogic changeSim(String newValue, CellularLogic[] simulations, CellularLogic simulation) {
        if (null == newValue) {
            return simulation;
        }else switch (newValue) {
            case "Simple Ripple" -> {
                simulation = simulations[1];
                return simulation;
            }
            case "Conway's Game of Life" -> {
                simulation = simulations[2];
                return simulation;
            }
            default -> {
                return simulation;
            }
        }
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
    private void handlePlayBtn(CellularLogic simulation, CellularAnimTimer animation){
        System.out.println("STARTING THE SIMULATION");
        
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
     * This method saves the settings of a simulation in a CSV File called settings.csv
     * This file is contained in the resources folder, in a package called data
     * Source used as an example to learn how to use PrintWriter to write in a Csv File: https://stackoverflow.com/questions/68218102/how-can-i-write-data-to-csv-in-chunks-via-printwriter-in-java
     */
    private void handleSaveItm(CellularLogic simulation) throws IOException {
        System.out.println("Save button clicked");
        
        try(FileWriter fw = new FileWriter("src/main/resources/data/settings.csv");
                PrintWriter writer = new PrintWriter(fw);){
            //Erase previous save settings
            writer.flush();
            //Write damping
            writer.write(Double.toString(sldrDamping.getValue())+",");
            //Write scale
            writer.write(scaleChoice.getValue().toString()+",");
            //Write simulation type
            writer.write(simTypeChoice.getValue().toString()+",");
            // Write speed
            writer.write(Double.toString(sldrSpeed.getValue())+",");
            //Write points
            for(Iterator<Point> points = pointList.iterator(); points.hasNext();){
                Point currentPoint = points.next();
                if(points.hasNext()==false)
                    writer.write(Integer.toString(currentPoint.getX())+","+Integer.toString(currentPoint.getY()));
                else
                    writer.write(Integer.toString(currentPoint.getX())+","+Integer.toString(currentPoint.getY())+",");
            }
            writer.write("\n");
        }
    }

    private void handleLoadItm(CellularLogic simulation) {
        System.out.println("Load button clicked");
        try{
            CSVReader reader = new CSVReader(new FileReader("src/main/resources/data/settings.csv"));
            int saveOption  = 0;
            String[] settings = reader.readAll().get(saveOption);
            // Set the damping
            sldrDamping.adjustValue(Double.parseDouble(settings[0]));
            // Set scale
            scaleChoice.setValue(Integer.parseInt(settings[1]));
            // Set simulation type
            simTypeChoice.setValue(settings[2]);
            simulation.setScaling(Integer.parseInt(settings[1]));
            // Set simulation speed
            sldrSpeed.adjustValue(Double.parseDouble(settings[3]));
            // Set points
            int x,y;
            System.out.println(settings.length);
            for(int counterIndex = 0; counterIndex<((settings.length-4)/2); counterIndex++){
                x=0;
                y=0;
                for(int counterCoordinates=0; counterCoordinates<2; counterCoordinates++){
                    if(counterCoordinates==0)
                        x=Integer.parseInt(settings[(counterIndex*2)+4]);
                    else
                        y=Integer.parseInt(settings[(counterIndex*2)+5]);
                }
                System.out.println("Points: x="+x+" and y="+y);
                simulation.colorCell(x, y, Color.CORAL);
                simulation.setPoint(x, y);
                pointList.add(new Point(x,y));
            }
            
        }catch(Exception e){
            System.out.println(e.toString());
        }
    }
    /**
     * Reset the animation and screen
     * The animation will stop and the simulation will be cleared.
     * @param simulation CellularLogic object providing the simulation target 
     * @param animation CellularAnimTimer object providing the animation it will handle
     * @param scaling scaling by which to reset the animation with
     */
    public void ResetScreenAndAnim(CellularLogic simulation, CellularAnimTimer animation ,int scaling) {
        for (int i=0;i<this.simulationsList.length;i++) {}
        simulation.setScaling(scaling);
        simulation.clearScreen();
        pointList.clear();
        animation.stop();
        animationRunning = false;
    }
}
