package com.iti.recyclerview;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InitializtionMain {
    @SerializedName("main")
    Main main;

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    @SerializedName("weather")
    List<Weather> weather;

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }
}
