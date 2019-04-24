package net.healthplayer.sampleforsdk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import net.healthplayer.sdk.DeviceHandler;
import net.healthplayer.sdk.HealthPlayerDeviceManager;
import net.healthplayer.sdk.DeviceObserver;
import net.healthplayer.sdk.HealthcareDataEntity;
import net.healthplayer.sdk.HealthPlayerModelManager;
import net.healthplayer.sdk.SensorHandler;
import net.healthplayer.sdk.SensorObserver;
import net.healthplayer.sdk.UserProfileEntity;
import net.healthplayer.sdk.util.LogUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SensorObserver, DeviceObserver {
//    public class MainActivity extends AppCompatActivity implements SensorObserver, DeviceObserver {

    HealthPlayerDeviceManager dm = HealthPlayerDeviceManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HealthPlayerModelManager mm = HealthPlayerModelManager.getInstance();
        mm.init(this);
        if (mm.registerLicense("U2FsdGVkX1+HerQxIOhuKJpZJ9oSlQF/VFiqQghMsROLXKhCuP1sGXUcdoA2tpYUWH84XxqClDQd/wRGX4BHm29VpTLlHoeJUncHFUzDf9g8Ncy1Ur142Ve7kMP3N/scUI9hia4qOrVUG3Az82kvdFeTRaElyyegvbOtBCJyZ/R1ZiJfYKFZXvMNv9NUIul4oM8qwC/WUzDTX0sO+E+bzwg3k34ZNHa/nl1mlSj5nzRoVYQkt237IPbStQ1Vc07YfT5OwsbmoDNqjr2abE7cdLzA1XFcvWMDL8y558AF+rV0giz5wKMBfhCsY0eNC6DsLm4R339vWSHv4fzETC7Fpg==") == false) {
            LogUtil.d("BasecSetting", "[onClick] Failed to register License.");
        }
        dm.init(this);

        try {
            if (mm.login("abcdefghijk", "abcdefghijk") == false) {
                mm.createUserAnonymous("abcdefghijk", "abcdefghijk");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        UserProfileEntity profile;

        try {
            List<UserProfileEntity> profilelist = new ArrayList<UserProfileEntity>();
            mm.getProfile(profilelist);

            {
                Calendar birth = Calendar.getInstance();
                birth.set(1990, 4, 1);
                String gender = "1";
                double height = 160.0;
                double weight = 60.0;
                String area = "tokyo";
                profile = new UserProfileEntity(birth, gender, height, weight, area);

                mm.setProfile(profile);
            }
        } catch (Exception e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
        dm.attachDeviceObserver(this);
    }

    @Override
    protected void onDestroy() {
        // TODO 自動生成されたメソッド・スタブ
        dm.revokePassometer();
        super.onDestroy();
    }

    @Override
    public void notify(DeviceHandler deviceHandler, NotifyEvent notifyEvent) {

    }

    @Override
    public void notify(DeviceHandler deviceHandler, HealthcareDataEntity healthcareDataEntity) {

    }

    @Override
    public void notify(DeviceHandler deviceHandler, HealthcareDataEntity[] healthcareDataEntities) {

    }

    @Override
    public void notifyStepsData(SensorHandler sensorHandler, HealthcareDataEntity healthcareDataEntity) {

    }

    @Override
    public void notifyActivityData(SensorHandler sensorHandler, HealthcareDataEntity healthcareDataEntity) {

    }

    @Override
    public void notifyCalorieData(SensorHandler sensorHandler, HealthcareDataEntity healthcareDataEntity) {

    }

    @Override
    public void notifyLocationData(SensorHandler sensorHandler, HealthcareDataEntity healthcareDataEntity) {
//        Log.d("MainActivity", "notify location data:" + data.getValueString());
    }
//    @Override
//    public void onClick() {
//
//        int id = arg0.getId();
//        if (id == R.id.button01) {
//            Intent intent = new Intent(this, ManporoidActivity.class);
//
//            startActivity(intent);
//        }
//        if (id == R.id.button03) {
//            dm.attatchSensorObserver(this);
//            dm.invokeLBS(0);
//        }
//        if (id == R.id.button04) {
//            dm.revokeLBS();
//            dm.detatchSensorObserver(this);
//        }
//        if (id == R.id.button05) {
//            dm.invokeBluetooth("UA-851PBT-C");
//            dm.invokeBluetooth("UC-411PBT-C");
//            dm.invokeBluetooth("TANITA BC-505");
//        }
//        if (id == R.id.button07) {
//            dm.revokeBluetooth("UA-851PBT-C");
//            dm.revokeBluetooth("UC-411PBT-C");
//            dm.revokeBluetooth("TANITA BC-505");
//        }
//        if (id == R.id.button06) {
//            Intent intent = new Intent(this, SonyFelicaActivity.class);
//            startActivity(intent);
//        }
//    }

}
