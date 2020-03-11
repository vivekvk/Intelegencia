package intelegencia.vivek.intelegencia;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import intelegencia.vivek.intelegencia.model.WeatherResponseModel;
import intelegencia.vivek.intelegencia.network.ApiClient;
import intelegencia.vivek.intelegencia.network.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkRequest extends Worker {

    //a public static string that will be used as the key
    //for sending and receiving data
    public static final String TASK_DESC = "location";
    public static final String TEMP = "temp";
    public static final String FEEL_LIKE_TEMP = "feels_like";
    public static final String MINIMUM_TEMP = "temp_minimum";
    public static final String MAXIMUM_TEMP = "temp_maximum";
    public static final String HUMIDITY = "humidity";
    public static final String PRESSURE = "pressure";
    Map<String, Object> data = new HashMap<>();

    public WorkRequest(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @NonNull
    @Override
    public Result doWork() {

        //getting the input data
        String location = getInputData().getString(TASK_DESC);
        getWeatherForecast(location);

        Data output = new Data.Builder()
                .putString(TEMP,data.get(TEMP).toString())
                .build();
        return Result.success(output);
    }

    private void getWeatherForecast(String location) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<WeatherResponseModel> call = apiService.getCurrentWeather(location);
        call.enqueue(new Callback<WeatherResponseModel>() {
            @Override
            public void onResponse(Call<WeatherResponseModel> call, Response<WeatherResponseModel> response) {

                if (response.code() == 200) {
                    data.put(TEMP, response.body().getMain().getTemp().toString());
                    data.put(FEEL_LIKE_TEMP, response.body().getMain().getFeels_like().toString());
                    data.put(MINIMUM_TEMP, response.body().getMain().getTemp_min().toString());
                    data.put(MAXIMUM_TEMP, response.body().getMain().getTemp_max().toString());
                    data.put(HUMIDITY, response.body().getMain().getHumidity().toString());
                    data.put(PRESSURE, response.body().getMain().getPressure().toString());

                } else {
                    Log.e("Error", "API error");
                }

            }

            @Override
            public void onFailure(Call<WeatherResponseModel> call, Throwable t) {
                Log.e("Error", "API failure");

            }
        });

    }
}