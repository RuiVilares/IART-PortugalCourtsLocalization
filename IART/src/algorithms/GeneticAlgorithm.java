package algorithms;

import javafx.util.Pair;
import location.Place;

import java.util.Vector;

/**
 * Created by Antonio on 21-03-2016.
 */
public class GeneticAlgorithm {
    /**
     * Maximum distance to the court
     */
    private double maxDistance;
    /**
     * Probability of mutation 0..100
     */
    private int pbMutation;
    /**
     * Probability of marriage 0..100
     */
    private int pbMarriage;
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
     * These are the elite individuals
     */
    private Vector<Pair<Integer,Vector<Boolean> > > elitist = new Vector<Pair<Integer,Vector<Boolean> > >();
    /**
     * The population is represented with an array of bytes
     */
    private Vector<Pair<Integer,Vector<Boolean> > > population = new Vector<Pair<Integer,Vector<Boolean> > >();
    /**
     * Best individual and its score
     */
    private Pair<Integer,Vector<Boolean> > bestIndividual = new Pair(Integer.MIN_VALUE, new Vector<Boolean>());
    /**
     * Heuristic of the algorithm
     */
    private Heuristic heuristic;

    /**
     * Constructor of non-elitist
     * @param locations information about the individuals
     * @param generationSize number of individuals per generation
     * @param iterations number of generations
     * @param dist maximum distance a citizen can be from the court
     * @param pbMutation probability of mutation
     * @param pbMarriage probability of marriage
     */
    public GeneticAlgorithm(Vector<Place> locations, int generationSize, int iterations, double dist, int pbMutation, int pbMarriage) {
        this.locations = locations;
        this.generationSize = generationSize;
        this.iterations = iterations;
        this.pbMutation = Math.abs(pbMutation);
        this.pbMarriage = Math.abs(pbMarriage);
        if (pbMutation > 100 || pbMarriage > 100) {
            System.err.println("Probabilities not valid");
        }
        maxDistance = dist;
        heuristic = new Heuristic(locations, dist);
        createInitialPopulation();
    }

    /**
     * Constructor of elitist
     * @param locations information about the individuals
     * @param bestToPass number of individuals that pass
     * @param generationSize number of individuals per generation
     * @param iterations number of generations
     * @param dist maximum distance a citizen can be from the court
     * @param pbMutation probability of mutation
     * @param pbMarriage probability of marriage
     */
    public GeneticAlgorithm(Vector<Place> locations, int bestToPass, int generationSize, int iterations, double dist, int pbMutation, int pbMarriage) {
        this(locations, generationSize, iterations, dist, pbMutation, pbMarriage);
        if (bestToPass > locations.size()) {
            System.err.println("Elitist error: best to pass bigger than the population");
        }
        this.bestToPass = bestToPass;
    }

    /**
     * Create an initial random population
     */
    private void createInitialPopulation() {
        for (int i = 0; i < generationSize; i++) {
            Vector<Boolean> individual = randomIndividual();
            population.add(new Pair(Integer.MIN_VALUE, individual));
        }

        population = heuristic.computeBestAndUpdateScores(population);
        bestIndividual = heuristic.getBest(population, bestIndividual);
    }

    /**
     * Create a random individual
     * @return random individual
     */
    private Vector<Boolean> randomIndividual() {
        Vector<Boolean> individual = new Vector<Boolean>();

        for (int i = 0; i < locations.size(); i++) {
            individual.add(((int) (Math.random()*10) % 2) == 0);
        }

        return individual;
    }

    /**
     * Computes a solution using genetic algorithm
     * Elite individuals pass directly to the next generation without
     * marriage and mutation
     */
    public void compute() {
        for (int i = 1; i <= iterations; i++) {
            selection();

            marriage();

            mutation();

            addEliteToPopulation();
            population = heuristic.computeBestAndUpdateScores(population);
            bestIndividual = heuristic.getBest(population, bestIndividual);
        }
    }

    /**
     * Add the elite to the rest of the population
     */
    private void addEliteToPopulation() {
        for (Pair<Integer,Vector<Boolean> > p : elitist) {
            population.add(p);
        }
    }

