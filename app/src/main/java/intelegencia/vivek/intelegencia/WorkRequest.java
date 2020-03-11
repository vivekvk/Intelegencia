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

        try {
            String location = getInputData().getString(TASK_DESC);

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<WeatherResponseModel> call = apiService.getCurrentWeather(location);

            WeatherResponseModel  model = call.execute().body();

                data.put(TEMP, model.getMain().getTemp().toString());
                data.put(FEEL_LIKE_TEMP, model.getMain().getFeels_like().toString());
                data.put(MINIMUM_TEMP, model.getMain().getTemp_min().toString());
                data.put(MAXIMUM_TEMP, model.getMain().getTemp_max().toString());
                data.put(HUMIDITY, model.getMain().getHumidity().toString());
                data.put(PRESSURE, model.getMain().getPressure().toString());
                data.put(TASK_DESC,model.getWeather().get(0).getDescription());

            Data output = new Data.Builder()
                    .putAll(data)
                    .build();

            return Result.success(output);

        }catch (Exception exception) {
            Log.e("Error cleaning up", "error");
            return Result.failure();
        }
    }

}