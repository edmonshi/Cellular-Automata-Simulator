/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.vanier.waveSim.models;

import javafx.animation.AnimationTimer;

/**
 * The timer of the cellular logic simulation to synchronize the cellular logic and JavaFX.
 * Extends the AnimationTimer class of JavaFX that is responsible for creating timers.
 * 
 * @author William Carbonneau <2265724 at edu.vaniercollege.ca>
 */
public class CellularAnimTimer extends AnimationTimer {

    private CellularLogic logic;
    
    /**
     * Constructor of CellularAnimTimer
     * @param logic
     */
    public CellularAnimTimer(CellularLogic logic) {
        this.logic = logic;
    }
    
    /**
     * Overwritten method of AnimationTimer
     * Called for every frame.
     * @param now Time in nanoseconds
     */
    @Override
    public void handle(long now) {
        doHandle();
    }
    
    /**
     * Call the simFrame method to generate frame
     */
    private void doHandle(){
        logic.simFrame();
    }
}
