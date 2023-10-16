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
 * The abstract class CellularLogic defines the basic structure and behavior for
 * cellular automata logic. It serves as a foundation for specific cellular
 * automata implementations.
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

    protected Color backgroundColor = Color.WHITE;

    /**
     * Must be an even integer, should be regulated by a dropdown menu in the GUI
     */
    protected int scaling = 1;

    /**
     * Create new simulation from a width and height
     *
     * @param operatingCanvas The canvas to draw the simulation on. Must be
     * accessible from calling thread.
     * @param widthX The width in pixels for the horizontal
     * @param heightY The height in pixels for the vertical, measured top to
     * bottom Must set scaling, but it is by default 1
     */
    public CellularLogic(Canvas operatingCanvas, int widthX, int heightY) {
        this.operatingCanvas = operatingCanvas; // TODO handle error invalid

        this.widthX = widthX;
        this.heightY = heightY;
        this.scaledX = widthX;
        this.scaledY = heightY;

        // make grid even width and height
        if (widthX % 2 != 0) {
            widthX -= 1;
        } else if (heightY % 2 != 0) {
            heightY -= 1;
        }

        this.current = new float[widthX][heightY];
        this.nextFrame = new float[widthX][heightY];
    }

    /**
     * Scale the height and width of the grid by the scaling factor given.
     *
     * @param scaling The scaling factor.
     */
    public void setScaling(int scaling) {
        this.scaling = scaling;
        // scale grid by scaling factor (since it is an even number, and the width/height are even, this will always be integers.)
        this.scaledX = widthX / this.scaling;
        this.scaledY = heightY / this.scaling;
        this.current = new float[widthX / this.scaling][heightY / this.scaling];
        this.nextFrame = new float[widthX / this.scaling][heightY / this.scaling];

    }

    /**
     * Get the scaled width of the simulation and return it.
     *
     * @return scaledX The scaled width of the simulation. It is an int
     * value.
     */
    public int getScaledX() {
        return scaledX;
    }

    /**
     * Get the scaled height of the simulation and return it.
     *
     * @return scaledY The scaled height of the simulation. It is an int
     * value.
     */
    public int getScaledY() {
        return scaledY;
    }

    /**
     * Get the operating canvas of the simulation and return it.
     *
     * @return opertingCanavas The operating canvas of the simulation. It is a
     * Canvas object.
     */
    public Canvas getOperatingCanvas() {
        return operatingCanvas;
    }

    /**
     * Get the scaling factor of the simulation and return it.
     *
     * @return scaling The scaling factor of the simulation. It is an int
     * value.
     */
    public int getScaling() {
        return scaling;
    }

    /**
     * Set the width of the simulation to the specified width.
     *
     * @param widthX The specified width of the simulation. It is an int
     * value.
     */
    public void setWidthX(int widthX) {
        this.widthX = widthX;
    }

    /**
     * Set the height of the simulation to the specified height.
     *
     * @param heightY The specified height of the simulation. It is an int
     * value.
     */
    public void setHeightY(int heightY) {
        this.heightY = heightY;
    }

    /**
     * Get the width of the simulation and return it.
     *
     * @return widthX The width of the simulation. It is an int value.
     */
    public int getWidthX() {
        return widthX;
    }

    /**
     * Get the height of the simulation and return it.
     *
     * @return heightY The height of the simulation. It is an int value.
     */
    public int getHeightY() {
        return heightY;
    }

    /**
     * Get the background color of the simulation and return it.
     *
     * @return backgroundColor The background color of the simulation. It is a
     * color object.
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Set the background color of the simulation to the specified background
     * color.
     *
     * @param backgroundColor The specified color that the background of the
     * simulation. It is a color object.
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Set a point in the entered vertical and horizontal position.
     *
     * @param x horizontal position in pixels on the canvas
     * @param y vertical position in pixels on the canvas
     */
    public void setPoint(int x, int y) {
        this.current[x / this.scaling][y / this.scaling] = 255;
    }

    /**
     * Removes a point from the simulation array if it is maximum 255 (removes
     * only origination points)
     *
     * @param x horizontal position in pixels on the canvas
     * @param y vertical position in pixels on the canvas
     * @return true if point was removed from the array, false otherwise
     */
    public boolean removePoint(int x, int y) {
        if (this.current[x / this.scaling][y / this.scaling] == 255) {
            this.current[x / this.scaling][y / this.scaling] = 0;
            return true;
        }
        return false;
    }

    /**
     * Set the color of a single cell in the canvas from array coordinates using
     * scale factor.
     *
     * @param xPosInArray The x position from top right in units of scaled
     * pixels int the numerical grid
     * @param yPosInArray The y position from top right in units of scaled
     * pixels int the numerical grid
     * @param color The color of the pixel using javaFX Color object
     */
    public void colorCell(int xPosInArray, int yPosInArray, Color color) {
        GraphicsContext Graphics = this.operatingCanvas.getGraphicsContext2D();
        Graphics.setFill(color);
        WritablePixelFormat<IntBuffer> format = WritablePixelFormat.getIntArgbInstance();
        Graphics.fillRect(xPosInArray * this.scaling, yPosInArray * this.scaling, this.scaling, this.scaling);

    }

    /**
     * Clear the grid of simulation by resetting all grid cells to the background color.
     */
    public void clearScreen() {
        GraphicsContext Graphics = this.operatingCanvas.getGraphicsContext2D();
        Graphics.setFill(backgroundColor);
        Graphics.fillRect(0, 0, widthX, heightY);
    }

    /**
     * The simulation logic that must be implemented by the extending class.
     * Must set pixel colors using colorCell, and modify the current and
     * nextFrame grids accordingly. Use the scaledX and scaledY properties to
     * simulate
     */
    public abstract void simFrame();
}
