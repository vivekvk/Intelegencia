package intelegencia.vivek.intelegencia.network;

import intelegencia.vivek.intelegencia.model.WeatherResponseModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("weather?appid=5ad7218f2e11df834b0eaf3a33a39d2a&")
    Call<WeatherResponseModel> getCurrentWeather(@Query("q") String current_location);
}
