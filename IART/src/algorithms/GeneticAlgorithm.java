package algorithms;

import javafx.util.Pair;
import location.Place;

import java.util.Vector;

/**
 * Created by Antonio on 21-03-2016.
 */
public class GeneticAlgorithm {
    /**
     * elitist choice
     */
    private boolean elitist = false;
    /**
     * if elitist, then this will represent the number that will pass
     */
    private int bestToPass = 0;
    /**
     * Number of individuals per generation
     */
    private int generationSize;
    /**
     * Number of generations to compute
     */
    private int iterations;
    /**
     * Information about the individuals. In this case, the locations
     */
    private Vector<Place> locations = new Vector<Place>();
    /**
     * The population is represented with an array of bytes
     */
    private Vector<Pair<Integer,Vector<Byte> > > population = new Vector<Pair<Integer,Vector<Byte> > >();
    /**
     * Best individual and its score
     */
    private Pair<Integer,Vector<Byte> > bestIndividual = new Pair(Integer.MIN_VALUE, new Vector<Byte>());
    /**
     * Heuristic of the algorithm
     */
    private Heuristic heuristic;

    /**
     * Constructor of non-elitist
     * @param locations information about the individuals
     * @param generationSize number of individuals per generation
     * @param iterations number of generations
     */
    GeneticAlgorithm(Vector<Place> locations, int generationSize, int iterations) {
        this.locations = locations;
        this.generationSize = generationSize;
        this.iterations = iterations;
        heuristic = new Heuristic(locations);
        createInitialPopulation();
    }

    /**
     * Constructor of elitist
     * @param locations information about the individuals
     * @param bestToPass number of individuals that pass
     * @param generationSize number of individuals per generation
     * @param iterations number of generations
     */
    GeneticAlgorithm(Vector<Place> locations, int bestToPass, int generationSize, int iterations) {
        this(locations, generationSize, iterations);
        if (bestToPass > locations.size()) {
            System.err.println("Elitist error: best to pass bigger than the population");
            System.exit(-1);
        }
        this.bestToPass = bestToPass;
        elitist = true;
    }

    /**
     * Create an initial random population
     */
    private void createInitialPopulation() {
        for (int i = 0; i < generationSize; i++) {
            Vector<Byte> individual = randomIndividual();
            int score = heuristic.computeScore(individual);
            population.add(new Pair(score, individual));
        }
    }

    /**
     * Create a random individual
     * @return random individual
     */
    private Vector<Byte> randomIndividual() {
        Vector<Byte> individual = new Vector<Byte>();

        for (int i = 0; i < locations.size(); i++) {
            individual.add((byte) (Math.random()%2));
        }

        return individual;
    }

    /**
     * Computes a solution using genetic algorithm
     */
    public void compute() {
        for (int i = 1; i <= iterations; i++) {
            //selecao
            //emparalhamento
            //mutacao
            //avaliacao
        }
    }

    public Vector<Byte> getBestIndividual() {
        return bestIndividual.getValue();
    }

    public int getBestScore() {
        return bestIndividual.getKey();
    }
}
