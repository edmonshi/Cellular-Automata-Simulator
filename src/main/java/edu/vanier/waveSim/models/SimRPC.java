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
    private int nreOfNeededPredator = 1;
    private int nreOfRandomPredator = 1;
    Color[] colors = {Color.ORANGE, Color.YELLOW, Color.BLACK, Color.BLUE, Color.PURPLE, Color.GREEN, Color.GRAY, Color.HOTPINK};
    private final static Logger logger = LoggerFactory.getLogger(SimRPC.class);
    private Random random = new Random();

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
                int color = (random.nextInt(nreOfDifferentEntities));
                current[i][j] = (float) color;
                colorCell(i, j, colors[color]);
            }
        }
    }

    /*
    To Modify for my 
    verify scaled x and y
     */
    @Override
    public void simFrame() {

        if (hasInitialized == false) {
            //InitializeRandomColor();
            hasInitialized = true;
        }

        for (int i = 0; i < scaledX; i++) {

            for (int j = 0; j < scaledY; j++) {
                devouredOrNot(i, j);
            }
        }

        paintTheCanvas(scaledX, scaledY);

        if (this.current.equals(this.nextFrame)) {
            System.out.println("the same");
        }
        float[][] temp = this.current;
        this.current = this.nextFrame;
        this.nextFrame = temp;
    }

    /**
     *
     * @param x
     * @param y
     */
    public void devouredOrNot(int x, int y) {

        int predatorStates = (nreOfDifferentEntities - 0) / 2;
        int[] predators = new int[predatorStates];
        int[] predatorsInt = new int[predatorStates];
        int nearPredators = 0;
        int predatorColorInt;

        //Count number if neighbour predators for each predator state
        for (int k = 0; k < predatorStates; k++) {

            predatorColorInt = (int) (getCellState(x, y) + 1 + k);
            if (predatorColorInt >= nreOfDifferentEntities) {
                predatorColorInt = predatorColorInt - nreOfDifferentEntities;
            }
            predatorsInt[k] = predatorColorInt;

            predators[k] = lookAround(x, y);
            nearPredators += predators[k];
        }
        int randomMinimum = random.nextInt(nreOfRandomPredator);

        //If there are more neighbour predators than the threshold, change current cell to a random predator cell (weighted)
        if (nearPredators >= nreOfNeededPredator + randomMinimum && nearPredators > 0) {
            int r = random.nextInt(nearPredators);
            int k = -1;
            while (r >= 0) {
                k++;
                r -= predators[k];
            }
            this.nextFrame[x][y] = (float) predatorsInt[k];
            //colorCell(x, y, colors[predatorsInt[k]]);

        }

    }

    public void paintTheCanvas(int x, int y) {
        for (int i = 0; i < x; i++) {

            for (int j = 0; j < y; j++) {
                colorCell(i, j, colors[(int) getCellState(i, j)]);
            }
        }
    }

    public int lookAround(int x, int y) {
        int c = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                //Catches < 3; j++) {
                if (i != 1 || j != 1) {
                    //Catches all the index that will be out of bound and ignore them
                    try {
                        if (getCellState(x + j - 1, y + i - 1) == getCellState(x, y)) {
                            c++;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                }
            }
        }
        return c;
    }

    public float getCellState(int x, int y) {
        return current[x][y];
    }
    @Override
    public void setPoint(int x, int y) {
        //Do nothing
    }
}
