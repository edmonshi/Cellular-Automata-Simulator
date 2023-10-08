package edu.vanier.waveSim.models;

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
    public int x;
    public int y;
    Pixel[][] canvas = new Pixel[x][y];
    

    public Grid() {
        setX(100);
        setY(100);
        Pixel[][] canvas = new Pixel[100][100];
        setCanvas(canvas);
    }
    
    
    public Grid(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Pixel[][] getCanvas() {
        return canvas;
    }

    public void setCanvas(Pixel[][] canvas) {
        this.canvas = canvas;
    }
    public Pixel getPix(int x, int y){
        return this.canvas[x][y];
    }
    public void setPixel(int x, int y, Pixel pixel){
        this.canvas[x][y].setColor(pixel.getColor());
        this.canvas[x][y].setOn(pixel.isOn());
        this.canvas[x][y].setSize(pixel.getSize());
    }
    
}
