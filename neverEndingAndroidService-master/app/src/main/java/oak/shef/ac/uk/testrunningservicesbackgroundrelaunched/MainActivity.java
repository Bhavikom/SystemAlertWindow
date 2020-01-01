/**
 * created by Fabio Ciravegna, The University of Sheffield, f.ciravegna@shef.ac.uk
 * LIcence: MIT
 * Copyright (c) 2016 (c) Fabio Ciravegna

 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package oak.shef.ac.uk.testrunningservicesbackgroundrelaunched;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import oak.shef.ac.uk.testrunningservicesbackgroundrelaunched.data.DBManager;
import oak.shef.ac.uk.testrunningservicesbackgroundrelaunched.data.Hero;

public class MainActivity extends AppCompatActivity {
    Intent mServiceIntent;
    private SensorService mSensorService;
    Button btnCheckRecords;
    Context ctx;

    public Context getCtx() {
        return ctx;
    }
    private DBManager dbManager;
    List<Hero> heroList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        dbManager = new DBManager(this);
        dbManager.open();
        heroList = new ArrayList<>();

        ctx = this;

        btnCheckRecords = findViewById(R.id.btn_check_records);
        btnCheckRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchRecords().execute();
            }
        });

        mSensorService = new SensorService(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }


    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }

    private class FetchRecords extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            //heroList =  viewModel.getLatestData();
            heroList = dbManager.fetch();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(heroList.size() > 0) {

                Toast.makeText(MainActivity.this ," NO of records : "+heroList.size(),Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }
}


