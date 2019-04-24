package net.healthplayer.sampleforsdk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import net.healthplayer.sdk.*;
import net.healthplayer.sdk.util.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity implements DeviceObserver {
//    public class MainActivity extends AppCompatActivity implements SensorObserver, DeviceObserver {

    private HealthPlayerDeviceManager dm = HealthPlayerDeviceManager.getInstance();
    private HealthPlayerModelManager mm = HealthPlayerModelManager.getInstance();
    private DeviceObserver deviceObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // prepare variables
        UserProfileEntity profile;

        // normal initialize app
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonClick(v);
                    }
                }
        );

        // ModelManager init
        mm.init(this);
        // Licence check
        // Register License Code to use SDK
        if (!mm.registerLicense("U2FsdGVkX1+HerQxIOhuKJpZJ9oSlQF/VFiqQghMsROLXKhCuP1sGXUcdoA2tpYUWH84XxqClDQd/wRGX4BHm29VpTLlHoeJUncHFUzDf9g8Ncy1Ur142Ve7kMP3N/scUI9hia4qOrVUG3Az82kvdFeTRaElyyegvbOtBCJyZ/R1ZiJfYKFZXvMNv9NUIul4oM8qwC/WUzDTX0sO+E+bzwg3k34ZNHa/nl1mlSj5nzRoVYQkt237IPbStQ1Vc07YfT5OwsbmoDNqjr2abE7cdLzA1XFcvWMDL8y558AF+rV0giz5wKMBfhCsY0eNC6DsLm4R339vWSHv4fzETC7Fpg==")) {
            LogUtil.d("BaseSetting", "[onClick] Failed to register License.");
        }
        // DeviceManager init after ModelManager init
        dm.init(this);

        // Try Login to use HealthPlayer with server
        try {
            if (!mm.login("abcdefghijk", "abcdefghijk")) {
                mm.createUserAnonymous("abcdefghijk", "abcdefghijk");
            }
        } catch (Exception e) {
            LogUtil.d("BaseSetting", "[onClick] Failed to Login.");
            e.printStackTrace();
        }

        try {
            // get profiles
            List<UserProfileEntity> profiles = new ArrayList<UserProfileEntity>();
            mm.getProfile(profiles);
            // Create user entity
            Calendar birth = Calendar.getInstance();
            birth.set(1998, 1, 17);
            String gender = "1";
            double height = 160.0;
            double weight = 50.2;
            String area = "tokyo";
            profile = new UserProfileEntity(birth, gender, height, weight, area);

            // Set User profile of this user
            mm.setProfile(profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dm.attachDeviceObserver(this);
    }

    // method for click event
    private void buttonClick(View v) {
        String devicename = "UA-851PBT-C";
        dm.invokeBluetooth(devicename);
//        mm.acquireHealthcareData();
        dm.revokeBluetooth(devicename);
    }

    @Override
    protected void onDestroy() {
        // deprecated
        // どうしたら良いですか?
        dm.revokePassometer();
        super.onDestroy();
    }

    // Override DeviceManager Interface
    @Override
    public void notify(DeviceHandler deviceHandler, NotifyEvent notifyEvent) {
        // do nothing
    }

    @Override
    public void notify(DeviceHandler deviceHandler, HealthcareDataEntity healthcareDataEntity) {
        TextView textView = findViewById(R.id.button);
        textView.setText(getString(R.string.data_from_device,
                deviceHandler.getDeviceName(), healthcareDataEntity.getValues()));
    }

    @Override
    public void notify(final DeviceHandler deviceHandler, HealthcareDataEntity[] healthcareDataEntities) {
        for (HealthcareDataEntity healthcareDataEntity : Arrays.asList(healthcareDataEntities)) {
            this.notify(deviceHandler, healthcareDataEntity);
        }
        this.notify();
    }
}
