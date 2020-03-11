package intelegencia.vivek.intelegencia;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import mumayank.com.airlocationlibrary.AirLocation;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.tv_CurrentTemp)
    TextView tvCurrentTemp;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.tv_feelsLike)
    TextView tvFellLike;
    @BindView(R.id.tv_minimum)
    TextView tvMinimum;
    @BindView(R.id.tv_maximum)
    TextView tvMaximum;
    @BindView(R.id.tv_humidity)
    TextView tvHumidity;

    private AirLocation airLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Fetch location simply like this whenever you need
        airLocation = new AirLocation(this, true, true, new AirLocation.Callbacks() {
            @Override
            public void onSuccess(@NotNull Location location) {
                // do something
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    String cityName = addresses.get(0).getAddressLine(0);
                    prepareWeatherRequest(cityName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(@NotNull AirLocation.LocationFailedEnum locationFailedEnum) {
                // do something
            }
        });

    }

    private void prepareWeatherRequest(String location) {
        Data data = new Data.Builder()
                .putString(WorkRequest.TASK_DESC, location)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED) // works only with Wifi
                .build();

        /*PeriodicWorkRequest periodicWorkRequest
                = new PeriodicWorkRequest.Builder(WorkRequest.class, 2, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setInputData(data)
                .build();
*/
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(WorkRequest.class)
                .setConstraints(constraints)
                .setInputData(data)
                .build();

        WorkManager.getInstance().enqueue(request);
        progress_bar.setVisibility(View.VISIBLE);

        WorkManager.getInstance().getWorkInfoByIdLiveData(request.getId()).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(@Nullable WorkInfo workInfo) {
                if (workInfo != null && workInfo.getState().isFinished()) {
                    progress_bar.setVisibility(View.GONE);
                    tvLocation.setText("Bangalore");
                    tvCurrentTemp.setText(workInfo.getOutputData().getKeyValueMap().get(WorkRequest.TEMP) + "F");
                    tvDesc.setText(workInfo.getOutputData().getKeyValueMap().get(WorkRequest.TASK_DESC).toString());
                    tvFellLike.setText("Feel like " + workInfo.getOutputData().getKeyValueMap().get(WorkRequest.FEEL_LIKE_TEMP).toString() + "F");
                    tvMaximum.setText("Maximum Temp. " + workInfo.getOutputData().getKeyValueMap().get(WorkRequest.MAXIMUM_TEMP).toString() + "F");
                    tvMinimum.setText("Minimum Temp. " + workInfo.getOutputData().getKeyValueMap().get(WorkRequest.MINIMUM_TEMP).toString() + "F");
                    tvHumidity.setText("Humidity " + workInfo.getOutputData().getKeyValueMap().get(WorkRequest.HUMIDITY).toString());
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        airLocation.onActivityResult(requestCode, resultCode, data);
    }

    // override and call airLocation object's method by the same name
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
