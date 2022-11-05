package cs4720.cs.virginia.edu.intentexamples;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class MyService extends Service {

    private Handler mHandler = new Handler();
    private static long UPDATE_INTERVAL = 5000; // default

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("Intent Example", "Service Binded");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        Log.i("Intent Example", "Service onStart");
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, UPDATE_INTERVAL);
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onCreate() {
        Log.i("Intent Example", "Service onCreate");
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
        Log.i("Intent Example", "Service onDestroy");
        _shutdownService();

    }

    private void doServiceWork() {
        String currentDateTimeString = DateFormat.format("MM/dd/yy h:mm:ssaa", new Date()).toString();
        Toast.makeText(this, "Service: " + currentDateTimeString, Toast.LENGTH_SHORT).show();

    }

    private void _shutdownService() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        Log.i("Intent Example", "Timer stopped...");
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            doServiceWork();
            mHandler.postDelayed(this, UPDATE_INTERVAL);
        }
    };
}
