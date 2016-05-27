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
        //parser.saveCities();

        parser.getCitiesByFile();

        parser.loadPrices();
        System.out.println(parser.toString());


        Vector<Place> places = parser.getCities();


        //GeneticAlgorithm(Vector<Place> locations, int nCourts, int bestToPass, int generationSize, int iterations, double dist, int pbMutation, int pbMarriage, double iterationsBeforeStop)
        GeneticAlgorithm ga = new GeneticAlgorithm(places, 5.0, 5, 20, 500, 0.5, 25, 75, 0.5);
        System.err.println();
        System.err.println();
        System.err.println("--------------------");
        System.err.println("Algoritmos Genéticos");
        System.err.println("--------------------");
        ga.compute();

        int best = ga.getBestScore();
        places = ga.getBestChoice();

        System.out.println(best);
        for (Place p : places) {
            System.out.println(p);
        }

        //SimulatedAnnealing(Vector<Place> locations, int nCourts, double initialTemperature, double delta, double dist, double iterationsBeforeStop)
        SimulatedAnnealing sa = new SimulatedAnnealing(places, 50, 50, 0.01, 1, 0.5);
        //sa.parseLocations();
        System.err.println();
        System.err.println();
        System.err.println("----------------------");
        System.err.println("Arrefecimento Simulado");
        System.err.println("----------------------");
        sa.compute();


        best = sa.getBestScore();
        places = sa.getBestChoice();

        System.out.println(best);
        for (Place p : places) {
            System.out.println(p);
        }
    }
}
