package edu.vanier.waveSim.tests;

import com.opencsv.exceptions.CsvException;
import java.io.IOException;
/**
 * @author Everyone
 */
public class LoadTest {
    public static void main(String[] args) throws IOException, CsvException {
            String[] settings = {"Illegal data that needs to be caught"};
            // Verify data from csv file, before parsing
            boolean fileValid = verifyFileSettingsModified(settings);
            if(fileValid)
                System.out.println("No errors detected. The file is valid");
            else
                System.out.println("The file is invalid.");
    }
        public static boolean verifyFileSettingsModified(String[] info) throws IOException, CsvException{
        if(info.length<15){
            System.out.println("The file does not contain the minimum amount of information required to load a simulation.");
            return false;
        }
            
        try{
            double dampVerification = Double.parseDouble(info[0]);
            // Make sure that damping is between the right numerical bounds
            // o.oo1 to 0.150
            if(dampVerification>0.150||dampVerification<0.001)
            {
                System.out.println("The first value is incorrect. The value of the damping should be between 0.001 and 0.150");
                return false;
            }
        }catch(Exception e){
            System.out.println("The first value should be a number corresponding to the value of the damping. However, it does not seem like a numerical value.");
            return false;
        }
        //Check scaling
        try{
            int scaleVerification = Integer.parseInt(info[1]);
            if(scaleVerification>8||scaleVerification<1){
                System.out.println("The scaling should be a number between 1 and 8. However, it seems to be out of bounds");
                return false;
            }
        }catch(Exception e){
            System.out.println("The second value should be an integer corresponding to the value of the scaling. However, it does seem like a number.");
            return false;
        }
        //Check simulation type
        String[] simulationTypes = {"Simple Ripple", "Conway's Game of Life", "Rock-Paper-Scissors", "Brian's Brain", "Forest Fire","Diffusion Limited Aggregation"};
        boolean isOneOfTypes = false;
        for(String element:simulationTypes)
            if(element.equals(info[2]))
                isOneOfTypes = true;
        if(isOneOfTypes==false){
            System.out.println("The third value, corresponding to the simulation type is invalid. Please try again.");
            return false;
        }
        //Check speed: Between 1 and 500
        try{
            double speedVerification = Double.parseDouble(info[3]);
            if(speedVerification>500||speedVerification<1){
                System.out.println("The fourth value, corresponding to the speed of the simulation should be a value between 1 and 500. However, the value in the file seems to be out of bound. Please try again.");
                return false;
            }
        }catch(Exception e){
            System.out.println("The fourth value inside the file, corresponding to the speed of the simulation is not a number. Please try again, using a valid file.");
            return false;
        }
        // Verify stage dimensions
        // Width
        try{
            double widthStage = Double.parseDouble(info[4]);
            if(widthStage<1){
                System.out.println("The value at the "+(5)+"th position should be a number corresponding to the width of the window. It should be bigger than 0. However, it is inferior to 1.");
            }
        }catch(Exception e){
            System.out.println("The value at the "+(5)+"th position should be a number corresponding to the width of the window. However, it does not look like a number.");
            return false;
        }
        //Height
        try{
            double heightStage = Double.parseDouble(info[5]);
            if(heightStage<1){
                System.out.println("The value at the "+(6)+"th position should be a number corresponding to the height of the window. It should be bigger than 0. However, it is inferior to 1.");
            }
        }catch(Exception e){
            System.out.println("The value at the "+(6)+"th position should be a number corresponding to the height of the window. However, it does not look like a number.");
            return false;
        }
        // Don't need to check for frame limits, because if something illegal is entered, then it just automatically goes to 'max'
        //Check how many points are in the file
        int numOfCoordinates = (info.length-15);
        if(numOfCoordinates%2==1){
            System.out.println("A coordinate is missing. Please try again, using a valid file.");
            return false;
        }
        return true;
    }
}
