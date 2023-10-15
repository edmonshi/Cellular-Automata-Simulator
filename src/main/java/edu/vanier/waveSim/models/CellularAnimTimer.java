/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.vanier.waveSim.models;

import javafx.animation.AnimationTimer;

/**
 *
 * @author William Carbonneau <2265724 at edu.vaniercollege.ca>
 */
public class CellularAnimTimer extends AnimationTimer {

    private CellularLogic logic;
    
    /**TODO documentation
     * @param logic
     */
    public CellularAnimTimer(CellularLogic logic) {
        this.logic = logic;
    }
    
    @Override
    public void handle(long now) {
        doHandle();
    }
    
    private void doHandle(){
        logic.simFrame();
    }
}
