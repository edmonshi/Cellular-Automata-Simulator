package edu.vanier.waveSim.deprecated;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import static javafx.scene.paint.Color.color;
import javafx.stage.Stage;


public class Grid{
    //A grid is a 2-D array of pixels
    //Set the number of pixels we want
    // We want 100 for example
    // The canvas has 400*500 pixels
    // Each pixel = 10*10
    GridPixel[][] canvas = new GridPixel[40][50];
    

    public Grid() {
        GridPixel[][] canvas = new GridPixel[40][50];
        for(int i=0; i<40; i++){
            for(int j=0; j<50; j++){
                canvas[i][j] = new GridPixel();
            }
        }
        setCanvas(canvas);
    }
    
    
    public GridPixel[][] getCanvas() {
        return canvas;
    }

    public void setCanvas(GridPixel[][] canvas) {
        this.canvas = canvas;
    }
    public GridPixel getPix(int x, int y){
        return this.canvas[x][y];
    }
    public void setPixel(int x, int y, GridPixel pixel){
        this.canvas[x][y].setColor(pixel.getColor());
        this.canvas[x][y].setOn(pixel.isOn());
        this.canvas[x][y].setSize(pixel.getSize());
    }
    
}
