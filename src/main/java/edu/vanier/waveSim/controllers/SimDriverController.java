package edu.vanier.waveSim.controllers;

import com.opencsv.CSVReader;
import edu.vanier.waveSim.MainApp;
import edu.vanier.waveSim.models.CellularAnimTimer;
import edu.vanier.waveSim.models.CellularLogic;
import javafx.fxml.FXML;
import edu.vanier.waveSim.models.ConwayGameOfLifeLogic;
import edu.vanier.waveSim.models.SimLogicWave1;
import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.swing.JFileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class of the MainApp's UI.
 *
 * @author TODO
 */
public class SimDriverController{

    private final static Logger logger = LoggerFactory.getLogger(SimDriverController.class);
    
    private Stage primaryStage;
    
    private boolean animationRunning = false;
    
    int scale = 1;
    int delayMillis = 1;
    int sceneHeight;
    int sceneWidth;

    public SimDriverController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public int getSceneHeight() {
        return sceneHeight;
    }

    public void setSceneHeight(int sceneHeight) {
        this.sceneHeight = sceneHeight;
    }

    public int getSceneWidth() {
        return sceneWidth;
    }

    public void setSceneWidth(int sceneWidth) {
        this.sceneWidth = sceneWidth;
    }
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

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
    @FXML
    private Pane SimCanvasPane;
    @FXML
    private Label lblWi;
    @FXML
    private Label lblHi;
    @FXML
    private TabPane SimTabPane;
    
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
        
        
        
        // https://stackoverflow.com/questions/37678704/how-to-embed-javafx-canvas-into-borderpane
//        SimCanvas.widthProperty().bind(SimCanvasPane.widthProperty());
//        SimCanvas.heightProperty().bind(SimCanvasPane.heightProperty());
  
        SimTabPane.heightProperty().addListener((observable) -> {
            setHeight(SimTabPane.heightProperty().getValue().intValue(), simulation, animation, lblHi);
        });
        SimTabPane.widthProperty().addListener((observable) -> {
            setWidth(SimTabPane.widthProperty().getValue().intValue(), simulation, animation, lblWi);
        });
        
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
            try {
                handleLoadItm(simulation);
            } catch (FileNotFoundException ex) {
                java.util.logging.Logger.getLogger(SimDriverController.class.getName()).log(Level.SEVERE, null, ex);
            }
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
    
    /**TODO Documentation*/
    private void setWidth(int width, CellularLogic simulation, CellularAnimTimer animation, Label lblWidth) {
        animation.stop();
        pointList.clear();
        animationRunning = false;
        if (width % 2 == 1) {
            width --;
        }
        simulation.setWidth(width);
        simulation.setScaling(simulation.getScaling());
        SimCanvas.setWidth(width);
        simulation.clearScreen();
        lblWidth.setText("Width: "+width);
    }
    /**TODO Documentation*/
    private void setHeight(int height, CellularLogic simulation, CellularAnimTimer animation, Label lblHeight) {
        int HBoxHeight = 100;

        animation.stop();
        pointList.clear();
        animationRunning = false;
        if (height % 2 == 1) {
            height --;
        }
        int realHeight = height-HBoxHeight;
        simulation.setHeight(realHeight);
        simulation.setScaling(simulation.getScaling());
        SimCanvas.setHeight(realHeight);
        simulation.clearScreen();
        lblHeight.setText("Height: "+realHeight);
    }
    
