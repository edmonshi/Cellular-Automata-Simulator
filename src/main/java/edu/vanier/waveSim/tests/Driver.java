package edu.vanier.waveSim.tests;

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
 * TODO document this
 */
public class Driver extends Application {

    private final static Logger logger = LoggerFactory.getLogger(Driver.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Bootstrapping the application...");
            //-- 1) Load the scene graph from the specified FXML file and 
            // associate it with its FXML controller.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/basicCanvasTest.fxml"));

            loader.setController(new SimDriverController());
            BorderPane root = loader.load();
            
            // Creating grid
            Grid grid = new Grid();
            // Has height of 400 and a width of 500
            GridPixel pixel = new GridPixel();
            pixel.setColor(Color.CORAL);
            pixel.setOn(true);
            pixel.setSize(10);
            grid.setPixel(3,3,pixel);
            //The center is the canvas. Just need to cast it.
            Canvas canvas = (Canvas) root.getCenter();
            System.out.println(canvas.getHeight()+" "+canvas.getWidth());
            for(int i = 0; i<grid.getCanvas().length; i++){
                for(int j=0; j<grid.getCanvas()[0].length; j++){
                    if(grid.getCanvas()[i][j].isOn()){
                        //Paint a square of 10*10 in the canvas
                        canvas.getGraphicsContext2D().fillRect(i*10, j*10, 10, 10);
                    }
                }
            }
            for(int i=0; i<25; i++){
                canvas.getGraphicsContext2D().getPixelWriter().setColor(i, i, Color.CYAN);
                // Source to get the method of getGraphicsContext2D() tp paint in the canvas:
                // https://stackoverflow.com/questions/70085482/drawing-with-transparency-on-javafx-canvas-using-pixelwriter
                
            }
            // We need to modify the canvas, then re-set it to the center
            root.setCenter(canvas);
            //-- 2) Create and set the scene to the stage.
            Scene scene = new Scene(root, 500, 300);
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
            primaryStage.setAlwaysOnTop(true);
            primaryStage.show();
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

