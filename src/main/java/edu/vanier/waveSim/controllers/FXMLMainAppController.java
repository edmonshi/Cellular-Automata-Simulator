                                                                    package edu.vanier.waveSim.controllers;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import edu.vanier.waveSim.models.CellularAnimTimer;
import edu.vanier.waveSim.models.CellularLogic;
import javafx.fxml.FXML;
import edu.vanier.waveSim.models.ConwayGameOfLifeLogic;
import edu.vanier.waveSim.models.ForestFire;
import edu.vanier.waveSim.models.SimLogicWave;
import edu.vanier.waveSim.models.SimRPC;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import javafx.animation.PauseTransition;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
 *
 * @author TODO
 */
public class FXMLMainAppController{

    private final static Logger logger = LoggerFactory.getLogger(FXMLMainAppController.class);
    
    private Stage primaryStage;
    
    private boolean animationRunning = false;
    
    private final Transition pause = new PauseTransition(Duration.millis(5));
    int scale = 1;
    int delayMillis = 1;
    int sceneHeight;
    int sceneWidth;
    String[] settings;

    public String[] getSettings() {
        return settings;
    }

    public void setSettings(String[] settings) {
        this.settings = settings;
    }
    
    public FXMLMainAppController(Stage primaryStage) {
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

    public void stopAnimation() {
        animation.stop();
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
    CellularLogic[] simulationsList = new CellularLogic[5];
    CellularLogic simulation;
    CellularAnimTimer animation;
    
    /**
     * Utility for creating new refreshed animation timers
     */
    private CellularAnimTimer newAnimationTimer() {
        return new CellularAnimTimer(simulation, this);
    }
    
//    get elements from FXML
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
    @FXML private Button btnSaveRender;
    @FXML private Button btnPauseRender;
    @FXML private Button btnResetRender;
    @FXML private Pane SimCanvasPane;
    @FXML private Label lblWi;
    @FXML private Label lblHi;
    @FXML private TabPane SimTabPane;
    @FXML private MenuItem guideItm;
    @FXML private TextField txtBoxRippleLimit;
    @FXML private TextField txtBoxConwayLimit;
    @FXML private TextField txtBoxRPCLimit;
    @FXML
    private Label amplitudeLbl;
    @FXML
    private Slider amplitudeSldr;
    
    // list of choices for scale factor, 1 and then multiples of 2 (for math reasons)
    ObservableList<Integer> scaleChoiceItems = FXCollections.observableArrayList(1,2,4,6,8);
    
    //list of simulation types, simple wave, etc
    ObservableList<String> simTypeChoiceItems = FXCollections.observableArrayList("Simple Ripple", "Conway's Game of Life", "Rock-Paper-Scissors", "Langton's Ant");
    
    
    
    
    
    /**
     * Initialize the FXML file of the simulation, assignee events to the controllers and 
     * import the simulation the the FXML file.
     */
    @FXML
    public void initialize() {
        // create simulation objects

        SimLogicWave WaveSim = new SimLogicWave(SimCanvas, (int) SimCanvas.getWidth(), (int) SimCanvas.getHeight(), 1);
        ConwayGameOfLifeLogic Conway = new ConwayGameOfLifeLogic(SimCanvas, (int) SimCanvas.getWidth(), (int) SimCanvas.getHeight(), 1);
        SimRPC RPC = new SimRPC(SimCanvas, (int) SimCanvas.getWidth(), (int) SimCanvas.getHeight(), 1);
        ForestFire SLA = new ForestFire(SimCanvas, (int) SimCanvas.getWidth(), (int) SimCanvas.getHeight(), 1);
        // initialize default simulation
        simulation = WaveSim;
        
        simulationsList[0] = simulation;
        simulationsList[1] = WaveSim;
        simulationsList[2] = Conway;
        simulationsList[3] = RPC;
        simulationsList[4] = SLA;
        
        
        
        // initialize default animation object
        animation = newAnimationTimer();
        simulation.clearScreen();
        
        pointList = new HashSet<>();
        
        // set ChoiceBox elements
        scaleChoice.setValue(1);
        scaleChoice.setItems(scaleChoiceItems);
        
        simTypeChoice.setValue("Simple Ripple");
        simTypeChoice.setItems(simTypeChoiceItems);
        
        // set default btn disabled state
        btnPause.setDisable(true);
        btnReset.setDisable(true);
        
        // waiting util for load points
        pause.setOnFinished((event) -> {
            loadPointsUtil();
        });
        
        // https://stackoverflow.com/questions/37678704/how-to-embed-javafx-canvas-into-borderpane
//        SimCanvas.widthProperty().bind(SimCanvasPane.widthProperty());
//        SimCanvas.heightProperty().bind(SimCanvasPane.heightProperty());
  
        SimTabPane.heightProperty().addListener((observable) -> {
            setHeight(SimTabPane.heightProperty().getValue().intValue(), simulation, animation, lblHi);
        });
        SimTabPane.widthProperty().addListener((observable) -> {
            setWidth(SimTabPane.widthProperty().getValue().intValue(), simulation, animation, lblWi);
        });
        
        itmRenderStart.setOnAction((event) -> {
            handleRenderStart();
        });
        
        itmStopRender.setOnAction((event) -> {
            handleRenderStop();
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
        
        itmSave.setOnAction((event)->{
            try {
                try {
                    handleSaveItm(simulation);
                } catch (FileNotFoundException | CsvException ex) {
                    java.util.logging.Logger.getLogger(FXMLMainAppController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
        });
        
        itmLoad.setOnAction((event)->{
            try {
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
        guideItm.setOnAction((event)->{
            try {
                handleGuideItm();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(FXMLMainAppController.class.getName()).log(Level.SEVERE, null, ex);
            }
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
        SimCanvas.setWidth(width);
        ResetScreenAndAnim(simulation, animation, scale);
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
        SimCanvas.setHeight(realHeight);
        ResetScreenAndAnim(simulation, animation, scale);
        lblHeight.setText("Height: "+realHeight);
    }
    
    /**TODO Documentation -> switched the active simulation
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
                case "Langton's Ant"->{
                    simulation = simulations[4];
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
            if (simulation != simulationsList[3]) {
                // set the point in the simulation
                simulation.setPoint(xFloor, yFloor);
                // add the point to the canvas as Color.RED
                // the scaling must be adjusted because colorCell uses array coorrdinates, not canvas coordinates
                simulation.colorCell(xFloorScaled, yFloorScaled, Color.RED);
            }
        }else if (animationRunning == false && pointList.contains(clickPoint)){
            pointList.remove(clickPoint);
            // if the point was removed from the array, remove from canvas.
            if (simulation.removePoint(xFloor, yFloor) && simulation != simulationsList[3]) {
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
        handlePlayBtn(simulation, animation);
    }
    
    /**
     * TODO
     */
    private void handleRenderStop() {
        System.out.println("Stop Render");
        for (CellularLogic sim: simulationsList) {
            sim.setRenderFlag(false);
        }
        ResetScreenAndAnim(simulation, animation, scale);
    }
    private int frameLim;

    public int getFrameLim() {
        return frameLim;
    }

    public void setFrameLim(int frameLim) {
        this.frameLim = frameLim;
    }
    
    /**
     * Validate integer from TextField as valid int
     * @param input the input String from the TextField
     * @param box the instance of the TextField
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
     * TODO use the simulation parameter to check for valid simulation or something
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
    String nameFile;

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }
    
    /**
     * This method saves the settings of a simulation in a CSV File.
     * The file can either be created by the method inside of a specified directory by the user, or the settings can be saved inside of an existing csv file.
     * Source used as an example to learn how to use PrintWriter to write in a Csv File: 
     * 1) - Few examples: https://stackoverflow.com/questions/68218102/how-can-i-write-data-to-csv-in-chunks-via-printwriter-in-java
     * 2) - Putting conditions in paratheses of the try-catch: https://www.baeldung.com/java-csv
     * 3) - Using .write() method: https://www.javatpoint.com/java-printwriter-write-method#:~:text=Java%20PrintWriter%20write(char%20%5B%5D%20buf%2C%20int%20off%2C%20int,of%20an%20array%20of%20characters.
     * Sources To use file chooser and directory chooser:
     * 1) https://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm
     * 2) https://docs.oracle.com/javase/8/javafx/api/javafx/stage/DirectoryChooser.html
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
                writer.write(Integer.toString(currentPoint.getX()*simulation.getScaling())+","+Integer.toString(currentPoint.getY()*simulation.getScaling())+",");
            }
            //Write the properties of the canvas and the stage
            writer.write(Double.toString(simulation.getOperatingCanvas().getWidth())+",");
            writer.write(Double.toString(simulation.getOperatingCanvas().getHeight())+",");
            writer.write(Double.toString(primaryStage.getWidth())+",");
            writer.write(Double.toString(primaryStage.getHeight())+",");
            //Write the frame limit
            writer.write(Integer.toString(this.getFrameLim()));
            writer.write("\n");
        }
    }
    /**
     * This method creates a dialog that is responsible of letting the user choose a name for the file he wants to create.
     * It returns the name of the csv file, and is meant to be used in the save settings method, of the user wants to create a new csv file.
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
     */
    public void setStageDimensions(double x, double y){
        primaryStage.setWidth(x);
        primaryStage.setHeight(y);
    }
    
    File fileLoad;

    public File getFileLoad() {
        return fileLoad;
    }

    public void setFileLoad(File fileLoad) {
        this.fileLoad = fileLoad;
    }
    
    private void loadPointsUtil() {
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
                
                newPoint((double)x, (double)y, simulation);
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
        File file;
        file = f.showOpenDialog(stage);
        boolean isValid = verifyFileCSV(file);
        if(isValid==false){
            handleLoadItm(simulation);
            return;
        }
        CSVReader reader = new CSVReader(new FileReader(file.getPath()));
        int saveOption = 0;
        settings = reader.readAll().get(saveOption);
        verifyFileSettings(settings);
        this.primaryStage.setAlwaysOnTop(true);
        
            // Set height and width
            setStageDimensions(Double.parseDouble(settings[settings.length-3]),Double.parseDouble(settings[settings.length-2]));
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
        for (int i=0;i<this.simulationsList.length;i++) {}
        simulation.setScaling(scaling);
        simulation.clearScreen();
        pointList.clear();
        animation.stop();
        btnPlay.setDisable(false);
        btnPause.setDisable(true);
        btnReset.setDisable(true);
        animationRunning = false;
        if (simulation.getRenderFlag()) {
            System.out.println("Stop Render");
            for (CellularLogic sim: simulationsList) {
                sim.setRenderFlag(false);
            }
        }
        simulation.setFrameNumber(0);
        System.out.println("Stopped Animation");
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
    private void showAlertInfo(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Validation");
        alert.setContentText("File Validation");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
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
        if(info.length<8){
            showAlert("The file does not contain enough information for the load settings to work. Please use another one, "
                    + "containing at least the damping, the scaling, the simulation type, the speed, zero or more "
                    + "points coordinates, the width and height of the canvas and the window.");
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
        String[] simulationTypes = {"Simple Ripple", "Conway's Game of Life", "Rock-Paper-Scissors"};
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
        //Check how many points are in the file
        int numOfCoordinates = (info.length-9);
        if(numOfCoordinates%2==1){
            showAlert("A coordinate is missing. Please try again, using a valid file.");
            isValid=false;
        }
        // Verify canvas dimensions
        // Width
        double widthCanvas=0;
        double heightCanvas=0;
        try{
            widthCanvas = Double.parseDouble(info[info.length-5]);
            if(widthCanvas<1){
                showAlert("The value at the "+(info.length-4)+"th position should be a number corresponding to the width of the canvas. It should be bigger than 0. However, it is inferior to 1.");
            }
        }catch(Exception e){
            showAlert("The value at the "+(info.length-4)+"th position should be a number corresponding to the width of the canvas. However, it does not look like a number.");
            isValid=false;
        }
        //Height
        try{
            heightCanvas = Double.parseDouble(info[info.length-4]);
            if(heightCanvas<1){
                showAlert("The value at the "+(info.length-3)+"th position should be a number corresponding to the height of the canvas. It should be bigger than 0. However, it is inferior to 1.");
            }
        }catch(Exception e){
            showAlert("The value at the "+(info.length-3)+"th position should be a number corresponding to the height of the canvas. However, it does not look like a number.");
            isValid=false;
        }
        // Go through every point to check if they are valid
        if(numOfCoordinates!=0){
            for(int counter=0; counter<numOfCoordinates; counter++){
                try{
                    int coordinate = Integer.parseInt(info[4+counter]);
                    // Verify of the coordinates are within the proper bounds
                    //x value
                    if(counter%2==0){
                        if((double)coordinate>(widthCanvas)||coordinate<0){
                            showAlert("The x-coordinate at the "+(counter+5)+"th position is out of bounds. It should be between 0 and "+widthCanvas);
                            isValid = false;
                        }
                    }
                    //y value
                    if(counter%2==1){
                        if((double)coordinate>(heightCanvas)||coordinate<0){
                            showAlert("The y-coordinate at the "+(counter+5)+"th position is out of bounds. It should be between 0 and "+heightCanvas);
                            isValid = false;
                        }
                    }
                }catch(Exception e){
                    showAlert("The value at the "+(5+counter)+"th position should be an integer corresponding to a coordinate inside the canvas. However, it does not look like an integer. Please try again, using a valid file.");
                    isValid=false;
                }
            }
        }
        
        // Verify stage dimensions
        // Width
        try{
            double widthStage = Double.parseDouble(info[info.length-3]);
            if(widthStage<1){
                showAlert("The value at the "+(info.length-2)+"th position should be a number corresponding to the width of the window. It should be bigger than 0. However, it is inferior to 1.");
            }
        }catch(Exception e){
            showAlert("The value at the "+(info.length-2)+"th position should be a number corresponding to the width of the window. However, it does not look like a number.");
            isValid=false;
        }
        //Height
        try{
            double heightStage = Double.parseDouble(info[info.length-2]);
            if(heightStage<1){
                showAlert("The value at the "+(info.length-1)+"th position should be a number corresponding to the height of the window. It should be bigger than 0. However, it is inferior to 1.");
            }
        }catch(Exception e){
            showAlert("The value at the "+(info.length-1)+"th position should be a number corresponding to the height of the window. However, it does not look like a number.");
            isValid=false;
        }
        //Verification for the frame limit here
        try{
            int frameLim = Integer.parseInt(info[info.length-1]);
            if(frameLim<0){
                showAlert("The value of the frame limit is invalid, because it is negative. Please try again with a file that has a positive value for the frame limit.");
                System.out.println("Frame limit ="+info[info.length-1]);
                isValid=false;
            }
        }catch(Exception e){
            showAlert("The value corresponding to the frame limit is invalid. It does not appear to be a number. Please try again with a valid file.");
            System.out.println("Frame limit ="+info[info.length-1]);
            isValid = false;
        }
        return isValid;
    }
    /**
     * This method creates a help dialog for the user.
     */
    private void handleGuideItm() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/helpGuide.fxml"));
        loader.setController(new FXMLHelpGuideController());
        Pane root = loader.load();
        
        Scene scene = new Scene(root, 400,400);
        Stage guideDialog = new Stage();
        guideDialog.setTitle("Help Dialog");
        guideDialog.setScene(scene);
        guideDialog.setAlwaysOnTop(true);
        guideDialog.sizeToScene();
        guideDialog.showAndWait();
    }
}
