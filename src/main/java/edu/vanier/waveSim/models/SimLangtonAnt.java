/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.vanier.waveSim.models;

import java.util.ArrayList;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author loovd
 */
public class SimLangtonAnt extends CellularLogic{
    private final static Logger logger = LoggerFactory.getLogger(SimLangtonAnt.class);
    private boolean needToInitialize = true;
    // 0 for white, and 1 for black
    /*
    private Ant[][] ants = new Ant[scaledX][scaledY];
*/
    private ArrayList<Ant> ants = new ArrayList<>();
    

    public SimLangtonAnt(Canvas operatingCanvas, int widthX, int heightY, int scale) {
        super(operatingCanvas, widthX, heightY);
        // deal with scaling
        if (scale < 1 || scale % 2 != 0) {
            logger.info("scaling is not set, setting to 1 by default");
        }else {
            setScaling(scale);
        }
    }

    @Override
       public void simFrame() {
           //Locate the ants
           if(needToInitialize){
               for(int counterX = 1; counterX<scaledX-1; counterX++){
                   for(int counterY=1; counterY<scaledY-1; counterY++){
                       if(this.current[counterX][counterY]==255){
                           Ant ant = new Ant();
                           ant.setX(counterX);
                           ant.setY(counterY);
                           ant.setBlackCell(false);
                           ant.setDirection('U');
                           ants.add(ant);
                       }
                   }
               }
               
           }
           for(int counterX = 0; counterX<ants.size(); counterX++){
               Ant present = ants.get(counterX);
               char D = ants.get(counterX).getDirection();
               if(present.isBlackCell()){
                   Ant newAnt = turnAntiClockWise(D, counterX);
                   this.nextFrame[newAnt.getX()][newAnt.getY()]=255;
                   colorCell(present.getX(), present.getY(), Color.WHITE);
               }
               else{
                   Ant newAnt = turnClockWise(D, counterX);
                   this.nextFrame[newAnt.getX()][newAnt.getY()]=255;
                   colorCell(present.getX(), present.getY(),Color.BLACK);
               }
           }
        float[][] temp = this.current;
        this.current = this.nextFrame;
        this.nextFrame = temp;
       }
    public boolean antIsHere(int x, int y){
        if(this.current[x][y]==255){
            return true;
        }
        else 
            return false;
    }
    private Ant turnAntiClockWise(char D, int counterX){
        Ant ant = new Ant();
        if(D=='U'){
            ant.setDirection('L');
            ant.setX(ants.get(counterX).getX()-1);
            ant.setY(ants.get(counterX).getY());
        }
        else if(D=='L'){
            ant.setDirection('D');
            ant.setX(ants.get(counterX).getX());
            ant.setY(ants.get(counterX).getY()-1);
            ants.set(counterX, ant);
        }
        else if(D=='D'){
            ant.setDirection('R');
            ant.setX(ants.get(counterX).getX()+1);
            ant.setY(ants.get(counterX).getY());
            ants.set(counterX, ant);
        }
        //D==R
        else{
            ant.setDirection('U');
            ant.setX(ants.get(counterX).getX());
            ant.setY(ants.get(counterX).getY()+1);
            ants.set(counterX, ant);
        }
        return ant;
    }
    private Ant turnClockWise(char D, int counterX){
        Ant ant = new Ant();
        if(D=='U'){
            ant.setDirection('L');
            ant.setX(ants.get(counterX).getX()-1);
            ant.setY(ants.get(counterX).getY());
        }
        else if(D=='L'){
            ant.setDirection('D');
            ant.setX(ants.get(counterX).getX());
            ant.setY(ants.get(counterX).getY()-1);
            ants.set(counterX, ant);
        }
        else if(D=='D'){
            ant.setDirection('R');
            ant.setX(ants.get(counterX).getX()+1);
            ant.setY(ants.get(counterX).getY());
            ants.set(counterX, ant);
        }
        //D==R
        else{
            ant.setDirection('U');
            ant.setX(ants.get(counterX).getX());
            ant.setY(ants.get(counterX).getY()+1);
            ants.set(counterX, ant);
        }
        return ant;
    }
    
}
class BlackCell{
}
class Ant{
    // Has x and y coordinates
    private int x;
    private int y;

    public Ant() {
        this.x=-1;
        this.y=-1;
        this.setDirection('U');
        this.setBlackCell(false);
    }
    
    // Has an orientation
    private char Direction = 'U';
    // Can be Up, Left, Right, Down
    // By default is directed up
    
    // Can be located on a black or white cell
    private boolean blackCell = false;
    // by default all celss are white

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

    public char getDirection() {
        return Direction;
    }

    public void setDirection(char Direction) {
        this.Direction = Direction;
    }

    public boolean isBlackCell() {
        return blackCell;
    }

    public void setBlackCell(boolean blackCell) {
        this.blackCell = blackCell;
    }
}