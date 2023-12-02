package edu.vanier.waveSim.controllers;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import edu.vanier.waveSim.models.CellularAnimTimer;
import edu.vanier.waveSim.models.CellularLogic;
import javafx.fxml.FXML;
import edu.vanier.waveSim.models.ConwayGameOfLifeLogic;
import edu.vanier.waveSim.models.SimBriansBrain;
import edu.vanier.waveSim.models.SimForestFire;
import edu.vanier.waveSim.models.SimLogicWave;
import edu.vanier.waveSim.models.SimRPC;
import edu.vanier.waveSim.models.SimDiffusionLimitedAggregation;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class of the MainApp's UI.
 * Contains the logic for most visual elements 
 *
 * @author William Carbonneau and Loovdrish Sujore
 */
public class FXMLMainAppController{

    private final static Logger logger = LoggerFactory.getLogger(FXMLMainAppController.class);
    
    private Stage primaryStage; // instance of the stage which uses this controller
    
    private boolean animationRunning = false; // is the animation running?
    
    private final Transition pause = new PauseTransition(Duration.millis(50)); // pause necessary for a workaround with rendering the canvas after resize
    
    private boolean hasLoadedViewFolder = false; // boolean fo rif the viewing folder has been loaded to use
    
    
    int scale = 1; // scale of canvas cells
    int delayMillis = 1; // delay of animation to be set
    int sceneHeight;
    int sceneWidth;
    String[] settings;

    /**
     * This getter returns the settings attribute of the FXML MainAppController object.
     * It is set after using the save settings feature, which saves the settings of the simulation in a csv file.
     * @return type String []
     */
    public String[] getSettings() {
        return settings;
    }
    /**
     * This setter sets the settings attribute of the FXML MainAppController object.
     * It is used when using the save settings feature, which saves the settings of the simulation in a csv file.
     * @param settings
     */
    public void setSettings(String[] settings) {
        this.settings = settings;
    }
    
    /**
     * Constructor of main controller 
     */
    public FXMLMainAppController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Get height of the scene
     * @return type int
     */
    public int getSceneHeight() {
        return sceneHeight;
    }

    /**
     * Set height of the scene in pixels
     * @param sceneHeight type int - new height of scene in pixels
     */
    public void setSceneHeight(int sceneHeight) {
        this.sceneHeight = sceneHeight;
    }

    /**
     * Get width of the scene
     * @return type int
     */
    public int getSceneWidth() {
        return sceneWidth;
    }

    /**
     * Set width of the scene in pixels
     * @param sceneWidth type int - new width of scene in pixels
     */
    public void setSceneWidth(int sceneWidth) {
        this.sceneWidth = sceneWidth;
    }

    /**
     * Get the instance of PrimaryStage
     * @return type Stage (javaFX) 
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Stop the running animation of the canvas and viewRender - to exit the application
     */
    public void stopAnimation() {
        animation.stop();
        viewRenderTimer.stop();
    }

    
    /**
     * Point class for use in array of origin points
     * Only useful as a private class because nothing else uses this
     */
    private class Point{
        private int x;
        private int y;
        /**
         * Constructor of a Point object on the screen.
         * @param x the position of the point on the x-axis
         * @param y the position of the point on the y-axis
         */
        Point(int x, int y){
            this.x = x;
            this.y = y;
        }
        /**
         * Getter that returns the location of a point on the x-axis
         * @return  an integer corresponding to the position of the point on the x-axis.
         */
        public int getX() {
            return x;
        }
        /**
         * Getter that returns the location of a point on the y-axis
         * @return  an integer corresponding to the position of the point on the y-axis.
         */
        public int getY() {
            return y;
        }
        /**
         * Setter that sets the position of the point on the x-axis
         * @param x the position of the point on the x-axis
         */
        public void setX(int x) {
            this.x = x;
        }
        /**
         * Setter that sets the position of the point on the y-axis
         * @param y the position of the point on the y-axis
         */
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
    
    private HashSet<Point> pointList; // list of origin points on screen
    private CellularLogic[] simulationsList = new CellularLogic[7]; // list of all simulations - active simulation is always instance 0
    private CellularLogic simulation; // the current active simulation
    private CellularAnimTimer animation; // the animation of the canvas
    private Integer viewRenderFrameDelay = 100; // delay for each frame of view render tab
    private EventHandler<ActionEvent> onFinishedFrameDelay = this::nextViewRenderFrame; // event handler for view render
    private Timeline viewRenderTimer = new Timeline(new KeyFrame(Duration.millis(viewRenderFrameDelay), onFinishedFrameDelay)); // timer fo runiform frame for view render tab
    private List<String> folderFiles = new ArrayList<String>(); // list of all files to load view render images from selected folder
    private int imageSequenceIndex = 0; // index of current image in sequence for view render
    /**
     * TODO
     */
    private void nextViewRenderFrame(ActionEvent event) {
        if (!hasLoadedViewFolder) {
            return;
        }
        
        System.out.println(folderFiles.size());
        if (imageSequenceIndex < folderFiles.size()) {
            imageViewSequence.setImage(new Image(folderFiles.get(imageSequenceIndex)));
            imageSequenceIndex++;
        }else {
            imageSequenceIndex = 0; // restart from beginning
        }
    }
    
