package com.iti.recyclerview;

import java.util.ArrayList;
import java.util.List;

public class WeatherList {
    private List<Weather> Featured = new ArrayList<Weather>();

    public List<Weather> getFeatured() {
        return Featured;
    }

    public void setFeatured(List<Weather> featured) {
        Featured = featured;
    }


}
