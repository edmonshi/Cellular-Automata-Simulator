package edu.vanier.waveSim.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class FXMLHelpGuideController extends Stage{
    @FXML
    private Pane pane;
    @FXML
    private VBox vBox;
    @FXML
    private Accordion accordion;
    @FXML
    private TextFlow howToUseAppTxtFlw;
    @FXML
    private TextFlow functTxtFlw;
    @FXML
    private TextFlow saveLoadTxtFlw;
    @FXML
    private TextFlow renderTxtFlw;

    public FXMLHelpGuideController() {
    }
    
    /*
    SimTabPane.heightProperty().addListener((observable) -> {
            setHeight(SimTabPane.heightProperty().getValue().intValue(), simulation, animation, lblHi);
        });
        SimTabPane.widthProperty().addListener((observable) -> {
            setWidth(SimTabPane.widthProperty().getValue().intValue(), simulation, animation, lblWi);
        });
    */
    @FXML
    private void initialize(){
        /*
        this.widthProperty().addListener((observable)->{
            System.out.println("working...");
            pane.setPrefWidth(this.getWidth());
            vBox.setPrefWidth(this.getWidth());
            accordion.setPrefWidth(this.getWidth());
            howUseAppTPane.setPrefWidth(this.getWidth());
        });
*/
        this.pane.widthProperty().addListener((observable)->{
            System.out.println("working..."+this.pane.getWidth());
            accordion.setPrefWidth(this.pane.getWidth());
            vBox.setPrefWidth(this.pane.getWidth());
            accordion.setPrefWidth(this.pane.getWidth());
            howToUseAppTxtFlw.setPrefWidth(this.pane.getWidth());
            functTxtFlw.setPrefWidth(this.pane.getWidth());
            saveLoadTxtFlw.setPrefWidth(this.pane.getWidth());
            renderTxtFlw.setPrefWidth(this.pane.getWidth());
        });
    }
    
}
