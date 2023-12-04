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
 * Wikipedia, 2023
 * Vogel, 2018
 * @author Loovdrish Sujore
 */
public class SimBriansBrain extends CellularLogic{
    private final static Logger logger = LoggerFactory.getLogger(SimBriansBrain.class);
    /**
     * Constructor for the simulation.
     * @param operatingCanvas The canvas on which the animation will run
     * @param widthX The width of the canvas
     * @param heightY The height of the canvas
     * @param scale The scaling used
     */
    public SimBriansBrain(Canvas operatingCanvas, int widthX, int heightY, int scale) {
        super(operatingCanvas, widthX, heightY);
        // deal with scaling
        if (scale < 1 || scale % 2 != 0) {
            logger.info("scaling is not set, setting to 1 by default");
        }else {
            setScaling(scale);
        }
    }
    /**
     * This method is an overriden method which represents the logic of the simulation
     * The idea of checking whether the cells are going to be alive or dead in the next frame using methods comes from: Algosome, n.d. (Which was used in Conway's Game of Life)
     */
    @Override
    public void simFrame() {
        // Goes through every single cell
        for(int counterX=1; counterX<scaledX-1; counterX++){
            for(int counterY=1; counterY<scaledY-1; counterY++){
                // If the cell is alive in the current frame, set it to the dying state for the next frame
                if(isAlive(counterX, counterY)){
                    //Set to dying state
                    this.nextFrame[counterX][counterY]=1;
                    colorCell(counterX, counterY, Color.GREY);
                }
                // If the cell is in a dying state, set the cell to dead for the next frame
                else if(isDying(counterX, counterY)){
                    this.nextFrame[counterX][counterY]=0;
                    colorCell(counterX, counterY, Color.WHITE);
                }
                // If the cell is dead, it can be set alive again-> Check the conditions
                else{
                    // If it respects the conditions to be alive in the next frame, set the cell to alive in the next frame
                    if(isAliveNext(counterX, counterY)){
                        this.nextFrame[counterX][counterY]=255;
                        colorCell(counterX, counterY, Color.BLACK);
                    }
                    // If the conditions are not respected, it remains dead
                    else{
                        this.nextFrame[counterX][counterY] = 0;
                        colorCell(counterX, counterY, Color.WHITE);
                    }
                }
        }
        }
        float[][] temp = this.current;
        this.current = this.nextFrame;
        this.nextFrame = temp;
    }
    /**
     * Verifies whether the cell is alive.
     * The cell is alive is it has a value of 255.
     * @param x type int, corresponds to the position of the cell on the x-axis
     * @param y type int, corresponds to the position of the cell on the y-axis
     * @return Boolean that represents whether the cell is alive or not
     */
    public boolean isAlive(int x, int y){
        if(this.current[x][y]==255)
            return true;
        else
            return false;
    }
    /**
     * Verifies whether the cell is dying.
     * The cell is dying is it has a value of 1.
     * @param x type int, corresponds to the position of the cell on the x-axis
     * @param y type int, corresponds to the position of the cell on the y-axis
     * @return boolean that represents whether the cell is dying or not
     */
    public boolean isDying(int x, int y){
        if(this.current[x][y]==1){
            return true;
        }
        else return false;
    }
    /**
     * Verifies whether the cell is going to be alive in the next frame.
     * The cell is going to be alive in the next frame if 2 of its neighbours are alive.
     * @param x type int, corresponds to the position of the cell on the x-axis
     * @param y type int, corresponds to the position of the cell on the y-axis
     * @return boolean that represents whether the cell is going t be alive or not
     */
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
}