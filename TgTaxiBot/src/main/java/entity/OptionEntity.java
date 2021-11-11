package entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OptionEntity {

    @JsonProperty("class_text")
    private String type;

    @JsonProperty("price")
    private String price;


    public String getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "OptionEntity{" +
                "type='" + type + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
