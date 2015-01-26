package pokersquares.evaluations;

import java.util.Arrays;
import java.util.Map;
import pokersquares.config.Settings;
import static pokersquares.config.Settings.Evaluations.colHands;
import static pokersquares.config.Settings.Evaluations.rowHands;
import pokersquares.environment.Board;
import pokersquares.environment.Card;
import pokersquares.environment.Hand;

public class PatternPolicy {
    public static Map<Integer, Double> patternEvaluations = new java.util.HashMap();

    public static double evaluate(Board board){
        double evaluation = 0;
        for(int i = 0; i < 5; ++i){
            evaluation += evaluate(board.hands.get(i));
            evaluation += evaluate(board.hands.get(i+5));
        }
        return evaluation;
    }
    
    public static double evaluate(Hand hand) {
        //Generate rankCountCounts... What a weird place to put this.
        
        if(!hand.hasPattern()){
            for(int i = 0; i < Card.NUM_RANKS; ++i)
                ++hand.rankCountCounts[hand.rankCounts[i]];
            buildPattern(hand);
        }
        
        if (patternEvaluations.containsKey(hand.getPattern()))
            return patternEvaluations.get(hand.getPattern());
        else 
            return scoreHand(hand);
    }
    
    public static void buildPattern(Hand hand) {
        //[isCol][flushCapable][4xRank][4xRank][4xRank][4xRank][4xRank]
        hand.countRankCounts();
        
        int[] rankPattern = new int[5];
        
        String [] nums = {"0", "1", "2", "3", "4"};
            int iNums = 0;
            
            //Iterates through num CountCounts, 
            //recording a rank identifier for each rank
            
            if (hand.numCards > 0)
                //for the maximum number of rank multiples
                for (int i = 5; i > 0; --i) {
                    //for each rank multiple occuring at least once
                    //System.out.println(pattern + " " + rankCountCounts[i] + " " + nums[iNums] + "HERE");
                    if (hand.rankCountCounts[i] > 0) {
                        //for each occurance of a rank multiple
                        for (int j = 0; j < hand.rankCountCounts[i]; ++j) {
                            for (int k = 0; k < i; ++k) {
                                rankPattern[0] = 0; //change this
                            }
                            iNums++;
                        }
                    }
                }
        
        int pattern = (hand.isCol ? 2 : 0);
        pattern += (hand.numSuits <= 1 ? 1 : 0);
        //int pattern = 0; //Temp
        
        //Iterates through num CountCounts, 
        //recording a rank identifier for each rank
        //for the maximum number of rank multiples
        for (int i = 0; i < 5; ++i) {
            //for each rank multiple occuring at least once
            //System.out.println(pattern + " " + rankCountCounts[i] + " " + nums[iNums] + "HERE");
            pattern = pattern << 3;
            pattern += hand.rankCountCounts[i];
        }
        /*
>>>>>>> FETCH_HEAD
        int[] ranks = new int[5];
        for(int i = 0; i < 5; ++i){
            Card c = hand.getCard(i);
            if (c == null) continue;
            ranks[i] = c.getRank();
        }
        Arrays.sort(ranks);
        for(int i = 4; i >= 0; --i){
            pattern = pattern << 4;
            pattern += ranks[i];
        }*/
        hand.setPattern(pattern);
    }
    
    private static double scoreHand(Hand hand) {
        //Policy Scores should relate to probability, 
        //They are currently assigned by gut,
        //The probability shousld be calculated or else learned 
        if(hand.numCards == 0) return 0;
        
        double handScore = 0;
        
        
        
        if (hand.isCol) {
            for (int i = 0; i < colHands.length; ++i) {
                if (colHands[i] == 1) {
                    handScore += selectScorePolicy(hand, i);
                }
            }
        }else {
            for (int i = 0; i < rowHands.length; ++i) {
                if (rowHands[i] == 1) {
                    handScore += selectScorePolicy(hand, i);
                }
            }
        }
        
        patternEvaluations.put(hand.getPattern(), handScore);
        
        return handScore;
    }
    
    private static double selectScorePolicy(Hand hand, int i) {
        //Policy Value Score Product
        
        if (i == 0) //High Card
            return Settings.Evaluations.handScores[0] * scoreHighCardPolicy(hand);
        else if (i == 1)  //One Pair
            return Settings.Evaluations.handScores[1] * scorePairPolicy(hand);
        else if (i == 2) //Two Pair
            return Settings.Evaluations.handScores[2] * scoreTwoPairPolicy(hand);
        else if (i == 3) //Three of a Kind
            return Settings.Evaluations.handScores[3] * scoreThreeOfAKindPolicy(hand);
        else if (i == 4) //Straight
            return Settings.Evaluations.handScores[4] * scoreStraightPolicy(hand);
        else if (i == 5) //Flush
            return Settings.Evaluations.handScores[5] * scoreFlushPolicy(hand);
        else if (i == 6) //Full House
            return Settings.Evaluations.handScores[6] * scoreFullHousePolicy(hand);
        else if (i == 7) //Four of a Kind
            return Settings.Evaluations.handScores[7] * scoreFourOfAKindPolicy(hand);
        else if (i == 8) //Straight Flush
            return Settings.Evaluations.handScores[8] * scoreStraightFlushPolicy(hand);
        else if (i == 9) //Royal Flush
            return Settings.Evaluations.handScores[9] * scoreRoyalFlushPolicy(hand);
        else return 0;
        /*
        //Policy Value
        if (i == 0) //High Card
            return scoreHighCardPolicy(hand);
        else if (i == 1)  //One Pair
            return scorePairPolicy(hand);
        else if (i == 2) //Two Pair
            return scoreTwoPairPolicy(hand);
        else if (i == 3) //Three of a Kind
            return scoreThreeOfAKindPolicy(hand);
        else if (i == 4) //Straight
            return scoreStraightPolicy(hand);
        else if (i == 5) //Flush
            return scoreFlushPolicy(hand);
        else if (i == 6) //Full House
            return scoreFullHousePolicy(hand);
        else if (i == 7) //Four of a Kind
            return scoreFourOfAKindPolicy(hand);
        else if (i == 8) //Straight Flush
            return scoreStraightFlushPolicy(hand);
        else if (i == 9) //Royal Flush
            return scoreRoyalFlushPolicy(hand);
        else return 0;
                */
    }
    
