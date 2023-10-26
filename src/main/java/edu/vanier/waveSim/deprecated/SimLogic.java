/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.vanier.waveSim.deprecated;

import edu.vanier.waveSim.controllers.SimDriverController;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

/**
 *
 * @author William Carbonneau <2265724 at edu.vaniercollege.ca>
 */
public class SimLogic {

    private float[][] current;
    private float[][] nextFrame;

    private int widthX;
    private int heightY;

    /**
     * Some damping value 0 to 1 non-integer
     */
    private float damping = (float) 0.9;

    private Canvas operatingCanvas;

    /**
     * Get the damping from the simulation and return it.
     *
     * @return damping - The damping of the simulation. Some float value 0 to 1
     * non-integer.
     */
    public float getDamping() {
        return damping;
    }

    /**
     * Set damping for the waves. Some float value 0 to 1 non-integer.
     */
    public void setDamping(float damping) {
        this.damping = damping;
    }

    /**
     * Set the width of the simulation to the specified width.
     *
     * @param widthX - The specified width of the simulation. It is an int
     * value.
     */
    public void setWidthX(int widthX) {
        this.widthX = widthX;
    }

    /**
     * Set the height of the simulation to the specified height.
     *
     * @param heightY - The specified height of the simulation. It is an int
     * value.
     */
    public void setHeightY(int heightY) {
        this.heightY = heightY;
    }

    /**
     * Get the width of the simulation and return it.
     *
     * @return widthX - The width of the simulation. It is an int value.
     */
    public int getWidthX() {
        return widthX;
    }

    /**
     * Get the height of the simulation and return it.
     *
     * @return heightY - The height of the simulation. It is an int value.
     */
    public int getHeightY() {
        return heightY;
    }

    /**
     * Create new simulation from a width and height
     *
     * @param operatingCanvas The canvas to draw the simulation on. Must be
     * accessible from calling thread.
     * @param widthX The width in pixels for the horizontal
     * @param heightY The height in pixels for the vertical, measured top to
     * bottom
     */
    public SimLogic(Canvas operatingCanvas, int widthX, int heightY) {
        this.widthX = widthX;
        this.heightY = heightY;
        this.operatingCanvas = operatingCanvas; // TODO handle error invalid
        this.current = new float[widthX][heightY];
        this.nextFrame = new float[widthX][heightY];
    }

    /**
     * Set a point in the entered vertical and horizontal position.
     *
     * @param x horizontal position in pixels on the canvas
     * @param y vertical position in pixels on the canvas
     */
    public void setPoint(int x, int y) {
        this.current[x][y] = 255;
    }

    /**
     * Simulation logic is here per frame, comes from:
     * https://web.archive.org/web/20160418004149/http://freespace.virgin.net/hugo.elias/graphics/x_water.htm
     * Use the simulation logic of the waves to create the next frame of the
     * simulation. Creates a new grid the that is filled up with points using
     * the simulation logic of the waves and the current frame. Then replace the
     * current frame.
     */
    public void simFrame() {
        // for every non-edge element
        for (int x = 1; x < widthX - 1; x++) {
            for (int y = 1; y < heightY - 1; y++) {
                this.nextFrame[x][y] = (this.current[x - 1][y]
                        + this.current[x + 1][y]
                        + this.current[x][y - 1]
                        + this.current[x][y + 1])
                        / 2
                        - this.nextFrame[x][y];
                this.nextFrame[x][y] = this.nextFrame[x][y] * this.damping;

                // must make pixels positive for the color and map to 0-255 (use modulus)
                int pixelValue = ((int) (this.nextFrame[x][y]));
                if (pixelValue < 0) {
                    pixelValue = -pixelValue;
                }
                Color color;
                if (pixelValue != 0) {
                    color = Color.BLUE;
                } else {
                    color = Color.WHITE;
                }
                // get colour update
                //SimDriverController.colorCellWilliamVersion(this.operatingCanvas, x, y, color); // TODO colour mapping

            }
        }
        float[][] temp = this.current;
        this.current = this.nextFrame;
        this.nextFrame = temp;
    }

}
