package edu.vanier.waveSim.controllers;

import edu.vanier.waveSim.models.AnimTimer;
import javafx.fxml.FXML;
import edu.vanier.waveSim.models.Grid;
import edu.vanier.waveSim.models.GridPixel;
import edu.vanier.waveSim.models.SimLogic;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
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

    private final static Logger logger = LoggerFactory.getLogger(MainAppController.class);

//    get canvas from FXML
    @FXML
    Canvas SimCanvas;
    @FXML
    Button btnTest;
    @FXML
    Button btnStop;
    @FXML
    Button btnStart;
    
    @FXML
    public void initialize() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/basicCanvasTest.fxml"));
        // create simulation object
        SimLogic simulation = new SimLogic(SimCanvas, (int) SimCanvas.getWidth(), (int) SimCanvas.getHeight());
        AnimTimer animation = new AnimTimer(simulation);
        
        btnTest.setOnAction((event) -> {
            handleTestBtn(simulation, animation);
        });
         
        btnStop.setOnAction((event) -> {
            handleStopBtn(animation);
        });
        
        btnStart.setOnAction((event) -> {
            handleStartBtn(animation);
        });
        
    }
    
    private void handleTestBtn(SimLogic simulation, AnimTimer animation){
        System.out.println("Test");
        
        simStartButton(simulation, animation);
        
        
        // TODO make sure this is not commented in final build
//        // draw a line
//        for(int i =0; i< SimCanvas.getHeight()-1; i++){
//        colorCellWilliamVersion(SimCanvas, 30, i, Color.BLACK);
//        }
    }
    
    private void handleStopBtn(AnimTimer animation) {
        System.out.println("Stop button pressed");
        animation.stop();
        System.out.println("Animation stopped");
    }
    
    /**
     * Simulation starting method.
     * 
     * @param simulation SimLogic simulation object
     * @param animation AnimTimer animation object to control timing
     */
    public void simStartButton(SimLogic simulation, AnimTimer animation){
        System.out.println("STARTING THE SIMULATION");
        
        simulation.setPoint(100, 100);
        
        simulation.setDamping(1);
        
        animation.start();
        
        System.out.println("Simulation end");
    }
    
    public void handleStartBtn(AnimTimer animation) {
        System.out.println("Starting animation");
        animation.start();
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
