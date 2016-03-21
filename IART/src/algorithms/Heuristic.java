package algorithms;

import javafx.util.Pair;
import location.Place;

import java.util.Vector;

/**
 * Created by Antonio on 21-03-2016.
 */
public class Heuristic {
    /**
     * Information about the individuals. In this case, the locations
     */
    private Vector<Place> locations = new Vector<Place>();
    /**
     * Maximum distance to the court
     */
    private double maxDistance;
    /**
     * Average number of citizens
     */
    private int avgCitizens;

    /**
     * Constructor of the heuristic
     * @param locations information about the locations
     * @param dist maximum distance a citizen can be from the court
     */
    Heuristic(Vector<Place> locations, double dist) {
        this.locations = locations;
        maxDistance = dist;
        for (Place p : locations) {
            avgCitizens += p.getCitizens();
        }
        avgCitizens /= locations.size();
    }

    /**
     * Computes the score of a given individual
     * @param individual individual to evaluate
     * @return individual's score
     */
    public int computeScore(Vector<Boolean> individual) {
        int score = 0;

        //minimize number of courts -> ncourt * avgCitizens
        score += minimizeNumberOfCourts(individual);

        //maximize number of citizens in courts -> where no court do ncitizens
        score += maximizeCitizensInCourts(individual);

        //minimize distance -> dist * citizens
        //avoid distances that exceed the maximum -> if dist > max then dist * citizens
        score += minimizeDistance(individual);

        return score;
    }

    /**
     * Minimize distance
     * Avoid distances that exceed the maximum
     * @param individual individual to evaluate
     * @return penalty (negative)
     */
    private int minimizeDistance(Vector<Boolean> individual) {
        int score = 0;

        for (int i = 0; i < individual.size(); i++) {
            if (!individual.get(i)) {
                double minimum = Double.MAX_VALUE;

                for (int j = 0; j < individual.size(); j++) {
                    if (i != j && individual.get(j)) {
                        minimum = Math.min(minimum, locations.get(i).dist(locations.get(j)));
                    }
                }

                int avoid = 1;
                if (minimum > maxDistance) {
                    avoid = 2;
                }
                score -= (avoid * minimum * locations.get(i).getCitizens());
            }
        }

        return score;
    }

    /**
     * Maximize number of citizens in courts
     * @param individual individual to evaluate
     * @return penalty (negative)
     */
    private int maximizeCitizensInCourts(Vector<Boolean> individual) {
        int score = 0;

        for (int i = 0; i < individual.size(); i++) {
            if (!individual.get(i)) {
                score -= locations.get(i).getCitizens();
            }
        }

        return score;
    }

    /**
     * Minimize number of courts
     * @param individual individual to evaluate
     * @return penalty (negative)
     */
    private int minimizeNumberOfCourts(Vector<Boolean> individual) {
        int score = 0;

        for (Boolean bool : individual) {
            if (bool) {
                score--;
            }
        }

        score *= avgCitizens;

        return score;
    }

    /**
     * Returns the best between the given best so far and the population
     * @param population population of the algorithm
     * @param bestIndividual best individual so far
     */
    public void computeBestAndUpdateScores(Vector<Pair<Integer, Vector<Boolean>>> population, Pair<Integer, Vector<Boolean>> bestIndividual) {
        for (int i = 0; i < population.size(); i++) {
            Vector<Boolean> individual = population.elementAt(i).getValue();
            population.set(i, new Pair(computeScore(individual), individual));
            if (population.get(i).getKey() > bestIndividual.getKey()) {
                bestIndividual = new Pair(population.get(i).getKey(), population.get(i).getValue().clone());
            }
        }
    }
}
