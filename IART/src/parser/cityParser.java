package parser;

import javafx.util.Pair;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
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
public class cityParser {

    private final String citiesURL = "https://pt.wikipedia.org/wiki/Lista_de_municípios_de_Portugal_por_população";

    //http://nominatim.openstreetmap.org/search?q=Porto, Portugal&format=json

    Vector<Place> places = new Vector<Place>();

    private String namePopHTML;

    public Vector<Place> getCities() throws IOException{
        System.out.println("Extracting data from the Web...");
        extractNamePopHtml();
        parseCities();
        return places;
    }

    private void parseCities() throws IOException {
        Document doc = Jsoup.parse(namePopHTML);
        Elements citiesHtml = doc.getElementsByAttributeValue("style", "text-align: center;");

        for (Element aCitiesHtml : citiesHtml) {
            String name = aCitiesHtml.children().get(1).text();
            String population = aCitiesHtml.children().get(2).text();

            Pair<Double,Double> coords = getLatLong(name);

            places.add(new Place(name, coords.getKey(), coords.getValue(), parsePopulation(population)));
            System.out.println(places.get(places.size() - 1).getName() + " " + places.get(places.size() - 1).getCoord_x() + " " + places.get(places.size() - 1).getCoord_y() + " " + places.get(places.size() - 1).getCitizens());
        }

        System.out.println("Finished extracting city names and populations");
    }

    private int parsePopulation(String population){
        String ret = "";

        for(int i = 0; i < population.length(); ++i)
            if(Character.isDigit(population.charAt(i)))
                ret+=population.charAt(i);

        return Integer.parseInt(ret);
    }

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

    public static String excuteGet(String targetURL, String urlParameters) {
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


    private Pair<Double,Double> getLatLong(String name) {
        String url = null;
        String nameAux = name.replace("\\(([^)]+)\\)"," ");
        System.out.println(nameAux);
        try {
            url = "http://nominatim.openstreetmap.org/search?q=" + URLEncoder.encode(nameAux, "UTF-8") + ",%20Portugal&format=json";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String json = excuteGet(url,"");

        JSONArray obj = new JSONArray(json);

        JSONObject results = (JSONObject) obj.get(0);

        Double x = results.getDouble("lat");
        Double y = results.getDouble("lon");

        Pair<Double, Double> coords = new Pair<>(x, y);
        return coords;
    }

}
