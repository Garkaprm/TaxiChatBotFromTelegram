import com.fasterxml.jackson.databind.ObjectMapper;
import entity.TaxiInfo;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;


public class Taxi {
    private static ObjectMapper mapper = new ObjectMapper();
    private String beginAdress;
    private String endAdress;

    public void setBeginAdress(String beginAdress) {
        this.beginAdress = beginAdress;
    }

    public void setEndAdress(String endAdress) {
        this.endAdress = endAdress;
    }

    public TaxiInfo taxiInfo() throws IOException {
        String CLID = "ak210916";
        String taxiClass = "econom";
        URI uri = UriBuilder.fromUri("https://taxi-routeinfo.taxi.yandex.net/taxi_info")
                .queryParam("clid", CLID)
                .queryParam("rll", rll())
                .queryParam("class", taxiClass)
                .build();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(uri);
            String APIKEY = "dKrneXRYjCCzbRDrKwkbwMvyuZmVjnunjIJq";
            httpGet.addHeader("YaTaxi-Api-Key", APIKEY);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            return mapper.readValue(response.getEntity().getContent(), TaxiInfo.class);
        }
    }
    public String rll() throws IOException {
        GetCoordinates coordinates = new GetCoordinates();
        return coordinates.longlat(beginAdress) + "~" + coordinates.longlat(endAdress);
    }
    public String reference() throws IOException {
        GetCoordinates coordinates = new GetCoordinates();

        return "start" + coordinates.latForRef(beginAdress) + "&start" + coordinates.longForRef(beginAdress)
                + "&end" + coordinates.latForRef(endAdress) + "&end" + coordinates.longForRef(endAdress);
    }
}
