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
 * @author 2265724
 */
public class ConwayGameOfLifeLogic extends CellularLogic{
    
    private final static Logger logger = LoggerFactory.getLogger(SimLogicWave1.class);

    public ConwayGameOfLifeLogic(Canvas operatingCanvas, int widthX, int heightY, int scale) {
        super(operatingCanvas, widthX, heightY);
        // deal with scaling
        if (scale < 1 || scale % 2 != 0) {
            logger.error("scaling is wrong, setting to 1 by default");
        }else {
            setScaling(scale);
        }
    }

    @Override
    public void simFrame() {
        for (int x =1; x<scaledX-1;x++){
            for (int y =1; y<scaledY-1;y++){
                if ( isAlive(x,y)){
                    this.nextFrame[x][y]= 255;
                    colorCell(x,y,Color.GREEN);
                }else{
                    this.nextFrame[x][y]= 0;
                    colorCell(x,y,backgroundColor);
                }
                
            }
        
        }
//        if (isAlive(1,1)) {
//            colorCell(1,1,Color.GREEN);
//        }
//        if (isAlive(1,2)) {
//            colorCell(1,2,Color.GREEN);
//        }
//        if (isAlive(2,2)) {
//            colorCell(2,2,Color.GREEN);
//        }
//        if (isAlive(2,1)) {
//            colorCell(2,1,Color.GREEN);
//        }

        float[][] temp = this.current;
        this.current = this.nextFrame;
        this.nextFrame = temp;
    
    }
    // isAlive returns 0 for dead and 1 for alive
    // nextFrame and current
    public boolean isAlive(int x, int y){
        // setPoint sets a value of 255, TODO override maybe
        int neighbours = 0;
        boolean isAlive= false;
        if(this.current[x-1][y-1] == 255) {
            neighbours++;
        }
        if(this.current[x][y-1] == 255) {
            neighbours++;
        }
        if(this.current[x+1][y-1] == 255) {
            neighbours++;
        }
        if(this.current[x-1][y+1] == 255) {
            neighbours++;
        }
        if(this.current[x][y+1] == 255) {
            neighbours++;
        }
        if(this.current[x+1][y+1] == 255) {
            neighbours++;
        }
        if(this.current[x-1][y] == 255) {
            neighbours++;
        }
        if(this.current[x+1][y] == 255) {
            neighbours++;
        }
        if((neighbours<2)||(neighbours>3)){
            isAlive = false;
        }
        else if ((neighbours == 2 && this.current[x][y] == 255) || neighbours == 3){
            isAlive = true;
        }else{
            isAlive = false;
        }
        return isAlive;
    }    
}
