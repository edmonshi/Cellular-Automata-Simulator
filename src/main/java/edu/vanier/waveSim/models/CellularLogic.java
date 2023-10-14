/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.vanier.waveSim.models;

import java.nio.IntBuffer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;

/**
 *
 * @author William Carbonneau <2265724 at edu.vaniercollege.ca>
 */
public abstract class CellularLogic {
    protected float[][] current;
    protected float[][] nextFrame;
    
    protected final Canvas operatingCanvas;
    
    protected int widthX;
    protected int heightY;
    
    protected int scaledX;
    protected int scaledY;
    
    /**Must be an even integer, will be regulated by a dropdown menu the GUI*/
    protected int scaling = 1;
    
    /**
     * Create new simulation from a width and height 
     * @param operatingCanvas The canvas to draw the simulation on. Must be accessible from calling thread.
     * @param widthX The width in pixels for the horizontal
     * @param heightY The height in pixels for the vertical, measured top to bottom
     * Must set scaling, but it is by default 1
     */
    public CellularLogic(Canvas operatingCanvas, int widthX, int heightY) {
        this.operatingCanvas = operatingCanvas; // TODO handle error invalid
        
        this.widthX = widthX;
        this.heightY = heightY;
        
        // make grid even width and height
        if (widthX%2 != 0){ widthX -= 1;
        }else if (heightY%2 != 0){ heightY -= 1;} 
    }

    public void setScaling(int scaling) {
        this.scaling = scaling;
                // scale grid by scaling factor (since it is an even number, and the width/height are even, this will always be integers.)
        this.scaledX = widthX/this.scaling;
        this.scaledY = heightY/this.scaling;
        this.current = new float[widthX/this.scaling][heightY/this.scaling];
        this.nextFrame = new float[widthX/this.scaling][heightY/this.scaling];
        
    }

    public int getScaling() {
        return scaling;
    }
    
    public void setWidthX(int widthX) {
        this.widthX = widthX;
    }

    public void setHeightY(int heightY) {
        this.heightY = heightY;
    }
    
    public int getWidthX() {
        return widthX;
    }

    public int getHeightY() {
        return heightY;
    }
    
    /**TODO documentation
     * @param x horizontal position in pixels on the canvas
     * @param y vertical position in pixels on the canvas
     */
    public void setPoint(int x, int y) {
        this.current[x/this.scaling][y/this.scaling] = 255;
    }
    
    /**
     * Set the color of a single cell in the grid using scale factor.
     * @param xPosInGrid The x position from top right in units of scaled pixels int the numerical grid
     * @param yPosInGrid The y position from top right in units of scaled pixels int the numerical grid
     * @param color The color of the pixel using javaFX Color object
     */
    protected void colorCell(int xPosInGrid, int yPosInGrid, Color color){
        GraphicsContext Graphics = this.operatingCanvas.getGraphicsContext2D();
        Graphics.setFill(color);
        WritablePixelFormat<IntBuffer> format = WritablePixelFormat.getIntArgbInstance();
        Graphics.fillRect(xPosInGrid*this.scaling, yPosInGrid*this.scaling, this.scaling, this.scaling);
        
    }
    
    /**The simulation logic that must be implemented by the extending class. 
     * Must set pixel colors using colorCell, and modify the current and nextFrame grids accordingly.
     * Use the scaledX and scaledY properties to simulate
     */
     public abstract void simFrame();
}
