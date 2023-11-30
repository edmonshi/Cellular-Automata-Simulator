/**
 * https://github.com/Gugubo/Rock-Paper-Scissor-Cellular-Automaton/blob/master/Panel.java
 * https://github.com/topics/cellular-automata?l=java
 */
package edu.vanier.waveSim.models;

import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 2264570
 */
public class SimRPC extends CellularLogic {

    private int frameNumber = 0;
    private int nreOfDifferentEntities = 3;
    private int nreOfNeededPredator = 2;
    private int nreOfRandomPredator = 1;
    Color[] colors = {Color.RED, Color.BLUE, Color.GREEN};
    private final static Logger logger = LoggerFactory.getLogger(SimRPC.class);
    private Random random = new Random();
    private PerlinNoise perlin = new PerlinNoise();

    public SimRPC(Canvas operatingCanvas, int widthX, int heightY, int scaling) {
        super(operatingCanvas, widthX, heightY);
        if (scaling < 1 || scaling % 2 != 0) {
            logger.info("scaling is not set, setting to 1 by default");
        } else {
            setScaling(scaling);
        }
    }

    public void InitializeRandomColor() {
        for (int i = 0; i < scaledX; i++) {
            for (int j = 0; j < scaledY; j++) {
                int color;
                double value = perlin.noise(i, j);
                if (value >= -1 && value < -0.3) {
                    color = 0;
                } else if (value >= -0.3 && value < 0.3) {
                    color = 1;
                } else if (value >= 0.3 && value <= 1) {
                    color = 2;
                } else {
                    color = 0;
                }
                current[i][j] = color;

            }
        }
        for(int i = 0; i < 3; i++){
            System.out.println((i+1)%3);
        }
        nextFrame = current;
    }

    /*
    To Modify for my 
    verify scaled x and y
     */
    @Override
    public void simFrame() {

        if (hasInitialized == false) {
            InitializeRandomColor();
            System.out.println("Initialized");
            hasInitialized = true;
        } else {
            
            for (int i = 0; i < scaledX; i++) {
                for (int j = 0; j < scaledY; j++) {
                    devouredOrNot(i, j);
                }
            }
            float[][] temp = this.current;
            this.current = this.nextFrame;
            this.nextFrame = temp;

        }

        paintTheCanvas(scaledX, scaledY);
    }

    /**
     *
     * @param x
     * @param y
     */
    public void devouredOrNot(int x, int y) {
        /*
        if (current[x][y] == 0) {
            this.nextFrame[x][y] = 1;
        }
        else if (current[x][y] == 1) {
            this.nextFrame[x][y] = 2;
        }
        else if (current[x][y] == 2) {
            this.nextFrame[x][y] = 0;
        }
     */    

        int predators = 0;
/*        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                // System.out.println((x+j-1)+", "+(y+i-1));
                //Catches < 3; j++) {
                if (i != 1 || j != 1) {
                    //Catches all the index that will be out of bound and ignore them
                    try {
                        if (current[x + i - 1][y + j - 1] == ((current[x][y] + 1) % 3)) {
                            predators++;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }

                }
            }
        }
*/
    try{
        float greg = ((current[x][y] + 1) % 3);
        if(current[x-1][y-1]==greg){
            predators++;
        }
        if(current[x-1][y]==greg){
            predators++;
        }
        if(current[x-1][y+1]==greg){
            predators++;
        }
        if(current[x][y-1]==greg){
            predators++;
        }
        if(current[x][y+1]==greg){
            predators++;
        }
        if(current[x+1][y-1]==greg){
            predators++;
        }
        if(current[x+1][y]==greg){
            predators++;
        }
        if(current[x+1][y+1]==greg){
            predators++;
        }
    }catch(ArrayIndexOutOfBoundsException e){
    }

        //Red -> 0, Blue -> 1, Green -> 2
        switch ((int) current[x][y]) {
            case 0 -> {
                //predators = lookAround(x, y, 1);
                //System.out.println(predators);
                if (predators >= nreOfNeededPredator) {
                    this.nextFrame[x][y] = 1;
                }
            }
            case 1 -> {
                // predators = lookAround(x, y, 2);
                if (predators >=nreOfNeededPredator) {
                    this.nextFrame[x][y] = 2;
                }
            }
            case 2 -> {
                // predators = lookAround(x, y, 0);
                if (predators >= nreOfNeededPredator) {
                    this.nextFrame[x][y] = 0;
                }
            }
            default -> {
            }
        }

    }

    public void paintTheCanvas(int x, int y) {
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                colorCell(i, j, colors[(int) (current[i][j])]);
            }
        }
    }

    public int lookAround(int x, int y, int predatorInt) {
        int c = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                // System.out.println((x+j-1)+", "+(y+i-1));
                //Catches < 3; j++) {
                if (i != 1 || j != 1) {
                    //Catches all the index that will be out of bound and ignore them
                    try {
                        if (current[x + j - 1][y + i - 1] == predatorInt) {
                            c++;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }

                }
            }
        }
        // System.out.println(c);

        return c;

    }

    @Override
    public void setPoint(int x, int y) {
        //Do nothing
    }
}
