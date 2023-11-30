package edu.vanier.waveSim.models;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Brian's brain is a cellular automaton model that consists of 3 types of cells (pixels).
 * A cell can be either alive, in a dying state of dead.
 * A cell that is alive goes into a dying state in the next frame, and a cell that is in the dying state dies in the next time frame.
 * Only a cell that is dead can be turned on (alive) again.
 * To do so, the cell must have 2 cells in the cell's moore neighborhood in order to be turned alive again.
 * Sources for rules:
 * https://en.wikipedia.org/wiki/Brian%27s_Brain
 * https://www.arnevogel.com/brians-brain-cellular-automaton/#:~:text=Rules%3A%20There%20are%20three%20cell,dies%20in%20the%20next%20iteration.
 * For other cellular automata:
 * https://en.wikipedia.org/wiki/Day_and_Night_(cellular_automaton)
 * https://datarepository.wolframcloud.com/resources/Famous-2D-Cellular-Automata
 */
public class SimBriansBrain extends CellularLogic{
    private final static Logger logger = LoggerFactory.getLogger(SimBriansBrain.class);
    private boolean toInitialize = true;
    //Copied from ConwayGameOfLife
    public SimBriansBrain(Canvas operatingCanvas, int widthX, int heightY, int scale) {
        super(operatingCanvas, widthX, heightY);
        // deal with scaling
        if (scale < 1 || scale % 2 != 0) {
            logger.info("scaling is not set, setting to 1 by default");
        }else {
            setScaling(scale);
        }
    }

    @Override
    public void simFrame() {
        for(int counterX=1; counterX<scaledX-1; counterX++){
            for(int counterY=1; counterY<scaledY-1; counterY++){
                // is alive
                if(isAlive(counterX, counterY)){
                    //Set to dying state
                    this.nextFrame[counterX][counterY]=1;
                    colorCell(counterX, counterY, Color.GREY);
                }
                // is dying
                else if(isDying(counterX, counterY)){
                    this.nextFrame[counterX][counterY]=0;
                    colorCell(counterX, counterY, Color.WHITE);
                }
                // is Dead-> can be set alive again
                else{
                    // check neighbourhood
                    if(isAliveNext(counterX, counterY)){
                        this.nextFrame[counterX][counterY]=255;
                        colorCell(counterX, counterY, Color.BLACK);
                    }
                }
        }
        }
        float[][] temp = this.current;
        this.current = this.nextFrame;
        this.nextFrame = temp;
    }
    public boolean isAlive(int x, int y){
        if(this.current[x][y]==255)
            return true;
        else
            return false;
    }
    public boolean isDying(int x, int y){
        if(this.current[x][y]==1){
            return true;
        }
        else return false;
    }
    public boolean isAliveNext(int x, int y){
        // check moore neighbourhood
        int neighbours=0;
        if(this.current[x-1][y-1]==255){
            neighbours++;
        }
        if(this.current[x][y-1]==255){
            neighbours++;
        }
        if(this.current[x+1][y-1]==255){
            neighbours++;
        }
        if(this.current[x-1][y]==255){
            neighbours++;
        }
        if(this.current[x+1][y]==255){
            neighbours++;
        }
        if(this.current[x-1][y+1]==255){
            neighbours++;
        }
        if(this.current[x][y+1]==255){
            neighbours++;
        }
        if(this.current[x+1][y+1]==255){
            neighbours++;
        }
        if(neighbours==2)
            return true;
        else
            return false;
    }
    public void initializeSim(){
        for(int counterX=1; counterX<scaledX-1; counterX++){
            for(int counterY=1; counterY<scaledY-1; counterY++){
                double num = Math.random()*3;
                // is alive
                if(num>=0&&num<1.0){
                    this.current[counterX][counterY]=255;
                    colorCell(counterX, counterY, Color.BLACK);
                }
                // is dying
                else if(num<2.0){
                    this.current[counterX][counterY]=1;
                    colorCell(counterX, counterY, Color.GREY);
                }
                // is Dead-> can be set alive again
                else{
                    this.current[counterX][counterY]=0;
                    colorCell(counterX, counterY, Color.WHITE);
                }
        }
        }
    }
}
/*
Unused code:
if(this.current[x-1][y-1]==255||this.current[x-1][y-1]==1){
            neighbours++;
        }
        if(this.current[x-1][y]==255||this.current[x-1][y]==1){
            neighbours++;
        }
        if(this.current[x-1][y+1]==255||this.current[x-1][y+1]==1){
            neighbours++;
        }
        if(this.current[x][y-1]==255||this.current[x][y-1]==1){
            neighbours++;
        }
        if(this.current[x][y+1]==255||this.current[x][y+1]==1){
            neighbours++;
        }
        if(this.current[x+1][y-1]==255||this.current[x+1][y-1]==1){
            neighbours++;
        }
        if(this.current[x+1][y]==255||this.current[x+1][y]==1){
            neighbours++;
        }
        if(this.current[x+1][y+1]==255||this.current[x+1][y+1]==1){
            neighbours++;
        }
*/