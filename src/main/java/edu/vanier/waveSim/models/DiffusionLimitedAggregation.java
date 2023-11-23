/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.vanier.waveSim.models;

import java.util.Date;
import java.util.Random;
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

    private boolean needToInitialize = true;

    public DiffusionLimitedAggregation(Canvas operatingCanvas, int widthX, int heightY, int scale) {
        super(operatingCanvas, widthX, heightY);
        // deal with scaling
        if (scale < 1 || scale % 2 != 0) {
            logger.info("scaling is not set, setting to 1 by default");
        } else {
            setScaling(scale);
        }
    }

    @Override
    public void simFrame() {
        if (needToInitialize) {
            needToInitialize = false;
            for (int counterX = 1; counterX < scaledX - 1; counterX++) {
                for (int counterY = 1; counterY < scaledY - 1; counterY++) {
                    Random random = new Random();
                    int chance = random.nextInt(100);
                    if (chance >= 90) {
                        this.current[counterX][counterY] = 1;
                        colorCell(counterX, counterY, Color.BLUE);
                    } else {
                        this.current[counterX][counterY] = 0;
                        colorCell(counterX, counterY, Color.BLACK);
                    }
                }
            }
            this.current[scaledX/2][scaledY/2] = 3;
            colorCell(scaledY/2, scaledY/2, Color.ORANGE);
        }
        for (int counterX = 1; counterX < scaledX - 1; counterX++) {
            for (int counterY = 1; counterY < scaledY - 1; counterY++) {
                //if () {
                    
                //}
            }
        }

        float[][] temp = this.current;
        this.current = this.nextFrame;
        this.nextFrame = temp;

    }

    private boolean isEmpty(int x, int y) {
        if (this.current[x][y] == 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isParticle(int x, int y) {
        if (this.current[x][y] == 1) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isDendrite(int x, int y) {
        if (this.current[x][y] == 2) {
            return true;
        } else {
            return false;
        }
    }
}
