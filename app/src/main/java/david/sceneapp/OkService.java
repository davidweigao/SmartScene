package david.sceneapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.widget.Toast;

/**
 * Created by david on 7/2/14.
 */
public class OkService extends NotificationListenerService {

    public static OkService currentInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        currentInstance = this;
        WifiSceneTrigger trigger = new WifiSceneTrigger(getDemoSilentMode(),this,"\"nmagic2\"");
        trigger.activate();
    }

    private Scene getDemoSilentMode() {
        Scene ss = new Scene();
        ss.setSystemVolume(0);
        return ss;
    }

    private Scene getDemoSoundMode() {
        Scene ss = new Scene();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        ss.setSystemVolume(audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
        return ss;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        String name = statusBarNotification.getPackageName();
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification statusBarNotification) {
        Toast.makeText(this,"removed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}
