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

    private final static Logger logger = LoggerFactory.getLogger(SimDiffusionLimitedAggregation.class);

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

    }

    private class Particle extends Point {

        public Particle(int x, int y) {
            super(x, y);
        }

    }

    private class Dendrite extends Point {

        private int age = 0;

        public Dendrite(int x, int y) {
            super(x, y);
            this.age = 0;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

    }

    private List<Point> entities = new ArrayList<Point>();
    private List<Dendrite> dendrites = new ArrayList<>();

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
            entities.clear();
            dendrites.clear();
            needToInitialize = false;
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
            this.current[(scaledX - 2) / 2][(scaledY - 2) / 2] = 3;
            colorCell((scaledX - 2) / 2, (scaledY - 2) / 2, Color.ORANGE);
            entities.add(new Dendrite((scaledX - 2) / 2, (scaledY - 2) / 2));
            dendrites.add(new Dendrite((scaledX - 2) / 2, (scaledY - 2) / 2));
        }
        checkNeighbours();
        move();
        for (Dendrite dendrite : dendrites) {
            colorCell(dendrite.getX(), dendrite.getY(), Color.ORANGE);
            dendrite.setAge(dendrite.getAge() + 1);
        }
    }

    private void checkNeighbours() {
        List<Point> neighbours = new ArrayList<Point>();
        for (Dendrite dendrite : dendrites) {
            if (dendrite.getAge() < 200) {
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
        }
        entities.removeAll(neighbours);
        for (Point neighbour : neighbours) {
            Dendrite newDendrite = new Dendrite(neighbour.getX(), neighbour.getY());
            if (!dendrites.contains(newDendrite)) {
                dendrites.add(new Dendrite(neighbour.getX(), neighbour.getY()));
            }
        }
        neighbours.clear();
    }

    private void move() {
        List<Point> moved = new ArrayList<>();
        for (Point particle : entities) {
            if (particle instanceof Particle) {
                int x = particle.getX();
                int y = particle.getY();
                colorCell(x, y, Color.BLACK);
                Random random = new Random();
                int direction = random.nextInt(8);
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
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        x--;
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
