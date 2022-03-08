import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static com.tinf.qmobile.fragment.SettingsFragment.ALERT;

import android.content.Context;
import android.util.Log;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import androidx.work.Configuration;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.testing.SynchronousExecutor;
import androidx.work.testing.WorkManagerTestInitHelper;

import com.tinf.qmobile.service.ParserWorker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4ClassRunner.class)
public class WorkerTests {

    @Before
    public void setup() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Configuration config = new Configuration.Builder()
                .setMinimumLoggingLevel(Log.DEBUG)
                .setExecutor(new SynchronousExecutor())
                .build();

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config);
    }

    @Test
    public void run() throws ExecutionException, InterruptedException {
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(ParserWorker.class).build();
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.enqueue(request).getResult().get();
    }

}
