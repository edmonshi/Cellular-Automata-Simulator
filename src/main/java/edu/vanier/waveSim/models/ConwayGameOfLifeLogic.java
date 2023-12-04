package edu.vanier.waveSim.models;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *Conway's Game of Life is a cellular automaton that works by using several principles.
 * A cell can be either dead or alive.
 * All cells, by default are dead.
 * To make cell come to life, click on the canvas.
 * A cell that is alive, and that has two or three neighbouring cells that are alive in the present frame will be alive in the next frame.
 * A cell that has 0,1,4,5,6,7, or 8 cells that are alive in the present frame will be dead in the next frame.
 * A cell that is dead can come to life in the next frame if and only if it has 3 neighbouring cells in the present frame
 * The idea of checking whether the cells are going to be alive or dead in the next frame using methods comes from: Algosome, n.d.
 * Rules taken from: Johnson, n.d.
 * @author Loovdrish Sujore
 * @author William Carbonneau
 */
public class ConwayGameOfLifeLogic extends CellularLogic{
    
    private final static Logger logger = LoggerFactory.getLogger(ConwayGameOfLifeLogic.class);
    /** The color of the simulation is green*/
    private final Color simColor = Color.GREEN;
    /**
     * Constructor for the simulation.
     * @param operatingCanvas The canvas on which the animation will run
     * @param widthX The width of the canvas
     * @param heightY The height of the canvas
     * @param scale The scaling used
     */
    public ConwayGameOfLifeLogic(Canvas operatingCanvas, int widthX, int heightY, int scale) {
        super(operatingCanvas, widthX, heightY);
        // deal with scaling
        if (scale < 1 || scale % 2 != 0) {
            logger.info("scaling is not set or 1, setting to 1 by default");
        }else {
            setScaling(scale);
        }
    }
    /**
     * This method handles the logic of the simulation
     */
    @Override
    public void simFrame() {
        // goes through each value of the 2-d float array 'current', which represents the cells of the canvas
        for (int x =1; x<scaledX-1;x++){
            for (int y =1; y<scaledY-1;y++){
                // if the cell respects the rule for being alive in the next frame, set to alive
                if ( isAlive(x,y)){
                    // set that cell to alive in the next frame
                    this.nextFrame[x][y]= 255;
                    colorCell(x,y,simColor);
                // if the conditions are not followed, it is dead
                }else{
                    // set to dead in the next frame
                    this.nextFrame[x][y]= 0;
                    colorCell(x,y,backgroundColor);
                }
                
            }
        
        }
        // set the next frame to be the current one
        float[][] temp = this.current;
        this.current = this.nextFrame;
        this.nextFrame = temp;
    
    }
    /** Check if a cell is alive or dead
     * @param x type int horizontal coordinate in pixels
     * @param y type int vertical coordinate in pixels (top to bottom)
     * @return 0 (false) for dead and 255 (true) for alive*/
    public boolean isAlive(int x, int y){
        // setPoint sets a value of 255
        //counts the number of neighbours
        int neighbours = 0;
        boolean isAlive= false;
        // check around
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
        // if it has less than 2 or more than 3 neighbours, it is automatically dead in the next frame
        if((neighbours<2)||(neighbours>3)){
            isAlive = false;
        }
        // if it is alive and has 2 neighbours, or if it is dead and has 3 neighbours
        // it will be alive in the next frame
        else if ((neighbours == 2 && this.current[x][y] == 255) || neighbours == 3){
            isAlive = true;
        // if these two conditions are not respected, it is automatically dead in the next frame.
        }else{
            isAlive = false;
        }
        // return whether it is alive or dead in the next frame.
        return isAlive;
    }    
}
