package net.healthplayer.sampleforsdk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import net.healthplayer.sdk.DeviceHandler;
import net.healthplayer.sdk.DeviceObserver;
import net.healthplayer.sdk.HealthPlayerDeviceManager;
import net.healthplayer.sdk.HealthPlayerModelManager;
import net.healthplayer.sdk.HealthcareDataEntity;
import net.healthplayer.sdk.UserProfileEntity;
import net.healthplayer.sdk.util.LogUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Sample application for using device
 */
public class MainActivity extends AppCompatActivity implements DeviceObserver {

    private HealthPlayerDeviceManager dm = HealthPlayerDeviceManager.getInstance();
    private HealthPlayerModelManager mm = HealthPlayerModelManager.getInstance();
    private TextView mainTextView;
    private ArrayList<HealthcareDataEntity> healthcareDataEntities;
    // このActivityに接続しているデバイスの名前一覧
    private ArrayList<String> attachedDeviceNames;

    /**
     * onCreate function
     * <p>
     * Please initialize `HealthPlayerModelManager` and `HealthPlayerDeviceManager`
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // prepare variables
        UserProfileEntity profile;

        // normal initialize app
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        healthcareDataEntities = new ArrayList<>();
        attachedDeviceNames = new ArrayList<>();
        findViewById(R.id.button).setOnClickListener(
                this::getDataFromServer
        );
        mainTextView = findViewById(R.id.mainTextView);

        // initialize SDK
        mainTextView.setText(getString(R.string.initializing));
        // ModelManager init
        mm.init(this);
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

        // UserのProfileをサーバーに登録する
        try {
            // get profiles
            List<UserProfileEntity> profiles = new ArrayList<>();
            mm.getProfile(profiles);

            // Set User profile of this user
            profile = createTestProfile();
            mm.setProfile(profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // デバイスからのEventの通知先(Activity等)を設定します
        dm.attachDeviceObserver(this);
        mainTextView.setText(getString(R.string.completeinit));
    }

    /**
     * プロファイルを作っています
     *
     * @return UserProfileEntity
     */
    private UserProfileEntity createTestProfile() {
        // Create user entity
        Calendar birth = Calendar.getInstance();
        birth.set(1998, 1, 17);
        String gender = "1";
        double height = 160.0;
        double weight = 50.2;
        String area = "tokyo";
        return new UserProfileEntity(birth, gender, height, weight, area);
    }

    /**
     * 今回はボタンをクリック時に, サーバーのデータを更新してからデータを表示します
     *
     * @param v view
     */
    private void getDataFromServer(View v) {
        this.connectHeartRateDevice();
        // データを更新します
        mm.synchronize();

        // ある期間の血圧データをサーバーから取得してみます.
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.set(2019, 1, 1);
        end.set(2019, 5, 1);
        mm.acquireHealthcareData("heartRate", start, end, healthcareDataEntities);
        mainTextView.setText(getString(R.string.data_from_device, "heartRate"
                , healthcareDataEntities));
    }

    /**
     * 心拍計を接続します
     */
    private void connectHeartRateDevice() {
        // Bluetooth で接続します
        String deviceName = "UA-851PBT-C";
        attachedDeviceNames.add(deviceName);
        dm.invokeBluetooth(deviceName);
    }

    @Override
    protected void onDestroy() {
        // 接続している可能性のある全てのデバイスとのBluetooth接続を切ります
        attachedDeviceNames.forEach(deviceName -> dm.revokeBluetooth(deviceName));
        super.onDestroy();
    }

    // Override DeviceManager Interface
    @Override
    public void notify(DeviceHandler deviceHandler, NotifyEvent notifyEvent) {
        // do nothing
    }

    /**
     * デバイスからの通知を受け取った時の処理
     * <p>
     * 今回はサーバーにデータを保存する処理のみ行っています
     *
     * @param deviceHandler        DeviceHandler
     * @param healthcareDataEntity healthcare デバイスの一つの測定結果
     */
    @Override
    public void notify(DeviceHandler deviceHandler, HealthcareDataEntity healthcareDataEntity) {
        mm.storeHealthcareData(healthcareDataEntity);
    }


    /**
     * デバイスからの通知を受け取った時の処理
     * <p>
     * 今回はサーバーにデータを保存する処理のみ行っています
     *
     * @param deviceHandler          DeviceHandler
     * @param healthcareDataEntities healthcare デバイスの測定結果
     */
    @Override
    public void notify(final DeviceHandler deviceHandler, HealthcareDataEntity[] healthcareDataEntities) {
        for (HealthcareDataEntity healthcareDataEntity : healthcareDataEntities) {
            this.notify(deviceHandler, healthcareDataEntity);
        }
    }
}
