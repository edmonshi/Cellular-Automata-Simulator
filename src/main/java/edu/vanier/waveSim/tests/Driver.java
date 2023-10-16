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
 * The Main class of the application.
 * It extends the Application class and is responsible for initializing the JavaFX application, 
 * setting up the primary stage, and launching the application.
 * 
 * @author TODO
 */
public class Driver extends Application {

    private final static Logger logger = LoggerFactory.getLogger(Driver.class);

    /**
     * Override the abstract method start(Stage primaryStage) of Application.
     * The main entry point for the JavaFX application.
     * 
     * @param primaryStage the primary stage for this application, onto which the application scene can be set. 
     * @exception Exception - if something goes wrong
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Bootstrapping the application...");
            //-- 1) Load the scene graph from the specified FXML file and 
            // associate it with its FXML controller.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainAppTest.fxml"));

            loader.setController(new SimDriverController());
            BorderPane root = loader.load();
            
            //-- 2) Create and set the scene to the stage.
            
            
            
            Scene scene = new Scene(root, 700, 526);
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
            primaryStage.setAlwaysOnTop(true);
            primaryStage.show();
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    
    /**
     * Main method of the program.
     * Execute the launch method of the Application class.
     * Launch a standalone application.
     * @param args - The line of arguments passed down to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}

