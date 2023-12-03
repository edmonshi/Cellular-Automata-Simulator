package edu.vanier.waveSim.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simulation of Diffusion-limited aggregation based on:
 * Wikimedia Foundation. (2021, July 3)
 * Computer-Kurzweil,  thomaswoehlke. (n.d.).
 * @author edmon
 */
public class SimDiffusionLimitedAggregation extends CellularLogic {

    private final static Logger logger = LoggerFactory.getLogger(SimDiffusionLimitedAggregation.class);

    /** 
     * Point class to use to keep track of points
     * This has subclasses so it is better if it stays private and is not merged with the other private point class in the main controller
     */
    private class Point {

        private int x;
        private int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

    }

    /**
     * Subclass of point for particles
     */
    private class Particle extends Point {

        public Particle(int x, int y) {
            super(x, y);
        }

    }

    /**
     * Subclass of point for dendrites
     */
    private class Dendrite extends Point {

        public Dendrite(int x, int y) {
            super(x, y);

        }

    }
    
    /**List of entities*/
    private List<Point> entities = new ArrayList<Point>();
    /**List of dendrites*/
    private List<Dendrite> dendrites = new ArrayList<>();

    /*Contructor to instantate the simulation*/
    public SimDiffusionLimitedAggregation(Canvas operatingCanvas, int widthX, int heightY, int scale) {
        super(operatingCanvas, widthX, heightY);
        // deal with scaling
        if (scale < 1 || scale % 2 != 0) {
            logger.info("scaling is not set, setting to 1 by default");
        } else {
            setScaling(scale);
        }
    }

    @Override
    /**
     * The main simulation method called every frame. Overrides from the superclass.
     */
    public void simFrame() {
        //Initialize the simulation with particles and a root dendrite
        if (!hasInitialized) {
            entities.clear();
            dendrites.clear();
            hasInitialized = true;
            // loops over the scaled width and height to see all cells
            for (int counterX = 1; counterX < scaledX - 1; counterX++) {
                for (int counterY = 1; counterY < scaledY - 1; counterY++) {
                    Random random = new Random();
                    int chance = random.nextInt(100);
                    if (chance >= 95) {
                        colorCell(counterX, counterY, Color.BLUE);
                        entities.add(new Particle(counterX, counterY));
                    } else {
                        colorCell(counterX, counterY, Color.BLACK);
                    }
                }
            }
            // color current cell
            this.current[(scaledX - 2) / 2][(scaledY - 2) / 2] = 3;
            colorCell((scaledX - 2) / 2, (scaledY - 2) / 2, Color.ORANGE);
            entities.add(new Dendrite((scaledX - 2) / 2, (scaledY - 2) / 2));
            dendrites.add(new Dendrite((scaledX - 2) / 2, (scaledY - 2) / 2));
        }
        checkNeighbours();
        move();
        // color all dendrites
        for (Dendrite dendrite : dendrites) {
            colorCell(dendrite.getX(), dendrite.getY(), Color.ORANGE);
        }
    }

    /**
     * Check neighboring points to update rules
     */
    private void checkNeighbours() {
        List<Point> neighbours = new ArrayList<Point>();
        for (Dendrite dendrite : dendrites) {

            for (Point particle : entities) {
                if (particle instanceof Particle) {
                    if ((particle.getX() == dendrite.getX() && particle.getY() == dendrite.getY() - 1) || (particle.getX() == dendrite.getX() && particle.getY() == dendrite.getY() + 1)) {
                        if (!neighbours.contains(particle)) {
                            neighbours.add(particle);
                        }
                        break;
                    } else if ((particle.getY() == dendrite.getY() && particle.getX() == dendrite.getX() - 1) || (particle.getY() == dendrite.getY() && particle.getX() == dendrite.getX() + 1)) {
                        if (!neighbours.contains(particle)) {
                            neighbours.add(particle);
                        }
                        break;
                    } else if ((particle.getX() == dendrite.getX() - 1 && particle.getY() == dendrite.getY() - 1) || (particle.getX() == dendrite.getX() - 1 && particle.getY() == dendrite.getY() + 1)) {
                        if (!neighbours.contains(particle)) {
                            neighbours.add(particle);
                        }
                        break;
                    } else if ((particle.getX() == dendrite.getX() + 1 && particle.getY() == dendrite.getY() - 1) || (particle.getX() == dendrite.getX() + 1 && particle.getY() == dendrite.getY() + 1)) {
                        if (!neighbours.contains(particle)) {
                            neighbours.add(particle);
                        }
                        break;
                    }
                }
            }

        }
        entities.removeAll(neighbours);
        // for all neibours, modify dendrites
        for (Point neighbour : neighbours) {
            Dendrite newDendrite = new Dendrite(neighbour.getX(), neighbour.getY());
            if (!dendrites.contains(newDendrite)) {
                dendrites.add(new Dendrite(neighbour.getX(), neighbour.getY()));
            }
        }
        neighbours.clear();
    }

    /** 
     * move random particles
     */
    private void move() {
        List<Point> moved = new ArrayList<>();
        for (Point particle : entities) {
            if (particle instanceof Particle) {
                int x = particle.getX();
                int y = particle.getY();
                colorCell(x, y, Color.BLACK);
                Random random = new Random();
                int direction = random.nextInt(4);
                switch (direction >= 0 ? direction : -direction) {
                    case 0:
                        y--;
                        break;
                    case 1:
                        x++;
                        break;
                    case 2:
                        
                        x--;
                        break;
                    case 3:
                        y++;
                        break;
                }

                if (x == 0) {
                    x = scaledX - 2;
                }
                if (y == 0) {
                    y = scaledY - 2;
                }
                if (x > scaledX - 2) {
                    x -= (scaledX - 2);
                }
                if (y > scaledY - 2) {
                    y -= (scaledY - 2);
                }
                particle.setX(x);
                particle.setY(y);
                moved.add(particle);
                colorCell(x, y, Color.BLUE);
            }
        }
        entities = moved;
        entities.addAll(dendrites);
    }

    private boolean isParticle(int x, int y) {
        return true;
    }

    private boolean isDendrite(int x, int y) {
        return true;
    }

}