    /**
     * Mutate individuals
     */
    private void mutation() {
        int total = locations.size() * population.size();

        for (int i = 0; i < population.size(); i++) {
            for (int j = 0; j < locations.size(); j++) {
                if (pbMutation > (Math.random() * 100)) {
                    population.get(i).getValue().set(j, !population.get(i).getValue().get(j));
                }
            }
        }
    }

    /**
     * Marry individuals
     */
    private void marriage() {
        Vector<Pair<Integer,Vector<Boolean> > > newPopulation = new Vector<Pair<Integer,Vector<Boolean> > >();
        Vector<Pair<Integer,Vector<Boolean> > > marry = new Vector<Pair<Integer,Vector<Boolean> > >();

        for (int i = 0; i < population.size(); i++) {
            if (pbMarriage > (Math.random() * 100)) {
                marry.add(population.get(i));
            } else {
                newPopulation.add(population.get(i));
            }
        }

        if (marry.size() % 2 != 0) {
            newPopulation.add(marry.get(0));
            marry.remove(0);
        }

        for (int i = 0; i < marry.size(); i+=2) {
            int cut = (int) ((Math.random()*100) % locations.size());
            Pair<Integer, Vector<Boolean> > individualA = population.get(i);
            Pair<Integer, Vector<Boolean> > individualB = population.get(i+1);
            newPopulation.add(mix(individualA, individualB, cut));
            newPopulation.add(mix(individualB, individualA, cut));
        }

        population = newPopulation;
    }

    /**
     * Mix two individuals given a cut (the bit number)
     * @param individualA first individual
     * @param individualB second individual
     * @param cut bit number
     * @return new mixed individual
     */
    private Pair<Integer,Vector<Boolean>> mix(Pair<Integer, Vector<Boolean>> individualA, Pair<Integer, Vector<Boolean>> individualB, int cut) {
        Vector<Boolean> bool = new Vector<Boolean>();
        for (int i = 0; i < cut; i++) {
            bool.add(individualA.getValue().get(i));
        }
        for (int i = cut; i < locations.size(); i++) {
            bool.add(individualB.getValue().get(i));
        }

        return new Pair<Integer,Vector<Boolean>>(Integer.MIN_VALUE, bool);
    }

    /**
     * Select a new population
     */
    private void selection() {
        int scoreSum = 0;
        for (Pair<Integer,Vector<Boolean> > p : population) {
            scoreSum += p.getKey();
        }

        scoreSum = Math.abs(scoreSum);

        elitist = new Vector<Pair<Integer,Vector<Boolean> > >();

        //add best elements (elitist)
        for (int i = 1; i <= bestToPass; i++) {
            elitist.add(population.get(population.size()-i));
        }

        Vector<Pair<Integer,Vector<Boolean> > > newPopulation = new Vector<Pair<Integer,Vector<Boolean> > >();

        int diff = population.size() - elitist.size();
        //add the rest
        for (int i = 0; i < diff; i++) {
            newPopulation.add(getIndividual(Math.random() * 100, scoreSum));
        }

        population = newPopulation;
    }

    /**
     * Returns an individual given a probability
     * @param pb probability
     * @param sum sum of scores
     * @return selected individual
     */
    private Pair<Integer,Vector<Boolean>> getIndividual(double pb, int sum) {
        int acum = 0, individualPb = 0;
        for (Pair<Integer,Vector<Boolean> > p : population) {
            individualPb = Math.abs(p.getKey());
            acum += ((sum - individualPb) / sum);
            if (pb < acum) {
                return p;
            }
        }

        return population.get(population.size()-1);
    }

    public Vector<Boolean> getBestIndividual() {
        return bestIndividual.getValue();
    }

    public int getBestScore() {
        return bestIndividual.getKey();
    }

    /**
     * Get the best solution computed
     * @return best solution
     */
    public Vector<Place> getBestChoice() {
        for (int i = 0; i < locations.size(); i++) {
            locations.get(i).setCourt(bestIndividual.getValue().get(i));
        }

        return locations;
    }
}
