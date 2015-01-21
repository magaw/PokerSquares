package pokersquares.config;

import pokersquares.algorithms.*;
import pokersquares.environment.*;
import java.util.Arrays;

public class Settings {
    //Holds all CONSTANTS for all classes for easy reference and tweaking
    public enum PointSystem {RANDOM, AMERICAN, BRITISH, HYPERCORNER, SINGLEHAND };
    
    public static class Main {
        public static int games = 1;
        public static int seed = 0;
        public static boolean verbose = false;
        public static int randomPointSystemSeed = 5;
        
        public static PointSystem pointSystem = PointSystem.AMERICAN;
        
        
    }
    
    public static class Environment {
        public static PokerSquaresPointSystem system;
    }
    
    public static class Algorithms {
        public static int searchDepth = 2;      //Currently unused.
        public static int simSampleSize = 1000; 
        public static int playSampleSize = 24;  //Max = 24, Min = 1
        public static int deckSampleMax = 52;   //Currently unused?
        
        public static boolean positionRankEnabled = false;
        
        public static Algorithm simAlgoritm = new OBF();
        public static Algorithm[] algorithm = 
                new Algorithm[] {new OBF(), new OBF(), new OBF()};
    }
    
    public static class BMO {
        public static int[] turnSplits = new int[]{5, 25, 25};
    }
    
    public static class Evaluations {
        
        //Pattern Policy
        public static String pattern = "A"; //A,B,C
        
        
        public static double[] handScores = new double[10];
        
        //array hold exponents for values of hands in american order
        //public static double[] exps = {1, 1, 1, 1, 1.2, 1.18, 0.95, 0.647, 1.1, 1.1};
        public static double[] exps;
        
        public static double[] pairPolicy;
        public static double[] twoPairPolicy;
        public static double[] threeOfAKindPolicy;
        public static double[] flushPolicy;
        public static double[] fullHousePolicy;
        public static double[] fourOfAKindPolicy;
        
        public static void debug () {
            System.out.println("Hand Scores: " + Arrays.toString(handScores));
            System.out.println("Exponents: " + Arrays.toString(exps));
            System.out.println("Pair Policy: " + Arrays.toString(pairPolicy));
            System.out.println("Two Pair Policy: " + Arrays.toString(twoPairPolicy));
            System.out.println("Three Of A Kind Policy : " + Arrays.toString(threeOfAKindPolicy));
            System.out.println("Flush Policy: " + Arrays.toString(flushPolicy));
            System.out.println("Full House Policy: " + Arrays.toString(fullHousePolicy));
            System.out.println("Four Of A Kind Policy: " + Arrays.toString(fourOfAKindPolicy));
        }
    }
}
