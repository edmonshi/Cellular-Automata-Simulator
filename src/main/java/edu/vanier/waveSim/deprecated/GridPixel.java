package edu.vanier.waveSim.deprecated;

import javafx.scene.paint.Color;

public class GridPixel{
    public boolean on;
    // Whether or not the pixel is currently activated
    public int size;
    // The size refers to the dimension of the sides, which are equal since a pixel is a square
    public Color color;
    // The color of the pixel

    public GridPixel() {
        // When you create a pixel, it is neutral and does not impact the animation
        // Need to activate it
        setOn(false);
        setSize(10);
        setColor(Color.WHITE);
    }

    public GridPixel(boolean on, int size, Color color) {
        this.on = on;
        this.size = size;
        this.color = color;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    
    
}
