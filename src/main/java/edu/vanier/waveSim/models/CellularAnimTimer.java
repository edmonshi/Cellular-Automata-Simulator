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
 * The timer of the cellular logic simulation to synchronize the cellular logic
 * and JavaFX. Extends the AnimationTimer class of JavaFX that is responsible
 * for creating timers.
 *
 * @author William Carbonneau <2265724 at edu.vaniercollege.ca>
 */
public class CellularAnimTimer extends AnimationTimer {

    private CellularLogic logic;
    private FXMLMainAppController controller;
    private long delayMillis = 1;

    // use internally to regulate speed
    private long lastUpdate = 0;

    /**
     * Set the delay for the frames to control speed in milliseconds
     * @param delayMillis type long - milliseconds
     */
    public void setDelayMillis(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    /**
     * Get the value of the frame delay in milliseconds
     * @return type long - frame delay in milliseconds
     */
    public long getDelayMillis() {
        return delayMillis;
    }

    /**
     * Constructor of CellularAnimTimer
     * Creates a new logic parameter and a resets the controller to the main app
     * @param logic
     */
    public CellularAnimTimer(CellularLogic logic, FXMLMainAppController controller) {
        this.logic = logic;
        this.controller = controller;
    }

    /**
     * Overwritten method of AnimationTimer Called for every frame.
     *
     * @param now Time in nanoseconds
     */
    @Override
    public void handle(long now) {
        if (now - lastUpdate >= delayMillis * 1_000_000) {
            doHandle();
            lastUpdate = now;
        }
    }

    /**
     * Necessary method to animate, called every frame
     * Call the simFrame method to generate frame in animation loop
     * Also tries to save the frame if the render flag is true
     */
    private void doHandle() {
        if (logic.getFrameNumber() < logic.getFrameLimit()) {
            if (logic.getRenderFlag()) {
                // if needed, create a new buffered image to render into
                logic.createRenderContext();
            }
            // call the logic of the animation
            logic.simFrame();
            if (logic.getRenderFlag()) {
                try {
                    // save a rendered frame
                    logic.saveFrame();
                } catch (IOException ex) {
                    Logger.getLogger(CellularAnimTimer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            // increment frame number
            logic.setFrameNumber(logic.getFrameNumber()+1);
        } else {
            // reset the simulation once a render is completed
            controller.ResetScreenAndAnim(logic, this, logic.getScaling());
        }
    }
}
