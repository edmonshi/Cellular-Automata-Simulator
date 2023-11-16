/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.vanier.waveSim.models;

import edu.vanier.waveSim.controllers.FXMLMainAppController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;

/**
 * The timer of the cellular logic simulation to synchronize the cellular logic and JavaFX.
 * Extends the AnimationTimer class of JavaFX that is responsible for creating timers.
 * 
 * @author William Carbonneau <2265724 at edu.vaniercollege.ca>
 */
public class CellularAnimTimer extends AnimationTimer {

    private CellularLogic logic;
    private FXMLMainAppController controller;
    private long delayMillis = 1;
    
    // use internally to regulate speed
    private long lastUpdate = 0 ;

    public void setDelayMillis(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    public long getDelayMillis() {
        return delayMillis;
    }

    
    /**
     * Constructor of CellularAnimTimer
     * @param logic
     */
    public CellularAnimTimer(CellularLogic logic, FXMLMainAppController controller) {
        this.logic = logic;
        this.controller = controller;
    }
    
    /**
     * Overwritten method of AnimationTimer
     * Called for every frame.
     * @param now Time in nanoseconds
     */
    @Override
    public void handle(long now) {
        if (now - lastUpdate >= delayMillis * 1_000_000) {
            doHandle();
            lastUpdate = now ;
        }
    }
    
    /**
     * Call the simFrame method to generate frame
     */
    private void doHandle(){
        if (logic.frameNumber < logic.frameLimit) {
            if (logic.getRenderFlag()) {
                logic.createRenderContext();
            }
            logic.simFrame();
            if (logic.getRenderFlag()) {
                try {
                    logic.saveFrame();
                } catch (IOException ex) {
                    Logger.getLogger(CellularAnimTimer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("Frame: "+logic.frameNumber);
            logic.frameNumber++;
        }else {
            controller.ResetScreenAndAnim(logic,this,logic.getScaling());
        }
    }
}
