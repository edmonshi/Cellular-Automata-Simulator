package edu.vanier.waveSim.controllers;

import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.vanier.waveSim.controllers.MainAppController;
import edu.vanier.waveSim.controllers.SimDriverController;
import edu.vanier.waveSim.models.Grid;
import edu.vanier.waveSim.models.GridPixel;
import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class of the MainApp's UI.
 *
 * @author frostybee
 */
public class SimDriverController {

    private final static Logger logger = LoggerFactory.getLogger(MainAppController.class);

    @FXML
    public void initialize() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/basicCanvasTest.fxml"));
    }
    //Cannot use PixelWriter without a surface to write on => Use fxml layout as that surface
    /**
     * This method colors individual cells of the canvas contained in a fxml file.
     * The canvas is assumed to be in the center.
     */
    public BorderPane colorCell(BorderPane root, int x, int y){
        // Creating grid
        Grid grid = new Grid();
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