    /**
     * Get and verify a list of files from a directory and save that curated list to
     * the instance variable. This will be used for the View Render animation
     * @return boolean that corresponds to whether or not the folder chosen by the user is valid.
     */
    private boolean getFileList() {
        File folder;
        Stage stage = new Stage();
        DirectoryChooser dc = new DirectoryChooser();
        primaryStage.setAlwaysOnTop(false);
        stage.setAlwaysOnTop(true);
        dc.setInitialDirectory(dc.showDialog(stage));
        stage.setAlwaysOnTop(false);
        primaryStage.setAlwaysOnTop(true);
        folder = dc.getInitialDirectory();
        if (folder == null || !folder.exists() || !folder.canRead()) {
            return false;
        }
        int csvs = 0;
        //Find the .csn file here
        File[] temp = folder.listFiles();
        File[] files = new File[temp.length-1];
        int counter=0;
        for(File file: temp){
            if(file.toString().endsWith(".csv")){
                System.out.println(file.getAbsolutePath());
                adjustSizeIV(file.getAbsolutePath());
            }
            else{
                files[counter] = file;
                counter++;
            }
        }
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));
        // get list of subfolders
        folderFiles.clear();
        for (File file:files) {
            folderFiles.add(file.getName());
        }
        
        System.out.println(folderFiles.toString());
        for (String nFile:folderFiles) {
            if (!nFile.endsWith(".bmp")) {
                if (nFile.endsWith(".csv")) {
                    System.out.println(nFile.toString());
                    csvs++;
                    folderFiles.remove(nFile);
                }
                folderFiles.remove(nFile);
            }else {
                folderFiles.set(folderFiles.indexOf(nFile), folder + "\\" + nFile);
            }
        }
        return !folderFiles.isEmpty();
    }
    /**
     * This method sets the size, the dimensions of the stage, and of the image view that is used to render the simulation.
     * Since the image view has been bound to the stage, the method sets only the dimensions of the stage, which will automatically set the dimensions of the image view.
     * @param path The path to the file containing the dimensions of the primary stage
     */
    private void adjustSizeIV(String path){
        try(CSVReader reader = new CSVReader(new FileReader(path))){
            String[] stageDimensions = reader.readNext();
            System.out.println(stageDimensions.toString());
            primaryStage.setWidth(Double.parseDouble(stageDimensions[0]));
            primaryStage.setHeight(Double.parseDouble(stageDimensions[1]));
        }catch(Exception e){
            System.out.println("File not read");
        }
    }
    
    /**
     * Utility for creating new refreshed animation timers
     */
    private CellularAnimTimer newAnimationTimer() {
        return new CellularAnimTimer(simulation, this);
    }
    
    //get elements from FXML
    @FXML private Canvas SimCanvas;
    @FXML private Button btnPlay;
    @FXML private Button btnPause;
    @FXML private Button btnReset;
    @FXML private ChoiceBox scaleChoice;
    @FXML private ChoiceBox simTypeChoice;
    @FXML private Slider sldrDamping;
    @FXML private Label lblDamping;
    @FXML private Slider sldrSpeed;
    @FXML private Label lblSpeed;
    @FXML private MenuItem itmSave;
    @FXML private MenuItem itmLoad;
    @FXML private MenuItem itmRenderStart;
    @FXML private MenuItem itmStopRender;
    @FXML private Button btnPauseRender;
    @FXML private Button btnResetRender;
    @FXML private Label lblWi;
    @FXML private Label lblHi;
    @FXML private TabPane SimTabPane;
    @FXML private MenuItem guideItm;
    @FXML private TextField txtBoxRippleLimit;
    @FXML private TextField txtBoxConwayLimit;
    @FXML private TextField txtBoxRPCLimit;
    @FXML private TextField txtBoxDLALimit;
    @FXML private TextField txtBoxSLALimit;
    @FXML private TextField txtBoxBrainFrameLimit;
    @FXML private Slider amplitudeSldr;
    @FXML private ImageView imageViewSequence;
    @FXML private Button btnPlayRender;
    @FXML private Button btnLoad;
    @FXML private Slider fireSldr;
    @FXML private Slider treeSldr;
    
    // list of choices for scale factor, 1 and then multiples of 2 (for math reasons)
    ObservableList<Integer> scaleChoiceItems = FXCollections.observableArrayList(1,2,4,6,8);
    
    //list of simulation types, simple wave, etc
    ObservableList<String> simTypeChoiceItems = FXCollections.observableArrayList("Simple Ripple", "Conway's Game of Life", "Rock-Paper-Scissors", "Forest Fire", "Diffusion Limited Aggregation", "Brian's Brain");
    
    /**
     * Initialize the FXML file of the simulation, assignee events to the controllers and 
     * import the simulation the the FXML file.
     */
    @FXML
    public void initialize() {
        // create simulation objects
        SimLogicWave WaveSim = new SimLogicWave(SimCanvas, (int) SimCanvas.getWidth(), (int) SimCanvas.getHeight(), 1);
        ConwayGameOfLifeLogic Conway = new ConwayGameOfLifeLogic(SimCanvas, (int) SimCanvas.getWidth(), (int) SimCanvas.getHeight(), 1);
        SimBriansBrain Brain = new SimBriansBrain(SimCanvas, (int) SimCanvas.getWidth(), (int) SimCanvas.getHeight(), 1);
        SimRPC RPC = new SimRPC(SimCanvas, (int) SimCanvas.getWidth(), (int) SimCanvas.getHeight(), 1);
        SimForestFire SLA = new SimForestFire(SimCanvas, (int) SimCanvas.getWidth(), (int) SimCanvas.getHeight(), 1);
        SimDiffusionLimitedAggregation DLA = new SimDiffusionLimitedAggregation(SimCanvas, (int) SimCanvas.getWidth(), (int) SimCanvas.getHeight(), 1);
        SimBriansBrain SBB = new SimBriansBrain(SimCanvas, (int) SimCanvas.getWidth(), (int) SimCanvas.getHeight(), 1);
        
        // initialize default simulation
        simulation = WaveSim;
        
        // add simualtion objects to the list
        simulationsList[0] = simulation;
        simulationsList[1] = WaveSim;
        simulationsList[2] = Conway;
        simulationsList[3] = RPC;
        simulationsList[4] = SLA;
        simulationsList[5] = DLA;
        simulationsList[6] = SBB;
        
        // view render works indefinitely
        viewRenderTimer.setCycleCount(Timeline.INDEFINITE);
        
        // initialize default animation object
        animation = newAnimationTimer();
        simulation.clearScreen();
        
        pointList = new HashSet<>();
        
        // set ChoiceBox elements
        scaleChoice.setValue(1);
        scaleChoice.setItems(scaleChoiceItems);
        
        // default simulation
        simTypeChoice.setValue("Simple Ripple");
        simTypeChoice.setItems(simTypeChoiceItems); // create drop-down values
        
        // set default btn disabled state
        btnPause.setDisable(true);
        btnReset.setDisable(true);
        
        // waiting util for load points
        pause.setOnFinished((event) -> {
            loadPointsUtil();
        });
        
        // bind height of simaultion to tab pane height
        SimTabPane.heightProperty().addListener((observable) -> {
            setHeight(SimTabPane.heightProperty().getValue().intValue(), simulation, animation, lblHi);
        });
        // bind width of simaultion to tab pane width
        SimTabPane.widthProperty().addListener((observable) -> {
            setWidth(SimTabPane.widthProperty().getValue().intValue(), simulation, animation, lblWi);
        });
        // handle render start from top menu bar
        itmRenderStart.setOnAction((event) -> {
            handleRenderStart();
        });
        // handle render stop from top menu bar
        itmStopRender.setOnAction((event) -> {
            handleRenderStop();
        });
        // handle play simulation button
        btnPlay.setOnAction((event) -> {
            handlePlayBtn(animation);
        });
        // handle pause simulation button
        btnPause.setOnAction((event) -> {
            handlePauseBtn(animation);
        });
        // handle reset simulation button
        btnReset.setOnAction((event) -> {
            ResetScreenAndAnim(simulation, animation,simulation.getScaling());
        });
        
        // handle text boxes for simulation limits
        txtBoxRippleLimit.textProperty().addListener((observable, previous, input) -> {
            int frameLimit = validateFrameLimit(input, txtBoxRippleLimit);
            WaveSim.setFrameLimit(frameLimit);
        });
        txtBoxConwayLimit.textProperty().addListener((observable, previous, input) -> {
            int frameLimit = validateFrameLimit(input, txtBoxConwayLimit);
            Conway.setFrameLimit(frameLimit);
        });
        txtBoxRPCLimit.textProperty().addListener((observable, previous, input) -> {
            int frameLimit = validateFrameLimit(input, txtBoxRPCLimit);
            RPC.setFrameLimit(frameLimit);
        });
        txtBoxSLALimit.textProperty().addListener((observable, previous, input) -> {
            int frameLimit = validateFrameLimit(input, txtBoxSLALimit);
            SLA.setFrameLimit(frameLimit);
        });
        txtBoxBrainFrameLimit.textProperty().addListener((observable, previous, input) -> {
            int frameLimit = validateFrameLimit(input, txtBoxBrainFrameLimit);
            SBB.setFrameLimit(frameLimit);
        });
        txtBoxDLALimit.textProperty().addListener((observable, previous, input) -> {
            int frameLimit = validateFrameLimit(input, txtBoxDLALimit);
            DLA.setFrameLimit(frameLimit);
        });
        // handle load button in view render tab 
        btnLoad.setOnAction((event) -> {
            hasLoadedViewFolder = getFileList();
        });
        // Bind the dimensions of the primary stage to the image view
        // Source used to understand the syntax: StackOverFlow 22993550, July 2017
        // The rest has been inspired from the code in this implementation (sldrDamping on Line 555)-> (ukasp, JavaFX: Slider class 2022) see README
        //Width
        primaryStage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                imageViewSequence.setFitWidth(newValue.doubleValue()-167);
            }
        });
        //Height
        // Source used to understand the syntax: StackOverFlow 22993550, July 2017
        // The rest has been inspired from the code in this implementation (sldrDamping on Line 555)-> (ukasp, JavaFX: Slider class 2022) see README
        primaryStage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                imageViewSequence.setFitHeight(newValue.doubleValue()-150);
            }
        });
        // change activity of buttons by default view render tab
        btnPauseRender.setDisable(true);
        btnResetRender.setDisable(true);
        
        // handle play view render button
        btnPlayRender.setOnAction((event) -> {
            
            viewRenderTimer.play();
            btnPlayRender.setDisable(true);
            btnPauseRender.setDisable(false);
            btnResetRender.setDisable(false);
        });
        //handle pause view render button
        btnPauseRender.setOnAction((event) -> {
            viewRenderTimer.pause();
            btnPlayRender.setDisable(false);
            btnPauseRender.setDisable(true);
            btnResetRender.setDisable(true);
        });
        // handle reset view render button 
        btnResetRender.setOnAction((event) -> {
            viewRenderTimer.stop();
            btnPlayRender.setDisable(false);
            btnPauseRender.setDisable(true);
            btnResetRender.setDisable(true);
            imageSequenceIndex = 0;
        });
        // handle save settings button from top menu bar 
        itmSave.setOnAction((event)->{
            try {
                try {
                    // handle the save item method
                    handleSaveItm(simulation);
                } catch (FileNotFoundException | CsvException ex) {
                    java.util.logging.Logger.getLogger(FXMLMainAppController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
        });
        // handle load settings button from top menu bar 
        itmLoad.setOnAction((event)->{
            try {
                // handle the load item method
                handleLoadItm(simulation);
            } catch (FileNotFoundException ex) {
                java.util.logging.Logger.getLogger(FXMLMainAppController.class.getName()).log(Level.SEVERE, null, ex);
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
        // Add listener to the slider which controls the probability of a fire catching, in the simaultion "Forest Fire"
        // Comes from (ukasp, JavaFX: Slider class 2022) see README
        fireSldr.valueProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue){
                SLA.setFire(newValue.doubleValue()/1000);
            }
        });
        // Add listener to the slider which controls the probability of a tree growing, in the simulation "Forest Fire"
        // Comes from (ukasp, JavaFX: Slider class 2022) see README
        treeSldr.valueProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue){
                SLA.setTree(newValue.doubleValue()/100);
            }
        });
        // Add listener to the slider which controls the amplitude of the waves, in the simulation "Wave Simulation"
        // Comes from (ukasp, JavaFX: Slider class 2022) see README
        amplitudeSldr.valueProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue){
                WaveSim.setAmplitude((int) amplitudeSldr.getValue());
                
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
                animation = newAnimationTimer();
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
        // handle guide button in top menu bar
        guideItm.setOnAction((event)->{
            try {
                handleGuideItm(guideItm);
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(FXMLMainAppController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
}
    
    /**
     * Set the width of the canvas for the simulation - also sets the width of the grid contained in the simulations
     * @param width type int the new width
     * @param simualtion type CellularLogic the current simulation
     * @param animation type CellularAnimTimer the animation of the canvas
     * @param lblWidth type Label (javaFX) the label to update
     */
    private void setWidth(int width, CellularLogic simulation, CellularAnimTimer animation, Label lblWidth) {
        animation.stop();
        pointList.clear();
        animationRunning = false;
        // make the width an even number
        if (width % 2 == 1) {
            width --;
        }
        simulation.setWidth(width);
        SimCanvas.setWidth(width);
        // reset the screen and simulations
        ResetScreenAndAnim(simulation, animation, scale);
        // set width label
        lblWidth.setText("Width: "+width);
    }
    /**
     * Set the height of the canvas for the simulation - also sets the height of the grid contained in the simulations
     * @param height type int the new height
     * @param simualtion type CellularLogic the current simulation
     * @param animation type CellularAnimTimer the animation of the canvas
     * @param lblHeight type Label (javaFX) the label to update
     */
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
        SimCanvas.setHeight(realHeight);
        ResetScreenAndAnim(simulation, animation, scale);
        lblHeight.setText("Height: "+realHeight);
    }
    
    /**
     * Switches the active simulation based on logic from drop-down menu
     * @param newValue type String the name of the simulation to switch to
     * @param simulations type CellularLogic[] a list of all simulations
     * @param simulation type CellularLogic the instance of current simulation
     */
    private CellularLogic changeSim(String newValue, CellularLogic[] simulations, CellularLogic simulation) {
        int Height, Width;
        if (null == newValue) {
            return simulation;
        }else{
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
                case "Rock-Paper-Scissors" ->{
                    simulation = simulations[3];
                    return simulation;                    
                }
                case "Forest Fire"->{
                    simulation = simulations[4];
                    return simulation;
                }
                case "Diffusion Limited Aggregation"->{
                    simulation = simulations[5];
                    return simulation;
                }
                case "Brian's Brain"->{
                    simulation = simulations[6];
                    return simulation;
                }
                default -> {
                    return simulation;
                }
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
            if (simulation != simulationsList[3] && simulation != simulationsList[5]) {
                // set the point in the simulation
                simulation.setPoint(xFloor, yFloor);
                // add the point to the canvas as Color.RED
                // the scaling must be adjusted because colorCell uses array coorrdinates, not canvas coordinates
                simulation.colorCell(xFloorScaled, yFloorScaled, Color.RED);
            }
        }else if (animationRunning == false && pointList.contains(clickPoint)){
            pointList.remove(clickPoint);
            // if the point was removed from the array, remove from canvas.
            if (simulation.removePoint(xFloor, yFloor) && simulation != simulationsList[3] && simulation != simulationsList[5]) {
                simulation.colorCell(xFloorScaled, yFloorScaled, simulation.getBackgroundColor());
            }
        }
    }
    
    /**
     * Start rendering the current animation upon button click 
     */
    private void handleRenderStart() {
        System.out.println("Start Render");
        String path = new File("").getAbsolutePath()+"/render"+System.currentTimeMillis();
        for (CellularLogic sim: simulationsList) {
            sim.setRenderFlag(true);
            sim.setRenderPath(path);
        }
        
        // create directory
        new File(path).mkdirs();
        saveDimensions(path);
        handlePlayBtn(animation);
    }
    /**
     * This method save the dimensions of the stage in a csv file.
     * @param path The path of the csv file
     */
    public void saveDimensions(String path){
        File file = new File(path+"\\"+"dimensions.csv");
        System.out.println(file.getAbsolutePath());
        try(FileWriter fw = new FileWriter(file);
            PrintWriter writer = new PrintWriter(fw);){
            writer.write(Double.toString(primaryStage.getWidth())+",");
            writer.write(Double.toString(primaryStage.getHeight()));
        }catch(Exception e){
        }
    }
    /**
     * Handle the render stop button operation
     */
    private void handleRenderStop() {
        System.out.println("Stop Render");
        for (CellularLogic sim: simulationsList) {
            sim.setRenderFlag(false);
        }
        ResetScreenAndAnim(simulation, animation, scale);
    }
    
    private int frameLim; // TODO docs comment explain this

    /**
     * Getter that returns the frame limit
     * @return frameLim integer that corresponds to the frame limit
     */
    public int getFrameLim() {
        return frameLim;
    }

    /**
     * Setter that sets the frame limit
     * @param frameLim integer that corresponds to the frame limit
     */
    public void setFrameLim(int frameLim) {
        this.frameLim = frameLim;
    }
    
    /**
     * Validate integer from TextField as valid int
     * @param input the input String from the TextField
     * @param box the instance of the TextField
     * @return the integer which corresponds to the frame limit
     */
    private int validateFrameLimit(String input, TextField box) {
        if ("max".equals(input)) return Integer.MAX_VALUE;
        try {
            int inputInt = Integer.parseInt(input);
            if (inputInt > 0) {
                return inputInt;
            }else {
                throw(new NumberFormatException());
            }
        }catch (NumberFormatException e) {
            box.setText("max");
            return Integer.MAX_VALUE;
        }
    }
    
    
    /**
     * Event that is activated when the play button is clicked.
     * The animation will play.
     * @param animation the animation it will handle
     */
    private void handlePlayBtn(CellularAnimTimer animation){
        System.out.println("STARTING THE SIMULATION");
        
        animationRunning = true;
        
        animation.start();
        
        pointList.clear();
        btnPlay.setDisable(true);
        btnPause.setDisable(false);
        btnReset.setDisable(false);
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
        btnPlay.setDisable(false);
        btnPause.setDisable(true);
        btnReset.setDisable(false);
        System.out.println("Animation stopped");
    }
    
    //Corresponds to the name chosen by the user to name the csv file that will be created by the program, when using the save settings feature
    private String nameFile;
    /**
     * Return the value of nameFile
     * @return type String
     */
    public String getNameFile() {
        return nameFile;
    }

    /**
     * Set the value of nameFile
     * @param nameFile type String - new value
     */
    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }
    
    /**
     * This method saves the settings of a simulation in a CSV File.
     * The file can either be created by the method inside of a specified directory by the user, or the settings can be saved inside of an existing csv file.
     * Source that gave the idea to initialize the file writer in the try-catch parentheses, which made the code work:  Baeldung, n.d.
     * Source to use file chooser: Redko, n.d.
     * Source to use directory chooser: Oracle, n.d.
     * @param simulation The simulation being used, which is necessary to get the scaling
     * @throws IOException
     * @throws FileNotFoundException
     * @throws CsvException
     */
    private void handleSaveItm(CellularLogic simulation) throws IOException, FileNotFoundException, CsvException {
        System.out.println("Save button clicked");
        //Ask user if he already has a file in which he wants to save the settings, or if we create new file for him in give directory chosen by him
        primaryStage.setAlwaysOnTop(false);
        boolean createNewFile = askUserSaveSettingsDialog("Choose an existing file or create new one?");
        primaryStage.setAlwaysOnTop(true);
        File file = new File("");
        Stage stage  = new Stage();
        //User already has a file
        if(createNewFile==false){
            // create file chooser
            FileChooser f = new FileChooser();
            stage.setAlwaysOnTop(true);
            this.primaryStage.setAlwaysOnTop(false);
            file = f.showOpenDialog(stage);
            boolean fileValid = verifyFileCSV(file);
            if(fileValid==false){
                handleSaveItm(simulation);
                return;
            }
            showAlertInfo("File has been accepted.");
            this.primaryStage.setAlwaysOnTop(true);
        }
        // User does not have an existing file, and wants to create one
        else{
            // 1- Choose the directory in which the user wants to save the settings
            DirectoryChooser dc = new DirectoryChooser();
            primaryStage.setAlwaysOnTop(false);
            stage.setAlwaysOnTop(true);
            dc.setInitialDirectory(dc.showDialog(stage));
            stage.setAlwaysOnTop(false);
            primaryStage.setAlwaysOnTop(true);
            try {
                dc.getInitialDirectory().createNewFile();
                System.out.println(dc.getInitialDirectory());
                // Make a dialog appear for the user to choose a name for the file
                this.setNameFile(chooseNameDialog());
                // Create a file with the name
                file = new File(dc.getInitialDirectory()+"\\"+this.getNameFile()+".csv");
            } catch (IOException ex) {
                System.out.println("Error in the program: "+ex.toString());
       }
        }
        try(FileWriter writer = new FileWriter(file.getAbsolutePath())){
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
            //Write the properties of the canvas and the stage
            writer.write(Double.toString(primaryStage.getWidth())+",");
            writer.write(Double.toString(primaryStage.getHeight())+",");
            //Write the frame limit
            writer.write(txtBoxRippleLimit.getText()+",");
            writer.write(txtBoxConwayLimit.getText()+",");
            writer.write(txtBoxRPCLimit.getText()+",");
            writer.write(txtBoxSLALimit.getText()+",");
            writer.write(txtBoxDLALimit.getText()+",");
            writer.write(txtBoxBrainFrameLimit.getText()+",");
            // Write value of sliders for Forest Fire
            writer.write(Double.toString(fireSldr.getValue())+",");
            writer.write(Double.toString(treeSldr.getValue())+",");
            //Write Amplitude
            writer.write(Double.toString(amplitudeSldr.getValue())+",");
            //Write points
            for(Iterator<Point> points = pointList.iterator(); points.hasNext();){
                Point currentPoint = points.next();
                if(!points.hasNext()){
                    writer.write(Integer.toString(currentPoint.getX()*simulation.getScaling())+","+Integer.toString(currentPoint.getY()*simulation.getScaling()));
                    break;
                }
                writer.write(Integer.toString(currentPoint.getX()*simulation.getScaling())+","+Integer.toString(currentPoint.getY()*simulation.getScaling())+",");
            }
        }catch(Exception e){
            System.out.println(e.toString());
        }
    }
    /**
     * This method creates a dialog that is responsible of letting the user choose a name for the file he wants to create.
     * It returns the name of the csv file, and is meant to be used in the save settings method, if the user wants to create a new csv file.
     * It creates a new stage which will be used as a window to contain the text field used to write the name of the file.
     * @return nameFile, which corresponds to the name of the file
     */
    public String chooseNameDialog(){
        Stage stage = new Stage();
        VBox root = new VBox();
        Label nameLbl = new Label("Please write the name of your file");
        TextField nameTxtFld = new TextField("Name");
        nameTxtFld.setLayoutX(0);
        Button OkBtn = new Button("OK");
        OkBtn.setOnAction((event)->{
            nameFile = nameTxtFld.getText();
            stage.close();
        });
        root.getChildren().addAll(nameLbl,nameTxtFld, OkBtn);
        stage.setAlwaysOnTop(true);
        Scene scene = new Scene(root, 300, 300);
        stage.setScene(scene);
        stage.showAndWait();
        return nameFile;
    }
    /**
     * This method sets the height and the width of the stage.
     * @param x The width of the stage
     * @param y The height of the stage
     */
    public void setStageDimensions(double x, double y){
        primaryStage.setWidth(x);
        primaryStage.setHeight(y);
    }
    /**
     * This method loads the points that are contained in the settings of a csv file
     * The method cannot be used by itself. It needs to be used in a method that will set the value of the settings[] attribute, corresponding
     * to the settings that were loaded.
     * From the settings[] attributes, it locates the indexes of the cooredinates, using the loop, and sets them on the canvas.
     */
    private void loadPointsUtil() {
        int x,y;
            for(int counterIndex = 0; counterIndex<((settings.length-15)/2); counterIndex++){
                x=0;
                y=0;
                for(int counterCoordinates=0; counterCoordinates<2; counterCoordinates++){
                    if(counterCoordinates==0)
                        x=Integer.parseInt(settings[(counterIndex*2)+15]);
                    else
                        y=Integer.parseInt(settings[(counterIndex*2)+16]);
                }
                
                newPoint((double)x, (double)y, simulation);
          }
    }
    
    /**
     * This method loads the settings from a csv file chosen by the user.
     * The method uses a file chooser which allows the user to choose a file from his computer.
     * Source to use file chooser: Redko, n.d.
     * The file needs to be csv, therefore, exception handling is used to verify the validity of the file chosen by the user.
     * The indexes, prosition, of the data in the csv file is that which is used to determine what piece of data corresponds to what setting specifically
     * For example, the first piece of data, the first value in the csv file, is always going to be the value of the damping, because it is saved that way, in
     * the save settings feature.
     * If not, then the file is invalid.
     * In that case, the user is asked to try again, until the data contained inside the csv file is valid.
     * @param simulation The simulation whose data will be adjusted based on the information retrieved from the csv file
     * @throws FileNotFoundException
     */
    private void handleLoadItm(CellularLogic simulation) throws FileNotFoundException {
        animationRunning=false;
        
        System.out.println("Load button clicked");
        try{
            FileChooser f = new FileChooser();
        Stage stage  = new Stage();
        stage.setAlwaysOnTop(true);
        this.primaryStage.setAlwaysOnTop(false);
        File file;
        file = f.showOpenDialog(stage);
        boolean isValid = verifyFileCSV(file);
        if(isValid==false){
            handleLoadItm(simulation);
            return;
        }
        CSVReader reader = new CSVReader(new FileReader(file.getPath()));
        settings = reader.readAll().get(0);
        if(!verifyFileSettings(settings)){
            handleLoadItm(simulation);
            return;
        }
        this.primaryStage.setAlwaysOnTop(true);
            // Set height and width
            setStageDimensions(Double.parseDouble(settings[4]),Double.parseDouble(settings[5]));
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
            // Set Limits
            txtBoxRippleLimit.setText(settings[6]);
            txtBoxConwayLimit.setText(settings[7]);
            txtBoxRPCLimit.setText(settings[8]);
            txtBoxSLALimit.setText(settings[9]);
            txtBoxDLALimit.setText(settings[10]);
            txtBoxBrainFrameLimit.setText(settings[11]);
            //Set Sliders for Forest Fire
            fireSldr.setValue(Double.parseDouble(settings[12]));
            treeSldr.setValue(Double.parseDouble(settings[13]));
            //Set amplitude
            amplitudeSldr.setValue(Double.parseDouble(settings[14]));
            //Set points, pause because the canvas needs to update its size
            pause.play();
            
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
        simulationsList[5].setHasInitialized(false);
        this.simulation = changeSim(simTypeChoice.getValue().toString(), simulationsList, simulation);
        this.animation.stop();
        this.animation = newAnimationTimer();
        this.animation.setDelayMillis(delayMillis);
        for (int i=1;i<this.simulationsList.length;i++) {
            simulationsList[i].setScaling(scaling);
            simulationsList[i].clearScreen();
            pointList.clear();
            animation.stop();
            btnPlay.setDisable(false);
            simulationsList[i].setHasInitialized(false);
            btnPause.setDisable(true);
            btnReset.setDisable(true);
            animationRunning = false;
            if (simulationsList[i].getRenderFlag()){
                simulationsList[i].setRenderFlag(false);

            }
            simulationsList[i].setFrameNumber(0);
        }
    }
    /**
     * This method shows an alert to the user.
     * It is used in the save and load settings to show error.
     * @param message This corresponds to a String which is the message that will be shown to the user
     */
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
    /**
     * This method shows an alert to the user.
     * The alert corresponds to an information, informing the user that the file has been validated
     * It is used in the save settings to show confirmation.
     * @param message This corresponds to a String which is the message that will be shown to the user
     */
    private void showAlertInfo(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Validation");
        alert.setContentText("File Validation");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
    /**
     * This methd is used to ask the user of he wants to create a new file or not, for the saving of the settings.
     * It contains two buttons, "Create New File" and "Choose Existing File", which, upon click by the user, returns a boolean which corresponds
     * to whether or not a file needs to be created.
     * @param message The message that will be shown to the user
     * @return boolean which corresponds to whether or not a file needs to be created.
     */
    private boolean askUserSaveSettingsDialog(String message){
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("File Option");
        alert.setContentText(message);
        ButtonType createBtnType = new ButtonType("Create New File");
        ButtonType chooseBtnType = new ButtonType("Choose Existing File");
        alert.getButtonTypes().addAll(createBtnType, chooseBtnType);
        alert.showAndWait();
        if(alert.getResult()==createBtnType){
            return true;
        }
        else
            return false;
    }
    /**
     * Verifies of the file chosen by the user is valid.
     * If not valid, the method shows an alert, displaying what is wrong with the file
     * @param file The file that needs to be verified
     * @return boolean of whether or not the file is valid
     * @throws FileNotFoundException
     * @throws IOException
     * @throws CsvException
     */
    private boolean verifyFileCSV(File file) throws FileNotFoundException, IOException, CsvException{
        boolean isValid =true; //Assume that the file is valid, then look for mistakes
        //Verify .csv
        if(!".csv".equals(file.getPath().substring(file.getPath().length()-4, file.getPath().length()))){
            showAlert("The file is not a csv file. Please try again.");
            isValid=false;
        }
        return isValid;
    }
    private boolean verifyFileSettings(String[] info) throws IOException, CsvException{
        boolean isValid=true;
        if(info.length<15){
            showAlert("The file does not contain the minimum amount of information required to load a simulation.");
            isValid = false;
        }
            
        try{
            double dampVerification = Double.parseDouble(info[0]);
            // Make sure that damping is between the right numerical bounds
            // o.oo1 to 0.150
            if(dampVerification>0.150||dampVerification<0.001)
            {
                showAlert("The first value is incorrect. The value of the damping should be between 0.001 and 0.150");
                isValid = false;
            }
        }catch(Exception e){
            showAlert("The first value should be a number corresponding to the value of the damping. However, it does not seem like a numerical value.");
            isValid = false;
        }
        //Check scaling
        try{
            int scaleVerification = Integer.parseInt(info[1]);
            if(scaleVerification>8||scaleVerification<1){
                showAlert("The scaling should be a number between 1 and 8. However, it seems to be out of bounds");
                isValid = false;
            }
        }catch(Exception e){
            showAlert("The second value should be an integer corresponding to the value of the scaling. However, it does seem like a number.");
            isValid = false;
        }
        //Check simulation type
        String[] simulationTypes = {"Simple Ripple", "Conway's Game of Life", "Rock-Paper-Scissors", "Brian's Brain", "Forest Fire","Diffusion Limited Aggregation"};
        boolean isOneOfTypes = false;
        for(String element:simulationTypes)
            if(element.equals(info[2]))
                isOneOfTypes = true;
        if(isOneOfTypes==false){
            showAlert("The third value, corresponding to the simulation type is invalid. Please try again.");
            isValid = false;
        }
        //Check speed: Between 1 and 500
        try{
            double speedVerification = Double.parseDouble(info[3]);
            if(speedVerification>500||speedVerification<1){
                showAlert("The fourth value, corresponding to the speed of the simulation should be a value between 1 and 500. However, the value in the file seems to be out of bound. Please try again.");
                isValid=false;
            }
        }catch(Exception e){
            showAlert("The fourth value inside the file, corresponding to the speed of the simulation is not a number. Please try again, using a valid file.");
            isValid = false;
        }
        // Verify stage dimensions
        // Width
        try{
            double widthStage = Double.parseDouble(info[4]);
            if(widthStage<1){
                showAlert("The value at the "+(5)+"th position should be a number corresponding to the width of the window. It should be bigger than 0. However, it is inferior to 1.");
            }
        }catch(Exception e){
            showAlert("The value at the "+(5)+"th position should be a number corresponding to the width of the window. However, it does not look like a number.");
            isValid=false;
        }
        //Height
        try{
            double heightStage = Double.parseDouble(info[5]);
            if(heightStage<1){
                showAlert("The value at the "+(6)+"th position should be a number corresponding to the height of the window. It should be bigger than 0. However, it is inferior to 1.");
            }
        }catch(Exception e){
            showAlert("The value at the "+(6)+"th position should be a number corresponding to the height of the window. However, it does not look like a number.");
            isValid=false;
        }
        // Don't need to check for frame limits, because if something illegal is entered, then it just automatically goes to 'max'
        //Check how many points are in the file
        int numOfCoordinates = (info.length-15);
        if(numOfCoordinates%2==1){
            showAlert("A coordinate is missing. Please try again, using a valid file.");
            isValid=false;
        }
        return isValid;
    }
    /**
     * This method creates a help dialog for the user.
     * @param guideItm type MenuItm, which will be set disabled as long as the help dialog is being used
     */
    private void handleGuideItm(MenuItem guideItm) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/helpGuide.fxml"));
        loader.setController(new FXMLHelpGuideController());
        Pane root = loader.load();
        guideItm.setDisable(true);
        
        Scene scene = new Scene(root, 600,400);
        Stage guideDialog = new Stage();
        guideDialog.setTitle("Help Dialog");
        guideDialog.setScene(scene);
        guideDialog.setAlwaysOnTop(true);
        guideDialog.sizeToScene();
        guideDialog.setResizable(false);
        guideDialog.setOnCloseRequest(event -> {
        guideItm.setDisable(false);
        });
        guideDialog.showAndWait();
    }
}
