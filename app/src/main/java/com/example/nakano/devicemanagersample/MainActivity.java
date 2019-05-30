package com.example.nakano.devicemanagersample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import net.healthplayer.sdk.*;
import net.healthplayer.sdk.util.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Sample application for using device
 */
public class MainActivity extends AppCompatActivity implements DeviceObserver {

    private HealthPlayerDeviceManager dm = HealthPlayerDeviceManager.getInstance();
    private HealthPlayerModelManager  mm = HealthPlayerModelManager.getInstance();

    final String LICENSE = "U2FsdGVkX1+2qQou9nYB2SlxOQeYhg/A9mQYhHzNt8FEwFM6Op9WqoIFs1BzBDCjerfjYOLN/tqyHvM6m8QpJGx+rTkFjjixttERcEQsDpY605C3ympbPLQ+2ZJIWqFTm808Fp2dRCbu77voxCdpYMVqJPgXIrIhSBtVGTnHU1AS6odqi3ihT80THKxP9eL22LGSfBizeqBa7gdJn020pMOMSEU86Wm4tq0cFO9PKqYhhwZVzYKb0bnGvIULVr4Saxe5Vg6f9KcNYLzv7LwH1+EHu2o8HZezbkTCSzl9BH9EBO/90chaZfAr/YEgzly2gPFHGsAokNK07ngs3ZlIKBhxGOS0q7rObchg8VSJ9Xc=";
    final String TESTUSERID = "abcdefghijk";
    final String TESTUSERPASS = "abcdefghijk";

    /**
     * onCreate function
     * <p>
     * Please initialize `HealthPlayerModelManager` and `HealthPlayerDeviceManager`
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        {
            mm.init(this);
            if (!mm.registerLicense(LICENSE)) throw new AssertionError();

            dm.init(this);
            try {
                if (!mm.login(TESTUSERID, TESTUSERPASS)) {
                    mm.createUserAnonymous(TESTUSERID, TESTUSERPASS);
                }
            } catch (Exception e) {
                LogUtil.d("BaseSetting", "[onClick] Failed to Login.");
                e.printStackTrace();
            }

            dm.attachDeviceObserver(MainActivity.this);
        }

        findViewById(R.id.button1).setOnClickListener((view) -> {
            dm.stopParingBluetooth("UC-411PBT-C");
            if (dm.paringBluetooth("UC-411PBT-C", MainActivity.this)) {
            } else {
                dm.detachDeviceObserver(MainActivity.this);
            }
        });

        findViewById(R.id.button2).setOnClickListener((view) -> {
            boolean result = dm.invokeBluetooth("UC-411PBT-C", MainActivity.this);
            Log.i("notify", "invokeBluetooth: " + result);
        });
    }

    @Override
    public void notify(DeviceHandler var1, DeviceObserver.NotifyEvent var2) {
        Log.i("notify", var2.toString());
        switch (var2) {
            case deviceDisConnected:
            {
                dm.revokeBluetooth("UC-411PBT-C");

                Calendar begin = Calendar.getInstance();
                begin.set(2010, 0, 1);

                Calendar end = Calendar.getInstance();
                end.set(2020, 11, 31);

                List<HealthcareDataEntity> data = new ArrayList<>();
                mm.acquireHealthcareData("weight", begin, end, data);
                Log.i("notify", "----------");
                data.forEach((it) -> {
                    Log.i("notify", new SimpleDateFormat("HH:mm").format(it.getDate().getTime()) + " " + it.getValue() + "kg");
                });
                Log.i("notify", "----------");
                break;
            }
        }
    }

    @Override
    public void notify(DeviceHandler var1, HealthcareDataEntity var2) {
        Log.i("notify", var2.getKind());
    }

    @Override
    public void notify(DeviceHandler var1, HealthcareDataEntity[] var2) {
        Log.i("notify", ""+var2.length);
    }

    @Override
    protected void onDestroy() {
        // 接続している可能性のある全てのデバイスとのBluetooth接続を切ります
        super.onDestroy();
    }
}
