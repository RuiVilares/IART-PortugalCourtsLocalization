package algorithms;

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
     * Constructor of the heuristic
     * @param locations information about the locations
     */
    Heuristic(Vector<Place> locations) {
        this.locations = locations;
    }

    /**
     * Computes the score of a given individual
     * @param individual individual to evaluate
     * @return individual's score
     */
    public int computeScore(Vector<Byte> individual) {
        return 0;
    }
}
