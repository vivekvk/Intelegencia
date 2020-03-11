package intelegencia.vivek.intelegencia;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderApi fusedLocationClient;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fusedLocationClient = LocationServices.FusedLocationApi;

        Data data = new Data.Builder()
                .putString(WorkRequest.TASK_DESC, "Bengaluru")
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


}
