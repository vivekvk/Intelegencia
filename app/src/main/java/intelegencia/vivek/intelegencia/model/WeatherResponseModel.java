package intelegencia.vivek.intelegencia.model;

import java.util.List;

public class WeatherResponseModel {

    private List<Weather> weather;

    private Main main;

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }
}
