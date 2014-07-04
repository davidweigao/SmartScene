package david.sceneapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

/**
 * Created by david on 7/2/14.
 */
public class WifiSceneTrigger implements SceneTrigger {

    private WifiInfo currentWifiInfo;

    private Scene scene;
    private String wifiSSID;
    private Context activateContext;

    public WifiSceneTrigger(Scene scene, Context context, String wifiSSID) {
        this.scene = scene;
        this.activateContext = context;
        this.wifiSSID = wifiSSID;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if(networkInfo != null) {
                    switch (networkInfo.getState()) {
                        case CONNECTED:
                            WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                            if(wifiInfo != null) {
                                useWifiInfo(wifiInfo, context);
                            }
                            break;
                        default:
                            break;
                    }
                }
            } else if(intent.getAction().equals(WifiManager.NETWORK_IDS_CHANGED_ACTION)) {
            }
        }
    };

    private void useWifiInfo(WifiInfo wifiInfo, Context context) {
        currentWifiInfo = wifiInfo;
        if(isQualified()) {
            scene.implement(context);
        }
        currentWifiInfo = null;
    }


    @Override
    public boolean isQualified() {
        boolean retval = false;
        if(currentWifiInfo.getSSID().equals(wifiSSID)) {
            retval = true;
        }
        return retval;
    }

    @Override
    public void activate() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        activateContext.registerReceiver(receiver, filter);
    }

    @Override
    public void deactivate() {
        activateContext.unregisterReceiver(receiver);
    }
}
