/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.vanier.waveSim.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author William Carbonneau <2265724 at edu.vaniercollege.ca>
 */
public class RenderSettingsController {
    int frameLimit = Integer.MAX_VALUE;
    Stage self;
    
    @FXML
    private TextField txtFrameLimit;
    @FXML
    private Button btnApply;

    /**
     * TODO
     */
    public void setSelf(Stage self) {
        this.self = self;
    }

    /**
     * TODO
     */
    public int getFrameLimit() {
        return frameLimit;
    }
    
    @FXML
    public void initialize() {
        btnApply.setOnAction((event) -> {
            String input = txtFrameLimit.getText();
            try {
                int inputInt = Integer.parseInt(input);
                if (inputInt > 0) {
                    frameLimit = inputInt;
                    self.hide();
                }
            }catch (Exception e) {
                txtFrameLimit.setText("Invalid, please use an integer");
            }
        });
    }
}
