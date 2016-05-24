package algorithms;

import javafx.util.Pair;
import location.Place;

import java.util.Vector;

/**
 * Created by Antonio on 21-03-2016.
 */
public class GeneticAlgorithm {
    /**
     * Percentage of iterations allowed with the same best individual
     */
    private double iterationsBeforeStop = 0.2;
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
     * Number of courts to optimize its location
     */
    private int nCourts;
    /**
     * Budget to build the courts
     */
    private double budget;
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
     * @param nCourts number of courts to optimize its location
     * @param generationSize number of individuals per generation
     * @param iterations number of generations
     * @param dist maximum distance a citizen can be from the court
     * @param pbMutation probability of mutation
     * @param pbMarriage probability of marriage
     * @param iterationsBeforeStop Percentage of iterations allowed with the same best individual
     */
    public GeneticAlgorithm(Vector<Place> locations, int nCourts, int generationSize, int iterations, double dist, int pbMutation, int pbMarriage, double iterationsBeforeStop) {
        this.locations = locations;
        this.generationSize = generationSize;
        this.iterations = iterations;
        this.pbMutation = Math.abs(pbMutation);
        this.pbMarriage = Math.abs(pbMarriage);
        this.iterationsBeforeStop = iterationsBeforeStop;
        this.nCourts = nCourts;
        if (pbMutation > 100 || pbMarriage > 100) {
            System.err.println("Probabilities not valid");
        }
        maxDistance = dist;
        heuristic = new Heuristic(locations, nCourts, dist);
        createInitialPopulation();
    }

    /**
     * Constructor of elitist
     * @param locations information about the individuals
     * @param nCourts number of courts to optimize its location
     * @param bestToPass number of individuals that pass
     * @param generationSize number of individuals per generation
     * @param iterations number of generations
     * @param dist maximum distance a citizen can be from the court
     * @param pbMutation probability of mutation
     * @param pbMarriage probability of marriage
     * @param iterationsBeforeStop Percentage of iterations allowed with the same best individual
     */
    public GeneticAlgorithm(Vector<Place> locations, int nCourts, int bestToPass, int generationSize, int iterations, double dist, int pbMutation, int pbMarriage, double iterationsBeforeStop) {
        this(locations, nCourts, generationSize, iterations, dist, pbMutation, pbMarriage, iterationsBeforeStop);
        if (bestToPass > locations.size()) {
            System.err.println("Elitist error: best to pass bigger than the population");
        }
        this.bestToPass = bestToPass;
    }

    /**
     * Constructor with budget
     * @param locations information about the individuals
     * @param price budget
     * @param bestToPass number of individuals that pass
     * @param generationSize number of individuals per generation
     * @param iterations number of generations
     * @param dist maximum distance a citizen can be from the court
     * @param pbMutation probability of mutation
     * @param pbMarriage probability of marriage
     * @param iterationsBeforeStop Percentage of iterations allowed with the same best individual
     */
    public GeneticAlgorithm(Vector<Place> locations, double price, int bestToPass, int generationSize, int iterations, double dist, int pbMutation, int pbMarriage, double iterationsBeforeStop) {
        this(locations, 0, generationSize, iterations, dist, pbMutation, pbMarriage, iterationsBeforeStop);
        this.bestToPass = bestToPass;
        this.budget = price;
        heuristic = new Heuristic(locations, price, dist);
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

        if (nCourts == 0) {
            for (int i = 0; i < locations.size(); i++) {
                individual.add(((int) (Math.random() * 10) % 2) == 0);
            }
        } else {
            for (int i = 0; i < locations.size(); i++) {
                individual.add(false);
            }
            int courts = nCourts;
            while (courts > 0) {
                int random = ((int) (Math.random() * 10000)) % individual.size();
                if (!individual.get(random)) {
                    individual.set(random, true);
                    courts--;
                }
            }
        }

        return individual;
    }

    /**
     * Computes a solution using genetic algorithm
     * Elite individuals pass directly to the next generation without
     * marriage and mutation
     */
    public void compute() {

        int bestScore = Integer.MIN_VALUE;
        int totalIterations = (int) (iterations * iterationsBeforeStop);

        for (int i = 1, stop = totalIterations; i <= iterations && stop > 0; i++, stop--) {
            selection();

            marriage();

            mutation();

            addEliteToPopulation();
            population = heuristic.computeBestAndUpdateScores(population);
            bestIndividual = heuristic.getBest(population, bestIndividual);

            if (bestScore < bestIndividual.getKey()) {
                stop = totalIterations;
                bestScore = bestIndividual.getKey();
            }
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

        for (int i = 0; i < population.size(); i++) {
            for (int j = 0; j < locations.size(); j++) {
                if (pbMutation > (Math.random() * 100)) {
                    population.get(i).getValue().set(j, !population.get(i).getValue().get(j));
                }
            }
        }

        if (nCourts > 0) {
            for (int i = 0; i < population.size(); i++) {
                population.set(i, new Pair<Integer, Vector<Boolean>>(Integer.MIN_VALUE, heuristic.correctNumberCourts(population.get(i).getValue())));
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

        if (this.nCourts == 0) {
            return new Pair<Integer, Vector<Boolean>>(Integer.MIN_VALUE, bool);
        }

        return new Pair<Integer, Vector<Boolean>>(Integer.MIN_VALUE, heuristic.correctNumberCourts(bool));
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
