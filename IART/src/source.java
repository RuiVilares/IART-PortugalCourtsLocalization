import algorithms.GeneticAlgorithm;
import algorithms.SimulatedAnnealing;
import javafx.util.Pair;
import location.Place;

import parser.cityParser;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by Antonio on 21-03-2016.
 */
public class source {
    public static void main(String[] args) throws IOException {

        cityParser parser = new cityParser();
        //parser.getCitiesByWeb();

        parser.getCitiesByFile();

        System.out.println(parser.toString());

        //Vector<Place> places = parser.getCities();

        /*

        //GeneticAlgorithm(Vector<Place> locations, int generationSize, int iterations, double dist, int pbMutation, int pbMarriage)
        //GeneticAlgorithm(Vector<Place> locations, int bestToPass, int generationSize, int iterations, double dist, int pbMutation, int pbMarriage)
        GeneticAlgorithm ga = new GeneticAlgorithm(places, 5, 50, 100, 100, 25, 75 );
        ga.compute();

        int best = ga.getBestScore();
        places = ga.getBestChoice();

        System.out.println(best);
        for (Place p : places) {
            System.out.println(p);
        }

        //SimulatedAnnealing(Vector<Place> locations, double delta, int dist)
        SimulatedAnnealing sa = new SimulatedAnnealing(places, 500, 1, 10);
        //sa.parseLocations();
        sa.compute();


        best = sa.getBestScore();
        places = sa.getBestChoice();

        System.out.println(best);
        for (Place p : places) {
            System.out.println(p);
        }

        */

    }
}
