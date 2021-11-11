import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class GetCoordinates {
    private static ObjectMapper MAPPER = new ObjectMapper();

    public String longlat(String adress) throws IOException {
        URL myURL = new URL("https://nominatim.openstreetmap.org/search?q=" + "+пермь+район+" + adress + "&format=json&polygon=1&addressdetails=1");
        URLConnection connection = myURL.openConnection();
        InputStream response = connection.getInputStream();
        JsonNode array = MAPPER.readTree(response);
        JsonNode node = array.get(0);
        System.out.println(adress);
        return node.get("lon").asText() + "," + node.get("lat").asText();
    }
    public String latForRef(String adress) throws IOException {
        URL myURL = new URL("https://nominatim.openstreetmap.org/search?q=" + "+пермь+район+" + adress + "&format=json&polygon=1&addressdetails=1");
        URLConnection connection = myURL.openConnection();
        InputStream response = connection.getInputStream();
        JsonNode array = MAPPER.readTree(response);
        JsonNode node = array.get(0);
        return "-lat=" + node.get("lat").asText();
    }
    public String longForRef(String adress) throws IOException {
        URL myURL = new URL("https://nominatim.openstreetmap.org/search?q=" + "+пермь+район+" + adress + "&format=json&polygon=1&addressdetails=1");
        URLConnection connection = myURL.openConnection();
        InputStream response = connection.getInputStream();
        JsonNode array = MAPPER.readTree(response);
        JsonNode node = array.get(0);
        return "-lon=" + node.get("lon").asText();
    }
}
