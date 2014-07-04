package david.sceneapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.service.notification.NotificationListenerService;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by david on 7/2/14.
 */
public class NotificationSceneTrigger implements SceneTrigger {
    private Context activateContext;
    private Set<String> pkgNames = new HashSet<String>();
    private Scene scene;

    public NotificationSceneTrigger(Context activateContext, Scene scene, Set<String> pkgNames) {
        this.activateContext = activateContext;
        this.scene = scene;
        this.pkgNames.addAll(pkgNames);

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String pkgName = intent.getStringExtra(LALALAService.KEY_NOTIF_PKG_NAME);
            if(pkgName != null) {
                if(pkgNames.contains(pkgName)) {
                    Toast.makeText(context, pkgName, Toast.LENGTH_SHORT).show();
                    if(isQualified()) {
                        scene.implement(context);
                    }
                }
            }
        }
    };

    @Override
    public boolean isQualified() {
        return true;
    }

    @Override
    public void activate() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(LALALAService.ACTION_GET_NOTIFICATION);
        activateContext.registerReceiver(receiver, filter);
    }

    @Override
    public void deactivate() {
        activateContext.unregisterReceiver(receiver);
    }
}