    /**TODO Documentation -> switched the active simulation
     */
    private CellularLogic changeSim(String newValue, CellularLogic[] simulations, CellularLogic simulation) {
        int Height, Width;
        if (null == newValue) {
            return simulation;
        }else
            Height = simulation.getHeightY();
            Width = simulation.getWidthX();
            // set width and height for every simulation so they have the right width and height on switch
            for(CellularLogic sim:simulations){
                sim.setHeight(Height);
                sim.setWidth(Width);
            }
            switch (newValue) {
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
        
        pointList.clear();
 
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
        FileChooser f = new FileChooser();
            Stage stage  = new Stage();
            stage.setAlwaysOnTop(true);
            this.primaryStage.setAlwaysOnTop(false);
            File file = f.showOpenDialog(stage);
            this.primaryStage.setAlwaysOnTop(true);
        try(FileWriter fw = new FileWriter(file.getPath());
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
                    writer.write(Integer.toString(currentPoint.getX())+","+Integer.toString(currentPoint.getY())+",");
            }
            writer.write(Integer.toString(simulation.getHeightY())+",");
            writer.write(Integer.toString(simulation.getWidthX())+",");
            writer.write(Double.toString(primaryStage.getWidth())+",");
            writer.write(Double.toString(primaryStage.getHeight()));
            writer.write("\n");
        }
    }
    /**
     * This method loads the settings from a csv file chosen by the user.
     * The file needs to be csv, therefore, exception handling is used to verify the validity of the file chosen by the user.
     */
    private void handleLoadItm(CellularLogic simulation) throws FileNotFoundException {
        animationRunning=false;
        
        System.out.println("Load button clicked");
        try{
            FileChooser f = new FileChooser();
        Stage stage  = new Stage();
        stage.setAlwaysOnTop(true);
        this.primaryStage.setAlwaysOnTop(false);
        File file = f.showOpenDialog(stage);
        this.primaryStage.setAlwaysOnTop(true);
        // make sure that the file is csv
        /*
        Cannot mkae alert dialog appear on tp of main Window
        boolean isCsv = "csv".equals(file.getPath().substring(file.getPath().length()-3, file.getPath().length()));
        if(!isCsv){
            showAlert("The file chosen is not a csv file. Please use a csv file. Try again.");
            itmLoad.getOnAction();
        }
        */
        CSVReader reader = new CSVReader(new FileReader(file.getPath()));
        int saveOption = 0;
            String[] settings = reader.readAll().get(saveOption);
            // Set height and width
            // Of the stage
            primaryStage.setHeight(Double.parseDouble(settings[settings.length-1]));
            primaryStage.setWidth(Double.parseDouble(settings[settings.length-2]));
            // Of the canavs
            /*
            simulation.setWidthX(Integer.parseInt(settings[settings.length-4]));
            simulation.setWidth(Integer.parseInt(settings[settings.length-4]));
            SimCanvas.setWidth(Double.parseDouble(settings[settings.length-4]));
            SimTabPane.setPrefWidth(Double.parseDouble(settings[settings.length-4]));
            simulation.setHeightY(Integer.parseInt(settings[settings.length-3]));
            simulation.setHeight(Integer.parseInt(settings[settings.length-3]));
            SimCanvas.setHeight(Double.parseDouble(settings[settings.length-3]));
            SimTabPane.setPrefHeight(Double.parseDouble(settings[settings.length-3]));
            System.out.println(simulation.getOperatingCanvas().getWidth());
            */
            //Set scaling
            simulation.setScaling(Integer.parseInt(settings[1]));
            // Set the damping
            sldrDamping.adjustValue(Double.parseDouble(settings[0]));
            // Set scale
            int scale = Integer.parseInt(settings[1]);
            scaleChoice.setValue(scale);
            // Set simulation type
            simTypeChoice.setValue(settings[2]);
            changeSim(simTypeChoice.getValue().toString(), simulationsList, simulation);
            // Set simulation speed
            sldrSpeed.adjustValue(Double.parseDouble(settings[3]));
            //Set points
            int x,y;
            for(int counterIndex = 0; counterIndex<((settings.length-8)/2); counterIndex++){
                x=0;
                y=0;
                for(int counterCoordinates=0; counterCoordinates<2; counterCoordinates++){
                    if(counterCoordinates==0)
                        x=Integer.parseInt(settings[(counterIndex*2)+4]);
                    else
                        y=Integer.parseInt(settings[(counterIndex*2)+5]);
                }
                System.out.println("Points: x="+x+" and y="+y);
                //simulation.colorCell(x,y, Color.CORAL);
                
                newPoint((double)x,(double)y,simulation);
                //simulation.setPoint((x), (y));
                //pointList.add(new Point(x,y));
            }
            System.out.println(settings.length);
            
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
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Please try again.");
        alert.setHeaderText(message);
        alert.showAndWait();
        if(alert.getResult() == ButtonType.OK){
            System.out.println("Error message seen.");
        }
        
    }
}
