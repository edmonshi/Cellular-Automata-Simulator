/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.vanier.waveSim.models;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author edmon
 */
public class DiffusionLimitedAggregation extends CellularLogic {

    private final static Logger logger = LoggerFactory.getLogger(ConwayGameOfLifeLogic.class);

    public DiffusionLimitedAggregation(Canvas operatingCanvas, int widthX, int heightY, int scale) {
        super(operatingCanvas, widthX, heightY);
        // deal with scaling
        if (scale < 1 || scale % 2 != 0) {
            logger.info("scaling is not set, setting to 1 by default");
        } else {
            setScaling(scale);
        }
    }

    boolean needToInitialize = true;

    @Override
    public void simFrame() {
        if (needToInitialize) {
            for (int counterX = 1; counterX < scaledX - 1; counterX++) {
                for (int counterY = 1; counterY < scaledY - 1; counterY++) {
                    this.current[counterX][counterY] = 0;
                    colorCell(counterX, counterY, Color.BLACK);
                }
            }
        }
        for (int counterX = 1; counterX<scaledX-1; counterX++) {
            for(int counterY =1; counterY<scaledY-1; counterY++){
                
            }
        }

        float[][] temp = this.current;
        this.current = this.nextFrame;
        this.nextFrame = temp;

    }

}
