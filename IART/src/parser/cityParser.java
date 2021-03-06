package parser;

import javafx.util.Pair;
import location.Place;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;

/**
 * Created by Rui on 10/04/2016.
 */


/**
 * Get the name, population, x_coord and y_coord of each Portuguese city.
 */
public class cityParser {

    /**
     * The URL to get the city names and populations.
     */
    private final String citiesURL = "https://pt.wikipedia.org/wiki/Lista_de_municípios_de_Portugal_por_população";

    /**
     * File that saves the cities.
     */
    private final String fileName = "cities.sav";

    /**
     * Vector with all the cities.
     */
    Vector<Place> places = new Vector<Place>();

    /**
     * HTML page with city name and populations.
     */
    private String namePopHTML;

    /**
     * Returns all the cities saved.
     * @return Vector with all the information.
     */
    public Vector<Place> getCities(){
        return places;
    }

    /**
     * Get all information for all cities through the internet.
     */
    public void getCitiesByWeb() throws IOException{
        extractNamePopHtml();
        parseCities();
    }

    /**
     * Get all information for all cities through the file.
     */
    public void getCitiesByFile() {
        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            places = (Vector<Place>) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse city information throught HTML page and HTTP requests
     */
    private void parseCities() throws IOException {
        Document doc = Jsoup.parse(namePopHTML);
        Elements citiesHtml = doc.getElementsByAttributeValue("style", "text-align: center;");

        for (Element aCitiesHtml : citiesHtml) {
            String name = aCitiesHtml.children().get(1).text();
            String population = aCitiesHtml.children().get(2).text();

            Pair<Double,Double> coords = getLatLong(name);
            if (coords != null){
                places.add(new Place(name, coords.getKey(), coords.getValue(), parsePopulation(population)));
            }
        }
    }

    /**
     * Convert string population number into integer.
     * @param population String with population number
     * @return int with population number.
     */
    private int parsePopulation(String population){
        String ret = "";
        for(int i = 0; i < population.length(); ++i)
            if(Character.isDigit(population.charAt(i)))
                ret+=population.charAt(i);
        return Integer.parseInt(ret);
    }

    /**
     * Save HTML page in namePopHTML String
     */
    private void extractNamePopHtml() {
        try {
            URL url = new URL(citiesURL);
            InputStream is = (InputStream)url.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine()) != null)
                sb.append(line);
            namePopHTML = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns all the cities saved.
     * @param targetURL String with the URL to do the request
     * @param urlParameters Optional parameters to the httprequest
     * @return String with the result
     */
    private static String excuteGet(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;
        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
            String line;
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Get real latitude and longitude
     * @param name City name to search
     * @return Pair with the <x, y> coords
     */
    private Pair<Double,Double> getLatLong(String name) {
        String url = null;
        Pair<Double, Double> coords = null;
        try {
            url = "http://nominatim.openstreetmap.org/search?q=" + URLEncoder.encode(name, "UTF-8") + ",%20Portugal&format=json";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String json = excuteGet(url,"");

        JSONArray obj = new JSONArray(json);

        if(obj.length() != 0){
            JSONObject results = (JSONObject) obj.get(0);
            Double x = results.getDouble("lat");
            Double y = results.getDouble("lon");
            coords = new Pair<>(x, y);
        }
        return coords;
    }

    /**
     * Serializes the city information.
     */
    public void saveCities() {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(places);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPrices(){
        try (BufferedReader br = new BufferedReader(new FileReader("prices.txt"))) {
            String line;
            while (br.ready()) {
                line = br.readLine();
                String[] parts = line.split(" - ");
                String name = parts[0];
                int price = Integer.parseInt(parts[1]);
                setPrice(name, price);
            }
        } catch (IOException io){
            System.out.println(io);
        }
    }

    private void setPrice(String name, int price){
        for (int i = 0; i < places.size(); i++){
            if (name.equals(places.get(i).getName())){
                places.get(i).setPrice(price/100);
                break;
            }
        }
    }

    @Override
    public String toString() {
        String aux = "";
        for (int i = 0; i < places.size(); i++){
            aux += places.get(i).getName() + " " + places.get(i).getCitizens() + " " + places.get(i).getCoord_x() + " " + places.get(i).getCoord_y() + " " + places.get(i).getPrice() + "\n";
        }
        return aux;
    }

    public void javascriptFileConstructor(Vector<Place> places, String filename){
        //https://developers.google.com/maps/documentation/javascript/examples/marker-simple

        String javascript = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\">\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>Courts in Portugal</title>\n" +
                "    <style>\n" +
                "      html, body {\n" +
                "        height: 100%;\n" +
                "        margin: 0;\n" +
                "        padding: 0;\n" +
                "      }\n" +
                "      #map {\n" +
                "        height: 100%;\n" +
                "      }\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "   <div id=\"map\"></div>\n" +
                "   <script>\n" +
                "      function initMap() {\n" +
                "        var array = [\n";
        for (int i = 0; i < places.size(); i++){
            if(places.get(i).isCourt()){
                javascript +=
                        "          { name: '" +
                                places.get(i).getName() +
                                "', lat: " +
                                places.get(i).getCoord_x() +
                                " , lng: " +
                                places.get(i).getCoord_y() +
                                "},\n";
            }
        }
        javascript +=
                "        ];\n\n" +
                        "        var map = new google.maps.Map(document.getElementById('map'), {\n" +
                        "           zoom: 4,\n" +
                        "           center: array[0]\n" +
                        "        });\n" +
                        "\n" +
                        "       array.forEach(function(ele) {\n"  +
                        "           new google.maps.Marker({\n" +
                        "               position: ele,\n" +
                        "               map: map,\n" +
                        "               title: ele.name\n" +
                        "           })});\n" +
                        "       }\n" +
                        "   </script>\n" +
                        "   <script async defer\n" +
                        "       src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyDbzBzA_844mIB43D_gqph92jB8Q4-nUUQ&callback=initMap\">\n" +
                        "   </script>\n" +
                        "  </body>\n" +
                        "</html>";
        try {
            FileOutputStream out = new FileOutputStream(new File(filename));
            out.write(javascript.getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
