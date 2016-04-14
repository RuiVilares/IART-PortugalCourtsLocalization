package algorithms;

import javafx.util.Pair;
import location.Place;

import java.util.Vector;

/**
 * Created by Antonio on 21-03-2016.
 */
public class SimulatedAnnealing {
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
     * @param initialTemperature initial temperature
     * @param delta decrease of temperature
     * @param dist maximum distance recommended to the court
     */
    public SimulatedAnnealing(Vector<Place> locations, double initialTemperature, double delta, double dist) {
        this.locations = locations;
        this.delta = delta;
        this.finalTemperature = initialTemperature;
        maxDistance = dist;
        heuristic = new Heuristic(locations, dist);
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

        return individual;
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
                individual = new Pair<Integer,Vector<Boolean> >
                        (heuristic.computeScore(individual.getValue()), individual.getValue());
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

        Pair<Integer,Vector<Boolean> > bestInd = bestIndividual;

        //to the infinite and beyond
        for (double temperature = finalTemperature; temperature > 1; temperature -= delta) {
            Pair<Integer,Vector<Boolean> > individual = random(bestIndividual);

            int diff = individual.getKey() - bestIndividual.getKey();

            if (bestInd.getKey() < individual.getKey()) {
                bestInd = individual;
            }

            if (diff > 0 || Math.exp(diff / temperature) > Math.random()) {
                bestIndividual = individual;
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
