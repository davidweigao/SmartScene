package david.sceneapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;



public class WifiActivity extends ActionBarActivity {

    private TextView mInfoTV;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        mInfoTV = (TextView) findViewById(R.id.infoTV);

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.wifi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_start_service:
                showServiceState();
                break;
            case R.id.action_connection_info:
                showWifiInfo();
            case R.id.action_volume_info:
                showAudioMode();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showServiceState() {
        String info = null;
        if(LALALAService.currentInstance == null) {
            Intent intent = new Intent(this,LALALAService.class);
            if(startService(intent) != null) {
                info = "smart scene service started";
            } else {
                info = "failed to start service";
            }

        } else {
            info = "smart scene service is alreay running";
        }
        Toast.makeText(this,info,Toast.LENGTH_SHORT).show();
    }

    private void showAudioMode() {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int alarmVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        int dtmfVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_DTMF);
        int musicVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int notifVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        int systemVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        int ringVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        int voiceCallVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
//        int defaultVolume = mAudioManager.getStreamVolume(AudioManager.USE_DEFAULT_STREAM_TYPE);

        String volumeInfo = String.format("\nalarmVolume: %d" +
                "\n" +
                "dtmfVolume: %d" +
                "\n" +
                "musicVolume: %d" +
                "\n" +
                "notifVolume: %d" +
                "\n" +
                "voiceCallVolume: %d" +
                "\nsystemVolume: %d" +
                "\nringVolume: %d", alarmVolume, dtmfVolume, musicVolume, notifVolume, voiceCallVolume, systemVolume, ringVolume);
        mInfoTV.setText(volumeInfo);

    }

    private void showWifiInfo() {
        WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        try {
            String bssid = wifiInfo.getBSSID();
            boolean hiddenBSSID = wifiInfo.getHiddenSSID();
            int ipAddress = wifiInfo.getIpAddress();
            byte[] bytes = BigInteger.valueOf(ipAddress).toByteArray();

            InetAddress inetAddress = InetAddress.getByAddress(bytes);

            int linkSpeed = wifiInfo.getLinkSpeed(); //Mbps
            String macAddress = wifiInfo.getMacAddress();
            int networkId = wifiInfo.getNetworkId();
            int rssi = wifiInfo.getRssi();
            String ssid = wifiInfo.getSSID();

            SupplicantState suppState = wifiInfo.getSupplicantState();
            NetworkInfo.DetailedState detailedState = wifiInfo.getDetailedStateOf(suppState);

            String info = String.format("\nBSSID: %s" +
                            "\nhiddenBSSID: %b" +
                            "\nIP Address: %s" +
                            "\nlinkSpeed: %d" +
                            "\nMAC Address: %s" +
                            "\nnetwork ID: %d" +
                            "\nRSSI: %d" +
                            "\nSSID: %s" +
                            "\nSupplicant State: %s" +
                            "\nDetailed State: %s",
                    bssid, hiddenBSSID, inetAddress.getHostAddress(), linkSpeed, macAddress,
                    networkId, rssi, ssid, suppState, detailedState
            );

            mInfoTV.setText(info);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }





}
