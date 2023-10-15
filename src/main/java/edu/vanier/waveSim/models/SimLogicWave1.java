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
 * @author William Carbonneau <2265724 at edu.vaniercollege.ca>
 */
public class SimLogicWave1 extends CellularLogic{
    
    /**Some damping value 0 to 1 non-integer
     */
    private float damping = (float) 0.9;

    
    private final static Logger logger = LoggerFactory.getLogger(SimLogicWave1.class);
    

    public float getDamping() {
        return damping;
    }


    /** * Set damping for the waves.Some float value 0 to 1 non-integer.
     * @param damping, the damping value, must be a float between 0 and < 1
     */
    public void setDamping(float damping) {
        this.damping = damping;
    }


    /**
     * Create new simulation from a width and height 
     * @param operatingCanvas The canvas to draw the simulation on. Must be accessible from calling thread.
     * @param widthX The width in pixels for the horizontal
     * @param heightY The height in pixels for the vertical, measured top to bottom
     * @param scaling Scaling should be handled by the GUI as a dropdown menu, but it is checked here anyway.
     */
    public SimLogicWave1(Canvas operatingCanvas, int widthX, int heightY, int scaling) {
        super(operatingCanvas, widthX, heightY);
        if (scaling < 1 || scaling % 2 != 0) {
            logger.error("scaling is wrong, setting to 1 by default");
        }else {
            setScaling(scaling);
        }
    }

    
    /**Simulation logic is here per frame, comes from: https://web.archive.org/web/20160418004149/http://freespace.virgin.net/hugo.elias/graphics/x_water.htm
     *TODO Cite properly
     */
    @Override
    public void simFrame(){
        // for every non-edge element
        for (int x =1; x<scaledX-1;x++){
            for (int y =1; y<scaledY-1;y++){
                this.nextFrame[x][y] = (this.current[x-1][y]
                                      + this.current[x+1][y]
                                      + this.current[x][y-1]
                                      + this.current[x][y+1])
                                        /2
                                      -this.nextFrame[x][y];
                this.nextFrame[x][y] = this.nextFrame[x][y] * this.damping;
                
                
                // must make pixels positive for the color and map to 0-255 (use modulus)
                int pixelValue = ((int) (this.nextFrame[x][y]));
                if (pixelValue < 0) {
                    pixelValue = -pixelValue;
                }
                Color color;
                if(pixelValue != 0) {
                    // sim Color
                    color = Color.BLUE;
                } else{
                    // background Color
                    color = backgroundColor;
                }
                // get colour update
                colorCell(x, y, color); // TODO colour mapping
                
            }
        }
        float[][] temp = this.current;
        this.current = this.nextFrame;
        this.nextFrame = temp;
    }
    

    
    
    
}
