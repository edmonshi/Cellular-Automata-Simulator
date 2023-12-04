package deprecated;

import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
    /**
     * Constructor for the FXMLMainAppController
     * Non-param constructor
     */
    public FXMLHelpGuideController() {
    }
    /**
     * Initializes the controller
     */
    @FXML
    private void initialize(){
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
