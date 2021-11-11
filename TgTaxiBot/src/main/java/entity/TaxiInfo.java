package entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaxiInfo {

    @JsonProperty("options")
    private List<OptionEntity> options;

    @JsonProperty("time_text")
    private String timeText;

    public List<OptionEntity> getOptions() {
        return options;
    }

    public String getTimeText() {
        return timeText;
    }

    @Override
    public String toString() {
        return "PriceEntity{" +
                "options=" + options +
                ", timeText='" + timeText + '\'' +
                '}';
    }
}
