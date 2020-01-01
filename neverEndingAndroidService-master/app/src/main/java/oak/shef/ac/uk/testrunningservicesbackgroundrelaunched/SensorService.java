package oak.shef.ac.uk.testrunningservicesbackgroundrelaunched;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import oak.shef.ac.uk.testrunningservicesbackgroundrelaunched.data.DBManager;

/**
 * Created by fabio on 30/01/2016.
 */
public class SensorService extends Service {

    public static String url = "https://us-central1-zoftino-stores.cloudfunctions.net/";
    public int counter=0;
    Context context;
    DBManager dbManager;
    public SensorService(Context applicationContext) {
        super();
        //this.context  = applicationContext;

    }

    public SensorService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        context = this;
        dbManager = new DBManager(this);
        dbManager.open();
        Log.i("HERE", "here I am!");

        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 10000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                //Log.i("in timer", "in timer ++++  "+ (counter++));
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("UI thread", "I am the UI thread");
                        //Toast.makeText(getApplicationContext()," counter value : "+counter++,Toast.LENGTH_SHORT).show();

                        CouponsAPI couponsAPI = new CouponsAPI(url,context ,dbManager);
                        try {
                            couponsAPI.callService();
                        }catch (Exception e){
                            Log.e("refresh cpn work", "failed to refresh coupons");
                        }
                    }
                });
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}