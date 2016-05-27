package algorithms;

import javafx.util.Pair;
import location.Place;

import java.util.Comparator;
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
     * Number of courts to optimize its location
     */
    private int nCourts;
    /**
     * Budget to build the courts
     */
    private double budget;

    /**
     * Constructor of the heuristic
     * @param locations information about the locations
     * @param nCourts number of courts to optimize its location
     * @param dist maximum distance a citizen can be from the court
     */
    Heuristic(Vector<Place> locations, int nCourts, double dist) {
        this.locations = locations;
        maxDistance = dist;
        this.nCourts = nCourts;
    }

    /**
     * Constructor of the heuristic
     * @param locations information about the locations
     * @param price budget
     * @param dist maximum distance a citizen can be from the court
     */
    Heuristic(Vector<Place> locations, double price, double dist) {
        this.locations = locations;
        maxDistance = dist;
        this.nCourts = 0;
        this.budget = price;
    }

    /**
     * Computes the score of a given individual
     * @param individual individual to evaluate
     * @return individual's score
     */
    public int computeScore(Vector<Boolean> individual) {
        int score = 0;
        double price = 0;
        int avgCitizens = 0;

        for (int i = 0; i < individual.size(); i++) {
            if (!individual.get(i)) {
                double minimum = Double.MAX_VALUE;
                double maximum = -1;

                //computes the shortest distance to a court
                for (int j = 0; j < individual.size(); j++) {
                    if (i != j) {
                        if (individual.get(j)) {
                            minimum = Math.min(minimum, locations.get(i).dist(locations.get(j)));
                        } else {
                            maximum = Math.max(maximum, locations.get(i).dist(locations.get(j)));
                        }
                    }
                }
                if (minimum == Double.MAX_VALUE) {
                    minimum = maximum;
                }

                int avoid = 1;
                if (minimum > maxDistance) {
                    avoid = 2;
                }

                score -= (avoid * minimum * locations.get(i).getPrice() * locations.get(i).getCitizens());
            } else {
                price += locations.get(i).getPrice();
            }
            avgCitizens += locations.get(i).getCitizens();
        }

        avgCitizens /= locations.size();

        if (nCourts == 0 && price > budget) {
            score -= (avgCitizens * price);
        }

        return score;
    }

    /**
     * Returns the best between the given best so far and the population
     * @param population population of the algorithm
     * @return population scored and sorted
     */
    public Vector<Pair<Integer,Vector<Boolean> > > computeBestAndUpdateScores(Vector<Pair<Integer, Vector<Boolean>>> population) {

        for (int i = 0; i < population.size(); i++) {
            Vector<Boolean> individual = population.elementAt(i).getValue();
            population.set(i, new Pair(computeScore(individual), individual));
        }

        population.sort(new Comparator<Pair<Integer, Vector<Boolean>>>() {
            @Override
            public int compare(Pair<Integer, Vector<Boolean>> o1, Pair<Integer, Vector<Boolean>> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        return population;
    }

    /**
     * Compare an individual with the population
     * @param population population to compare
     * @param bestIndividual individual to compare
     * @return best individual
     */
    public Pair<Integer,Vector<Boolean>> getBest(Vector<Pair<Integer, Vector<Boolean>>> population, Pair<Integer, Vector<Boolean>> bestIndividual) {
        if (population.get(population.size()-1).getKey() > bestIndividual.getKey()) {
            return population.get(population.size()-1);
        }

        return bestIndividual;
    }

    /**
     * Restricts the number of courts to the acceptable values
     * @param bool individual
     * @return corrected individual
     */
    protected Vector<Boolean> correctNumberCourts(Vector<Boolean> bool) {

        if (nCourts == 0) {
            return bool;
        }

        Vector<Integer> positionsT = new Vector<Integer>();
        Vector<Integer> positionsF = new Vector<Integer>();
        for (int i = 0; i < bool.size(); i++) {
            if (bool.get(i)) {
                positionsT.add(i);
            } else {
                positionsF.add(i);
            }
        }

        while (positionsT.size() > nCourts) {
            int random = ((int) (Math.random()*10000)) % positionsT.size();
            bool.set(positionsT.get(random), false);
            positionsT.remove(random);
        }

        while (positionsT.size() < nCourts) {
            int random = ((int) (Math.random()*10000)) % positionsF.size();
            bool.set(positionsF.get(random), true);
            positionsT.add(positionsF.get(random));
            positionsF.remove(random);
        }

        return bool;
    }
}