    private static double scoreFullHandPolicy(Hand hand) {
        //
        
        if (hand.numCards != hand.numRanks) return 0;
            
        return 0;
    }
    
    private static double scoreHighCardPolicy(Hand hand) {
        //if there is no possibility of a high card
        if(hand.numCards != hand.numRanks) return 0;
        //if there is a high card
        if(hand.numCards == 5) return 1;
        //if there is a possibility of a high card
        return Settings.Evaluations.highCardPolicy[hand.numCards];
    }
    
    private static double scorePairPolicy(Hand hand) {
        //if there is a pair
        if (hand.rankCountCounts[2] == 1) {
            if(hand.numCards == 5 && (hand.rankCountCounts[3] == 1 || hand.numSuits == 1)) 
                return 0; //If we have a full house || flush
            return 1; //We have it!
        }
        //if there is a possibility of a pair
        if(hand.rankCountCounts[3] == 1 || hand.rankCountCounts[4] == 1 //We have three or four of a kind
                || hand.numCards == 5) return 0; //Or a full hand
        
        return Settings.Evaluations.pairPolicy[hand.numCards - 1];
    }
    
    private static double scoreTwoPairPolicy(Hand hand) {
        if(hand.rankCountCounts[3] == 1) return 0; //Can't get two pair when we have three of a kind.
        if(hand.rankCountCounts[2] == 2) return 1; //We already have it
        if(hand.numCards == 5) return 0; //Hand is full.
        if(hand.rankCountCounts[2] == 1) return Settings.Evaluations.twoPairPolicy[hand.numCards - 2]; //0, 1, 2, 3
        if(hand.numCards > 3) return 0;
        return Settings.Evaluations.twoPairPolicy[hand.numCards + 3]; //4, 5, 6
    }
    
    private static double scoreThreeOfAKindPolicy(Hand hand) {
        if(hand.rankCountCounts[3] == 1)
            if(hand.rankCountCounts[2] == 1) return 0; //Already have full house
            else return 1; //Have three of a kind
        if(hand.numCards == 5) return 0; //Hand is full
        if(hand.rankCountCounts[2] == 1) return Settings.Evaluations.threeOfAKindPolicy[hand.numCards - 2];
        if(hand.numCards > 3) return 0; //Can't make a three of a kind.
        return Settings.Evaluations.threeOfAKindPolicy[hand.numCards + 2];
    }
    
    private static double scoreStraightPolicy(Hand hand) {
        return 0;
    }
    
    private static double scoreFlushPolicy(Hand hand) {
        if(hand.numSuits > 1) return 0;
        if(hand.numCards == 5) return 1;
        return Settings.Evaluations.flushPolicy[hand.numCards];
    }
    
    private static double scoreFullHousePolicy(Hand hand) {
        if(hand.rankCountCounts[3] == 1){
            if(hand.rankCountCounts[2] == 1) return 1; //We has it.
            if(hand.numCards == 5) return 0; //We can't get it
            return Settings.Evaluations.fullHousePolicy[0];
        }
        if(hand.numCards == 5) return 0; //Hand is full
        if(hand.rankCountCounts[2] == 2) return Settings.Evaluations.fullHousePolicy[1];
        if(hand.rankCountCounts[2] == 1){
            if(hand.numCards > 3) return 0; //Can't get it
            return Settings.Evaluations.fullHousePolicy[hand.numCards];
        }
        if(hand.numCards > 2) return 0; //Can't get it
        return Settings.Evaluations.fullHousePolicy[hand.numCards + 3];
    }
    
    private static double scoreFourOfAKindPolicy(Hand hand) {
        if(hand.rankCountCounts[4] == 1) return 1;
        if(hand.numCards == 5) return 0;
        if(hand.rankCountCounts[3] == 1)
            return Settings.Evaluations.fourOfAKindPolicy[hand.numCards - 3]; //0-1
        if(hand.numCards > 3) return 0;
        if(hand.rankCountCounts[2] == 1)
            return Settings.Evaluations.fourOfAKindPolicy[hand.numCards]; //2-3
        if(hand.numCards > 2) return 0;
        return Settings.Evaluations.fourOfAKindPolicy[hand.numCards + 3];
    }
    
    private static double scoreStraightFlushPolicy(Hand hand) {
        double straightFlushScore = 0;
        
        //if there is a StraightFlush
        if (hand.hasStraight && (hand.numSuits == 1) && (hand.numCards == 5)) straightFlushScore = 1;
        
        return straightFlushScore;
    }
    
    private static double scoreRoyalFlushPolicy(Hand hand) {
        double royalFlushScore = 0;
        
        //if there is a StraightFlush
        if (hand.hasRoyal && (hand.numSuits == 1) && (hand.numCards == 5)) royalFlushScore = 1;
        
        return royalFlushScore;
    }
}
