package edu.vanier.waveSim.controllers;

import edu.vanier.waveSim.models.CellularAnimTimer;
import edu.vanier.waveSim.models.CellularLogic;
import javafx.fxml.FXML;
import edu.vanier.waveSim.deprecated.Grid;
import edu.vanier.waveSim.deprecated.GridPixel;
import edu.vanier.waveSim.deprecated.SimLogic;
import edu.vanier.waveSim.models.SimLogicWave1;
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

//    get canvas from FXML
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
        
        // bind text property to the slider value
        lblDamping.textProperty().bind(Bindings.format("%.3f",sldrDamping.valueProperty()));
        
        
        
    }
    
    private void handleTestBtn(SimLogicWave1 simulation, CellularAnimTimer animation){
        System.out.println("Test");
        
        System.out.println("STARTING THE SIMULATION");
        
        System.out.println(simulation.getDamping());
        
        int choice = (int) scaleChoice.getValue();
        
        simulation.setScaling(choice);
        
        simulation.setPoint(50, 50);
        
        animation.start();
 
    }
    
    private void handleStopBtn(CellularAnimTimer animation) {
        System.out.println("Stop button pressed");
        animation.stop();
        System.out.println("Animation stopped");
    }
    
    
    public void handleStartBtn(CellularAnimTimer animation) {
        System.out.println("Restarting animation button pressed");
        animation.start();
        System.out.println("Restarted animation");
    }
    
    /**
     * William's version of set color the way it makes sense for him.
     * Is probably more efficient because it does not create new grids and does not return anything
     * @param canvas The canvas object from FXML to write to
     * @param xPos The x position from top right in pixels
     * @param yPos The y position from top right in pixels
     * @param color The color of the pixel using javaFX Color object
     */
    public static void colorCellWilliamVersion(Canvas canvas, int xPos, int yPos, Color color){
        GraphicsContext Graphics = canvas.getGraphicsContext2D();
        Graphics.setFill(color);
        Graphics.getPixelWriter().setColor(xPos, yPos, color);
    }
    
    //Cannot use PixelWriter without a surface to write on => Use fxml layout as that surface
    /**
     * This method colors individual cells of the canvas contained in a fxml file.The canvas is assumed to be in the center.
     * @param root root pane to color cells of
     * @param x position x in pixels
     * @param y position y in pixels from top down
     * @return BorderPane
     */
    public BorderPane colorCell(Grid grid, BorderPane root, int x, int y){
        // Has height of 400 and a width of 500
        GridPixel pixel = new GridPixel();
        pixel.setColor(Color.CORAL);
        pixel.setOn(true);
        pixel.setSize(10);
        grid.setPixel(x, y , pixel);
        //The center is the canvas. Just need to cast it.
        Canvas canvas = (Canvas) root.getCenter();
        System.out.println(canvas.getHeight()+" "+canvas.getWidth());
        for(int i = 0; i<grid.getCanvas().length; i++){
            for(int j=0; j<grid.getCanvas()[0].length; j++){
                if(grid.getCanvas()[i][j].isOn()){
                    //Paint a square of 10*10 in the canvas
                    // Source to get the method of getGraphicsContext2D() tp paint in the canvas:
                    // https://stackoverflow.com/questions/70085482/drawing-with-transparency-on-javafx-canvas-using-pixelwriter
                    canvas.getGraphicsContext2D().fillRect(i*10, j*10, 10, 10);
                }
            }
        }
        // We need to modify the canvas, then re-set it to the center
        root.setCenter(canvas);
        //-- 2) Create and set the scene to the stage.
            return root;
        
    }

}
