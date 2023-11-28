package edu.vanier.waveSim.tests;

import edu.vanier.waveSim.models.PerlinNoise;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * 
 */
public class PerlinTest {
    static BufferedImage image = new BufferedImage(500,500, BufferedImage.TYPE_INT_RGB);
    static File folder = new File("");
    static String location = System.getProperty("user.dir");
    
        
    public static void main(String[] args) {
        PerlinNoise perlin = new PerlinNoise();
        System.out.println(location);
        for (int i = 0; i<500;i++){
            for (int j = 0; j<500;j++){
                int pixColor = new java.awt.Color(0,0,(int) Math.sqrt((perlin.noise(i, j)*200)*(perlin.noise(i, j)*200))).getRGB();
                System.out.println(perlin.noise(i, j));
                image.setRGB(i,j, pixColor);
            }
        }
        File imgOut = new File(location+"/"+"test.bmp");
        try {
            ImageIO.write(image, "BMP", imgOut);
        } catch (IOException ex) {
            Logger.getLogger(PerlinTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
