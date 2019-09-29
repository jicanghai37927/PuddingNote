package com.haiyunshan.pudding;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.haiyunshan.pudding.ftp.FtpHelper;
import com.haiyunshan.pudding.network.WifiUtils;
import com.haiyunshan.pudding.utils.FileHelper;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FtpServerActivity extends AppCompatActivity implements View.OnClickListener {

    static final int NOTIFICATION_ID = 1003;

    View mUnavailableView;
    View mNetworkSettingBtn;

    View mServerView;
    TextView mAddressView;
    TextView mNetworkNameView;
    TextView mHomeDirView;
    TextView mStatusView;

    Toolbar mToolbar;

    File mHomeDir;

    FtpHelper mFtpHelper;

    NetworkConnectChangedReceiver mReceiver;

    /**
     *
     * @param fragment
     * @param homeDir
     */
    public static final void start(Fragment fragment, String homeDir) {
        Activity context = fragment.getActivity();
        Intent intent = new Intent(context, FtpServerActivity.class);
        intent.putExtra("home_dir", homeDir);

        fragment.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftp_server);

        {
            Intent intent = this.getIntent();
            String homeDir = intent.getStringExtra("home_dir");
            if (TextUtils.isEmpty(homeDir)) {
                this.mHomeDir = Environment.getExternalStorageDirectory();
            } else {
                this.mHomeDir = new File(homeDir);
                if (!mHomeDir.exists()) {
                    mHomeDir = Environment.getExternalStorageDirectory();
                }
            }
        }

        {
            this.mUnavailableView = findViewById(R.id.layout_wifi_unavailable);

            this.mNetworkSettingBtn = findViewById(R.id.btn_network_setting);
            mNetworkSettingBtn.setOnClickListener(this);
        }

        {
            this.mServerView = findViewById(R.id.layout_ftp);

            this.mAddressView = findViewById(R.id.tv_address);
            this.mNetworkNameView = findViewById(R.id.tv_network_name);
            this.mHomeDirView = findViewById(R.id.tv_home_dir);

            this.mStatusView = findViewById(R.id.btn_status);
            mStatusView.setOnClickListener(this);
        }

        {
            this.mToolbar = findViewById(R.id.toolbar);
            mToolbar.setNavigationIcon(R.drawable.chips_ic_close_24dp);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        {
            boolean value = WifiUtils.isWifi(this);
            if (value) { // wifi可用，启动ftp

                this.mFtpHelper = new FtpHelper(getHomeDir());
                this.startFtp(mFtpHelper);


            }

            this.updateUI();
        }

        {
            this.registerReceiver();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.hideNotification();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!isFinishing()) {
            this.showNotification();
        }
    }

    @Override
    protected void onDestroy() {

        this.unregisterReceiver();

        this.hideNotification();

        if (mFtpHelper != null) {
            mFtpHelper.close();
            mFtpHelper = null;
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v == mNetworkSettingBtn) {
            WifiUtils.startWifiSetting(this);
        } else if (v == mStatusView) {
            this.onBackPressed();
        }
    }

    void updateUI() {

        boolean value = WifiUtils.isWifi(this);
        if (value && (mFtpHelper != null)) {
            mUnavailableView.setVisibility(View.INVISIBLE);
            mServerView.setVisibility(View.VISIBLE);

            {
                String address = WifiUtils.getLocalIpAddress();
                address = "ftp://" + address + ":" + mFtpHelper.getPort();

                mAddressView.setText(address);
            }

            {
                String name = WifiUtils.getWifiName(this);

                mNetworkNameView.setText(name);
            }

            {
                File file = this.getHomeDir();
                String[] array = FileHelper.getPrettyPath(this, file);
                StringBuilder sb = new StringBuilder();
                sb.append('\"');
                for (String str : array) {
                    sb.append(str);
                }
                sb.append('\"');

                mHomeDirView.setText(sb);
            }

            {
                int resId = mFtpHelper.isRunning()? R.string.ftp_started: R.string.ftp_connecting;
                mStatusView.setText(resId);
            }

        } else {
            mUnavailableView.setVisibility(View.VISIBLE);
            mServerView.setVisibility(View.INVISIBLE);
        }
    }

    void startFtp(FtpHelper helper) {

        FtpTask task = new FtpTask(helper);

        Observable.create(task)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FtpHelper>() {
                    @Override
                    public void accept(FtpHelper helper) {
                        updateUI();
                    }
                });


    }

    private void registerReceiver() {
        if (mReceiver != null) {
            return;
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);

        this.mReceiver = new NetworkConnectChangedReceiver();
        registerReceiver(mReceiver, filter);
    }

    private void unregisterReceiver() {
        if (mReceiver == null) {
            return;
        }

        this.unregisterReceiver(mReceiver);
        mReceiver = null;
    }

    private void showNotification() {

        {
            this.hideNotification();
        }

        if (mFtpHelper != null && mFtpHelper.isRunning()) {

            String channelId = this.getPackageName();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);

            {
                String address = WifiUtils.getLocalIpAddress();
                address = "ftp://" + address + ":" + mFtpHelper.getPort();

                builder.setContentTitle("共享服务运行中...")
                        .setContentText(address)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setWhen(System.currentTimeMillis())
                        .setDefaults(Notification.DEFAULT_ALL);
            }

            {
                Intent notificationIntent = new Intent(this, this.getClass());
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(contentIntent);
            }

            {

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                int id = NOTIFICATION_ID;

                Notification notification = builder.build();
                notification.flags |= Notification.FLAG_ONGOING_EVENT;
                notification.flags |= Notification.FLAG_NO_CLEAR;

                manager.notify(id, notification);
            }
        }
    }

    private void hideNotification() {
        int id = NOTIFICATION_ID;

        NotificationManager manager = (NotificationManager)(getSystemService(NOTIFICATION_SERVICE));
        manager.cancel(id);
    }

    void onNetworkChanged() {
        boolean value = WifiUtils.isWifi(this);
        if (value) {
            if (mFtpHelper == null) {
                mFtpHelper = new FtpHelper(getHomeDir());
                this.startFtp(mFtpHelper);
            }
        } else {
            if (mFtpHelper != null) {
                mFtpHelper.close();
                mFtpHelper = null;
            }
        }

        this.updateUI();
    }

    File getHomeDir() {
        return this.mHomeDir;
    }

    private class FtpTask implements ObservableOnSubscribe<FtpHelper> {

        FtpHelper mHelper;

        FtpTask(FtpHelper helper) {
            this.mHelper = helper;
        }

        @Override
        public void subscribe(ObservableEmitter<FtpHelper> emitter) throws Exception {
            mHelper.start();

            emitter.onNext(mHelper);
            emitter.onComplete();
        }
    }

    private class NetworkConnectChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            {
                onNetworkChanged();
            }

            String action = intent.getAction();

            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);

                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:

                        break;
                    case WifiManager.WIFI_STATE_DISABLING:

                        break;
                    case WifiManager.WIFI_STATE_ENABLED:

                        break;
                    case WifiManager.WIFI_STATE_ENABLING:

                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN: {

                        break;
                    }
                    default: {
                        break;
                    }
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();

                    if (state == NetworkInfo.State.DISCONNECTED) {

                    } else if (state == NetworkInfo.State.CONNECTED) {

                    }
                }
            }
        }
    }
}
