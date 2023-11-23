/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.vanier.waveSim.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author edmon
 */
public class SimDiffusionLimitedAggregation extends CellularLogic {
    //Diffusion Limited Aggregation https://en.wikipedia.org/wiki/Diffusion-limited_aggregation
    //Code inspired from https://github.com/Computer-Kurzweil/computer_kurzweil

    private final static Logger logger = LoggerFactory.getLogger(ConwayGameOfLifeLogic.class);

    private boolean needToInitialize = true;

    //Point class to use to keep track of points
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

        //TODO return list of neighbouring points
        public List<Point> getDendriteNeighbours() {
            List<Point> neighbours = new ArrayList<Point>();
            for (Point point : entities) {
                if (!point.equals(this)) {
                    if ((point.getX() == this.getX() && point.getY() == this.getY()-1) || (point.getX() == this.getX() && point.getY() == this.getY()+1)) {
                        neighbours.add(point);
                    }
                }
            }
            return neighbours;
        }
    }

    private class Particle extends Point {

        public Particle(int x, int y) {
            super(x, y);
        }

    }

    private class Dendrite extends Point {

        public Dendrite(int x, int y) {
            super(x, y);
        }

    }

    private List<Point> entities = new ArrayList<Point>();

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
    public void simFrame() {
        //Initialize the simulation with particles and a root dendrite
        if (needToInitialize) {
            needToInitialize = false;
            for (int counterX = 1; counterX < scaledX - 1; counterX++) {
                for (int counterY = 1; counterY < scaledY - 1; counterY++) {
                    Random random = new Random();
                    int chance = random.nextInt(100);
                    if (chance >= 90) {
                        colorCell(counterX, counterY, Color.BLUE);
                        entities.add(new Particle(counterX, counterY));
                    } else {
                        colorCell(counterX, counterY, Color.BLACK);
                    }
                }
            }
            this.current[(scaledX - 2) / 2][(scaledY - 2) / 2] = 3;
            colorCell((scaledX - 2) / 2, (scaledY - 2) / 2, Color.ORANGE);
            entities.add(new Dendrite((scaledX - 2) / 2, (scaledY - 2) / 2));
        }
        //Check if particle becoming dendrite
        //particles.forEach((t) -> {
//            if(t.getType().equals(particles)){
//                List<Point> neighbours = t.getNeighbours();
//                for (Point neighbour : neighbours) {
//                    if(neighbour.getType().equals("dendrite")){
//                        particles.remove(t);
//                        this.current[t.getX()][t.getY()] = 3;
//                        colorCell(t.getX()/ 2, t.getX() / 2, Color.ORANGE);
//                    }
//                }
//                
//            }

        //});
        
        checkNeighbours();
        //move();
    }

    private void checkNeighbours() {
        for (Point point : entities) {
            if (point instanceof Dendrite) {
                List<Point> neighbours = point.getDendriteNeighbours();
                System.out.println(neighbours.size());
                for (Point neighbour : neighbours) {
                    if (neighbour instanceof Particle) {
                        entities.remove(neighbour);
                        entities.add(new Dendrite(neighbour.getX(), neighbour.getY()));
                        colorCell(neighbour.getX(), neighbour.getY(), Color.ORANGE);
                    }
                }
            }
        }
    }

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
                        y++;
                        break;
                    case 3:
                        x--;
                        break;
                }

                x %= (scaledX - 1);
                y %= (scaledY - 1);
                if (x == 0) {
                    x = scaledX - 2;
                }
                if (y == 0) {
                    y = scaledY - 2;
                }
                particle.setX(x);
                particle.setY(y);
                moved.add(particle);
                entities = moved;
                colorCell(x, y, Color.BLUE);
            }
        }
    }

    private boolean isParticle(int x, int y) {
        return true;
    }

    private boolean isDendrite(int x, int y) {
        return true;
    }
}
