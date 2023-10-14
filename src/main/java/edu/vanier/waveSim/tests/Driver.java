package edu.vanier.waveSim.tests;

import edu.vanier.waveSim.controllers.SimDriverController;
import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
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
            
            //-- 2) Create and set the scene to the stage.
            
            
            
            Scene scene = new Scene(root, 662, 400);
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

