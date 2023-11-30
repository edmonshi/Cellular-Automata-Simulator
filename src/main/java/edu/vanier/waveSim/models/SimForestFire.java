
package edu.vanier.waveSim.models;

import java.util.ArrayList;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimForestFire extends CellularLogic{
    private final static Logger logger = LoggerFactory.getLogger(SimForestFire.class);
    private boolean needToInitialize = true;
    private double fire=0.5;
    private double tree=0.5;
    

    public SimForestFire(Canvas operatingCanvas, int widthX, int heightY, int scale) {
        super(operatingCanvas, widthX, heightY);
        // deal with scaling
        if (scale < 1 || scale % 2 != 0) {
            logger.info("scaling is not set, setting to 1 by default");
        } else {
            setScaling(scale);
        }
    }
    // Number between 1 and 10
    public void setFire(double fire) {
        this.fire = fire;
    }

    public void setTree(double tree) {
        this.tree = tree;
    }

    @Override
       public void simFrame() {
           if(needToInitialize){
               needToInitialize=false;
               for(int counterX = 1; counterX<scaledX-1; counterX++){
                   for(int counterY =1; counterY<scaledY-1; counterY++){
                       this.current[counterX][counterY]=2;
                       colorCell(counterX, counterY, Color.GREEN);
                   }
                   }
               
           }
           for(int counterX = 1; counterX<scaledX-1; counterX++){
               for(int counterY =1; counterY<scaledY-1; counterY++){
                   // 3 states
                   if(isAlive(counterX, counterY)){
                       // Check moore neighbourhood for burning fire trees
                       if(mooreBurning(counterX, counterY)){
                           this.nextFrame[counterX][counterY]=1;
                           colorCell(counterX, counterY, Color.RED);
                       }
                       else{
                           double f = Math.random();
                           if(f>0&&f<fire){
                               this.nextFrame[counterX][counterY]=1;
                               colorCell(counterX, counterY, Color.RED);
                           }
                       }
                   }
                   else if(isBurning(counterX, counterY)){
                       this.nextFrame[counterX][counterY]=0;
                       colorCell(counterX, counterY, Color.BLACK);
                   }
                   // is DEad
                   else{
                       double n = Math.random();
                       if(n>0&&n<=tree){
                           this.nextFrame[counterX][counterY]=2;
                           colorCell(counterX, counterY, Color.GREEN);
                       }
                   }
               }
           }
        float[][] temp = this.current;
        this.current = this.nextFrame;
        this.nextFrame = temp;
    }

    private boolean isAlive(int x, int y) {
        if (this.current[x][y] == 2) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isBurning(int x, int y) {
        if (this.current[x][y] == 1) {
            return true;
        } else {
            return false;
        }
    }

    private boolean mooreBurning(int x, int y) {
        if (this.current[x - 1][y - 1] == 1) {
            return true;
        }
        if (this.current[x - 1][y] == 1) {
            return true;
        }
        if (this.current[x - 1][y + 1] == 1) {
            return true;
        }
        if (this.current[x][y - 1] == 1) {
            return true;
        }
        if (this.current[x][y + 1] == 1) {
            return true;
        }
        if (this.current[x + 1][y - 1] == 1) {
            return true;
        }
        if (this.current[x + 1][y] == 1) {
            return true;
        }
        if (this.current[x + 1][y + 1] == 1) {
            return true;
        }
        return false;
    }

}
