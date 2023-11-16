package edu.vanier.waveSim;

import edu.vanier.waveSim.controllers.FXMLMainAppController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a JavaFX project template to be used for creating GUI applications.
 * The JavaFX GUI framework (version: 20.0.2) is linked to this project in the
 * build.gradle file.
 * @link: https://openjfx.io/javadoc/20/
 * @see: /Build Scripts/build.gradle
 * @author Sleiman Rabah.
 */
public class MainApp extends Application {

    private final static Logger logger = LoggerFactory.getLogger(MainApp.class);

    FXMLMainAppController SDC;
    /**
     * Override the abstract method start(Stage primaryStage) of Application.The main entry point for the JavaFX application.
     * 
     * @param primaryStage the primary stage for this application, onto which the application scene can be set.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Bootstrapping the application...");
            //-- 1) Load the scene graph from the specified FXML file and 
            // associate it with its FXML controller.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainAppFXML.fxml"));
            primaryStage.setTitle("Cellular Automation Simulations Explorer");
            SDC = new FXMLMainAppController(primaryStage);
            loader.setController(SDC);
            BorderPane root = loader.load();
            
            //-- 2) Create and set the scene to the stage.
            Scene scene = new Scene(root, 700, 526);
            primaryStage.setScene(scene);
            primaryStage.setAlwaysOnTop(true);
            primaryStage.sizeToScene();
            primaryStage.show();
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    @Override
    public void stop() throws Exception {
        super.stop();
        SDC.stopAnimation();
    }
    
    /**
     * Main method of the program.
     * Execute the launch method of the Application class.
     * Launch a standalone application.
     * @param args - The parameters of the launch method.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
