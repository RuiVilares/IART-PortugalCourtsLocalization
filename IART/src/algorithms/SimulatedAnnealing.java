package algorithms;

import javafx.util.Pair;
import location.Place;

import java.util.Vector;

/**
 * Created by Antonio on 21-03-2016.
 */
public class SimulatedAnnealing {
    /**
     * Percentage of iterations allowed with the same best individual
     */
    private double iterationsBeforeStop = 0.2;
    /**
     * Maximum distance to the court
     */
    private double maxDistance;
    /**
     * Final temperature for the algorithm
     */
    private double finalTemperature;
    /**
     * Velocity of the temperature decreasing
     */
    private double delta;
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
     * Best individual and its score
     */
    private Pair<Integer,Vector<Boolean> > bestIndividual = null;
    /**
     * Heuristic of the algorithm
     */
    private Heuristic heuristic;

    /**
     * Constructor of Simulated Annealing
     * @param locations vector of places
     * @param nCourts number of courts to optimize its location
     * @param initialTemperature initial temperature
     * @param delta decrease of temperature
     * @param dist maximum distance recommended to the court
     * @param iterationsBeforeStop Percentage of iterations allowed with the same best individual
     */
    public SimulatedAnnealing(Vector<Place> locations, int nCourts, double initialTemperature, double delta, double dist, double iterationsBeforeStop) {
        this.locations = locations;
        this.delta = delta;
        this.finalTemperature = initialTemperature;
        this.nCourts = nCourts;
        this.iterationsBeforeStop = iterationsBeforeStop;
        maxDistance = dist;
        heuristic = new Heuristic(locations, nCourts, dist);
    }

    /**
     * Constructor of Simulated Annealing
     * @param locations vector of places
     * @param price budget
     * @param initialTemperature initial temperature
     * @param delta decrease of temperature
     * @param dist maximum distance recommended to the court
     * @param iterationsBeforeStop Percentage of iterations allowed with the same best individual
     */
    public SimulatedAnnealing(Vector<Place> locations, double price, double initialTemperature, double delta, double dist, double iterationsBeforeStop) {
        this(locations, 0, initialTemperature, delta, dist, iterationsBeforeStop);
        this.budget = price;
        heuristic = new Heuristic(locations, price, dist);
    }

    /**
     * This method is to used with genetic algorithms
     */
    public void parseLocations() {
        bestIndividual = new Pair(Integer.MIN_VALUE, new Vector<Boolean>());
        for (int i = 0; i < locations.size(); i++) {
            bestIndividual.getValue().add(locations.get(i).isCourt());
        }
        bestIndividual = new Pair<Integer,Vector<Boolean> >
                (heuristic.computeScore(bestIndividual.getValue()), bestIndividual.getValue());
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

        if (nCourts == 0) {
            return individual;
        }

        return heuristic.correctNumberCourts(individual);
    }

    /**
     * Create a random individual based on the given one
     * @param individual individual to 'mutate'
     * @return new similar individual
     */
    private Pair<Integer,Vector<Boolean> > random(Pair<Integer,Vector<Boolean> > individual) {
        int i = 0;
        while (true) {
            if (Math.random()*100 < 50) {
                individual.getValue().set(i, !individual.getValue().get(i));
                if (nCourts > 0) {
                    individual = new Pair<Integer,Vector<Boolean> >
                            (heuristic.computeScore(individual.getValue()), heuristic.correctNumberCourts(individual.getValue()));
                }
                return individual;
            }
            i++;
            i %= individual.getValue().size();
        }
    }

    /**
     * Compute a solution using simulated annealing
     */
    public void compute() {
        if (bestIndividual == null) {
            Vector<Boolean> v = randomIndividual();
            bestIndividual = new Pair<Integer,Vector<Boolean> >(heuristic.computeScore(v), v);
        }

        int bestScore = Integer.MIN_VALUE;
        int totalIterations = (int) ((finalTemperature / delta) * iterationsBeforeStop);

        Pair<Integer,Vector<Boolean> > bestInd = bestIndividual;

        //to the infinite and beyond
        for (double temperature = finalTemperature, stop = totalIterations; temperature > 1 && stop > 0; temperature -= delta, stop--) {
            Pair<Integer,Vector<Boolean> > individual = random(bestIndividual);

            int diff = individual.getKey() - bestIndividual.getKey();

            if (bestInd.getKey() < individual.getKey()) {
                bestInd = individual;
            }

            if (diff > 0 || Math.exp(diff / temperature) > Math.random()) {
                bestIndividual = individual;
            }

            if (bestScore < bestIndividual.getKey()) {
                stop = totalIterations;
                bestScore = bestIndividual.getKey();
            }
        }

        if (bestInd.getKey() > bestIndividual.getKey()) {
            bestIndividual = bestInd;
        }
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
