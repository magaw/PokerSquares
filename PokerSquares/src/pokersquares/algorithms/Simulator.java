package pokersquares.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import pokersquares.config.Settings;
import static pokersquares.config.Settings.Environment.system;
import pokersquares.environment.Board;
import pokersquares.environment.Card;
import pokersquares.environment.PokerSquares;
import pokersquares.players.BeemoV2;

public class Simulator {
    public class Gamer extends Thread {
        private final Board board;
        private final int offset;
        private int numSimulations;
        private double totalScore = 0;
        private int simsRun = 0;
        
        public Gamer(Board board, int numSimulations, int offset){
            this.board = board;
            this.numSimulations = numSimulations + offset;
            this.offset = offset;
        }
        
        @Override
        public void run() {
            //Random r = new Random(Settings.Main.seed + offset);
            Random r = new Random();
            
            /*
            Settings.Training.train = false;
            PokerSquares ps = new PokerSquares(new BeemoV2(), system);
            ps.verboseScores = false;
            ps.playSequence(numSimulations, offset, Settings.Main.verbose);
            ps.verboseScores = true;
            totalScore = ps.getScoreMean() * numSimulations;
            simsRun = numSimulations;
            */
            
            //PLAY Game
            while(numSimulations-- > offset){
                //SHUFFLE deck
                Stack<Card> deck = new Stack<Card>();
                for (Card card : Card.getAllCards())
                        deck.push(card);
                Collections.shuffle(deck, r);
                
                Board b = new Board(board);
                while (b.getTurn() < 25) {
                    Card c = deck.pop();
                    b.removeCard(c);
                    int[] p = Settings.Algorithms.simAlgorithm.search(c, b, millisRemaining);
                    b.playCard(c, p);
                }
                simsRun++;
                totalScore += Settings.Environment.system.getScore(b.getGrid());
            } 
                    
       }
    }
    
    private final Board board;
    private final int numSimulations, variator;
    private final long millisRemaining;
    
    public int simsRun = 0;
    public int numThreads = 0;
    private double totalScore = 0;
    public final List<Board> allBoards = new ArrayList();
    public final List<Board> allFinalBoards = new ArrayList();
    
    public Simulator(Board tb, int numSimulations, long millisRemaining, int variator, boolean genBoards){
        this.numThreads = Runtime.getRuntime().availableProcessors(); //Set num threads to num cores
        this.board = tb;
        this.numSimulations = numSimulations;
        this.millisRemaining = millisRemaining;
        this.variator = variator;
    }
    
    public void run(){
        //Number of threads used is 16 right now...
        Gamer[] gamers = new Gamer[this.numThreads];
        //Gamer[] gamers = new Gamer[1];
        int simPerThread = numSimulations/gamers.length;
        int extraThread = numSimulations - (simPerThread / gamers.length);
        
        for(int i = 0; i < gamers.length; ++i){
            gamers[i] = new Gamer(board, (i < extraThread) ? simPerThread : simPerThread + 1, (i * simPerThread) + variator);
            gamers[i].start();
        }
        
        totalScore = 0;
        
        for(int i = 0; i < gamers.length; ++i){
            try{
                gamers[i].join();
                totalScore += gamers[i].totalScore;
                simsRun += gamers[i].simsRun;
            }catch(InterruptedException ex){System.err.println("Simulator Interrupted!");}
        }
    }
    
    public static double simulate(Board tb, int numSimulations, long millisRemaining, int variator){
        
        Simulator sim = new Simulator(tb, numSimulations, millisRemaining, variator, false);
        sim.run();
        //System.out.println(sim.totalScore + " " + sim.simsRun);
        return sim.totalScore / sim.simsRun;
    }
}
