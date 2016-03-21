import algorithms.GeneticAlgorithm;
import location.Place;

import java.util.Vector;

/**
 * Created by Antonio on 21-03-2016.
 */
public class source {
    public static void main(String[] args) {
        Vector<Place> places = new Vector<Place>();
        //Place(String name, double coord_x, double coord_y, int population)
        places.add(new Place("Porto", 0, 0, 123));
        places.add(new Place("Lisboa", 1, 2, 234));
        places.add(new Place("Aveiro", 56, 86, 345));
        places.add(new Place("Setubal", 65, 23, 456));
        places.add(new Place("Leiria", 76, 43, 567));
        places.add(new Place("Viana do Castelo", 14, 25, 678));
        places.add(new Place("Algarve", 54, 23, 879));
        places.add(new Place("Alentejo", 12, 29, 890));
        places.add(new Place("FEUP", 61, 71, 901));

        //GeneticAlgorithm(Vector<Place> locations, int generationSize, int iterations, double dist, int pbMutation, int pbMarriage)
        //GeneticAlgorithm(Vector<Place> locations, int bestToPass, int generationSize, int iterations, double dist, int pbMutation, int pbMarriage)
        GeneticAlgorithm ga = new GeneticAlgorithm(places, 1, 5, 10, 100, 25, 75 );
        ga.compute();

        int best = ga.getBestScore();
        places = ga.getBestChoice();

        System.out.println(best);
        for (Place p : places) {
            System.out.println(p);
        }
    }
}
