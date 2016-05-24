package location;

/**
 * Created by Antonio on 21-03-2016.
 */
public class Place implements java.io.Serializable {
    /**
     * Name of the place
     */
    private String name;
    /**
     * x coordinate of the place
     */
    private double coord_x;
    /**
     * y coordinate of the place
     */
    private double coord_y;
    /**
     * number of people living in
     */
    private int citizens;
    /**
     * is there a court
     */
    private boolean court = false;
    /**
     * Price of building a court in this place
     */
    private double price = 1;

    /**
     * Constructor of class Place
     * @param name name of the place
     * @param coord_x coordinate x of the place
     * @param coord_y coordinate y of the place
     * @param population size of the population
     */
    public Place(String name, double coord_x, double coord_y, int population) {
        this.name = new String(name);
        this.coord_x = coord_x;
        this.coord_y = coord_y;
        this.citizens = population;
    }

    /**
     * Constructor of class Place
     * @param name name of the place
     * @param coord_x coordinate x of the place
     * @param coord_y coordinate y of the place
     * @param population size of the population
     * @param price price of the place
     */
    public Place(String name, double coord_x, double coord_y, int population, double price) {
        this.name = new String(name);
        this.coord_x = coord_x;
        this.coord_y = coord_y;
        this.citizens = population;
        this.price = price;
    }

    /**
     * Computes the distance between two given places
     * @param p place to compute distance
     * @return distance between two places
     */
    public double dist(Place p) {
        double distance = Math.sqrt(Math.pow(coord_x-p.coord_x,2) + Math.pow(coord_y-p.coord_y,2));

        return distance;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isCourt() {
        return court;
    }

    public String getName() {
        return name;
    }

    public double getCoord_x() {
        return coord_x;
    }

    public double getCoord_y() {
        return coord_y;
    }

    public int getCitizens() {
        return citizens;
    }

    public void setCourt(boolean court) {
        this.court = court;
    }

    public double getPrice() {
        return price;
    }
}
