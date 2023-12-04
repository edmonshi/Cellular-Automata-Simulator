package edu.vanier.waveSim.models;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This simulation simulates a forest. 
 * A forest has 3 types of spots: (1) A given spot can have tree growing on it, or (2) that spot can have a tree that is burning, or (3) that spot can be burnt.
 * By default, the forest starts with all burnt spots (No trees or burning trees).
 * Based on certain probabilities, a tree can grow.
 * Then, once a tree has been grown, there is a probability that this tree will catch fire.
 * These 2 probabilities are controlled by the user using sliders.
 * Once a tree has caugth fire, it will burn all the trees that are arund it.
 * And once a tree has caugth fire, it is guaranteed that it will be burnt in the next frame.
 * Source for Rules:
 * Dingeldein, n.d.
 * @author Loovdrish Sujore
 */
public class SimForestFire extends CellularLogic{
    private final static Logger logger = LoggerFactory.getLogger(SimForestFire.class);
    // Corresponds to the initial probability of the trees catching fire
    private double fire = 0.00001;
    // Corresponds to the initial probability of a tree growing in a burnt spot
    private double tree=0.5;
    
    /**
     * Constructor for the simulation.
     * @param operatingCanvas The canvas on which the animation will run
     * @param widthX The width of the canvas
     * @param heightY The height of the canvas
     * @param scale The scaling used
     */
    public SimForestFire(Canvas operatingCanvas, int widthX, int heightY, int scale) {
        super(operatingCanvas, widthX, heightY);
        // deal with scaling
        if (scale < 1 || scale % 2 != 0) {
            logger.info("scaling is not set, setting to 1 by default");
        } else {
            setScaling(scale);
        }
    }
    /**
     * Setter whidh sets the probability of a fire catching in the forest
     * This is used in the application's controller class to modify this value
     * @param fire The probability of the fire catching
     */
    public void setFire(double fire) {
        this.fire = fire;
    }
    /**
     * Setter whidh sets the probability of a tree growing on a burnt spot
     * This is used in the application's controller class to modify this value
     * @param tree The probability of a tree growing on a burnt spot
     */
    public void setTree(double tree) {
        this.tree = tree;
    }
    /**
     * This method handles the logic of the simulation
     */
    @Override
       public void simFrame() {
           // Goes through every cell
           for(int counterX = 1; counterX<scaledX-1; counterX++){
               for(int counterY =1; counterY<scaledY-1; counterY++){
                   // Verify the 3 states
                   // If the cell is alive it can catch fire
                   if(isAlive(counterX, counterY)){
                       // If one of the trees from its moore neighbourhood is on fire, then it will catch fire in the next frame
                       if(mooreBurning(counterX, counterY)){
                           this.nextFrame[counterX][counterY]=1;
                           colorCell(counterX, counterY, Color.RED);
                       }
                       // If not, there is still a probability that it will catch fire
                       else{
                           // To activate the part of the probability, we generate a random number between 0 and 1
                           double f = Math.random();
                           // If that number is between 0 and the probability of the fire catching, then the tree is lit on fire
                           if(f>0&&f<fire){
                               this.nextFrame[counterX][counterY]=1;
                               colorCell(counterX, counterY, Color.RED);
                           }
                           // If not, then the tree will remain alive in the next frame
                           else{
                               this.nextFrame[counterX][counterY] = 2;
                               colorCell(counterX, counterY, Color.GREEN);
                           }
                       }
                   }
                   // If a tree is burning, it will be burnt in the next frame
                   else if(isBurning(counterX, counterY)){
                       this.nextFrame[counterX][counterY]=0;
                       colorCell(counterX, counterY, Color.BLACK);
                   }
                   // If a tree is dead, it can stay dead or a tree can grow there and replace it
                   else{
                       // Generate a random number
                       double n = Math.random();
                       //Verify whether that number is between 0 and the probability of a tree growing
                       if(n>0&&n<=tree){
                           // If so, then set a tree there for the next frame
                           this.nextFrame[counterX][counterY]=2;
                           colorCell(counterX, counterY, Color.GREEN);
                       }
                       //If not, then it will remain burnt
                       else{
                           this.nextFrame[counterX][counterY]=0;
                           colorCell(counterX, counterY, Color.BLACK);
                       }
                   }
               }
           }
           // Set the current frame to the next one to update
           this.current = this.nextFrame;
    }
    /**
     * This method checks whether a cell is alive or not
     * A cell is alive if its floating value is equal to 2
     * @param x Corresponds to the location of the cell on the x-axis
     * @param y Corresponds to the location of the cell on the y-axis
     * @return The boolean corresponding to whether that cell is alive or not
     */
    private boolean isAlive(int x, int y) {
        if (this.current[x][y] == 2) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * This method checks whether a cell is burning or not
     * A cell is burning if its floating value is equal to 1
     * @param x Corresponds to the location of the cell on the x-axis
     * @param y Corresponds to the location of the cell on the y-axis
     * @return The boolean corresponding to whether that cell is burning or not
     */
    private boolean isBurning(int x, int y) {
        if (this.current[x][y] == 1) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * This method checks whether a cell's moore neighbourhood is burning or not
     * A cell's moore neighbourhood is burning if at least one its 'neighbours' has a value of 1
     * @param x Corresponds to the location of the cell on the x-axis
     * @param y Corresponds to the location of the cell on the y-axis
     * @return The boolean corresponding to whether that cell's moore neighbourhood is burning or not
     */
    private boolean mooreBurning(int x, int y) {
        // check around cell
        if (this.current[x - 1][y - 1] == 1) {
            return true;
        }
        else if (this.current[x - 1][y] == 1) {
            return true;
        }
        else if (this.current[x - 1][y + 1] == 1) {
            return true;
        }
        else if (this.current[x][y - 1] == 1) {
            return true;
        }
        else if (this.current[x][y + 1] == 1) {
            return true;
        }
        else if (this.current[x + 1][y - 1] == 1) {
            return true;
        }
        else if (this.current[x + 1][y] == 1) {
            return true;
        }
        else if (this.current[x + 1][y + 1] == 1) {
            return true;
        }
        else 
            return false;
    }

}
