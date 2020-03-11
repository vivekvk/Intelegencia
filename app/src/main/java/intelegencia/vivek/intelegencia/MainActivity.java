package intelegencia.vivek.intelegencia;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_CurrentTemp)
    TextView tvCurrentTemp;
    @BindView(R.id.tv_location)
    TextView tvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Data data = new Data.Builder()
                .putString(WorkRequest.TASK_DESC, "Bengaluru")
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED) // works only with Wifi
                .build();

        PeriodicWorkRequest periodicWorkRequest
                = new PeriodicWorkRequest.Builder(WorkRequest.class, 2, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setInputData(data)
                .build();

        WorkManager.getInstance().enqueue(periodicWorkRequest);


        WorkManager.getInstance().getWorkInfoByIdLiveData(periodicWorkRequest.getId()).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(@Nullable WorkInfo workInfo) {
                if ((workInfo != null)&(workInfo.getState().isFinished())) {
                    tvLocation.setText("Bangalore");
                    tvCurrentTemp.setText(workInfo.getOutputData().getString(WorkRequest.TEMP));

                }
            }
        });
    }
}
