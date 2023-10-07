package edu.vanier.waveSim.models;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import static javafx.scene.paint.Color.color;
import javafx.stage.Stage;


public class Grid extends Stage{
    public GraphicsContext g;
    Canvas canvas;
    public GraphicsContext getG() {
        return g;
    }
    public void setG(GraphicsContext g) {
        this.g = g;
    }
    public Canvas getCanvas() {
        return canvas;
    }
    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }
    public Grid(Stage owner) {
        setTitle("Grid");
        initOwner(owner);
        initComponents();
    }

    private void initComponents() {
        Group root = new Group();
        canvas = new Canvas(1000, 1000);
        g = canvas.getGraphicsContext2D();
        
        Scene scene = new Scene(root, 1000, 1000);
        //The maximum x and y values for method colorPix() are therefore 100 both
        setScene(scene);
        
    }
    private void colorPix(int x, int y, Color color){
        //The pixels have the dimensions set by height and width
        // x and y correspond to the coordinates of the pixel you want to color
        x=x*10+5;
        y=y*10+5;
        PixelWriter pw = g.getPixelWriter();
        for(int i=0; i<5; i++){
            //Assume that the dimensions of each pixel is 10*10
            pw.setColor(x-i, y-i, color);
            pw.setColor(x+i, y+i, color);
        }
    
    }
    

    
    
    
    
}
